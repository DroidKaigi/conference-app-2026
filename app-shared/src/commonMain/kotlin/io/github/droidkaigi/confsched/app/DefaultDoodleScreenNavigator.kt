package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.DefaultScreenNavigator
import io.github.droidkaigi.confsched.core.model.DoodleScreenScope
import io.github.droidkaigi.confsched.feature.doodle.DoodleScreenNavigator

@Inject
@SingleIn(DoodleScreenScope::class)
@ContributesBinding(
    scope = DoodleScreenScope::class,
    binding = binding<DoodleScreenNavigator>(),
)
class DefaultDoodleScreenNavigator(
    appNavigator: AppNavigator,
) : DefaultScreenNavigator(appNavigator),
    DoodleScreenNavigator
