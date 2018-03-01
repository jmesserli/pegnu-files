package nu.peg.web.files.file

import java.nio.file.Path

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