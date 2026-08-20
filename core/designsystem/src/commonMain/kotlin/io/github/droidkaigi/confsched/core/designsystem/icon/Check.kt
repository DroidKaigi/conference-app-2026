package io.github.droidkaigi.confsched.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

val KaigiIcons.Default.Check: ImageVector
    get() = cachedCheck ?: ImageVector.Builder(
        name = "KaigiIcons.Default.Check",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 144f,
        viewportHeight = 144f,
    )
        .addPath(
            pathData = addPathNodes("M24 73.424C26.718 76.28 34.812 84.968 40.254 90.674C45.696 96.308 51.132 107.39 56.574 107.324C62.016 107.324 67.254 96.044 72.762 90.338C78.336 84.704 84.51 79.526 89.682 73.49C94.86 67.448 98.838 60.356 103.878 54.182C108.924 48.08 117.348 39.59 120 36.668"),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 10.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
        .build()
        .also { cachedCheck = it }

private var cachedCheck: ImageVector? = null
