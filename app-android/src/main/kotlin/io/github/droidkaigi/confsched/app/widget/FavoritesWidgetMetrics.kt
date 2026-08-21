package io.github.droidkaigi.confsched.app.widget

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

// Spacing follows the widget spec's five-step scale on a 4dp base.
internal val InsetBleed = 8.dp
internal val InsetFrame = 12.dp
internal val InsetRow = 8.dp
internal val GapTight = 4.dp
internal val GapBase = 8.dp
internal val GapWide = 16.dp
internal val GapArt = 20.dp
internal val RowHeight = 22.dp
internal val TimeCellWidth = 40.dp

// Halfway between the 2x2 (158dp) and 4x2 (338dp) design sizes.
private val MediumMinWidth = 250.dp

internal fun isMedium(size: DpSize): Boolean = size.width >= MediumMinWidth

internal fun mascotClearance(medium: Boolean): Dp = if (medium) 37.dp + GapArt else 0.dp
