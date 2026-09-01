package io.github.droidkaigi.confsched.core.model

import soil.query.MutationKey

typealias NotificationPermissionMutationKey = MutationKey<NotificationPermissionResult, Unit>

/** Where the platform left notifications once the app asked to post them. */
enum class NotificationPermissionResult {
    /** The app may post notifications. */
    Enabled,

    /** Notifications stay off. */
    Disabled,

    /** Notifications stay off, and the system settings that turn them on are open. */
    SettingsOpened,
}
