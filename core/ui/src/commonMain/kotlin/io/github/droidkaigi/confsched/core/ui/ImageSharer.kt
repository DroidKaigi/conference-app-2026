package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable

/**
 * Hands a PNG image to whatever the platform offers for passing it on: the system share sheet
 * where there is one, and a save dialog on the platforms that have none.
 */
@Composable
expect fun rememberImageSharer(): (ByteArray) -> Unit
