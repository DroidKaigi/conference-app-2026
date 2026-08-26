package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.posix.memcpy

@Composable
actual fun rememberImagePicker(onImagePicked: (ByteArray) -> Unit): () -> Unit {
    // PHPickerViewController holds its delegate weakly, so the composition owns the strong reference.
    val delegate = remember(onImagePicked) { ImagePickerDelegate(onImagePicked) }
    return remember(delegate) { { presentImagePicker(delegate) } }
}

private fun presentImagePicker(delegate: PHPickerViewControllerDelegateProtocol) {
    val configuration = PHPickerConfiguration().apply {
        filter = PHPickerFilter.imagesFilter
        selectionLimit = 1
    }
    val picker = PHPickerViewController(configuration)
    picker.delegate = delegate
    keyRootViewController()?.presentViewController(picker, animated = true, completion = null)
}

private const val IMAGE_TYPE_IDENTIFIER = "public.image"

private class ImagePickerDelegate(
    private val onImagePicked: (ByteArray) -> Unit,
) : NSObject(),
    PHPickerViewControllerDelegateProtocol {
    override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
        picker.dismissViewControllerAnimated(true, null)
        val itemProvider = (didFinishPicking.firstOrNull() as? PHPickerResult)?.itemProvider ?: return
        itemProvider.loadDataRepresentationForTypeIdentifier(IMAGE_TYPE_IDENTIFIER) { data, _ ->
            val bytes = data?.toByteArray() ?: return@loadDataRepresentationForTypeIdentifier
            dispatch_async(dispatch_get_main_queue()) { onImagePicked(bytes) }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    if (size == 0) return ByteArray(0)
    return ByteArray(size).apply { usePinned { memcpy(it.addressOf(0), bytes, length) } }
}
