package io.github.droidkaigi.confsched.core.ui

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import java.io.ByteArrayOutputStream

actual fun ImageBitmap.encodeToPng(): ByteArray = ByteArrayOutputStream().use { stream ->
    asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream)
    stream.toByteArray()
}
