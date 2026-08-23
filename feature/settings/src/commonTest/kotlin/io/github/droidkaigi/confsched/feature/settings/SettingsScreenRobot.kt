package io.github.droidkaigi.confsched.feature.settings

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.model.Appearance
import io.github.droidkaigi.confsched.core.model.AppearanceSettings
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.testing.Robot
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class SettingsScreenRobot(composeUiTest: ComposeUiTest) : Robot(composeUiTest) {

    private val graph = createGraph<SettingsScreenTestGraph>()
    private var backCount = 0

    fun setupSettings(settings: AppearanceSettings) {
        graph.appearanceSubscriptionKey.set(Appearance(colorScheme = KaigiColorScheme.MorningMist, settings = settings))
    }

    fun setupContent() {
        setScreenContent {
            context(graph.screenContext) {
                SettingsScreenRoot(onNavigateBack = { backCount++ })
            }
        }
    }

    fun clickOption(label: String) {
        composeUiTest.onNodeWithText(label).performClick()
        composeUiTest.waitForIdle()
    }

    fun checkOptionDisplayed(label: String) {
        composeUiTest.onNodeWithText(label).assertIsDisplayed()
    }

    fun checkOptionSelected(label: String) {
        composeUiTest.onNodeWithText(label).assertIsSelected()
    }

    fun checkOptionNotSelected(label: String) {
        composeUiTest.onNodeWithText(label).assertIsNotSelected()
    }

    fun clickBack() {
        composeUiTest.onNodeWithContentDescription("Back").performClick()
        composeUiTest.waitForIdle()
    }

    fun checkSettingsWritten(settings: AppearanceSettings) {
        assertEquals(settings, graph.appearanceSettingsMutationKey.invocations.tryReceive().getOrNull())
    }

    fun checkBackInvoked(times: Int) {
        assertEquals(times, backCount)
    }
}
