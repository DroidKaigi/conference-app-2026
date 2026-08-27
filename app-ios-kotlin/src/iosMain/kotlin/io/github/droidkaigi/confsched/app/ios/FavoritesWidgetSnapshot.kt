package io.github.droidkaigi.confsched.app.ios

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import io.github.droidkaigi.confsched.core.designsystem.RoomShape
import io.github.droidkaigi.confsched.core.designsystem.isDark
import io.github.droidkaigi.confsched.core.designsystem.roomTheme
import io.github.droidkaigi.confsched.core.designsystem.toMaterialColorScheme
import io.github.droidkaigi.confsched.core.model.ConferenceTimeZone
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.SessionRoom
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.model.endInstant
import io.github.droidkaigi.confsched.core.model.startInstant
import kotlinx.datetime.number
import kotlinx.datetime.offsetAt
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

/**
 * What the widget extension is handed instead of the app's own stores, so it renders without
 * linking the shared Kotlin code.
 */
@Serializable
internal data class FavoritesWidgetSnapshot(
    val schemaVersion: Int,
    val clockOffsetSeconds: Long,
    val conference: FavoritesWidgetConference,
    val colors: FavoritesWidgetSnapshotColors,
    val favorites: List<FavoritesWidgetSnapshotSession>,
)

@Serializable
internal data class FavoritesWidgetConference(
    val startEpochSeconds: Long,
    val endEpochSeconds: Long,
    val utcOffsetSeconds: Int,
    val day1Month: Int,
    val day1Day: Int,
    val day2Month: Int,
    val day2Day: Int,
    val day1LastSessionEndEpochSeconds: Long?,
    val day2LastSessionEndEpochSeconds: Long?,
)

@Serializable
internal data class FavoritesWidgetSnapshotColors(
    val surface: String,
    val onSurface: String,
    val onSurfaceVariant: String,
    val primary: String,
    val onPrimary: String,
    val isDark: Boolean,
)

@Serializable
internal data class FavoritesWidgetSnapshotSession(
    val id: String,
    val day: Int,
    val titleEn: String,
    val titleJa: String,
    val startsAt: String,
    val endsAt: String,
    val startEpochSeconds: Long,
    val endEpochSeconds: Long,
    val roomLabel: String,
    val roomContainer: String,
    val roomOnContainer: String,
)

internal fun favoritesWidgetSnapshot(
    schemaVersion: Int,
    timetable: Timetable,
    favoriteIds: Set<TimetableItemId>,
    colorScheme: KaigiColorScheme,
    clockOffset: Duration,
): FavoritesWidgetSnapshot {
    val isDark = colorScheme.isDark
    return FavoritesWidgetSnapshot(
        schemaVersion = schemaVersion,
        clockOffsetSeconds = clockOffset.inWholeSeconds,
        conference = conferenceWindow(timetable),
        colors = colorScheme.toSnapshotColors(),
        favorites = timetable.items
            .filter { it.id in favoriteIds }
            .sortedBy(TimetableItem::startInstant)
            .map { it.toSnapshotSession(isDark) },
    )
}

private fun conferenceWindow(timetable: Timetable): FavoritesWidgetConference {
    val start = DroidKaigi2026Day.Day1.at(0, 0)
    return FavoritesWidgetConference(
        startEpochSeconds = start.epochSeconds,
        endEpochSeconds = (DroidKaigi2026Day.Day2.at(0, 0) + 24.hours).epochSeconds,
        utcOffsetSeconds = ConferenceTimeZone.offsetAt(start).totalSeconds,
        day1Month = DroidKaigi2026Day.Day1.date.month.number,
        day1Day = DroidKaigi2026Day.Day1.date.day,
        day2Month = DroidKaigi2026Day.Day2.date.month.number,
        day2Day = DroidKaigi2026Day.Day2.date.day,
        day1LastSessionEndEpochSeconds = timetable.lastSessionEndEpochSeconds(DroidKaigi2026Day.Day1),
        day2LastSessionEndEpochSeconds = timetable.lastSessionEndEpochSeconds(DroidKaigi2026Day.Day2),
    )
}

private fun Timetable.lastSessionEndEpochSeconds(day: DroidKaigi2026Day): Long? = items
    .filter { it.day == day }
    .maxOfOrNull { it.endInstant.epochSeconds }

private fun KaigiColorScheme.toSnapshotColors(): FavoritesWidgetSnapshotColors {
    val scheme = toMaterialColorScheme()
    return FavoritesWidgetSnapshotColors(
        surface = scheme.surfaceContainerLow.toHex(),
        onSurface = scheme.onSurface.toHex(),
        onSurfaceVariant = scheme.onSurfaceVariant.toHex(),
        primary = scheme.primary.toHex(),
        onPrimary = scheme.onPrimary.toHex(),
        isDark = isDark,
    )
}

private fun TimetableItem.toSnapshotSession(isDark: Boolean): FavoritesWidgetSnapshotSession {
    val theme = roomTheme(room, isDark)
    return FavoritesWidgetSnapshotSession(
        id = id.value,
        day = day.number,
        titleEn = title.en,
        titleJa = title.ja,
        startsAt = startsAt,
        endsAt = endsAt,
        startEpochSeconds = startInstant.epochSeconds,
        endEpochSeconds = endInstant.epochSeconds,
        roomLabel = roomLabel(room, theme.shape),
        roomContainer = theme.container.toHex(),
        roomOnContainer = theme.onContainer.toHex(),
    )
}

private val DroidKaigi2026Day.number: Int
    get() = when (this) {
        DroidKaigi2026Day.Day1 -> 1
        DroidKaigi2026Day.Day2 -> 2
    }

private fun roomLabel(room: SessionRoom, shape: RoomShape?): String {
    val mark = when (shape) {
        RoomShape.Circle -> "○"
        RoomShape.Star -> "✦"
        RoomShape.Square -> "□"
        RoomShape.Triangle -> "△"
        RoomShape.Diamond -> "◇"
        null -> null
    }
    return if (mark == null) room.name else "$mark ${room.name}"
}

private fun Color.toHex(): String = "#" + toArgb().toUInt().toString(16).padStart(8, '0')
