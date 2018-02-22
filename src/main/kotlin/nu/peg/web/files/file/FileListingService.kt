package nu.peg.web.files.file

import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors
import javax.transaction.Transactional

@Service
@Transactional
class FileListingService {
    fun listFiles(): List<FileDto> = Files.walk(Paths.get("."), 1)
            .map { FileDto(it, Files.isDirectory(it)) }
            .sorted()
            .collect(Collectors.toList())
}

data class FileDto(
        val path: Path,
        val directory: Boolean
) : Comparable<FileDto> {

    override fun compareTo(other: FileDto): Int {
        if (!directory) {
            if (other.directory) return 1
        } else if (!other.directory) return -1

        return path.fileName.compareTo(other.path.fileName)
    }
}
