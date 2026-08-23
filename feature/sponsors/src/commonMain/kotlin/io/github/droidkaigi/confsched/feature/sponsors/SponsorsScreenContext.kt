package io.github.droidkaigi.confsched.feature.sponsors

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.KaigiLogger
import io.github.droidkaigi.confsched.core.common.PresenterContext
import io.github.droidkaigi.confsched.core.common.ScreenContext
import io.github.droidkaigi.confsched.core.model.SponsorsQueryKey
import io.github.droidkaigi.confsched.core.model.SponsorsScreenScope

@Inject
class SponsorsPresenterContext(override val logger: KaigiLogger) : PresenterContext

@Inject
@SingleIn(SponsorsScreenScope::class)
class SponsorsScreenContext(
    val sponsorsQueryKey: SponsorsQueryKey,
    override val logger: KaigiLogger,
    val presenterContext: SponsorsPresenterContext,
) : ScreenContext
