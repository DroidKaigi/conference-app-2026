package io.github.droidkaigi.confsched.feature.search

import io.github.droidkaigi.confsched.core.common.Navigator
import io.github.droidkaigi.confsched.core.model.SessionRoom
import io.github.droidkaigi.confsched.core.model.TimetableItemId

interface SearchScreenNavigator : Navigator {
    fun openSessionDetail(id: TimetableItemId)

    fun offerFirstFavoriteGuidance(room: SessionRoom)
}
