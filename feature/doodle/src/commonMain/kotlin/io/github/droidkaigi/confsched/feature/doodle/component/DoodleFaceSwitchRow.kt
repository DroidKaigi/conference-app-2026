package io.github.droidkaigi.confsched.feature.doodle.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiSegmentedButton
import io.github.droidkaigi.confsched.core.ui.KaigiSingleChoiceSegmentedButtonRow
import io.github.droidkaigi.confsched.feature.doodle.DoodleCardFace
import io.github.droidkaigi.confsched.feature.doodle.generated.resources.Res
import io.github.droidkaigi.confsched.feature.doodle.generated.resources.doodle_face_back
import io.github.droidkaigi.confsched.feature.doodle.generated.resources.doodle_face_front
import org.jetbrains.compose.resources.stringResource

/** Picks the card face a single canvas shows, where the window is too narrow to show both. */
@Composable
internal fun DoodleFaceSwitchRow(
    selectedFace: DoodleCardFace,
    onFaceClick: (DoodleCardFace) -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentColor = MaterialTheme.colorScheme.primary
    KaigiSingleChoiceSegmentedButtonRow(
        outlineSeed = FACE_SWITCH_SEED,
        modifier = modifier,
        borderColor = contentColor,
    ) {
        KaigiSegmentedButton(
            selected = selectedFace == DoodleCardFace.Front,
            onClick = { onFaceClick(DoodleCardFace.Front) },
            dividerSeed = FACE_SWITCH_DIVIDER_SEED,
            selectedContainerColor = contentColor,
            selectedContentColor = MaterialTheme.colorScheme.onPrimary,
            contentColor = contentColor,
        ) {
            Text(stringResource(Res.string.doodle_face_front), style = MaterialTheme.typography.labelLarge)
        }
        KaigiSegmentedButton(
            selected = selectedFace == DoodleCardFace.Back,
            onClick = { onFaceClick(DoodleCardFace.Back) },
            dividerSeed = null,
            leadingDividerSeed = FACE_SWITCH_DIVIDER_SEED,
            selectedContainerColor = contentColor,
            selectedContentColor = MaterialTheme.colorScheme.onPrimary,
            contentColor = contentColor,
        ) {
            Text(stringResource(Res.string.doodle_face_back), style = MaterialTheme.typography.labelLarge)
        }
    }
}

private const val FACE_SWITCH_SEED = 4331
private const val FACE_SWITCH_DIVIDER_SEED = 4332

@LocalePreviews
@Composable
private fun DoodleFaceSwitchRowPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DoodleFaceSwitchRow(selectedFace = DoodleCardFace.Front, onFaceClick = {})
    }
}

@LocalePreviews
@Composable
private fun DoodleFaceSwitchRowBackSelectedPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DoodleFaceSwitchRow(selectedFace = DoodleCardFace.Back, onFaceClick = {})
    }
}
