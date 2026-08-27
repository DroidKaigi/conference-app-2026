package io.github.droidkaigi.confsched.core.ui

import android.content.ClipData
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
actual fun rememberImageSharer(): (ByteArray) -> Unit {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    return remember(context, coroutineScope) { { bytes -> coroutineScope.shareImage(context, bytes) } }
}

private fun CoroutineScope.shareImage(context: Context, bytes: ByteArray) {
    launch {
        val uri = withContext(Dispatchers.IO) {
            val directory = File(context.cacheDir, SHARED_IMAGE_DIRECTORY).apply { mkdirs() }
            val file = File(directory, SHARED_IMAGE_FILE_NAME).apply { writeBytes(bytes) }
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            // The share sheet renders its preview thumbnail from the clip data, not the extra.
            clipData = ClipData.newUri(context.contentResolver, null, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, null))
    }
}

// Mirrors the <cache-path> the app manifest's FileProvider exposes.
private const val SHARED_IMAGE_DIRECTORY = "shared-images"
private const val SHARED_IMAGE_FILE_NAME = "profile-card.png"
