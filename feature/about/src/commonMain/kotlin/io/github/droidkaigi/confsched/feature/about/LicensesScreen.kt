package io.github.droidkaigi.confsched.feature.about

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocaleScreenPreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiLargeTopAppBar
import io.github.droidkaigi.confsched.feature.about.generated.resources.Res
import io.github.droidkaigi.confsched.feature.about.generated.resources.licenses
import org.jetbrains.compose.resources.stringResource

@Composable
fun LicensesScreen(
    uiState: LicensesScreenUiState,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            KaigiLargeTopAppBar(title = stringResource(Res.string.licenses), onBackClick = onBackClick)
        },
    ) { innerPadding ->
        // AboutLibraries renders the rows, the inline detail and the license dialog, and opens
        // every link it offers through LocalUriHandler.
        LibrariesContainer(
            libraries = uiState.libs,
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding,
        )
    }
}

@LocaleScreenPreviews
@Composable
private fun LicensesScreenPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        LicensesScreen(
            uiState = LicensesScreenUiState.fake(),
            onBackClick = {},
        )
    }
}
