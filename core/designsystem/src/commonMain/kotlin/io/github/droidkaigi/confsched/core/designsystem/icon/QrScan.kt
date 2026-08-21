package io.github.droidkaigi.confsched.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

val KaigiIcons.Default.QrScan: ImageVector
    get() = cachedQrScan ?: ImageVector.Builder(
        name = "KaigiIcons.Default.QrScan",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 144f,
        viewportHeight = 144f,
    )
        .addPath(
            pathData = addPathNodes("M37.3951 36.5867C38.9431 35.0987 44.9611 35.2127 46.5091 36.7607C48.0571 38.3027 48.2251 44.4347 46.7371 45.9287C45.2491 47.4167 39.0571 47.3627 37.5091 45.7547C35.9071 44.2067 35.9071 38.1347 37.3951 36.5867Z"),
            fill = SolidColor(Color.Black),
        )
        .addPath(
            pathData = addPathNodes("M100.377 36.5267C101.865 35.0387 108.057 35.1527 109.605 36.7007C111.147 38.2487 111.147 44.3207 109.659 45.8147C108.171 47.3027 102.093 47.3027 100.545 45.7547C99.0031 44.2067 98.8831 38.0207 100.377 36.5267Z"),
            fill = SolidColor(Color.Black),
        )
        .addPath(
            pathData = addPathNodes("M37.5091 99.7367C39.0571 98.1887 45.1891 98.0747 46.6771 99.6227C48.2251 101.117 48.1711 107.303 46.6231 108.851C45.0751 110.399 38.9971 110.339 37.4551 108.851C35.9611 107.303 35.9611 101.285 37.5091 99.7367Z"),
            fill = SolidColor(Color.Black),
        )
        .addPath(
            pathData = addPathNodes("M87.4231 86.6147C89.3731 84.6647 96.9391 84.6107 98.8291 86.4407C100.665 88.3367 100.605 95.9567 98.6551 97.8467C96.7651 99.7967 89.0311 100.025 87.1351 98.1347C85.2451 96.2447 85.4731 88.5647 87.4231 86.6147Z"),
            fill = SolidColor(Color.Black),
        )
        .addPath(
            pathData = addPathNodes("M111.381 86.5007C113.271 84.5507 121.005 84.5507 122.895 86.4407C124.791 88.3367 124.671 95.9567 122.727 97.8467C120.837 99.7967 113.271 99.8507 111.381 97.9607C109.485 96.0707 109.431 88.3907 111.381 86.5007Z"),
            fill = SolidColor(Color.Black),
        )
        .addPath(
            pathData = addPathNodes("M87.3091 110.567C89.1991 108.677 96.7651 108.677 98.7151 110.627C100.665 112.577 100.779 120.257 98.8831 122.147C96.9931 124.037 89.3131 123.863 87.4231 121.919C85.4731 120.023 85.4191 112.463 87.3091 110.567Z"),
            fill = SolidColor(Color.Black),
        )
        .addPath(
            pathData = addPathNodes("M40.6374 22.7511C34.7394 22.8111 26.3154 19.8891 23.1594 22.8111C20.0094 25.7331 21.8454 34.3851 21.7854 40.2891C21.7254 46.1931 19.5534 55.4751 22.7034 58.2231C25.8534 60.9771 34.6794 56.7951 40.6374 56.7951C46.6014 56.7351 55.3674 60.8031 58.4034 58.0551C61.4394 55.3011 58.8054 46.2471 58.8654 40.2891C58.8654 34.3251 61.5594 25.3311 58.5174 22.4091C55.4814 19.4871 46.5414 22.6971 40.6374 22.7511Z"),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 10.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
        .addPath(
            pathData = addPathNodes("M103.679 22.4091C97.7754 22.4091 88.8354 19.6551 86.0274 22.6371C83.1594 25.6191 86.8314 34.3251 86.7714 40.2891C86.6574 46.2471 82.7574 55.3611 85.6254 58.3371C88.4334 61.3791 97.7754 58.5111 103.679 58.3971C109.577 58.2831 118.061 60.7491 121.097 57.7071C124.133 54.6711 121.727 46.1931 121.841 40.2891C121.901 34.3851 124.595 25.3911 121.559 22.4091C118.577 19.4271 109.577 22.3491 103.679 22.4091Z"),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 10.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
        .addPath(
            pathData = addPathNodes("M40.6374 85.0431C34.6794 85.1031 25.7994 82.5831 22.9314 85.6191C20.0094 88.6551 23.3334 97.4211 23.3334 103.325C23.3334 109.229 20.1234 117.767 22.9914 120.977C25.9134 124.127 34.7394 122.411 40.6374 122.411C46.5414 122.411 55.6554 124.241 58.4034 121.091C61.0974 117.881 56.9694 109.283 57.0294 103.325C57.0294 97.3671 61.3254 88.4271 58.5774 85.3911C55.8834 82.3491 46.6014 84.9891 40.6374 85.0431Z"),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 10.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
        .build()
        .also { cachedQrScan = it }

private var cachedQrScan: ImageVector? = null
