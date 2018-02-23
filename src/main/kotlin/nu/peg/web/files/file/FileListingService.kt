package nu.peg.web.files.file

import nu.peg.web.files.config.FilesProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.nio.file.*
import java.util.stream.Collectors
import javax.transaction.Transactional

@Service
@Transactional
class FileListingService
@Autowired
constructor(
        private val config: FilesProperties
) {
    fun listFiles(subPath: String): List<FileDto> {
        val baseDir = config.listing.baseDirectory
        val basePath = Paths.get(baseDir)
        val targetPath = Paths.get(baseDir, subPath).normalize()

        if (!targetPath.startsWith(basePath))
            throw NoSuchFileException("")

        return Files.walk(targetPath, 1, FileVisitOption.FOLLOW_LINKS)
                .filter { it != targetPath }
                .map { FileDto(it, basePath.relativize(it).toString(), Files.isDirectory(it)) }
                .sorted()
                .collect(Collectors.toList())
    }
}

data class FileDto(
        val path: Path,
        val relativePath: String,
        val directory: Boolean
) : Comparable<FileDto> {

    override fun compareTo(other: FileDto): Int {
        if (!directory) {
            if (other.directory) return 1
        } else if (!other.directory) return -1

        return path.fileName.compareTo(other.path.fileName)
    }
}
