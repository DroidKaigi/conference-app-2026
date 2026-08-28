package io.github.droidkaigi.confsched.feature.eventmap

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.KaigiLogger
import io.github.droidkaigi.confsched.core.common.PresenterContext
import io.github.droidkaigi.confsched.core.common.ScreenContext
import io.github.droidkaigi.confsched.core.model.PrizesQueryKey
import io.github.droidkaigi.confsched.core.model.StampCollectingScreenScope

@Inject
class StampCollectingPresenterContext(
    override val logger: KaigiLogger,
) : PresenterContext

@Inject
@SingleIn(StampCollectingScreenScope::class)
class StampCollectingScreenContext(
    val prizesQueryKey: PrizesQueryKey,
    override val logger: KaigiLogger,
    val presenterContext: StampCollectingPresenterContext,
) : ScreenContext
