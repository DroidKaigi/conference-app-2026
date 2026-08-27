package io.github.droidkaigi.confsched.feature.profilecard

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.ui.MirroredSketchShape
import io.github.droidkaigi.confsched.core.ui.SketchRoundRectShape
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals

class MirroredSketchShapeTest {
    private val density = Density(density = 1f, fontScale = 1f)
    private val size = Size(320f, 480f)
    private val shape = SketchRoundRectShape(seed = 7, cornerRadius = 16.dp, borderThickness = 2.dp, roughness = 2.dp, tremor = 1.dp)

    private fun bounds(outline: Outline) = assertIs<Outline.Generic>(outline).path.getBounds()

    @Test
    fun the_mirrored_outline_stays_inside_the_same_bounds() {
        val original = bounds(shape.createOutline(size, LayoutDirection.Ltr, density))
        val mirrored = bounds(MirroredSketchShape(shape).createOutline(size, LayoutDirection.Ltr, density))
        assertEquals(size.width - original.right, mirrored.left, absoluteTolerance = 0.01f)
        assertEquals(size.width - original.left, mirrored.right, absoluteTolerance = 0.01f)
        assertEquals(original.top, mirrored.top, absoluteTolerance = 0.01f)
        assertEquals(original.bottom, mirrored.bottom, absoluteTolerance = 0.01f)
    }

    @Test
    fun mirroring_changes_the_outline() {
        val original = assertIs<Outline.Generic>(shape.createOutline(size, LayoutDirection.Ltr, density)).path
        val mirrored = assertIs<Outline.Generic>(MirroredSketchShape(shape).createOutline(size, LayoutDirection.Ltr, density)).path
        assertNotEquals(original.getBounds().left, mirrored.getBounds().left)
    }
}
