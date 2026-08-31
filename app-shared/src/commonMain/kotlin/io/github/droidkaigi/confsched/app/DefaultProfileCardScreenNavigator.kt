package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.DefaultScreenNavigator
import io.github.droidkaigi.confsched.core.model.DoodleTarget
import io.github.droidkaigi.confsched.core.model.ProfileCardScreenScope
import io.github.droidkaigi.confsched.feature.doodle.DoodleNavKey
import io.github.droidkaigi.confsched.feature.profilecard.ProfileCardScreenNavigator

@Inject
@SingleIn(ProfileCardScreenScope::class)
@ContributesBinding(
    scope = ProfileCardScreenScope::class,
    binding = binding<ProfileCardScreenNavigator>(),
)
class DefaultProfileCardScreenNavigator(
    private val appNavigator: AppNavigator,
) : DefaultScreenNavigator(appNavigator),
    ProfileCardScreenNavigator {
    override fun openDoodle(target: DoodleTarget) {
        appNavigator.goTo(DoodleNavKey(target))
    }
}
