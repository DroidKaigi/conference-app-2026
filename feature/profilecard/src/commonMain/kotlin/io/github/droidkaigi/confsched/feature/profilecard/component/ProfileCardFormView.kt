package io.github.droidkaigi.confsched.feature.profilecard.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.feature.profilecard.ProfileCardScreenUiState

@Composable
fun ProfileCardFormView(
    uiState: ProfileCardScreenUiState.Form,
    onNickNameChange: (String) -> Unit,
    onOccupationChange: (String) -> Unit,
    onLinkChange: (String) -> Unit,
    onMascotSelected: (Mascot) -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedTextField(
            value = uiState.nickName,
            onValueChange = onNickNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nickname") },
            singleLine = true,
        )
        OutlinedTextField(
            value = uiState.occupation,
            onValueChange = onOccupationChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Occupation") },
            singleLine = true,
        )
        OutlinedTextField(
            value = uiState.link,
            onValueChange = onLinkChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Link") },
            singleLine = true,
        )
        Text("Mascot", style = MaterialTheme.typography.labelLarge)
        MascotPicker(selected = uiState.mascot, onMascotSelected = onMascotSelected)
        Button(onClick = onSubmitClick) {
            Text("Create")
        }
    }
}

@Preview
@Composable
private fun ProfileCardFormViewPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardFormView(
            uiState = ProfileCardScreenUiState.Form(),
            onNickNameChange = {},
            onOccupationChange = {},
            onLinkChange = {},
            onMascotSelected = {},
            onSubmitClick = {},
        )
    }
}
