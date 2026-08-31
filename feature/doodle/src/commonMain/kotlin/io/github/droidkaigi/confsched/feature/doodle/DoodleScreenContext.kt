package io.github.droidkaigi.confsched.feature.doodle

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.KaigiLogger
import io.github.droidkaigi.confsched.core.common.PresenterContext
import io.github.droidkaigi.confsched.core.common.ScreenContext
import io.github.droidkaigi.confsched.core.model.DoodleMutationKey
import io.github.droidkaigi.confsched.core.model.DoodleScreenScope
import io.github.droidkaigi.confsched.core.model.DoodleTarget
import io.github.droidkaigi.confsched.core.model.DoodlesSubscriptionKey
import io.github.droidkaigi.confsched.core.model.ProfileCardSubscriptionKey

@Inject
class DoodlePresenterContext(
    val target: DoodleTarget,
    val doodleMutationKey: DoodleMutationKey,
    override val logger: KaigiLogger,
) : PresenterContext

@Inject
@SingleIn(DoodleScreenScope::class)
class DoodleScreenContext(
    val target: DoodleTarget,
    val doodlesSubscriptionKey: DoodlesSubscriptionKey,
    val profileCardSubscriptionKey: ProfileCardSubscriptionKey,
    override val logger: KaigiLogger,
    val presenterContext: DoodlePresenterContext,
) : ScreenContext
