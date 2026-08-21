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
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import io.github.droidkaigi.confsched.R

@Composable
internal fun PostConferenceContent(colors: FavoritesWidgetColors) {
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
