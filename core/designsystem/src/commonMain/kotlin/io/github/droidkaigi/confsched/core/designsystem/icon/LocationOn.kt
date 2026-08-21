package io.github.droidkaigi.confsched.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

val KaigiIcons.Default.LocationOn: ImageVector
    get() = cachedLocationOn ?: ImageVector.Builder(
        name = "KaigiIcons.Default.LocationOn",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 144f,
        viewportHeight = 144f,
    )
        .addPath(
            pathData = addPathNodes("M42.3252 73.2999C44.1072 76.2939 44.1072 73.1259 48.9372 81.5859C53.7732 90.0939 64.0093 124.264 71.3113 124.204C78.6193 124.09 87.8773 89.5179 92.7673 81.0639C97.5973 72.5499 98.2332 76.1739 100.357 73.2999C102.433 70.4799 104.155 67.2639 105.307 63.9279C106.459 60.5919 107.089 56.8479 107.089 53.2839C107.089 49.7199 106.399 45.8619 105.193 42.4719C103.981 39.1359 102.085 35.7399 99.8413 32.9199C97.5973 30.1599 94.7232 27.6339 91.7352 25.6779C88.7412 23.7219 85.3453 22.1679 81.8953 21.1899C78.5053 20.2119 74.8213 19.7499 71.3113 19.8099C67.8073 19.8099 64.1232 20.3859 60.7872 21.4779C57.4513 22.5099 54.1152 24.1779 51.2412 26.1939C48.3612 28.2039 45.7152 30.7959 43.6452 33.6159C41.5212 36.3759 39.8532 39.6519 38.7012 42.9879C37.6092 46.2699 37.0332 49.8879 36.9192 53.3439C36.8592 56.7939 37.3212 60.3579 38.2392 63.6939C39.1032 67.0299 40.5432 70.3119 42.3252 73.2999Z"),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 10.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
        .build()
        .also { cachedLocationOn = it }

private var cachedLocationOn: ImageVector? = null
