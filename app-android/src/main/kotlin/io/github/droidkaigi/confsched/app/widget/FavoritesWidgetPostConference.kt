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
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.width
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import io.github.droidkaigi.confsched.R
import io.github.droidkaigi.confsched.core.model.FavoritesWidgetState
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme

@Composable
internal fun PostConferenceContent(colors: FavoritesWidgetColors) {
    FarewellContent(
        message = LocalContext.current.getString(R.string.widget_post_message),
        secondary = null,
        colors = colors,
    )
}

@Composable
internal fun DayWrapUpContent(state: FavoritesWidgetState.DayWrapUp, colors: FavoritesWidgetColors) {
    val context = LocalContext.current
    FarewellContent(
        message = context.getString(R.string.widget_wrap_up_message),
        secondary = if (state.tomorrowFavorites > 0) {
            tomorrowFavoritesText(state.tomorrowFavorites)
        } else {
            context.getString(R.string.widget_wrap_up_add)
        },
        colors = colors,
    )
}

@Composable
private fun FarewellContent(message: String, secondary: String?, colors: FavoritesWidgetColors) {
    if (isMedium(LocalSize.current)) {
        FarewellMediumContent(message, secondary, colors)
    } else {
        FarewellSmallContent(message, colors)
    }
}

@Composable
private fun FarewellSmallContent(message: String, colors: FavoritesWidgetColors) {
    Box(
        modifier = GlanceModifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            SymbolMark(44.dp, colors)
            Spacer(modifier = GlanceModifier.height(GapBase))
            Text(
                text = message,
                style = sansStyle(colors.onSurface, 12.sp, TextAlign.Center),
                maxLines = 3,
            )
        }
    }
}

@Composable
private fun FarewellMediumContent(message: String, secondary: String?, colors: FavoritesWidgetColors) {
    Column(modifier = GlanceModifier.fillMaxSize()) {
        BrandRow(medium = true, colors = colors)
        Spacer(modifier = GlanceModifier.defaultWeight())
        FarewellMediumBody(message, secondary, colors)
        Spacer(modifier = GlanceModifier.defaultWeight())
    }
}

@Composable
private fun FarewellMediumBody(message: String, secondary: String?, colors: FavoritesWidgetColors) {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = GlanceModifier.defaultWeight()) {
            Text(
                text = message,
                style = sansStyle(colors.onSurface, 12.sp),
                maxLines = 3,
            )
            if (secondary != null) {
                Spacer(modifier = GlanceModifier.height(GapTight))
                Text(
                    text = secondary,
                    style = sansStyle(colors.onSurfaceVariant, 12.sp),
                    maxLines = 1,
                )
            }
        }
        Spacer(modifier = GlanceModifier.width(GapArt))
        val mascot = LocalWidgetMascot.current
        Mascot(mascot.resId, mascot.width(34.dp), 34.dp, colors)
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = PREVIEW_SMALL_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Preview(widthDp = PREVIEW_MEDIUM_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
private fun PostConferencePreview() {
    FavoritesWidgetContent(FavoritesWidgetState.PostConference, KaigiColorScheme.MorningMist.toFavoritesWidgetColors(), previewWidgetMascot())
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = PREVIEW_SMALL_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Preview(widthDp = PREVIEW_MEDIUM_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
private fun DayWrapUpPreview() {
    FavoritesWidgetContent(
        FavoritesWidgetState.DayWrapUp(tomorrowFavorites = 3),
        KaigiColorScheme.MorningMist.toFavoritesWidgetColors(),
        previewWidgetMascot(),
    )
}
