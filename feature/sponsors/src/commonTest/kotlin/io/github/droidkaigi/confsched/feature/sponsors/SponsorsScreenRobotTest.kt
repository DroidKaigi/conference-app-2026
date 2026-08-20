package io.github.droidkaigi.confsched.feature.sponsors

import androidx.compose.ui.test.ExperimentalTestApi
import io.github.droidkaigi.confsched.core.model.Sponsor
import io.github.droidkaigi.confsched.core.model.SponsorGroup
import io.github.droidkaigi.confsched.core.model.SponsorPlan
import io.github.droidkaigi.confsched.core.model.Sponsors
import io.github.droidkaigi.confsched.core.testing.RobotTest
import io.github.droidkaigi.confsched.core.testing.runRobotTest
import kotlinx.collections.immutable.persistentListOf
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class SponsorsScreenRobotTest : RobotTest() {

    private val sampleSponsors = Sponsors(
        groups = persistentListOf(
            SponsorGroup(
                plan = SponsorPlan.Platinum,
                sponsors = persistentListOf(sponsor("Sponsor A", "sponsor-a", SponsorPlan.Platinum)),
            ),
            SponsorGroup(
                plan = SponsorPlan.Supporter,
                sponsors = persistentListOf(sponsor("Sponsor G", "sponsor-g", SponsorPlan.Supporter)),
            ),
        ),
    )

    @Test
    fun sponsors_screen_behaviour() = runRobotTest(
        robotFactory = { SponsorsScreenRobot(this) },
    ) {
        describe("when the sponsors have loaded") {
            doIt {
                setupSponsors(sampleSponsors)
                setupContent()
            }
            itShould("show a section per plan present in the payload") {
                checkPlanSectionDisplayed("PLATINUM SPONSORS")
                checkPlanSectionDisplayed("SPONSORS")
                checkPlanSectionDoesNotExist("GOLD SPONSORS")
            }
            itShould("show each sponsor under its plan") {
                checkSponsorDisplayed("Sponsor A")
                checkSponsorDisplayed("Sponsor G")
            }
            describe("and a sponsor is tapped") {
                doIt {
                    clickSponsor("Sponsor A")
                }
                itShould("open that sponsor's site") {
                    checkOpenedSites("https://example.com/sponsor-a")
                }
            }
            describe("and back is tapped") {
                doIt {
                    clickBack()
                }
                itShould("leave the screen once") {
                    checkBackInvoked(times = 1)
                }
            }
        }

        describe("when two sponsors on one plan share a name") {
            doIt {
                setupSponsors(
                    Sponsors(
                        groups = persistentListOf(
                            SponsorGroup(
                                plan = SponsorPlan.Gold,
                                sponsors = persistentListOf(
                                    sponsor("Sponsor D", "sponsor-d-jp", SponsorPlan.Gold),
                                    sponsor("Sponsor D", "sponsor-d-us", SponsorPlan.Gold),
                                ),
                            ),
                        ),
                    ),
                )
                setupContent()
            }
            itShould("render both instead of failing on a duplicate key") {
                checkPlanSectionDisplayed("GOLD SPONSORS")
                checkSponsorCount("Sponsor D", expected = 2)
            }
        }

        describe("when the sponsors have not arrived yet") {
            doIt {
                setupPendingSponsors()
                setupContent()
            }
            itShould("show the loading fallback") {
                checkLoadingDisplayed()
                checkPlanSectionDoesNotExist("PLATINUM SPONSORS")
            }
            describe("and they arrive") {
                doIt {
                    releaseSponsors(sampleSponsors)
                }
                itShould("swap the fallback for the content") {
                    checkSponsorDisplayed("Sponsor A")
                }
            }
        }

        describe("when the sponsors fail to load") {
            doIt {
                setupFailingSponsors()
                setupContent()
            }
            itShould("show the error fallback") {
                checkErrorDisplayed()
            }
        }

        describe("when the payload carries no sponsors") {
            doIt {
                setupSponsors(Sponsors(groups = persistentListOf()))
                setupContent()
            }
            itShould("show the empty state") {
                checkEmptyStateDisplayed()
                checkPlanSectionDoesNotExist("PLATINUM SPONSORS")
            }
        }
    }

    private fun sponsor(name: String, slug: String, plan: SponsorPlan) = Sponsor(
        name = name,
        logoUrl = "https://example.com/$slug.png",
        plan = plan,
        link = "https://example.com/$slug",
    )
}
