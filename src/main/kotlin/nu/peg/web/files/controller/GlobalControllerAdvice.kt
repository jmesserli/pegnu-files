package nu.peg.web.files.controller

import nu.peg.web.files.config.FilesProperties
import nu.peg.web.files.exception.SubPathIsNotInBasePathException
import nu.peg.web.files.security.SecurityUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ModelAttribute
import java.nio.file.NoSuchFileException

@ControllerAdvice
class GlobalControllerAdvice @Autowired constructor(
        private val config: FilesProperties
) {
    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(GlobalControllerAdvice::class.java)
    }

    @ModelAttribute("version")
    fun version(): String {
        return config.version
    }

    @ModelAttribute("baseUrl")
    fun baseUrl(): String {
        return config.application.baseUrl
    }

    @ModelAttribute("loggedIn")
    fun loggedIn() = SecurityUtil.isLoggedIn()

    @ModelAttribute("username")
    fun username(): String {
        val auth = SecurityContextHolder.getContext().authentication

        return auth?.name ?: "no-user"
    }

    @ExceptionHandler(NoSuchFileException::class)
    fun `404`(model: Model) = handleStatus(model, "404", "The requested page could not be found")

    @ExceptionHandler(SubPathIsNotInBasePathException::class)
    fun `400`(model: Model) = handleStatus(model, "400", "Bad request")

    @ExceptionHandler(Exception::class)
    fun generalException(model: Model, exception: Exception): String {
        LOGGER.error("Global exception handler caught exception", exception)
        return handleStatus(model, "500", "Oops. Something has gone wrong :/", exception.javaClass.canonicalName)
    }

    private fun handleStatus(model: Model, code: String, text: String, detail: String? = null): String {
        model.addAttribute("statusCode", code)
        model.addAttribute("statusText", text)
        if (detail != null) {
            model.addAttribute("detail", detail)
        }
        return "status"
    }
}