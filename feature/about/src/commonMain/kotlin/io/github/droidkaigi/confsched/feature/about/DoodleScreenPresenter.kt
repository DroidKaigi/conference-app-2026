package io.github.droidkaigi.confsched.feature.about

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import io.github.droidkaigi.confsched.core.common.ActionEffect
import io.github.droidkaigi.confsched.core.common.ScreenChannel

@Composable
context(_: DoodlePresenterContext)
fun doodleScreenPresenter(
    screenChannel: ScreenChannel<DoodleScreenAction, DoodleScreenActionResult>,
): DoodleScreenUiState {
    var reloadCount by retain { mutableStateOf(0) }

    ActionEffect(screenChannel) { action ->
        when (action) {
            DoodleScreenAction.Reload -> {
                reloadCount++
                screenChannel.emit(DoodleScreenActionResult.Reloaded)
            }
        }
    }

    return DoodleScreenUiState(
        title = "Doodle",
        reloadCount = reloadCount,
    )
}
