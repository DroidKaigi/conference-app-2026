package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.PreviewImage
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import org.jetbrains.compose.resources.painterResource

/**
 * The square a person's own picture is shown in.
 *
 * The picture comes from outside the app, so both the edge it is cut to and the line drawn
 * around it are even: a wobbling one would read as the picture itself being misshapen. The
 * hand-sketched outline is reserved for what the app draws, which is why this shares only the
 * corner rounding with [KaigiPlaceholderAvatar].
 *
 * @param imageUrl where the picture is loaded from.
 * @param contentDescription what the picture is, for a screen reader.
 * @param size the side of the square.
 * @param modifier the [Modifier] applied to the avatar.
 * @param borderColor the colour of the line around the picture, which separates a pale one
 *   from the surface behind it.
 */
@Composable
fun KaigiAvatar(
    imageUrl: String,
    contentDescription: String?,
    size: Dp,
    modifier: Modifier = Modifier,
    borderColor: Color = MaterialTheme.colorScheme.outline,
) {
    RemoteImage(
        imageUrl = imageUrl,
        contentDescription = contentDescription,
        modifier = modifier
            .size(size)
            .clip(KaigiAvatarDefaults.shape)
            .border(
                width = KaigiAvatarDefaults.borderThickness,
                color = borderColor,
                shape = KaigiAvatarDefaults.shape,
            ),
    )
}

/**
 * The square standing in for a person the app has no picture of, holding initials or an icon.
 *
 * What it holds is the app's own drawing, so it carries the hand-sketched outline that
 * [KaigiAvatar] does without.
 *
 * @param seed the value the outline is drawn from. The same seed always produces the same
 *   outline, so give neighbouring avatars different ones or a list of them reads as a repeat.
 * @param size the side of the square.
 * @param modifier the [Modifier] applied to the avatar.
 * @param containerColor the colour filling the square behind [content].
 * @param contentColor the colour [content] draws in, provided as [LocalContentColor].
 * @param borderColor the colour of the outline.
 * @param content the initials or icon standing in for the person.
 */
@Composable
fun KaigiPlaceholderAvatar(
    seed: Int,
    size: Dp,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    content: @Composable BoxScope.() -> Unit,
) {
    val combinedSeed = combineSketchSeed(seed)
    val shape = SketchRoundRectShape(
        seed = combinedSeed,
        roughness = KaigiAvatarDefaults.roughness,
        tremor = KaigiAvatarDefaults.tremor,
        cornerRadius = size * KaigiAvatarDefaults.CORNER_RADIUS_RATIO,
        borderThickness = KaigiAvatarDefaults.borderThickness,
    )
    Box(modifier = modifier.size(size)) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .background(containerColor),
            contentAlignment = Alignment.Center,
        ) {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                content()
            }
        }
        Box(Modifier.matchParentSize().sketchBorder(shape, borderColor))
    }
}

/**
 * The square a speaker is shown in: their own picture, and the mascot of the room they speak in
 * whenever that picture is not there — while it loads, once it fails, and when there is none.
 *
 * The frame stays the even one of [KaigiAvatar] rather than the sketched one of
 * [KaigiPlaceholderAvatar], even though the mascot is the app's own drawing: the two states swap
 * as a load resolves, so a frame that changed with them would twitch through every row of a list.
 *
 * @param iconUrl where the speaker's picture is loaded from, or null when they have none.
 * @param mascot the character standing in for the picture.
 * @param contentDescription what the picture is, for a screen reader.
 * @param size the side of the square.
 * @param modifier the [Modifier] applied to the avatar.
 * @param containerColor the color filling the square behind the mascot.
 * @param contentColor the color the mascot is drawn in.
 * @param borderColor the color of the line around the square.
 */
@Composable
fun KaigiSpeakerAvatar(
    iconUrl: String?,
    mascot: Mascot,
    contentDescription: String?,
    size: Dp,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    borderColor: Color = MaterialTheme.colorScheme.outline,
) {
    val frame = modifier
        .size(size)
        .clip(KaigiAvatarDefaults.shape)
        .border(
            width = KaigiAvatarDefaults.borderThickness,
            color = borderColor,
            shape = KaigiAvatarDefaults.shape,
        )
    val mascotFace: @Composable () -> Unit = {
        SpeakerMascot(
            mascot = mascot,
            size = size,
            containerColor = containerColor,
            contentColor = contentColor,
        )
    }
    if (iconUrl == null) {
        Box(modifier = frame) { mascotFace() }
    } else {
        RemoteImageWithPlaceholder(
            imageUrl = iconUrl,
            contentDescription = contentDescription,
            modifier = frame,
            placeholder = mascotFace,
        )
    }
}

@Composable
private fun SpeakerMascot(
    mascot: Mascot,
    size: Dp,
    containerColor: Color,
    contentColor: Color,
) {
    Box(
        modifier = Modifier.fillMaxSize().background(containerColor),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(mascot.cardArt ?: mascotFArt),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().padding(size * KaigiAvatarDefaults.MASCOT_INSET_RATIO),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(contentColor),
        )
    }
}

object KaigiAvatarDefaults {
    /** The share of the side the corners are rounded by, the same at every size. */
    const val CORNER_RADIUS_RATIO = 0.4f

    val shape = RoundedCornerShape(percent = (CORNER_RADIUS_RATIO * 100).toInt())

    val borderThickness = 1.5.dp

    /** The share of the side kept clear around the mascot, the same at every size. */
    const val MASCOT_INSET_RATIO = 0.15f

    val roughness: Dp @Composable get() = scaleSketchAmplitude(0.4.dp)
    val tremor: Dp @Composable get() = scaleSketchAmplitude(0.15.dp)

    val initialsStyle
        @Composable get() = MaterialTheme.typography.titleMedium
}

@Preview
@Composable
private fun KaigiAvatarPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            KaigiAvatar(
                imageUrl = PreviewImage.AvatarSample.imageUrl,
                contentDescription = null,
                size = 100.dp,
            )
            KaigiPlaceholderAvatar(seed = 851, size = 100.dp) {
                Text("C01", style = KaigiAvatarDefaults.initialsStyle)
            }
            KaigiSpeakerAvatar(
                iconUrl = null,
                mascot = Mascot.A,
                contentDescription = null,
                size = 100.dp,
            )
            KaigiSpeakerAvatar(
                iconUrl = null,
                mascot = Mascot.B,
                contentDescription = null,
                size = 48.dp,
            )
            KaigiSpeakerAvatar(
                iconUrl = null,
                mascot = Mascot.C,
                contentDescription = null,
                size = 32.dp,
            )
            KaigiSpeakerAvatar(
                iconUrl = null,
                mascot = Mascot.E,
                contentDescription = null,
                size = 24.dp,
            )
        }
    }
}
