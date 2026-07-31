package io.github.droidkaigi.confsched.feature.sessions.timetable

import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet

data class TimetableScreenUiState(
    val day: DroidKaigi2026Day,
    val sessions: PersistentList<TimetableItem>,
    val bookmarks: PersistentSet<TimetableItemId>,
    val rawResponse: String,
    val isRawResponseExpanded: Boolean,
)
