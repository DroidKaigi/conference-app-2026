package io.github.droidkaigi.confsched.feature.eventmap.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.PrizeGroup
import io.github.droidkaigi.confsched.core.model.PrizeId
import io.github.droidkaigi.confsched.core.model.Prizes
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiChip
import io.github.droidkaigi.confsched.core.ui.RemoteImage
import io.github.droidkaigi.confsched.core.ui.SketchCard
import io.github.droidkaigi.confsched.core.ui.SketchRoundRectShape
import io.github.droidkaigi.confsched.core.ui.current
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.Res
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.stamp_collecting_group
import org.jetbrains.compose.resources.stringResource

internal val PrizePageCardWidth = 300.dp

private val PrizePlateWidth = 268.dp
private val PrizePlateHeight = 206.dp

fun prizePageCardTestTag(prizeId: PrizeId) = "PrizePageCard:${prizeId.value}"

@Composable
internal fun PrizePageCard(
    id: PrizeId,
    name: String,
    imageUrl: String,
    group: PrizeGroup,
    seed: Int,
    modifier: Modifier = Modifier,
) {
    SketchCard(
        shape = SketchRoundRectShape(seed = seed, cornerRadius = 20.dp, borderThickness = 2.dp),
        modifier = modifier.testTag(prizePageCardTestTag(id)),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(16.dp)
                .width(PrizePlateWidth),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PrizePlateHeight)
                    // Prize photos are shot on white, so the plate keeps true white in every theme.
                    .background(color = Color.White, shape = RoundedCornerShape(6.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(6.dp),
                    ),
            ) {
                RemoteImage(
                    imageUrl = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            KaigiChip(
                seed = seed,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            ) {
                Text(
                    text = stringResource(Res.string.stamp_collecting_group, group.name).uppercase(),
                    style = MaterialTheme.typography.titleSmallEmphasized.copy(letterSpacing = 1.12.sp),
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 19.sp),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@LocalePreviews
@Composable
private fun PrizePageCardPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        val prize = Prizes.fake().items[1]
        PrizePageCard(
            id = prize.id,
            name = prize.name.current(),
            imageUrl = prize.imageUrl,
            group = PrizeGroup.A,
            seed = 310,
            modifier = Modifier
                .padding(16.dp)
                .width(PrizePageCardWidth),
        )
    }
}
