package io.github.droidkaigi.confsched.core.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import kotlin.random.Random

/**
 * A speckled overlay that reads as paper grain. Drawn over the content with an overlay blend, so
 * it both lightens and darkens what is underneath the way fibre catches the light.
 */
fun Modifier.paperGrain(alpha: Float): Modifier = drawWithCache {
    val brush = ShaderBrush(ImageShader(GrainTile, TileMode.Repeated, TileMode.Repeated))
    onDrawWithContent {
        drawContent()
        drawRect(brush = brush, alpha = alpha, blendMode = BlendMode.Overlay)
    }
}

private const val GRAIN_TILE_SIZE = 96

// One shared tile: the speckle must not shimmer, so every draw reads the same pixels.
private val GrainTile: ImageBitmap by lazy {
    val bitmap = ImageBitmap(GRAIN_TILE_SIZE, GRAIN_TILE_SIZE)
    val canvas = Canvas(bitmap)
    val paint = Paint()
    val random = Random(20260901)
    val pixel = Size(1f, 1f)
    for (y in 0 until GRAIN_TILE_SIZE) {
        for (x in 0 until GRAIN_TILE_SIZE) {
            val luminance = random.nextFloat()
            paint.color = Color(luminance, luminance, luminance)
            canvas.drawRect(
                left = x.toFloat(),
                top = y.toFloat(),
                right = x + pixel.width,
                bottom = y + pixel.height,
                paint = paint,
            )
        }
    }
    bitmap
}
