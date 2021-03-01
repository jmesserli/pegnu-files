package nu.peg.web.files.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("files")
class FilesProperties(
    var listingBaseDir: String = "",
    var downloadBaseDir: String = "",
    var webBaseUrl: String = "",
    var headSnippets: List<String> = listOf()
)
