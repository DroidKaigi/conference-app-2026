@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.browser.document
import org.khronos.webgl.Int8Array
import org.khronos.webgl.toByteArray
import org.w3c.dom.HTMLInputElement
import org.w3c.files.FileReader
import org.w3c.files.get

@Composable
actual fun rememberImagePicker(onImagePicked: (ByteArray) -> Unit): () -> Unit =
    remember(onImagePicked) { { pickImage(onImagePicked) } }

private fun pickImage(onImagePicked: (ByteArray) -> Unit) {
    val input = document.createElement("input") as HTMLInputElement
    input.type = "file"
    input.accept = "image/*"
    input.onchange = {
        val file = input.files?.get(0)
        if (file != null) {
            val reader = FileReader()
            reader.onload = { onImagePicked(readerResultBytes(reader).toByteArray()) }
            reader.readAsArrayBuffer(file)
        }
    }
    input.click()
}

private fun readerResultBytes(reader: FileReader): Int8Array = js("new Int8Array(reader.result)")
