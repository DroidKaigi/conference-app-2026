package io.github.droidkaigi.confsched.feature.eventmap.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.SketchRoundRectShape
import io.github.droidkaigi.confsched.core.ui.combineSketchSeed
import io.github.droidkaigi.confsched.core.ui.sketchBorder

@Composable
internal fun SketchCard(
    modifier: Modifier = Modifier,
    shape: SketchRoundRectShape = SketchRoundRectShape(seed = 40, cornerRadius = 16.dp, borderThickness = 2.dp),
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    color: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    content: @Composable (() -> Unit) = {},
) {
    val combinedShape = shape.copy(seed = combineSketchSeed(shape.seed))

    Surface(
        shape = combinedShape,
        color = color,
        modifier = modifier
            .sketchBorder(
                shape = combinedShape,
                color = borderColor,
            ),
        content = content,
    )
}

@Preview
@Composable
private fun SketchCardPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        SketchCard(
            modifier = Modifier
                .padding(16.dp),
        ) {
            Text(
                text = "スタンプラリー",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .width(260.dp)
                    .padding(16.dp),
            )
        }
    }
}
