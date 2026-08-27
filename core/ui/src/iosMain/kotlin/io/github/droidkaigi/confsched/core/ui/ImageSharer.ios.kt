package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIImage

@Composable
actual fun rememberImageSharer(): (message: String, png: ByteArray) -> Unit = remember {
    { message, png ->
        val image = UIImage(data = png.toNSData())
        keyRootViewController()?.presentViewController(
            UIActivityViewController(activityItems = listOf(message, image), applicationActivities = null),
            animated = true,
            completion = null,
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun ByteArray.toNSData(): NSData = usePinned { pinned ->
    NSData.create(bytes = pinned.addressOf(0), length = size.toULong())
}
