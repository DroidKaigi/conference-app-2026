package io.github.droidkaigi.confsched.feature.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import io.github.droidkaigi.confsched.core.common.ActionEffect
import io.github.droidkaigi.confsched.core.common.ScreenChannel

@Composable
context(_: SearchPresenterContext)
fun searchScreenPresenter(
    screenChannel: ScreenChannel<SearchScreenAction, SearchScreenActionResult>,
): SearchScreenUiState {
    var reloadCount by retain { mutableStateOf(0) }

    ActionEffect(screenChannel) { action ->
        when (action) {
            SearchScreenAction.Reload -> {
                reloadCount++
                screenChannel.emit(SearchScreenActionResult.Reloaded)
            }
        }
    }

    return SearchScreenUiState(
        title = "Search",
        reloadCount = reloadCount,
    )
}
