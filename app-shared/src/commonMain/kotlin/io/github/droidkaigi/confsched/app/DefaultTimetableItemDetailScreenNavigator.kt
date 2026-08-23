package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.model.TimetableItemDetailScreenScope
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableItemDetailNavKey
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableItemDetailScreenNavigator

@Inject
@SingleIn(TimetableItemDetailScreenScope::class)
@ContributesBinding(TimetableItemDetailScreenScope::class)
class DefaultTimetableItemDetailScreenNavigator(private val appNavigator: AppNavigator) : TimetableItemDetailScreenNavigator {
    override fun openSessionDetail(id: TimetableItemId) {
        appNavigator.goTo(TimetableItemDetailNavKey(id))
    }
}
