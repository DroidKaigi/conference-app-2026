package io.github.droidkaigi.confsched.app.widget

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import io.github.droidkaigi.confsched.R
import io.github.droidkaigi.confsched.app.MainActivity
import io.github.droidkaigi.confsched.app.aboutDeepLinkIntent
import io.github.droidkaigi.confsched.app.favoriteSessionDeepLinkIntent
import io.github.droidkaigi.confsched.app.favoritesDeepLinkIntent
import io.github.droidkaigi.confsched.core.designsystem.RoomShape
import io.github.droidkaigi.confsched.core.designsystem.roomTheme
import io.github.droidkaigi.confsched.core.model.FavoritesWidgetRow
import io.github.droidkaigi.confsched.core.model.FavoritesWidgetSlot
import io.github.droidkaigi.confsched.core.model.FavoritesWidgetState
import io.github.droidkaigi.confsched.core.model.MultiLangText
import io.github.droidkaigi.confsched.core.model.Room
import io.github.droidkaigi.confsched.core.model.toFavoritesWidgetRows
import androidx.glance.appwidget.action.actionStartActivity as actionStartActivityIntent

// Spacing follows the widget spec's five-step scale on a 4dp base.
private val InsetBleed = 8.dp
private val InsetFrame = 12.dp
private val InsetRow = 8.dp
private val GapTight = 4.dp
private val GapBase = 8.dp
private val GapWide = 16.dp
private val GapArt = 20.dp
private val RowHeight = 22.dp
private val TimeCellWidth = 40.dp

// Halfway between the 2x2 (158dp) and 4x2 (338dp) design sizes.
private val MediumMinWidth = 250.dp

private const val MAX_MEDIUM_ROWS = 3

@Composable
internal fun FavoritesWidgetContent(state: FavoritesWidgetState, colors: FavoritesWidgetColors) {
    val context = LocalContext.current
    // Session rows carry their own favorites/session/{id} action.
    // TODO: Route the empty state to search once the search screen exists.
    val backgroundAction = when (state) {
        is FavoritesWidgetState.Schedule -> actionStartActivityIntent(favoritesDeepLinkIntent(context))
        FavoritesWidgetState.PostConference -> actionStartActivityIntent(aboutDeepLinkIntent(context))
        else -> actionStartActivity<MainActivity>()
    }
    Box(
        modifier = GlanceModifier.fillMaxSize()
            .background(ColorProvider(colors.surface))
            .cornerRadius(16.dp)
            .clickable(backgroundAction),
    ) {
        SketchBorder(colors)
        Box(modifier = GlanceModifier.fillMaxSize().padding(InsetBleed + InsetFrame)) {
            StateContent(state, colors)
        }
    }
}

@Composable
private fun StateContent(state: FavoritesWidgetState, colors: FavoritesWidgetColors) {
    when (state) {
        is FavoritesWidgetState.Countdown -> CountdownContent(state, colors)
        FavoritesWidgetState.Empty -> EmptyContent(colors)
        is FavoritesWidgetState.Schedule -> ScheduleContent(state, colors)
        FavoritesWidgetState.PostConference -> PostConferenceContent(colors)
    }
}

@Composable
private fun SketchBorder(colors: FavoritesWidgetColors) {
    val size = LocalSize.current
    val context = LocalContext.current
    val bitmap = remember(size, colors) {
        sketchBorderBitmap(
            widthDp = size.width.value - 2 * InsetBleed.value,
            heightDp = size.height.value - 2 * InsetBleed.value,
            density = context.resources.displayMetrics.density,
            color = colors.primary.toArgb(),
            medium = isMedium(size),
        )
    }
    Image(
        provider = ImageProvider(bitmap),
        contentDescription = null,
        modifier = GlanceModifier.fillMaxSize().padding(InsetBleed),
        contentScale = ContentScale.FillBounds,
    )
}

@Composable
private fun CountdownContent(state: FavoritesWidgetState.Countdown, colors: FavoritesWidgetColors) {
    val medium = isMedium(LocalSize.current)
    Column(modifier = GlanceModifier.fillMaxSize()) {
        BrandRow(medium, colors)
        Spacer(modifier = GlanceModifier.defaultWeight())
        CountdownBody(state, colors, medium)
        Spacer(modifier = GlanceModifier.defaultWeight())
    }
}

@Composable
private fun CountdownBody(
    state: FavoritesWidgetState.Countdown,
    colors: FavoritesWidgetColors,
    medium: Boolean,
) {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CountdownFigures(state, colors, medium)
        if (medium) {
            Spacer(modifier = GlanceModifier.defaultWeight())
            Mascot(R.drawable.widget_mascot_koala, 28.dp, 30.dp, colors)
        }
    }
}

@Composable
private fun CountdownFigures(
    state: FavoritesWidgetState.Countdown,
    colors: FavoritesWidgetColors,
    medium: Boolean,
) {
    val context = LocalContext.current
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = context.getString(R.string.widget_countdown_prefix),
                style = sansStyle(colors.onSurfaceVariant, 12.sp),
            )
            Spacer(modifier = GlanceModifier.width(GapTight))
            Text(
                text = state.daysUntilStart.toString(),
                style = monoStyle(colors.primary, 36.sp, FontWeight.Normal),
            )
            Spacer(modifier = GlanceModifier.width(GapTight))
            Text(
                text = context.getString(R.string.widget_countdown_unit),
                style = sansStyle(colors.onSurfaceVariant, 12.sp),
            )
        }
        Spacer(modifier = GlanceModifier.height(GapTight))
        Text(
            text = context.getString(R.string.widget_countdown_dates),
            style = monoStyle(colors.onSurfaceVariant, 12.sp, FontWeight.Bold),
        )
        if (medium) {
            Spacer(modifier = GlanceModifier.height(GapBase))
            Text(
                text = context.getString(R.string.widget_countdown_note),
                style = sansStyle(colors.onSurfaceVariant, 12.sp),
            )
        }
    }
}

@Composable
private fun EmptyContent(colors: FavoritesWidgetColors) {
    val medium = isMedium(LocalSize.current)
    val context = LocalContext.current
    val label = if (medium) R.string.widget_schedule_label else R.string.widget_favorites_label
    Column(modifier = GlanceModifier.fillMaxSize()) {
        HeaderRow(context.getString(label), live = false, colors = colors)
        Spacer(modifier = GlanceModifier.height(GapBase))
        EmptyBody(colors, medium)
    }
}

@Composable
private fun EmptyBody(colors: FavoritesWidgetColors, medium: Boolean) {
    val context = LocalContext.current
    val message = if (medium) R.string.widget_empty_medium else R.string.widget_empty_small
    Box(modifier = GlanceModifier.fillMaxSize()) {
        Column(modifier = GlanceModifier.fillMaxWidth().padding(end = mascotClearance(medium))) {
            Text(
                text = context.getString(message),
                style = sansStyle(colors.onSurface, 12.sp),
                maxLines = 4,
            )
            if (medium) {
                Spacer(modifier = GlanceModifier.height(GapBase))
                Text(
                    text = context.getString(R.string.widget_empty_hint),
                    style = sansStyle(colors.onSurfaceVariant, 12.sp),
                )
            }
        }
        Box(
            modifier = GlanceModifier.fillMaxSize(),
            contentAlignment = Alignment.BottomEnd,
        ) {
            Mascot(R.drawable.widget_mascot_ladybug, if (medium) 37.dp else 33.dp, if (medium) 34.dp else 30.dp, colors)
        }
    }
}

@Composable
private fun PostConferenceContent(colors: FavoritesWidgetColors) {
    if (isMedium(LocalSize.current)) {
        PostConferenceMediumContent(colors)
    } else {
        PostConferenceSmallContent(colors)
    }
}

@Composable
private fun PostConferenceSmallContent(colors: FavoritesWidgetColors) {
    val context = LocalContext.current
    Box(
        modifier = GlanceModifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            SymbolMark(44.dp, colors)
            Spacer(modifier = GlanceModifier.height(GapBase))
            Text(
                text = context.getString(R.string.widget_post_message),
                style = sansStyle(colors.onSurface, 12.sp, TextAlign.Center),
                maxLines = 3,
            )
        }
    }
}

@Composable
private fun PostConferenceMediumContent(colors: FavoritesWidgetColors) {
    Column(modifier = GlanceModifier.fillMaxSize()) {
        BrandRow(medium = true, colors = colors)
        Spacer(modifier = GlanceModifier.defaultWeight())
        PostConferenceMediumBody(colors)
        Spacer(modifier = GlanceModifier.defaultWeight())
    }
}

@Composable
private fun PostConferenceMediumBody(colors: FavoritesWidgetColors) {
    val context = LocalContext.current
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = context.getString(R.string.widget_post_message),
            style = sansStyle(colors.onSurface, 12.sp),
            maxLines = 3,
            modifier = GlanceModifier.defaultWeight(),
        )
        Spacer(modifier = GlanceModifier.width(GapArt))
        Mascot(R.drawable.widget_mascot_jellyfish, 37.dp, 34.dp, colors)
    }
}

@Composable
private fun ScheduleContent(state: FavoritesWidgetState.Schedule, colors: FavoritesWidgetColors) {
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

@Composable
private fun HeaderRow(label: String, live: Boolean, colors: FavoritesWidgetColors) {
    Row(
        modifier = GlanceModifier.fillMaxWidth().padding(horizontal = InsetRow),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SymbolMark(19.dp, colors)
        Spacer(modifier = GlanceModifier.width(GapBase))
        Text(label, style = monoStyle(colors.onSurfaceVariant, 12.sp, FontWeight.Bold))
        Spacer(modifier = GlanceModifier.defaultWeight())
        if (live) {
            LivePill(colors)
        }
    }
}

@Composable
private fun BrandRow(medium: Boolean, colors: FavoritesWidgetColors) {
    val context = LocalContext.current
    val brand = if (medium) R.string.widget_brand_full else R.string.widget_brand
    Row(verticalAlignment = Alignment.CenterVertically) {
        SymbolMark(19.dp, colors)
        Spacer(modifier = GlanceModifier.width(GapBase))
        Text(
            text = context.getString(brand),
            style = monoStyle(colors.onSurface, 12.sp, FontWeight.Bold),
        )
    }
}

@Composable
private fun LivePill(colors: FavoritesWidgetColors) {
    val context = LocalContext.current
    Box(
        modifier = GlanceModifier.background(ColorProvider(colors.primary))
            .cornerRadius(9.dp)
            .padding(horizontal = GapBase, vertical = 1.dp),
    ) {
        Text(
            text = context.getString(R.string.widget_live_badge),
            style = monoStyle(colors.onPrimary, 12.sp, FontWeight.Bold),
        )
    }
}

@Composable
private fun SymbolMark(markSize: Dp, colors: FavoritesWidgetColors) {
    Image(
        provider = ImageProvider(R.drawable.widget_symbol_mark),
        contentDescription = null,
        modifier = GlanceModifier.size(markSize),
        colorFilter = ColorFilter.tint(ColorProvider(colors.primary)),
    )
}

@Composable
private fun Mascot(
    @DrawableRes resId: Int,
    mascotWidth: Dp,
    mascotHeight: Dp,
    colors: FavoritesWidgetColors,
) {
    Image(
        provider = ImageProvider(resId),
        contentDescription = null,
        modifier = GlanceModifier.size(mascotWidth, mascotHeight),
        colorFilter = ColorFilter.tint(ColorProvider(colors.onSurfaceVariant)),
    )
}

@Composable
private fun RoomChip(room: Room, colors: FavoritesWidgetColors) {
    val theme = roomTheme(room, colors.isDark)
    Box(
        modifier = GlanceModifier.background(ColorProvider(theme.container))
            .cornerRadius(11.dp)
            .padding(horizontal = GapBase, vertical = 3.dp),
    ) {
        Text(
            text = chipLabel(room, theme.shape),
            style = monoStyle(theme.onContainer, 12.sp, FontWeight.Bold),
            maxLines = 1,
        )
    }
}

private fun chipLabel(room: Room, shape: RoomShape?): String {
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

private fun isMedium(size: DpSize): Boolean = size.width >= MediumMinWidth

private fun mascotClearance(medium: Boolean): Dp = if (medium) 37.dp + GapArt else 0.dp

private fun Context.localized(text: MultiLangText): String =
    if (resources.configuration.locales[0].language == "ja") text.ja else text.en

private fun monoStyle(color: Color, size: TextUnit, weight: FontWeight): TextStyle = TextStyle(
    color = ColorProvider(color),
    fontSize = size,
    fontWeight = weight,
    fontFamily = FontFamily.Monospace,
)

private fun sansStyle(color: Color, size: TextUnit): TextStyle = TextStyle(
    color = ColorProvider(color),
    fontSize = size,
    fontFamily = FontFamily.SansSerif,
)

private fun sansStyle(color: Color, size: TextUnit, align: TextAlign): TextStyle = TextStyle(
    color = ColorProvider(color),
    fontSize = size,
    fontFamily = FontFamily.SansSerif,
    textAlign = align,
)

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
