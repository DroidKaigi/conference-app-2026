package io.github.droidkaigi.confsched.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/** The 文A toggle with English active: the heavier stroke marks the language now shown. */
val KaigiIcons.Default.LanguageToggleEnglish: ImageVector
    get() = cachedLanguageToggleEnglish ?: ImageVector.Builder(
        name = "KaigiIcons.Default.LanguageToggleEnglish",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 144f,
        viewportHeight = 144f,
    )
        .addPath(
            pathData = addPathNodes("M34.3077 11.2045C34.3077 11.4814 34.3364 12.9923 34.6101 16.2766C35.3153 20.6155 36.5186 25.1675 37.888 28.8579C38.5283 30.4923 39.0578 31.643 39.6033 33.2507M10.8203 46.0104C12.9635 45.8261 18.3311 44.8299 26.2049 44.05C30.7189 43.6028 39.6528 42.1596 48.2329 40.4641C49.6225 40.2056 50.5215 40.0887 52.9982 39.3597C55.4752 38.6306 59.5023 37.293 64.5235 35.9149M58.6174 43.4253C58.6076 45.8866 57.3435 51.0734 54.2854 56.2074C49.6689 63.957 44.5169 71.0044 40.6497 75.1555C36.7622 79.3281 29.1999 83.8841 20.5515 88.6275C14.5873 90.6157 10.7715 90.9063 7.9171 91.0026C6.536 91.099 5.2918 91.2911 3.931 91.4894M29.7717 60.3281C30.1987 61.0762 36.7255 68.6498 41.1943 74.0128C45.1415 78.7501 48.9395 81.1503 51.9282 82.1956C55.2277 82.7695 58.3161 82.8316 60.4611 83.1226C61.448 83.3522 62.2274 83.7467 64.0383 84.1531"),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 7.8609f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
        .addPath(
            pathData = addPathNodes("M65.9578 129.8866C65.9578 128.5292 66.171 126.5293 71.2214 116.3162C75.7168 107.2269 85.5154 90.9201 90.7169 81.716C99.0269 67.0111 100.1849 59.6168 100.8675 58.7985C101.9328 57.5214 108.0911 65.853 116.5937 77.0796C123.9469 86.789 128.6263 96.0474 132.0063 102.7468C135.152 108.3732 136.5812 112.76 136.8215 120.1025C136.8886 121.6854 137.0224 122.435 137.1602 123.5695M82.8231 97.8896C83.044 97.8896 91.3113 97.724 106.3366 97.1542C113.2081 96.7499 118.5506 96.1115 122.7028 95.783C126.8554 95.454 129.6553 95.454 132.5407 95.454"),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 13.678f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
        .build()
        .also { cachedLanguageToggleEnglish = it }

private var cachedLanguageToggleEnglish: ImageVector? = null
