package io.github.droidkaigi.confsched.feature.profilecard.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiButton
import io.github.droidkaigi.confsched.core.ui.KaigiButtonDefaults
import io.github.droidkaigi.confsched.core.ui.KaigiOutlinedButton
import io.github.droidkaigi.confsched.feature.profilecard.ProfileCardScreenUiState
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.Res
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.add_image_button
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.create_card_button
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.link_label
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.link_placeholder
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.mascot_label
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.nickname_label
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.nickname_placeholder
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.occupation_label
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.occupation_placeholder
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.profile_image_label
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.sketchiness_label
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.subtitle
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileCardFormView(
    uiState: ProfileCardScreenUiState.Form,
    onNickNameChange: (String) -> Unit,
    onOccupationChange: (String) -> Unit,
    onLinkChange: (String) -> Unit,
    onMascotSelected: (Mascot) -> Unit,
    onSketchIntensitySelected: (SketchIntensity) -> Unit,
    onAddImageClick: () -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(stringResource(Res.string.subtitle), style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = uiState.nickName,
            onValueChange = onNickNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(Res.string.nickname_label)) },
            placeholder = { Text(stringResource(Res.string.nickname_placeholder)) },
            singleLine = true,
        )
        OutlinedTextField(
            value = uiState.occupation,
            onValueChange = onOccupationChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(Res.string.occupation_label)) },
            placeholder = { Text(stringResource(Res.string.occupation_placeholder)) },
            singleLine = true,
        )
        OutlinedTextField(
            value = uiState.link,
            onValueChange = onLinkChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(Res.string.link_label)) },
            placeholder = { Text(stringResource(Res.string.link_placeholder)) },
            singleLine = true,
        )
        Text(stringResource(Res.string.profile_image_label), style = MaterialTheme.typography.labelLarge)
        KaigiOutlinedButton(onClick = onAddImageClick, seed = ProfileCardFormViewDefaults.addImageButtonSeed) {
            Icon(
                imageVector = if (uiState.hasAvatarImage) Icons.Filled.Check else Icons.Filled.Add,
                contentDescription = null,
                modifier = Modifier.size(KaigiButtonDefaults.iconSize),
            )
            Text(stringResource(Res.string.add_image_button), style = KaigiButtonDefaults.labelStyle)
        }
        Text(stringResource(Res.string.mascot_label), style = MaterialTheme.typography.labelLarge)
        MascotPicker(selected = uiState.mascot, onMascotSelected = onMascotSelected)
        Text(stringResource(Res.string.sketchiness_label), style = MaterialTheme.typography.labelLarge)
        SketchIntensityPicker(selected = uiState.sketchIntensity, onSketchIntensitySelected = onSketchIntensitySelected)
        KaigiButton(
            onClick = onSubmitClick,
            seed = ProfileCardFormViewDefaults.submitButtonSeed,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(Res.string.create_card_button), style = KaigiButtonDefaults.labelStyle)
        }
    }
}

private object ProfileCardFormViewDefaults {
    val addImageButtonSeed = 710
    val submitButtonSeed = 720
}

@LocalePreviews
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
            onSketchIntensitySelected = {},
            onAddImageClick = {},
            onSubmitClick = {},
        )
    }
}
