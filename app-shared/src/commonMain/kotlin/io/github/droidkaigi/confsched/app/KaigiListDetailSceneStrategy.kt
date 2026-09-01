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
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldScope
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import io.github.droidkaigi.confsched.core.common.instantNavTransition

// The list-detail directive sets its own spacer to zero so pane backgrounds meet edge-to-edge;
// this is the separation each pane draws itself via LocalPanePartitionSpacerSize.
internal val PanePartitionSpacerSize = 24.dp

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun <T : Any> rememberKaigiListDetailSceneStrategy(): SceneStrategy<T> {
    val delegate = rememberKaigiListDetailPaneStrategy<T>()
    return remember(delegate) { PaneMotionSceneStrategy(delegate) }
}

/**
 * Hands the scaffold sole ownership of the motion between its panes.
 *
 * The scaffold animates a pane appearing and disappearing itself, and NavDisplay would otherwise
 * animate the same navigation a second time as the scene it renders changes.
 */
private class PaneMotionSceneStrategy<T : Any>(
    private val delegate: SceneStrategy<T>,
) : SceneStrategy<T> {
    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? =
        with(delegate) { calculateScene(entries) }?.let(::PaneMotionScene)
}

private class PaneMotionScene<T : Any>(private val delegate: Scene<T>) : Scene<T> by delegate {
    override val metadata: Map<String, Any> = instantNavTransition()

    override fun equals(other: Any?): Boolean = other is PaneMotionScene<*> && delegate == other.delegate

    override fun hashCode(): Int = delegate.hashCode()
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun <T : Any> rememberKaigiListDetailPaneStrategy(): ListDetailSceneStrategy<T> {
    val dragBounds = remember(::PaneExpansionDragBounds)
    val scaffoldDirective = calculatePaneScaffoldDirective(currentWindowAdaptiveInfoV2())
    return rememberListDetailSceneStrategy(
        // The default, PopUntilScaffoldValueChange, reports no entry to go back to here: closing
        // the detail leaves both panes expanded, around the list's placeholder, so back exits.
        backNavigationBehavior = BackNavigationBehavior.PopLatest,
        directive = PaneScaffoldDirective(
            maxHorizontalPartitions = scaffoldDirective.maxHorizontalPartitions,
            horizontalPartitionSpacerSize = 0.dp,
            maxVerticalPartitions = scaffoldDirective.maxVerticalPartitions,
            verticalPartitionSpacerSize = scaffoldDirective.verticalPartitionSpacerSize,
            defaultPanePreferredWidth = scaffoldDirective.defaultPanePreferredWidth,
            defaultPanePreferredHeight = scaffoldDirective.defaultPanePreferredHeight,
            excludedBounds = scaffoldDirective.excludedBounds,
            shouldAutoFocusCurrentDestination = false,
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
    val layoutDirection = LocalLayoutDirection.current
    Box(
        modifier = Modifier
            .fillMaxHeight()
            // The handle is placed centered on the seam, so its own coordinates are where the
            // seam position for drag clamping is read from. positionInParent().x is always
            // physical-left-based; PaneExpansionState's offset space runs from the start edge,
            // so it needs mirroring under Rtl to stay in the same space as minBound/maxBound.
            .onPlaced { coordinates ->
                val scaffold = coordinates.parentLayoutCoordinates ?: return@onPlaced
                val minWidthPx = with(density) { PaneMinWidth.toPx() }
                val physicalSeamX = coordinates.positionInParent().x + coordinates.size.width / 2f
                dragBounds.seamPosition = if (layoutDirection == LayoutDirection.Rtl) {
                    scaffold.size.width - physicalSeamX
                } else {
                    physicalSeamX
                }
                dragBounds.minBound = minWidthPx
                dragBounds.maxBound = scaffold.size.width - minWidthPx
                dragBounds.maxOvershoot = with(density) { PaneMaxOvershoot.toPx() }
            }
            .paneExpansionDraggable(
                state = state,
                minTouchTargetSize = LocalMinimumInteractiveComponentSize.current,
                interactionSource = interactionSource,
            )
            .pointerHoverIcon(HorizontalResizePointerIcon),
        contentAlignment = Alignment.Center,
    ) {
        // Panes are clipped to their own bounds, so the detail pane cannot cast a real shadow
        // across the seam; this band, drawn from the drag handle slot placed above both panes,
        // stands in for that shadow on the list side. Brush.horizontalGradient stops are always
        // physical-left-to-right, so they need reversing under Rtl to keep darkening toward the
        // detail pane, which sits on the physical left there instead of the physical right.
        val scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = PANE_SHADOW_MAX_ALPHA)
        val shadowGradient = if (layoutDirection == LayoutDirection.Rtl) {
            Brush.horizontalGradient(0f to scrimColor, 1f to Color.Transparent)
        } else {
            Brush.horizontalGradient(0f to Color.Transparent, 1f to scrimColor)
        }
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = -PaneShadowWidth / 2)
                .fillMaxHeight()
                .width(PaneShadowWidth)
                .background(shadowGradient),
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
