package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme

/**
 * A filled action in a hand-sketched pill, for the one action a section leads with.
 *
 * @param onClick called when the button is clicked.
 * @param seed the value the pill is drawn from. The same seed always produces the same pill,
 *   so give neighbouring buttons different ones or a row of them reads as a repeat.
 * @param modifier the [Modifier] applied to the button.
 * @param containerColor the colour filling the pill.
 * @param contentColor the colour [content] draws in, provided as [LocalContentColor].
 * @param content the label, and an icon leading it where the action takes one.
 */
@Composable
fun KaigiButton(
    onClick: () -> Unit,
    seed: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    content: @Composable RowScope.() -> Unit,
) {
    SketchButton(
        onClick = onClick,
        seed = seed,
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor,
        borderColor = containerColor,
        contentPadding = KaigiButtonDefaults.filledContentPadding,
        content = content,
    )
}

/**
 * An action in a hand-sketched pill drawn in outline, for the actions a section offers
 * alongside the one it leads with.
 *
 * @param onClick called when the button is clicked.
 * @param seed the value the pill is drawn from. The same seed always produces the same pill,
 *   so give neighbouring buttons different ones or a row of them reads as a repeat.
 * @param modifier the [Modifier] applied to the button.
 * @param contentColor the colour the outline and [content] take.
 * @param content the label, and an icon leading it where the action takes one.
 */
@Composable
fun KaigiOutlinedButton(
    onClick: () -> Unit,
    seed: Int,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    content: @Composable RowScope.() -> Unit,
) {
    SketchButton(
        onClick = onClick,
        seed = seed,
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = contentColor,
        borderColor = contentColor,
        contentPadding = KaigiButtonDefaults.outlinedContentPadding,
        content = content,
    )
}

@Composable
private fun SketchButton(
    onClick: () -> Unit,
    seed: Int,
    modifier: Modifier,
    containerColor: Color,
    contentColor: Color,
    borderColor: Color,
    contentPadding: PaddingValues,
    content: @Composable RowScope.() -> Unit,
) {
    val combinedSeed = combineSketchSeed(seed)
    val shape = SketchRoundRectShape(
        seed = combinedSeed,
        roughness = KaigiButtonDefaults.roughness,
        tremor = KaigiButtonDefaults.tremor,
        cornerRadius = KaigiButtonDefaults.height / 2,
        borderThickness = KaigiButtonDefaults.borderThickness,
    )
    // Drawn at the height the design gives it, while the press claims Material's minimum
    // without pushing neighbouring buttons apart.
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .height(KaigiButtonDefaults.height),
    ) {
        Row(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .background(containerColor)
                .clickable(role = Role.Button, onClick = onClick)
                .padding(contentPadding),
            horizontalArrangement = Arrangement.spacedBy(
                space = KaigiButtonDefaults.iconSpacing,
                alignment = Alignment.CenterHorizontally,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                content()
            }
        }
        Box(Modifier.matchParentSize().sketchBorder(shape, borderColor))
    }
}

object KaigiButtonDefaults {
    val height = 44.dp
    val iconSize = 16.dp
    val iconSpacing = 8.dp
    val borderThickness = 1.5.dp
    val roughness = 0.4.dp
    val tremor = 0.15.dp

    val filledContentPadding = PaddingValues(horizontal = 24.dp)
    val outlinedContentPadding = PaddingValues(horizontal = 16.dp)

    val labelStyle
        @Composable get() = MaterialTheme.typography.labelLarge
}

@Preview
@Composable
private fun KaigiButtonPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            KaigiButton(onClick = {}, seed = 871, modifier = Modifier.fillMaxWidth()) {
                Text("Show more", style = KaigiButtonDefaults.labelStyle)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                KaigiOutlinedButton(onClick = {}, seed = 872, modifier = Modifier.weight(1f)) {
                    Text("Slides", style = KaigiButtonDefaults.labelStyle)
                }
                KaigiOutlinedButton(onClick = {}, seed = 873, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Filled.PlayCircle,
                        contentDescription = null,
                        modifier = Modifier.size(KaigiButtonDefaults.iconSize),
                    )
                    Text("Video", style = KaigiButtonDefaults.labelStyle)
                }
            }
        }
    }
}
