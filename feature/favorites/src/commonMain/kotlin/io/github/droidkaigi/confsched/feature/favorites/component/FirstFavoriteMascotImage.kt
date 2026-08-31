package io.github.droidkaigi.confsched.feature.favorites.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.Res
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.first_favorite_mascot_a
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.first_favorite_mascot_a_body
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.first_favorite_mascot_b
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.first_favorite_mascot_b_body
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.first_favorite_mascot_c
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.first_favorite_mascot_c_body
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.first_favorite_mascot_d
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.first_favorite_mascot_d_body
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.first_favorite_mascot_e
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.first_favorite_mascot_e_body
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.first_favorite_mascot_f
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.first_favorite_mascot_f_body
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/** The filled mascot, its paper and its ink drawn as separate layers so each takes its own theme role. */
@Composable
internal fun FirstFavoriteMascotImage(mascot: Mascot, height: Dp, modifier: Modifier = Modifier) {
    Box(modifier.size(width = mascot.firstFavoriteWidthAt(height), height = height)) {
        Image(
            painter = painterResource(mascot.filledPaperResource),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            colorFilter = ColorFilter.tint(LocalKaigiIllustrationColors.current.onSkyPanel),
        )
        Image(
            painter = painterResource(mascot.filledInkResource),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            colorFilter = ColorFilter.tint(LocalKaigiIllustrationColors.current.skyPanel),
        )
    }
}

/** Every mascot is drawn 50 units tall and as wide as its own art, so a height alone fixes the size. */
internal fun Mascot.firstFavoriteWidthAt(height: Dp): Dp = height * when (this) {
    Mascot.A -> 57f / 50f
    Mascot.B -> 54f / 50f
    Mascot.C -> 47f / 50f
    Mascot.D -> 55f / 50f
    Mascot.E -> 43f / 50f
    Mascot.F -> 48f / 50f
}

private val Mascot.filledPaperResource: DrawableResource
    get() = when (this) {
        Mascot.A -> Res.drawable.first_favorite_mascot_a_body
        Mascot.B -> Res.drawable.first_favorite_mascot_b_body
        Mascot.C -> Res.drawable.first_favorite_mascot_c_body
        Mascot.D -> Res.drawable.first_favorite_mascot_d_body
        Mascot.E -> Res.drawable.first_favorite_mascot_e_body
        Mascot.F -> Res.drawable.first_favorite_mascot_f_body
    }

private val Mascot.filledInkResource: DrawableResource
    get() = when (this) {
        Mascot.A -> Res.drawable.first_favorite_mascot_a
        Mascot.B -> Res.drawable.first_favorite_mascot_b
        Mascot.C -> Res.drawable.first_favorite_mascot_c
        Mascot.D -> Res.drawable.first_favorite_mascot_d
        Mascot.E -> Res.drawable.first_favorite_mascot_e
        Mascot.F -> Res.drawable.first_favorite_mascot_f
    }

@Preview
@Composable
private fun FirstFavoriteMascotImagePreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        FirstFavoriteMascotImage(mascot = Mascot.E, height = 58.dp)
    }
}
