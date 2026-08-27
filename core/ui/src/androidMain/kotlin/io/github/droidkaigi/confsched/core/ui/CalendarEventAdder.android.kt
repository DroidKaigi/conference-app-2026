package io.github.droidkaigi.confsched.core.ui

import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberCalendarEventAdder(): (CalendarEvent) -> Unit {
    val context = LocalContext.current
    return remember(context) {
        { event ->
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, event.title)
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.startsAt.toEpochMilliseconds())
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.endsAt.toEpochMilliseconds())
                putExtra(CalendarContract.Events.EVENT_LOCATION, event.location)
                putExtra(CalendarContract.Events.DESCRIPTION, event.url)
            }
            context.startActivity(intent)
        }
    }
}
