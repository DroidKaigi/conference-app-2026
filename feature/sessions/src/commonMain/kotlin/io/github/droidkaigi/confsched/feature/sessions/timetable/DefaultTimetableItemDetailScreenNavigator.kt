package io.github.droidkaigi.confsched.feature.sessions.timetable

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.DefaultScreenNavigator
import io.github.droidkaigi.confsched.core.model.TimetableItemDetailScreenScope

@Inject
@ContributesBinding(
    scope = TimetableItemDetailScreenScope::class,
    binding = binding<TimetableItemDetailScreenNavigator>(),
)
class DefaultTimetableItemDetailScreenNavigator(
    appNavigator: AppNavigator
) : DefaultScreenNavigator(appNavigator), TimetableItemDetailScreenNavigator
