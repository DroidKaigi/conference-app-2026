package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.PreviewImage
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme

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
 * The square standing in for a speaker the app has no picture of, holding a drawn face.
 *
 * The outline is even rather than sketched, unlike [KaigiPlaceholderAvatar]: a wobbling one
 * reads as the face itself being misshapen, and at the smallest size it degrades into corners.
 *
 * @param size the side of the square.
 * @param modifier the [Modifier] applied to the avatar.
 * @param containerColor the colour filling the square behind the face.
 * @param contentColor the colour the face is drawn in.
 * @param borderColor the colour of the line around the square.
 */
@Composable
fun KaigiFaceAvatar(
    size: Dp,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    borderColor: Color = MaterialTheme.colorScheme.outline,
) {
    val featureThickness = (size * KaigiAvatarDefaults.FACE_FEATURE_RATIO)
        .coerceAtLeast(KaigiAvatarDefaults.minFaceFeatureThickness)
    Canvas(
        modifier = modifier
            .size(size)
            .clip(KaigiAvatarDefaults.shape)
            .background(containerColor)
            .border(
                width = size * KaigiAvatarDefaults.FACE_OUTLINE_RATIO,
                color = borderColor,
                shape = KaigiAvatarDefaults.shape,
            ),
    ) {
        val stroke = Stroke(width = featureThickness.toPx(), cap = StrokeCap.Round)
        val width = this.size.width
        val height = this.size.height
        listOf(0.34f, 0.66f).forEach { eyeX ->
            drawPath(
                path = Path().apply {
                    moveTo(width * eyeX, height * 0.38f)
                    quadraticTo(width * (eyeX + 0.04f), height * 0.43f, width * eyeX, height * 0.48f)
                },
                color = contentColor,
                style = stroke,
            )
        }
        drawPath(
            path = Path().apply {
                moveTo(width * 0.33f, height * 0.61f)
                quadraticTo(width * 0.5f, height * 0.75f, width * 0.67f, height * 0.61f)
            },
            color = contentColor,
            style = stroke,
        )
    }
}

object KaigiAvatarDefaults {
    /** The share of the side the corners are rounded by, the same at every size. */
    const val CORNER_RADIUS_RATIO = 0.4f

    val shape = RoundedCornerShape(percent = (CORNER_RADIUS_RATIO * 100).toInt())

    val borderThickness = 1.5.dp

    /** The share of the side the drawn face's eyes and mouth are stroked at. */
    const val FACE_FEATURE_RATIO = 0.0375f

    /** The share of the side the line around a drawn face is stroked at. */
    const val FACE_OUTLINE_RATIO = 0.0125f

    val minFaceFeatureThickness = 0.9.dp
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
            KaigiFaceAvatar(size = 100.dp)
            KaigiFaceAvatar(size = 48.dp)
            KaigiFaceAvatar(size = 24.dp)
        }
    }
}
