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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocaleScreenPreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiTopAppBar
import io.github.droidkaigi.confsched.feature.about.component.AboutEventCard
import io.github.droidkaigi.confsched.feature.about.component.AboutNavigationRow
import io.github.droidkaigi.confsched.feature.about.generated.resources.Res
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_description
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_event_date
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_logo_description
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_social_medium
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_social_x
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_social_youtube
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_title
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_venue
import io.github.droidkaigi.confsched.feature.about.generated.resources.code_of_conduct
import io.github.droidkaigi.confsched.feature.about.generated.resources.contributors
import io.github.droidkaigi.confsched.feature.about.generated.resources.credits
import io.github.droidkaigi.confsched.feature.about.generated.resources.debug_menu
import io.github.droidkaigi.confsched.feature.about.generated.resources.debug_menu_description
import io.github.droidkaigi.confsched.feature.about.generated.resources.licenses
import io.github.droidkaigi.confsched.feature.about.generated.resources.licenses_description
import io.github.droidkaigi.confsched.feature.about.generated.resources.others
import io.github.droidkaigi.confsched.feature.about.generated.resources.privacy_policy
import io.github.droidkaigi.confsched.feature.about.generated.resources.sponsors
import io.github.droidkaigi.confsched.feature.about.generated.resources.staff
import io.github.droidkaigi.confsched.feature.about.generated.resources.version
import org.jetbrains.compose.resources.stringResource

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
    Scaffold(
        topBar = { KaigiTopAppBar(title = stringResource(Res.string.about_title)) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            // Placeholder for the conference logo banner; replace with the logo asset (see PR description).
            // The band shares the top bar's colour, so the bar and the banner read as one surface.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.inverseSurface)
                    .padding(vertical = 40.dp),
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
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
            )
            AboutEventCard(
                date = stringResource(Res.string.about_event_date),
                venue = stringResource(Res.string.about_venue),
                onClick = onOpenEventMap,
                modifier = Modifier.padding(all = 16.dp),
            )

            Text(
                text = stringResource(Res.string.credits),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp),
            )
            HorizontalDivider()
            AboutNavigationRow(stringResource(Res.string.contributors), onClick = onOpenContributors)
            HorizontalDivider()
            AboutNavigationRow(stringResource(Res.string.staff), onClick = onOpenStaff)
            HorizontalDivider()
            AboutNavigationRow(stringResource(Res.string.sponsors), onClick = onOpenSponsors)
            HorizontalDivider()

            Text(
                text = stringResource(Res.string.others),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp),
            )
            HorizontalDivider()
            AboutNavigationRow(stringResource(Res.string.code_of_conduct), onClick = onOpenCodeOfConduct)
            HorizontalDivider()
            AboutNavigationRow(
                stringResource(Res.string.licenses),
                onClick = onOpenLicenses,
                supporting = stringResource(Res.string.licenses_description),
            )
            HorizontalDivider()
            AboutNavigationRow(stringResource(Res.string.privacy_policy), onClick = onOpenPrivacyPolicy)
            HorizontalDivider()
            if (isDebugMenuAvailable) {
                AboutNavigationRow(
                    stringResource(Res.string.debug_menu),
                    onClick = onOpenDebug,
                    supporting = stringResource(Res.string.debug_menu_description),
                )
                HorizontalDivider()
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            ) {
                Text(
                    text = stringResource(Res.string.about_social_youtube),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable(onClick = onOpenYoutube).padding(8.dp),
                )
                Text(
                    text = stringResource(Res.string.about_social_x),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable(onClick = onOpenX).padding(8.dp),
                )
                Text(
                    text = stringResource(Res.string.about_social_medium),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable(onClick = onOpenMedium).padding(8.dp),
                )
            }
            Text(
                text = "${stringResource(Res.string.version)} ${uiState.versionName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
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
            uiState = AboutScreenUiState(versionName = "1.0.0"),
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
            uiState = AboutScreenUiState(versionName = "1.0.0"),
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
