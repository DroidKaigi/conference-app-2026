package io.github.droidkaigi.confsched.feature.debug

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.DefaultScreenNavigator
import io.github.droidkaigi.confsched.core.model.DebugScreenScope

@Inject
@ContributesBinding(
    scope = DebugScreenScope::class,
    binding = binding<DebugScreenNavigator>(),
)
class DefaultDebugScreenNavigator(
    private val appNavigator: AppNavigator
) : DefaultScreenNavigator(appNavigator), DebugScreenNavigator {
    override fun openSoilErrors() {
        appNavigator.goTo(SoilErrorsNavKey)
    }
}
