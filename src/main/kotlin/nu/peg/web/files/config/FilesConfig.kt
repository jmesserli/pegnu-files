package nu.peg.web.files.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(FilesProperties::class)
class FilesConfig