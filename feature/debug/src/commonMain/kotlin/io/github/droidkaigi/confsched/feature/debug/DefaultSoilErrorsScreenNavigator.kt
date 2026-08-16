package io.github.droidkaigi.confsched.feature.debug

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.DefaultScreenNavigator
import io.github.droidkaigi.confsched.core.model.LicensesScreenScope
import io.github.droidkaigi.confsched.core.model.SoilErrorsScreenScope

@Inject
@SingleIn(SoilErrorsScreenScope::class)
@ContributesBinding(scope = SoilErrorsScreenScope::class,
    binding = binding<SoilErrorsScreenNavigator>()
)
class DefaultSoilErrorsScreenNavigator(
    appNavigator: AppNavigator
) : DefaultScreenNavigator(appNavigator), SoilErrorsScreenNavigator
