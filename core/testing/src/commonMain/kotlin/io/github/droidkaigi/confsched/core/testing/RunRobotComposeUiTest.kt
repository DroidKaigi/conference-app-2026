package io.github.droidkaigi.confsched.core.testing

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import io.github.droidkaigi.confsched.core.preview.SCREEN_PREVIEW_HEIGHT_DP
import io.github.droidkaigi.confsched.core.preview.SCREEN_PREVIEW_WIDTH_DP

// Robot captures share the screen previews' phone-sized frame, at 2x density so text and icons
// keep the sharpness of an xhdpi device.
internal const val ROBOT_SCREEN_DENSITY = 2f
internal const val ROBOT_SCREEN_WIDTH_PX = SCREEN_PREVIEW_WIDTH_DP * ROBOT_SCREEN_DENSITY
internal const val ROBOT_SCREEN_HEIGHT_PX = SCREEN_PREVIEW_HEIGHT_DP * ROBOT_SCREEN_DENSITY

/**
 * Runs a Robot scenario in a Compose test host sized like a phone screen. The Skiko targets take
 * the size above; the default test surface (1024x768 at 1x) is a desktop window no supported
 * device has.
 */
@OptIn(ExperimentalTestApi::class)
internal expect fun runRobotComposeUiTest(block: suspend ComposeUiTest.() -> Unit)
