package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.DefaultScreenNavigator
import io.github.droidkaigi.confsched.core.model.LicensesScreenScope
import io.github.droidkaigi.confsched.feature.about.LicensesScreenNavigator

@Inject
@SingleIn(LicensesScreenScope::class)
@ContributesBinding(
    scope = LicensesScreenScope::class,
    binding = binding<LicensesScreenNavigator>(),
)
class DefaultLicensesScreenNavigator(
    appNavigator: AppNavigator,
) : DefaultScreenNavigator(appNavigator),
    LicensesScreenNavigator
