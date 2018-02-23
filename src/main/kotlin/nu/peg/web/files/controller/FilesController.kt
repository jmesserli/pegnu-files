package nu.peg.web.files.controller

import nu.peg.web.files.file.FileListingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.view.RedirectView

@Controller
class FilesController @Autowired constructor(
        private val fileListingService: FileListingService
) {
    @GetMapping("/")
    fun home(): RedirectView {
        return RedirectView("/files")
    }

    @GetMapping("/files")
    fun files(
            model: Model,
            @RequestParam("path", required = false, defaultValue = "")
            path: String
    ): String {
        val files = fileListingService.listFiles(path)

        model.addAttribute("files", files)

        return "files"
    }
}