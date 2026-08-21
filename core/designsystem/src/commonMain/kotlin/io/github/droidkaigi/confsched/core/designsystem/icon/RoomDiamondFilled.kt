package io.github.droidkaigi.confsched.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

val KaigiIcons.Default.RoomDiamondFilled: ImageVector
    get() = cachedRoomDiamondFilled ?: ImageVector.Builder(
        name = "KaigiIcons.Default.RoomDiamondFilled",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 144f,
        viewportHeight = 144f,
    )
        .addPath(
            pathData = addPathNodes("M71.7728 39C75.4508 39 79.2428 46.302 82.9268 49.926C86.6588 53.49 90.3968 57.054 94.0148 60.732C97.6388 64.41 104.827 68.376 104.659 71.994C104.485 75.618 96.7928 78.732 93.0008 82.296C89.2088 85.806 85.4708 89.316 81.9608 93.108C78.3968 96.9 75.4508 104.652 71.7728 104.994C68.0948 105.276 63.4568 98.598 59.7788 94.974C56.0408 91.35 52.9868 87.108 49.5908 83.256C46.1948 79.41 39.1748 75.618 39.3428 71.994C39.4568 68.376 46.9868 65.376 50.5508 61.698C54.1148 58.014 57.1748 53.772 60.7388 50.034C64.2488 46.242 68.0948 39.054 71.7728 39Z"),
            fill = SolidColor(Color.Black),
        )
        .addPath(
            pathData = addPathNodes("M82.9268 49.926C79.2428 46.302 75.4508 39 71.7728 39C68.0948 39.054 64.2488 46.242 60.7388 50.034C57.1748 53.772 54.1148 58.014 50.5508 61.698C46.9868 65.376 39.4568 68.376 39.3428 71.994C39.1748 75.618 46.1948 79.41 49.5908 83.256C52.9868 87.108 56.0408 91.35 59.7788 94.974C63.4568 98.598 68.0948 105.276 71.7728 104.994C75.4508 104.652 78.3968 96.9 81.9608 93.108C85.4708 89.316 89.2088 85.806 93.0008 82.296C96.7928 78.732 104.485 75.618 104.659 71.994C104.827 68.376 97.6388 64.41 94.0148 60.732C90.4853 57.1439 86.8415 53.6644 83.2006 50.1874L82.9268 49.926Z"),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 10.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
        .build()
        .also { cachedRoomDiamondFilled = it }

private var cachedRoomDiamondFilled: ImageVector? = null
