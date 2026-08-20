package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

/** The desktop has no share sheet, so the text goes to the clipboard for the user to paste. */
@Composable
actual fun rememberTextSharer(): (String) -> Unit = remember {
    { text ->
        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(text), null)
    }
}
