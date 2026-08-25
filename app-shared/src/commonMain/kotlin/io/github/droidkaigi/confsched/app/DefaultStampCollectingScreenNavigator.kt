package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.DefaultScreenNavigator
import io.github.droidkaigi.confsched.core.model.StampCollectingScreenScope
import io.github.droidkaigi.confsched.feature.eventmap.StampCollectingScreenNavigator

@Inject
@SingleIn(StampCollectingScreenScope::class)
@ContributesBinding(
    scope = StampCollectingScreenScope::class,
    binding = binding<StampCollectingScreenNavigator>(),
)
class DefaultStampCollectingScreenNavigator(
    appNavigator: AppNavigator,
) : DefaultScreenNavigator(appNavigator),
    StampCollectingScreenNavigator
