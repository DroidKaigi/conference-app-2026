@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.khronos.webgl.Int8Array
import org.khronos.webgl.toInt8Array

@Composable
actual fun rememberImageSharer(): (ByteArray) -> Unit = remember {
    { bytes -> shareOrDownload(bytes.toInt8Array()) }
}

/** Only some browsers offer to share a file, so a download stands in on the rest. */
private fun shareOrDownload(data: Int8Array): Unit = js(
    """{
        const file = new File([data], 'profile-card.png', { type: 'image/png' });
        if (navigator.canShare && navigator.canShare({ files: [file] })) {
            navigator.share({ files: [file] });
        } else {
            const url = URL.createObjectURL(file);
            const link = document.createElement('a');
            link.href = url;
            link.download = 'profile-card.png';
            link.click();
            URL.revokeObjectURL(url);
        }
    }""",
)
