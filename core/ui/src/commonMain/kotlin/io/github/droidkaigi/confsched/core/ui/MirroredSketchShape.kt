package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

/**
 * [shape] flipped left-to-right, so the reverse side of a card turned over shows the same
 * hand-sketched edge the front does.
 */
@Immutable
data class MirroredSketchShape(val shape: SketchOutlineShape) : SketchOutlineShape {
    override val borderThickness: Dp get() = shape.borderThickness

    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline =
        when (val outline = shape.createOutline(size, layoutDirection, density)) {
            is Outline.Generic -> Outline.Generic(
                Path().apply {
                    addPath(outline.path)
                    transform(
                        Matrix().apply {
                            translate(x = size.width)
                            scale(x = -1f)
                        },
                    )
                },
            )

            else -> outline
        }
}
