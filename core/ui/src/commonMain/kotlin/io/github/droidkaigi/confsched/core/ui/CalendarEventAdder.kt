package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable
import kotlin.time.Instant

data class CalendarEvent(
    val title: String,
    val startsAt: Instant,
    val endsAt: Instant,
    val location: String,
    val url: String,
)

/**
 * Hands an event to whatever the platform offers for adding it to a calendar: the system event
 * editor where there is one, and an iCalendar file opened by the default handler on the rest.
 */
@Composable
expect fun rememberCalendarEventAdder(): (CalendarEvent) -> Unit
