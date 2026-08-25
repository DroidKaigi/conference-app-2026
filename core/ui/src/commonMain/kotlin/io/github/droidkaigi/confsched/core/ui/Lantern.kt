package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.LocalKaigiIllustrationColors
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme


/**
 * Defines the unique style of a lantern.
 */
@Immutable
internal data class LanternStyle(
    val hangingCord: Dp,
    val width: Dp,
    val height: Dp,
    val pathData: String,
    val ribPathData: List<String>,
    val viewBox: Pair<Float, Float>,
    val ribViewBox: Pair<Float, Float>,
    val ribOffsetY: Float,
    val capPathData: String,
    val capViewBox: Pair<Float, Float>,
) {
    companion object {
        val Type0 = LanternStyle(
            hangingCord = 16.dp,
            width = 29.dp,
            height = 39.dp,
            pathData = "M25.5869 3.81366C25.2312 2.97449 26.8528 1.72175 23.8993 1.32119C20.9459 0.920621 10.096 0.870066 7.12764 1.31013C4.15931 1.7502 5.73613 3.17066 5.3473 4.07161C4.95846 4.97255 4.97216 6.09249 4.69742 6.94102C4.42268 7.78956 3.95449 8.59027 3.63015 9.37495C3.3058 10.1596 2.99467 10.9334 2.67026 11.8453C2.34584 12.7571 1.84593 14.2077 1.60254 15.0739C1.35914 15.94 1.22094 16.4561 1.14904 17.2587C1.07714 18.0613 1.17432 19.1641 1.15315 20.0899C1.13197 21.0158 0.943798 22.1352 1.01669 23.0451C1.08959 23.9551 1.36962 24.9021 1.60874 25.7773C1.84786 26.6525 2.21366 27.6297 2.5112 28.515C2.80874 29.4003 3.13475 30.4262 3.46839 31.3105C3.80204 32.1948 4.18097 33.2382 4.59648 34.0418C5.01198 34.8454 5.61334 35.4851 6.0653 36.333C6.51727 37.1809 4.51637 38.9154 7.42126 39.3412C10.3261 39.7671 21.3912 39.4513 24.2209 38.9945C27.0506 38.5378 24.7072 37.3505 25.1069 36.4865C25.5066 35.6226 26.3016 34.4363 26.719 33.5945C27.1364 32.7527 27.3771 32.0972 27.7158 31.2253C28.0544 30.3535 28.4999 29.0047 28.8358 28.1456C29.1718 27.2865 29.6149 26.6696 29.8156 25.8559C30.0164 25.0423 29.994 23.9306 30.0905 23.0602C30.187 22.1897 30.534 21.3117 30.4188 20.4156C30.3036 19.5195 29.5987 18.3863 29.3706 17.4596C29.1425 16.5329 29.057 15.4501 28.993 14.6238C28.929 13.7975 29.1798 13.1762 28.9706 12.2953C28.7614 11.4145 28.1413 10.035 27.6857 9.1183C27.23 8.2016 26.4586 7.41475 26.1228 6.56601C25.787 5.71727 25.9427 4.65284 25.5869 3.81366Z",
            ribPathData = listOf(
                "M2.91533 0.649902C-0.925697 0.873088 6.75637 1.5408 9.31705 1.86617C11.8777 2.19154 13.1581 2.31908 15.7188 2.27675C18.2795 2.23441 19.5598 1.95979 22.1205 1.65449C24.6812 1.34919 32.3632 0.95116 28.5222 0.750243",
                "M1.79844 9.99973C-2.37756 10.2653 5.97444 10.9674 8.75844 11.2612C11.5424 11.555 12.9344 11.5164 15.7184 11.4687C18.5024 11.421 19.8944 11.3298 22.6784 11.0226C25.4624 10.7155 33.8144 10.1377 29.6384 9.93311",
                "M2.91533 19.1745C-0.925697 19.4825 6.75637 20.1189 9.31705 20.4811C11.8777 20.8433 13.1581 21.0387 15.7188 20.9857C18.2795 20.9326 19.5598 20.6247 22.1205 20.2157C24.6812 19.8068 32.3632 19.1492 28.5222 18.9409",
            ),
            viewBox = 32f to 41f,
            ribViewBox = 32f to 22f,
            ribOffsetY = 10.0f,
            capPathData = "M1.09961 1.1001H13.2796",
            capViewBox = 15f to 3f,
        )
        val Type1 = LanternStyle(
            hangingCord = 26.dp,
            width = 27.dp,
            height = 36.dp,
            pathData = "M23.9338 3.85855C23.6326 2.99501 25.489 1.67674 22.6801 1.27633C19.8713 0.875916 9.07375 0.91771 6.37835 1.35596C3.68296 1.79421 6.20137 3.14928 5.83392 4.01539C5.46646 4.88149 4.49626 5.98818 4.08176 6.76912C3.66726 7.55007 3.53929 8.12667 3.24329 8.89629C2.94729 9.6659 2.49839 10.7608 2.23177 11.5792C1.96515 12.3977 1.77225 13.2221 1.57689 14.0117C1.38153 14.8013 1.06843 15.7236 1.01077 16.5141C0.953116 17.3046 1.12144 18.1466 1.21653 18.9523C1.31163 19.7579 1.48979 20.7858 1.60514 21.5494C1.72049 22.3131 1.78986 22.9285 1.93748 23.7251C2.0851 24.5217 2.3285 25.6695 2.52774 26.528C2.72699 27.3865 2.86229 28.2542 3.18277 29.0908C3.50325 29.9273 4.22346 30.9941 4.53073 31.7563C4.838 32.5184 4.71798 33.0524 5.1032 33.8543C5.48843 34.6562 4.21904 36.3329 6.93838 36.7682C9.65771 37.2036 19.448 37.0025 22.099 36.5754C24.7501 36.1483 23.089 34.8924 23.5076 34.0986C23.9262 33.3047 24.2666 32.4048 24.7153 31.6136C25.164 30.8225 26.0233 29.9762 26.312 29.1539C26.6006 28.3315 26.3241 27.2788 26.5194 26.4741C26.7147 25.6693 27.3231 24.8928 27.5325 24.1241C27.7419 23.3553 27.7157 22.5014 27.8282 21.6694C27.9406 20.8374 28.159 19.741 28.2353 18.9241C28.3117 18.1072 28.4988 17.3827 28.3055 16.5638C28.1122 15.7449 27.2819 14.6142 27.0271 13.8062C26.7724 12.9982 26.8534 12.2935 26.7135 11.5139C26.5737 10.7344 26.4969 9.70846 26.1528 8.93399C25.8088 8.15952 24.9182 7.48554 24.5632 6.67347C24.2081 5.8614 24.2351 4.72209 23.9338 3.85855Z",
            ribPathData = listOf(
                "M2.76048 0.649902C-0.815654 0.812056 6.33661 1.18555 8.7207 1.47309C11.1048 1.76063 12.2968 2.07928 14.6809 2.08762C17.065 2.09595 18.2571 1.79982 20.6411 1.51476C23.0252 1.2297 30.1775 0.835295 26.6014 0.662322",
                "M1.71926 9.44897C-2.16874 9.65355 5.60726 10.0613 8.19926 10.3173C10.7913 10.5733 12.0873 10.7259 14.6793 10.7289C17.2713 10.7318 18.5673 10.619 21.1593 10.3322C23.7513 10.0453 31.5273 9.47107 27.6393 9.29443",
                "M2.76048 18.0041C-0.815654 18.2511 6.33661 18.5932 8.7207 18.9176C11.1048 19.242 12.2968 19.6286 14.6809 19.6262C17.065 19.6238 18.2571 19.2943 20.6411 18.9056C23.0252 18.5169 30.1775 17.8629 26.6014 17.6826",
            ),
            viewBox = 30f to 39f,
            ribViewBox = 30f to 21f,
            ribOffsetY = 11.0f,
            capPathData = "M1.09961 1.1001H13.2796",
            capViewBox = 15f to 3f,
        )
        val Type2 = LanternStyle(
            hangingCord = 11.dp,
            width = 30.dp,
            height = 41.dp,
            pathData = "M26.3582 4.28534C25.9566 3.40051 27.7813 2.08568 24.7672 1.58922C21.7531 1.09276 10.4218 0.783616 7.52015 1.18246C4.61849 1.5813 7.06793 3.14896 6.63184 4.08198C6.19576 5.01499 5.24996 6.01787 4.7946 7.01382C4.33923 8.00977 4.0644 9.32305 3.7858 10.3066C3.50721 11.2902 3.29833 12.2507 3.05338 13.1612C2.80843 14.0718 2.58222 15.156 2.25487 15.9974C1.92752 16.8387 1.09528 17.5473 1.00742 18.4197C0.919559 19.292 1.63969 20.4634 1.70574 21.4495C1.77179 22.4357 1.34047 23.7036 1.42023 24.5831C1.5 25.4626 1.93557 26.0521 2.20429 26.9464C2.473 27.8406 2.86629 29.1793 3.09969 30.1721C3.33308 31.165 3.32289 32.2901 3.66301 33.1517C4.00314 34.0133 4.85558 34.7297 5.22545 35.5573C5.59532 36.3849 5.64408 37.4266 5.97471 38.3245C6.30534 39.2224 4.181 40.6507 7.29187 41.1691C10.4027 41.6875 22.3076 41.9971 25.4177 41.5642C28.5277 41.1313 26.3559 39.3829 26.7299 38.4635C27.1038 37.5441 27.3845 36.6866 27.7548 35.8179C28.125 34.9492 28.7135 33.9812 29.044 33.034C29.3746 32.0869 29.5314 30.8025 29.8207 29.8982C30.11 28.9939 30.6445 28.2798 30.8522 27.3821C31.06 26.4844 31.0695 25.2482 31.1191 24.2877C31.1688 23.3273 31.2204 22.254 31.1623 21.3794C31.1041 20.5048 30.8707 19.6857 30.7555 18.8213C30.6403 17.9568 30.5907 16.8979 30.4422 15.9767C30.2938 15.0555 30.0554 13.9822 29.8278 13.0637C29.6003 12.1453 29.428 11.1875 29.0199 10.2364C28.6119 9.28537 27.7034 8.07162 27.2775 7.11945C26.8516 6.16727 26.7598 5.17018 26.3582 4.28534Z",
            ribPathData = listOf(
                "M2.99276 0.649902C-0.980718 0.893367 6.96624 1.76682 9.61523 2.07626C12.2642 2.3857 13.5887 2.22932 16.2377 2.19711C18.8867 2.16491 20.2112 2.18287 22.8602 1.91523C25.5092 1.6476 33.4561 1.112 29.4826 0.858934",
                "M1.83803 10.2935C-2.48197 10.5793 6.15803 11.3871 9.03803 11.765C11.918 12.1428 13.358 12.2204 16.238 12.1828C19.118 12.1453 20.558 11.9466 23.438 11.5771C26.318 11.2076 34.958 10.5923 30.638 10.3355",
                "M2.99276 20.2167C-0.980718 20.445 6.96624 20.8871 9.61523 21.2334C12.2642 21.5797 13.5887 21.8912 16.2377 21.9482C18.8867 22.0053 20.2112 21.8899 22.8602 21.5186C25.5092 21.1474 33.4561 20.3522 29.4826 20.0918",
            ),
            viewBox = 33f to 43f,
            ribViewBox = 33f to 23f,
            ribOffsetY = 9.24f,
            capPathData = "M1.09961 1.1001H13.2796",
            capViewBox = 15f to 3f,
        )

        fun fromIndex(index: Int) = when (index.coerceIn(0, 2)) {
            0 -> Type0
            1 -> Type1
            else -> Type2
        }
    }
}

/**
 * A lantern component based on Figma specs.
 */
@Composable
internal fun Lantern(
    style: LanternStyle,
    seed: Int,
    modifier: Modifier = Modifier,
    isLit: Boolean = false,
) {
    val combinedSeed = combineSketchSeed(seed)
    // Figma Spec: Opacity is 1.0 when lit, 0.22 (paper) / 0.45 (ribs) when unlit
    val bodyOpacity = if (isLit) 1.0f else 0.22f
    val ribOpacity = if (isLit) 1.0f else 0.45f

    val lanternGlowColor = LocalKaigiIllustrationColors.current.lanternGlow
    val bodyColor = lanternGlowColor.copy(alpha = bodyOpacity)
    val borderColor = MaterialTheme.colorScheme.primary

    // Parse paths only when style data changes
    val bodyPath = remember(style.pathData) {
        PathParser().parsePathString(style.pathData).toPath()
    }
    val ribPaths = remember(style.ribPathData) {
        style.ribPathData.map { PathParser().parsePathString(it).toPath() }
    }
    val capPath = remember(style.capPathData) {
        PathParser().parsePathString(style.capPathData).toPath()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .wrapContentSize(),
    ) {
        // Hanging cord (16dp height / 1.3dp thickness)
        SketchVerticalDivider(
            seed = combinedSeed + 1,
            thickness = 1.3.dp,
            color = borderColor,
            modifier = Modifier
                .height(style.hangingCord),
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.wrapContentSize(),
        ) {
            // TODO: Glow effect

            Canvas(
                modifier = Modifier.size(style.width, style.height),
            ) {
                // Calculate scale factors based on viewBox
                val scaleX = size.width / style.viewBox.first
                val scaleY = size.height / style.viewBox.second

                // All drawing operations happen within the scaled viewBox coordinate system
                withTransform(
                    {
                        scale(scaleX, scaleY, pivot = Offset.Zero)
                    },
                ) {
                    // 1. Draw Lantern Body
                    drawPath(path = bodyPath, color = bodyColor)
                    drawPath(
                        path = bodyPath,
                        color = borderColor,
                        style = Stroke(
                            width = 2.dp.toPx() / scaleX,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                        ),
                    )

                    // 2. Draw Lantern Ribs
                    withTransform(
                        {
                            translate(0f, style.ribOffsetY)
                        },
                    ) {
                        ribPaths.forEach { ribPath ->
                            drawPath(
                                path = ribPath,
                                color = borderColor.copy(alpha = ribOpacity),
                                style = Stroke(
                                    width = 1.3.dp.toPx() / scaleX,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round,
                                ),
                            )
                        }
                    }

                    // 3. Draw Lantern Cap (Fine-tuned position above the body)
                    withTransform(
                        {
                            val capOffsetX = (style.viewBox.first - style.capViewBox.first) / 2f
                            translate(capOffsetX, -2.2f)
                        },
                    ) {
                        drawPath(
                            path = capPath,
                            color = borderColor,
                            style = Stroke(
                                width = 2.2.dp.toPx() / scaleX,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round,
                            ),
                        )
                    }
                }
            }
        }

        // Tassel (9dp height / 1.4dp thickness)
        SketchVerticalDivider(
            seed = combinedSeed + 2,
            thickness = 1.4.dp,
            color = borderColor,
            modifier = Modifier
                .height(9.dp),
        )
    }
}

@LocalePreviews
@Composable
private fun LanternPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        Row(horizontalArrangement = Arrangement.spacedBy(40.dp)) {
            Lantern(style = LanternStyle.Type0, seed = 1, isLit = true)
            Lantern(style = LanternStyle.Type1, seed = 2, isLit = true)
            Lantern(style = LanternStyle.Type2, seed = 3, isLit = false)
        }
    }
}
