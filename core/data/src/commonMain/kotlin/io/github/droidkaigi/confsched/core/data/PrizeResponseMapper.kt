package io.github.droidkaigi.confsched.core.data

import io.github.droidkaigi.confsched.core.model.Prize
import io.github.droidkaigi.confsched.core.model.PrizeGroup
import io.github.droidkaigi.confsched.core.model.PrizeId
import io.github.droidkaigi.confsched.core.model.Prizes
import kotlinx.collections.immutable.toPersistentList

fun PrizeListResponse.toPrizes(): Prizes = Prizes(
    items = prizes.map(PrizeResponse::toPrize).toPersistentList(),
)

private fun PrizeResponse.toPrize(): Prize = Prize(
    id = PrizeId(id),
    name = name.toMultiLangText(),
    group = group.toPrizeGroup(),
    imageUrl = image,
)

private fun PrizeGroupResponse.toPrizeGroup(): PrizeGroup = when (this) {
    PrizeGroupResponse.A -> PrizeGroup.A
    PrizeGroupResponse.B -> PrizeGroup.B
    PrizeGroupResponse.C -> PrizeGroup.C
}
