package io.github.droidkaigi.confsched.feature.sessions.timetable

import androidx.navigation3.runtime.NavKey
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import kotlinx.serialization.Serializable

@Serializable
data object TimetableNavKey : NavKey

@Serializable
data class TimetableItemDetailNavKey(val id: TimetableItemId) : NavKey
