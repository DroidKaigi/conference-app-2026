package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.DefaultScreenNavigator
import io.github.droidkaigi.confsched.core.model.SponsorsScreenScope
import io.github.droidkaigi.confsched.feature.sponsors.SponsorsScreenNavigator

@Inject
@SingleIn(SponsorsScreenScope::class)
@ContributesBinding(
    scope = SponsorsScreenScope::class,
    binding = binding<SponsorsScreenNavigator>(),
)
class DefaultSponsorsScreenNavigator(
    appNavigator: AppNavigator,
) : DefaultScreenNavigator(appNavigator),
    SponsorsScreenNavigator
