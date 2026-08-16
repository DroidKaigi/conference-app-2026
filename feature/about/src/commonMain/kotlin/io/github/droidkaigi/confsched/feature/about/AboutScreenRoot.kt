package io.github.droidkaigi.confsched.feature.about

import androidx.compose.runtime.Composable
import io.github.droidkaigi.confsched.core.common.context

@Composable
context(screenContext: AboutScreenContext)
fun AboutScreenRoot(
    onNavigateToEventMap: () -> Unit,
    onNavigateToSponsors: () -> Unit,
    onNavigateToContributors: () -> Unit,
    onNavigateToStaff: () -> Unit,
    onNavigateToLicenses: () -> Unit,
    onOpenCodeOfConduct: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    isDebugMenuAvailable: Boolean,
    onNavigateToDebug: () -> Unit,
) {
    val uiState = context(screenContext.presenterContext) {
        aboutScreenPresenter()
    }
    AboutScreen(
        uiState = uiState,
        onOpenEventMap = onNavigateToEventMap,
        onOpenSponsors = onNavigateToSponsors,
        onOpenContributors = onNavigateToContributors,
        onOpenStaff = onNavigateToStaff,
        onOpenLicenses = onNavigateToLicenses,
        onOpenCodeOfConduct = onOpenCodeOfConduct,
        onOpenPrivacyPolicy = onOpenPrivacyPolicy,
        isDebugMenuAvailable = isDebugMenuAvailable,
        onOpenDebug = onNavigateToDebug,
    )
}
