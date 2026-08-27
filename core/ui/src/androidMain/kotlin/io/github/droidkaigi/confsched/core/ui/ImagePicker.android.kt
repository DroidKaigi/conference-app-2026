package io.github.droidkaigi.confsched.core.ui

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

@Composable
actual fun rememberImagePicker(onImagePicked: (ByteArray) -> Unit): () -> Unit {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) coroutineScope.readImage(context.contentResolver, uri, onImagePicked)
    }
    return remember(launcher) {
        { launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
    }
}

// The picked bytes are decoded later with a decoder that neither reads EXIF orientation nor
// understands HEIF, so the image is normalized to an upright JPEG here.
private fun CoroutineScope.readImage(resolver: ContentResolver, uri: Uri, onImagePicked: (ByteArray) -> Unit) {
    launch {
        val bytes = withContext(Dispatchers.IO) {
            decodeUpright(resolver, uri)?.squareThumbnail(PICKED_IMAGE_SIDE)?.let { bitmap ->
                val out = ByteArrayOutputStream()
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, PICKED_IMAGE_JPEG_QUALITY, out)) out.toByteArray() else null
            }
        }
        if (bytes != null) onImagePicked(bytes)
    }
}

private fun Bitmap.squareThumbnail(maxSide: Int): Bitmap {
    val side = minOf(width, height)
    val scale = minOf(1f, maxSide.toFloat() / side)
    val matrix = Matrix().apply { setScale(scale, scale) }
    return Bitmap.createBitmap(this, (width - side) / 2, (height - side) / 2, side, side, matrix, true)
}

private fun decodeUpright(resolver: ContentResolver, uri: Uri): Bitmap? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
    ImageDecoder.decodeBitmap(ImageDecoder.createSource(resolver, uri)) { decoder, _, _ ->
        // Bitmap.compress rejects hardware bitmaps.
        decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
    }
} else {
    val bitmap = resolver.openInputStream(uri)?.use(BitmapFactory::decodeStream) ?: return null
    val orientation = resolver.openInputStream(uri)?.use { stream ->
        ExifInterface(stream).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
    } ?: ExifInterface.ORIENTATION_NORMAL
    val matrix = Matrix().apply {
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> postRotate(90f)

            ExifInterface.ORIENTATION_ROTATE_180 -> postRotate(180f)

            ExifInterface.ORIENTATION_ROTATE_270 -> postRotate(270f)

            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> postScale(-1f, 1f)

            ExifInterface.ORIENTATION_FLIP_VERTICAL -> postScale(1f, -1f)

            ExifInterface.ORIENTATION_TRANSPOSE -> {
                postRotate(90f)
                postScale(-1f, 1f)
            }

            ExifInterface.ORIENTATION_TRANSVERSE -> {
                postRotate(270f)
                postScale(-1f, 1f)
            }

            else -> return bitmap
        }
    }
    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}
