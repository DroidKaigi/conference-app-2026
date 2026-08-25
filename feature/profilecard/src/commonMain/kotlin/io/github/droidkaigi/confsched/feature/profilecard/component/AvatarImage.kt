package io.github.droidkaigi.confsched.feature.profilecard.component

// Kotlin's default ByteArray equals/hashCode compare by reference, which would break Compose's
// structural state-change detection; this wrapper compares by content instead.
class AvatarImage(val bytes: ByteArray) {
    override fun equals(other: Any?): Boolean = other is AvatarImage && bytes.contentEquals(other.bytes)

    override fun hashCode(): Int = bytes.contentHashCode()
}
