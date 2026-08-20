package io.github.droidkaigi.confsched.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

val KaigiIcons.Default.Map: ImageVector
    get() = cachedMap ?: ImageVector.Builder(
        name = "KaigiIcons.Default.Map",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 144f,
        viewportHeight = 144f,
    )
        .addPath(
            pathData = addPathNodes("M54.5095 23.7302C54.4495 27.1682 53.8195 37.3622 54.0475 44.2322C54.2755 51.0482 55.7695 57.8642 55.9375 64.6802C56.1115 71.4962 55.3075 78.3122 55.1395 85.1822C54.9055 91.9982 54.8515 102.192 54.7915 105.63M88.9855 38.6222C89.2735 42.0602 90.3595 52.2542 90.5335 59.1242C90.7015 65.9402 90.4195 72.7562 89.9575 79.5722C89.5615 86.3882 88.1815 93.2042 88.0675 100.074C87.9535 106.89 88.9855 117.084 89.2135 120.522M21.4015 38.4482C24.3835 33.8102 32.6875 33.5222 38.2435 31.1162C43.8535 28.7102 49.3555 23.7902 54.9055 24.0722C60.4615 24.3002 65.7355 30.0902 71.4595 32.5502C77.1895 35.0702 83.4895 39.2522 89.2735 39.0782C95.0575 38.9102 100.5 34.1522 106.056 31.5782C111.612 28.9982 119.682 21.4982 122.604 23.5562C125.526 25.6802 123.522 37.3622 123.696 44.2322C123.864 51.0482 123.696 57.8642 123.522 64.6802C123.408 71.4962 123.066 78.3122 122.832 85.1822C122.664 91.9982 125.298 100.992 122.436 105.576C119.568 110.154 111.21 110.154 105.708 112.62C100.152 115.14 94.8295 120.348 89.2735 120.348C83.7175 120.408 77.9335 115.254 72.2035 112.788C66.4795 110.328 60.5755 105.516 54.9055 105.516C49.2355 105.456 43.6255 110.154 38.1295 112.674C32.5735 115.14 24.4975 122.526 21.8035 120.408C19.0555 118.29 21.8035 106.836 21.6355 100.074C21.5155 93.2582 21.1195 86.3882 20.8855 79.5722C20.7175 72.7562 20.3755 65.9402 20.4295 59.0702C20.5435 52.2542 18.4255 43.0862 21.4015 38.4482Z"),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 10.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
        .build()
        .also { cachedMap = it }

private var cachedMap: ImageVector? = null
