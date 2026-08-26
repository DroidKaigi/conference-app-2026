package io.github.droidkaigi.confsched.feature.profilecard

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.PresenterContext
import io.github.droidkaigi.confsched.core.common.ScreenContext
import io.github.droidkaigi.confsched.core.model.ProfileCardScreenScope

@Inject
class ProfileCardPresenterContext : PresenterContext

@Inject
@SingleIn(ProfileCardScreenScope::class)
class ProfileCardScreenContext(
    val presenterContext: ProfileCardPresenterContext,
) : ScreenContext
