package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.DefaultScreenNavigator
import io.github.droidkaigi.confsched.core.model.SessionRoom
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.model.TimetableScreenScope
import io.github.droidkaigi.confsched.core.model.mascot
import io.github.droidkaigi.confsched.feature.favorites.FirstFavoriteNotificationNavKey
import io.github.droidkaigi.confsched.feature.search.SearchNavKey
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableItemDetailNavKey
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableScreenNavigator

@Inject
@SingleIn(TimetableScreenScope::class)
@ContributesBinding(
    scope = TimetableScreenScope::class,
    binding = binding<TimetableScreenNavigator>(),
)
class DefaultTimetableScreenNavigator(
    private val appNavigator: AppNavigator,
) : DefaultScreenNavigator(appNavigator),
    TimetableScreenNavigator {
    override fun openSessionDetail(id: TimetableItemId) {
        appNavigator.goTo(TimetableItemDetailNavKey(id))
    }

    override fun openSearch() {
        appNavigator.goTo(SearchNavKey)
    }

    override fun openFirstFavoriteGuidance(room: SessionRoom) {
        appNavigator.goTo(FirstFavoriteNotificationNavKey(room.mascot))
    }
}
