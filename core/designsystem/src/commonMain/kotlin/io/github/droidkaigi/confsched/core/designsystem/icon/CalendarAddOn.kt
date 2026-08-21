package io.github.droidkaigi.confsched.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

val KaigiIcons.Default.CalendarAddOn: ImageVector
    get() = cachedCalendarAddOn ?: ImageVector.Builder(
        name = "KaigiIcons.Default.CalendarAddOn",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 144f,
        viewportHeight = 144f,
    )
        .addPath(
            pathData = addPathNodes("M25.1903 61.368C28.2983 61.428 37.6823 62.13 43.9643 61.896C50.2403 61.662 56.4623 60.252 62.7383 59.958C69.0143 59.724 75.2363 60.018 81.5123 60.252C87.7943 60.432 94.0103 61.194 100.292 61.308C106.568 61.428 115.958 60.9 119.066 60.84M49.5983 21C49.7123 25.11 50.2403 41.538 50.3603 45.642M94.5983 21C94.5983 25.11 94.4843 41.538 94.4843 45.642M72.2423 74.394C72.1883 77.208 72.0083 85.716 71.9483 91.41C71.8943 97.098 71.8943 105.606 71.8943 108.426M55.1123 91.056C57.9263 91.176 66.4343 91.524 72.1283 91.584C77.8163 91.644 86.3243 91.47 89.1443 91.47M24.9503 36.018C28.2983 33.204 37.6823 36.198 43.9643 36.432C50.2403 36.666 56.4623 37.254 62.7383 37.488C69.0143 37.722 75.2363 37.896 81.5123 37.842C87.7943 37.782 94.0103 37.488 100.292 37.194C106.568 36.96 115.778 33.498 119.126 36.198C122.408 38.898 119.888 47.694 120.062 53.388C120.296 59.082 120.356 64.83 120.296 70.524C120.296 76.212 120.122 81.96 119.948 87.654C119.768 93.348 119.42 99.096 119.3 104.784C119.126 110.478 122.174 119.046 119.006 121.86C115.838 124.62 106.508 121.512 100.292 121.452C94.0703 121.392 87.7943 121.512 81.5123 121.566C75.2363 121.566 69.0143 121.626 62.7383 121.626C56.4623 121.566 50.1803 121.452 43.9643 121.512C37.7423 121.512 28.4123 124.56 25.3043 121.8C22.1963 118.986 25.4783 110.478 25.4243 104.784C25.3643 99.096 25.0103 93.348 24.8363 87.654C24.6023 81.96 24.3083 76.212 24.1883 70.524C24.0143 64.83 23.7803 59.136 23.8943 53.388C24.0143 47.64 21.6083 38.838 24.9503 36.018Z"),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 10.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
        .build()
        .also { cachedCalendarAddOn = it }

private var cachedCalendarAddOn: ImageVector? = null
