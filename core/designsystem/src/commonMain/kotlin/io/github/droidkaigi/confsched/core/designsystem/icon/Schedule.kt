package io.github.droidkaigi.confsched.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

val KaigiIcons.Default.Schedule: ImageVector
    get() = cachedSchedule ?: ImageVector.Builder(
        name = "KaigiIcons.Default.Schedule",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 144f,
        viewportHeight = 144f,
    )
        .addPath(
            pathData = addPathNodes("M72.6597 38.6853C72.5997 41.5173 72.4737 49.9653 72.2877 55.6353C72.1677 61.3053 69.6357 68.6373 71.6697 72.8313C73.7037 77.0253 80.1177 78.4413 84.5517 80.8413C88.9317 83.3073 95.7717 86.2053 98.0517 87.2553M125.298 72.5853C125.67 78.6273 125.052 85.3413 123.204 91.1973C121.29 97.0533 117.96 103.035 113.958 107.721C109.95 112.341 104.466 116.409 99.0417 119.181C93.5517 121.959 87.2037 123.621 81.2277 124.299C75.2457 125.037 68.9577 124.671 63.1017 123.495C57.3117 122.385 51.3957 120.351 46.1517 117.519C40.9737 114.621 35.8017 110.859 31.7937 106.425C27.7857 101.985 24.2097 96.4413 21.9897 90.8313C19.7757 85.1613 18.5997 78.6873 18.5997 72.5853C18.5397 66.4833 19.8357 60.0093 21.9297 54.3393C24.0237 48.6093 27.3537 42.9393 31.2357 38.3133C35.1237 33.6273 40.1157 29.3133 45.3537 26.2953C50.6517 23.2113 56.8197 20.9313 62.7957 19.9473C68.8377 18.9573 75.4317 19.1433 81.2877 20.3793C87.2037 21.6093 93.1197 24.2013 98.1777 27.4053C103.29 30.5493 107.916 34.8033 111.678 39.3633C115.5 43.9233 118.704 49.2213 120.984 54.7713C123.264 60.3213 124.926 66.5433 125.298 72.5853Z"),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 10.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
        .build()
        .also { cachedSchedule = it }

private var cachedSchedule: ImageVector? = null
