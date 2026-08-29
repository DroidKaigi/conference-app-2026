package io.github.droidkaigi.confsched.feature.profilecard

import androidx.compose.ui.graphics.ImageBitmap
import soil.query.MutationKey

typealias ShareProfileCardMutationKey = MutationKey<ShareableProfileCardImage, ImageBitmap>

/** A finished share image, in the PNG encoding every platform's share sheet takes. */
class ShareableProfileCardImage(val pngBytes: ByteArray) {
    override fun equals(other: Any?): Boolean = other is ShareableProfileCardImage && pngBytes.contentEquals(other.pngBytes)

    override fun hashCode(): Int = pngBytes.contentHashCode()
}
