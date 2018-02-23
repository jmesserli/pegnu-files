package nu.peg.web.files.controller

import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.nio.file.NoSuchFileException

@ControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(NoSuchFileException::class)
    fun `404`(): String {
        return "404"
    }
}