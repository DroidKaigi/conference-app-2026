package io.github.droidkaigi.confsched.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import io.github.droidkaigi.confsched.core.designsystem.icon.ArrowBack
import io.github.droidkaigi.confsched.core.designsystem.icon.GridView
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.designsystem.icon.Search
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.generated.resources.Res
import io.github.droidkaigi.confsched.core.ui.generated.resources.back
import org.jetbrains.compose.resources.stringResource

/**
 * The bar at the top of a screen: a title in the display face on one line, with a navigation
 * icon leading it and actions trailing.
 *
 * It carries its own background rather than leaving it to a `Scaffold`, so a screen can run
 * the same colour on behind whatever it puts underneath — a tab row, most often.
 *
 * @param title the text naming the screen. A screen whose content opens with a headline of its
 *   own passes an empty string, leaving the bar to the navigation icon and the actions.
 * @param modifier the [Modifier] applied to the bar.
 * @param navigationIcon the control leading the title, most often a [KaigiTopAppBarBackButton].
 * @param containerColor the colour filling the band.
 * @param contentColor the colour the title, [navigationIcon] and [actions] draw in.
 * @param windowInsets the insets the bar holds its content clear of. The band fills behind them,
 *   so a screen drawing edge to edge keeps its colour under the status bar.
 * @param scrollBehavior how the bar answers the content scrolling under it.
 * @param actions the controls trailing the title.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KaigiTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.inverseSurface,
    contentColor: Color = MaterialTheme.colorScheme.inverseOnSurface,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = { BarTitle(title) },
        modifier = modifier,
        navigationIcon = navigationIcon,
        actions = { Actions(actions) },
        expandedHeight = KaigiTopAppBarDefaults.height,
        windowInsets = windowInsets,
        colors = KaigiTopAppBarDefaults.colors(containerColor, contentColor),
        scrollBehavior = scrollBehavior,
        contentPadding = KaigiTopAppBarDefaults.contentPadding,
    )
}

/**
 * The bar at the top of a screen reached from another: the navigation icon and the actions on
 * one row, with the title on its own row below them.
 *
 * Handed a [scrollBehavior] it collapses onto the one row as the content scrolls under it.
 *
 * A screen only reaches this bar from another one, so it always leads with the back control and
 * takes the click rather than the control. As the detail pane of a list-detail scene the bar
 * closes rather than returns, and holds its content clear of the boundary it shares with the list.
 *
 * @param title the text naming the screen.
 * @param onBackClick called when the back control is clicked.
 * @param modifier the [Modifier] applied to the bar.
 * @param containerColor the colour filling the band.
 * @param contentColor the colour the title, the back control and [actions] draw in.
 * @param windowInsets the insets the bar holds its content clear of.
 * @param scrollBehavior how the bar answers the content scrolling under it.
 * @param actions the controls trailing the back control.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KaigiLargeTopAppBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.inverseSurface,
    contentColor: Color = MaterialTheme.colorScheme.inverseOnSurface,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val density = LocalDensity.current
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    SideEffect {
        scrollBehavior?.state?.heightOffsetLimit = with(density) {
            -(KaigiTopAppBarDefaults.largeHeight - KaigiTopAppBarDefaults.height).toPx()
        }
    }

    val collapsedFraction = scrollBehavior?.state?.collapsedFraction ?: 0f
    val scrollProgress = FastOutSlowInEasing.transform(collapsedFraction)

    // As a detail pane the bar spans the pane but its content starts past the boundary, so the
    // title centres on what is left rather than on the band.
    val paneStartInset = paneStartInset()

    // BarTitle renders at headlineMedium; the collapsed title scales down from that size.
    val expandedTitleFontSize = MaterialTheme.typography.headlineMedium.fontSize
    val collapsedTitleScale =
        KaigiTopAppBarDefaults.collapsedTitleFontSize.value / expandedTitleFontSize.value
    val titleScale = lerp(1f, collapsedTitleScale, scrollProgress)

    val barHeight = with(density) {
        lerp(
            KaigiTopAppBarDefaults.largeHeight.toPx(),
            KaigiTopAppBarDefaults.height.toPx(),
            collapsedFraction,
        ).toDp()
    }
    var barWidth by remember { mutableFloatStateOf(0f) }
    var titleWidth by remember { mutableIntStateOf(0) }
    var titleHeight by remember { mutableIntStateOf(0) }
    var titleBaseline by remember { mutableFloatStateOf(0f) }

    val contentWidth = with(density) { barWidth.toDp() } - paneStartInset

    // Pivot the scale on the title's own baseline and start edge so both hold still while the
    // glyphs shrink; the translations below are then measured against those fixed lines.
    val titleScalePivot = TransformOrigin(
        pivotFractionX = if (isRtl) 1f else 0f,
        pivotFractionY = if (titleHeight > 0) titleBaseline / titleHeight else 0f,
    )

    // graphicsLayer translationX is geometric (positive is rightward), so the shift to the
    // horizontal centre mirrors under a right-to-left layout.
    val titleCentreTranslationX = with(density) {
        val fromStartEdge = (contentWidth.toPx() - titleWidth * collapsedTitleScale) / 2 -
            KaigiTopAppBarDefaults.largeTitleStartInset.toPx()
        if (isRtl) -fromStartEdge else fromStartEdge
    }

    // The collapsed title rides one baseline padding above the bar's bottom on its own; this
    // nudge lands its optical centre on the bar's centre line, shared with the navigation icon.
    val titleCentreTranslationY = with(density) {
        val collapsedBarHeight = KaigiTopAppBarDefaults.height.toPx()
        val restingBaseline =
            collapsedBarHeight - KaigiTopAppBarDefaults.largeTitleBaselinePadding.toPx()
        val centredBaseline =
            collapsedBarHeight / 2f + collapsedTitleScale * (titleBaseline - titleHeight / 2f)
        centredBaseline - restingBaseline
    }

    // One Text serves both states and graphicsLayer does not re-measure, so a single width has to
    // satisfy the tighter of the two budgets: the expanded insets, and the collapsed clearance read
    // back through the scale the glyphs will have shrunk to by then.
    val maxTitleWidth = if (barWidth == 0f) {
        Dp.Unspecified
    } else {
        min(
            (contentWidth - KaigiTopAppBarDefaults.collapsedTitleClearance * 2) /
                collapsedTitleScale,
            contentWidth - KaigiTopAppBarDefaults.largeTitleStartInset * 2,
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor)
            .windowInsetsPadding(windowInsets)
            .height(barHeight)
            .clipToBounds()
            .onSizeChanged { size ->
                barWidth = size.width.toFloat()
            },
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .height(KaigiTopAppBarDefaults.height)
                    .padding(horizontal = KaigiTopAppBarDefaults.topRowHorizontalPadding),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ListDetailSceneAwareBackButton(onClick = onBackClick)
                Spacer(Modifier.weight(1f))
                Actions(actions = actions)
            }

            BarTitle(
                title = title,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(
                        start = paneStartInset + KaigiTopAppBarDefaults.largeTitleStartInset,
                    )
                    .paddingFromBaseline(
                        bottom = KaigiTopAppBarDefaults.largeTitleBaselinePadding,
                    )
                    .widthIn(max = maxTitleWidth)
                    .graphicsLayer(
                        transformOrigin = titleScalePivot,
                        scaleX = titleScale,
                        scaleY = titleScale,
                        translationX = lerp(0f, titleCentreTranslationX, scrollProgress),
                        translationY = lerp(0f, titleCentreTranslationY, scrollProgress),
                    ),
            ) { textLayoutResult ->
                titleWidth = textLayoutResult.size.width
                titleHeight = textLayoutResult.size.height
                titleBaseline = textLayoutResult.firstBaseline
            }

            AnimatedVisibility(
                modifier = Modifier.align(Alignment.BottomCenter),
                visible = scrollProgress >= DIVIDER_REVEAL_PROGRESS,
                enter = fadeIn(animationSpec = tween(DIVIDER_FADE_IN_DURATION_MILLIS)),
            ) {
                SketchHorizontalDivider(seed = DIVIDER_SKETCH_SEED)
            }
        }
    }
}

private const val DIVIDER_REVEAL_PROGRESS = 0.9f

private const val DIVIDER_FADE_IN_DURATION_MILLIS = 200

private const val DIVIDER_SKETCH_SEED = 650

const val KAIGI_TOP_APP_BAR_BACK_BUTTON_TEST_TAG = "KaigiTopAppBarBackButtonTestTag"

/**
 * The back arrow every screen reached from another leads its bar with, sized and described the
 * same way in each of them.
 *
 * [ListDetailSceneAwareBackButton] wraps this for a screen that may be shown as a pane; reach for
 * this one directly only where the control cannot be either.
 *
 * @param onClick called when the arrow is clicked.
 * @param modifier the [Modifier] applied to the button.
 */
@Composable
fun KaigiTopAppBarBackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        onClick = onClick,
        modifier = modifier.testTag(KAIGI_TOP_APP_BAR_BACK_BUTTON_TEST_TAG),
    ) {
        Icon(
            imageVector = KaigiIcons.Default.ArrowBack,
            contentDescription = stringResource(Res.string.back),
            modifier = Modifier.size(KaigiIconButtonDefaults.iconSize),
        )
    }
}

@Composable
private fun BarTitle(
    title: String,
    modifier: Modifier = Modifier,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
) {
    Text(
        text = title,
        modifier = modifier,
        style = MaterialTheme.typography.headlineMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        onTextLayout = onTextLayout,
    )
}

@Composable
private fun Actions(actions: @Composable RowScope.() -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(KaigiTopAppBarDefaults.actionSpacing),
        verticalAlignment = Alignment.CenterVertically,
        content = actions,
    )
}

object KaigiTopAppBarDefaults {
    val height = 64.dp
    val largeHeight = 112.dp
    val actionSpacing = 8.dp

    /**
     * `TopAppBar` already insets its action slot by 4.dp, so this carries the remainder of the
     * 16.dp the bar keeps at the trailing edge. The leading edge needs none: the inset the bar
     * applies to a navigation icon, and the one it applies to a title without one, both come to
     * the same 16.dp on their own.
     */
    val contentPadding = PaddingValues(end = 12.dp)

    /** Matches Material 3 `TopAppBarHorizontalPadding`. */
    internal val topRowHorizontalPadding = 4.dp

    internal val largeTitleStartInset = 16.dp

    /** Aligns the expanded title with Material 3 `LargeTopAppBar`. */
    internal val largeTitleBaselinePadding = 28.dp

    internal val collapsedTitleFontSize = 22.sp

    /** The back arrow's 48.dp button plus a 16.dp gap, kept clear on each side. */
    internal val collapsedTitleClearance = 64.dp

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    internal fun colors(containerColor: Color, contentColor: Color): TopAppBarColors =
        TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            // The band is one flat colour, so scrolling content under it must not tint it.
            scrolledContainerColor = containerColor,
            navigationIconContentColor = contentColor,
            titleContentColor = contentColor,
            actionIconContentColor = contentColor,
        )
}

@Preview
@Composable
private fun KaigiTopAppBarPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        KaigiTopAppBar(title = "Timetable", windowInsets = WindowInsets(0)) {
            KaigiIconButton(seed = 777, onClick = {}) {
                Icon(KaigiIcons.Default.Search, contentDescription = null)
            }
            KaigiIconButton(seed = 778, onClick = {}) {
                Icon(KaigiIcons.Default.GridView, contentDescription = null)
            }
        }
    }
}

@LocalePreviews
@Composable
private fun KaigiLargeTopAppBarPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        KaigiLargeTopAppBar(
            title = "Contributors",
            onBackClick = {},
            windowInsets = WindowInsets(0),
        )
    }
}

@LocalePreviews
@Composable
private fun KaigiLargeTopAppBarCollapsedPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        KaigiLargeTopAppBar(
            title = "Contributors",
            onBackClick = {},
            windowInsets = WindowInsets(0),
            scrollBehavior = collapsedScrollBehavior(),
        )
    }
}

@LocalePreviews
@Composable
private fun KaigiLargeTopAppBarLongTitlePreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        KaigiLargeTopAppBar(
            title = LONG_PREVIEW_TITLE,
            onBackClick = {},
            windowInsets = WindowInsets(0),
        )
    }
}

@LocalePreviews
@Composable
private fun KaigiLargeTopAppBarLongTitleCollapsedPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        KaigiLargeTopAppBar(
            title = LONG_PREVIEW_TITLE,
            onBackClick = {},
            windowInsets = WindowInsets(0),
            scrollBehavior = collapsedScrollBehavior(),
        )
    }
}

private const val LONG_PREVIEW_TITLE = "A screen title long enough to need truncating"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun collapsedScrollBehavior(): TopAppBarScrollBehavior {
    val collapsedLimit = with(LocalDensity.current) {
        -(KaigiTopAppBarDefaults.largeHeight - KaigiTopAppBarDefaults.height).toPx()
    }
    return TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(
            initialHeightOffsetLimit = collapsedLimit,
            initialHeightOffset = collapsedLimit,
        ),
    )
}
