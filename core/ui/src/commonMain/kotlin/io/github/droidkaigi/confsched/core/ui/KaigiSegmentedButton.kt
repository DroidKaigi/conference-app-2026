package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme

/**
 * A row of mutually exclusive options sharing one hand-sketched outline.
 *
 * The selected fill is clipped from that outline, so it follows the drawn edge at the ends
 * of the row instead of cutting a straight corner across it.
 *
 * The row is drawn on the header band, so it takes its outline and its unselected labels from
 * the colours that band contrasts with rather than from the surface behind the screen.
 *
 * @param outlineSeed the value the outline enclosing the row is drawn from. The rules dividing
 *   the options are seeded separately, by each [KaigiSegmentedButton].
 * @param modifier the [Modifier] applied to the row.
 * @param width the width the row is fixed to.
 * @param borderColor the colour of the outline.
 * @param content the options, each a [KaigiSegmentedButton].
 */
@Composable
fun KaigiSingleChoiceSegmentedButtonRow(
    outlineSeed: Int,
    modifier: Modifier = Modifier,
    width: Dp = KaigiSegmentedButtonDefaults.width,
    borderColor: Color = MaterialTheme.colorScheme.inverseOnSurface,
    content: @Composable KaigiSingleChoiceSegmentedButtonRowScope.() -> Unit,
) {
    val combinedSeed = combineSketchSeed(outlineSeed)
    val shape = SketchRoundRectShape(
        seed = combinedSeed,
        roughness = KaigiSegmentedButtonDefaults.roughness,
        tremor = KaigiSegmentedButtonDefaults.tremor,
        cornerRadius = KaigiSegmentedButtonDefaults.cornerRadius,
        borderThickness = KaigiSegmentedButtonDefaults.borderThickness,
    )
    Box(modifier = modifier.width(width).height(KaigiSegmentedButtonDefaults.height)) {
        Row(modifier = Modifier.fillMaxSize().clip(shape).selectableGroup()) {
            val segmentedButtonRowScope = remember { KaigiSingleChoiceSegmentedButtonRowScopeImpl(this) }
            segmentedButtonRowScope.content()
        }
        Box(Modifier.matchParentSize().sketchBorder(shape, borderColor))
    }
}

/** Handed to the content of a [KaigiSingleChoiceSegmentedButtonRow], and obtainable nowhere else. */
interface KaigiSingleChoiceSegmentedButtonRowScope : RowScope

private class KaigiSingleChoiceSegmentedButtonRowScopeImpl(scope: RowScope) :
    KaigiSingleChoiceSegmentedButtonRowScope,
    RowScope by scope

/**
 * One option of a [KaigiSingleChoiceSegmentedButtonRow].
 *
 * @param selected whether this option is the one in effect.
 * @param onClick called when the option is clicked.
 * @param dividerSeed the value the rule separating this option from the one after it is drawn
 *   from. Pass `null` on the last option, the way Material asks the caller for an item's
 *   position in the row.
 * @param leadingDividerSeed the [dividerSeed] of the option immediately before this one, or
 *   `null` for the first option. Pass it so the selected fill can be clipped to meet that rule.
 * @param modifier the [Modifier] applied to the option.
 * @param selectedContainerColor the colour filling the option while [selected].
 * @param selectedContentColor the colour [label] draws in while [selected].
 * @param contentColor the colour [label] and the divider draw in while not [selected].
 * @param label the text naming the option.
 */
@Composable
fun KaigiSingleChoiceSegmentedButtonRowScope.KaigiSegmentedButton(
    selected: Boolean,
    onClick: () -> Unit,
    dividerSeed: Int?,
    leadingDividerSeed: Int? = null,
    modifier: Modifier = Modifier,
    selectedContainerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    selectedContentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.inverseOnSurface,
    label: @Composable () -> Unit,
) {
    val combinedDividerSeed = if (dividerSeed != null) combineSketchSeed(dividerSeed) else null
    val combinedLeadingDividerSeed = if (leadingDividerSeed != null) combineSketchSeed(leadingDividerSeed) else null
    Box(
        modifier = modifier
            .weight(1f)
            .fillMaxHeight()
            .drawWithCache {
                val trailingClip: Path? = if (selected && combinedDividerSeed != null) {
                    sketchVerticalFillPath(
                        width = size.width,
                        height = size.height,
                        centerX = size.width,
                        roughness = KaigiSegmentedButtonDefaults.dividerRoughness,
                        tremor = KaigiSegmentedButtonDefaults.dividerTremor,
                        sweepWavelength = KaigiSegmentedButtonDefaults.dividerSweepWavelength,
                        tremorWavelength = KaigiSegmentedButtonDefaults.dividerTremorWavelength,
                        seed = combinedDividerSeed,
                        fillLeft = true,
                    )
                } else {
                    null
                }
                val leadingClip: Path? = if (selected && combinedLeadingDividerSeed != null) {
                    sketchVerticalFillPath(
                        width = size.width,
                        height = size.height,
                        centerX = 0f,
                        roughness = KaigiSegmentedButtonDefaults.dividerRoughness,
                        tremor = KaigiSegmentedButtonDefaults.dividerTremor,
                        sweepWavelength = KaigiSegmentedButtonDefaults.dividerSweepWavelength,
                        tremorWavelength = KaigiSegmentedButtonDefaults.dividerTremorWavelength,
                        seed = combinedLeadingDividerSeed,
                        fillLeft = false,
                    )
                } else {
                    null
                }
                onDrawBehind {
                    if (selected) {
                        if (trailingClip != null || leadingClip != null) {
                            drawContext.canvas.save()
                            trailingClip?.let { drawContext.canvas.clipPath(it) }
                            leadingClip?.let { drawContext.canvas.clipPath(it) }
                            drawRect(selectedContainerColor)
                            drawContext.canvas.restore()
                        } else {
                            drawRect(selectedContainerColor)
                        }
                    }
                }
            }
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(
            LocalContentColor provides if (selected) selectedContentColor else contentColor,
            content = label,
        )
        if (dividerSeed != null) {
            SketchVerticalDivider(
                seed = dividerSeed,
                modifier = Modifier.align(Alignment.CenterEnd)
                    .offset(x = KaigiSegmentedButtonDefaults.dividerHalfWidth),
                color = contentColor,
                thickness = KaigiSegmentedButtonDefaults.dividerThickness,
                roughness = KaigiSegmentedButtonDefaults.dividerRoughness,
                tremor = KaigiSegmentedButtonDefaults.dividerTremor,
                sweepWavelength = KaigiSegmentedButtonDefaults.dividerSweepWavelength,
                tremorWavelength = KaigiSegmentedButtonDefaults.dividerTremorWavelength,
            )
        }
    }
}

object KaigiSegmentedButtonDefaults {
    val width = 240.dp
    val height = 40.dp
    val cornerRadius = 16.dp
    val borderThickness = 2.dp
    val roughness = 0.4.dp
    val tremor = 0.15.dp
    val dividerThickness = 1.dp
    val dividerRoughness = 0.3.dp
    val dividerTremor = 0.3.dp
    val dividerSweepWavelength = 140.dp
    val dividerTremorWavelength = 42.dp
    val dividerHalfWidth = (dividerThickness + (dividerRoughness + dividerTremor) * 2) / 2
}

@Preview
@Composable
private fun KaigiSegmentedButtonFirstSelectedPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.inverseSurface)
                .padding(16.dp),
        ) {
            KaigiSingleChoiceSegmentedButtonRow(outlineSeed = 900) {
                KaigiSegmentedButton(selected = true, onClick = {}, dividerSeed = 902) {
                    Text("9/2", style = MaterialTheme.typography.labelLarge)
                }
                KaigiSegmentedButton(selected = false, onClick = {}, dividerSeed = null, leadingDividerSeed = 902) {
                    Text("9/3", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Preview
@Composable
private fun KaigiSegmentedButtonSecondSelectedPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.inverseSurface)
                .padding(16.dp),
        ) {
            KaigiSingleChoiceSegmentedButtonRow(outlineSeed = 900) {
                KaigiSegmentedButton(selected = false, onClick = {}, dividerSeed = 902) {
                    Text("9/2", style = MaterialTheme.typography.labelLarge)
                }
                KaigiSegmentedButton(selected = true, onClick = {}, dividerSeed = null, leadingDividerSeed = 902) {
                    Text("9/3", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Preview
@Composable
private fun KaigiSegmentedButtonThreeSegmentsPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.inverseSurface)
                .padding(16.dp),
        ) {
            KaigiSingleChoiceSegmentedButtonRow(outlineSeed = 900) {
                KaigiSegmentedButton(selected = false, onClick = {}, dividerSeed = 901) {
                    Text("A", style = MaterialTheme.typography.labelLarge)
                }
                KaigiSegmentedButton(selected = true, onClick = {}, dividerSeed = 902, leadingDividerSeed = 901) {
                    Text("B", style = MaterialTheme.typography.labelLarge)
                }
                KaigiSegmentedButton(selected = false, onClick = {}, dividerSeed = null, leadingDividerSeed = 902) {
                    Text("C", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
