package io.github.droidkaigi.confsched.core.testing

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runSkikoComposeUiTest
import androidx.compose.ui.unit.Density
import java.util.Locale

@OptIn(ExperimentalTestApi::class)
internal actual fun runRobotComposeUiTest(block: suspend ComposeUiTest.() -> Unit) {
    // Robots assert on English strings and captures are compared across machines, so resource
    // resolution must not follow the host machine's locale.
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
