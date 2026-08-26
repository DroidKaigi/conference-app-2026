package io.github.droidkaigi.confsched.core.preview

import io.github.droidkaigi.confsched.core.model.MultiLangText
import io.github.droidkaigi.confsched.core.model.Prize
import io.github.droidkaigi.confsched.core.model.PrizeGroup
import io.github.droidkaigi.confsched.core.model.PrizeId
import io.github.droidkaigi.confsched.core.model.Prizes
import kotlinx.collections.immutable.persistentListOf

fun Prizes.Companion.fake(): Prizes = Prizes(
    items = persistentListOf(
        fakePrize(1, PrizeGroup.A),
        fakePrize(2, PrizeGroup.A),
        fakePrize(3, PrizeGroup.A),
        fakePrize(4, PrizeGroup.B),
        fakePrize(5, PrizeGroup.B),
        fakePrize(6, PrizeGroup.B),
        fakePrize(7, PrizeGroup.C),
        fakePrize(8, PrizeGroup.C),
    ),
)

private fun fakePrize(number: Int, group: PrizeGroup) = Prize(
    id = PrizeId("prize-$number"),
    name = MultiLangText(ja = "グッズ$number", en = "Prize $number"),
    group = group,
    imageUrl = PreviewImage.PrizePhoto.imageUrl,
)
