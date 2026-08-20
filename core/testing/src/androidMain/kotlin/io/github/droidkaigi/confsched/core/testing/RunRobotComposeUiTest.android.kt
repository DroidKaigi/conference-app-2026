package io.github.droidkaigi.confsched.core.testing

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest

@OptIn(ExperimentalTestApi::class)
internal actual fun runRobotComposeUiTest(block: suspend ComposeUiTest.() -> Unit) {
    runComposeUiTest(block = block)
}
