package io.github.droidkaigi.confsched.feature.about.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
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
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.LocalNavigationBarOccupiedHeight
import io.github.droidkaigi.confsched.feature.about.generated.resources.Res
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_description
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_event_date
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

/**
 * Everything the page carries below the hero. While [dimmed] the block fades and takes no input, so
 * the wall being drawn on is the only thing the page reacts to.
 */
@Composable
internal fun AboutPageBodyView(
    versionName: String,
    isDebugMenuAvailable: Boolean,
    dimmed: Boolean,
    onOpenVenueWithMap: () -> Unit,
    onOpenSponsors: () -> Unit,
    onOpenContributors: () -> Unit,
    onOpenStaff: () -> Unit,
    onOpenLicenses: () -> Unit,
    onOpenCodeOfConduct: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenDebug: () -> Unit,
    onOpenYoutube: () -> Unit,
    onOpenX: () -> Unit,
    onOpenMedium: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // The navigation bar floats over the content, so the page reserves its room at the bottom.
    val navigationBarHeight = LocalNavigationBarOccupiedHeight.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (dimmed) AboutPageBodyDefaults.dimmedAlpha else 1f)
            .swallowPointerInput(dimmed),
    ) {
        Spacer(modifier = Modifier.height(AboutPageBodyDefaults.heroGap))
        Text(
            text = stringResource(Res.string.about_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth().padding(horizontal = AboutPageBodyDefaults.textInset),
        )
        Spacer(modifier = Modifier.height(AboutPageBodyDefaults.eventCardGap))
        AboutEventCard(
            date = stringResource(Res.string.about_event_date),
            venue = stringResource(Res.string.about_venue),
            viewMapLabel = stringResource(Res.string.about_view_map),
            onViewMap = onOpenVenueWithMap,
            modifier = Modifier.padding(horizontal = AboutPageBodyDefaults.textInset),
        )
        Spacer(modifier = Modifier.height(AboutPageBodyDefaults.sectionsGap))
        Column(modifier = Modifier.padding(horizontal = AboutPageBodyDefaults.sectionInset)) {
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

            Spacer(modifier = Modifier.height(AboutPageBodyDefaults.sectionGap))
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

            Spacer(modifier = Modifier.height(AboutPageBodyDefaults.sectionGap))
            AboutFooter(
                versionName = versionName,
                onOpenYoutube = onOpenYoutube,
                onOpenX = onOpenX,
                onOpenMedium = onOpenMedium,
                modifier = Modifier.padding(bottom = navigationBarHeight),
            )
        }
    }
}

// Consumed in the initial pass, so a gesture never reaches the controls underneath.
private fun Modifier.swallowPointerInput(swallowing: Boolean): Modifier = if (!swallowing) {
    this
} else {
    pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                awaitPointerEvent(PointerEventPass.Initial).changes.forEach(PointerInputChange::consume)
            }
        }
    }
}

private object AboutPageBodyDefaults {
    val dimmedAlpha = 0.4f
    val heroGap = 22.dp
    val eventCardGap = 12.dp
    val sectionsGap = 40.dp
    val sectionGap = 24.dp
    val textInset = 44.dp
    val sectionInset = 24.dp
}

@LocalePreviews
@Composable
private fun AboutPageBodyViewPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        AboutPageBodyView(
            versionName = "1.0.0",
            isDebugMenuAvailable = true,
            dimmed = false,
            onOpenVenueWithMap = {},
            onOpenSponsors = {},
            onOpenContributors = {},
            onOpenStaff = {},
            onOpenLicenses = {},
            onOpenCodeOfConduct = {},
            onOpenPrivacyPolicy = {},
            onOpenSettings = {},
            onOpenDebug = {},
            onOpenYoutube = {},
            onOpenX = {},
            onOpenMedium = {},
        )
    }
}

@LocalePreviews
@Composable
private fun AboutPageBodyViewDimmedPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        AboutPageBodyView(
            versionName = "1.0.0",
            isDebugMenuAvailable = false,
            dimmed = true,
            onOpenVenueWithMap = {},
            onOpenSponsors = {},
            onOpenContributors = {},
            onOpenStaff = {},
            onOpenLicenses = {},
            onOpenCodeOfConduct = {},
            onOpenPrivacyPolicy = {},
            onOpenSettings = {},
            onOpenDebug = {},
            onOpenYoutube = {},
            onOpenX = {},
            onOpenMedium = {},
        )
    }
}
