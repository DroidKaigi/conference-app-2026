package io.github.droidkaigi.confsched.feature.eventmap

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import io.github.droidkaigi.confsched.core.common.ActionEffect
import io.github.droidkaigi.confsched.core.common.ScreenChannel
import io.github.droidkaigi.confsched.core.model.Floor
import io.github.droidkaigi.confsched.core.model.Projects

@Composable
context(_: EventMapPresenterContext)
fun eventMapScreenPresenter(
    screenChannel: ScreenChannel<EventMapScreenAction, Nothing>,
    projects: Projects,
): EventMapScreenUiState {
    var selectedFloor by retain { mutableStateOf(Floor.Ground) }

    ActionEffect(screenChannel) { action ->
        when (action) {
            is EventMapScreenAction.SelectFloor -> selectedFloor = action.floor
            is EventMapScreenAction.ToggleFloor -> selectedFloor = selectedFloor.toggle()
        }
    }

    return EventMapScreenUiState(
        selectedFloor = selectedFloor,
        projects = projects.items,
    )
}
