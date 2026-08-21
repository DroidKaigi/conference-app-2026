package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme

/**
 * A round icon button on a hand-sketched disc.
 *
 * It sits on the header band, so it fills with the band's contrasting colour and tints its
 * icon back to the band. A control the design leaves bare passes a transparent
 * [containerColor]: the disc stops being drawn, but the press still lands on its outline.
 *
 * @param seed the value the disc is drawn from. The same seed always produces the same disc,
 *   so give neighbouring buttons different ones or a row of them reads as a repeat.
 * @param onClick called when the button is clicked.
 * @param modifier the [Modifier] applied to the button.
 * @param size the diameter of the disc.
 * @param containerColor the colour filling the disc.
 * @param contentColor the colour [content] draws in, provided as [LocalContentColor].
 * @param content the icon the disc holds.
 */
@Composable
fun KaigiIconButton(
    seed: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = KaigiIconButtonDefaults.size,
    containerColor: Color = MaterialTheme.colorScheme.inverseOnSurface,
    contentColor: Color = MaterialTheme.colorScheme.inverseSurface,
    content: @Composable () -> Unit,
) {
    val combinedSeed = combineSketchSeed(seed)
    Box(
        modifier = modifier
            .size(size)
            .clip(SketchRoundRectShape(seed = combinedSeed, cornerRadius = size / 2))
            .background(containerColor)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor, content = content)
    }
}

object KaigiIconButtonDefaults {
    val size = 38.dp
}

@Preview
@Composable
private fun KaigiIconButtonPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.inverseSurface)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            KaigiIconButton(seed = 777, onClick = {}) {
                Icon(Icons.Filled.Search, contentDescription = null)
            }
            KaigiIconButton(seed = 778, onClick = {}) {
                Icon(Icons.Filled.DateRange, contentDescription = null)
            }
        }
    }
}
