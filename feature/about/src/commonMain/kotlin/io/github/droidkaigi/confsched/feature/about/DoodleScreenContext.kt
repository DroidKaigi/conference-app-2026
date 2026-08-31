package io.github.droidkaigi.confsched.feature.about

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.KaigiLogger
import io.github.droidkaigi.confsched.core.common.PresenterContext
import io.github.droidkaigi.confsched.core.common.ScreenContext
import io.github.droidkaigi.confsched.core.model.DoodleMutationKey
import io.github.droidkaigi.confsched.core.model.DoodleScreenScope
import io.github.droidkaigi.confsched.core.model.DoodleSubscriptionKey

@Inject
class DoodlePresenterContext(
    val doodleMutationKey: DoodleMutationKey,
    override val logger: KaigiLogger,
) : PresenterContext

@Inject
@SingleIn(DoodleScreenScope::class)
class DoodleScreenContext(
    val doodleSubscriptionKey: DoodleSubscriptionKey,
    override val logger: KaigiLogger,
    val presenterContext: DoodlePresenterContext,
) : ScreenContext
