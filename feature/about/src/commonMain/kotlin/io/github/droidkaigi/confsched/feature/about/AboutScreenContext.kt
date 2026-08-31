package io.github.droidkaigi.confsched.feature.about

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.KaigiLogger
import io.github.droidkaigi.confsched.core.common.PresenterContext
import io.github.droidkaigi.confsched.core.common.ScreenContext
import io.github.droidkaigi.confsched.core.model.AboutScreenScope
import io.github.droidkaigi.confsched.core.model.DoodlesSubscriptionKey
import io.github.droidkaigi.confsched.core.model.buildconfig.BuildConfigProvider

@Inject
class AboutPresenterContext(
    val buildConfig: BuildConfigProvider,
    override val logger: KaigiLogger,
) : PresenterContext

@Inject
@SingleIn(AboutScreenScope::class)
class AboutScreenContext(
    val doodlesSubscriptionKey: DoodlesSubscriptionKey,
    override val logger: KaigiLogger,
    val presenterContext: AboutPresenterContext,
) : ScreenContext
