package nu.peg.web.files.file

import nu.peg.web.files.config.FilesProperties
import nu.peg.web.files.exception.SubPathIsNotInBasePathException
import nu.peg.web.files.exception.TargetIsNotDirectoryException
import nu.peg.web.files.security.SecurityUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.nio.file.FileVisitOption
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Paths
import java.util.stream.Collectors

@Service
class FileService
@Autowired
constructor(
    private val config: FilesProperties
) {
    fun listFiles(subPath: String): List<FileDto> {
        val (basePath, targetPath) = checkSubpath(config.listingBaseDir, subPath)

        if (!Files.isDirectory(targetPath))
            throw TargetIsNotDirectoryException()

        val loggedIn = SecurityUtil.canSeeHidden()
        val fileList = Files.walk(targetPath, 1, FileVisitOption.FOLLOW_LINKS)
            .filter { it != targetPath }
            .map { FileDto(it, basePath.relativize(it).toString(), Files.isDirectory(it)) }
            .filter { loggedIn || !it.fileName.startsWith(".") }
            .sorted()
            .collect(Collectors.toList())

        if (targetPath != basePath) {
            val relativePath = basePath.relativize(targetPath.resolve("..")).normalize()
            fileList.add(0, FileDto(targetPath, relativePath.toString(), true, ".."))
        }

        return fileList
    }

    fun getDownloadLink(subPath: String): URI {
        val (basePath, targetPath) = checkSubpath(config.listingBaseDir, subPath)
        val relativeTargetPath = basePath.relativize(targetPath).normalize()

        val splitPath = relativeTargetPath.toString()
            .split('/', '\\').toTypedArray()

        return UriComponentsBuilder.fromHttpUrl(config.downloadBaseDir)
            .pathSegment(*splitPath)
            .build().toUri().normalize()
    }

    fun generateBreadcrumbs(subPath: String): List<BreadcrumbDto> {
        val (basePath, targetPath) = checkSubpath(config.listingBaseDir, subPath)

        val homeDto = BreadcrumbDto("", "Home", "fa-home")
        if (basePath == targetPath) {
            homeDto.active = true
            return listOf(homeDto)
        }

        val breadcrumbs: MutableList<BreadcrumbDto> = mutableListOf()
        val relativeTargetPath = basePath.relativize(targetPath)
        var parent = relativeTargetPath
        do {
            breadcrumbs.add(
                BreadcrumbDto(
                    parent.toString(),
                    parent.fileName.toString()
                )
            )
        } while ({ parent = parent.parent; parent }() != null)
        breadcrumbs.add(homeDto)
        breadcrumbs[0].active = true

        return breadcrumbs.reversed()
    }

    fun createFolder(subPath: String, folderName: String): String {
        val (basePath, targetPath) = checkSubpath(config.listingBaseDir, subPath)
        val folderTarget = targetPath.resolve(folderName).normalize()

        if (!folderTarget.startsWith(basePath)) {
            throw SubPathIsNotInBasePathException()
        }

        if (!Files.exists(folderTarget)) {
            Files.createDirectory(folderTarget)
        }

        return basePath.relativize(folderTarget).normalize().toString()
    }

    fun uploadFile(subPath: String, file: MultipartFile): String {
        val (basePath, targetPath) = checkSubpath(config.listingBaseDir, subPath)
        val filePath = targetPath.resolve(file.originalFilename).normalize()

        if (!filePath.startsWith(basePath)) {
            throw SubPathIsNotInBasePathException()
        }

        file.transferTo(filePath.toFile())

        return basePath.relativize(filePath.parent).normalize().toString()
    }

    fun deleteFile(subPath: String): String {
        val (basePath, targetPath) = checkSubpath(config.listingBaseDir, subPath)
        Files.walkFileTree(targetPath, DeletingFileVisitor())

        return basePath.relativize(targetPath.parent).normalize().toString()
    }

    private fun checkSubpath(baseDir: String, subPath: String): PathCheckResult {
        val basePath = Paths.get(baseDir)
        val targetPath = Paths.get(baseDir, subPath).normalize()

        if (!targetPath.startsWith(basePath))
            throw SubPathIsNotInBasePathException()

        if (!Files.exists(targetPath))
            throw NoSuchFileException("The target file does not exist")

        return PathCheckResult(basePath, targetPath)
    }
}
