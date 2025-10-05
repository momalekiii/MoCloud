package com.pira.ccloud.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.pira.ccloud.VideoPlayerActivity

object DownloadUtils {
    fun openUrl(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            // Handle error or show a message
        }
    }

    fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Video URL", text)
        clipboard.setPrimaryClip(clip)
        
        // Show a toast or snackbar to indicate success
        android.widget.Toast.makeText(context, "Link copied to clipboard", android.widget.Toast.LENGTH_SHORT).show()
    }

    fun openWithADM(context: Context, url: String) {
        try {
            // Try multiple common ADM package names and intent actions
            val packages = arrayOf(
                "com.dv.adm",
                "com.dv.adm.pay",
                "com.dv.get",
                "com.dv.adm.old"
            )
            
            var success = false
            for (pkg in packages) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    intent.`package` = pkg
                    context.startActivity(intent)
                    success = true
                    break
                } catch (e: Exception) {
                    // Try next package
                }
            }
            
            // If none of the specific packages work, try the general approach
            if (!success) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("adm://$url"))
                    context.startActivity(intent)
                    success = true
                } catch (e: Exception) {
                    // Continue to fallback
                }
            }
            
            // If all else fails, fallback to browser
            if (!success) {
                openUrl(context, url)
            }
        } catch (e: Exception) {
            // If ADM is not installed, fallback to browser
            openUrl(context, url)
        }
    }

    fun openWithVLC(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.parse(url), "video/*")
            intent.setPackage("org.videolan.vlc")
            context.startActivity(intent)
        } catch (e: Exception) {
            // If VLC is not installed, show a message
            android.widget.Toast.makeText(context, "VLC Player not installed", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}