package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.model.FirstFavoriteNotificationScreenScope
import io.github.droidkaigi.confsched.core.model.MutationTag
import io.github.droidkaigi.confsched.core.model.NotificationPermissionMutationKey
import io.github.droidkaigi.confsched.core.model.NotificationPermissionResult
import io.github.droidkaigi.confsched.core.model.SoilIds
import soil.query.buildMutationKey

@Inject
@ContributesBinding(FirstFavoriteNotificationScreenScope::class)
class DefaultNotificationPermissionMutationKey(
    extraTag: MutationTag,
) : NotificationPermissionMutationKey by buildMutationKey(
    id = SoilIds.notificationPermissionMutation(extraTag),
    // The platform posts no notifications, so there is nothing to ask the reader for.
    mutate = { NotificationPermissionResult.Disabled },
)
