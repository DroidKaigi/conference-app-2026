package io.github.droidkaigi.confsched.feature.eventmap

import androidx.compose.runtime.Composable
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.ui.SoilDataBoundary
import soil.query.compose.rememberQuery

@Composable
context(screenContext: PrizeOverlayScreenContext)
fun PrizeOverlayScreenRoot(
    onNavigateBack: () -> Unit,
) {
    SoilDataBoundary(state = rememberQuery(screenContext.prizesQueryKey)) { prizes ->
        val uiState = context(screenContext.presenterContext) {
            prizeOverlayScreenPresenter(
                prizes = prizes,
                initialPage = screenContext.navKey.page,
            )
        }

        PrizeOverlayScreen(
            uiState = uiState,
            onCloseClick = onNavigateBack,
        )
    }
}
