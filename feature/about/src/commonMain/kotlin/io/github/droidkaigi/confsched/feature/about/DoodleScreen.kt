package io.github.droidkaigi.confsched.feature.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme

@Composable
fun DoodleScreen(
    uiState: DoodleScreenUiState,
    onReloadClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(uiState.title)
        Text("Reloaded ${uiState.reloadCount} times")
        Button(onClick = onReloadClick) { Text("Reload") }
        Button(onClick = onBackClick) { Text("Back") }
    }
}

@Preview
@Composable
private fun DoodleScreenPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DoodleScreen(
            uiState = DoodleScreenUiState(title = "Doodle", reloadCount = 0),
            onReloadClick = {},
            onBackClick = {},
        )
    }
}
