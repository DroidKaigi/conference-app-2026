package io.github.droidkaigi.confsched.core.ui

import io.github.droidkaigi.confsched.core.model.TimetableItem
import kotlin.time.Instant

/**
 * Represents the visual progress state of a timeline rule in the timetable.
 */
sealed interface TimetableLineState {
    data object Upcoming : TimetableLineState
    data object Passed : TimetableLineState
    data class InProgress(val progress: Float) : TimetableLineState
}

fun calculateTimetableLineState(
    currentTime: Instant,
    startsAt: Instant,
    endsAt: Instant,
): TimetableLineState {
    return when {
        currentTime < startsAt -> TimetableLineState.Upcoming

        currentTime >= endsAt -> TimetableLineState.Passed

        else -> {
            val totalDuration = (endsAt - startsAt).inWholeMilliseconds.toFloat()
            val elapsedDuration = (currentTime - startsAt).inWholeMilliseconds.toFloat()
            val progress = if (totalDuration > 0f) {
                (elapsedDuration / totalDuration).coerceIn(0f, 1f)
            } else {
                1f
            }
            TimetableLineState.InProgress(progress)
        }
    }
}

fun TimetableItem.lineState(currentTime: Instant): TimetableLineState {
    return calculateTimetableLineState(currentTime, startsAtInstant, endsAtInstant)
}
