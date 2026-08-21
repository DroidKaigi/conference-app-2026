package io.github.droidkaigi.confsched.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

val KaigiIcons.Default.Translate: ImageVector
    get() = cachedTranslate ?: ImageVector.Builder(
        name = "KaigiIcons.Default.Translate",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 144f,
        viewportHeight = 144f,
    )
        .addPath(
            pathData = addPathNodes("M38.3727 19.8047C39.8187 22.3487 45.4767 32.6867 46.9227 35.2307M23.6367 37.6007C27.1047 37.5407 37.5027 37.0247 44.4387 37.1927C51.3687 37.3127 61.7727 38.1767 65.2347 38.3507M59.6307 53.0807C57.1467 55.5107 49.6347 62.7347 44.7867 67.7027C39.9867 72.7247 33.0567 80.5247 30.7467 83.0687M30.5127 52.9667C32.8227 55.5647 39.5847 63.4847 44.4387 68.4527C49.2927 73.4807 57.0327 80.4707 59.5167 82.8947M72.6327 124.205C73.8447 121.085 77.4267 111.725 79.9107 105.545C82.3947 99.3047 84.7047 93.0647 87.4227 86.9987C90.1947 80.9327 93.6027 69.1427 96.4347 69.2027C99.2667 69.2627 101.865 81.2747 104.583 87.3467C107.355 93.4127 110.247 99.4187 112.899 105.545C115.503 111.665 119.085 121.025 120.357 124.145M81.4167 103.637C83.9007 103.349 91.4067 102.251 96.4347 102.191C101.463 102.191 108.975 103.289 111.459 103.523"),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 10.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
        .build()
        .also { cachedTranslate = it }

private var cachedTranslate: ImageVector? = null
