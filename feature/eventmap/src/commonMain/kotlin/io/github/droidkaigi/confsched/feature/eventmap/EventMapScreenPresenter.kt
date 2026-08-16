package io.github.droidkaigi.confsched.feature.eventmap

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import io.github.droidkaigi.confsched.core.common.ActionEffect
import io.github.droidkaigi.confsched.core.common.ScreenChannel

@Composable
context(_: EventMapPresenterContext)
fun eventMapScreenPresenter(
    screenChannel: ScreenChannel<EventMapScreenAction, Nothing>,
): EventMapScreenUiState {
    var selectedFloor by retain { mutableStateOf(EventMapFloor.Ground) }

    val eventMapItems = remember(selectedFloor) {
        EventMapScreenUiState.mock(selectedFloor)
    }

    ActionEffect(screenChannel) { action ->
        when (action) {
            is EventMapScreenAction.SelectFloor -> selectedFloor = action.floor
        }
    }

    return EventMapScreenUiState(
        selectedFloor = selectedFloor,
        eventMapItems = eventMapItems,
    )
}
