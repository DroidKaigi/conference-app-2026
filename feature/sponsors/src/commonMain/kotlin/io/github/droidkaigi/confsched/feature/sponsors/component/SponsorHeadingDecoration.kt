package io.github.droidkaigi.confsched.feature.sponsors.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.SponsorPlan
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.feature.sponsors.generated.resources.Res
import io.github.droidkaigi.confsched.feature.sponsors.generated.resources.sponsor_plan_gold
import io.github.droidkaigi.confsched.feature.sponsors.generated.resources.sponsor_plan_platinum
import io.github.droidkaigi.confsched.feature.sponsors.generated.resources.sponsor_plan_supporter
import io.github.droidkaigi.confsched.feature.sponsors.generated.resources.sponsors_gold_heading
import io.github.droidkaigi.confsched.feature.sponsors.generated.resources.sponsors_platinum_heading
import io.github.droidkaigi.confsched.feature.sponsors.generated.resources.sponsors_supporters_heading
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SponsorHeadingDecoration(
    plan: SponsorPlan,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(plan.headingDrawable),
        contentDescription = stringResource(plan.headingTitle),
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
        modifier = modifier,
    )
}

private val SponsorPlan.headingDrawable: DrawableResource
    get() = when (this) {
        SponsorPlan.Supporter -> Res.drawable.sponsors_supporters_heading
        SponsorPlan.Gold -> Res.drawable.sponsors_gold_heading
        SponsorPlan.Platinum -> Res.drawable.sponsors_platinum_heading
    }

private val SponsorPlan.headingTitle: StringResource
    get() = when (this) {
        SponsorPlan.Supporter -> Res.string.sponsor_plan_supporter
        SponsorPlan.Gold -> Res.string.sponsor_plan_gold
        SponsorPlan.Platinum -> Res.string.sponsor_plan_platinum
    }

@LocalePreviews
@Composable
private fun SponsorHeadingDecorationPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        Column {
            SponsorHeadingDecoration(SponsorPlan.Platinum)
            Spacer(Modifier.height(16.dp))
            SponsorHeadingDecoration(SponsorPlan.Gold)
            Spacer(Modifier.height(16.dp))
            SponsorHeadingDecoration(SponsorPlan.Supporter)
        }
    }
}
