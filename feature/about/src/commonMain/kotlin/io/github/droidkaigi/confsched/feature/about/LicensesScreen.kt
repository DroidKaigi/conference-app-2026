package io.github.droidkaigi.confsched.feature.about

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.libraryColors
import com.mikepenz.aboutlibraries.ui.compose.m3.style.m3VariantColors
import com.mikepenz.aboutlibraries.ui.compose.style.LicenseHueResolver
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocaleScreenPreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiLargeTopAppBar
import io.github.droidkaigi.confsched.core.ui.paneStartInset
import io.github.droidkaigi.confsched.core.ui.plus
import io.github.droidkaigi.confsched.core.ui.rememberListDetailSceneAwareLazyListState
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
            lazyListState = rememberListDetailSceneAwareLazyListState(),
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding + PaddingValues(start = paneStartInset()),
            colors = LicensesScreenDefaults.libraryColors(),
            variantColors = LicensesScreenDefaults.variantColors(),
        )
    }
}

private object LicensesScreenDefaults {
    // The library's default badge and row colors do not follow this app's color schemes.
    @Composable
    fun variantColors() = with(MaterialTheme.colorScheme) {
        LibraryDefaults.m3VariantColors(
            rowBackground = surface,
            rowExpandedBackground = surfaceContainerLow,
            rowOnBackground = onSurface,
            rowSubtleContent = onSurfaceVariant,
            rowDivider = outlineVariant,
            licenseHueResolver = LicenseHueResolver { primary },
        )
    }

    // Only the license dialog reads these; its defaults do not follow this app's color schemes.
    @Composable
    fun libraryColors() = with(MaterialTheme.colorScheme) {
        LibraryDefaults.libraryColors(
            dialogBackgroundColor = surfaceContainerHigh,
            dialogContentColor = onSurface,
            dialogConfirmButtonColor = primary,
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
