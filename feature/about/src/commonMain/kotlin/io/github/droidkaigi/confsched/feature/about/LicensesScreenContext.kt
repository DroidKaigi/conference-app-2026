package io.github.droidkaigi.confsched.feature.about

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.KaigiLogger
import io.github.droidkaigi.confsched.core.common.PresenterContext
import io.github.droidkaigi.confsched.core.common.ScreenContext
import io.github.droidkaigi.confsched.core.model.LicensesQueryKey
import io.github.droidkaigi.confsched.core.model.LicensesScreenScope

@Inject
class LicensesPresenterContext(override val logger: KaigiLogger) : PresenterContext

@Inject
@SingleIn(LicensesScreenScope::class)
class LicensesScreenContext(
    val licensesQueryKey: LicensesQueryKey,
    override val logger: KaigiLogger,
    val presenterContext: LicensesPresenterContext,
) : ScreenContext
