package io.github.droidkaigi.confsched.feature.about

import androidx.compose.runtime.Composable
import io.github.droidkaigi.confsched.core.common.ActionResultEffect
import io.github.droidkaigi.confsched.core.common.LocalSnackbarHostState
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.common.retainScreenChannel
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodleTarget
import io.github.droidkaigi.confsched.core.ui.SoilDataBoundary
import io.github.droidkaigi.confsched.core.ui.showSnackbar
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
        val screenChannel = retainScreenChannel<AboutScreenAction, AboutScreenActionResult>()
        val snackbarHostState = LocalSnackbarHostState.current

        ActionResultEffect(screenChannel) { result ->
            when (result) {
                is AboutScreenActionResult.ShowMessage -> snackbarHostState.showSnackbar(result.message)
            }
        }

        val uiState = context(screenContext.presenterContext) {
            aboutScreenPresenter(screenChannel = screenChannel, doodle = doodle)
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
            onOpenYoutube = onOpenYoutube,
            onOpenX = onOpenX,
            onOpenMedium = onOpenMedium,
            isDebugMenuAvailable = isDebugMenuAvailable,
            onOpenDebug = onNavigateToDebug,
            onStartDoodlingClick = { screenChannel.send(AboutScreenAction.StartDoodling) },
            onCancelDoodlingClick = { screenChannel.send(AboutScreenAction.CancelDoodling) },
            onDoodleDoneClick = { screenChannel.send(AboutScreenAction.SaveWallDoodle(it)) },
        )
    }
}
