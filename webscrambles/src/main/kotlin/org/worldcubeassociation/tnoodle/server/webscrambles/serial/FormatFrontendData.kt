package org.worldcubeassociation.tnoodle.server.webscrambles.serial

import kotlinx.serialization.Serializable
import org.worldcubeassociation.tnoodle.server.model.FormatData

@Serializable
data class FormatFrontendData(
    val name: String,
    val shortName: String
) {
    companion object {
        fun fromDataModel(format: FormatData): FormatFrontendData {
            return FormatFrontendData(
                format.description,
                format.tag
            )
        }
    }
}
