package io.github.droidkaigi.confsched.feature.favorites.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.droidkaigi.confsched.core.designsystem.kaigiTypography
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.KaigiFontFamily
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.SketchCard
import io.github.droidkaigi.confsched.core.ui.SketchDefaults
import io.github.droidkaigi.confsched.core.ui.SketchRoundRectShape

/**
 * The surface both first-favorite steps are drawn on.
 *
 * @param seed the value the border is drawn from, so the two steps do not repeat one outline.
 */
@Composable
internal fun FirstFavoriteDialogCard(
    seed: Int,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    SketchCard(
        modifier = modifier.width(FirstFavoriteDialogDefaults.width),
        shape = SketchRoundRectShape(
            seed = seed,
            roughness = SketchDefaults.roughness,
            tremor = SketchDefaults.tremor,
            cornerRadius = FirstFavoriteDialogDefaults.cornerRadius,
            borderThickness = FirstFavoriteDialogDefaults.borderThickness,
        ),
        borderColor = MaterialTheme.colorScheme.outline,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Column(
            modifier = Modifier.padding(FirstFavoriteDialogDefaults.contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(FirstFavoriteDialogDefaults.gap),
            content = content,
        )
    }
}

internal object FirstFavoriteDialogDefaults {
    val width = 340.dp
    val cornerRadius = 18.dp
    val borderThickness = 3.dp
    val gap = 12.dp
    val contentPadding = PaddingValues(20.dp)

    /** The eyebrow is set in the display face whatever face the rest of the app is in. */
    val eyebrowStyle
        @Composable get() = kaigiTypography(KaigiFontFamily.CourierPrime).labelMedium.copy(letterSpacing = 0.6.sp)

    val titleStyle
        @Composable get() = MaterialTheme.typography.titleMedium

    val descriptionStyle
        @Composable get() = MaterialTheme.typography.bodyMedium
}

@Preview
@Composable
private fun FirstFavoriteDialogCardPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        FirstFavoriteDialogCard(seed = 777, modifier = Modifier.padding(16.dp)) {
            Text(
                text = "NICE CHOICE",
                style = FirstFavoriteDialogDefaults.eyebrowStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
