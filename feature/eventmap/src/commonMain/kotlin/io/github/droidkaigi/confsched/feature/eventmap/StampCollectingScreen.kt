package io.github.droidkaigi.confsched.feature.eventmap

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Prizes
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocaleScreenPreviews
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiLargeTopAppBar
import io.github.droidkaigi.confsched.core.ui.paneStartInset
import io.github.droidkaigi.confsched.core.ui.rememberListDetailSceneAwareLazyListState
import io.github.droidkaigi.confsched.feature.eventmap.component.ExhibitionAreaMapView
import io.github.droidkaigi.confsched.feature.eventmap.component.PrizeGroupSection
import io.github.droidkaigi.confsched.feature.eventmap.component.StampCollectingSectionHeader
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.Res
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.stamp_collecting_exchange_hours_day1
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.stamp_collecting_exchange_hours_day2
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.stamp_collecting_exchange_hours_title
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.stamp_collecting_exchange_place_title
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.stamp_collecting_exhibition_area
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.stamp_collecting_introducing
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.stamp_collecting_title
import org.jetbrains.compose.resources.stringResource

// Each prize card wobbles on its own seed; the groups are laid out one after another, so the
// range has to stay wide enough for every prize the screen shows.
private const val PRIZE_SEED_BASE = 210
private const val PRIZE_SEED_STRIDE = 10

internal const val STAMP_COLLECTING_INTRODUCING_TEST_TAG = "StampCollectingIntroducingTestTag"
internal const val STAMP_COLLECTING_EXCHANGE_PLACE_TITLE_TEST_TAG = "StampCollectingExchangePlaceTitleTestTag"
internal const val STAMP_COLLECTING_EXHIBITION_AREA_TEST_TAG = "StampCollectingExhibitionAreaTestTag"
internal const val STAMP_COLLECTING_EXCHANGE_HOURS_TITLE_TEST_TAG = "StampCollectingExchangeHoursTitleTestTag"
internal const val STAMP_COLLECTING_EXCHANGE_HOURS_DAY1_TEST_TAG = "StampCollectingExchangeHoursDay1TestTag"
internal const val STAMP_COLLECTING_EXCHANGE_HOURS_DAY2_TEST_TAG = "StampCollectingExchangeHoursDay2TestTag"

@Composable
fun StampCollectingScreen(
    uiState: StampCollectingScreenUiState,
    onBackClick: () -> Unit,
    onPrizeClick: (page: Int) -> Unit,
) {
    Scaffold(
        topBar = {
            KaigiLargeTopAppBar(
                title = stringResource(Res.string.stamp_collecting_title),
                onBackClick = onBackClick,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            state = rememberListDetailSceneAwareLazyListState(),
            contentPadding = PaddingValues(16.dp)
                .plus(PaddingValues(start = paneStartInset(), bottom = 122.dp)),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            item {
                Text(
                    text = stringResource(Res.string.stamp_collecting_introducing),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag(STAMP_COLLECTING_INTRODUCING_TEST_TAG),
                )
            }
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StampCollectingSectionHeader(
                        title = stringResource(Res.string.stamp_collecting_exchange_place_title),
                        modifier = Modifier.testTag(STAMP_COLLECTING_EXCHANGE_PLACE_TITLE_TEST_TAG),
                    )
                    Text(
                        text = stringResource(Res.string.stamp_collecting_exhibition_area),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag(STAMP_COLLECTING_EXHIBITION_AREA_TEST_TAG),
                    )
                    ExhibitionAreaMapView()
                }
            }
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StampCollectingSectionHeader(
                        title = stringResource(Res.string.stamp_collecting_exchange_hours_title),
                        modifier = Modifier.testTag(STAMP_COLLECTING_EXCHANGE_HOURS_TITLE_TEST_TAG),
                    )
                    Text(
                        text = stringResource(Res.string.stamp_collecting_exchange_hours_day1),
                        style = MaterialTheme.typography.titleSmallEmphasized,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag(STAMP_COLLECTING_EXCHANGE_HOURS_DAY1_TEST_TAG),
                    )
                    Text(
                        text = stringResource(Res.string.stamp_collecting_exchange_hours_day2),
                        style = MaterialTheme.typography.titleSmallEmphasized,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag(STAMP_COLLECTING_EXCHANGE_HOURS_DAY2_TEST_TAG),
                    )
                }
            }
            itemsIndexed(uiState.prizeGroups) { index, group ->
                PrizeGroupSection(
                    group = group,
                    seed = PRIZE_SEED_BASE + index * PRIZE_SEED_STRIDE,
                    onPrizeClick = { prize -> onPrizeClick(uiState.prizes.indexOf(prize)) },
                )
            }
        }
    }
}

@LocaleScreenPreviews
@Composable
private fun StampCollectingScreenPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        StampCollectingScreen(
            uiState = StampCollectingScreenUiState.of(Prizes.fake()),
            onBackClick = {},
            onPrizeClick = {},
        )
    }
}
