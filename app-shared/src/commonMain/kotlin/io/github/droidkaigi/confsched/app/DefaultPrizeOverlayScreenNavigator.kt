package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.DefaultScreenNavigator
import io.github.droidkaigi.confsched.core.model.PrizeOverlayScreenScope
import io.github.droidkaigi.confsched.feature.eventmap.PrizeOverlayScreenNavigator

@Inject
@SingleIn(PrizeOverlayScreenScope::class)
@ContributesBinding(
    scope = PrizeOverlayScreenScope::class,
    binding = binding<PrizeOverlayScreenNavigator>(),
)
class DefaultPrizeOverlayScreenNavigator(
    appNavigator: AppNavigator,
) : DefaultScreenNavigator(appNavigator),
    PrizeOverlayScreenNavigator
