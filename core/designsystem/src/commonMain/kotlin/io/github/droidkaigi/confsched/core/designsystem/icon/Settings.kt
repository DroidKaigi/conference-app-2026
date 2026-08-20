package io.github.droidkaigi.confsched.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

val KaigiIcons.Default.Settings: ImageVector
    get() = cachedSettings ?: ImageVector.Builder(
        name = "KaigiIcons.Default.Settings",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 144f,
        viewportHeight = 144f,
    )
        .addPath(
            pathData = addPathNodes("M123.024 61.7307C119.676 58.1607 106.92 61.5687 102.69 59.7567C98.3999 57.9387 96.9179 55.3587 97.4699 50.7387C98.0699 46.1247 107.58 36.8367 106.098 32.0547C104.67 27.2727 93.618 20.9487 88.728 22.0467C83.838 23.2047 80.5379 36.0627 76.854 38.8107C73.1759 41.6187 70.2599 41.6187 66.5759 38.7567C62.8379 35.9547 59.5439 22.9827 54.7079 21.8847C49.8119 20.7867 38.7119 27.2727 37.3379 32.1087C35.9639 36.8907 45.8579 45.9027 46.5179 50.6847C47.1179 55.4127 45.3059 58.5987 41.0759 60.5787C36.8399 62.5047 24.4739 59.0427 21.1199 62.4447C17.7659 65.7987 17.7659 77.4507 21.0659 80.8587C24.3659 84.2667 36.7859 80.9727 40.9619 82.9467C45.1439 84.8727 46.7879 87.7887 46.1279 92.5107C45.4139 97.2927 35.2499 106.419 36.6779 111.369C38.1599 116.259 49.8119 123.183 54.8699 122.139C59.9279 121.041 63.2219 107.847 66.9059 104.937C70.5359 102.021 73.0619 102.021 76.6919 104.661C80.3759 107.301 84.1139 119.775 88.8359 120.765C93.5639 121.809 103.896 115.599 105.108 110.871C106.374 106.197 96.756 97.4067 96.2579 92.6787C95.766 87.9507 97.854 84.3807 102.252 82.5087C106.644 80.5887 119.232 84.7107 122.694 81.2427C126.162 77.7807 126.324 65.3067 123.024 61.7307Z"),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 10.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
        .build()
        .also { cachedSettings = it }

private var cachedSettings: ImageVector? = null
