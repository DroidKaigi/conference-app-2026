package io.github.droidkaigi.confsched.feature.eventmap.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Prize
import io.github.droidkaigi.confsched.core.model.PrizeGroup
import io.github.droidkaigi.confsched.core.model.Prizes
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.current
import io.github.droidkaigi.confsched.feature.eventmap.StampCollectingPrizeGroup
import io.github.droidkaigi.confsched.feature.eventmap.StampCollectingScreenUiState
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.Res
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.stamp_collecting_group
import org.jetbrains.compose.resources.stringResource

// The design lays the prizes out two to a row on a phone; wider windows fit more.
private const val PRIZE_MIN_COLUMNS = 2
private val PrizeColumnMinWidth = 158.dp
private val PrizeSpacing = 12.dp

fun prizeGroupSectionTestTag(group: PrizeGroup) = "PrizeGroupSection:${group.name}"

@Composable
internal fun PrizeGroupSection(
    group: StampCollectingPrizeGroup,
    seed: Int,
    onPrizeClick: (Prize) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val columns = ((maxWidth + PrizeSpacing) / (PrizeColumnMinWidth + PrizeSpacing))
            .toInt()
            .coerceAtLeast(PRIZE_MIN_COLUMNS)
        Column(
            verticalArrangement = Arrangement.spacedBy(PrizeSpacing),
        ) {
            StampCollectingSectionHeader(
                title = stringResource(Res.string.stamp_collecting_group, group.group.name),
                modifier = Modifier.testTag(prizeGroupSectionTestTag(group.group)),
            )
            for (rowPrizes in group.prizes.withIndex().chunked(columns)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(PrizeSpacing),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    for ((index, prize) in rowPrizes) {
                        PrizeCardItem(
                            id = prize.id,
                            name = prize.name.current(),
                            imageUrl = prize.imageUrl,
                            seed = seed + index,
                            onClick = { onPrizeClick(prize) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    repeat(columns - rowPrizes.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@LocalePreviews
@Composable
private fun PrizeGroupSectionPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        PrizeGroupSection(
            group = StampCollectingScreenUiState.of(Prizes.fake()).prizeGroups.first(),
            seed = 210,
            onPrizeClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
