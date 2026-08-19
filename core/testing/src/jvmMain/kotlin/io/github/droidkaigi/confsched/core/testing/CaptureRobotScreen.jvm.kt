package io.github.droidkaigi.confsched.core.testing

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.InternalRoborazziApi
import com.github.takahirom.roborazzi.provideRoborazziContext
import io.github.takahirom.roborazzi.captureRoboImage
import java.io.File

@OptIn(ExperimentalTestApi::class, InternalRoborazziApi::class)
internal actual fun ComposeUiTest.captureRobotScreen(name: String) {
    // Roborazzi resolves a bare name against the working directory, so the capture is placed in the
    // configured output directory, beside the goldens the preview tests write.
    val filePath = File(provideRoborazziContext().outputDirectory, "$name.png").path
    onRoot().captureRoboImage(filePath = filePath)
}
