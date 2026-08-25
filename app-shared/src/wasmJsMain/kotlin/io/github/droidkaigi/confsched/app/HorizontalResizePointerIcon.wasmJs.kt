package io.github.droidkaigi.confsched.app

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.fromKeyword

@OptIn(ExperimentalComposeUiApi::class)
internal actual val HorizontalResizePointerIcon: PointerIcon = PointerIcon.fromKeyword("col-resize")
