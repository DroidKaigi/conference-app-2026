package io.github.droidkaigi.confsched.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.github.droidkaigi.confsched.core.model.DoodlePenSize
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.generated.resources.Res
import io.github.droidkaigi.confsched.core.ui.generated.resources.doodle_pen_normal
import io.github.droidkaigi.confsched.core.ui.generated.resources.doodle_pen_thick
import io.github.droidkaigi.confsched.core.ui.generated.resources.doodle_pen_thin
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/** Picks the pen the next stroke is drawn with, whichever canvas it is drawn on. */
@Composable
fun DoodlePenSizeRow(
    selectedPenSize: DoodlePenSize,
    onPenSizeClick: (DoodlePenSize) -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentColor = MaterialTheme.colorScheme.primary
    KaigiSingleChoiceSegmentedButtonRow(
        outlineSeed = PEN_SIZE_SEED,
        modifier = modifier,
        borderColor = contentColor,
    ) {
        DoodlePenSize.entries.forEachIndexed { index, penSize ->
            KaigiSegmentedButton(
                selected = penSize == selectedPenSize,
                onClick = { onPenSizeClick(penSize) },
                dividerSeed = PenSizeDividerSeeds.getOrNull(index),
                leadingDividerSeed = PenSizeDividerSeeds.getOrNull(index - 1),
                selectedContainerColor = contentColor,
                selectedContentColor = MaterialTheme.colorScheme.onPrimary,
                contentColor = contentColor,
            ) {
                Text(stringResource(penSize.label), style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

private val DoodlePenSize.label: StringResource
    get() = when (this) {
        DoodlePenSize.Thin -> Res.string.doodle_pen_thin
        DoodlePenSize.Normal -> Res.string.doodle_pen_normal
        DoodlePenSize.Thick -> Res.string.doodle_pen_thick
    }

private const val PEN_SIZE_SEED = 4341

// One rule fewer than there are options: the last option has nothing after it to be separated from.
private val PenSizeDividerSeeds = listOf(4342, 4343)

@LocalePreviews
@Composable
private fun DoodlePenSizeRowPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DoodlePenSizeRow(selectedPenSize = DoodlePenSize.Normal, onPenSizeClick = {})
    }
}

@LocalePreviews
@Composable
private fun DoodlePenSizeRowThickSelectedPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DoodlePenSizeRow(selectedPenSize = DoodlePenSize.Thick, onPenSizeClick = {})
    }
}
