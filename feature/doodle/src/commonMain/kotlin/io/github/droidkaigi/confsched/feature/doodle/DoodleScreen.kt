package io.github.droidkaigi.confsched.feature.doodle

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.ProfileCard
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.LocaleScreenPreviews
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.preview.fakeOnCardFace
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiLargeTopAppBar
import io.github.droidkaigi.confsched.feature.doodle.component.DoodleCardEditorView
import io.github.droidkaigi.confsched.feature.doodle.component.DoodleWallEditorView
import io.github.droidkaigi.confsched.feature.doodle.generated.resources.Res
import io.github.droidkaigi.confsched.feature.doodle.generated.resources.doodle_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun DoodleScreen(
    uiState: DoodleScreenUiState,
    onSaveWallClick: (Doodle) -> Unit,
    onSaveCardClick: (Doodle, Doodle) -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            KaigiLargeTopAppBar(title = stringResource(Res.string.doodle_title), onBackClick = onBackClick)
        },
    ) { innerPadding ->
        val contentModifier = Modifier.fillMaxSize().padding(innerPadding)
        when (uiState) {
            is DoodleScreenUiState.Wall -> DoodleWallEditorView(
                uiState = uiState,
                onSaveClick = onSaveWallClick,
                modifier = contentModifier,
            )

            is DoodleScreenUiState.Card -> DoodleCardEditorView(
                uiState = uiState,
                onSaveClick = onSaveCardClick,
                modifier = contentModifier,
            )
        }
    }
}

// A screen preview names its own frame; the expanded layout needs one wider than a phone's.
private val ExpandedScreenPreviewSize = DpSize(width = 1000.dp, height = 800.dp)

@LocaleScreenPreviews
@Composable
private fun DoodleScreenPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DoodleScreen(
            uiState = DoodleScreenUiState.Wall(savedDoodle = Doodle.fake(), isSaving = false),
            onSaveWallClick = {},
            onSaveCardClick = { _, _ -> },
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
            uiState = DoodleScreenUiState.Wall(savedDoodle = Doodle.Empty, isSaving = false),
            onSaveWallClick = {},
            onSaveCardClick = { _, _ -> },
            onBackClick = {},
        )
    }
}

@LocaleScreenPreviews
@Composable
private fun DoodleScreenCardFrontPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DoodleScreen(
            uiState = cardUiState(DoodleCardFace.Front),
            onSaveWallClick = {},
            onSaveCardClick = { _, _ -> },
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
            uiState = cardUiState(DoodleCardFace.Back),
            onSaveWallClick = {},
            onSaveCardClick = { _, _ -> },
            onBackClick = {},
        )
    }
}

@LocalePreviews
@Composable
private fun DoodleScreenCardSideBySidePreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        Box(modifier = Modifier.size(ExpandedScreenPreviewSize)) {
            DoodleScreen(
                uiState = cardUiState(DoodleCardFace.Front),
                onSaveWallClick = {},
                onSaveCardClick = { _, _ -> },
                onBackClick = {},
            )
        }
    }
}

private fun cardUiState(initialFace: DoodleCardFace) = DoodleScreenUiState.Card(
    frontDoodle = Doodle.fakeOnCardFace(),
    backDoodle = Doodle.fakeOnCardFace(),
    initialFace = initialFace,
    card = ProfileCard.fake(),
    isSaving = false,
)
