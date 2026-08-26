package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable

/**
 * Opens the platform's own image picker. Calling the returned lambda launches it; a picked image
 * reaches [onImagePicked] as its raw bytes, and cancelling calls nothing.
 */
@Composable
expect fun rememberImagePicker(onImagePicked: (ByteArray) -> Unit): () -> Unit
