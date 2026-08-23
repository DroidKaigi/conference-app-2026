package io.github.droidkaigi.confsched.core.model

data class TimetableItemAsset(
    val videoUrl: String?,
    val slideUrl: String?,
) {
    val isEmpty: Boolean get() = videoUrl == null && slideUrl == null

    companion object {
        val Empty = TimetableItemAsset(videoUrl = null, slideUrl = null)
    }
}
