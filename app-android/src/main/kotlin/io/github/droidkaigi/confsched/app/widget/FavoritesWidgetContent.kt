package io.github.droidkaigi.confsched.app.widget

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.unit.ColorProvider
import io.github.droidkaigi.confsched.app.MainActivity
import io.github.droidkaigi.confsched.app.aboutDeepLinkIntent
import io.github.droidkaigi.confsched.app.favoritesDeepLinkIntent
import io.github.droidkaigi.confsched.app.timetableDayDeepLinkIntent
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.FavoritesWidgetState
import androidx.glance.appwidget.action.actionStartActivity as actionStartActivityIntent

@Composable
internal fun FavoritesWidgetContent(state: FavoritesWidgetState, colors: FavoritesWidgetColors, mascot: WidgetMascot) {
    val context = LocalContext.current
    // Session rows carry their own favorites/session/{id} action.
    val backgroundAction = when (state) {
        is FavoritesWidgetState.Schedule -> actionStartActivityIntent(favoritesDeepLinkIntent(context))

        is FavoritesWidgetState.Empty -> actionStartActivityIntent(timetableDayDeepLinkIntent(context, state.day))

        is FavoritesWidgetState.TodayDone -> actionStartActivityIntent(timetableDayDeepLinkIntent(context, state.day))

        is FavoritesWidgetState.DayWrapUp ->
            actionStartActivityIntent(timetableDayDeepLinkIntent(context, DroidKaigi2026Day.Day2))

        FavoritesWidgetState.PostConference -> actionStartActivityIntent(aboutDeepLinkIntent(context))

        is FavoritesWidgetState.Countdown, FavoritesWidgetState.EventDay -> actionStartActivity<MainActivity>()
    }
    Box(
        modifier = GlanceModifier.fillMaxSize()
            .background(ColorProvider(colors.surface))
            .cornerRadius(16.dp)
            .clickable(backgroundAction),
    ) {
        SketchBorder(colors)
        Box(modifier = GlanceModifier.fillMaxSize().padding(InsetBleed + InsetFrame)) {
            CompositionLocalProvider(LocalWidgetMascot provides mascot) {
                StateContent(state, colors)
            }
        }
    }
}

@Composable
private fun StateContent(state: FavoritesWidgetState, colors: FavoritesWidgetColors) {
    when (state) {
        is FavoritesWidgetState.Countdown -> CountdownContent(state, colors)
        FavoritesWidgetState.EventDay -> EventDayContent(colors)
        is FavoritesWidgetState.Empty -> EmptyContent(state, colors)
        is FavoritesWidgetState.Schedule -> ScheduleContent(state, colors)
        is FavoritesWidgetState.TodayDone -> TodayDoneContent(state, colors)
        is FavoritesWidgetState.DayWrapUp -> DayWrapUpContent(state, colors)
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
