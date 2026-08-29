package io.github.droidkaigi.confsched.core.ui

import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import org.jetbrains.skia.Rect
import org.jetbrains.skia.SamplingMode
import org.jetbrains.skia.Surface

internal fun ByteArray.toPickedImageJpeg(): ByteArray? {
    val image = runCatching { Image.makeFromEncoded(this) }.getOrNull() ?: return null
    val side = minOf(image.width, image.height)
    val target = minOf(side, PICKED_IMAGE_SIDE)
    val surface = Surface.makeRasterN32Premul(target, target)
    surface.canvas.drawImageRect(
        image = image,
        src = Rect.makeXYWH(((image.width - side) / 2).toFloat(), ((image.height - side) / 2).toFloat(), side.toFloat(), side.toFloat()),
        dst = Rect.makeWH(target.toFloat(), target.toFloat()),
        samplingMode = SamplingMode.MITCHELL,
        paint = null,
        strict = true,
    )
    return surface.makeImageSnapshot().encodeToData(EncodedImageFormat.JPEG, PICKED_IMAGE_JPEG_QUALITY)?.bytes
}
