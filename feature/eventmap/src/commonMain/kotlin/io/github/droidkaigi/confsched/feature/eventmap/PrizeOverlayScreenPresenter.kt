package io.github.droidkaigi.confsched.feature.eventmap

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.droidkaigi.confsched.core.model.Prizes

@Composable
context(_: PrizeOverlayPresenterContext)
fun prizeOverlayScreenPresenter(
    prizes: Prizes,
    initialPage: Int,
): PrizeOverlayScreenUiState {
    // The pager walks the prizes in the order the grid lays them out, so the page the grid asked
    // for only lands on the right prize when both sides group them the same way.
    val orderedPrizes = remember(prizes) { StampCollectingScreenUiState.of(prizes).prizes }

    return PrizeOverlayScreenUiState(
        prizes = orderedPrizes,
        initialPage = initialPage,
    )
}
