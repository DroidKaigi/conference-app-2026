package io.github.droidkaigi.confsched.core.preview

import io.github.droidkaigi.confsched.core.model.Sponsor
import io.github.droidkaigi.confsched.core.model.SponsorGroup
import io.github.droidkaigi.confsched.core.model.SponsorPlan
import io.github.droidkaigi.confsched.core.model.Sponsors
import kotlinx.collections.immutable.persistentListOf

fun Sponsors.Companion.fake(): Sponsors = Sponsors(
    groups = persistentListOf(
        SponsorGroup(
            plan = SponsorPlan.Platinum,
            sponsors = persistentListOf(
                fakeSponsor("Sponsor A", SponsorPlan.Platinum),
                fakeSponsor("Sponsor B", SponsorPlan.Platinum),
                fakeSponsor("Sponsor C", SponsorPlan.Platinum),
            ),
        ),
        SponsorGroup(
            plan = SponsorPlan.Gold,
            sponsors = persistentListOf(
                fakeSponsor("Sponsor D", SponsorPlan.Gold),
                fakeSponsor("Sponsor E", SponsorPlan.Gold),
                fakeSponsor("Sponsor F", SponsorPlan.Gold),
                fakeSponsor("Sponsor G", SponsorPlan.Gold),
                fakeSponsor("Sponsor H", SponsorPlan.Gold),
                fakeSponsor("Sponsor I", SponsorPlan.Gold),
            ),
        ),
        SponsorGroup(
            plan = SponsorPlan.Supporter,
            sponsors = persistentListOf(
                fakeSponsor("Sponsor J", SponsorPlan.Supporter),
                fakeSponsor("Sponsor K", SponsorPlan.Supporter),
                fakeSponsor("Sponsor L", SponsorPlan.Supporter),
                fakeSponsor("Sponsor M", SponsorPlan.Supporter),
                fakeSponsor("Sponsor N", SponsorPlan.Supporter),
                fakeSponsor("Sponsor O", SponsorPlan.Supporter),
                fakeSponsor("Sponsor P", SponsorPlan.Supporter),
                fakeSponsor("Sponsor Q", SponsorPlan.Supporter),
                fakeSponsor("Sponsor R", SponsorPlan.Supporter),
            ),
        ),
    ),
)

private fun fakeSponsor(name: String, plan: SponsorPlan) = Sponsor(
    name = name,
    logoUrl = PreviewImage.SessionCover.imageUrl,
    plan = plan,
    link = "https://example.com/${name.lowercase().replace(" ", "-")}",
)
