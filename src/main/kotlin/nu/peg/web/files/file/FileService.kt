package nu.peg.web.files.file

import nu.peg.web.files.config.FilesProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.net.URI
import java.net.URLEncoder
import java.nio.file.*
import java.util.stream.Collectors

@Service
class FileService
@Autowired
constructor(
        private val config: FilesProperties
) {
    fun listFiles(subPath: String): List<FileDto> {
        val (basePath, targetPath) = checkSubpath(config.listing.baseDirectory, subPath)

        if (!Files.isDirectory(targetPath))
            throw TargetIsFileException()

        val fileList = Files.walk(targetPath, 1, FileVisitOption.FOLLOW_LINKS)
                .filter { it != targetPath }
                .map { FileDto(it, basePath.relativize(it).toString(), Files.isDirectory(it)) }
                .sorted()
                .collect(Collectors.toList())

        if (targetPath != basePath) {
            val relativePath = basePath.relativize(targetPath.resolve("..")).normalize()
            fileList.add(0, FileDto(targetPath, relativePath.toString(), true, ".."))
        }

        return fileList
    }

    fun getDownloadLink(subPath: String): URI? {
        val (basePath, targetPath) = checkSubpath(config.listing.baseDirectory, subPath)
        val relativeTargetPath = basePath.relativize(targetPath).normalize()

        val encodedPath = relativeTargetPath.toString()
                .split('/')
                .map { URLEncoder.encode(it, "UTF-8") }
                .joinToString("/")

        return URI("${config.download.baseUrl}/$encodedPath").normalize()
    }

    fun generateBreadcrumbs(subPath: String): List<BreadcrumbDto> {
        val (basePath, targetPath) = checkSubpath(config.listing.baseDirectory, subPath)

        val homeDto = BreadcrumbDto("", "Home", "fa-home")
        if (basePath == targetPath) {
            homeDto.active = true
            return listOf(homeDto)
        }

        val breadcrumbs: MutableList<BreadcrumbDto> = mutableListOf()
        val relativeTargetPath = basePath.relativize(targetPath)
        var parent = relativeTargetPath
        do {
            breadcrumbs.add(BreadcrumbDto(
                    parent.toString(),
                    parent.fileName.toString()
            ))
        } while ({ parent = parent.parent; parent }() != null)
        breadcrumbs.add(homeDto)
        breadcrumbs[0].active = true

        return breadcrumbs.reversed()
    }

    fun createFolder(subPath: String, folderName: String): String {
        val (basePath, targetPath) = checkSubpath(config.listing.baseDirectory, subPath)
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
        val (basePath, targetPath) = checkSubpath(config.listing.baseDirectory, subPath)
        val filePath = targetPath.resolve(file.originalFilename).normalize()

        if (!filePath.startsWith(basePath)) {
            throw SubPathIsNotInBasePathException()
        }

        file.transferTo(filePath.toFile())

        return basePath.relativize(filePath.parent).normalize().toString()
    }

    fun deleteFile(subPath: String): String {
        val (basePath, targetPath) = checkSubpath(config.listing.baseDirectory, subPath)
        Files.delete(targetPath)

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

data class FileDto(
        val path: Path,
        val relativePath: String,
        val directory: Boolean,
        val fileName: String = path.fileName.toString()
) : Comparable<FileDto> {

    override fun compareTo(other: FileDto): Int {
        if (!directory) {
            if (other.directory) return 1
        } else if (!other.directory) return -1

        return path.fileName.compareTo(other.path.fileName)
    }
}

data class PathCheckResult(
        val basePath: Path,
        val targetPath: Path
)

data class BreadcrumbDto(
        val relativePath: String,
        val name: String,
        val icon: String? = null,
        var active: Boolean = false
)

class TargetIsFileException : IOException("The target path is not a directory")

class SubPathIsNotInBasePathException : IOException("The sub path is not contained in the base path. Directory traversal?")