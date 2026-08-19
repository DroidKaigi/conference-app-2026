package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme

/**
 * The destinations of the app, gathered into one hand-drawn pill floating over the content.
 *
 * Destinations carry no label; each is named to a screen reader through the description its
 * icon is given.
 *
 * @param outlineSeed the value the pill enclosing the destinations is drawn from. The disc
 *   marking the destination in view is seeded separately, by each [KaigiNavigationBarItem].
 * @param modifier the [Modifier] applied to the bar.
 * @param content the destinations, each a [KaigiNavigationBarItem].
 */
@Composable
fun KaigiNavigationBar(
    outlineSeed: Int,
    modifier: Modifier = Modifier,
    content: @Composable KaigiNavigationBarScope.() -> Unit,
) {
    val combinedSeed = combineSketchSeed(outlineSeed)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = 32.dp,
                end = 32.dp,
                top = KaigiNavigationBarDefaults.topMargin,
                bottom = KaigiNavigationBarDefaults.bottomMargin,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .widthIn(max = KaigiNavigationBarDefaults.maxWidth)
                .fillMaxWidth()
                .height(KaigiNavigationBarDefaults.height)
                .clip(
                    SketchRoundRectShape(
                        seed = combinedSeed,
                        roughness = KaigiNavigationBarDefaults.roughness,
                        tremor = KaigiNavigationBarDefaults.tremor,
                        cornerRadius = KaigiNavigationBarDefaults.cornerRadius,
                        // Pinned so a narrower screen stretches the one outline rather than
                        // drawing a different bar.
                        referenceSize = DpSize(
                            KaigiNavigationBarDefaults.maxWidth,
                            KaigiNavigationBarDefaults.height,
                        ),
                    ),
                )
                .background(MaterialTheme.colorScheme.inverseSurface)
                .padding(horizontal = 8.dp)
                .selectableGroup(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val navigationBarScope = remember { KaigiNavigationBarScopeImpl(this) }
            navigationBarScope.content()
        }
    }
}

/** Handed to the content of a [KaigiNavigationBar], and obtainable nowhere else. */
interface KaigiNavigationBarScope : RowScope

private class KaigiNavigationBarScopeImpl(scope: RowScope) :
    KaigiNavigationBarScope,
    RowScope by scope

/**
 * The destinations of the app, gathered into the same hand-drawn pill as [KaigiNavigationBar]
 * turned vertical, running down the leading edge of an expanded window.
 *
 * The rail occupies a column of [KaigiNavigationRailDefaults.columnWidth] and is centred in it,
 * both horizontally and on the height the column is given.
 *
 * @param outlineSeed the value the pill enclosing the destinations is drawn from. The disc
 *   marking the destination in view is seeded separately, by each [KaigiNavigationRailItem].
 * @param modifier the [Modifier] applied to the rail's column.
 * @param content the destinations, each a [KaigiNavigationRailItem].
 */
@Composable
fun KaigiNavigationRail(
    outlineSeed: Int,
    modifier: Modifier = Modifier,
    content: @Composable KaigiNavigationRailScope.() -> Unit,
) {
    val combinedSeed = combineSketchSeed(outlineSeed)
    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(KaigiNavigationRailDefaults.columnWidth)
            .padding(vertical = KaigiNavigationRailDefaults.verticalMargin),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .heightIn(max = KaigiNavigationRailDefaults.maxHeight)
                .fillMaxHeight()
                .width(KaigiNavigationRailDefaults.width)
                .clip(
                    SketchRoundRectShape(
                        seed = combinedSeed,
                        roughness = KaigiNavigationBarDefaults.roughness,
                        tremor = KaigiNavigationBarDefaults.tremor,
                        cornerRadius = KaigiNavigationBarDefaults.cornerRadius,
                        // Pinned so a shorter window stretches the one outline rather than
                        // drawing a different rail.
                        referenceSize = DpSize(
                            KaigiNavigationRailDefaults.width,
                            KaigiNavigationRailDefaults.maxHeight,
                        ),
                    ),
                )
                .background(MaterialTheme.colorScheme.inverseSurface)
                .padding(vertical = 8.dp)
                .selectableGroup(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val navigationRailScope = remember { KaigiNavigationRailScopeImpl(this) }
            navigationRailScope.content()
        }
    }
}

/** Handed to the content of a [KaigiNavigationRail], and obtainable nowhere else. */
interface KaigiNavigationRailScope : ColumnScope

private class KaigiNavigationRailScopeImpl(scope: ColumnScope) :
    KaigiNavigationRailScope,
    ColumnScope by scope

/**
 * One destination of a [KaigiNavigationBar].
 *
 * The one in view is marked by a filled disc behind its icon rather than by a label.
 *
 * @param selected whether this destination is the one in view.
 * @param onClick called when the destination is clicked.
 * @param indicatorSeed the value that disc is drawn from. It takes effect only while [selected].
 * @param modifier the [Modifier] applied to the destination.
 * @param icon the icon naming the destination. Give it a content description, since the
 *   destination carries no label of its own.
 */
@Composable
fun KaigiNavigationBarScope.KaigiNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    indicatorSeed: Int,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .weight(1f)
            .height(KaigiNavigationBarDefaults.height)
            .selectable(selected = selected, role = Role.Tab, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        KaigiNavigationItemIcon(selected = selected, indicatorSeed = indicatorSeed, icon = icon)
    }
}

/**
 * One destination of a [KaigiNavigationRail].
 *
 * The one in view is marked by a filled disc behind its icon rather than by a label.
 *
 * @param selected whether this destination is the one in view.
 * @param onClick called when the destination is clicked.
 * @param indicatorSeed the value that disc is drawn from. It takes effect only while [selected].
 * @param modifier the [Modifier] applied to the destination.
 * @param icon the icon naming the destination. Give it a content description, since the
 *   destination carries no label of its own.
 */
@Composable
fun KaigiNavigationRailScope.KaigiNavigationRailItem(
    selected: Boolean,
    onClick: () -> Unit,
    indicatorSeed: Int,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .weight(1f)
            .width(KaigiNavigationRailDefaults.width)
            .selectable(selected = selected, role = Role.Tab, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        KaigiNavigationItemIcon(selected = selected, indicatorSeed = indicatorSeed, icon = icon)
    }
}

@Composable
private fun KaigiNavigationItemIcon(
    selected: Boolean,
    indicatorSeed: Int,
    icon: @Composable () -> Unit,
) {
    val combinedSeed = combineSketchSeed(indicatorSeed)
    Box(
        modifier = Modifier
            .size(KaigiNavigationBarDefaults.indicatorSize)
            .then(
                if (selected) {
                    Modifier
                        .clip(
                            SketchRoundRectShape(
                                seed = combinedSeed,
                                roughness = KaigiNavigationBarDefaults.roughness,
                                tremor = KaigiNavigationBarDefaults.tremor,
                                cornerRadius = KaigiNavigationBarDefaults.indicatorSize / 2,
                            ),
                        )
                        .background(MaterialTheme.colorScheme.inverseOnSurface)
                } else {
                    Modifier
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        val tint = if (selected) {
            MaterialTheme.colorScheme.inverseSurface
        } else {
            MaterialTheme.colorScheme.inverseOnSurface
        }
        CompositionLocalProvider(LocalContentColor provides tint, content = icon)
    }
}

object KaigiNavigationBarDefaults {
    /** The bar keeps to a thumb's reach; a wider screen leaves it centered rather than stretching it. */
    val maxWidth = 300.dp
    val height = 56.dp
    val topMargin = 12.dp

    /**
     * What the design leaves between the bar and the platform's own navigation area, which
     * sits below it and is not the bar's to draw.
     */
    val bottomMargin = 49.dp

    /**
     * The room the bar takes over whatever it floats above.
     *
     * A scrollable underneath adds this to the bottom of its content padding, or its last
     * item stays under the bar.
     */
    val occupiedHeight = height + topMargin + bottomMargin

    val cornerRadius = 28.dp
    val indicatorSize = 40.dp
    val roughness = 0.4.dp
    val tremor = 0.15.dp
}

object KaigiNavigationRailDefaults {
    /** The rail keeps to the bar's reach turned vertical; a taller window leaves it centered rather than stretching it. */
    val maxHeight = 300.dp
    val width = 56.dp

    /** The column the rail occupies along the window's leading edge. */
    val columnWidth = 80.dp

    /** Keeps the pill off the window edges where the window is shorter than the pill. */
    val verticalMargin = 12.dp
}

/**
 * The room the app's navigation takes over the bottom of the content; a scrollable root
 * destination adds it to its bottom content padding.
 *
 * Defaults to [KaigiNavigationBarDefaults.occupiedHeight] — correct wherever a bar overlays
 * the content, including the native one on iOS — and the shell provides zero beside a
 * [KaigiNavigationRail], which overlays nothing.
 */
val LocalNavigationBarOccupiedHeight: ProvidableCompositionLocal<Dp> =
    compositionLocalOf { KaigiNavigationBarDefaults.occupiedHeight }

@Preview
@Composable
private fun KaigiNavigationBarPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        Box(Modifier.background(MaterialTheme.colorScheme.surface)) {
            KaigiNavigationBar(outlineSeed = 955) {
                KaigiNavigationBarItem(selected = true, onClick = {}, indicatorSeed = 956) {
                    Icon(Icons.Filled.DateRange, contentDescription = "Timetable")
                }
                KaigiNavigationBarItem(selected = false, onClick = {}, indicatorSeed = 957) {
                    Icon(Icons.Filled.Favorite, contentDescription = "Favorites")
                }
                KaigiNavigationBarItem(selected = false, onClick = {}, indicatorSeed = 958) {
                    Icon(Icons.Filled.Info, contentDescription = "About")
                }
            }
        }
    }
}

@Preview
@Composable
private fun KaigiNavigationRailPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        Box(
            Modifier
                .background(MaterialTheme.colorScheme.surface)
                .height(400.dp),
        ) {
            KaigiNavigationRail(outlineSeed = 955) {
                KaigiNavigationRailItem(selected = true, onClick = {}, indicatorSeed = 956) {
                    Icon(Icons.Filled.DateRange, contentDescription = "Timetable")
                }
                KaigiNavigationRailItem(selected = false, onClick = {}, indicatorSeed = 957) {
                    Icon(Icons.Filled.Favorite, contentDescription = "Favorites")
                }
                KaigiNavigationRailItem(selected = false, onClick = {}, indicatorSeed = 958) {
                    Icon(Icons.Filled.Info, contentDescription = "About")
                }
            }
        }
    }
}
