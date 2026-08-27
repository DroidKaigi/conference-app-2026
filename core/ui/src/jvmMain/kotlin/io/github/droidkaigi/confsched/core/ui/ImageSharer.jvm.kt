package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

/** The desktop has no share sheet, so the image goes wherever the user chooses to save it. */
@Composable
actual fun rememberImageSharer(): (ByteArray) -> Unit {
    val coroutineScope = rememberCoroutineScope()
    return remember(coroutineScope) { coroutineScope::saveImage }
}

private fun CoroutineScope.saveImage(bytes: ByteArray) {
    launch {
        // FileDialog blocks until the user is done, so it must not run on the UI thread.
        withContext(Dispatchers.IO) { chooseDestination()?.writeBytes(bytes) }
    }
}

private fun chooseDestination(): File? {
    val dialog = FileDialog(null as Frame?, null, FileDialog.SAVE)
    dialog.file = SHARED_IMAGE_FILE_NAME
    dialog.isVisible = true
    val directory = dialog.directory ?: return null
    val file = dialog.file ?: return null
    return File(directory, file)
}

private const val SHARED_IMAGE_FILE_NAME = "profile-card.png"
