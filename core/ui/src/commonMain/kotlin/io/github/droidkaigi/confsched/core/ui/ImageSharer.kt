package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable

/**
 * Hands a PNG image and the message that accompanies it to whatever the platform offers for
 * passing them on: the system share sheet where there is one, and a save dialog for the image
 * alone on the platforms that have none.
 */
@Composable
expect fun rememberImageSharer(): (message: String, png: ByteArray) -> Unit
