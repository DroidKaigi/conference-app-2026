package io.github.droidkaigi.confsched.app

import io.github.droidkaigi.confsched.core.designsystem.toMaterialColorScheme
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import kotlin.test.Test
import kotlin.test.assertEquals

class StatusBarIconAppearanceTest {

    // Every top app bar fills the band with inverseSurface, so this is the appearance each theme
    // asks for. DeepTeal's bar is the light one, and the case a 0.5 luminance midpoint would
    // place on the losing side.
    private val darkIconsOverTopAppBar = mapOf(
        KaigiColorScheme.MorningMist to false,
        KaigiColorScheme.DeepTeal to true,
        KaigiColorScheme.SakuraPlum to false,
        KaigiColorScheme.Terracotta to false,
        KaigiColorScheme.CampfireNight to false,
    )

    @Test
    fun eachThemePicksTheIconsItsTopAppBarCallsFor() {
        KaigiColorScheme.entries.forEach { scheme ->
            assertEquals(
                darkIconsOverTopAppBar.getValue(scheme),
                scheme.toMaterialColorScheme().inverseSurface.prefersDarkIcons(),
                "$scheme picks the wrong icons over its top app bar",
            )
        }
    }
}
