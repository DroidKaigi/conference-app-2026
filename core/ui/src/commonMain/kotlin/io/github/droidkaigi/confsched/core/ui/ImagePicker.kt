package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable

/**
 * Opens the platform's own image picker. Calling the returned lambda launches it; a picked image
 * reaches [onImagePicked] as the bytes of a square JPEG no larger than [PICKED_IMAGE_SIDE] pixels,
 * and cancelling calls nothing.
 */
@Composable
expect fun rememberImagePicker(onImagePicked: (ByteArray) -> Unit): () -> Unit

// Decoded synchronously on every composition that shows it, so stored no larger than its largest plate needs.
const val PICKED_IMAGE_SIDE = 512

const val PICKED_IMAGE_JPEG_QUALITY = 90
