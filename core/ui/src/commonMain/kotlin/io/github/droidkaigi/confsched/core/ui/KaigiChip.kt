package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme

/**
 * A small label in a filled, hand-sketched pill.
 *
 * The fill is clipped from the outline the border strokes, so the two meet on the line
 * rather than leaving it floating over a squarer shape.
 *
 * @param seed the value the pill is drawn from. The same seed always produces the same pill,
 *   so give neighbouring chips different ones or a row of them reads as a repeat.
 * @param containerColor the colour filling the pill.
 * @param contentColor the colour of the border and of [content], the latter provided as
 *   [LocalContentColor].
 * @param modifier the [Modifier] applied to the chip.
 * @param content the label the pill holds.
 */
@Composable
fun KaigiChip(
    seed: Int,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val combinedSeed = combineSketchSeed(seed)
    val shape = SketchRoundRectShape(
        seed = combinedSeed,
        roughness = KaigiChipDefaults.roughness,
        tremor = KaigiChipDefaults.tremor,
        cornerRadius = KaigiChipDefaults.cornerRadius,
        borderThickness = KaigiChipDefaults.borderThickness,
    )
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .clip(shape)
                .background(containerColor)
                .padding(horizontal = 8.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                content()
            }
        }
        Box(Modifier.matchParentSize().sketchBorder(shape, contentColor))
    }
}

object KaigiChipDefaults {
    val cornerRadius = 8.dp
    val borderThickness = 1.dp
    val roughness = 0.3.dp
    val tremor = 0.1.dp

    val labelStyle
        @Composable get() = MaterialTheme.typography.labelSmall
}

@Preview
@Composable
private fun KaigiChipPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            KaigiChip(
                seed = 11,
                containerColor = Color(0xFFE2DCFE),
                contentColor = Color(0xFF3F2296),
            ) {
                Text("NARWHAL", style = KaigiChipDefaults.labelStyle)
            }
            KaigiChip(
                seed = 12,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            ) {
                Text("EN", style = KaigiChipDefaults.labelStyle)
            }
        }
    }
}
