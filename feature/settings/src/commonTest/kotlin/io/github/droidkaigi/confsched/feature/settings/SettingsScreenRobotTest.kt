package io.github.droidkaigi.confsched.feature.settings

import androidx.compose.ui.test.ExperimentalTestApi
import io.github.droidkaigi.confsched.core.model.AppearanceSettings
import io.github.droidkaigi.confsched.core.model.KaigiFontFamily
import io.github.droidkaigi.confsched.core.model.SketchStrength
import io.github.droidkaigi.confsched.core.testing.RobotTest
import io.github.droidkaigi.confsched.core.testing.runRobotTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class SettingsScreenRobotTest : RobotTest() {

    @Test
    fun settings_screen_behaviour() = runRobotTest(
        robotFactory = { SettingsScreenRobot(this) },
    ) {
        describe("when the stored settings have loaded") {
            doIt {
                setupSettings(AppearanceSettings.Default)
                setupContent()
            }
            itShould("show each group with the stored option marked") {
                checkOptionDisplayed(KaigiFontFamily.Default)
                checkOptionDisplayed(KaigiFontFamily.CourierPrime)
                checkOptionDisplayed(SketchStrength.Normal)
                checkOptionSelected(KaigiFontFamily.Default)
                checkOptionNotSelected(KaigiFontFamily.CourierPrime)
            }
            describe("and another font is tapped") {
                doIt {
                    clickOption(KaigiFontFamily.NotoSans)
                }
                itShould("write that font alongside the settings already stored") {
                    checkSettingsWritten(
                        AppearanceSettings.Default.copy(fontFamily = KaigiFontFamily.NotoSans),
                    )
                }
            }
            describe("and back is tapped") {
                doIt {
                    clickBack()
                }
                itShould("leave the screen") {
                    checkBackInvoked(times = 1)
                }
            }
        }
    }
}
