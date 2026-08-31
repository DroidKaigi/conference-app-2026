package io.github.droidkaigi.confsched.feature.favorites

sealed interface FirstFavoriteNotificationScreenAction {
    data object TurnOnNotifications : FirstFavoriteNotificationScreenAction

    data object Later : FirstFavoriteNotificationScreenAction
}
