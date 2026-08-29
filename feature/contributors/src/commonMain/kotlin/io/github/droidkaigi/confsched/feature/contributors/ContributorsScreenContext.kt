package io.github.droidkaigi.confsched.feature.contributors

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.KaigiLogger
import io.github.droidkaigi.confsched.core.common.PresenterContext
import io.github.droidkaigi.confsched.core.common.ScreenContext
import io.github.droidkaigi.confsched.core.model.ContributorsQueryKey
import io.github.droidkaigi.confsched.core.model.ContributorsScreenScope

@Inject
class ContributorsPresenterContext(override val logger: KaigiLogger) : PresenterContext

@Inject
@SingleIn(ContributorsScreenScope::class)
class ContributorsScreenContext(
    val contributorsQueryKey: ContributorsQueryKey,
    override val logger: KaigiLogger,
    val presenterContext: ContributorsPresenterContext,
) : ScreenContext
