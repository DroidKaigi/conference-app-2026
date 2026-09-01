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

@Composable
actual fun rememberImagePicker(
    onImagePicked: (ByteArray) -> Unit,
    onImagePickFailed: () -> Unit,
): () -> Unit {
    val coroutineScope = rememberCoroutineScope()
    return remember(coroutineScope, onImagePicked, onImagePickFailed) {
        { coroutineScope.pickImage(onImagePicked, onImagePickFailed) }
    }
}

private fun CoroutineScope.pickImage(onImagePicked: (ByteArray) -> Unit, onImagePickFailed: () -> Unit) {
    launch {
        // FileDialog blocks until the user is done, so it must not run on the UI thread.
        val file = withContext(Dispatchers.IO) { chooseImage() } ?: return@launch
        val bytes = withContext(Dispatchers.IO) { file.readBytes().toPickedImageJpeg() }
        if (bytes != null) onImagePicked(bytes) else onImagePickFailed()
    }
}

private fun chooseImage(): File? {
    val dialog = FileDialog(null as Frame?, null, FileDialog.LOAD)
    dialog.setFilenameFilter { _, name -> IMAGE_EXTENSIONS.any { name.lowercase().endsWith(it) } }
    dialog.isVisible = true
    val directory = dialog.directory ?: return null
    val file = dialog.file ?: return null
    return File(directory, file)
}

private val IMAGE_EXTENSIONS = listOf(".png", ".jpg", ".jpeg", ".gif", ".webp", ".bmp")
