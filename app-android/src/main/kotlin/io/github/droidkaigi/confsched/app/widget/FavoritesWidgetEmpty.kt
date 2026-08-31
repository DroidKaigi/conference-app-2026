package io.github.droidkaigi.confsched.app.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.Text
import io.github.droidkaigi.confsched.R
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.FavoritesWidgetState
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme

@Composable
internal fun EmptyContent(state: FavoritesWidgetState.Empty, colors: FavoritesWidgetColors) {
    val context = LocalContext.current
    DayPromptContent(
        message = context.getString(R.string.widget_empty_message),
        hint = context.getString(R.string.widget_empty_hint, state.day.label),
        otherDayFavorites = state.otherDayFavorites,
        colors = colors,
    )
}

@Composable
internal fun TodayDoneContent(state: FavoritesWidgetState.TodayDone, colors: FavoritesWidgetColors) {
    val context = LocalContext.current
    DayPromptContent(
        message = context.getString(R.string.widget_done_message),
        hint = context.getString(R.string.widget_done_hint),
        otherDayFavorites = state.otherDayFavorites,
        colors = colors,
    )
}

@Composable
private fun DayPromptContent(
    message: String,
    hint: String,
    otherDayFavorites: Int,
    colors: FavoritesWidgetColors,
) {
    val medium = isMedium(LocalSize.current)
    val context = LocalContext.current
    val label = if (medium) R.string.widget_schedule_label else R.string.widget_favorites_label
    Column(modifier = GlanceModifier.fillMaxSize()) {
        HeaderRow(context.getString(label), live = false, colors = colors)
        Spacer(modifier = GlanceModifier.height(GapBase))
        DayPromptBody(message, hint, otherDayFavorites, colors, medium)
    }
}

@Composable
private fun DayPromptBody(
    message: String,
    hint: String,
    otherDayFavorites: Int,
    colors: FavoritesWidgetColors,
    medium: Boolean,
) {
    Box(modifier = GlanceModifier.fillMaxSize()) {
        Column(modifier = GlanceModifier.fillMaxWidth().padding(end = mascotClearance(medium))) {
            Text(
                text = message,
                style = sansStyle(colors.onSurface, 12.sp),
                maxLines = 4,
            )
            if (medium) {
                Spacer(modifier = GlanceModifier.height(GapBase))
                Text(
                    text = hint,
                    style = sansStyle(colors.onSurfaceVariant, 12.sp),
                )
                if (otherDayFavorites > 0) {
                    Spacer(modifier = GlanceModifier.height(GapTight))
                    Text(
                        text = tomorrowFavoritesText(otherDayFavorites),
                        style = sansStyle(colors.onSurfaceVariant, 12.sp),
                    )
                }
            }
        }
        Box(
            modifier = GlanceModifier.fillMaxSize(),
            contentAlignment = Alignment.BottomEnd,
        ) {
            val mascot = LocalWidgetMascot.current
            val mascotHeight = if (medium) 34.dp else 30.dp
            Mascot(mascot.resId, mascot.width(mascotHeight), mascotHeight, colors)
        }
    }
}

@Composable
internal fun tomorrowFavoritesText(count: Int): String = LocalContext.current.resources
    .getQuantityString(R.plurals.widget_tomorrow_favorites, count, count)

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = PREVIEW_SMALL_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Preview(widthDp = PREVIEW_MEDIUM_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
private fun EmptyPreview() {
    FavoritesWidgetContent(
        FavoritesWidgetState.Empty(day = DroidKaigi2026Day.Day1, otherDayFavorites = 2),
        KaigiColorScheme.MorningMist.toFavoritesWidgetColors(),
        previewWidgetMascot(),
    )
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = PREVIEW_SMALL_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Preview(widthDp = PREVIEW_MEDIUM_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
private fun TodayDonePreview() {
    FavoritesWidgetContent(
        FavoritesWidgetState.TodayDone(day = DroidKaigi2026Day.Day1, otherDayFavorites = 1),
        KaigiColorScheme.MorningMist.toFavoritesWidgetColors(),
        previewWidgetMascot(),
    )
}
