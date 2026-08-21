package io.github.droidkaigi.confsched.feature.search

import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.SessionType
import io.github.droidkaigi.confsched.core.model.TimetableItemId

sealed interface SearchScreenAction {
    data class Bookmark(val id: TimetableItemId) : SearchScreenAction
    data class ChangeQueryText(val text: String) : SearchScreenAction
    data class SelectDay(val day: DroidKaigi2026Day?) : SearchScreenAction
    data class ToggleCategory(val id: Long) : SearchScreenAction
    data class ToggleSessionType(val sessionType: SessionType) : SearchScreenAction
    data class ToggleLanguage(val language: Language) : SearchScreenAction
    data object ClearFilters : SearchScreenAction
}
