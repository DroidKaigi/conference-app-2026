package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.DefaultScreenNavigator
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.model.TimetableScreenScope
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableItemDetailNavKey
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableScreenNavigator

@Inject
@SingleIn(TimetableScreenScope::class)
@ContributesBinding(TimetableScreenScope::class)
class DefaultTimetableScreenNavigator(
    private val appNavigator: AppNavigator
) : DefaultScreenNavigator(appNavigator), TimetableScreenNavigator {
    override fun openSessionDetail(id: TimetableItemId) {
        appNavigator.goTo(TimetableItemDetailNavKey(id))
    }
}
