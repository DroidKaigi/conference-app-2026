package io.github.droidkaigi.confsched.feature.sessions.timetable

import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.sessionUrl

/**
 * A web address rather than a platform calendar API, so every target adds an event the same way
 * and none of them has to ask for calendar permission.
 */
internal fun calendarUrl(item: TimetableItem): String {
    val date = item.day.date.toString().replace("-", "")
    val dates = "${date}T${item.startsAt.toCalendarTime()}/${date}T${item.endsAt.toCalendarTime()}"
    val parameters = listOf(
        "action" to "TEMPLATE",
        "text" to item.title.en,
        "dates" to dates,
        "ctz" to CONFERENCE_TIME_ZONE_ID,
        "location" to item.room.name,
        "details" to sessionUrl(item.id),
    )
    return parameters.joinToString(separator = "&", prefix = "$CALENDAR_TEMPLATE_URL?") { (name, value) ->
        "$name=${value.encodeUrlComponent()}"
    }
}

private fun String.toCalendarTime(): String = replace(":", "") + "00"

private fun String.encodeUrlComponent(): String = buildString {
    for (byte in this@encodeUrlComponent.encodeToByteArray()) {
        val code = byte.toInt() and 0xFF
        val character = code.toChar()
        if (character in UNRESERVED) {
            append(character)
        } else {
            append('%')
            append(HEX_DIGITS[code shr 4])
            append(HEX_DIGITS[code and 0x0F])
        }
    }
}

private const val CALENDAR_TEMPLATE_URL = "https://calendar.google.com/calendar/render"
private const val CONFERENCE_TIME_ZONE_ID = "Asia/Tokyo"
private const val HEX_DIGITS = "0123456789ABCDEF"
private val UNRESERVED = ('A'..'Z') + ('a'..'z') + ('0'..'9') + listOf('-', '.', '_', '~')
