package io.github.droidkaigi.confsched.core.data

import kotlinx.coroutines.delay

class FakeSponsorApi : SponsorApi {
    override suspend fun getSponsors(): SponsorListResponse {
        delay(300)
        return SponsorListResponse(
            status = HttpStatusResponse.OK,
            sponsor = listOf(
                fakeSponsor("Sponsor A", SponsorPlanResponse.PLATINUM),
                fakeSponsor("Sponsor B", SponsorPlanResponse.PLATINUM),
                fakeSponsor("Sponsor C", SponsorPlanResponse.PLATINUM),
                fakeSponsor("Sponsor D", SponsorPlanResponse.GOLD),
                fakeSponsor("Sponsor E", SponsorPlanResponse.GOLD),
                fakeSponsor("Sponsor F", SponsorPlanResponse.GOLD),
                fakeSponsor("Sponsor G", SponsorPlanResponse.GOLD),
                fakeSponsor("Sponsor H", SponsorPlanResponse.GOLD),
                fakeSponsor("Sponsor I", SponsorPlanResponse.GOLD),
                fakeSponsor("Sponsor J", SponsorPlanResponse.SUPPORTER),
                fakeSponsor("Sponsor K", SponsorPlanResponse.SUPPORTER),
                fakeSponsor("Sponsor L", SponsorPlanResponse.SUPPORTER),
                fakeSponsor("Sponsor M", SponsorPlanResponse.SUPPORTER),
                fakeSponsor("Sponsor N", SponsorPlanResponse.SUPPORTER),
                fakeSponsor("Sponsor O", SponsorPlanResponse.SUPPORTER),
                fakeSponsor("Sponsor P", SponsorPlanResponse.SUPPORTER),
                fakeSponsor("Sponsor Q", SponsorPlanResponse.SUPPORTER),
                fakeSponsor("Sponsor R", SponsorPlanResponse.SUPPORTER),
            ),
        )
    }

    private fun fakeSponsor(
        name: String,
        plan: SponsorPlanResponse,
    ) = SponsorResponse(
        sponsorName = name,
        sponsorLogo = "https://placehold.jp/240x120.png",
        plan = plan,
        link = "https://example.com/${name.lowercase().replace(" ", "-")}",
        checkedBySponsor = true,
    )
}
