package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberTextSharer(): (String) -> Unit = remember { ::shareOrCopy }

/** The Web Share API is only offered on some browsers, so the clipboard stands in on the rest. */
private fun shareOrCopy(text: String): Unit =
    js("{ if (navigator.share) { navigator.share({ text: text }); } else { navigator.clipboard.writeText(text); } }")
