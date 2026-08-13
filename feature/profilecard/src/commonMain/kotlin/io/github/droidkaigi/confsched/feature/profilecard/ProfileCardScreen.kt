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
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme

@Composable
fun ProfileCardScreen(
    uiState: ProfileCardScreenUiState,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when (uiState) {
            is ProfileCardScreenUiState.Form -> Text("Form", style = MaterialTheme.typography.headlineSmall)
            is ProfileCardScreenUiState.Card -> Text(uiState.nickName, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Preview
@Composable
private fun ProfileCardScreenFormPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardScreen(uiState = ProfileCardScreenUiState.Form())
    }
}

@Preview
@Composable
private fun ProfileCardScreenCardPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardScreen(
            uiState = ProfileCardScreenUiState.Card(
                nickName = "Speaker A",
                occupation = "Software Engineer",
                link = "https://example.com",
            ),
        )
    }
}
