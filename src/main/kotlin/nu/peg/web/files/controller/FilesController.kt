package nu.peg.web.files.controller

import nu.peg.web.files.file.FileService
import nu.peg.web.files.file.TargetIsFileException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView
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

        model.addAttribute("path", path)
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

    @GetMapping("/createFolder")
    fun createFolder(
            @RequestParam("path", required = true)
            path: String,
            @RequestParam("folderName", required = true)
            folderName: String,
            model: Model
    ): ModelAndView {
        val subPath = fileService.createFolder(path, folderName)
        return ModelAndView(RedirectView("/files"), mapOf("path" to subPath))
    }

    @PostMapping("/uploadFile")
    fun uploadFile(
            @RequestPart("file") file: MultipartFile,
            @RequestParam("path") path: String
    ): ModelAndView {
        val subPath = fileService.uploadFile(path, file)
        return ModelAndView(RedirectView("/files"), mapOf("path" to subPath))
    }

    @GetMapping("/deleteFile")
    fun deleteFile(
            @RequestParam("path") path: String
    ): ModelAndView {
        val subPath = fileService.deleteFile(path)
        return ModelAndView(RedirectView("/files"), mapOf("path" to subPath))
    }
}