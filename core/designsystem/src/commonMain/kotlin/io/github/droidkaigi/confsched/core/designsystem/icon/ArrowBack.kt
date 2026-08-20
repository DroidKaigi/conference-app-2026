package io.github.droidkaigi.confsched.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

val KaigiIcons.Default.ArrowBack: ImageVector
    get() = cachedArrowBack ?: ImageVector.Builder(
        name = "KaigiIcons.Default.ArrowBack",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 144f,
        viewportHeight = 144f,
    )
        .addPath(
            pathData = addPathNodes("M124.205 72.2635C120.707 72.2635 110.279 71.9515 103.349 72.0775C96.4247 72.2035 89.4287 72.9535 82.4987 72.9595C75.5687 72.9595 68.5727 72.3895 61.6427 72.0115C54.7127 71.6395 47.7227 70.7575 40.7927 70.7575C33.8627 70.8175 23.4347 72.0775 19.9367 72.3295M61.181 31.0195C58.871 33.3295 51.941 40.1275 47.459 44.7475C42.905 49.4275 38.747 54.3775 34.127 58.9975C29.507 63.5515 19.937 67.7755 19.805 72.2635C19.739 76.7515 28.847 81.7015 33.599 86.1235C38.351 90.4795 43.763 94.2415 48.443 98.7235C53.129 103.212 59.531 110.604 61.709 112.98"),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 10.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
        .build()
        .also { cachedArrowBack = it }

private var cachedArrowBack: ImageVector? = null
