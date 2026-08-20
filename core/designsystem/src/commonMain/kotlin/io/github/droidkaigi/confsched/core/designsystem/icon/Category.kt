package io.github.droidkaigi.confsched.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

val KaigiIcons.Default.Category: ImageVector
    get() = cachedCategory ?: ImageVector.Builder(
        name = "KaigiIcons.Default.Category",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 144f,
        viewportHeight = 144f,
    )
        .addPath(
            pathData = addPathNodes("M79.5568 33.2339C75.8488 28.7999 72.4468 19.8659 68.7388 19.8059C65.0368 19.6859 61.1428 28.4339 57.2548 32.7479C53.3668 37.0619 48.9328 41.0699 45.2908 45.6299C41.6428 50.1839 33.3808 57.8399 35.4448 60.0839C37.5148 62.2739 50.2108 58.8119 57.6208 58.9319C65.0368 59.1119 72.4468 60.7559 79.8568 60.9959C87.3328 61.1819 100.395 62.6399 102.213 60.2099C104.097 57.7799 94.8628 51.0359 91.0348 46.5359C87.2668 42.0419 83.3188 37.7279 79.5568 33.2339Z"),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 10.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
        .addPath(
            pathData = addPathNodes("M60.9628 111.18C62.2408 108.444 63.0268 105.102 62.9668 102.066C62.9068 99.0299 61.8748 95.8079 60.5368 93.1379C59.1988 90.4619 57.1948 87.9119 54.8908 85.9079C52.6408 83.8439 49.8448 81.8339 46.8688 80.8619C43.8328 79.8899 40.0648 79.2839 36.9688 79.9499C33.8668 80.5619 30.4048 82.5059 28.1548 84.7499C25.9708 86.9399 24.4528 90.2819 23.4808 93.1379C22.5088 96.0539 22.3828 99.0899 22.3828 102.066C22.3828 105.042 22.5688 108.084 23.5408 110.934C24.5128 113.79 26.0908 117.072 28.3408 119.262C30.5848 121.386 33.9268 123.21 37.0288 123.876C40.1248 124.548 43.8328 124.122 46.8688 123.27C49.8448 122.358 52.8208 120.594 55.1908 118.59C57.5008 116.526 59.6848 113.916 60.9628 111.18Z"),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 10.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
        .addPath(
            pathData = addPathNodes("M99.7228 80.0159C92.8588 80.0759 82.5928 77.3399 79.1908 80.9219C75.7888 84.5099 79.3708 94.5959 79.3708 101.46C79.3708 108.324 75.7888 118.41 79.1308 122.052C82.5328 125.64 92.8588 123.21 99.7228 123.21C106.587 123.27 117.219 125.76 120.441 122.178C123.603 118.53 118.737 108.384 118.803 101.46C118.803 94.5359 123.723 84.2039 120.561 80.6219C117.405 77.0339 106.587 79.9499 99.7228 80.0159Z"),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 10.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
        .build()
        .also { cachedCategory = it }

private var cachedCategory: ImageVector? = null
