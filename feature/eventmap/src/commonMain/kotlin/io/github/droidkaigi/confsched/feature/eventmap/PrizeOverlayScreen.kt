package io.github.droidkaigi.confsched.feature.eventmap

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Prizes
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.current
import io.github.droidkaigi.confsched.feature.eventmap.component.PrizePageCard
import io.github.droidkaigi.confsched.feature.eventmap.component.PrizePageCardWidth
import io.github.droidkaigi.confsched.feature.eventmap.component.prizePageCardTestTag
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.Res
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.prize_overlay_close
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.stamp_collecting_prize_overlay_close
import io.github.droidkaigi.confsched.feature.eventmap.generated.resources.stamp_collecting_prize_overlay_counter
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private const val PRIZE_PAGE_SEED_BASE = 310

private val PrizePageSpacing = 16.dp

internal const val PRIZE_OVERLAY_SCREEN_CLOSE_BUTTON_TEST_TAG = "PrizeOverlayScreenCloseButtonTestTag"

@Composable
fun PrizeOverlayScreen(
    uiState: PrizeOverlayScreenUiState,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(initialPage = uiState.initialPage) { uiState.prizes.size }
    val scrimInteractionSource = remember(::MutableInteractionSource)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PrizeOverlayScrimColor)
            .clickable(
                interactionSource = scrimInteractionSource,
                indication = null,
                onClick = onCloseClick,
            ),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.align(Alignment.Center),
        ) {
            BoxWithConstraints {
                HorizontalPager(
                    state = pagerState,
                    pageSize = PageSize.Fixed(PrizePageCardWidth),
                    pageSpacing = PrizePageSpacing,
                    contentPadding = PaddingValues(
                        horizontal = ((maxWidth - PrizePageCardWidth) / 2).coerceAtLeast(0.dp),
                    ),
                ) { page ->
                    val prize = uiState.prizes[page]
                    PrizePageCard(
                        name = prize.name.current(),
                        imageUrl = prize.imageUrl,
                        group = prize.group,
                        seed = PRIZE_PAGE_SEED_BASE + page,
                        // The scrim behind closes the overlay; a tap that lands on the card is not
                        // a tap outside it.
                        modifier = Modifier
                            .testTag(prizePageCardTestTag(prize.id))
                            .pointerInput(Unit) { detectTapGestures {} },
                    )
                }
            }
            Text(
                text = stringResource(
                    Res.string.stamp_collecting_prize_overlay_counter,
                    pagerState.currentPage + 1,
                    uiState.prizes.size,
                ),
                style = MaterialTheme.typography.titleSmallEmphasized.copy(letterSpacing = 0.78.sp),
                color = PrizeOverlayContentColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        IconButton(
            onClick = onCloseClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(end = 8.dp)
                .testTag(PRIZE_OVERLAY_SCREEN_CLOSE_BUTTON_TEST_TAG),
        ) {
            Image(
                painter = painterResource(Res.drawable.prize_overlay_close),
                contentDescription = stringResource(Res.string.stamp_collecting_prize_overlay_close),
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

// The overlay lays its own scrim over whatever screen it opens on, so the marks on that scrim keep
// one light colour in every theme rather than following the surface behind them.
private val PrizeOverlayScrimColor = Color.Black.copy(alpha = 0.5f)
private val PrizeOverlayContentColor = Color(0xFFE8ECF4)

@LocalePreviews
@Composable
private fun PrizeOverlayScreenPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        PrizeOverlayScreen(
            uiState = PrizeOverlayScreenUiState(
                prizes = StampCollectingScreenUiState.of(Prizes.fake()).prizes,
                initialPage = 1,
            ),
            onCloseClick = {},
        )
    }
}
