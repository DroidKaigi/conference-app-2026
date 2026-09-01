package io.github.droidkaigi.confsched.feature.profilecard

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runSkikoComposeUiTest
import androidx.compose.ui.unit.Density
import io.github.droidkaigi.confsched.core.designsystem.KaigiTheme
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.KaigiFontFamily
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.model.SketchStrength
import io.github.droidkaigi.confsched.core.model.Sketchiness
import io.github.droidkaigi.confsched.feature.profilecard.component.PROFILE_CARD_VIEW_SHARE_BUTTON_TEST_TAG
import io.github.droidkaigi.confsched.feature.profilecard.component.ProfileCardView
import kotlin.test.Test
import kotlin.test.assertEquals

class ProfileCardShareImageCaptureTest {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun the_share_button_hands_over_an_image_at_the_share_canvas_size() = runSkikoComposeUiTest(
        // A phone-sized host at 2x, so the capture is shown to come out at its own fixed size
        // rather than at whatever the screen draws at.
        size = Size(600f, 1200f),
        density = Density(2f),
    ) {
        var captured: ImageBitmap? = null
        setContent {
            KaigiTheme(
                colorScheme = KaigiColorScheme.MorningMist,
                fontFamily = KaigiFontFamily.Default,
                sketchStrength = SketchStrength.Normal,
                sketchBaseSeed = 0,
            ) {
                ProfileCardView(
                    uiState = ProfileCardScreenUiState.Card(
                        nickName = "Speaker A",
                        occupation = "Software Engineer",
                        link = "https://example.com",
                        mascot = Mascot.C,
                        sketchiness = Sketchiness.Normal,
                        avatarImage = null,
                    ),
                    colorScheme = KaigiColorScheme.MorningMist,
                    onCardClick = {},
                    onEditClick = {},
                    onShareClick = { captured = it },
                )
            }
        }
        onNodeWithTag(PROFILE_CARD_VIEW_SHARE_BUTTON_TEST_TAG).performClick()
        waitUntil { captured != null }
        assertEquals(1200, captured?.width)
        assertEquals(630, captured?.height)
    }
}
