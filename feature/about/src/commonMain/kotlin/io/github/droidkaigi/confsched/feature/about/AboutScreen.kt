package io.github.droidkaigi.confsched.feature.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.icon.Award
import io.github.droidkaigi.confsched.core.designsystem.icon.Build
import io.github.droidkaigi.confsched.core.designsystem.icon.FileCopy
import io.github.droidkaigi.confsched.core.designsystem.icon.Gavel
import io.github.droidkaigi.confsched.core.designsystem.icon.Groups
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.designsystem.icon.Person
import io.github.droidkaigi.confsched.core.designsystem.icon.PrivacyTip
import io.github.droidkaigi.confsched.core.designsystem.icon.Settings
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocaleScreenPreviews
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiTopAppBar
import io.github.droidkaigi.confsched.core.ui.LocalNavigationBarOccupiedHeight
import io.github.droidkaigi.confsched.feature.about.component.AboutEventCard
import io.github.droidkaigi.confsched.feature.about.component.AboutFooter
import io.github.droidkaigi.confsched.feature.about.component.AboutHero
import io.github.droidkaigi.confsched.feature.about.component.AboutNavigationRow
import io.github.droidkaigi.confsched.feature.about.component.AboutRowDivider
import io.github.droidkaigi.confsched.feature.about.component.AboutSectionHeader
import io.github.droidkaigi.confsched.feature.about.generated.resources.Res
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_description
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_event_date
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_title
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_venue
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_view_map
import io.github.droidkaigi.confsched.feature.about.generated.resources.code_of_conduct
import io.github.droidkaigi.confsched.feature.about.generated.resources.contributors
import io.github.droidkaigi.confsched.feature.about.generated.resources.credits
import io.github.droidkaigi.confsched.feature.about.generated.resources.debug_menu
import io.github.droidkaigi.confsched.feature.about.generated.resources.ic_about_credits
import io.github.droidkaigi.confsched.feature.about.generated.resources.ic_about_others
import io.github.droidkaigi.confsched.feature.about.generated.resources.licenses
import io.github.droidkaigi.confsched.feature.about.generated.resources.others
import io.github.droidkaigi.confsched.feature.about.generated.resources.privacy_policy
import io.github.droidkaigi.confsched.feature.about.generated.resources.settings
import io.github.droidkaigi.confsched.feature.about.generated.resources.sponsors
import io.github.droidkaigi.confsched.feature.about.generated.resources.staff
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
    onOpenDoodle: () -> Unit,
    onOpenYoutube: () -> Unit,
    onOpenX: () -> Unit,
    onOpenMedium: () -> Unit,
    isDebugMenuAvailable: Boolean,
    onOpenDebug: () -> Unit,
) {
    // The navigation bar floats over the content, so the scroll reserves its room at the bottom.
    val navigationBarHeight = LocalNavigationBarOccupiedHeight.current
    Scaffold(
        topBar = { KaigiTopAppBar(title = stringResource(Res.string.about_title)) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            AboutHero(doodle = uiState.doodle, onEditDoodleClick = onOpenDoodle)
            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = stringResource(Res.string.about_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 44.dp),
            )
            Spacer(modifier = Modifier.height(12.dp))
            AboutEventCard(
                date = stringResource(Res.string.about_event_date),
                venue = stringResource(Res.string.about_venue),
                viewMapLabel = stringResource(Res.string.about_view_map),
                onViewMap = onOpenVenueWithMap,
                modifier = Modifier.padding(horizontal = 44.dp),
            )
            Spacer(modifier = Modifier.height(40.dp))

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                AboutSectionHeader(
                    title = stringResource(Res.string.credits),
                    icon = Res.drawable.ic_about_credits,
                )
                AboutNavigationRow(
                    stringResource(Res.string.contributors),
                    leadingIcon = KaigiIcons.Default.Groups,
                    onClick = onOpenContributors,
                )
                AboutRowDivider(seed = 1)
                AboutNavigationRow(
                    stringResource(Res.string.staff),
                    leadingIcon = KaigiIcons.Default.Person,
                    onClick = onOpenStaff,
                )
                AboutRowDivider(seed = 2)
                AboutNavigationRow(
                    stringResource(Res.string.sponsors),
                    leadingIcon = KaigiIcons.Default.Award,
                    onClick = onOpenSponsors,
                )

                Spacer(modifier = Modifier.height(24.dp))
                AboutSectionHeader(
                    title = stringResource(Res.string.others),
                    icon = Res.drawable.ic_about_others,
                )
                AboutNavigationRow(
                    stringResource(Res.string.code_of_conduct),
                    leadingIcon = KaigiIcons.Default.Gavel,
                    onClick = onOpenCodeOfConduct,
                )
                AboutRowDivider(seed = 3)
                AboutNavigationRow(
                    stringResource(Res.string.licenses),
                    leadingIcon = KaigiIcons.Default.FileCopy,
                    onClick = onOpenLicenses,
                )
                AboutRowDivider(seed = 4)
                AboutNavigationRow(
                    stringResource(Res.string.privacy_policy),
                    leadingIcon = KaigiIcons.Default.PrivacyTip,
                    onClick = onOpenPrivacyPolicy,
                )
                AboutRowDivider(seed = 5)
                AboutNavigationRow(
                    stringResource(Res.string.settings),
                    leadingIcon = KaigiIcons.Default.Settings,
                    onClick = onOpenSettings,
                )
                if (isDebugMenuAvailable) {
                    AboutRowDivider(seed = 6)
                    AboutNavigationRow(
                        stringResource(Res.string.debug_menu),
                        leadingIcon = KaigiIcons.Default.Build,
                        onClick = onOpenDebug,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                AboutFooter(
                    versionName = uiState.versionName,
                    onOpenYoutube = onOpenYoutube,
                    onOpenX = onOpenX,
                    onOpenMedium = onOpenMedium,
                    modifier = Modifier.padding(bottom = navigationBarHeight),
                )
            }
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
            uiState = AboutScreenUiState(versionName = "1.0.0", doodle = Doodle.Empty),
            onOpenVenueWithMap = {},
            onOpenSponsors = {},
            onOpenContributors = {},
            onOpenStaff = {},
            onOpenLicenses = {},
            onOpenCodeOfConduct = {},
            onOpenPrivacyPolicy = {},
            onOpenSettings = {},
            onOpenDoodle = {},
            onOpenYoutube = {},
            onOpenX = {},
            onOpenMedium = {},
            isDebugMenuAvailable = true,
            onOpenDebug = {},
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
            uiState = AboutScreenUiState(versionName = "1.0.0", doodle = Doodle.Empty),
            onOpenVenueWithMap = {},
            onOpenSponsors = {},
            onOpenContributors = {},
            onOpenStaff = {},
            onOpenLicenses = {},
            onOpenCodeOfConduct = {},
            onOpenPrivacyPolicy = {},
            onOpenSettings = {},
            onOpenDoodle = {},
            onOpenYoutube = {},
            onOpenX = {},
            onOpenMedium = {},
            isDebugMenuAvailable = false,
            onOpenDebug = {},
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
            uiState = AboutScreenUiState(versionName = "1.0.0", doodle = Doodle.fake()),
            onOpenVenueWithMap = {},
            onOpenSponsors = {},
            onOpenContributors = {},
            onOpenStaff = {},
            onOpenLicenses = {},
            onOpenCodeOfConduct = {},
            onOpenPrivacyPolicy = {},
            onOpenSettings = {},
            onOpenDoodle = {},
            onOpenYoutube = {},
            onOpenX = {},
            onOpenMedium = {},
            isDebugMenuAvailable = true,
            onOpenDebug = {},
        )
    }
}
