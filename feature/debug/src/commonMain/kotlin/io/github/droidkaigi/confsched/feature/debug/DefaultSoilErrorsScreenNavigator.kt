package io.github.droidkaigi.confsched.feature.debug

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.DefaultScreenNavigator
import io.github.droidkaigi.confsched.core.model.LicensesScreenScope
import io.github.droidkaigi.confsched.core.model.SoilErrorsScreenScope

@Inject
@SingleIn(SoilErrorsScreenScope::class)
@ContributesBinding(SoilErrorsScreenScope::class)
class DefaultSoilErrorsScreenNavigator(
    appNavigator: AppNavigator
) : DefaultScreenNavigator(appNavigator), SoilErrorsScreenNavigator
