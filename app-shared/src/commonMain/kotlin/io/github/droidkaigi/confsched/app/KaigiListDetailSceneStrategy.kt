package io.github.droidkaigi.confsched.app

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDragHandle
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.PaneExpansionAnchor
import androidx.compose.material3.adaptive.layout.PaneExpansionState
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldScope
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

// The list-detail directive sets its own spacer to zero so pane backgrounds meet edge-to-edge;
// this is the separation each pane draws itself via LocalPanePartitionSpacerSize.
internal val PanePartitionSpacerSize = 24.dp

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun <T : Any> rememberKaigiListDetailSceneStrategy(): ListDetailSceneStrategy<T> {
    val dragBounds = remember(::PaneExpansionDragBounds)
    return rememberListDetailSceneStrategy(
        directive = calculatePaneScaffoldDirective(
            currentWindowAdaptiveInfoV2(),
        ).copy(
            horizontalPartitionSpacerSize = 0.dp,
        ),
        paneExpansionState = rememberPaneExpansionState(
            anchors = listOf(
                PaneExpansionAnchor.Offset.fromStart(PaneMinWidth),
                PaneExpansionAnchor.Proportion(0.5f),
                PaneExpansionAnchor.Offset.fromEnd(PaneMinWidth),
            ),
            consumeDragDelta = dragBounds::dampenOutOfBoundsDelta,
        ),
        paneExpansionDragHandle = { state -> PaneExpansionDragHandle(state, dragBounds) },
    )
}

// Rubber-bands dragging at the edge anchors; the state itself clamps only to [0, layout width].
// Positions are LTR window coordinates, matching PaneExpansionState's offset space.
private class PaneExpansionDragBounds {
    var seamPosition = Float.NaN
    var minBound = Float.NaN
    var maxBound = Float.NaN
    var maxOvershoot = Float.NaN

    fun dampenOutOfBoundsDelta(delta: Float): Float {
        if (seamPosition.isNaN() || minBound.isNaN() || maxBound.isNaN() || maxOvershoot.isNaN()) {
            return delta
        }
        return when {
            delta > 0 && seamPosition + delta > maxBound -> {
                val inBounds = (maxBound - seamPosition).coerceIn(0f, delta)
                val overshoot = (seamPosition - maxBound).coerceAtLeast(0f)
                inBounds + (delta - inBounds) * resistanceAt(overshoot)
            }

            delta < 0 && seamPosition + delta < minBound -> {
                val inBounds = (seamPosition - minBound).coerceIn(0f, -delta)
                val overshoot = (minBound - seamPosition).coerceAtLeast(0f)
                -(inBounds + (-delta - inBounds) * resistanceAt(overshoot))
            }

            else -> delta
        }
    }

    private fun resistanceAt(overshoot: Float): Float =
        (PANE_OVERSHOOT_RESISTANCE * (1f - overshoot / maxOvershoot)).coerceAtLeast(0f)
}

private const val PANE_OVERSHOOT_RESISTANCE = 0.3f
private val PaneMaxOvershoot = 48.dp

// Dragging settles onto one of the anchors above, so the edge anchors keep either pane from
// resting narrower than this.
private val PaneMinWidth = 360.dp

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun ThreePaneScaffoldScope.PaneExpansionDragHandle(
    state: PaneExpansionState,
    dragBounds: PaneExpansionDragBounds,
) {
    val interactionSource = remember(::MutableInteractionSource)
    val density = LocalDensity.current
    Box(
        modifier = Modifier
            .fillMaxHeight()
            // The handle is placed centered on the seam, so its own coordinates are where the
            // seam position for drag clamping is read from.
            .onGloballyPositioned { coordinates ->
                val scaffold = coordinates.parentLayoutCoordinates ?: return@onGloballyPositioned
                val minWidthPx = with(density) { PaneMinWidth.toPx() }
                dragBounds.seamPosition = coordinates.positionInParent().x + coordinates.size.width / 2f
                dragBounds.minBound = minWidthPx
                dragBounds.maxBound = scaffold.size.width - minWidthPx
                dragBounds.maxOvershoot = with(density) { PaneMaxOvershoot.toPx() }
            }
            .paneExpansionDraggable(
                state = state,
                minTouchTargetSize = LocalMinimumInteractiveComponentSize.current,
                interactionSource = interactionSource,
            ),
        contentAlignment = Alignment.Center,
    ) {
        // Panes are clipped to their own bounds, so the detail pane cannot cast a real shadow
        // across the seam; this band, drawn from the drag handle slot placed above both panes,
        // stands in for that shadow on the list side.
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = -PaneShadowWidth / 2)
                .fillMaxHeight()
                .width(PaneShadowWidth)
                .background(
                    Brush.horizontalGradient(
                        0f to Color.Transparent,
                        1f to MaterialTheme.colorScheme.scrim.copy(alpha = PANE_SHADOW_MAX_ALPHA),
                    ),
                ),
        )
        VerticalDragHandle(
            // The scaffold centers the handle on the pane boundary, but the visible gap is the
            // detail pane's start inset; shift the handle to the middle of that band.
            modifier = Modifier.offset(x = PanePartitionSpacerSize / 2),
            interactionSource = interactionSource,
        )
    }
}

private val PaneShadowWidth = 8.dp
private const val PANE_SHADOW_MAX_ALPHA = 0.1f
