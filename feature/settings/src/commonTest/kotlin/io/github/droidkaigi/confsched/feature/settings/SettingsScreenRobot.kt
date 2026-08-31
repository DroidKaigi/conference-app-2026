package io.github.droidkaigi.confsched.feature.settings

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.model.Appearance
import io.github.droidkaigi.confsched.core.model.AppearanceSettings
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.KaigiFontFamily
import io.github.droidkaigi.confsched.core.model.SketchStrength
import io.github.droidkaigi.confsched.core.testing.Robot
import io.github.droidkaigi.confsched.core.ui.KAIGI_TOP_APP_BAR_BACK_BUTTON_TEST_TAG
import io.github.droidkaigi.confsched.feature.settings.component.fontFamilyOptionItemTestTag
import io.github.droidkaigi.confsched.feature.settings.component.sketchStrengthOptionItemTestTag
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

    fun clickOption(fontFamily: KaigiFontFamily) {
        composeUiTest.onNodeWithTag(fontFamilyOptionItemTestTag(fontFamily)).performClick()
        composeUiTest.waitForIdle()
    }

    fun clickOption(sketchStrength: SketchStrength) {
        composeUiTest.onNodeWithTag(sketchStrengthOptionItemTestTag(sketchStrength)).performClick()
        composeUiTest.waitForIdle()
    }

    fun checkOptionDisplayed(fontFamily: KaigiFontFamily) {
        composeUiTest.onNodeWithTag(fontFamilyOptionItemTestTag(fontFamily)).assertIsDisplayed()
    }

    fun checkOptionDisplayed(sketchStrength: SketchStrength) {
        composeUiTest.onNodeWithTag(sketchStrengthOptionItemTestTag(sketchStrength)).assertIsDisplayed()
    }

    fun checkOptionSelected(fontFamily: KaigiFontFamily) {
        composeUiTest.onNodeWithTag(fontFamilyOptionItemTestTag(fontFamily)).assertIsSelected()
    }

    fun checkOptionSelected(sketchStrength: SketchStrength) {
        composeUiTest.onNodeWithTag(sketchStrengthOptionItemTestTag(sketchStrength)).assertIsSelected()
    }

    fun checkOptionNotSelected(fontFamily: KaigiFontFamily) {
        composeUiTest.onNodeWithTag(fontFamilyOptionItemTestTag(fontFamily)).assertIsNotSelected()
    }

    fun checkOptionNotSelected(sketchStrength: SketchStrength) {
        composeUiTest.onNodeWithTag(sketchStrengthOptionItemTestTag(sketchStrength)).assertIsNotSelected()
    }

    fun clickBack() {
        composeUiTest.onNodeWithTag(KAIGI_TOP_APP_BAR_BACK_BUTTON_TEST_TAG).performClick()
        composeUiTest.waitForIdle()
    }

    fun checkSettingsWritten(settings: AppearanceSettings) {
        assertEquals(settings, graph.appearanceSettingsMutationKey.invocations.tryReceive().getOrNull())
    }

    fun checkBackInvoked(times: Int) {
        assertEquals(times, backCount)
    }
}
