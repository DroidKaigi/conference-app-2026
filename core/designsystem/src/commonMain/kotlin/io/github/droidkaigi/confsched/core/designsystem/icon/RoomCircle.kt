package io.github.droidkaigi.confsched.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

val KaigiIcons.Default.RoomCircle: ImageVector
    get() = cachedRoomCircle ?: ImageVector.Builder(
        name = "KaigiIcons.Default.RoomCircle",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 144f,
        viewportHeight = 144f,
    )
        .addPath(
            pathData = addPathNodes("M104.989 72.5993C105.127 77.3213 103.813 82.8053 101.521 86.9033C99.2289 91.0013 95.1969 94.6133 91.3089 97.1153C87.4869 99.6173 82.9029 101.003 78.4629 101.975C74.0829 102.881 69.4329 103.295 64.9149 102.671C60.4029 102.047 55.3329 100.727 51.3729 98.1593C47.4849 95.5853 43.5969 91.4213 41.5149 87.1853C39.4989 82.8773 38.7369 77.3213 39.0789 72.5993C39.3609 67.8773 41.2329 63.0113 43.4589 58.9193C45.6789 54.8873 48.8049 51.0713 52.3449 48.2213C55.8909 45.3713 60.3309 42.9413 64.7769 41.8313C69.2229 40.7873 74.3649 40.7873 78.8049 41.7593C83.3229 42.7373 87.8349 45.0293 91.5189 47.8733C95.1969 50.6513 98.6709 54.4733 100.891 58.5713C103.183 62.6693 104.923 67.8773 104.989 72.5993Z"),
            fill = SolidColor(Color.Black),
        )
        .addPath(
            pathData = addPathNodes("M101.521 86.9033C103.813 82.8053 105.127 77.3213 104.989 72.5993C104.923 67.8773 103.183 62.6693 100.891 58.5713C98.6709 54.4733 95.1969 50.6513 91.5189 47.8733C87.8349 45.0293 83.3229 42.7373 78.8049 41.7593C74.3649 40.7873 69.2229 40.7873 64.7769 41.8313C60.3309 42.9413 55.8909 45.3713 52.3449 48.2213C48.8049 51.0713 45.6789 54.8873 43.4589 58.9193C41.2329 63.0113 39.3609 67.8773 39.0789 72.5993C38.7369 77.3213 39.4989 82.8773 41.5149 87.1853C43.5969 91.4213 47.4849 95.5853 51.3729 98.1593C55.3329 100.727 60.4029 102.047 64.9149 102.671C69.4329 103.295 74.0829 102.881 78.4629 101.975C82.9029 101.003 87.4869 99.6173 91.3089 97.1153C95.1969 94.6133 99.2289 91.0013 101.521 86.9033Z"),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 10.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
        .build()
        .also { cachedRoomCircle = it }

private var cachedRoomCircle: ImageVector? = null
