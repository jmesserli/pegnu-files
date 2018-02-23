package nu.peg.web.files.controller

import nu.peg.web.files.config.FilesProperties
import nu.peg.web.files.file.SubPathIsNotInBasePathException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ModelAttribute
import java.nio.file.NoSuchFileException

@ControllerAdvice
class GlobalControllerAdvice @Autowired constructor(
        private val config: FilesProperties
) {
    @ModelAttribute("baseUrl")
    fun baseUrl(): String {
        return config.application.baseUrl
    }

    @ExceptionHandler(NoSuchFileException::class)
    fun `404`(model: Model): String {
        model.addAttribute("statusCode", "404")
        model.addAttribute("statusText", "The requested page could not be found")
        return "status"
    }

    @ExceptionHandler(SubPathIsNotInBasePathException::class)
    fun `400`(model: Model): String {
        model.addAttribute("statusCode", "400")
        model.addAttribute("statusText", "Bad request")
        return "status"
    }
}