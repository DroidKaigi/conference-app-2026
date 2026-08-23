package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable

/**
 * Hands text to whatever the platform offers for passing it on: the system share sheet where
 * there is one, and the clipboard on the platforms that have none.
 */
@Composable
expect fun rememberTextSharer(): (String) -> Unit
