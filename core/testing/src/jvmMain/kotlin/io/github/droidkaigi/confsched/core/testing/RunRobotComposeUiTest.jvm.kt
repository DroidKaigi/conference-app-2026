package io.github.droidkaigi.confsched.core.testing

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runSkikoComposeUiTest
import androidx.compose.ui.unit.Density
import java.util.Locale

@OptIn(ExperimentalTestApi::class)
internal actual fun runRobotComposeUiTest(block: suspend ComposeUiTest.() -> Unit) {
    // Temporary: robots still locate nodes by English display strings, so resource resolution
    // must not follow the host locale until they move to test tags (#229).
    val previous = Locale.getDefault()
    Locale.setDefault(Locale.ENGLISH)
    try {
        runSkikoComposeUiTest(
            size = Size(ROBOT_SCREEN_WIDTH_PX, ROBOT_SCREEN_HEIGHT_PX),
            density = Density(ROBOT_SCREEN_DENSITY),
        ) { block() }
    } finally {
        Locale.setDefault(previous)
    }
}
