package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.generated.resources.Res
import io.github.droidkaigi.confsched.core.ui.generated.resources.doodle_outline
import org.jetbrains.compose.resources.stringResource

/** Turns the rim the next stroke is drawn with on and off. */
@Composable
fun DoodleOutlineToggle(
    outlined: Boolean,
    onOutlinedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    KaigiFilterChip(
        selected = outlined,
        onClick = { onOutlinedChange(!outlined) },
        label = stringResource(Res.string.doodle_outline),
        seed = OUTLINE_TOGGLE_SEED,
        modifier = modifier.testTag(DOODLE_OUTLINE_TOGGLE_TEST_TAG),
        role = Role.Checkbox,
    )
}

private const val OUTLINE_TOGGLE_SEED = 4391

@LocalePreviews
@Composable
private fun DoodleOutlineTogglePreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DoodleOutlineToggle(
            outlined = true,
            onOutlinedChange = {},
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(DoodleOutlineTogglePreviewPadding),
        )
    }
}

@LocalePreviews
@Composable
private fun DoodleOutlineToggleOffPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DoodleOutlineToggle(
            outlined = false,
            onOutlinedChange = {},
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(DoodleOutlineTogglePreviewPadding),
        )
    }
}

private val DoodleOutlineTogglePreviewPadding = 16.dp

const val DOODLE_OUTLINE_TOGGLE_TEST_TAG = "DoodleOutlineToggleTestTag"
