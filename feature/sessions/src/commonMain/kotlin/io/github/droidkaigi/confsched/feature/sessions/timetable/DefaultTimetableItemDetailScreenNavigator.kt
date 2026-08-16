package io.github.droidkaigi.confsched.feature.sessions.timetable

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.DefaultScreenNavigator
import io.github.droidkaigi.confsched.core.model.TimetableItemDetailScreenScope

@Inject
@SingleIn(TimetableItemDetailScreenScope::class)
@ContributesBinding(TimetableItemDetailScreenScope::class)
class DefaultTimetableItemDetailScreenNavigator(
    appNavigator: AppNavigator
) : DefaultScreenNavigator(appNavigator), TimetableItemDetailScreenNavigator
