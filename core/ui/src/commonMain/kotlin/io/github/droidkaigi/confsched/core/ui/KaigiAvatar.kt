package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
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

object KaigiAvatarDefaults {
    /** The share of the side the corners are rounded by, the same at every size. */
    const val CORNER_RADIUS_RATIO = 0.4f

    val shape = RoundedCornerShape(percent = (CORNER_RADIUS_RATIO * 100).toInt())

    val borderThickness = 1.5.dp
    val roughness = 0.4.dp
    val tremor = 0.15.dp

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
            KaigiPlaceholderAvatar(seed = 851, size = 100.dp) {
                Text("C01", style = KaigiAvatarDefaults.initialsStyle)
            }
            KaigiPlaceholderAvatar(seed = 852, size = 52.dp) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
            }
            KaigiPlaceholderAvatar(seed = 853, size = 24.dp) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                )
            }
        }
    }
}
