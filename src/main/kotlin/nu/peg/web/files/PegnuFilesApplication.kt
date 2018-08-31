package nu.peg.web.files

import nu.peg.web.files.bytecode.OAuthRequestAuthenticatorFixer
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class PegnuFilesApplication

fun main(args: Array<String>) {
    OAuthRequestAuthenticatorFixer.ensureExecution()
    SpringApplication.run(PegnuFilesApplication::class.java, *args)
}