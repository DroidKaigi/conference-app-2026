package io.github.droidkaigi.confsched.feature.search

import io.github.droidkaigi.confsched.core.model.TimetableItemId

sealed interface SearchScreenAction {
    data class Bookmark(val id: TimetableItemId) : SearchScreenAction
    data class ChangeQueryText(val text: String) : SearchScreenAction
}
