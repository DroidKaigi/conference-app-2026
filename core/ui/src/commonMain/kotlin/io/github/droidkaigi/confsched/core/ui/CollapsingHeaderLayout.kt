package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastMaxOfOrDefault
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.SCREEN_PREVIEW_HEIGHT_DP
import io.github.droidkaigi.confsched.core.preview.SCREEN_PREVIEW_WIDTH_DP
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import kotlin.math.roundToInt

private enum class CollapsingHeaderLayoutSlot {
    CollapsingContent,
    Content,
}

/**
 * How far a [CollapsingHeaderLayout] has folded its collapsing header away, and the policy that
 * decides how a scroll delta moves it. Each policy is a subclass carrying its own
 * [nestedScrollConnection]; obtain one from the matching `remember…State` function.
 */
@Stable
sealed class CollapsingHeaderState {

    abstract val nestedScrollConnection: NestedScrollConnection

    /** How far the collapsing header sits above its resting place: `0` at full height. */
    var collapsingOffsetY: Float by mutableFloatStateOf(0f)
        protected set

    /** The height of the collapsing header, measured by the layout it is handed to. */
    var collapsibleHeightPx: Float by mutableFloatStateOf(0f)
        internal set

    /**
     * Folds the header on a downward scroll and returns it to full height on any upward scroll,
     * from whatever position the content is in.
     */
    class EnterAlways internal constructor() : CollapsingHeaderState() {

        override val nestedScrollConnection: NestedScrollConnection = object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val newOffset = (collapsingOffsetY + available.y).coerceIn(-collapsibleHeightPx, 0f)
                val consumed = newOffset - collapsingOffsetY
                collapsingOffsetY = newOffset
                return Offset(0f, consumed)
            }
        }
    }
}

@Composable
fun rememberCollapsingHeaderEnterAlwaysState(): CollapsingHeaderState.EnterAlways {
    return remember { CollapsingHeaderState.EnterAlways() }
}

/**
 * A header that folds away above scrolling content as the content scrolls down.
 *
 * The layout takes each scroll delta before the content does, folds [headerContent] by as much of
 * it as that header's height allows, and leaves the remainder to the content. Any upward scroll
 * returns the header to full height from whatever position the content is in. The header is
 * clipped to the layout's bounds, so whatever sits above the layout stays in front of it.
 *
 * @param state how far the header has folded away, and the policy moving it.
 * @param headerContent the folding header. It must be opaque: content already scrolled away
 *   passes underneath it while the header returns to full height.
 * @param modifier the [Modifier] applied to the layout.
 * @param content the scrolling content, taking the padding that holds it clear of the header. The
 *   padding is measured in the same layout pass as the header, so content positioned against the
 *   header's edge moves with it rather than a frame behind it. Apply it as the scrolling
 *   container's content padding rather than as a margin, so the content scrolls under the header
 *   instead of ending at its edge.
 */
@Composable
fun CollapsingHeaderLayout(
    state: CollapsingHeaderState,
    headerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (contentPadding: PaddingValues) -> Unit,
) {
    SubcomposeLayout(
        modifier = modifier.clipToBounds().nestedScroll(state.nestedScrollConnection),
    ) { constraints ->
        val headerConstraints = constraints.copy(minWidth = 0, minHeight = 0)

        val headerMeasurables = subcompose(CollapsingHeaderLayoutSlot.CollapsingContent, headerContent)
        val headerPlaceables = headerMeasurables.map { it.measure(headerConstraints) }
        val headerHeightPx = headerPlaceables.fastMaxOfOrDefault(0) { it.height }
        state.collapsibleHeightPx = headerHeightPx.toFloat()

        val collapsingOffsetYPx = state.collapsingOffsetY
            .coerceIn(-headerHeightPx.toFloat(), 0f)
            .roundToInt()
        val collapsingHeaderContentPadding = PaddingValues(
            top = (headerHeightPx + collapsingOffsetYPx).toDp(),
        )
        val contentMeasurables = subcompose(CollapsingHeaderLayoutSlot.Content) {
            content(collapsingHeaderContentPadding)
        }
        val contentPlaceables = contentMeasurables.map { it.measure(constraints) }

        layout(constraints.maxWidth, constraints.maxHeight) {
            contentPlaceables.forEach { it.placeRelative(0, 0) }
            headerPlaceables.forEach { it.placeRelative(0, collapsingOffsetYPx) }
        }
    }
}

@Preview(widthDp = SCREEN_PREVIEW_WIDTH_DP, heightDp = SCREEN_PREVIEW_HEIGHT_DP)
@Composable
private fun CollapsingHeaderLayoutPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        CollapsingHeaderLayout(
            state = rememberCollapsingHeaderEnterAlwaysState(),
            headerContent = {
                Box(Modifier.fillMaxWidth().height(48.dp).background(MaterialTheme.colorScheme.inverseSurface))
            },
            modifier = Modifier.fillMaxSize(),
        ) { contentPadding ->
            LazyColumn(contentPadding = contentPadding) {
                items(6) { index ->
                    Text(text = "Item $index", modifier = Modifier.height(48.dp))
                }
            }
        }
    }
}
