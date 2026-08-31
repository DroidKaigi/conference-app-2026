package io.github.droidkaigi.confsched.app.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.unit.ColorProvider
import io.github.droidkaigi.confsched.R
import io.github.droidkaigi.confsched.app.favoriteSessionDeepLinkIntent
import io.github.droidkaigi.confsched.app.localized
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.FavoritesWidgetRow
import io.github.droidkaigi.confsched.core.model.FavoritesWidgetSlot
import io.github.droidkaigi.confsched.core.model.FavoritesWidgetState
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.computeFavoritesWidgetState
import io.github.droidkaigi.confsched.core.model.toFavoritesWidgetRows
import io.github.droidkaigi.confsched.core.preview.fake
import androidx.glance.appwidget.action.actionStartActivity as actionStartActivityIntent

private const val MAX_MEDIUM_ROWS = 3

@Composable
internal fun ScheduleContent(state: FavoritesWidgetState.Schedule, colors: FavoritesWidgetColors) {
    if (isMedium(LocalSize.current)) {
        ScheduleMediumContent(state, colors)
    } else {
        ScheduleSmallContent(state, colors)
    }
}

@Composable
private fun ScheduleSmallContent(state: FavoritesWidgetState.Schedule, colors: FavoritesWidgetColors) {
    val context = LocalContext.current
    val slot = state.slots.first()
    val label = if (slot.isLive) R.string.widget_live_small_label else R.string.widget_next_label
    Column(modifier = GlanceModifier.fillMaxSize()) {
        HeaderRow(context.getString(label), live = slot.isLive, colors = colors)
        Spacer(modifier = GlanceModifier.height(GapBase))
        if (slot.isLive) {
            SmallLiveBody(slot, colors)
        } else {
            SmallNextBody(slot, colors)
        }
    }
}

@Composable
private fun SmallNextBody(slot: FavoritesWidgetSlot, colors: FavoritesWidgetColors) {
    val context = LocalContext.current
    val session = slot.sessions.first()
    Column {
        Text(
            text = slot.startsAt,
            style = monoStyle(colors.primary, 14.sp, FontWeight.Bold),
        )
        Spacer(modifier = GlanceModifier.height(GapTight))
        Text(
            text = context.localized(session.title),
            style = sansStyle(colors.onSurface, 12.sp),
            maxLines = 2,
        )
        Spacer(modifier = GlanceModifier.height(GapBase))
        SmallChipRow(slot, colors, onBand = false)
    }
}

@Composable
private fun SmallLiveBody(slot: FavoritesWidgetSlot, colors: FavoritesWidgetColors) {
    val context = LocalContext.current
    val session = slot.sessions.first()
    val bandModifier = GlanceModifier.fillMaxWidth()
        .background(ColorProvider(colors.primary))
        .cornerRadius(8.dp)
        .padding(InsetRow)
    Box(
        // A shared slot leaves the session choice open, so only a lone live session deep-links.
        modifier = if (slot.sessions.size == 1) {
            bandModifier.clickable(actionStartActivityIntent(favoriteSessionDeepLinkIntent(context, session.id)))
        } else {
            bandModifier
        },
    ) {
        Column {
            Text(
                text = "${slot.startsAt} – ${slot.endsAt}",
                style = monoStyle(colors.onPrimary, 12.sp, FontWeight.Bold),
            )
            Spacer(modifier = GlanceModifier.height(GapTight))
            Text(
                text = context.localized(session.title),
                style = sansStyle(colors.onPrimary, 12.sp),
                maxLines = 2,
            )
            Spacer(modifier = GlanceModifier.height(GapBase))
            SmallChipRow(slot, colors, onBand = true)
        }
    }
}

@Composable
private fun SmallChipRow(slot: FavoritesWidgetSlot, colors: FavoritesWidgetColors, onBand: Boolean) {
    val context = LocalContext.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        RoomChip(slot.sessions.first().room, colors)
        if (slot.sessions.size > 1) {
            Spacer(modifier = GlanceModifier.width(GapBase))
            Text(
                text = context.getString(R.string.widget_same_slot_more_small, slot.sessions.size - 1),
                style = sansStyle(if (onBand) colors.onPrimary else colors.onSurfaceVariant, 12.sp),
            )
        }
    }
}

@Composable
private fun ScheduleMediumContent(state: FavoritesWidgetState.Schedule, colors: FavoritesWidgetColors) {
    val context = LocalContext.current
    val live = state.slots.any(FavoritesWidgetSlot::isLive)
    Column(modifier = GlanceModifier.fillMaxSize()) {
        HeaderRow(context.getString(R.string.widget_schedule_label), live = live, colors = colors)
        Spacer(modifier = GlanceModifier.height(GapBase))
        MediumRows(state.slots.toFavoritesWidgetRows(MAX_MEDIUM_ROWS), colors)
    }
}

@Composable
private fun MediumRows(rows: List<FavoritesWidgetRow>, colors: FavoritesWidgetColors) {
    Column(modifier = GlanceModifier.fillMaxWidth()) {
        for ((index, group) in rows.toBandGroups().withIndex()) {
            if (index > 0) {
                Spacer(modifier = GlanceModifier.height(GapBase))
            }
            if (group.isLive) {
                LiveBand(group.rows, colors)
            } else {
                ScheduleRow(group.rows.single(), colors)
            }
        }
    }
}

@Composable
private fun LiveBand(rows: List<FavoritesWidgetRow>, colors: FavoritesWidgetColors) {
    // The band runs 3dp beyond the row slot on top and bottom, per the spec's band geometry.
    Box(
        modifier = GlanceModifier.fillMaxWidth()
            .background(ColorProvider(colors.primary))
            .cornerRadius(8.dp)
            .padding(vertical = 3.dp),
    ) {
        Column {
            for ((index, row) in rows.withIndex()) {
                if (index > 0) {
                    Spacer(modifier = GlanceModifier.height(GapBase))
                }
                ScheduleRow(row, colors)
            }
        }
    }
}

@Composable
private fun ScheduleRow(row: FavoritesWidgetRow, colors: FavoritesWidgetColors) {
    when (row) {
        is FavoritesWidgetRow.Session -> SessionRow(row, colors)
        is FavoritesWidgetRow.More -> MoreRow(row, colors)
    }
}

@Composable
private fun SessionRow(row: FavoritesWidgetRow.Session, colors: FavoritesWidgetColors) {
    val context = LocalContext.current
    val ink = if (row.isLive) colors.onPrimary else colors.onSurface
    val rowModifier = GlanceModifier.fillMaxWidth().height(RowHeight).padding(horizontal = InsetRow)
    Row(
        modifier = if (row.isLive) {
            rowModifier.clickable(actionStartActivityIntent(favoriteSessionDeepLinkIntent(context, row.session.id)))
        } else {
            rowModifier
        },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (row.showsTime) row.session.startsAt else "",
            style = monoStyle(ink, 12.sp, FontWeight.Bold),
            modifier = GlanceModifier.width(TimeCellWidth),
        )
        Spacer(modifier = GlanceModifier.width(GapBase))
        Text(
            text = context.localized(row.session.title),
            style = sansStyle(ink, 12.sp),
            maxLines = 1,
            modifier = GlanceModifier.defaultWeight(),
        )
        Spacer(modifier = GlanceModifier.width(GapWide))
        RoomChip(row.session.room, colors)
    }
}

@Composable
private fun MoreRow(row: FavoritesWidgetRow.More, colors: FavoritesWidgetColors) {
    val context = LocalContext.current
    Row(
        modifier = GlanceModifier.fillMaxWidth().height(RowHeight).padding(horizontal = InsetRow),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = GlanceModifier.width(TimeCellWidth))
        Spacer(modifier = GlanceModifier.width(GapBase))
        Text(
            text = context.getString(R.string.widget_same_slot_more_row, row.count),
            style = sansStyle(colors.onSurfaceVariant, 12.sp),
        )
    }
}

private data class RowGroup(val isLive: Boolean, val rows: List<FavoritesWidgetRow>)

/** Consecutive live rows of one slot share a single band; everything else stands alone. */
private fun List<FavoritesWidgetRow>.toBandGroups(): List<RowGroup> {
    val groups = mutableListOf<RowGroup>()
    for (row in this) {
        val live = row is FavoritesWidgetRow.Session && row.isLive
        val previous = groups.lastOrNull()
        val sameSlot = previous != null && previous.isLive && live &&
            (row as FavoritesWidgetRow.Session).showsTime.not()
        if (sameSlot && previous != null) {
            groups[groups.lastIndex] = previous.copy(rows = previous.rows + row)
        } else {
            groups += RowGroup(isLive = live, rows = listOf(row))
        }
    }
    return groups
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = PREVIEW_SMALL_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Preview(widthDp = PREVIEW_MEDIUM_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
private fun SchedulePreview() {
    FavoritesWidgetContent(previewScheduleState(), KaigiColorScheme.MorningMist.toFavoritesWidgetColors(), previewWidgetMascot())
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = PREVIEW_MEDIUM_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
private fun ScheduleDarkPreview() {
    FavoritesWidgetContent(previewScheduleState(), KaigiColorScheme.CampfireNight.toFavoritesWidgetColors(), previewWidgetMascot())
}

/** Day 1 shortly after the first favorite starts, so the live band and a later row both show. */
private fun previewScheduleState(): FavoritesWidgetState {
    val timetable = Timetable.fake()
    return computeFavoritesWidgetState(
        now = DroidKaigi2026Day.Day1.at(hour = 10, minute = 5),
        timetable = timetable,
        favoriteIds = timetable.bookmarks,
    )
}
