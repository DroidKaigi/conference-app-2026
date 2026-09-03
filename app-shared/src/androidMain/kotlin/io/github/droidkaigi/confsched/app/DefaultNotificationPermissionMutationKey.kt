package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.model.FirstFavoriteNotificationScreenScope
import io.github.droidkaigi.confsched.core.model.MutationTag
import io.github.droidkaigi.confsched.core.model.NotificationPermissionMutationKey
import io.github.droidkaigi.confsched.core.model.SoilIds
import soil.query.buildMutationKey

@Inject
@ContributesBinding(FirstFavoriteNotificationScreenScope::class)
class DefaultNotificationPermissionMutationKey(
    extraTag: MutationTag,
    requester: AndroidNotificationPermissionRequester,
) : NotificationPermissionMutationKey by buildMutationKey(
    id = SoilIds.notificationPermissionMutation(extraTag),
    mutate = { requester.request() },
)
