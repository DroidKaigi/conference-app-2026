package io.github.droidkaigi.confsched.feature.eventmap

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.droidkaigi.confsched.core.model.Prizes

@Composable
context(_: StampCollectingPresenterContext)
fun stampCollectingScreenPresenter(prizes: Prizes): StampCollectingScreenUiState {
    val uiState = remember(prizes) { StampCollectingScreenUiState.of(prizes) }

    return uiState
}
