package io.github.droidkaigi.confsched.feature.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.model.AppearanceSettings
import io.github.droidkaigi.confsched.core.model.ColorSchemeSetting
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.KaigiFontFamily
import io.github.droidkaigi.confsched.core.model.SketchStrength
import io.github.droidkaigi.confsched.core.testing.runPresenterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsScreenPresenterTest {

    private val graph = createGraph<SettingsScreenTestGraph>()

    @Test
    fun the_stored_settings_reach_the_ui_state() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel ->
                settingsScreenPresenter(
                    screenChannel = channel,
                    appearanceSettings = AppearanceSettings(
                        colorSchemeSetting = ColorSchemeSetting.Fixed(KaigiColorScheme.DeepTeal),
                        fontFamily = KaigiFontFamily.CourierPrime,
                        sketchStrength = SketchStrength.Playful,
                    ),
                )
            },
        ) {
            val uiState = uiStates.awaitItem()
            assertEquals(KaigiFontFamily.CourierPrime, uiState.fontFamily)
            assertEquals(SketchStrength.Playful, uiState.sketchStrength)
            assertEquals(ColorSchemeSetting.Fixed(KaigiColorScheme.DeepTeal), uiState.colorSchemeSetting)
        }
    }

    @Test
    fun picking_a_font_writes_it_alongside_the_settings_already_stored() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel ->
                settingsScreenPresenter(
                    screenChannel = channel,
                    appearanceSettings = AppearanceSettings.Default.copy(
                        sketchStrength = SketchStrength.Subtle,
                    ),
                )
            },
        ) {
            uiStates.awaitItem()
            send(SettingsScreenAction.SelectFont(KaigiFontFamily.NotoSans))
            assertEquals(
                AppearanceSettings.Default.copy(
                    fontFamily = KaigiFontFamily.NotoSans,
                    sketchStrength = SketchStrength.Subtle,
                ),
                graph.appearanceSettingsMutationKey.invocations.receive(),
            )
        }
    }

    @Test
    fun picking_an_option_writes_the_settings_that_arrived_after_the_screen_opened() {
        var stored by mutableStateOf(AppearanceSettings.Default)
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel ->
                settingsScreenPresenter(
                    screenChannel = channel,
                    appearanceSettings = stored,
                )
            },
        ) {
            uiStates.awaitItem()
            stored = AppearanceSettings.Default.copy(sketchStrength = SketchStrength.Playful)
            assertEquals(SketchStrength.Playful, uiStates.awaitItem().sketchStrength)
            send(SettingsScreenAction.SelectFont(KaigiFontFamily.NotoSans))
            assertEquals(
                AppearanceSettings.Default.copy(
                    fontFamily = KaigiFontFamily.NotoSans,
                    sketchStrength = SketchStrength.Playful,
                ),
                graph.appearanceSettingsMutationKey.invocations.receive(),
            )
        }
    }

    @Test
    fun picking_a_theme_writes_that_scheme() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel ->
                settingsScreenPresenter(
                    screenChannel = channel,
                    appearanceSettings = AppearanceSettings.Default,
                )
            },
        ) {
            uiStates.awaitItem()
            send(SettingsScreenAction.SelectColorScheme(ColorSchemeSetting.Fixed(KaigiColorScheme.SakuraPlum)))
            assertEquals(
                ColorSchemeSetting.Fixed(KaigiColorScheme.SakuraPlum),
                graph.appearanceSettingsMutationKey.invocations.receive().colorSchemeSetting,
            )
        }
    }
}
