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
 * A speckled overlay that reads as the tooth of paper: a fine, even grain with a faint clumping,
 * blended over the content so it both lightens and darkens what is underneath the way fibre
 * catches the light. Deliberately free of low-frequency blotches, which read as stains.
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
private const val FINE_SHARE = 0.55f
private const val CLUMP_2_SHARE = 0.3f
private const val CLUMP_4_SHARE = 0.15f

// One shared tile: the grain must not shimmer, so every draw reads the same pixels.
private val GrainTile: ImageBitmap by lazy {
    val random = Random(20260901)
    val clump2 = valueNoise(cellSize = 2, random)
    val clump4 = valueNoise(cellSize = 4, random)
    val bitmap = ImageBitmap(GRAIN_TILE_SIZE, GRAIN_TILE_SIZE)
    val canvas = Canvas(bitmap)
    val paint = Paint()
    for (y in 0 until GRAIN_TILE_SIZE) {
        for (x in 0 until GRAIN_TILE_SIZE) {
            val fine = random.nextFloat()
            val value = 0.5f +
                (fine - 0.5f) * FINE_SHARE +
                (clump2(x, y) - 0.5f) * CLUMP_2_SHARE +
                (clump4(x, y) - 0.5f) * CLUMP_4_SHARE
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

// Bilinear value noise over a wrapping lattice, so the tile repeats seamlessly.
private fun valueNoise(cellSize: Int, random: Random): (Int, Int) -> Float {
    val cells = GRAIN_TILE_SIZE / cellSize
    val lattice = Array(cells) { FloatArray(cells) { random.nextFloat() } }
    return { x, y ->
        val cellX = x / cellSize
        val cellY = y / cellSize
        val fx = (x % cellSize) / cellSize.toFloat()
        val fy = (y % cellSize) / cellSize.toFloat()
        val c00 = lattice[cellY][cellX]
        val c10 = lattice[cellY][(cellX + 1) % cells]
        val c01 = lattice[(cellY + 1) % cells][cellX]
        val c11 = lattice[(cellY + 1) % cells][(cellX + 1) % cells]
        val top = c00 + (c10 - c00) * fx
        val bottom = c01 + (c11 - c01) * fx
        top + (bottom - top) * fy
    }
}
