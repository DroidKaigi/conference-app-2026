package io.github.droidkaigi.confsched.app.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.width
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import io.github.droidkaigi.confsched.R
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.FavoritesWidgetState
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import kotlinx.datetime.number

@Composable
internal fun CountdownContent(state: FavoritesWidgetState.Countdown, colors: FavoritesWidgetColors) {
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
            val mascot = LocalWidgetMascot.current
            Mascot(mascot.resId, mascot.width(30.dp), 30.dp, colors)
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
            text = context.getString(
                R.string.widget_countdown_dates,
                DroidKaigi2026Day.Day1.date.month.number,
                DroidKaigi2026Day.Day1.date.day,
                DroidKaigi2026Day.Day2.date.month.number,
                DroidKaigi2026Day.Day2.date.day,
            ),
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
internal fun EventDayContent(colors: FavoritesWidgetColors) {
    val medium = isMedium(LocalSize.current)
    Column(modifier = GlanceModifier.fillMaxSize()) {
        BrandRow(medium, colors)
        Spacer(modifier = GlanceModifier.defaultWeight())
        EventDayBody(colors, medium)
        Spacer(modifier = GlanceModifier.defaultWeight())
    }
}

@Composable
private fun EventDayBody(colors: FavoritesWidgetColors, medium: Boolean) {
    val context = LocalContext.current
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = context.getString(R.string.widget_event_day_message),
                style = monoStyle(colors.primary, 14.sp, FontWeight.Bold),
                maxLines = 2,
            )
            Spacer(modifier = GlanceModifier.height(GapTight))
            Text(
                text = context.getString(R.string.widget_event_day_note),
                style = sansStyle(colors.onSurfaceVariant, 12.sp),
                maxLines = 3,
            )
        }
        if (medium) {
            Spacer(modifier = GlanceModifier.defaultWeight())
            val mascot = LocalWidgetMascot.current
            Mascot(mascot.resId, mascot.width(30.dp), 30.dp, colors)
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = PREVIEW_SMALL_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Preview(widthDp = PREVIEW_MEDIUM_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
private fun CountdownPreview() {
    FavoritesWidgetContent(
        FavoritesWidgetState.Countdown(daysUntilStart = 12),
        KaigiColorScheme.MorningMist.toFavoritesWidgetColors(),
        previewWidgetMascot(),
    )
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = PREVIEW_SMALL_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Preview(widthDp = PREVIEW_MEDIUM_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
private fun EventDayPreview() {
    FavoritesWidgetContent(
        FavoritesWidgetState.EventDay,
        KaigiColorScheme.MorningMist.toFavoritesWidgetColors(),
        previewWidgetMascot(),
    )
}
