package io.github.droidkaigi.confsched.feature.sessions.timetable

import io.github.droidkaigi.confsched.core.model.TimetableItemId

sealed interface TimetableItemDetailScreenAction {
    data class Bookmark(val id: TimetableItemId) : TimetableItemDetailScreenAction

    data class SaveMemo(val text: String) : TimetableItemDetailScreenAction

    data object ToggleDescriptionExpansion : TimetableItemDetailScreenAction

    data object ToggleDisplayLanguage : TimetableItemDetailScreenAction
}
