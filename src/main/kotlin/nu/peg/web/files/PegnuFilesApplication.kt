package nu.peg.web.files

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class PegnuFilesApplication

fun main(args: Array<String>) {
    SpringApplication.run(PegnuFilesApplication::class.java, *args)
}