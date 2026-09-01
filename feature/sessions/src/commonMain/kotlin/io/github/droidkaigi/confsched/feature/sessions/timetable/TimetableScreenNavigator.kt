package io.github.droidkaigi.confsched.feature.sessions.timetable

import io.github.droidkaigi.confsched.core.common.Navigator
import io.github.droidkaigi.confsched.core.model.SessionRoom
import io.github.droidkaigi.confsched.core.model.TimetableItemId

interface TimetableScreenNavigator : Navigator {
    fun openSessionDetail(id: TimetableItemId)

    fun openSearch()

    fun openFirstFavoriteGuidance(room: SessionRoom)
}
