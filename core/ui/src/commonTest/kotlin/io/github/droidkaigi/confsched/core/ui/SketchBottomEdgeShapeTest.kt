package io.github.droidkaigi.confsched.core.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull

class SketchBottomEdgeShapeTest {
    private val density = Density(density = 1f)
    private val shape = SketchBottomEdgeShape(
        seed = 1,
        roughness = 8.dp,
        tremor = 1.dp,
        sweepWavelength = 200.dp,
        tremorWavelength = 42.dp,
    )

    @Test
    fun a_box_too_short_for_the_swing_falls_back_to_a_rectangle() {
        val size = Size(width = 400f, height = 18f)
        with(shape) { assertNull(density.edgePathOrNull(size)) }
        assertIs<Outline.Rectangle>(shape.createOutline(size, LayoutDirection.Ltr, density))
    }

    @Test
    fun a_box_without_width_falls_back_to_a_rectangle() {
        val size = Size(width = 0f, height = 100f)
        assertIs<Outline.Rectangle>(shape.createOutline(size, LayoutDirection.Ltr, density))
    }

    @Test
    fun the_wobble_is_validated_on_construction() {
        assertFailsWith<IllegalArgumentException> { shape.copy(roughness = (-1).dp) }
        assertFailsWith<IllegalArgumentException> { shape.copy(tremorWavelength = 0.dp) }
    }

    @Test
    fun the_edge_stroke_requires_a_positive_thickness() {
        assertFailsWith<IllegalArgumentException> {
            Modifier.sketchBottomEdge(shape = shape, color = Color.Black, thickness = 0.dp)
        }
    }
}
