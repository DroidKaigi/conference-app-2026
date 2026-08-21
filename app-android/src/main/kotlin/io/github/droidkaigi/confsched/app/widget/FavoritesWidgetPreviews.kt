package io.github.droidkaigi.confsched.app.widget

import androidx.compose.runtime.Composable
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.FavoritesWidgetState
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.computeFavoritesWidgetState
import io.github.droidkaigi.confsched.core.preview.fake

// The 2x2 and 4x2 cells of the design, which straddle the medium breakpoint.
private const val SMALL_WIDTH_DP = 158
private const val MEDIUM_WIDTH_DP = 338
private const val HEIGHT_DP = 158

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = SMALL_WIDTH_DP, heightDp = HEIGHT_DP)
@Preview(widthDp = MEDIUM_WIDTH_DP, heightDp = HEIGHT_DP)
@Composable
private fun CountdownPreview() {
    FavoritesWidgetContent(FavoritesWidgetState.Countdown(daysUntilStart = 12), KaigiColorScheme.MorningMist.toFavoritesWidgetColors())
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = SMALL_WIDTH_DP, heightDp = HEIGHT_DP)
@Preview(widthDp = MEDIUM_WIDTH_DP, heightDp = HEIGHT_DP)
@Composable
private fun EmptyPreview() {
    FavoritesWidgetContent(FavoritesWidgetState.Empty, KaigiColorScheme.MorningMist.toFavoritesWidgetColors())
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = SMALL_WIDTH_DP, heightDp = HEIGHT_DP)
@Preview(widthDp = MEDIUM_WIDTH_DP, heightDp = HEIGHT_DP)
@Composable
private fun SchedulePreview() {
    FavoritesWidgetContent(previewScheduleState(), KaigiColorScheme.MorningMist.toFavoritesWidgetColors())
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = MEDIUM_WIDTH_DP, heightDp = HEIGHT_DP)
@Composable
private fun ScheduleDarkPreview() {
    FavoritesWidgetContent(previewScheduleState(), KaigiColorScheme.CampfireNight.toFavoritesWidgetColors())
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = SMALL_WIDTH_DP, heightDp = HEIGHT_DP)
@Preview(widthDp = MEDIUM_WIDTH_DP, heightDp = HEIGHT_DP)
@Composable
private fun PostConferencePreview() {
    FavoritesWidgetContent(FavoritesWidgetState.PostConference, KaigiColorScheme.MorningMist.toFavoritesWidgetColors())
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
