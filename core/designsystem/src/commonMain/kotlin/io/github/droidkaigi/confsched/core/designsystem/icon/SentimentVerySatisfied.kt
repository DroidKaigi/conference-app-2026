package io.github.droidkaigi.confsched.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

val KaigiIcons.Default.SentimentVerySatisfied: ImageVector
    get() = cachedSentimentVerySatisfied ?: ImageVector.Builder(
        name = "KaigiIcons.Default.SentimentVerySatisfied",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 144f,
        viewportHeight = 144f,
    )
        .addPath(
            pathData = addPathNodes("M44.8232 63.7001C46.2152 62.0141 50.1332 53.6921 52.9652 53.5721C55.8032 53.5121 60.2612 61.5941 61.7132 63.2201M81.9752 63.5201C83.3612 61.7741 87.5192 53.1461 90.3572 53.1461C93.1892 53.0861 97.6532 61.5281 99.0992 63.2201M44.6432 83.1221C46.6952 86.8001 52.4252 100.792 56.8892 105.37C61.4072 109.954 66.7772 110.8 71.6612 110.74C76.5452 110.74 81.4292 109.534 86.1932 105.13C90.8972 100.666 97.8332 87.5801 100.125 84.0221M123.525 72.5081C123.645 78.4181 122.679 84.7481 120.747 90.3581C118.761 95.9621 115.563 101.572 111.765 106.156C107.907 110.74 103.017 114.838 97.8332 117.856C92.6492 120.868 86.6792 123.16 80.7692 124.306C74.9192 125.512 68.4032 125.758 62.4332 124.852C56.4632 123.946 50.1332 121.894 44.8892 118.882C39.6392 115.804 34.6352 111.466 30.8972 106.702C27.2192 101.932 24.3212 96.0881 22.6352 90.3581C20.8832 84.6881 20.2832 78.3581 20.5232 72.5081C20.7032 66.6581 21.9092 60.6281 23.7812 55.0781C25.7072 49.5281 28.4852 43.9841 32.0432 39.2201C35.5412 34.4561 40.0592 29.8721 45.1292 26.5541C50.1932 23.2361 56.3432 20.5841 62.3132 19.4381C68.2832 18.2321 75.0392 18.3521 81.0092 19.6181C86.9792 20.8241 93.0092 23.5361 98.0732 26.7941C103.143 30.0521 107.721 34.4561 111.405 39.1601C115.083 43.8041 118.155 49.2881 120.147 54.8381C122.199 60.3881 123.465 66.5981 123.525 72.5081Z"),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 10.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
        .build()
        .also { cachedSentimentVerySatisfied = it }

private var cachedSentimentVerySatisfied: ImageVector? = null
