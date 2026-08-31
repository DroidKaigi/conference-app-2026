package io.github.droidkaigi.confsched.feature.eventmap

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.text.intl.Locale
import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.model.DisplayLanguage
import io.github.droidkaigi.confsched.core.model.MultiLangText
import io.github.droidkaigi.confsched.core.model.Prize
import io.github.droidkaigi.confsched.core.model.PrizeGroup
import io.github.droidkaigi.confsched.core.model.PrizeId
import io.github.droidkaigi.confsched.core.model.Prizes
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.testing.Robot
import io.github.droidkaigi.confsched.core.ui.KAIGI_TOP_APP_BAR_BACK_BUTTON_TEST_TAG
import io.github.droidkaigi.confsched.feature.eventmap.component.prizeCardItemTestTag
import io.github.droidkaigi.confsched.feature.eventmap.component.prizeGroupSectionTestTag
import io.github.droidkaigi.confsched.feature.eventmap.component.prizePageCardTestTag
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class StampCollectingScreenRobot(composeUiTest: ComposeUiTest) : Robot(composeUiTest) {

    private val graph = createGraph<StampCollectingScreenTestGraph>()
    private var backCount = 0
    private var openedPrizePages = mutableListOf<Int>()
    private var overlayCloseCount = 0

    fun setupPrizes(prizes: Prizes) {
        graph.prizesQueryKey.set(prizes)
    }

    fun setupContent() {
        setScreenContent {
            context(graph.screenContext) {
                StampCollectingScreenRoot(
                    onNavigateBack = { backCount++ },
                    onNavigateToPrize = openedPrizePages::add,
                )
            }
        }
    }

    // The overlay is a dialog destination, and a Dialog renders into its own window, which leaves
    // the capture with two roots, so its own scenarios compose the screen directly.
    fun setupOverlayContent(initialPage: Int) {
        setScreenContent {
            PrizeOverlayScreen(
                uiState = PrizeOverlayScreenUiState(
                    prizes = StampCollectingScreenUiState.of(Prizes.fake()).prizes,
                    initialPage = initialPage,
                ),
                onCloseClick = { overlayCloseCount++ },
            )
        }
    }

    fun scrollToPrize(id: PrizeId) {
        composeUiTest.onNode(hasScrollAction()).performScrollToNode(hasTestTag(prizeCardItemTestTag(id)))
        composeUiTest.waitForIdle()
    }

    fun clickPrize(id: PrizeId) {
        composeUiTest.onNodeWithTag(prizeCardItemTestTag(id)).performClick()
        composeUiTest.waitForIdle()
    }

    fun clickOverlayClose() {
        composeUiTest.onNodeWithTag(PRIZE_OVERLAY_SCREEN_CLOSE_BUTTON_TEST_TAG).performClick()
        composeUiTest.waitForIdle()
    }

    fun clickBack() {
        composeUiTest.onNodeWithTag(KAIGI_TOP_APP_BAR_BACK_BUTTON_TEST_TAG).performClick()
        composeUiTest.waitForIdle()
    }

    fun checkExchangePlaceSectionDisplayed() {
        composeUiTest.onNodeWithTag(STAMP_COLLECTING_EXCHANGE_PLACE_TITLE_TEST_TAG).assertIsDisplayed()
        composeUiTest.onNodeWithTag(STAMP_COLLECTING_EXHIBITION_AREA_TEST_TAG).assertIsDisplayed()
    }

    fun checkExchangeHoursSectionDisplayed() {
        composeUiTest.onNodeWithTag(STAMP_COLLECTING_EXCHANGE_HOURS_TITLE_TEST_TAG).assertIsDisplayed()
        composeUiTest.onNodeWithTag(STAMP_COLLECTING_EXCHANGE_HOURS_DAY1_TEST_TAG).assertIsDisplayed()
    }

    fun checkPrizeGroupDisplayed(group: PrizeGroup) {
        textIn(prizeGroupSectionTestTag(group), group.name, substring = true).assertIsDisplayed()
    }

    fun checkPrizeDisplayed(prize: Prize) {
        textIn(prizeCardItemTestTag(prize.id), prize.name.displayed(), substring = false).assertIsDisplayed()
    }

    fun checkPrizePageDisplayed(prize: Prize) {
        textIn(prizePageCardTestTag(prize.id), prize.name.displayed(), substring = false).assertIsDisplayed()
        textIn(prizePageCardTestTag(prize.id), prize.group.name, substring = true).assertIsDisplayed()
    }

    fun checkTextDisplayed(text: String) {
        composeUiTest.onNodeWithText(text).assertIsDisplayed()
    }

    fun checkPrizeOpened(page: Int) {
        assertEquals(listOf(page), openedPrizePages)
    }

    fun checkBackInvoked(times: Int) {
        assertEquals(times, backCount)
    }

    fun checkOverlayCloseInvoked(times: Int) {
        assertEquals(times, overlayCloseCount)
    }

    // The text a tagged element carries sits on the nodes below it rather than on the tagged node
    // itself, which the unmerged tree keeps reachable whether or not the tagged node merges them.
    private fun textIn(testTag: String, text: String, substring: Boolean): SemanticsNodeInteraction {
        return composeUiTest.onNode(
            hasText(text, substring = substring) and hasAnyAncestor(hasTestTag(testTag)),
            useUnmergedTree = true,
        )
    }

    // The screen picks the side of a [MultiLangText] from the same locale the test runs under.
    private fun MultiLangText.displayed(): String {
        return of(if (Locale.current.language == "ja") DisplayLanguage.Japanese else DisplayLanguage.English)
    }
}
