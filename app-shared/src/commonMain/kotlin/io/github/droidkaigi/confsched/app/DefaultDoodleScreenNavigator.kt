package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.model.DoodleScreenScope
import io.github.droidkaigi.confsched.feature.about.DoodleScreenNavigator

@Inject
@SingleIn(DoodleScreenScope::class)
@ContributesBinding(DoodleScreenScope::class)
class DefaultDoodleScreenNavigator(
    @Suppress("unused") private val appNavigator: AppNavigator,
) : DoodleScreenNavigator
