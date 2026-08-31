package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> TimetableItemCardsFlowRow(
    items: List<T>,
    modifier: Modifier = Modifier,
    itemContent: @Composable (item: T) -> Unit,
) {
    val spacing = TimetableItemCardsFlowRowDefaults.spacing
    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        // Floor the card width in pixels, or rounding could push the last card of a row onto the next one.
        val cardWidth = with(density) {
            val spacingPx = spacing.roundToPx()
            val minCardWidthPx = TimetableItemCardsFlowRowDefaults.minCardWidth.roundToPx()
            val columns = maxOf(1, (constraints.maxWidth + spacingPx) / (minCardWidthPx + spacingPx))
            ((constraints.maxWidth - spacingPx * (columns - 1)) / columns).toDp()
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalArrangement = Arrangement.spacedBy(spacing),
        ) {
            for (item in items) {
                Box(
                    modifier = Modifier.width(cardWidth).fillMaxRowHeight(),
                    propagateMinConstraints = true,
                ) {
                    itemContent(item)
                }
            }
        }
    }
}

private object TimetableItemCardsFlowRowDefaults {
    val minCardWidth = 280.dp
    val spacing = 16.dp
}
