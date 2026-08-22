package io.github.droidkaigi.confsched.feature.profilecard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.SCREEN_PREVIEW_HEIGHT_DP
import io.github.droidkaigi.confsched.core.preview.SCREEN_PREVIEW_WIDTH_DP
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme

@Composable
fun ProfileCardScreen(
    uiState: ProfileCardScreenUiState,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(uiState.title, style = MaterialTheme.typography.headlineSmall)
    }
}

@Preview(widthDp = SCREEN_PREVIEW_WIDTH_DP, heightDp = SCREEN_PREVIEW_HEIGHT_DP)
@Composable
private fun ProfileCardScreenPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardScreen(uiState = ProfileCardScreenUiState(title = "Profile card"))
    }
}
