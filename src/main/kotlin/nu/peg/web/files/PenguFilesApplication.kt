package nu.peg.web.files

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@SpringBootApplication
class PenguFilesApplication

fun main(args: Array<String>) {
    SpringApplication.run(PenguFilesApplication::class.java, *args)
}


@Controller
class TestController {
    @GetMapping("/greeting")
    fun greeting(): String {
        return "greeting"
    }
}