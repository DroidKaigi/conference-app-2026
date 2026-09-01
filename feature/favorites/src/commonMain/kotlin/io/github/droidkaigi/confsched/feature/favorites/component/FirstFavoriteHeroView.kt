package io.github.droidkaigi.confsched.feature.favorites.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.LocalKaigiIllustrationColors
import io.github.droidkaigi.confsched.core.designsystem.icon.FavoriteFilled
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.FilledMascotImage
import io.github.droidkaigi.confsched.core.ui.filledMascotWidthAt
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.Res
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.first_favorite_hand_bell
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.first_favorite_hand_bell_body
import org.jetbrains.compose.resources.painterResource

private val MascotHeight = 95.dp

/** Where the mascot's middle sits, so a wider character grows to both sides instead of into the bell. */
private val MascotCenterX = 128.dp

/** The mascot ringing a hand bell, over the hearts a first favorite scatters. */
@Composable
internal fun FirstFavoriteHeroView(mascot: Mascot, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth().height(104.dp)) {
        HeartMark(sizeDp = 15, xDp = 32, yDp = 4, rotationDegrees = 14f)
        HeartMark(sizeDp = 12, xDp = 248, yDp = 7, rotationDegrees = -16f)
        HeartMark(sizeDp = 10, xDp = 7, yDp = 44, rotationDegrees = 8f)
        FilledMascotImage(
            mascot = mascot,
            height = MascotHeight,
            bodyColor = LocalKaigiIllustrationColors.current.onSkyPanel,
            lineColor = LocalKaigiIllustrationColors.current.skyPanel,
            modifier = Modifier.offset(
                x = MascotCenterX - mascot.filledMascotWidthAt(MascotHeight) / 2,
                y = 9.dp,
            ),
        )
        Box(modifier = Modifier.offset(x = 162.dp, y = 30.dp).size(width = 60.dp, height = 51.dp)) {
            Image(
                painter = painterResource(Res.drawable.first_favorite_hand_bell_body),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                colorFilter = ColorFilter.tint(LocalKaigiIllustrationColors.current.lanternGlow),
            )
            Image(
                painter = painterResource(Res.drawable.first_favorite_hand_bell),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                colorFilter = ColorFilter.tint(LocalKaigiIllustrationColors.current.skyPanel),
            )
        }
    }
}

@Composable
private fun HeartMark(sizeDp: Int, xDp: Int, yDp: Int, rotationDegrees: Float) {
    Icon(
        imageVector = KaigiIcons.Default.FavoriteFilled,
        contentDescription = null,
        modifier = Modifier
            .offset(x = xDp.dp, y = yDp.dp)
            .rotate(rotationDegrees)
            .size(sizeDp.dp),
        tint = MaterialTheme.colorScheme.primary,
    )
}

@Preview
@Composable
private fun FirstFavoriteHeroViewPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        FirstFavoriteHeroView(mascot = Mascot.E)
    }
}
