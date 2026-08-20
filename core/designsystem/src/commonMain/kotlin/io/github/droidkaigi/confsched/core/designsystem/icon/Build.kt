package io.github.droidkaigi.confsched.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

val KaigiIcons.Default.Build: ImageVector
    get() = cachedBuild ?: ImageVector.Builder(
        name = "KaigiIcons.Default.Build",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    )
        .addPath(
            pathData = addPathNodes("M4.41689 18.9741H4.46689M17.5949 3.55313C17.5949 3.55313 14.7059 3.06513 13.6099 3.56713C12.5209 4.06613 11.3869 5.39213 11.0289 6.53413C10.6729 7.67013 11.4509 10.3951 11.4509 10.3951C11.4509 10.3951 9.83389 12.1581 9.01289 13.0041C8.20789 13.8321 7.37789 14.6061 6.57289 15.4201C5.76789 16.2341 4.63789 16.9691 4.18489 17.8871C3.78289 18.7001 3.41589 20.2191 3.78589 20.5541C4.15589 20.8891 5.61989 20.3561 6.40889 19.8881C7.29789 19.3621 7.95089 18.1961 8.74889 17.3751C9.54589 16.5541 10.3739 15.7611 11.1939 14.9621C12.0149 14.1631 13.6719 12.5801 13.6719 12.5801C13.6719 12.5801 16.4029 13.1251 17.4889 12.6791C18.5919 12.2261 19.7999 10.9651 20.2219 9.83613C20.6429 8.70813 20.0219 5.90813 20.0219 5.90813C20.0219 5.90813 18.7809 7.03313 18.1949 7.60813C17.6239 8.16913 16.5489 9.31213 16.5489 9.31213C16.5489 9.31213 15.7839 9.12013 15.3909 9.02813C14.9839 8.93313 14.1459 8.75313 14.1459 8.75313C14.1459 8.75313 14.0499 8.00713 13.9699 7.62513C13.8849 7.22113 13.6449 6.39113 13.6449 6.39113C13.6449 6.39113 14.9569 5.48113 15.6119 5.01213C16.2739 4.53613 17.5949 3.55313 17.5949 3.55313Z"),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
        .build()
        .also { cachedBuild = it }

private var cachedBuild: ImageVector? = null
