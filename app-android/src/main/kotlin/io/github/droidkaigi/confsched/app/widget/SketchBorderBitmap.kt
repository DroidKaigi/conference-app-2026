package io.github.droidkaigi.confsched.app.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.ui.SketchRoundRectShape

/** The seed the design pins for the small border. */
private const val SMALL_BORDER_SEED = 2601

/** The seed the design pins for the medium border. */
private const val MEDIUM_BORDER_SEED = 2602

// The lattice is pinned to the design's base frame sizes so the wobble does not reshuffle
// when the launcher grid makes the actual widget a few dp larger or smaller.
private val SmallReferenceSize = DpSize(142.dp, 142.dp)
private val MediumReferenceSize = DpSize(322.dp, 142.dp)

private val BorderThickness = 1.5.dp
private val BorderCornerRadius = 16.dp

/**
 * Draws the hand-drawn widget frame into a bitmap of [widthDp] x [heightDp] at [density],
 * reusing the design system's sketch outline math.
 */
internal fun sketchBorderBitmap(
    widthDp: Float,
    heightDp: Float,
    density: Float,
    color: Int,
    medium: Boolean,
): Bitmap {
    val shape = SketchRoundRectShape(
        seed = if (medium) MEDIUM_BORDER_SEED else SMALL_BORDER_SEED,
        cornerRadius = BorderCornerRadius,
        borderThickness = BorderThickness,
        referenceSize = if (medium) MediumReferenceSize else SmallReferenceSize,
    )
    val densityScope = Density(density)
    val widthPx = (widthDp * density).toInt().coerceAtLeast(1)
    val heightPx = (heightDp * density).toInt().coerceAtLeast(1)
    val outline = shape.createOutline(
        size = Size(widthPx.toFloat(), heightPx.toFloat()),
        layoutDirection = LayoutDirection.Ltr,
        density = densityScope,
    ) as Outline.Generic
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = with(densityScope) { BorderThickness.toPx() }
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        this.color = color
    }
    val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
    Canvas(bitmap).drawPath(outline.path.asAndroidPath(), paint)
    return bitmap
}
