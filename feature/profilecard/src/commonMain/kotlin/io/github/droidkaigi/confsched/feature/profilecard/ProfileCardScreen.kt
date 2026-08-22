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
import io.github.droidkaigi.confsched.feature.profilecard.component.Mascot
import io.github.droidkaigi.confsched.feature.profilecard.component.ProfileCardFormView
import io.github.droidkaigi.confsched.feature.profilecard.component.SketchIntensity

@Composable
fun ProfileCardScreen(
    uiState: ProfileCardScreenUiState,
    onNickNameChange: (String) -> Unit,
    onOccupationChange: (String) -> Unit,
    onLinkChange: (String) -> Unit,
    onMascotSelected: (Mascot) -> Unit,
    onSketchIntensitySelected: (SketchIntensity) -> Unit,
    onSubmitClick: () -> Unit,
) {
    when (uiState) {
        is ProfileCardScreenUiState.Form -> ProfileCardFormView(
            uiState = uiState,
            onNickNameChange = onNickNameChange,
            onOccupationChange = onOccupationChange,
            onLinkChange = onLinkChange,
            onMascotSelected = onMascotSelected,
            onSketchIntensitySelected = onSketchIntensitySelected,
            onSubmitClick = onSubmitClick,
        )

        is ProfileCardScreenUiState.Card -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(uiState.nickName, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Preview
@Composable
private fun ProfileCardScreenFormPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardScreen(
            uiState = ProfileCardScreenUiState.Form(),
            onNickNameChange = {},
            onOccupationChange = {},
            onLinkChange = {},
            onMascotSelected = {},
            onSketchIntensitySelected = {},
            onSubmitClick = {},
        )
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
                mascot = Mascot.Koala,
                sketchIntensity = SketchIntensity.Normal,
            ),
            onNickNameChange = {},
            onOccupationChange = {},
            onLinkChange = {},
            onMascotSelected = {},
            onSketchIntensitySelected = {},
            onSubmitClick = {},
        )
    }
}
