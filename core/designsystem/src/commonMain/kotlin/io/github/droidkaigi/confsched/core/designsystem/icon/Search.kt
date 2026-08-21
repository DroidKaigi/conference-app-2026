package io.github.droidkaigi.confsched.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

val KaigiIcons.Default.Search: ImageVector
    get() = cachedSearch ?: ImageVector.Builder(
        name = "KaigiIcons.Default.Search",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 144f,
        viewportHeight = 144f,
    )
        .addPath(
            pathData = addPathNodes("M85.7498 85.6353C88.0418 87.6573 95.1878 93.5853 99.3638 98.1033C103.546 102.549 106.846 107.943 110.956 112.527C115.138 117.105 121.942 123.309 124.168 125.397M95.3198 58.2753C95.7278 63.7353 95.3198 70.0653 93.2318 75.3213C91.1438 80.5113 87.1658 85.9713 82.7198 89.4753C78.2738 92.9793 72.0698 95.3373 66.5438 96.3513C60.9518 97.2933 54.7538 96.6873 49.4258 95.2713C44.0378 93.8553 38.5778 91.2273 34.2638 87.8613C29.9498 84.4233 25.9718 79.7073 23.5478 74.7873C21.1898 69.8673 19.8398 63.8013 19.8398 58.2753C19.8398 52.7493 21.2558 46.8153 23.6138 41.7633C25.9058 36.7053 29.4098 31.5873 33.6578 27.8793C37.8338 24.1713 43.4978 20.8053 49.0238 19.5213C54.4838 18.1773 61.2218 18.3093 66.6098 19.8573C72.0038 21.4113 77.3258 25.0473 81.3698 28.8213C85.4138 32.5293 88.5158 37.4493 90.8078 42.3693C93.1658 47.2893 94.9178 52.8153 95.3198 58.2753Z"),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 10.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
        .build()
        .also { cachedSearch = it }

private var cachedSearch: ImageVector? = null
