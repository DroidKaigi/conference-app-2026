package io.github.droidkaigi.confsched.feature.settings.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.SketchDefaults
import io.github.droidkaigi.confsched.core.ui.SketchRoundRectShape
import io.github.droidkaigi.confsched.core.ui.combineSketchSeed
import io.github.droidkaigi.confsched.core.ui.sketchBorder

/** The board the theme swatches lie on, which wraps them by however many the width takes. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SwatchBoardView(
    seed: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val shape = SketchRoundRectShape(
        seed = combineSketchSeed(seed),
        roughness = SketchDefaults.roughness,
        tremor = SketchDefaults.tremor,
        cornerRadius = SwatchBoardDefaults.cornerRadius,
        borderThickness = SwatchBoardDefaults.borderThickness,
    )
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .sketchBorder(shape = shape, color = MaterialTheme.colorScheme.outline)
            .padding(SwatchBoardDefaults.padding),
        horizontalArrangement = Arrangement.spacedBy(SwatchBoardDefaults.swatchSpacing),
        verticalArrangement = Arrangement.spacedBy(SwatchBoardDefaults.swatchSpacing),
    ) {
        content()
    }
}

private object SwatchBoardDefaults {
    val cornerRadius = 16.dp
    val borderThickness = 2.dp
    val padding = 14.dp
    val swatchSpacing = 12.dp
}

@Preview
@Composable
private fun SwatchBoardViewPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        SwatchBoardView(seed = 1, modifier = Modifier.padding(16.dp)) {
            KaigiColorScheme.entries.forEachIndexed { index, scheme ->
                ColorSchemeSwatchItem(
                    colorScheme = scheme,
                    label = scheme.name,
                    selected = index == 0,
                    seed = index,
                    onClick = {},
                )
            }
        }
    }
}
