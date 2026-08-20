package io.github.droidkaigi.confsched.core.testing

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi

/**
 * Records what a scenario left on screen. Only the JVM target captures today; the other
 * targets run the same scenarios for their assertions alone.
 */
@OptIn(ExperimentalTestApi::class)
internal expect fun ComposeUiTest.captureRobotScreen(name: String)
