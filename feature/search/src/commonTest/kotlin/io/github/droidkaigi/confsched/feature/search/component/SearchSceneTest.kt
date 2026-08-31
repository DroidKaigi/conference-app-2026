package io.github.droidkaigi.confsched.feature.search.component

import androidx.compose.ui.graphics.Color
import io.github.droidkaigi.confsched.core.designsystem.KaigiIllustrationColors
import io.github.droidkaigi.confsched.core.designsystem.toMaterialColorScheme
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Mascot
import kotlin.test.Test
import kotlin.test.assertEquals

class SearchSceneTest {
    @Test
    fun an_app_seed_selects_a_stable_scene_pair_and_mascot() {
        assertEquals(
            expected = SearchSceneSelection(
                initialDirection = SearchSceneDirection.Magnifier,
                noMatchDirection = SearchSceneDirection.EmptyBox,
                mascot = Mascot.D,
            ),
            actual = searchSceneSelection(2026),
        )
    }

    @Test
    fun every_scene_path_builds() {
        val colors = KaigiColorScheme.MorningMist.toMaterialColorScheme()
        val illustrationColors = KaigiIllustrationColors(
            skyPanel = Color.Black,
            onSkyPanel = Color.White,
            lanternGlow = Color.Yellow,
        )

        SearchSceneDirection.entries.forEach { direction ->
            searchSceneVector(direction, colors, illustrationColors)
        }
    }
}
