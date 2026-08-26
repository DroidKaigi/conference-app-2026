package io.github.droidkaigi.confsched.core.ui

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image

actual fun ImageBitmap.encodeToPng(): ByteArray = Image.makeFromBitmap(asSkiaBitmap())
    .encodeToData(EncodedImageFormat.PNG)
    ?.bytes
    ?: error("Skia could not encode the image as PNG")
