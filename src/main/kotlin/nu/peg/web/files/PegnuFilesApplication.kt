package nu.peg.web.files

import nu.peg.web.files.file.FileListingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@SpringBootApplication
class PegnuFilesApplication

fun main(args: Array<String>) {
    SpringApplication.run(PegnuFilesApplication::class.java, *args)
}


@Controller
class TestController @Autowired constructor(
        private val fileListingService: FileListingService
) {
    @GetMapping("/", "/files")
    fun files(model: Model): String {
        val files = fileListingService.listFiles()

        model.addAttribute("files", files)

        return "files"
    }
}