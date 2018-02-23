package nu.peg.web.files.file

import nu.peg.web.files.config.FilesProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.IOException
import java.net.URI
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

        return URI("${config.download.baseUrl}/$relativeTargetPath").normalize()
    }

    fun generateBreadcrumbs(subPath: String): List<BreadcrumbDto> {
        val (basePath, targetPath) = checkSubpath(config.listing.baseDirectory, subPath)

        val homeDto = BreadcrumbDto("", "Home", "fa-home")
        if (basePath == targetPath)
            return listOf(homeDto)

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

        return breadcrumbs.reversed()
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
        val icon: String? = null
)

class TargetIsFileException : IOException("The target path is not a directory")

class SubPathIsNotInBasePathException : IOException("The sub path is not contained in the base path. Directory traversal?")