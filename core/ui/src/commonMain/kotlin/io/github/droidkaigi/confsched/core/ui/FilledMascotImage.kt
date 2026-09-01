package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.LocalKaigiIllustrationColors
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.generated.resources.Res
import io.github.droidkaigi.confsched.core.ui.generated.resources.mascot_a_filled_body
import io.github.droidkaigi.confsched.core.ui.generated.resources.mascot_a_filled_line
import io.github.droidkaigi.confsched.core.ui.generated.resources.mascot_b_filled_body
import io.github.droidkaigi.confsched.core.ui.generated.resources.mascot_b_filled_line
import io.github.droidkaigi.confsched.core.ui.generated.resources.mascot_c_filled_body
import io.github.droidkaigi.confsched.core.ui.generated.resources.mascot_c_filled_line
import io.github.droidkaigi.confsched.core.ui.generated.resources.mascot_d_filled_body
import io.github.droidkaigi.confsched.core.ui.generated.resources.mascot_d_filled_line
import io.github.droidkaigi.confsched.core.ui.generated.resources.mascot_e_filled_body
import io.github.droidkaigi.confsched.core.ui.generated.resources.mascot_e_filled_line
import io.github.droidkaigi.confsched.core.ui.generated.resources.mascot_f_filled_body
import io.github.droidkaigi.confsched.core.ui.generated.resources.mascot_f_filled_line
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/** The filled mascot, its body and its line drawn as separate layers so each takes its own color. */
@Composable
fun FilledMascotImage(
    mascot: Mascot,
    height: Dp,
    bodyColor: Color,
    lineColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(modifier.size(width = mascot.filledMascotWidthAt(height), height = height)) {
        Image(
            painter = painterResource(mascot.filledBodyResource),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            colorFilter = ColorFilter.tint(bodyColor),
        )
        Image(
            painter = painterResource(mascot.filledLineResource),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            colorFilter = ColorFilter.tint(lineColor),
        )
    }
}

/** Every mascot is drawn 50 units tall and as wide as its own art, so a height alone fixes the size. */
fun Mascot.filledMascotWidthAt(height: Dp): Dp = height * when (this) {
    Mascot.A -> 57f / 50f
    Mascot.B -> 54f / 50f
    Mascot.C -> 47f / 50f
    Mascot.D -> 55f / 50f
    Mascot.E -> 43f / 50f
    Mascot.F -> 48f / 50f
}

private val Mascot.filledBodyResource: DrawableResource
    get() = when (this) {
        Mascot.A -> Res.drawable.mascot_a_filled_body
        Mascot.B -> Res.drawable.mascot_b_filled_body
        Mascot.C -> Res.drawable.mascot_c_filled_body
        Mascot.D -> Res.drawable.mascot_d_filled_body
        Mascot.E -> Res.drawable.mascot_e_filled_body
        Mascot.F -> Res.drawable.mascot_f_filled_body
    }

private val Mascot.filledLineResource: DrawableResource
    get() = when (this) {
        Mascot.A -> Res.drawable.mascot_a_filled_line
        Mascot.B -> Res.drawable.mascot_b_filled_line
        Mascot.C -> Res.drawable.mascot_c_filled_line
        Mascot.D -> Res.drawable.mascot_d_filled_line
        Mascot.E -> Res.drawable.mascot_e_filled_line
        Mascot.F -> Res.drawable.mascot_f_filled_line
    }

@Preview
@Composable
private fun FilledMascotImagePreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        FilledMascotImage(
            mascot = Mascot.E,
            height = 58.dp,
            bodyColor = LocalKaigiIllustrationColors.current.onSkyPanel,
            lineColor = LocalKaigiIllustrationColors.current.skyPanel,
        )
    }
}
