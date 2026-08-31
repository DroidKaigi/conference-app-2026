package io.github.droidkaigi.confsched.core.ui

import io.github.droidkaigi.confsched.core.designsystem.toMaterialColorScheme
import io.github.droidkaigi.confsched.core.model.DoodleInk
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import kotlin.test.Test
import kotlin.test.assertEquals

class DoodleInkPaletteTest {
    @Test
    fun a_scheme_painting_two_roles_alike_offers_one_ink_for_the_pair() {
        val scheme = KaigiColorScheme.CampfireNight.toMaterialColorScheme()

        val palette = DoodleInkPalette(
            ink = DoodleInkStyle(color = scheme.onSurface, rimColor = scheme.surface),
            band = DoodleInkStyle(color = scheme.primary, rimColor = scheme.onPrimary),
            paper = DoodleInkStyle(color = scheme.surface, rimColor = scheme.onSurface),
            banner = DoodleInkStyle(color = scheme.secondaryContainer, rimColor = scheme.onSecondaryContainer),
        )

        assertEquals(scheme.onSurface, scheme.primary, "CampfireNight no longer paints the ink and the band alike")
        assertEquals(listOf(DoodleInk.Ink, DoodleInk.Paper, DoodleInk.Banner), palette.distinctInks())
    }

    @Test
    fun a_scheme_telling_every_role_apart_offers_them_all() {
        val scheme = KaigiColorScheme.MorningMist.toMaterialColorScheme()

        val palette = DoodleInkPalette(
            ink = DoodleInkStyle(color = scheme.onSurface, rimColor = scheme.surface),
            band = DoodleInkStyle(color = scheme.primary, rimColor = scheme.onPrimary),
            paper = DoodleInkStyle(color = scheme.surface, rimColor = scheme.onSurface),
            banner = DoodleInkStyle(color = scheme.secondaryContainer, rimColor = scheme.onSecondaryContainer),
        )

        assertEquals(DoodleInk.entries, palette.distinctInks())
    }
}
