package io.github.droidkaigi.confsched.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

val KaigiIcons.Default.ChevronRight: ImageVector
    get() = cachedChevronRight ?: ImageVector.Builder(
        name = "KaigiIcons.Default.ChevronRight",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 144f,
        viewportHeight = 144f,
    )
        .addPath(
            pathData = addPathNodes("M49.3242 25.1953C51.5982 27.9793 58.1682 36.6253 63.1542 41.8693C68.0802 47.0473 73.8282 51.4693 79.0722 56.5213C84.3102 61.5133 94.6722 66.8173 94.6722 71.9953C94.6722 77.1733 84.3762 82.5433 79.1982 87.5953C74.0202 92.7133 68.4642 97.2553 63.4722 102.499C58.5462 107.683 51.6582 116.077 49.3242 118.795"),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 10.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
        .build()
        .also { cachedChevronRight = it }

private var cachedChevronRight: ImageVector? = null
