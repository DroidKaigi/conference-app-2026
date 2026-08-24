package io.github.droidkaigi.confsched.core.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.asTimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/** Japan does not observe daylight saving, so a fixed offset states the conference's wall clock exactly. */
val ConferenceTimeZone: TimeZone = UtcOffset(hours = 9).asTimeZone()

/** The days the conference runs. */
@Serializable
enum class DroidKaigi2026Day(val date: LocalDate) {
    Day1(LocalDate(2026, 9, 2)),
    Day2(LocalDate(2026, 9, 3)),
    ;

    val label: String get() = "${date.month.number}/${date.day}"

    /** The instant this day reaches [hour]:[minute] at the conference. */
    fun at(hour: Int, minute: Int): Instant = date.atTime(hour, minute).toInstant(ConferenceTimeZone)

    companion object {
        fun ofOrNull(time: Instant): DroidKaigi2026Day? {
            val date = time.toLocalDateTime(ConferenceTimeZone).date
            return entries.find { it.date == date }
        }
    }
}
