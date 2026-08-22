package io.github.droidkaigi.confsched.app.widget

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.unit.ColorProvider
import io.github.droidkaigi.confsched.R
import io.github.droidkaigi.confsched.core.designsystem.RoomShape
import io.github.droidkaigi.confsched.core.designsystem.roomTheme
import io.github.droidkaigi.confsched.core.model.Room

@Composable
internal fun HeaderRow(label: String, live: Boolean, colors: FavoritesWidgetColors) {
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
internal fun BrandRow(medium: Boolean, colors: FavoritesWidgetColors) {
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
internal fun SymbolMark(markSize: Dp, colors: FavoritesWidgetColors) {
    Image(
        provider = ImageProvider(R.drawable.widget_symbol_mark),
        contentDescription = null,
        modifier = GlanceModifier.size(markSize),
        colorFilter = ColorFilter.tint(ColorProvider(colors.primary)),
    )
}

@Composable
internal fun Mascot(
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
internal fun RoomChip(room: Room, colors: FavoritesWidgetColors) {
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
