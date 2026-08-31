package io.github.droidkaigi.confsched.feature.about

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.KaigiLogger
import io.github.droidkaigi.confsched.core.common.PresenterContext
import io.github.droidkaigi.confsched.core.common.ScreenContext
import io.github.droidkaigi.confsched.core.model.DoodleScreenScope

@Inject
class DoodlePresenterContext(override val logger: KaigiLogger) : PresenterContext

@Inject
@SingleIn(DoodleScreenScope::class)
class DoodleScreenContext(
    override val logger: KaigiLogger,
    val presenterContext: DoodlePresenterContext,
) : ScreenContext
