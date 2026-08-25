package io.github.droidkaigi.confsched.app

import androidx.compose.ui.input.pointer.PointerIcon

// The common PointerIcon set has no horizontal resize cursor; only desktop and web give this a
// platform-specific icon, other platforms have no pointer to hover with.
internal expect val HorizontalResizePointerIcon: PointerIcon
