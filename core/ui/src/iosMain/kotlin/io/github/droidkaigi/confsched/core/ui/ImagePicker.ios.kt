package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSData
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIGraphicsImageRenderer
import platform.UIKit.UIGraphicsImageRendererFormat
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
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
            val bytes = data?.let(::uprightJpeg)?.toByteArray() ?: return@loadDataRepresentationForTypeIdentifier
            dispatch_async(dispatch_get_main_queue()) { onImagePicked(bytes) }
        }
    }
}

private const val JPEG_QUALITY = 0.9

// The picked bytes are decoded later by Skia, which neither reads EXIF orientation nor
// understands HEIC, so the image is normalized to an upright JPEG here.
@OptIn(ExperimentalForeignApi::class)
private fun uprightJpeg(data: NSData): NSData? {
    val image = UIImage.imageWithData(data) ?: return null
    val format = UIGraphicsImageRendererFormat.defaultFormat().apply { scale = 1.0 }
    val size = image.size
    val upright = UIGraphicsImageRenderer(size, format).imageWithActions {
        size.useContents { image.drawInRect(CGRectMake(0.0, 0.0, width, height)) }
    }
    return UIImageJPEGRepresentation(upright, JPEG_QUALITY)
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    if (size == 0) return ByteArray(0)
    return ByteArray(size).apply { usePinned { memcpy(it.addressOf(0), bytes, length) } }
}
