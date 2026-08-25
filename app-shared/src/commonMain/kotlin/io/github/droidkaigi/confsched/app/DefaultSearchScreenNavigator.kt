package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.model.SearchScreenScope
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.feature.search.SearchScreenNavigator
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableItemDetailNavKey

@Inject
@SingleIn(SearchScreenScope::class)
@ContributesBinding(SearchScreenScope::class)
class DefaultSearchScreenNavigator(private val appNavigator: AppNavigator) : SearchScreenNavigator {
    override fun openSessionDetail(id: TimetableItemId) {
        appNavigator.goTo(TimetableItemDetailNavKey(id))
    }
}
