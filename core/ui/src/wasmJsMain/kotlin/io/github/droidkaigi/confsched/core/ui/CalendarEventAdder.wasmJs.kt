package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberCalendarEventAdder(): (CalendarEvent) -> Unit = remember {
    { event -> downloadCalendarFile(CALENDAR_EVENT_FILE_NAME, event.toICalendar()) }
}

/** A browser has no calendar of its own, so the event is downloaded for whichever application handles iCalendar files. */
private fun downloadCalendarFile(name: String, content: String): Unit = js(
    """{
        const url = URL.createObjectURL(new Blob([content], { type: 'text/calendar' }));
        const link = document.createElement('a');
        link.href = url;
        link.download = name;
        link.click();
        // The browser reads the URL after click() returns, so revoking it synchronously can void the download.
        setTimeout(() => URL.revokeObjectURL(url), 0);
    }""",
)
