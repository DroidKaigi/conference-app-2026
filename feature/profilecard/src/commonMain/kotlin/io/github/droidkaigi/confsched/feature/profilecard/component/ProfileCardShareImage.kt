package io.github.droidkaigi.confsched.feature.profilecard.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.AvatarImage
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.model.Sketchiness
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.Res
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.share_background_campfire_night
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.share_background_deep_teal
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.share_background_morning_mist
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.share_background_sakura_plum
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.share_background_terracotta
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.math.min

/**
 * The picture the card is shared as: both faces laid on the greeting backdrop, at the social-card
 * proportions the design file fixes. The backdrop is a flat image per [colorScheme] because the
 * blob over the DroidKaigi mark is semi-transparent, which leaves the mark a colour no theme
 * variable carries.
 */
@Composable
internal fun ProfileCardShareImage(
    nickName: String,
    occupation: String,
    link: String,
    mascot: Mascot,
    sketchiness: Sketchiness,
    avatarImage: AvatarImage?,
    colorScheme: KaigiColorScheme,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.size(ProfileCardShareImageDefaults.size)) {
        Image(
            painter = painterResource(colorScheme.shareBackground()),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
        )
        ProfileCardFront(
            nickName = nickName,
            occupation = occupation,
            mascot = mascot,
            sketchiness = sketchiness,
            taped = true,
            avatarImage = avatarImage,
            modifier = Modifier.cardSlot(
                origin = ProfileCardShareImageDefaults.frontOrigin,
                rotationDegrees = ProfileCardShareImageDefaults.frontRotationDegrees,
            ),
        )
        ProfileCardBack(
            nickName = nickName,
            link = link,
            mascot = mascot,
            sketchiness = sketchiness,
            taped = true,
            modifier = Modifier.cardSlot(
                origin = ProfileCardShareImageDefaults.backOrigin,
                rotationDegrees = ProfileCardShareImageDefaults.backRotationDegrees,
            ),
        )
    }
}

/**
 * Places a card face in the slot whose top-start corner is [origin], scaled to fit and turned by
 * [rotationDegrees]. Rotation and scale run through a graphics layer rather than through layout so
 * the washi tape a face draws past its own bounds stays unclipped.
 */
private fun Modifier.cardSlot(origin: DpOffset, rotationDegrees: Float): Modifier {
    val slot = ProfileCardShareImageDefaults.cardSlot
    val face = ProfileCardFaceDefaults.size
    val faceScale = min(slot.width / face.width, slot.height / face.height)
    val centringInset = (slot.width - face.width * faceScale) / 2
    return offset(x = origin.x + centringInset, y = origin.y)
        .graphicsLayer {
            transformOrigin = TransformOrigin(0f, 0f)
            rotationZ = rotationDegrees
            scaleX = faceScale
            scaleY = faceScale
        }
}

private fun KaigiColorScheme.shareBackground(): DrawableResource = when (this) {
    KaigiColorScheme.MorningMist -> Res.drawable.share_background_morning_mist
    KaigiColorScheme.DeepTeal -> Res.drawable.share_background_deep_teal
    KaigiColorScheme.SakuraPlum -> Res.drawable.share_background_sakura_plum
    KaigiColorScheme.Terracotta -> Res.drawable.share_background_terracotta
    KaigiColorScheme.CampfireNight -> Res.drawable.share_background_campfire_night
}

private object ProfileCardShareImageDefaults {
    /** The canvas every social network previews a shared link at. */
    val size = DpSize(1200.dp, 630.dp)

    val cardSlot = DpSize(340.dp, 500.dp)
    val frontOrigin = DpOffset(420.dp, 58.dp)
    val backOrigin = DpOffset(760.dp, 88.dp)

    // Figma's Rotation field is the mirror of a graphics layer's rotationZ; these negate the
    // source's -5°/5° to match the render.
    val frontRotationDegrees = 5f
    val backRotationDegrees = -5f
}

@LocalePreviews
@Composable
private fun ProfileCardShareImagePreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardShareImage(
            nickName = "droidkaigi",
            occupation = "Software Engineer",
            link = "https://example.com",
            mascot = Mascot.Koala,
            sketchiness = Sketchiness.Normal,
            avatarImage = null,
            colorScheme = colorScheme,
        )
    }
}
