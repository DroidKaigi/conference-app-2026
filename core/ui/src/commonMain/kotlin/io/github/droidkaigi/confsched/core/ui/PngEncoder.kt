package io.github.droidkaigi.confsched.core.ui

import androidx.compose.ui.graphics.ImageBitmap

/** Encodes [this] as PNG bytes, the one image encoding every platform's share sheet accepts. */
expect fun ImageBitmap.encodeToPng(): ByteArray
