package io.github.droidkaigi.confsched.feature.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.github.droidkaigi.confsched.core.common.SystemBackEffect
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocaleScreenPreviews
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiTopAppBar
import io.github.droidkaigi.confsched.feature.about.component.AboutHero
import io.github.droidkaigi.confsched.feature.about.component.AboutPageBodyView
import io.github.droidkaigi.confsched.feature.about.component.AboutWallDoodleEditorView
import io.github.droidkaigi.confsched.feature.about.generated.resources.Res
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun AboutScreen(
    uiState: AboutScreenUiState,
    onOpenVenueWithMap: () -> Unit,
    onOpenSponsors: () -> Unit,
    onOpenContributors: () -> Unit,
    onOpenStaff: () -> Unit,
    onOpenLicenses: () -> Unit,
    onOpenCodeOfConduct: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenYoutube: () -> Unit,
    onOpenX: () -> Unit,
    onOpenMedium: () -> Unit,
    isDebugMenuAvailable: Boolean,
    onOpenDebug: () -> Unit,
    onStartDoodlingClick: () -> Unit,
    onCancelDoodlingClick: () -> Unit,
    onDoodleDoneClick: (Doodle) -> Unit,
) {
    SystemBackEffect(enabled = uiState.isDoodlingWall, onBack = onCancelDoodlingClick)
    Scaffold(
        topBar = { KaigiTopAppBar(title = stringResource(Res.string.about_title)) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState(), enabled = !uiState.isDoodlingWall),
        ) {
            if (uiState.isDoodlingWall) {
                AboutWallDoodleEditorView(savedDoodle = uiState.doodle, onDoneClick = onDoodleDoneClick)
            } else {
                AboutHero(doodle = uiState.doodle, onStartDoodlingClick = onStartDoodlingClick)
            }
            AboutPageBodyView(
                versionName = uiState.versionName,
                isDebugMenuAvailable = isDebugMenuAvailable,
                dimmed = uiState.isDoodlingWall,
                onOpenVenueWithMap = onOpenVenueWithMap,
                onOpenSponsors = onOpenSponsors,
                onOpenContributors = onOpenContributors,
                onOpenStaff = onOpenStaff,
                onOpenLicenses = onOpenLicenses,
                onOpenCodeOfConduct = onOpenCodeOfConduct,
                onOpenPrivacyPolicy = onOpenPrivacyPolicy,
                onOpenSettings = onOpenSettings,
                onOpenDebug = onOpenDebug,
                onOpenYoutube = onOpenYoutube,
                onOpenX = onOpenX,
                onOpenMedium = onOpenMedium,
            )
        }
    }
}

@LocaleScreenPreviews
@Composable
private fun AboutScreenPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        AboutScreen(
            uiState = AboutScreenUiState(versionName = "1.0.0", doodle = Doodle.Empty, isDoodlingWall = false),
            onOpenVenueWithMap = {},
            onOpenSponsors = {},
            onOpenContributors = {},
            onOpenStaff = {},
            onOpenLicenses = {},
            onOpenCodeOfConduct = {},
            onOpenPrivacyPolicy = {},
            onOpenSettings = {},
            onOpenYoutube = {},
            onOpenX = {},
            onOpenMedium = {},
            isDebugMenuAvailable = true,
            onOpenDebug = {},
            onStartDoodlingClick = {},
            onCancelDoodlingClick = {},
            onDoodleDoneClick = {},
        )
    }
}

@LocaleScreenPreviews
@Composable
private fun AboutScreenWithoutDebugMenuPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        AboutScreen(
            uiState = AboutScreenUiState(versionName = "1.0.0", doodle = Doodle.Empty, isDoodlingWall = false),
            onOpenVenueWithMap = {},
            onOpenSponsors = {},
            onOpenContributors = {},
            onOpenStaff = {},
            onOpenLicenses = {},
            onOpenCodeOfConduct = {},
            onOpenPrivacyPolicy = {},
            onOpenSettings = {},
            onOpenYoutube = {},
            onOpenX = {},
            onOpenMedium = {},
            isDebugMenuAvailable = false,
            onOpenDebug = {},
            onStartDoodlingClick = {},
            onCancelDoodlingClick = {},
            onDoodleDoneClick = {},
        )
    }
}

@LocaleScreenPreviews
@Composable
private fun AboutScreenWithDoodlePreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        AboutScreen(
            uiState = AboutScreenUiState(versionName = "1.0.0", doodle = Doodle.fake(), isDoodlingWall = false),
            onOpenVenueWithMap = {},
            onOpenSponsors = {},
            onOpenContributors = {},
            onOpenStaff = {},
            onOpenLicenses = {},
            onOpenCodeOfConduct = {},
            onOpenPrivacyPolicy = {},
            onOpenSettings = {},
            onOpenYoutube = {},
            onOpenX = {},
            onOpenMedium = {},
            isDebugMenuAvailable = true,
            onOpenDebug = {},
            onStartDoodlingClick = {},
            onCancelDoodlingClick = {},
            onDoodleDoneClick = {},
        )
    }
}

@LocaleScreenPreviews
@Composable
private fun AboutScreenDoodlingPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        AboutScreen(
            uiState = AboutScreenUiState(versionName = "1.0.0", doodle = Doodle.fake(), isDoodlingWall = true),
            onOpenVenueWithMap = {},
            onOpenSponsors = {},
            onOpenContributors = {},
            onOpenStaff = {},
            onOpenLicenses = {},
            onOpenCodeOfConduct = {},
            onOpenPrivacyPolicy = {},
            onOpenSettings = {},
            onOpenYoutube = {},
            onOpenX = {},
            onOpenMedium = {},
            isDebugMenuAvailable = true,
            onOpenDebug = {},
            onStartDoodlingClick = {},
            onCancelDoodlingClick = {},
            onDoodleDoneClick = {},
        )
    }
}
