package io.github.droidkaigi.confsched.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/** The 文A toggle with 日本語 active: the heavier stroke marks the language now shown. */
val KaigiIcons.Default.LanguageToggleJapanese: ImageVector
    get() = cachedLanguageToggleJapanese ?: ImageVector.Builder(
        name = "KaigiIcons.Default.LanguageToggleJapanese",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 144f,
        viewportHeight = 144f,
    )
        .addPath(
            pathData = addPathNodes("M37.2166 14.1131C37.2166 14.39 37.2453 15.9008 37.519 19.1852C38.2243 23.5241 39.4276 28.0761 40.7969 31.7664C41.4372 33.4009 41.9667 34.5515 42.5122 36.1593M13.7292 48.9189C15.8724 48.7347 21.24 47.7384 29.1139 46.9585C33.6278 46.5114 42.5617 45.0682 51.1419 43.3726C52.5313 43.1141 53.4306 42.9972 55.9072 42.2682C58.3837 41.5392 62.4113 40.2016 67.4325 38.8234M61.5264 46.3338C61.5166 48.7951 60.2525 53.9819 57.1939 59.1162C52.578 66.8655 47.4258 73.9129 43.5586 78.064C39.6712 82.2366 32.1089 86.7926 23.4604 91.536C17.4963 93.5242 13.6805 93.8148 10.826 93.9111C9.4449 94.0075 8.2008 94.1996 6.8399 94.3979M32.6806 63.2366C33.1076 63.9847 39.6344 71.5583 44.1032 76.9218C48.0504 81.6586 51.8483 84.0588 54.8372 85.1041C58.1367 85.6781 61.2246 85.7401 63.3701 86.0311C64.357 86.2607 65.1364 86.6552 66.9473 87.0621"),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 13.678f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
        .addPath(
            pathData = addPathNodes("M68.8668 132.7951C68.8668 131.4377 69.0795 129.4378 74.1304 119.2247C78.6259 110.1354 88.4244 93.8286 93.6254 84.625C101.9359 69.9196 103.094 62.5253 103.7765 61.707C104.8418 60.43 111.0001 68.7615 119.5027 79.9881C126.8559 89.6975 131.5348 98.9559 134.9153 105.6558C138.0605 111.2817 139.4902 115.669 139.7305 123.011C139.7971 124.5939 139.9314 125.3435 140.0692 126.478M85.7316 100.7981C85.953 100.7981 94.2204 100.6325 109.2456 100.0627C116.1171 99.6584 121.4596 99.02 125.6118 98.6915C129.7639 98.3625 132.5643 98.3625 135.4497 98.3625"),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 7.8609f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
        .build()
        .also { cachedLanguageToggleJapanese = it }

private var cachedLanguageToggleJapanese: ImageVector? = null
