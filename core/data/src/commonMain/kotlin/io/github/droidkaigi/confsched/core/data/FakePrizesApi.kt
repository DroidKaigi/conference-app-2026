package io.github.droidkaigi.confsched.core.data

import io.github.droidkaigi.confsched.core.preview.PreviewImage
import kotlinx.coroutines.delay

class FakePrizesApi : PrizesApi {
    override suspend fun getPrizes(): PrizeListResponse {
        delay(300)
        return PrizeListResponse(
            status = HttpStatusResponse.OK,
            prizes = listOf(
                fakePrize(1, PrizeGroupResponse.A),
                fakePrize(2, PrizeGroupResponse.A),
                fakePrize(3, PrizeGroupResponse.A),
                fakePrize(4, PrizeGroupResponse.B),
                fakePrize(5, PrizeGroupResponse.B),
                fakePrize(6, PrizeGroupResponse.B),
                fakePrize(7, PrizeGroupResponse.C),
                fakePrize(8, PrizeGroupResponse.C),
            ),
        )
    }

    private fun fakePrize(number: Int, group: PrizeGroupResponse) = PrizeResponse(
        id = "prize-$number",
        name = LocaledResponse(ja = "グッズ$number", en = "Prize $number"),
        group = group,
        image = PreviewImage.PrizePhoto.imageUrl,
    )
}
