package io.github.droidkaigi.confsched.app

import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.VerticalDragHandle
import androidx.compose.material3.VerticalDragHandleDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneDecoratorStrategy
import androidx.navigation3.scene.SceneDecoratorStrategyScope
import androidx.window.core.layout.WindowSizeClass
import io.github.droidkaigi.confsched.app_shared.generated.resources.Res
import io.github.droidkaigi.confsched.app_shared.generated.resources.collapse_navigation_rail
import io.github.droidkaigi.confsched.app_shared.generated.resources.expand_navigation_rail
import io.github.droidkaigi.confsched.core.common.TargetPlatform
import io.github.droidkaigi.confsched.core.common.currentPlatform
import io.github.droidkaigi.confsched.core.designsystem.icon.FavoriteBorder
import io.github.droidkaigi.confsched.core.designsystem.icon.Info
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.designsystem.icon.Map
import io.github.droidkaigi.confsched.core.designsystem.icon.ProfileCard
import io.github.droidkaigi.confsched.core.designsystem.icon.Timetable
import io.github.droidkaigi.confsched.core.ui.KaigiNavigationBar
import io.github.droidkaigi.confsched.core.ui.KaigiNavigationBarItem
import io.github.droidkaigi.confsched.core.ui.KaigiNavigationRail
import io.github.droidkaigi.confsched.core.ui.KaigiNavigationRailDefaults
import io.github.droidkaigi.confsched.core.ui.KaigiNavigationRailItem
import io.github.droidkaigi.confsched.core.ui.LocalNavigationBarOccupiedHeight
import io.github.droidkaigi.confsched.core.ui.androidSystemGestureExclusion
import io.github.droidkaigi.confsched.feature.about.AboutNavKey
import io.github.droidkaigi.confsched.feature.eventmap.EventMapNavKey
import io.github.droidkaigi.confsched.feature.favorites.FavoritesNavKey
import io.github.droidkaigi.confsched.feature.profilecard.ProfileCardNavKey
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableNavKey
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

// Public and free of UI types so the iOS shell can drive tab selection through RootTabNavigator.
// The label names the destination to a screen reader on every platform; neither bar draws it.
enum class RootTab(internal val key: NavKey, val label: String) {
    Timetable(TimetableNavKey, "Timetable"),
    EventMap(EventMapNavKey, "Event map"),
    Favorites(FavoritesNavKey, "Favorites"),
    About(AboutNavKey, "About"),
    ProfileCard(ProfileCardNavKey, "Profile"),
}

private val RootTab.icon: ImageVector
    get() = when (this) {
        RootTab.Timetable -> KaigiIcons.Default.Timetable
        RootTab.EventMap -> KaigiIcons.Default.Map
        RootTab.Favorites -> KaigiIcons.Default.FavoriteBorder
        RootTab.About -> KaigiIcons.Default.Info
        RootTab.ProfileCard -> KaigiIcons.Default.ProfileCard
    }

private val rootTabsByKey: Map<NavKey, RootTab> = RootTab.entries.associateBy(RootTab::key)

/**
 * The root tab a scene drawing [paneCount] entries still shows, or null when it shows none.
 *
 * Every scene this app forms draws the topmost [paneCount] entries of the back stack, so a
 * multi-pane scene keeps the entries below its top one on screen beside it: the list pane of a
 * list-detail scene is a root destination the reader is still looking at, and the tab it belongs to
 * stays the reader's place.
 */
private fun List<NavKey>.rootTabInTopPanes(paneCount: Int): RootTab? =
    takeLast(paneCount).asReversed().firstNotNullOfOrNull(rootTabsByKey::get)

/** Whether this scene draws two or more panes, one entry per pane. */
private val Scene<NavKey>.isMultiPane: Boolean
    get() = entries.size > 1

private class RootTabSceneDecorator(
    private val backStack: List<NavKey>,
    private val onSelectTab: (RootTab) -> Unit,
) : SceneDecoratorStrategy<NavKey> {

    override fun SceneDecoratorStrategyScope<NavKey>.decorateScene(scene: Scene<NavKey>): Scene<NavKey> {
        val currentTab = backStack.rootTabInTopPanes(scene.entries.size)
            ?: return scene
        return RootTabScene(scene, currentTab, onSelectTab)
    }
}

private class RootTabScene(
    private val delegate: Scene<NavKey>,
    private val currentTab: RootTab,
    private val onSelectTab: (RootTab) -> Unit,
) : Scene<NavKey> {
    override val key: Any get() = delegate.key
    override val entries get() = delegate.entries
    override val previousEntries get() = delegate.previousEntries

    override val content: @Composable () -> Unit = {
        val isExpanded = currentWindowAdaptiveInfoV2().windowSizeClass
            .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
        val currentDelegate by rememberUpdatedState(delegate)
        val movableContent = remember { movableContentOf { currentDelegate.content() } }
        if (isExpanded) {
            RootTabRailLayout(
                currentTab = currentTab,
                onSelectTab = onSelectTab,
                collapsible = currentDelegate.isMultiPane,
            ) {
                CompositionLocalProvider(
                    LocalNavigationBarOccupiedHeight provides 0.dp,
                    content = movableContent,
                )
            }
        } else {
            Box(Modifier.fillMaxSize()) {
                movableContent()
                RootTabBar(
                    currentTab = currentTab,
                    onSelectTab = onSelectTab,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }
    }

    override fun equals(other: Any?): Boolean =
        other is RootTabScene && delegate == other.delegate && currentTab == other.currentTab

    override fun hashCode(): Int = delegate.hashCode() * 31 + currentTab.hashCode()
}

/** The tab bar the shell shows under every root destination on windows narrower than expanded. */
@Composable
private fun RootTabBar(
    currentTab: RootTab,
    onSelectTab: (RootTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    KaigiNavigationBar(outlineSeed = RootTabBarSeeds.OUTLINE, modifier = modifier) {
        RootTab.entries.forEachIndexed { index, tab ->
            KaigiNavigationBarItem(
                selected = tab == currentTab,
                onClick = { onSelectTab(tab) },
                indicatorSeed = RootTabBarSeeds.FIRST_INDICATOR + index,
            ) {
                Icon(tab.icon, contentDescription = tab.label)
            }
        }
    }
}

/** The tab rail the shell shows beside every root destination on expanded windows. */
@Composable
private fun RootTabRail(
    currentTab: RootTab,
    onSelectTab: (RootTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    KaigiNavigationRail(outlineSeed = RootTabBarSeeds.OUTLINE, modifier = modifier) {
        RootTab.entries.forEachIndexed { index, tab ->
            KaigiNavigationRailItem(
                selected = tab == currentTab,
                onClick = { onSelectTab(tab) },
                indicatorSeed = RootTabBarSeeds.FIRST_INDICATOR + index,
            ) {
                Icon(tab.icon, contentDescription = tab.label)
            }
        }
    }
}

/** The rail column beside [content] on expanded windows, collapsible by its drag handle while [collapsible] holds. */
@Composable
private fun RootTabRailLayout(
    currentTab: RootTab,
    onSelectTab: (RootTab) -> Unit,
    collapsible: Boolean,
    content: @Composable () -> Unit,
) {
    val columnWidthPx = with(LocalDensity.current) {
        KaigiNavigationRailDefaults.columnWidth.toPx()
    }
    // Saves a plain Boolean rather than using the library Saver, which restores the state
    // without anchors and leaves its offset unusable until they are set again.
    val railDragState = rememberSaveable(
        saver = Saver(
            save = { it.currentValue == RailValue.Collapsed },
            restore = { collapsed ->
                AnchoredDraggableState(
                    initialValue = if (collapsed) RailValue.Collapsed else RailValue.Expanded,
                    anchors = railAnchors(columnWidthPx),
                )
            },
        ),
    ) {
        AnchoredDraggableState(
            initialValue = RailValue.Expanded,
            anchors = railAnchors(columnWidthPx),
        )
    }
    // A density change moves the anchors in place; recreating the state instead would reopen
    // a collapsed rail.
    SideEffect {
        railDragState.updateAnchors(railAnchors(columnWidthPx), railDragState.targetValue)
    }
    LaunchedEffect(collapsible, railDragState) {
        if (!collapsible) railDragState.animateTo(RailValue.Expanded)
    }
    Box(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxSize()) {
            CollapsingRailColumn(
                state = railDragState,
                columnWidthPx = columnWidthPx,
                currentTab = currentTab,
                onSelectTab = onSelectTab,
            )
            Box(Modifier.weight(1f)) {
                content()
            }
        }
        if (collapsible) {
            RootTabRailDragHandle(
                state = railDragState,
                columnWidthPx = columnWidthPx,
                modifier = Modifier.align(Alignment.CenterStart),
            )
        }
    }
}

/**
 * The rail column whose width follows the drag between full width and zero.
 *
 * The drag offset is read here, in a composable of its own, so the per-frame recomposition it
 * causes stays inside this column and never reaches the panes beside it.
 */
@Composable
private fun CollapsingRailColumn(
    state: AnchoredDraggableState<RailValue>,
    columnWidthPx: Float,
    currentTab: RootTab,
    onSelectTab: (RootTab) -> Unit,
) {
    val railWidth = with(LocalDensity.current) { railWidthPx(state, columnWidthPx).toDp() }
    if (railWidth > 0.dp) {
        Box(
            modifier = Modifier
                .width(railWidth)
                .fillMaxHeight()
                .clipToBounds(),
        ) {
            RootTabRail(
                currentTab = currentTab,
                onSelectTab = onSelectTab,
                // Unbounded keeps the column at its own width under the shrinking clip, and End
                // pins it to the moving boundary, so the drag slides the rail out instead of
                // squeezing it.
                modifier = Modifier.wrapContentWidth(align = Alignment.End, unbounded = true),
            )
        }
    }
}

private fun railAnchors(columnWidthPx: Float): DraggableAnchors<RailValue> = DraggableAnchors {
    RailValue.Expanded at 0f
    RailValue.Collapsed at -columnWidthPx
}

/** The rail column's current width: its full width offset by the drag, floored at zero. */
private fun railWidthPx(state: AnchoredDraggableState<RailValue>, columnWidthPx: Float): Float =
    (columnWidthPx + state.offset).coerceIn(0f, columnWidthPx)

/**
 * The hit area's start edge before the on-screen clamp: the area is centered on the drawn handle,
 * which sits flush with the boundary's inner side and stays on screen at width zero.
 */
private fun Density.unclampedHitAreaStartPx(
    state: AnchoredDraggableState<RailValue>,
    columnWidthPx: Float,
    handleWidth: Dp,
): Float {
    val handleHalfWidthPx = handleWidth.toPx() / 2
    val handleCenterPx = (railWidthPx(state, columnWidthPx) - handleHalfWidthPx)
        .coerceAtLeast(handleHalfWidthPx)
    return handleCenterPx - RootTabRailDragHandleDefaults.hitAreaWidth.toPx() / 2
}

/** The pane-boundary affordance whose drag collapses and restores the rail. */
@Composable
private fun RootTabRailDragHandle(
    state: AnchoredDraggableState<RailValue>,
    columnWidthPx: Float,
    modifier: Modifier = Modifier,
) {
    // Shared with the surrounding box's gesture so the handle renders the Material pressed
    // and dragged states for it.
    val interactionSource = remember(::MutableInteractionSource)
    val animationScope = rememberCoroutineScope()
    val collapsed = state.settledValue == RailValue.Collapsed
    val accessibilityActionLabel = stringResource(
        if (collapsed) Res.string.expand_navigation_rail else Res.string.collapse_navigation_rail,
    )
    val handleSize = VerticalDragHandleDefaults.sizes().size
    Box(
        modifier = modifier
            .offset {
                // Clamped so the hit area and its gesture exclusion stay fully on screen.
                val startPx = unclampedHitAreaStartPx(state, columnWidthPx, handleSize.width)
                IntOffset(startPx.coerceAtLeast(0f).roundToInt(), 0)
            }
            .size(
                width = RootTabRailDragHandleDefaults.hitAreaWidth,
                height = handleSize.height,
            )
            .then(
                // Only the collapsed handle sits inside Android's back-gesture edge inset, where
                // the restore drag would otherwise become a system back.
                if (collapsed) {
                    Modifier.androidSystemGestureExclusion()
                } else {
                    Modifier
                },
            )
            .anchoredDraggable(
                state = state,
                orientation = Orientation.Horizontal,
                interactionSource = interactionSource,
            )
            .pointerHoverIcon(HorizontalResizePointerIcon)
            .semantics {
                customActions = listOf(
                    CustomAccessibilityAction(
                        label = accessibilityActionLabel,
                    ) {
                        animationScope.launch {
                            state.animateTo(if (collapsed) RailValue.Expanded else RailValue.Collapsed)
                        }
                        true
                    },
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        VerticalDragHandle(
            interactionSource = interactionSource,
            // Shifted by whatever the clamp trimmed off the hit area, so the drawn handle keeps
            // its place at the boundary while the hit area stays on screen.
            modifier = Modifier.offset {
                val startPx = unclampedHitAreaStartPx(state, columnWidthPx, handleSize.width)
                IntOffset(startPx.coerceAtMost(0f).roundToInt(), 0)
            },
        )
    }
}

private enum class RailValue { Expanded, Collapsed }

private object RootTabRailDragHandleDefaults {
    /** Wider than the handle draws, so the drag is grabbable; the drawn size is [VerticalDragHandleDefaults.sizes]. */
    val hitAreaWidth = 24.dp
}

private object RootTabBarSeeds {
    const val OUTLINE = 955
    const val FIRST_INDICATOR = OUTLINE + 1
}

// Null on iOS, where a SwiftUI bar draws natively above the Compose view controller.
@Composable
internal fun rememberRootTabSceneDecorator(
    backStack: NavBackStack<NavKey>,
    onSelectTab: (RootTab) -> Unit,
): SceneDecoratorStrategy<NavKey>? = if (currentPlatform == TargetPlatform.Ios) {
    null
} else {
    remember(backStack, onSelectTab) {
        RootTabSceneDecorator(backStack, onSelectTab)
    }
}
