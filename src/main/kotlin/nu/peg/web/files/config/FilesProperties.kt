@file:Suppress("unused")

package nu.peg.web.files.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

@ConfigurationProperties("files")
class FilesProperties(
        @NestedConfigurationProperty
        var listing: FilesListingProperties = FilesListingProperties()
)

class FilesListingProperties(
        var baseDirectory: String = "./"
)