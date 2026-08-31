package io.github.droidkaigi.confsched.feature.doodle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodleTarget
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocaleScreenPreviews
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.preview.fakeOnCardFace
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.DoodleCanvasView
import io.github.droidkaigi.confsched.core.ui.KaigiButton
import io.github.droidkaigi.confsched.core.ui.KaigiButtonDefaults
import io.github.droidkaigi.confsched.core.ui.KaigiLargeTopAppBar
import io.github.droidkaigi.confsched.core.ui.KaigiOutlinedButton
import io.github.droidkaigi.confsched.feature.doodle.component.DoodleOverlayView
import io.github.droidkaigi.confsched.feature.doodle.component.DoodleUnderlayView
import io.github.droidkaigi.confsched.feature.doodle.generated.resources.Res
import io.github.droidkaigi.confsched.feature.doodle.generated.resources.doodle_clear
import io.github.droidkaigi.confsched.feature.doodle.generated.resources.doodle_save
import io.github.droidkaigi.confsched.feature.doodle.generated.resources.doodle_title
import io.github.droidkaigi.confsched.feature.doodle.generated.resources.doodle_undo
import org.jetbrains.compose.resources.stringResource

@Composable
fun DoodleScreen(
    uiState: DoodleScreenUiState,
    onSaveClick: (Doodle) -> Unit,
    onBackClick: () -> Unit,
) {
    // The strokes being edited are transient: only Save hands them to the data layer, so the
    // saved doodle is what an edit starts from again.
    val savedStrokes = uiState.savedDoodle.strokes
    val strokes = remember(savedStrokes, savedStrokes::toMutableStateList)
    Scaffold(
        topBar = {
            KaigiLargeTopAppBar(title = stringResource(Res.string.doodle_title), onBackClick = onBackClick)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DoodleCanvasView(
                doodle = Doodle(strokes = strokes.toList()),
                referenceSize = uiState.target.referenceSize,
                origin = uiState.target.doodleOrigin,
                inkColor = uiState.target.inkColor,
                haloColor = uiState.target.haloColor,
                onStrokeAdd = { strokes += it },
                modifier = Modifier.fillMaxWidth().weight(1f),
                background = { scale ->
                    DoodleUnderlayView(
                        target = uiState.target,
                        card = uiState.card,
                        scale = scale,
                        modifier = Modifier.matchParentSize(),
                    )
                },
            ) { scale ->
                DoodleOverlayView(
                    target = uiState.target,
                    card = uiState.card,
                    scale = scale,
                    modifier = Modifier.matchParentSize(),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                KaigiOutlinedButton(
                    onClick = { strokes.removeAt(strokes.lastIndex) },
                    seed = UNDO_BUTTON_SEED,
                    modifier = Modifier.weight(1f),
                    enabled = strokes.isNotEmpty(),
                ) {
                    Text(stringResource(Res.string.doodle_undo), style = KaigiButtonDefaults.labelStyle)
                }
                KaigiOutlinedButton(
                    onClick = strokes::clear,
                    seed = CLEAR_BUTTON_SEED,
                    modifier = Modifier.weight(1f),
                    enabled = strokes.isNotEmpty(),
                ) {
                    Text(stringResource(Res.string.doodle_clear), style = KaigiButtonDefaults.labelStyle)
                }
                KaigiButton(
                    onClick = { onSaveClick(Doodle(strokes = strokes.toList())) },
                    seed = SAVE_BUTTON_SEED,
                    modifier = Modifier.weight(1f),
                    enabled = !uiState.isSaving,
                ) {
                    Text(stringResource(Res.string.doodle_save), style = KaigiButtonDefaults.labelStyle)
                }
            }
        }
    }
}

private const val UNDO_BUTTON_SEED = 4311
private const val CLEAR_BUTTON_SEED = 4312
private const val SAVE_BUTTON_SEED = 4313

@LocaleScreenPreviews
@Composable
private fun DoodleScreenPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DoodleScreen(
            uiState = DoodleScreenUiState(
                target = DoodleTarget.AboutWall,
                savedDoodle = Doodle.fake(),
                card = null,
                isSaving = false,
            ),
            onSaveClick = {},
            onBackClick = {},
        )
    }
}

@LocaleScreenPreviews
@Composable
private fun DoodleScreenEmptyPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DoodleScreen(
            uiState = DoodleScreenUiState(
                target = DoodleTarget.AboutWall,
                savedDoodle = Doodle.Empty,
                card = null,
                isSaving = false,
            ),
            onSaveClick = {},
            onBackClick = {},
        )
    }
}

@LocaleScreenPreviews
@Composable
private fun DoodleScreenCardBackPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DoodleScreen(
            uiState = DoodleScreenUiState(
                target = DoodleTarget.ProfileCardBack,
                savedDoodle = Doodle.fakeOnCardFace(),
                card = PlaceholderProfileCard,
                isSaving = false,
            ),
            onSaveClick = {},
            onBackClick = {},
        )
    }
}
