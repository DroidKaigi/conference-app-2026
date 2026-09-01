package io.github.droidkaigi.confsched.feature.favorites

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.KaigiLogger
import io.github.droidkaigi.confsched.core.common.PresenterContext
import io.github.droidkaigi.confsched.core.common.ScreenContext
import io.github.droidkaigi.confsched.core.model.FirstFavoriteWidgetScreenScope

@Inject
class FirstFavoriteWidgetPresenterContext(override val logger: KaigiLogger) : PresenterContext

@Inject
@SingleIn(FirstFavoriteWidgetScreenScope::class)
class FirstFavoriteWidgetScreenContext(
    override val logger: KaigiLogger,
    val presenterContext: FirstFavoriteWidgetPresenterContext,
) : ScreenContext
