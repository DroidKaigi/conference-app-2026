package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.DefaultScreenNavigator
import io.github.droidkaigi.confsched.core.model.EventMapScreenScope
import io.github.droidkaigi.confsched.feature.eventmap.EventMapScreenNavigator

@Inject
@SingleIn(EventMapScreenScope::class)
@ContributesBinding(EventMapScreenScope::class)
class DefaultEventMapScreenNavigator(
    appNavigator: AppNavigator,
) : DefaultScreenNavigator(appNavigator), EventMapScreenNavigator
