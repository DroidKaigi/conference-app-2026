package io.github.droidkaigi.confsched.feature.favorites

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.KaigiLogger
import io.github.droidkaigi.confsched.core.common.PresenterContext
import io.github.droidkaigi.confsched.core.common.ScreenContext
import io.github.droidkaigi.confsched.core.model.FirstFavoriteGuidanceMutationKey
import io.github.droidkaigi.confsched.core.model.FirstFavoriteNotificationScreenScope

@Inject
class FirstFavoriteNotificationPresenterContext(
    val firstFavoriteGuidanceMutationKey: FirstFavoriteGuidanceMutationKey,
    override val logger: KaigiLogger,
) : PresenterContext

@Inject
@SingleIn(FirstFavoriteNotificationScreenScope::class)
class FirstFavoriteNotificationScreenContext(
    override val logger: KaigiLogger,
    val presenterContext: FirstFavoriteNotificationPresenterContext,
) : ScreenContext
