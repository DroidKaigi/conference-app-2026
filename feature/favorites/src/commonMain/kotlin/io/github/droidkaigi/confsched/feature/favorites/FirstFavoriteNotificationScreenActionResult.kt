package io.github.droidkaigi.confsched.feature.favorites

sealed interface FirstFavoriteNotificationScreenActionResult {
    /** The notification step is over, however it was answered; the widget step follows it. */
    data object Answered : FirstFavoriteNotificationScreenActionResult
}
