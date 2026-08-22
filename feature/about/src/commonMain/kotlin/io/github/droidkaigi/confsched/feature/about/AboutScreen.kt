package io.github.droidkaigi.confsched.feature.about

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.icon.Award
import io.github.droidkaigi.confsched.core.designsystem.icon.Build
import io.github.droidkaigi.confsched.core.designsystem.icon.Description
import io.github.droidkaigi.confsched.core.designsystem.icon.Gavel
import io.github.droidkaigi.confsched.core.designsystem.icon.Groups
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.designsystem.icon.Person
import io.github.droidkaigi.confsched.core.designsystem.icon.PrivacyTip
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocaleScreenPreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiTopAppBar
import io.github.droidkaigi.confsched.core.ui.LocalNavigationBarOccupiedHeight
import io.github.droidkaigi.confsched.feature.about.component.AboutEventCard
import io.github.droidkaigi.confsched.feature.about.component.AboutNavigationRow
import io.github.droidkaigi.confsched.feature.about.generated.resources.Res
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_android_trademark
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_description
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_event_date
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_logo_description
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_social_medium
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_social_x
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_social_youtube
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_venue
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_view_map
import io.github.droidkaigi.confsched.feature.about.generated.resources.code_of_conduct
import io.github.droidkaigi.confsched.feature.about.generated.resources.contributors
import io.github.droidkaigi.confsched.feature.about.generated.resources.credits
import io.github.droidkaigi.confsched.feature.about.generated.resources.debug_menu
import io.github.droidkaigi.confsched.feature.about.generated.resources.debug_menu_description
import io.github.droidkaigi.confsched.feature.about.generated.resources.licenses
import io.github.droidkaigi.confsched.feature.about.generated.resources.licenses_description
import io.github.droidkaigi.confsched.feature.about.generated.resources.links
import io.github.droidkaigi.confsched.feature.about.generated.resources.others
import io.github.droidkaigi.confsched.feature.about.generated.resources.privacy_policy
import io.github.droidkaigi.confsched.feature.about.generated.resources.sponsors
import io.github.droidkaigi.confsched.feature.about.generated.resources.staff
import io.github.droidkaigi.confsched.feature.about.generated.resources.version
import org.jetbrains.compose.resources.stringResource

// Links marks are third-party brand icons on a literal white ground with a fixed dark label, so
// they stay legible under every colour scheme (md3/onPrimary inverts to dark under two of them).
private val LinksGround = Color.White
private val LinksLabel = Color(0xFF11151C)

@Composable
fun AboutScreen(
    uiState: AboutScreenUiState,
    onOpenEventMap: () -> Unit,
    onOpenSponsors: () -> Unit,
    onOpenContributors: () -> Unit,
    onOpenStaff: () -> Unit,
    onOpenLicenses: () -> Unit,
    onOpenCodeOfConduct: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onOpenYoutube: () -> Unit,
    onOpenX: () -> Unit,
    onOpenMedium: () -> Unit,
    isDebugMenuAvailable: Boolean,
    onOpenDebug: () -> Unit,
) {
    // The navigation bar floats over the content, so the scroll reserves its room at the bottom.
    val navigationBarHeight = LocalNavigationBarOccupiedHeight.current
    Scaffold(
        topBar = { KaigiTopAppBar(title = uiState.title) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            // Placeholder for the hand-drawn hero (logo, mascot, wavy bottom edge); see PR description
            // for the asset and the generative spec. The band shares the top bar's colour.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.inverseSurface)
                    .padding(vertical = 48.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(Res.string.about_logo_description),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                )
            }
            Text(
                text = stringResource(Res.string.about_description),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
            )
            AboutEventCard(
                date = stringResource(Res.string.about_event_date),
                venue = stringResource(Res.string.about_venue),
                viewMapLabel = stringResource(Res.string.about_view_map),
                onViewMap = onOpenEventMap,
                modifier = Modifier.padding(all = 16.dp),
            )

            Text(
                text = stringResource(Res.string.credits),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp),
            )
            HorizontalDivider()
            AboutNavigationRow(
                stringResource(Res.string.contributors),
                leadingIcon = KaigiIcons.Default.Groups,
                onClick = onOpenContributors,
            )
            HorizontalDivider()
            AboutNavigationRow(
                stringResource(Res.string.staff),
                leadingIcon = KaigiIcons.Default.Person,
                onClick = onOpenStaff,
            )
            HorizontalDivider()
            AboutNavigationRow(
                stringResource(Res.string.sponsors),
                leadingIcon = KaigiIcons.Default.Award,
                onClick = onOpenSponsors,
            )
            HorizontalDivider()

            Text(
                text = stringResource(Res.string.others),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp),
            )
            HorizontalDivider()
            AboutNavigationRow(
                stringResource(Res.string.code_of_conduct),
                leadingIcon = KaigiIcons.Default.Gavel,
                onClick = onOpenCodeOfConduct,
            )
            HorizontalDivider()
            AboutNavigationRow(
                stringResource(Res.string.licenses),
                leadingIcon = KaigiIcons.Default.Description,
                onClick = onOpenLicenses,
                supporting = stringResource(Res.string.licenses_description),
            )
            HorizontalDivider()
            AboutNavigationRow(
                stringResource(Res.string.privacy_policy),
                leadingIcon = KaigiIcons.Default.PrivacyTip,
                onClick = onOpenPrivacyPolicy,
            )
            HorizontalDivider()
            if (isDebugMenuAvailable) {
                AboutNavigationRow(
                    stringResource(Res.string.debug_menu),
                    leadingIcon = KaigiIcons.Default.Build,
                    onClick = onOpenDebug,
                    supporting = stringResource(Res.string.debug_menu_description),
                )
                HorizontalDivider()
            }

            Text(
                text = stringResource(Res.string.links),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp),
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            ) {
                // Placeholder chips for the YouTube / X / Medium brand marks (see PR description).
                Box(
                    modifier = Modifier
                        .clickable(onClick = onOpenYoutube)
                        .background(LinksGround, RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text(text = stringResource(Res.string.about_social_youtube), color = LinksLabel)
                }
                Box(
                    modifier = Modifier
                        .clickable(onClick = onOpenX)
                        .background(LinksGround, RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text(text = stringResource(Res.string.about_social_x), color = LinksLabel)
                }
                Box(
                    modifier = Modifier
                        .clickable(onClick = onOpenMedium)
                        .background(LinksGround, RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text(text = stringResource(Res.string.about_social_medium), color = LinksLabel)
                }
            }
            Text(
                text = "${stringResource(Res.string.version)} ${uiState.versionName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            )
            Text(
                text = stringResource(Res.string.about_android_trademark),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 12.dp, bottom = 24.dp + navigationBarHeight),
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
            uiState = AboutScreenUiState(title = "About DroidKaigi 2026", versionName = "1.0.0"),
            onOpenEventMap = {},
            onOpenSponsors = {},
            onOpenContributors = {},
            onOpenStaff = {},
            onOpenLicenses = {},
            onOpenCodeOfConduct = {},
            onOpenPrivacyPolicy = {},
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
            uiState = AboutScreenUiState(title = "About DroidKaigi 2026", versionName = "1.0.0"),
            onOpenEventMap = {},
            onOpenSponsors = {},
            onOpenContributors = {},
            onOpenStaff = {},
            onOpenLicenses = {},
            onOpenCodeOfConduct = {},
            onOpenPrivacyPolicy = {},
            onOpenYoutube = {},
            onOpenX = {},
            onOpenMedium = {},
            isDebugMenuAvailable = false,
            onOpenDebug = {},
        )
    }
}
