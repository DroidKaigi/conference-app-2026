package io.github.droidkaigi.confsched.feature.search.component

import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.Language
import io.github.droidkaigi.confsched.core.model.SessionCategory
import io.github.droidkaigi.confsched.core.model.SessionType
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet

data class SearchFilterRowUiState(
    val selectedDay: DroidKaigi2026Day?,
    val categories: PersistentList<SessionCategory>,
    val selectedCategoryIds: PersistentSet<Long>,
    val sessionTypes: PersistentList<SessionType>,
    val selectedSessionTypes: PersistentSet<SessionType>,
    val selectedLanguages: PersistentSet<Language>,
)
