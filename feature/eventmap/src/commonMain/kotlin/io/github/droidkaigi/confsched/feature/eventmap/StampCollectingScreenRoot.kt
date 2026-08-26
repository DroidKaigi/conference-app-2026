package io.github.droidkaigi.confsched.feature.eventmap

import androidx.compose.runtime.Composable
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.ui.SoilDataBoundary
import soil.query.compose.rememberQuery

@Composable
context(screenContext: StampCollectingScreenContext)
fun StampCollectingScreenRoot(
    onNavigateBack: () -> Unit,
    onNavigateToPrize: (page: Int) -> Unit,
) {
    SoilDataBoundary(state = rememberQuery(screenContext.prizesQueryKey)) { prizes ->
        val uiState = context(screenContext.presenterContext) {
            stampCollectingScreenPresenter(prizes = prizes)
        }

        StampCollectingScreen(
            uiState = uiState,
            onBackClick = onNavigateBack,
            onPrizeClick = onNavigateToPrize,
        )
    }
}
