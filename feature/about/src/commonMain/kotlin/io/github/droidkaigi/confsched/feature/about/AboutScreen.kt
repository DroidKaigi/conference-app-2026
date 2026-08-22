package io.github.droidkaigi.confsched.feature.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import io.github.droidkaigi.confsched.core.designsystem.icon.Settings
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocaleScreenPreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiTopAppBar
import io.github.droidkaigi.confsched.core.ui.LocalNavigationBarOccupiedHeight
import io.github.droidkaigi.confsched.feature.about.component.AboutEventCard
import io.github.droidkaigi.confsched.feature.about.component.AboutHeroWaveShape
import io.github.droidkaigi.confsched.feature.about.component.AboutNavigationRow
import io.github.droidkaigi.confsched.feature.about.component.AboutSectionHeader
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
import io.github.droidkaigi.confsched.feature.about.generated.resources.ic_about_credits
import io.github.droidkaigi.confsched.feature.about.generated.resources.ic_about_others
import io.github.droidkaigi.confsched.feature.about.generated.resources.ic_social_medium
import io.github.droidkaigi.confsched.feature.about.generated.resources.ic_social_x
import io.github.droidkaigi.confsched.feature.about.generated.resources.ic_social_youtube
import io.github.droidkaigi.confsched.feature.about.generated.resources.img_about_footer_character
import io.github.droidkaigi.confsched.feature.about.generated.resources.img_about_hero_stage
import io.github.droidkaigi.confsched.feature.about.generated.resources.licenses
import io.github.droidkaigi.confsched.feature.about.generated.resources.licenses_description
import io.github.droidkaigi.confsched.feature.about.generated.resources.others
import io.github.droidkaigi.confsched.feature.about.generated.resources.privacy_policy
import io.github.droidkaigi.confsched.feature.about.generated.resources.settings
import io.github.droidkaigi.confsched.feature.about.generated.resources.sponsors
import io.github.droidkaigi.confsched.feature.about.generated.resources.staff
import io.github.droidkaigi.confsched.feature.about.generated.resources.version
import org.jetbrains.compose.resources.painterResource
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
    onOpenSettings: () -> Unit,
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
            // The hero illustration sits on a band that shares the top bar's colour, so the two read
            // as one surface. Its bottom scallops into the content below; the extra bottom padding is
            // the room the wave occupies. The character stage is fixed at 331dp and stays centred.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(AboutHeroWaveShape(amplitude = 12.dp, wavelength = 104.dp))
                    .background(MaterialTheme.colorScheme.inverseSurface)
                    .padding(top = 24.dp, bottom = 48.dp),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(Res.drawable.img_about_hero_stage),
                    contentDescription = stringResource(Res.string.about_logo_description),
                    modifier = Modifier.fillMaxWidth().widthIn(max = 331.dp),
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

            AboutSectionHeader(
                title = stringResource(Res.string.credits),
                icon = Res.drawable.ic_about_credits,
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

            AboutSectionHeader(
                title = stringResource(Res.string.others),
                icon = Res.drawable.ic_about_others,
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
            AboutNavigationRow(
                stringResource(Res.string.settings),
                leadingIcon = KaigiIcons.Default.Settings,
                onClick = onOpenSettings,
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

            Box(modifier = Modifier.fillMaxWidth().padding(top = 32.dp, bottom = 8.dp)) {
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ic_social_youtube),
                        contentDescription = stringResource(Res.string.about_social_youtube),
                        modifier = Modifier.size(48.dp).clickable(onClick = onOpenYoutube),
                    )
                    Image(
                        painter = painterResource(Res.drawable.ic_social_x),
                        contentDescription = stringResource(Res.string.about_social_x),
                        modifier = Modifier.size(48.dp).clickable(onClick = onOpenX),
                    )
                    Image(
                        painter = painterResource(Res.drawable.ic_social_medium),
                        contentDescription = stringResource(Res.string.about_social_medium),
                        modifier = Modifier.size(48.dp).clickable(onClick = onOpenMedium),
                    )
                }
                Image(
                    painter = painterResource(Res.drawable.img_about_footer_character),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 24.dp)
                        .size(width = 30.dp, height = 36.dp),
                )
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
            onOpenSettings = {},
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
            onOpenSettings = {},
            onOpenYoutube = {},
            onOpenX = {},
            onOpenMedium = {},
            isDebugMenuAvailable = false,
            onOpenDebug = {},
        )
    }
}
