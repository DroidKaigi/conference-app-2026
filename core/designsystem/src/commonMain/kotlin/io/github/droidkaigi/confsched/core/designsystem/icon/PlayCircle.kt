package io.github.droidkaigi.confsched.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

val KaigiIcons.Default.PlayCircle: ImageVector
    get() = cachedPlayCircle ?: ImageVector.Builder(
        name = "KaigiIcons.Default.PlayCircle",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 144f,
        viewportHeight = 144f,
    )
        .addPath(
            pathData = addPathNodes("M122.86 90.2472C124.762 84.4812 125.554 77.9832 125.374 71.9772C125.188 65.9652 123.964 59.6472 121.816 54.0672C119.728 48.4272 116.602 42.9072 112.738 38.3112C108.94 33.7092 104.032 29.5392 98.8241 26.5992C93.6701 23.5932 87.5981 21.5112 81.7121 20.5272C75.8261 19.5492 69.4481 19.6692 63.5621 20.6532C57.7361 21.6912 51.7301 23.7192 46.4561 26.5992C41.1821 29.4192 35.9681 33.2832 31.8581 37.7592C27.8141 42.2352 24.1361 47.8152 21.9281 53.5152C19.6601 59.2212 18.4301 65.8452 18.6161 71.9772C18.7361 78.1092 20.3321 84.6072 22.7261 90.1272C25.1141 95.7072 28.8581 100.915 32.9621 105.271C37.0121 109.627 41.9801 113.305 47.0681 116.245C52.2161 119.131 57.9221 121.399 63.6881 122.683C69.4481 123.973 75.8261 124.525 81.7721 123.853C87.7241 123.115 94.1621 121.339 99.4961 118.513C104.89 115.633 110.104 111.403 113.968 106.681C117.892 101.959 120.958 96.0132 122.86 90.2472Z"),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 10.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
        .build()
        .also { cachedPlayCircle = it }

private var cachedPlayCircle: ImageVector? = null
