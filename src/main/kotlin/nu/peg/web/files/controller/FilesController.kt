package nu.peg.web.files.controller

import nu.peg.web.files.file.FileService
import nu.peg.web.files.file.TargetIsFileException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.view.RedirectView

@Controller
class FilesController @Autowired constructor(
        private val fileService: FileService
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
    ): Any {
        val files = try {
            fileService.listFiles(path)
        } catch (ex: TargetIsFileException) {
            val view = RedirectView("/download")
            view.setPropagateQueryParams(true)
            return view
        }

        model.addAttribute("files", files)
        model.addAttribute("breadcrumbs", fileService.generateBreadcrumbs(path))
        return "files"
    }

    @GetMapping("/download")
    fun download(
            @RequestParam("path", required = true)
            path: String
    ): RedirectView {
        val downloadUri = fileService.getDownloadLink(path)
        return RedirectView(downloadUri.toString())
    }
}