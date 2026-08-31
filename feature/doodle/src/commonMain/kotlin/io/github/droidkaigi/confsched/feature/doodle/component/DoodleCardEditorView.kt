package io.github.droidkaigi.confsched.feature.doodle.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.fakeOnCardFace
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiButton
import io.github.droidkaigi.confsched.core.ui.KaigiButtonDefaults
import io.github.droidkaigi.confsched.core.ui.isExpandedWindowWidth
import io.github.droidkaigi.confsched.feature.doodle.DoodleCardFace
import io.github.droidkaigi.confsched.feature.doodle.DoodleEditContentPadding
import io.github.droidkaigi.confsched.feature.doodle.DoodleEditSectionSpacing
import io.github.droidkaigi.confsched.feature.doodle.DoodleScreenUiState
import io.github.droidkaigi.confsched.feature.doodle.PlaceholderProfileCard
import io.github.droidkaigi.confsched.feature.doodle.generated.resources.Res
import io.github.droidkaigi.confsched.feature.doodle.generated.resources.doodle_save
import org.jetbrains.compose.resources.stringResource

/**
 * Both card faces edited in one visit. A window wide enough shows them side by side; a narrower one
 * shows the selected face alone, and the strokes of the face out of view stay in this composition,
 * so switching back and forth loses nothing. Save writes the pair.
 */
@Composable
internal fun DoodleCardEditorView(
    uiState: DoodleScreenUiState.Card,
    onSaveClick: (Doodle, Doodle) -> Unit,
    modifier: Modifier = Modifier,
) {
    val savedFrontStrokes = uiState.frontDoodle.strokes
    val savedBackStrokes = uiState.backDoodle.strokes
    val frontStrokes = remember(savedFrontStrokes, savedFrontStrokes::toMutableStateList)
    val backStrokes = remember(savedBackStrokes, savedBackStrokes::toMutableStateList)
    var selectedFace by remember(uiState.initialFace) { mutableStateOf(uiState.initialFace) }
    BoxWithConstraints(modifier = modifier) {
        val sideBySide = maxWidth.isExpandedWindowWidth
        Column(
            modifier = Modifier.fillMaxSize().padding(DoodleEditContentPadding),
            verticalArrangement = Arrangement.spacedBy(DoodleEditSectionSpacing),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (sideBySide) {
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = DoodleEditSectionSpacing,
                        alignment = Alignment.CenterHorizontally,
                    ),
                ) {
                    DoodleCardFaceEditorView(
                        face = DoodleCardFace.Front,
                        doodle = Doodle(strokes = frontStrokes.toList()),
                        card = uiState.card,
                        onStrokeAdd = { frontStrokes += it },
                        onUndoClick = { frontStrokes.removeAt(frontStrokes.lastIndex) },
                        onClearClick = frontStrokes::clear,
                        modifier = Modifier.weight(1f),
                    )
                    DoodleCardFaceEditorView(
                        face = DoodleCardFace.Back,
                        doodle = Doodle(strokes = backStrokes.toList()),
                        card = uiState.card,
                        onStrokeAdd = { backStrokes += it },
                        onUndoClick = { backStrokes.removeAt(backStrokes.lastIndex) },
                        onClearClick = backStrokes::clear,
                        modifier = Modifier.weight(1f),
                    )
                }
            } else {
                DoodleFaceSwitchRow(selectedFace = selectedFace, onFaceClick = { selectedFace = it })
                val strokes = if (selectedFace == DoodleCardFace.Front) frontStrokes else backStrokes
                DoodleCardFaceEditorView(
                    face = selectedFace,
                    doodle = Doodle(strokes = strokes.toList()),
                    card = uiState.card,
                    onStrokeAdd = { strokes += it },
                    onUndoClick = { strokes.removeAt(strokes.lastIndex) },
                    onClearClick = strokes::clear,
                    modifier = Modifier.fillMaxWidth().weight(1f),
                )
            }
            KaigiButton(
                onClick = {
                    onSaveClick(
                        Doodle(strokes = frontStrokes.toList()),
                        Doodle(strokes = backStrokes.toList()),
                    )
                },
                seed = SAVE_BUTTON_SEED,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving,
            ) {
                Text(stringResource(Res.string.doodle_save), style = KaigiButtonDefaults.labelStyle)
            }
        }
    }
}

private const val SAVE_BUTTON_SEED = 4314

@LocalePreviews
@Composable
private fun DoodleCardEditorViewPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DoodleCardEditorView(
            uiState = DoodleScreenUiState.Card(
                frontDoodle = Doodle.fakeOnCardFace(),
                backDoodle = Doodle.Empty,
                initialFace = DoodleCardFace.Front,
                card = PlaceholderProfileCard,
                isSaving = false,
            ),
            onSaveClick = { _, _ -> },
            modifier = Modifier.fillMaxSize(),
        )
    }
}
