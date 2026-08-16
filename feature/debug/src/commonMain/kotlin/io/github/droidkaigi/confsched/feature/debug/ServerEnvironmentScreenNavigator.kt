package io.github.droidkaigi.confsched.feature.debug

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.DefaultScreenNavigator
import io.github.droidkaigi.confsched.core.common.Navigator
import io.github.droidkaigi.confsched.core.common.UiScope
import io.github.droidkaigi.confsched.core.model.ServerEnvironmentScreenScope
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableNavKey

interface ServerEnvironmentScreenNavigator : Navigator {
    fun openTimetable()
}

// Lives in-feature, unlike other Default*Navigators: feature:debug is dev-only tooling and is the
// one module exempt from cross-feature isolation, so it may reference TimetableNavKey directly.
@Inject
@ContributesBinding(ServerEnvironmentScreenScope::class)
class DefaultServerEnvironmentScreenNavigator(
    private val appNavigator: AppNavigator,
) : DefaultScreenNavigator(appNavigator), ServerEnvironmentScreenNavigator {
    override fun openTimetable() {
        appNavigator.goTo(TimetableNavKey)
    }
}
