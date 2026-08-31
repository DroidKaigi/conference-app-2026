package io.github.droidkaigi.confsched.feature.about

import androidx.compose.runtime.Composable
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodleTarget
import io.github.droidkaigi.confsched.core.ui.SoilDataBoundary
import soil.query.compose.rememberSubscription

@Composable
context(screenContext: AboutScreenContext)
fun AboutScreenRoot(
    onOpenVenueWithMap: () -> Unit,
    onNavigateToSponsors: () -> Unit,
    onNavigateToContributors: () -> Unit,
    onNavigateToStaff: () -> Unit,
    onNavigateToLicenses: () -> Unit,
    onOpenCodeOfConduct: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToDoodle: () -> Unit,
    onOpenYoutube: () -> Unit,
    onOpenX: () -> Unit,
    onOpenMedium: () -> Unit,
    isDebugMenuAvailable: Boolean,
    onNavigateToDebug: () -> Unit,
) {
    SoilDataBoundary(
        state = rememberSubscription(
            key = screenContext.doodlesSubscriptionKey,
            select = { doodles -> doodles[DoodleTarget.AboutWall] ?: Doodle.Empty },
        ),
    ) { doodle ->
        val uiState = context(screenContext.presenterContext) {
            aboutScreenPresenter(doodle)
        }
        AboutScreen(
            uiState = uiState,
            onOpenVenueWithMap = onOpenVenueWithMap,
            onOpenSponsors = onNavigateToSponsors,
            onOpenContributors = onNavigateToContributors,
            onOpenStaff = onNavigateToStaff,
            onOpenLicenses = onNavigateToLicenses,
            onOpenCodeOfConduct = onOpenCodeOfConduct,
            onOpenPrivacyPolicy = onOpenPrivacyPolicy,
            onOpenSettings = onNavigateToSettings,
            onOpenDoodle = onNavigateToDoodle,
            onOpenYoutube = onOpenYoutube,
            onOpenX = onOpenX,
            onOpenMedium = onOpenMedium,
            isDebugMenuAvailable = isDebugMenuAvailable,
            onOpenDebug = onNavigateToDebug,
        )
    }
}
