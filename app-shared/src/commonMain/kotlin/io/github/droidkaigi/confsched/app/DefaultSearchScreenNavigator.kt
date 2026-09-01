package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.DefaultScreenNavigator
import io.github.droidkaigi.confsched.core.model.SearchScreenScope
import io.github.droidkaigi.confsched.core.model.SessionRoom
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.feature.search.SearchScreenNavigator
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableItemDetailNavKey

@Inject
@SingleIn(SearchScreenScope::class)
@ContributesBinding(
    scope = SearchScreenScope::class,
    binding = binding<SearchScreenNavigator>(),
)
class DefaultSearchScreenNavigator(
    private val appNavigator: AppNavigator,
    private val firstFavoriteGuidance: FirstFavoriteGuidance,
) : DefaultScreenNavigator(appNavigator = appNavigator),
    SearchScreenNavigator {
    override fun openSessionDetail(id: TimetableItemId) {
        appNavigator.goTo(TimetableItemDetailNavKey(id))
    }

    override suspend fun offerFirstFavoriteGuidance(room: SessionRoom) {
        firstFavoriteGuidance.offer(room)
    }
}
