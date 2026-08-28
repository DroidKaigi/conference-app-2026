package io.github.droidkaigi.confsched.feature.eventmap

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.KaigiLogger
import io.github.droidkaigi.confsched.core.common.PresenterContext
import io.github.droidkaigi.confsched.core.common.ScreenContext
import io.github.droidkaigi.confsched.core.model.PrizeOverlayScreenScope
import io.github.droidkaigi.confsched.core.model.PrizesQueryKey

@Inject
class PrizeOverlayPresenterContext(
    override val logger: KaigiLogger,
) : PresenterContext

@Inject
@SingleIn(PrizeOverlayScreenScope::class)
class PrizeOverlayScreenContext(
    val navKey: PrizeOverlayNavKey,
    val prizesQueryKey: PrizesQueryKey,
    override val logger: KaigiLogger,
    val presenterContext: PrizeOverlayPresenterContext,
) : ScreenContext
