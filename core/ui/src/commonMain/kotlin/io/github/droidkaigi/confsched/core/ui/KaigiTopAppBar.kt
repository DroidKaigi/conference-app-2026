package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
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
    KaigiTopAppBar(
        title = { BarTitle(title) },
        modifier = modifier,
        navigationIcon = navigationIcon,
        containerColor = containerColor,
        contentColor = contentColor,
        windowInsets = windowInsets,
        scrollBehavior = scrollBehavior,
        actions = actions,
    )
}

/**
 * The bar at the top of a screen, with a composable where the title would go.
 *
 * Reach for this where that slot carries a control rather than text — a search field, most often.
 * A screen naming itself in text takes the [String] overload.
 *
 * @param title the content filling the slot the title would occupy.
 * @param modifier the [Modifier] applied to the bar.
 * @param navigationIcon the control leading [title], most often a [KaigiTopAppBarBackButton].
 * @param containerColor the colour filling the band.
 * @param contentColor the colour [navigationIcon] and [actions] draw in.
 * @param windowInsets the insets the bar holds its content clear of.
 * @param scrollBehavior how the bar answers the content scrolling under it.
 * @param actions the controls trailing [title].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KaigiTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.inverseSurface,
    contentColor: Color = MaterialTheme.colorScheme.inverseOnSurface,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = title,
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
 * A screen only reaches this bar from another one, so it always leads with the back arrow and
 * takes the click rather than the control.
 *
 * @param title the text naming the screen.
 * @param onBackClick called when the back arrow is clicked.
 * @param modifier the [Modifier] applied to the bar.
 * @param containerColor the colour filling the band.
 * @param contentColor the colour the title, the back arrow and [actions] draw in.
 * @param windowInsets the insets the bar holds its content clear of.
 * @param scrollBehavior how the bar answers the content scrolling under it.
 * @param actions the controls trailing the back arrow.
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
    LargeTopAppBar(
        title = { BarTitle(title) },
        modifier = modifier,
        navigationIcon = { KaigiTopAppBarBackButton(onClick = onBackClick) },
        actions = { Actions(actions) },
        collapsedHeight = KaigiTopAppBarDefaults.height,
        expandedHeight = KaigiTopAppBarDefaults.largeHeight,
        windowInsets = windowInsets,
        colors = KaigiTopAppBarDefaults.colors(containerColor, contentColor),
        scrollBehavior = scrollBehavior,
    )
}

/**
 * The back arrow every screen reached from another leads its bar with, sized and described the
 * same way in each of them.
 *
 * [KaigiLargeTopAppBar] draws this itself. Reach for it directly only where the control has to
 * change with the screen's state, as a bar that dismisses rather than returns does.
 *
 * @param onClick called when the arrow is clicked.
 * @param modifier the [Modifier] applied to the button.
 */
@Composable
fun KaigiTopAppBarBackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = KaigiIcons.Default.ArrowBack,
            contentDescription = stringResource(Res.string.back),
            modifier = Modifier.size(KaigiIconButtonDefaults.iconSize),
        )
    }
}

@Composable
private fun BarTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun Actions(actions: @Composable RowScope.() -> Unit) {
    Row(
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
