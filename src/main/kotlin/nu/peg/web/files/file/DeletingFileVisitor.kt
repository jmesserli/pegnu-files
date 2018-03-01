package nu.peg.web.files.file

import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes

/**
 * Allows the deletion of non-empty directory trees
 */
class DeletingFileVisitor : FileVisitor<Path> {
    override fun visitFileFailed(file: Path?, exc: IOException?) = FileVisitResult.CONTINUE

    override fun preVisitDirectory(dir: Path?, attrs: BasicFileAttributes?) = FileVisitResult.CONTINUE

    override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
        Files.delete(dir)
        return FileVisitResult.CONTINUE
    }

    override fun visitFile(file: Path, attrs: BasicFileAttributes?): FileVisitResult {
        Files.delete(file)
        return FileVisitResult.CONTINUE
    }
}