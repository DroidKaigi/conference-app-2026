package io.github.droidkaigi.confsched.feature.eventmap

import io.github.droidkaigi.confsched.core.model.Prize
import io.github.droidkaigi.confsched.core.model.PrizeGroup
import io.github.droidkaigi.confsched.core.model.Prizes
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

data class StampCollectingPrizeGroup(
    val group: PrizeGroup,
    val prizes: PersistentList<Prize>,
)

data class StampCollectingScreenUiState(
    val prizeGroups: PersistentList<StampCollectingPrizeGroup>,
    /** Every prize in group order — the pages the prize overlay swipes through. */
    val prizes: PersistentList<Prize>,
) {
    companion object {
        fun of(prizes: Prizes): StampCollectingScreenUiState {
            val prizeGroups = prizes.items
                .groupBy(Prize::group)
                .map { (group, groupPrizes) ->
                    StampCollectingPrizeGroup(group = group, prizes = groupPrizes.toPersistentList())
                }
                .sortedBy(StampCollectingPrizeGroup::group)
                .toPersistentList()
            return StampCollectingScreenUiState(
                prizeGroups = prizeGroups,
                prizes = prizeGroups.flatMap(StampCollectingPrizeGroup::prizes).toPersistentList(),
            )
        }
    }
}
