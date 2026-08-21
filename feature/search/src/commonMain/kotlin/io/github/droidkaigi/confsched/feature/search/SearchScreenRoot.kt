package io.github.droidkaigi.confsched.feature.search

import androidx.compose.runtime.Composable
import io.github.droidkaigi.confsched.core.common.ActionResultEffect
import io.github.droidkaigi.confsched.core.common.LocalSnackbarHostState
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.common.retainScreenChannel

@Composable
context(screenContext: SearchScreenContext)
fun SearchScreenRoot(
    onNavigateBack: () -> Unit,
) {
    val screenChannel = retainScreenChannel<SearchScreenAction, SearchScreenActionResult>()
    val snackbarHostState = LocalSnackbarHostState.current

    ActionResultEffect(screenChannel) { result ->
        when (result) {
            SearchScreenActionResult.Reloaded -> snackbarHostState.showSnackbar("Reloaded")
        }
    }

    val uiState = context(screenContext.presenterContext) {
        searchScreenPresenter(screenChannel)
    }
    SearchScreen(
        uiState = uiState,
        onReloadClick = { screenChannel.send(SearchScreenAction.Reload) },
        onBackClick = onNavigateBack,
    )
}
