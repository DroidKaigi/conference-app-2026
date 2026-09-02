package io.github.droidkaigi.confsched.core.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.random.Random

/**
 * A mottled overlay that reads as paper: a coarse sheet unevenness with a fine tooth on top,
 * blended over the content so it both lightens and darkens what is underneath the way fibre
 * catches the light.
 */
fun Modifier.paperGrain(alpha: Float): Modifier = drawWithCache {
    val brush = ShaderBrush(ImageShader(GrainTile, TileMode.Repeated, TileMode.Repeated))
    // One tile pixel spans one dp: at raw pixel scale the speckle vanishes on a dense screen.
    val scale = density
    onDrawWithContent {
        drawContent()
        withTransform({ scale(scale, scale, pivot = Offset.Zero) }) {
            drawRect(
                brush = brush,
                size = Size(size.width / scale, size.height / scale),
                alpha = alpha,
                blendMode = BlendMode.Overlay,
            )
        }
    }
}

private const val GRAIN_TILE_SIZE = 128
private const val GRAIN_CELL_SIZE = 8
private const val FINE_SHARE = 0.45f
private const val COARSE_SHARE = 0.55f

// One shared tile: the mottle must not shimmer, so every draw reads the same pixels.
private val GrainTile: ImageBitmap by lazy {
    val cells = GRAIN_TILE_SIZE / GRAIN_CELL_SIZE
    val random = Random(20260901)
    val lattice = Array(cells) { FloatArray(cells) { random.nextFloat() } }

    // Bilinear value noise over a wrapping lattice, so the tile repeats seamlessly.
    fun coarse(x: Int, y: Int): Float {
        val cellX = x / GRAIN_CELL_SIZE
        val cellY = y / GRAIN_CELL_SIZE
        val fx = (x % GRAIN_CELL_SIZE) / GRAIN_CELL_SIZE.toFloat()
        val fy = (y % GRAIN_CELL_SIZE) / GRAIN_CELL_SIZE.toFloat()
        val c00 = lattice[cellY][cellX]
        val c10 = lattice[cellY][(cellX + 1) % cells]
        val c01 = lattice[(cellY + 1) % cells][cellX]
        val c11 = lattice[(cellY + 1) % cells][(cellX + 1) % cells]
        val top = c00 + (c10 - c00) * fx
        val bottom = c01 + (c11 - c01) * fx
        return top + (bottom - top) * fy
    }

    val bitmap = ImageBitmap(GRAIN_TILE_SIZE, GRAIN_TILE_SIZE)
    val canvas = Canvas(bitmap)
    val paint = Paint()
    for (y in 0 until GRAIN_TILE_SIZE) {
        for (x in 0 until GRAIN_TILE_SIZE) {
            val fine = random.nextFloat()
            val value = 0.5f + (fine - 0.5f) * FINE_SHARE + (coarse(x, y) - 0.5f) * COARSE_SHARE
            paint.color = Color(value, value, value)
            canvas.drawRect(
                left = x.toFloat(),
                top = y.toFloat(),
                right = x + 1f,
                bottom = y + 1f,
                paint = paint,
            )
        }
    }
    bitmap
}
