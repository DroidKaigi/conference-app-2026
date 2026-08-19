package io.github.droidkaigi.confsched.app.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import io.github.droidkaigi.confsched.R
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

/**
 * Rasterizes the widget's shipped vector drawables and checks that the line art actually lands
 * on the canvas: hand-converted Figma vectors fail silently (blank or clipped render) rather
 * than at compile time.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class WidgetDrawableRenderTest {
    private fun render(resId: Int): Bitmap {
        val context = RuntimeEnvironment.getApplication()
        val drawable = requireNotNull(context.getDrawable(resId))
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth * RENDER_SCALE,
            drawable.intrinsicHeight * RENDER_SCALE,
            Bitmap.Config.ARGB_8888,
        )
        drawable.setBounds(0, 0, bitmap.width, bitmap.height)
        drawable.draw(Canvas(bitmap))
        return bitmap
    }

    private fun Bitmap.inkFraction(left: Float, top: Float, right: Float, bottom: Float): Double {
        val l = (width * left).toInt()
        val t = (height * top).toInt()
        val r = (width * right).toInt()
        val b = (height * bottom).toInt()
        var ink = 0
        for (y in t until b) {
            for (x in l until r) {
                if ((getPixel(x, y) ushr 24) > 0x20) ink++
            }
        }
        return ink.toDouble() / ((r - l) * (b - t))
    }

    private fun Bitmap.dump(name: String) {
        val dir = File("build/outputs/widget-drawable-renders").apply { mkdirs() }
        File(dir, "$name.png").outputStream().use { compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    private fun assertLineArt(resId: Int, name: String) {
        val bitmap = render(resId)
        bitmap.dump(name)
        val total = bitmap.inkFraction(0f, 0f, 1f, 1f)
        // Line art: some ink, but nowhere near a solid fill.
        assertTrue("$name total ink $total out of range", total in 0.02..0.45)
        for ((quadrant, box) in mapOf(
            "top-left" to listOf(0f, 0f, 0.5f, 0.5f),
            "top-right" to listOf(0.5f, 0f, 1f, 0.5f),
            "bottom-left" to listOf(0f, 0.5f, 0.5f, 1f),
            "bottom-right" to listOf(0.5f, 0.5f, 1f, 1f),
        )) {
            val ink = bitmap.inkFraction(box[0], box[1], box[2], box[3])
            assertTrue("$name $quadrant ink $ink too low", ink > 0.005)
        }
        // The viewport carries a half-stroke margin around the geometry, so ink on the outermost
        // pixel row means the drawing is clipped (the failure a mis-converted transform produces).
        for ((edge, box) in mapOf(
            "top" to listOf(0f, 0f, 1f, 0f),
            "bottom" to listOf(0f, 1f, 1f, 1f),
            "left" to listOf(0f, 0f, 0f, 1f),
            "right" to listOf(1f, 0f, 1f, 1f),
        )) {
            val ink = bitmap.edgeInkFraction(box[0], box[1], box[2], box[3])
            assertTrue("$name clipped at $edge edge (ink $ink)", ink == 0.0)
        }
    }

    private fun Bitmap.edgeInkFraction(left: Float, top: Float, right: Float, bottom: Float): Double {
        val l = (width * left).toInt().coerceAtMost(width - 1)
        val t = (height * top).toInt().coerceAtMost(height - 1)
        val r = ((width * right).toInt()).coerceAtLeast(l + 1).coerceAtMost(width)
        val b = ((height * bottom).toInt()).coerceAtLeast(t + 1).coerceAtMost(height)
        var ink = 0
        for (y in t until b) {
            for (x in l until r) {
                if ((getPixel(x, y) ushr 24) > 0x20) ink++
            }
        }
        return ink.toDouble() / ((r - l) * (b - t))
    }

    @Test
    fun koala_renders_as_line_art_in_every_quadrant() {
        assertLineArt(R.drawable.widget_mascot_koala, "koala")
    }

    @Test
    fun ladybug_renders_as_line_art_in_every_quadrant() {
        assertLineArt(R.drawable.widget_mascot_ladybug, "ladybug")
    }

    @Test
    fun jellyfish_renders_as_line_art_in_every_quadrant() {
        assertLineArt(R.drawable.widget_mascot_jellyfish, "jellyfish")
    }

    @Test
    fun symbol_mark_renders_as_line_art_in_every_quadrant() {
        assertLineArt(R.drawable.widget_symbol_mark, "symbol_mark")
    }

    companion object {
        private const val RENDER_SCALE = 8
    }
}
