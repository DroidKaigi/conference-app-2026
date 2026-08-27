package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.Desktop
import java.io.File

/** The desktop has no event editor of its own, so the event goes to whichever application handles iCalendar files. */
@Composable
actual fun rememberCalendarEventAdder(): (CalendarEvent) -> Unit {
    val coroutineScope = rememberCoroutineScope()
    return remember(coroutineScope) { coroutineScope::openCalendarFile }
}

private fun CoroutineScope.openCalendarFile(event: CalendarEvent) {
    if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) return
    launch(Dispatchers.IO) {
        val file = File(System.getProperty("java.io.tmpdir"), CALENDAR_EVENT_FILE_NAME)
        file.writeText(event.toICalendar())
        Desktop.getDesktop().open(file)
    }
}
