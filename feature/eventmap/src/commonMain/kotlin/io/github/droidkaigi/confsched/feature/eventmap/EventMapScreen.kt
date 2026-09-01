package io.github.droidkaigi.confsched.feature.eventmap

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.common.OnTabReselect
import io.github.droidkaigi.confsched.core.model.Floor
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Projects
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocaleScreenPreviews
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiTopAppBar
import io.github.droidkaigi.confsched.core.ui.SketchHorizontalDivider
import io.github.droidkaigi.confsched.feature.eventmap.component.EventItem
import io.github.droidkaigi.confsched.feature.eventmap.component.FloorMapCard
import io.github.droidkaigi.confsched.feature.eventmap.component.FloorTabRow
import io.github.droidkaigi.confsched.feature.eventmap.component.StampCollectingCard
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.Res
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.event_map_introducing
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.event_map_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun EventMapScreen(
    uiState: EventMapScreenUiState,
    onFloorClick: (Floor) -> Unit,
    onFloorToggle: () -> Unit,
    onStampCollectingLearnMoreClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            KaigiTopAppBar(title = stringResource(Res.string.event_map_title))
        },
        contentWindowInsets = WindowInsets(),
    ) { innerPadding ->
        val listState = rememberLazyListState()
        OnTabReselect(EventMapNavKey) { listState.animateScrollToItem(0) }
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(16.dp).plus(PaddingValues(bottom = 122.dp)),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            item {
                Text(
                    text = stringResource(Res.string.event_map_introducing),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            item {
                FloorTabRow(
                    selectedFloor = uiState.selectedFloor,
                    onFloorClick = onFloorClick,
                )
            }
            item {
                FloorMapCard(
                    selectedFloor = uiState.selectedFloor,
                    onFloorToggle = onFloorToggle,
                )
            }
            item {
                StampCollectingCard(
                    onLearnMoreClick = onStampCollectingLearnMoreClick,
                )
            }
            itemsIndexed(uiState.projects) { index, project ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    EventItem(
                        title = project.title,
                        i18nDesc = project.description,
                        room = project.room,
                        message = project.message,
                        moreDetailsUrl = project.moreDetailsUrl,
                        seed = index,
                    )
                    if (index != uiState.projects.lastIndex) {
                        SketchHorizontalDivider(
                            seed = index + 100,
                            thickness = 1.3.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier
                                .height(5.dp),
                        )
                    }
                }
            }
        }
    }
}

@LocaleScreenPreviews
@Composable
private fun EventMapScreenPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        val selectedFloor = Floor.Ground
        EventMapScreen(
            uiState = EventMapScreenUiState(
                selectedFloor = selectedFloor,
                projects = Projects.fake().items,
            ),
            onFloorClick = {},
            onFloorToggle = {},
            onStampCollectingLearnMoreClick = {},
        )
    }
}
