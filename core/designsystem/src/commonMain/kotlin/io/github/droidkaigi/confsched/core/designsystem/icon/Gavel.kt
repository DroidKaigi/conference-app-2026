package io.github.droidkaigi.confsched.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

val KaigiIcons.Default.Gavel: ImageVector
    get() = cachedGavel ?: ImageVector.Builder(
        name = "KaigiIcons.Default.Gavel",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 144f,
        viewportHeight = 144f,
    )
        .addPath(
            pathData = addPathNodes("M75.0614 64.313C72.8294 66.383 66.1334 72.749 61.6214 76.829C57.0494 80.963 52.0454 84.623 47.7434 88.967C43.4414 93.317 37.7714 100.589 35.7554 102.929M25.4414 123.044C28.7654 123.212 38.6774 124.304 45.3254 124.19C51.9134 124.07 58.5614 122.756 65.1494 122.468C71.7914 122.18 81.7034 122.468 85.0274 122.468M79.2974 19.7956C83.2514 19.9156 89.1554 27.5356 93.5054 31.9996C97.8614 36.4696 101.245 41.9116 105.367 46.4956C109.549 51.0256 118.027 55.5496 118.543 59.4436C119.059 63.2836 111.841 66.2056 108.463 69.6436C105.079 73.0816 102.157 80.4736 98.2034 80.0716C94.2554 79.6696 89.2094 71.4196 84.7394 67.1236C80.2754 62.8276 75.6314 58.5856 71.3354 54.1756C67.0394 49.7056 59.1914 44.2636 58.9574 40.4236C58.7294 36.5836 66.5234 34.5796 69.9014 31.0876C73.3394 27.6496 75.4034 19.6276 79.2974 19.7956Z"),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 10.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
        .build()
        .also { cachedGavel = it }

private var cachedGavel: ImageVector? = null
