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
import io.github.droidkaigi.confsched.core.model.FavoritesWidgetState
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme

@Composable
internal fun EmptyContent(colors: FavoritesWidgetColors) {
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

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = PREVIEW_SMALL_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Preview(widthDp = PREVIEW_MEDIUM_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
private fun EmptyPreview() {
    FavoritesWidgetContent(FavoritesWidgetState.Empty, KaigiColorScheme.MorningMist.toFavoritesWidgetColors())
}
