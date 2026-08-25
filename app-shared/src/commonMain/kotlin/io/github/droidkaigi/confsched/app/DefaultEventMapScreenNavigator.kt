package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.DefaultScreenNavigator
import io.github.droidkaigi.confsched.core.model.EventMapScreenScope
import io.github.droidkaigi.confsched.feature.eventmap.EventMapScreenNavigator
import io.github.droidkaigi.confsched.feature.eventmap.StampCollectingNavKey

@Inject
@SingleIn(EventMapScreenScope::class)
@ContributesBinding(
    scope = EventMapScreenScope::class,
    binding = binding<EventMapScreenNavigator>(),
)
class DefaultEventMapScreenNavigator(
    private val appNavigator: AppNavigator,
) : DefaultScreenNavigator(appNavigator),
    EventMapScreenNavigator {
    override fun openStampCollecting() {
        appNavigator.goTo(StampCollectingNavKey)
    }
}
