package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.DefaultScreenNavigator
import io.github.droidkaigi.confsched.core.model.ProfileCardScreenScope
import io.github.droidkaigi.confsched.feature.profilecard.ProfileCardScreenNavigator

@Inject
@SingleIn(ProfileCardScreenScope::class)
@ContributesBinding(ProfileCardScreenScope::class)
class DefaultProfileCardScreenNavigator(
    appNavigator: AppNavigator,
) : DefaultScreenNavigator(appNavigator), ProfileCardScreenNavigator
