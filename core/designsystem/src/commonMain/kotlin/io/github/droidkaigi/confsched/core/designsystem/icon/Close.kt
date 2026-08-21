package io.github.droidkaigi.confsched.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

val KaigiIcons.Default.Close: ImageVector
    get() = cachedClose ?: ImageVector.Builder(
        name = "KaigiIcons.Default.Close",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 144f,
        viewportHeight = 144f,
    )
        .addPath(
            pathData = addPathNodes("M27.5977 27.7383C30.2077 30.0243 38.4337 36.5643 43.3957 41.4003C48.3577 46.2423 52.8757 51.5883 57.5077 56.6823C62.1457 61.8423 66.5317 67.3203 71.3617 72.2943C76.1977 77.2503 81.5437 81.7683 86.5777 86.5383C91.5397 91.3683 96.5677 96.1443 101.464 101.112C106.3 106.008 113.356 113.718 115.708 116.262M116.398 28.0863C113.98 30.5583 106.756 38.1063 101.686 42.7683C96.5497 47.4963 90.7477 51.4923 85.6717 56.2803C80.6557 61.0023 76.0357 66.1743 71.3497 71.3583C66.6097 76.4823 62.3917 81.9843 57.5977 86.9943C52.8037 92.0583 47.6077 96.6723 42.5917 101.514C37.5697 106.296 30.0757 113.496 27.5977 115.914"),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 10.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
        .build()
        .also { cachedClose = it }

private var cachedClose: ImageVector? = null
