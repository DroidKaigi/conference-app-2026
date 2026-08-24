package io.github.droidkaigi.confsched.feature.favorites

import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.TimetableItemId

sealed interface FavoritesScreenAction {
    data class Bookmark(val id: TimetableItemId) : FavoritesScreenAction

    data class SelectDayFilter(val day: DroidKaigi2026Day?) : FavoritesScreenAction
}
