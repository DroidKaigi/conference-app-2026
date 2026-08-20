package io.github.droidkaigi.confsched.feature.sponsors.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.SponsorGroup
import io.github.droidkaigi.confsched.core.model.SponsorPlan

internal const val SPONSOR_GRID_COLUMNS = 6

// Extra spacing to meet the design's required heading-to-content vertical gap.
private val HEADING_EXTRA_SPACING = 20.dp

internal fun LazyGridScope.sponsorPlanSection(
    group: SponsorGroup,
    onSponsorClick: (String) -> Unit,
) {
    // The payload does not guarantee a unique sponsorName and a duplicate key throws in a lazy
    // layout, so this grid stays on positional keys.
    item(span = { GridItemSpan(maxLineSpan) }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = group.plan.headingExtraTopSpacing, bottom = HEADING_EXTRA_SPACING),
            contentAlignment = Alignment.Center,
        ) {
            SponsorHeadingDecoration(plan = group.plan)
        }
    }
    items(
        items = group.sponsors,
        span = { GridItemSpan(group.plan.itemColumnSpan) },
    ) { sponsor ->
        SponsorItem(
            name = sponsor.name,
            logoUrl = sponsor.logoUrl,
            onSponsorClick = { onSponsorClick(sponsor.link) },
            shape = group.plan.itemShape,
            contentPadding = group.plan.itemContentPadding,
            modifier = Modifier.fillMaxWidth().height(group.plan.itemHeight),
        )
    }
}

private val SponsorPlan.itemColumnSpan: Int
    get() = when (this) {
        SponsorPlan.Platinum -> SPONSOR_GRID_COLUMNS
        SponsorPlan.Gold -> SPONSOR_GRID_COLUMNS / 2
        SponsorPlan.Supporter -> SPONSOR_GRID_COLUMNS / 3
    }

private val SponsorPlan.itemHeight: Dp
    get() = when (this) {
        SponsorPlan.Platinum -> 80.dp
        SponsorPlan.Gold -> 80.dp
        SponsorPlan.Supporter -> 60.dp
    }

private val SponsorPlan.itemShape: Shape
    get() = when (this) {
        SponsorPlan.Platinum -> RoundedCornerShape(16.dp)
        SponsorPlan.Gold -> RoundedCornerShape(12.dp)
        SponsorPlan.Supporter -> RoundedCornerShape(8.dp)
    }

private val SponsorPlan.itemContentPadding: PaddingValues
    get() = when (this) {
        SponsorPlan.Platinum -> PaddingValues(16.dp)
        SponsorPlan.Gold -> PaddingValues(12.dp)
        SponsorPlan.Supporter -> PaddingValues(8.dp)
    }

// Extra top spacing applied before Gold and Supporter headings.
private val SponsorPlan.headingExtraTopSpacing: Dp
    get() = when (this) {
        SponsorPlan.Platinum -> 0.dp
        SponsorPlan.Gold -> HEADING_EXTRA_SPACING
        SponsorPlan.Supporter -> HEADING_EXTRA_SPACING
    }
