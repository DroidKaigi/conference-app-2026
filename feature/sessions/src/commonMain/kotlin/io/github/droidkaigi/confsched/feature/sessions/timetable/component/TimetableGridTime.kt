package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal val TimetableGridHeaderHeight = 34.dp
internal val TimetableGridSessionGap = 4.dp
internal val TimetableGridSessionMinHeight = 40.dp
internal val TimetableGridDefaultHourHeight = 132.dp
internal val TimetableGridMaxHourHeight = 220.dp
internal val TimetableGridVerticalPadding = 12.dp
internal val TimetableGridTimeGutterWidth = 52.dp
internal val TimetableGridRoomColumnWidth = 156.dp
internal val TimetableGridRoomColumnGap = 8.dp

internal fun timetableGridContentWidth(roomCount: Int, columnWidth: Dp): Dp =
    columnWidth * roomCount + TimetableGridRoomColumnGap * (roomCount - 1)

/** Columns keep their design width and stretch evenly once the viewport is wider than the grid. */
internal fun timetableGridColumnWidth(availableWidth: Dp, roomCount: Int): Dp {
    val stretched = (availableWidth - TimetableGridRoomColumnGap * (roomCount - 1)) / roomCount
    return stretched.coerceAtLeast(TimetableGridRoomColumnWidth)
}

internal val TimetableGridDayStartMinutes = 10 * 60
internal val TimetableGridDefaultDayEndMinutes = 18 * 60

internal fun String.toTimetableGridMinuteOfDay(): Int {
    val hour = substringBefore(":").toInt()
    val minute = substringAfter(":").toInt()
    return hour * 60 + minute
}

internal fun timetableGridMinuteOffsetY(
    minute: Int,
    hourHeight: Dp,
): Dp {
    val startOffsetMinutes = minute - TimetableGridDayStartMinutes
    return TimetableGridHeaderHeight + (hourHeight.value * startOffsetMinutes / 60f).dp
}

/**
 * Where a session's block starts.
 *
 * The gap that keeps neighbouring sessions apart sits above the block, so its lower edge
 * lands on the hour the session ends and its upper edge clears the rule above.
 */
internal fun timetableGridSessionOffsetY(
    startsAt: String,
    hourHeight: Dp = TimetableGridDefaultHourHeight,
): Dp {
    return timetableGridMinuteOffsetY(
        minute = startsAt.toTimetableGridMinuteOfDay(),
        hourHeight = hourHeight,
    ) + TimetableGridSessionGap
}

internal fun timetableGridSessionHeight(
    startsAt: String,
    endsAt: String,
    hourHeight: Dp = TimetableGridDefaultHourHeight,
): Dp {
    val durationMinutes = endsAt.toTimetableGridMinuteOfDay() - startsAt.toTimetableGridMinuteOfDay()
    return ((hourHeight.value * durationMinutes / 60f).dp - TimetableGridSessionGap)
        .coerceAtLeast(TimetableGridSessionMinHeight)
}

/**
 * How much of a block the design fills, by how long the session runs.
 *
 * Keyed to duration, not to the height the block is drawn at: the design shows and hides
 * nothing by height, so pinching resizes a block without changing what it lays out.
 */
internal enum class TimetableGridBlockBucket {
    Short,
    Medium,
    Tall,
    ;

    val titleMaxLines: Int
        get() = when (this) {
            Short -> 1
            Medium -> 2
            Tall -> 3
        }

    /** The design buys the shortest block its second row out of the space above the title. */
    val topPadding: Dp
        get() = when (this) {
            Short -> 4.dp
            Medium, Tall -> 6.dp
        }

    /** What sits between the title and the time below it, which the shortest block cannot spare. */
    val titleSpacing: Dp
        get() = when (this) {
            Short -> 0.dp
            Medium, Tall -> 4.dp
        }
}

internal fun timetableGridBlockBucket(startsAt: String, endsAt: String): TimetableGridBlockBucket {
    val durationMinutes = endsAt.toTimetableGridMinuteOfDay() - startsAt.toTimetableGridMinuteOfDay()
    return when {
        durationMinutes >= 60 -> TimetableGridBlockBucket.Tall
        durationMinutes >= 40 -> TimetableGridBlockBucket.Medium
        else -> TimetableGridBlockBucket.Short
    }
}

internal fun timetableGridContentHeight(
    endMinute: Int,
    hourHeight: Dp = TimetableGridDefaultHourHeight,
): Dp {
    val durationMinutes = endMinute - TimetableGridDayStartMinutes
    return TimetableGridHeaderHeight + (hourHeight.value * durationMinutes / 60f).dp
}

internal fun Int.toTimetableGridTimeLabel(): String {
    val hour = this / 60
    val minute = this % 60
    return "$hour:${minute.toString().padStart(2, '0')}"
}
