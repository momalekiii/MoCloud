package com.pira.ccloud.data.model

import android.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.Color as ComposeColor
import kotlinx.serialization.Serializable

@Serializable
data class SubtitleSettings(
    val backgroundColor: Int = Color.TRANSPARENT,
    val textColor: Int = Color.YELLOW,
    val borderColor: Int = Color.BLACK,
    val textSize: Float = 17f
) {
    companion object {
        // Default subtitle settings
        val DEFAULT = SubtitleSettings()
        
        // Glass background color (semi-transparent black)
        val GLASS_BACKGROUND = Color.argb(128, 0, 0, 0) // 50% transparent black
        
        // Convert Android Color int to Compose Color
        fun Int.toComposeColor(): ComposeColor = ComposeColor(this)
        
        // Convert Compose Color to Android Color int
        fun ComposeColor.toInt(): Int = this.toArgb()
    }
}