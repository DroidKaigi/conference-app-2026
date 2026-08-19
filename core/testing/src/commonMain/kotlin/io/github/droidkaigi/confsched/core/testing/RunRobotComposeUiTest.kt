package io.github.droidkaigi.confsched.core.testing

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi

// The phone-like viewport the screenshot pipeline captures at: 360dp x 800dp at 2x density,
// matching the w360dp-h800dp-xhdpi configuration the goldens were originally recorded with.
internal const val ROBOT_SCREEN_WIDTH_PX = 720f
internal const val ROBOT_SCREEN_HEIGHT_PX = 1600f
internal const val ROBOT_SCREEN_DENSITY = 2f

/**
 * Runs a Robot scenario in a Compose test host sized like a phone screen. The Skiko targets take
 * the size above; the default test surface (1024x768 at 1x) is a desktop window no supported
 * device has.
 */
@OptIn(ExperimentalTestApi::class)
internal expect fun runRobotComposeUiTest(block: suspend ComposeUiTest.() -> Unit)
