package io.github.droidkaigi.confsched.feature.eventmap

import androidx.compose.runtime.Composable
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.common.retainScreenChannel
import io.github.droidkaigi.confsched.core.ui.SoilDataBoundary
import soil.query.compose.rememberQuery

@Composable
context(screenContext: EventMapScreenContext)
fun EventMapScreenRoot(
    onNavigateToStampCollecting: () -> Unit,
) {
    val screenChannel = retainScreenChannel<EventMapScreenAction, Nothing>()
    SoilDataBoundary(state = rememberQuery(screenContext.projectsQueryKey)) { projects ->
        val uiState = context(screenContext.presenterContext) {
            eventMapScreenPresenter(screenChannel = screenChannel, projects = projects)
        }
        EventMapScreen(
            uiState = uiState,
            onFloorClick = { screenChannel.send(EventMapScreenAction.SelectFloor(it)) },
            onFloorToggle = { screenChannel.send(EventMapScreenAction.ToggleFloor) },
            onStampCollectingLearnMoreClick = onNavigateToStampCollecting,
        )
    }
}
