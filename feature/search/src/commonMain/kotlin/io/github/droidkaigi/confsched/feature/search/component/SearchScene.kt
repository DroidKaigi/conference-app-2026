package io.github.droidkaigi.confsched.feature.search.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.LocalKaigiIllustrationColors
import io.github.droidkaigi.confsched.core.designsystem.LocalSketchBaseSeed
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.SketchGroundLine
import io.github.droidkaigi.confsched.core.ui.scaleSketchAmplitude
import io.github.droidkaigi.confsched.feature.search.generated.resources.Res
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_mascot_a
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_mascot_b
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_mascot_c
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_mascot_d
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_mascot_e
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.random.Random

internal enum class SearchSceneDirection {
    RummageBox,
    Constellation,
    Magnifier,
    Signpost,
    EmptyBox,
}

internal enum class SearchMascot {
    A,
    B,
    C,
    D,
    E,
}

@Immutable
internal data class SearchSceneSelection(
    val initialDirection: SearchSceneDirection,
    val noMatchDirection: SearchSceneDirection,
    val mascot: SearchMascot,
)

internal fun searchSceneSelection(appSeed: Int): SearchSceneSelection {
    val random = Random(appSeed)
    val initialDirections = listOf(
        SearchSceneDirection.RummageBox,
        SearchSceneDirection.Constellation,
        SearchSceneDirection.Magnifier,
    )
    val noMatchDirections = listOf(
        SearchSceneDirection.Signpost,
        SearchSceneDirection.EmptyBox,
    )
    return SearchSceneSelection(
        initialDirection = initialDirections[random.nextInt(initialDirections.size)],
        noMatchDirection = noMatchDirections[random.nextInt(noMatchDirections.size)],
        mascot = SearchMascot.entries[random.nextInt(SearchMascot.entries.size)],
    )
}

@Composable
internal fun rememberSearchSceneSelection(): SearchSceneSelection {
    val appSeed = LocalSketchBaseSeed.current
    return remember(appSeed) { searchSceneSelection(appSeed) }
}

@Composable
internal fun SearchScene(
    direction: SearchSceneDirection,
    mascot: SearchMascot,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val illustrationColors = LocalKaigiIllustrationColors.current
    val imageVector = remember(direction, colors, illustrationColors) {
        searchSceneVector(direction, colors, illustrationColors)
    }
    val spec = direction.spec
    val sceneSize = DpSize(imageVector.defaultWidth, imageVector.defaultHeight)
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter,
    ) {
        if (maxWidth <= 0.dp) return@BoxWithConstraints
        val sceneScale = minOf(1f, maxWidth.value / spec.slotSize.width.value)
        val groundThickness = 2.dp * sceneScale
        val groundAmplitude = 1.6.dp * sceneScale
        val groundLineHeight = groundThickness + scaleSketchAmplitude(groundAmplitude) * 2
        Box(
            modifier = Modifier.size(
                width = spec.slotSize.width * sceneScale,
                height = spec.slotSize.height * sceneScale,
            ),
        ) {
            Box(
                modifier = Modifier
                    .offset(
                        x = spec.sceneOffset.x * sceneScale,
                        y = spec.sceneOffset.y * sceneScale,
                    )
                    .size(
                        width = sceneSize.width * sceneScale,
                        height = sceneSize.height * sceneScale,
                    ),
            ) {
                spec.groundLine?.let { groundLine ->
                    SketchGroundLine(
                        seed = groundLine.seed,
                        color = colors.onSurface,
                        thickness = groundThickness,
                        amplitude = groundAmplitude,
                        period = 26.dp * sceneScale,
                        modifier = Modifier
                            .offset(
                                x = groundLine.startX * sceneScale,
                                y = groundLine.centerY * sceneScale - groundLineHeight / 2,
                            )
                            .width(groundLine.width * sceneScale),
                    )
                }
                Image(
                    imageVector = imageVector,
                    contentDescription = null,
                    modifier = Modifier.size(
                        width = sceneSize.width * sceneScale,
                        height = sceneSize.height * sceneScale,
                    ),
                )
                Image(
                    painter = painterResource(mascot.resource),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(colors.primary),
                    modifier = Modifier
                        .offset(
                            x = (spec.mascotCenterX - mascot.size.width / 2) * sceneScale,
                            y = (spec.mascotBottomY - mascot.size.height) * sceneScale,
                        )
                        .size(
                            width = mascot.size.width * sceneScale,
                            height = mascot.size.height * sceneScale,
                        ),
                )
            }
        }
    }
}

private data class SearchSceneSpec(
    val slotSize: DpSize,
    val sceneOffset: DpOffset = DpOffset.Zero,
    val mascotCenterX: Dp,
    val mascotBottomY: Dp,
    val groundLine: SearchGroundLineSpec?,
)

private data class SearchGroundLineSpec(
    val startX: Dp,
    val centerY: Dp,
    val width: Dp,
    val seed: Int,
)

private val SearchSceneDirection.spec: SearchSceneSpec
    get() = when (this) {
        SearchSceneDirection.RummageBox -> SearchSceneSpec(
            slotSize = DpSize(332.dp, 252.dp),
            sceneOffset = DpOffset(16.dp, 0.dp),
            mascotCenterX = 56.5.dp,
            mascotBottomY = 188.76.dp,
            groundLine = SearchGroundLineSpec(
                startX = 8.dp,
                centerY = 179.dp,
                width = 284.dp,
                seed = 4101,
            ),
        )

        SearchSceneDirection.Constellation -> SearchSceneSpec(
            slotSize = DpSize(332.dp, 252.dp),
            sceneOffset = DpOffset((-0.8).dp, (-0.8).dp),
            mascotCenterX = 150.44.dp,
            mascotBottomY = 237.56.dp,
            groundLine = SearchGroundLineSpec(
                startX = 6.8.dp,
                centerY = 236.5.dp,
                width = 320.25.dp,
                seed = 4213,
            ),
        )

        SearchSceneDirection.Magnifier -> SearchSceneSpec(
            slotSize = DpSize(332.dp, 252.dp),
            mascotCenterX = 119.5.dp,
            mascotBottomY = 162.69.dp,
            groundLine = null,
        )

        SearchSceneDirection.Signpost -> SearchSceneSpec(
            slotSize = DpSize(260.dp, 150.dp),
            mascotCenterX = 78.5.dp,
            mascotBottomY = 120.12.dp,
            groundLine = SearchGroundLineSpec(
                startX = 4.dp,
                centerY = 119.4.dp,
                width = 252.dp,
                seed = 4427,
            ),
        )

        SearchSceneDirection.EmptyBox -> SearchSceneSpec(
            slotSize = DpSize(260.dp, 150.dp),
            mascotCenterX = 53.5.dp,
            mascotBottomY = 130.28.dp,
            groundLine = SearchGroundLineSpec(
                startX = 2.dp,
                centerY = 129.5.dp,
                width = 256.dp,
                seed = 4532,
            ),
        )
    }

private val SearchMascot.resource: DrawableResource
    get() = when (this) {
        SearchMascot.A -> Res.drawable.search_mascot_a
        SearchMascot.B -> Res.drawable.search_mascot_b
        SearchMascot.C -> Res.drawable.search_mascot_c
        SearchMascot.D -> Res.drawable.search_mascot_d
        SearchMascot.E -> Res.drawable.search_mascot_e
    }

private val SearchMascot.size: DpSize
    get() = when (this) {
        SearchMascot.A -> DpSize(57.dp, 52.dp)
        SearchMascot.B -> DpSize(54.dp, 52.dp)
        SearchMascot.C -> DpSize(47.dp, 54.dp)
        SearchMascot.D -> DpSize(55.dp, 52.dp)
        SearchMascot.E -> DpSize(44.dp, 52.dp)
    }

@Preview
@Composable
private fun SearchScenePreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp),
        ) {
            SearchSceneDirection.entries
                .zip(SearchMascot.entries)
                .chunked(2)
                .forEach { scenes ->
                    Row {
                        for ((direction, mascot) in scenes) {
                            SearchScene(
                                direction = direction,
                                mascot = mascot,
                                modifier = Modifier.width(180.dp),
                            )
                        }
                    }
                }
        }
    }
}
