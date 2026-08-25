package io.github.droidkaigi.confsched.feature.search.component

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.KaigiIllustrationColors

internal fun searchSceneVector(
    direction: SearchSceneDirection,
    colors: ColorScheme,
    illustrationColors: KaigiIllustrationColors,
): ImageVector = when (direction) {
    SearchSceneDirection.RummageBox -> searchRummageBoxSceneVector(colors)
    SearchSceneDirection.Constellation -> searchConstellationSceneVector(colors, illustrationColors)
    SearchSceneDirection.Magnifier -> searchMagnifierSceneVector(colors)
    SearchSceneDirection.Signpost -> searchSignpostSceneVector(colors)
    SearchSceneDirection.EmptyBox -> searchEmptyBoxSceneVector(colors)
}

private fun searchRummageBoxSceneVector(
    colors: ColorScheme,
): ImageVector = ImageVector.Builder(
    name = "SearchRummageBoxScene",
    defaultWidth = 300.dp,
    defaultHeight = 202.dp,
    viewportWidth = 300f,
    viewportHeight = 202f,
).apply {
    addPath(
        pathData = addPathNodes("M 24 178 L 28 168 L 32 178 H 24 Z M 276 178 L 280 168 L 284 178 H 276 Z"),
        fill = SolidColor(colors.onSurface),
    )
    addPath(
        pathData = addPathNodes(
            "M 109.903 77.6864 L 124.074 72.3654 L 136.756 63.9885 L 150.386 57.2847 L 159.705 74.2016 L " +
                "166.836 93.1095 L 176.455 110.87 L 163.169 117.506 L 149.969 124.089 L 133.775 130.418 L 125.408 " +
                "112.648 L 118.371 95.6191 L 109.903 77.6864 Z",
        ),
        fill = SolidColor(colors.surfaceContainerLow),
        stroke = SolidColor(colors.onSurface),
        strokeLineWidth = 1.6f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    )
    addPath(
        pathData = addPathNodes(
            "M 125.756 89.7863 L 135.23 84.665 L 144.004 81.5314 L 145.726 83.4178 L 135.92 87.7219 L 127.782 " +
                "92.0469 L 125.756 89.7863 Z M 129.401 96.5973 L 138.323 92.1235 L 147.334 87.6506 L 148.441 " +
                "89.6472 L 139.656 94.7888 L 131.03 99.185 L 129.401 96.5973 Z",
        ),
        fill = SolidColor(colors.outlineVariant),
    )
    addPath(
        pathData = addPathNodes(
            "M 126.591 67.6646 L 141.672 62.1714 L 157.296 60.1447 L 171.806 55.3786 L 176.888 75.1215 L " +
                "180.859 93.2118 L 185.803 112.761 L 171.106 115.292 L 154.963 119.388 L 141.69 123.643 L 135.814 " +
                "104.945 L 130.854 85.0616 L 126.591 67.6646 Z",
        ),
        fill = SolidColor(colors.surfaceContainerLow),
        stroke = SolidColor(colors.onSurface),
        strokeLineWidth = 1.6f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    )
    addPath(
        pathData = addPathNodes(
            "M 140.554 81.7346 L 150.617 78.6006 L 160.526 76.3129 L 160.896 79.4706 L 151.508 81.8842 L " +
                "141.469 84.6742 L 140.554 81.7346 Z M 141.933 88.7744 L 152.437 86.5751 L 161.462 84.3683 L " +
                "162.714 88.0102 L 153.609 89.7837 L 143.248 92.5145 L 141.933 88.7744 Z",
        ),
        fill = SolidColor(colors.outlineVariant),
    )
    addPath(
        pathData = addPathNodes(
            "M 147.205 59.595 L 161.305 58.875 L 176.545 59.145 L 192.085 59.325 L 192.695 77.075 L 193.225 " +
                "96.965 L 193.565 116.915 L 177.145 117.125 L 161.675 116.175 L 146.435 115.715 L 147.125 96.835 " +
                "L 147.395 77.085 L 147.205 59.595 Z",
        ),
        fill = SolidColor(colors.surfaceContainerLow),
        stroke = SolidColor(colors.onSurface),
        strokeLineWidth = 1.6f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    )
    addPath(
        pathData = addPathNodes(
            "M 157.04 76.5648 L 167.28 76.1748 L 177.05 76.6048 L 176.96 79.4848 L 166.72 79.8248 L 157.39 " +
                "79.6648 L 157.04 76.5648 Z M 157.225 84.0801 L 167.115 85.1301 L 176.705 84.1701 L 176.755 " +
                "88.0901 L 167.515 87.0801 L 156.855 87.9201 L 157.225 84.0801 Z",
        ),
        fill = SolidColor(colors.outlineVariant),
    )
    addPath(
        pathData = addPathNodes(
            "M 169.251 56.0895 L 182.73 58.873 L 199.049 64.2517 L 212.084 67.3125 L 208.222 86.752 L 204.634 " +
                "105.095 L 200.502 122.994 L 184.343 119.859 L 170.753 116.978 L 154.327 112.775 L 159.249 " +
                "93.9908 L 164.166 76.0265 L 169.251 56.0895 Z",
        ),
        fill = SolidColor(colors.surfaceContainerLow),
        stroke = SolidColor(colors.onSurface),
        strokeLineWidth = 1.6f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    )
    addPath(
        pathData = addPathNodes(
            "M 173.41 74.8408 L 183.51 77.6345 L 192.842 79.7993 L 193.448 82.1559 L 183.422 80.2826 L " +
                "172.865 77.7325 L 173.41 74.8408 Z M 171.833 82.8463 L 180.708 85.0388 L 191.213 87.5462 L " +
                "190.199 91.0479 L 180.523 88.6395 L 171.17 85.6285 L 171.833 82.8463 Z",
        ),
        fill = SolidColor(colors.outlineVariant),
    )
    addPath(
        pathData = addPathNodes(
            "M 188.233 57.8682 L 202.707 63.5038 L 216.36 71.075 L 230.544 78.5605 L 221.568 95.3219 L " +
                "214.813 112.866 L 205.237 130.926 L 192.27 123.5 L 177.238 117.938 L 164.058 111.587 L 171.983 " +
                "93.0336 L 181.198 75.0757 L 188.233 57.8682 Z",
        ),
        fill = SolidColor(colors.surfaceContainerLow),
        stroke = SolidColor(colors.onSurface),
        strokeLineWidth = 1.6f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    )
    addPath(
        pathData = addPathNodes(
            "M 190.624 78.7442 L 199.142 82.2425 L 208.517 86.8594 L 206.624 89.8746 L 197.862 85.3455 L " +
                "189.679 80.8644 L 190.624 78.7442 Z M 186.545 85.3054 L 195.719 90.4028 L 204.724 93.8606 L " +
                "203.518 97.6337 L 195.063 92.887 L 185.446 87.7402 L 186.545 85.3054 Z",
        ),
        fill = SolidColor(colors.outlineVariant),
    )
    addPath(
        pathData = addPathNodes(
            "M 96.1 106.12 L 115.04 106.36 L 138.98 108.61 L 157.63 107.27 L 179.07 106.21 L 202.72 108.31 L " +
                "223.55 106.9 L 245.46 106 L 243.55 129.68 L 244.71 155.44 L 245.45 178.58 L 224.82 179.36 L " +
                "201.09 179.31 L 180.13 177.86 L 157.56 179.18 L 139.12 177.76 L 117.48 180.68 L 95 178.86 L " +
                "95.82 154.06 L 95.71 131.61 L 96.1 106.12 Z",
        ),
        fill = SolidColor(colors.secondaryContainer),
        stroke = SolidColor(colors.onSurface),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    )
    addPath(
        pathData = addPathNodes(
            "M 141 132.03 L 155.51 133.08 L 171.21 132.48 L 185.37 132 L 199.89 132.58 L 199.53 151.94 L " +
                "185.63 152.16 L 169.58 152.51 L 156.09 153.38 L 141.61 152.9 L 141 132.03 Z",
        ),
        fill = SolidColor(colors.surfaceContainerHigh),
        stroke = SolidColor(colors.onSurface),
        strokeLineWidth = 1.6f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    )
    addPath(
        pathData = addPathNodes(
            "M 89 95 L 114.24 97.26 L 135.98 97.2 L 160.24 96.57 L 181.72 96.56 L 205.49 96.7 L 229.15 95.2 L " +
                "253.03 95.55 L 252.04 110.71 L 227.92 113.03 L 206.53 112.95 L 181.28 112.75 L 157.99 110.81 L " +
                "136.6 111.67 L 111.87 110.51 L 89.51 111.65 L 89 95 Z",
        ),
        fill = SolidColor(colors.surfaceContainerHigh),
        stroke = SolidColor(colors.onSurface),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    )
    addPath(
        pathData = addPathNodes(
            "M 36.3165 145.189 L 38.5557 157.693 L 40.5546 170.202 L 29.2768 171.389 L 18.2372 173.522 L " +
                "6.97468 175.569 L 5.64423 162.834 L 3.71469 150.176 L 15.2586 148.9 L 26.0096 146.91 L 36.3165 " +
                "145.189 Z",
        ),
        fill = SolidColor(colors.surfaceContainerLow),
        stroke = SolidColor(colors.onSurface),
        strokeLineWidth = 1.6f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    )
    addPath(
        pathData = addPathNodes(
            "M 153 140.39 L 164.94 140.07 L 176.18 140.45 L 187.98 140 L 187.68 143.42 L 176.66 142.89 L " +
                "164.91 143.81 L 153.35 143.67 L 153 140.39 Z",
        ),
        fill = SolidColor(colors.outlineVariant),
    )
}.build()

private fun searchConstellationSceneVector(
    colors: ColorScheme,
    illustrationColors: KaigiIllustrationColors,
): ImageVector = ImageVector.Builder(
    name = "SearchConstellationScene",
    defaultWidth = 336.dp,
    defaultHeight = 253.dp,
    viewportWidth = 336f,
    viewportHeight = 253f,
).apply {
    addPath(
        pathData = addPathNodes(
            "M 0.800637 2.99217 L 32.2855 2.76199 L 62.5695 1.81124 L 93.474 1.12069 L 121.246 3.4025 L " +
                "151.15 0.800438 L 182.314 2.73197 L 212.979 1.8913 L 242.352 2.37168 L 272.706 2.53181 L 303.01 " +
                "3.85285 L 334.815 2.22156 L 332.813 30.6441 L 335.095 58.9064 L 334.585 88.1496 L 335.095 " +
                "118.173 L 332.343 146.706 L 334.925 173.297 L 303.23 173.057 L 272.215 174.638 L 242.282 174.858 " +
                "L 213.799 175.969 L 182.574 173.157 L 153.191 172.897 L 120.906 173.657 L 91.2122 174.598 L " +
                "60.8782 173.237 L 30.384 174.628 L 2.91231 173.897 L 2.12168 147.076 L 2.50198 117.173 L 2.32184 " +
                "88.75 L 1.93153 58.7663 L 2.85226 30.594 L 0.800637 2.99217 Z",
        ),
        fill = SolidColor(illustrationColors.skyPanel),
        stroke = SolidColor(colors.onSurface),
        strokeLineWidth = 1.6f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    )
    addPath(
        pathData = addPathNodes(
            "M 58.8466 44.8353 m -12.0095 0 a 12.0095 12.0095 0 1 0 24.019 0 a 12.0095 12.0095 0 1 0 -24.019 " +
                "0 Z M 196.956 36.8289 m -12.0095 0 a 12.0095 12.0095 0 1 0 24.019 0 a 12.0095 12.0095 0 1 0 " +
                "-24.019 0 Z M 269.013 92.8733 m -12.0095 0 a 12.0095 12.0095 0 1 0 24.019 0 a 12.0095 12.0095 0 " +
                "1 0 -24.019 0 Z",
        ),
        fill = SolidColor(illustrationColors.onSkyPanel),
    )
    addPath(
        pathData = addPathNodes(
            "M 58.8466 44.8353 L 128.902 50.84 L 152.921 74.859 L 196.956 36.8289 L 269.013 92.8733 L 232.984 " +
                "132.905 L 176.94 120.895",
        ),
        stroke = SolidColor(illustrationColors.onSkyPanel),
        strokeLineWidth = 1.6f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    )
    addPath(
        pathData = addPathNodes(
            "M 58.8466 44.8353 m -5.00396 0 a 5.00396 5.00396 0 1 0 10.0079 0 a 5.00396 5.00396 0 1 0 " +
                "-10.0079 0 Z M 196.956 36.8289 m -5.00396 0 a 5.00396 5.00396 0 1 0 10.0079 0 a 5.00396 5.00396 " +
                "0 1 0 -10.0079 0 Z M 269.013 92.8733 m -5.00396 0 a 5.00396 5.00396 0 1 0 10.0079 0 a 5.00396 " +
                "5.00396 0 1 0 -10.0079 0 Z M 152.921 74.859 m -3.50277 0 a 3.50277 3.50277 0 1 0 7.00554 0 a " +
                "3.50277 3.50277 0 1 0 -7.00554 0 Z M 232.984 132.905 m -3.50277 0 a 3.50277 3.50277 0 1 0 " +
                "7.00554 0 a 3.50277 3.50277 0 1 0 -7.00554 0 Z M 30.8244 104.883 m -3.50277 0 a 3.50277 3.50277 " +
                "0 1 0 7.00554 0 a 3.50277 3.50277 0 1 0 -7.00554 0 Z M 128.902 50.84 m -3.00238 0 a 3.00238 " +
                "3.00238 0 1 0 6.00476 0 a 3.00238 3.00238 0 1 0 -6.00476 0 Z M 176.94 120.895 m -3.00238 0 a " +
                "3.00238 3.00238 0 1 0 6.00476 0 a 3.00238 3.00238 0 1 0 -6.00476 0 Z M 86.8687 22.8177 m " +
                "-2.20174 0 a 2.20174 2.20174 0 1 0 4.40348 0 a 2.20174 2.20174 0 1 0 -4.40348 0 Z M 250.999 " +
                "28.8224 m -2.20174 0 a 2.20174 2.20174 0 1 0 4.40348 0 a 2.20174 2.20174 0 1 0 -4.40348 0 Z M " +
                "301.038 58.8462 m -2.20174 0 a 2.20174 2.20174 0 1 0 4.40348 0 a 2.20174 2.20174 0 1 0 -4.40348 " +
                "0 Z M 301.038 130.903 m -2.20174 0 a 2.20174 2.20174 0 1 0 4.40348 0 a 2.20174 2.20174 0 1 0 " +
                "-4.40348 0 Z M 44.8355 146.916 m -2.20174 0 a 2.20174 2.20174 0 1 0 4.40348 0 a 2.20174 2.20174 " +
                "0 1 0 -4.40348 0 Z M 214.97 158.925 m -2.20174 0 a 2.20174 2.20174 0 1 0 4.40348 0 a 2.20174 " +
                "2.20174 0 1 0 -4.40348 0 Z M 172.937 134.907 m -2.4019 0 a 2.4019 2.4019 0 1 0 4.8038 0 a 2.4019 " +
                "2.4019 0 1 0 -4.8038 0 Z M 166.932 148.918 m -1.80143 0 a 1.80143 1.80143 0 1 0 3.60286 0 a " +
                "1.80143 1.80143 0 1 0 -3.60286 0 Z M 158.926 162.929 m -1.30103 0 a 1.30103 1.30103 0 1 0 " +
                "2.60206 0 a 1.30103 1.30103 0 1 0 -2.60206 0 Z",
        ),
        fill = SolidColor(illustrationColors.onSkyPanel),
    )
    addPath(
        pathData = addPathNodes(
            "M 20.8165 234.986 L 24.8196 224.978 L 28.8228 234.986 H 20.8165 Z M 301.038 234.986 L 305.041 " +
                "224.978 L 309.045 234.986 H 301.038 Z",
        ),
        fill = SolidColor(colors.onSurface),
    )
}.build()

private fun searchMagnifierSceneVector(
    colors: ColorScheme,
): ImageVector = ImageVector.Builder(
    name = "SearchMagnifierScene",
    defaultWidth = 332.dp,
    defaultHeight = 244.dp,
    viewportWidth = 332f,
    viewportHeight = 244f,
).apply {
    addPath(
        pathData = addPathNodes(
            "M 176.157 160.209 L 191.422 177.288 L 208.96 192.776 L 226.357 209.75 L 241.652 226.425 L " +
                "232.638 237.244 L 215.303 219.101 L 198.507 202.939 L 183.896 186.092 L 165.638 168.48 L 176.157 " +
                "160.209 Z M 118 112 m -70 0 a 70 70 0 1 0 140 0 a 70 70 0 1 0 -140 0 Z",
        ),
        fill = SolidColor(colors.primary),
    )
    addPath(
        pathData = addPathNodes("M 118 112 m -59 0 a 59 59 0 1 0 118 0 a 59 59 0 1 0 -118 0 Z"),
        fill = SolidColor(colors.primaryContainer),
    )
    addPath(
        pathData = addPathNodes("M 80 154 L 99 154.61 L 118 154.24 L 137 154.08 L 156 155.74"),
        stroke = SolidColor(colors.primary),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    )
    addPath(
        pathData = addPathNodes(
            "M 252 52 m -4 0 a 4 4 0 1 0 8 0 a 4 4 0 1 0 -8 0 Z M 286 86 m -3 0 a 3 3 0 1 0 6 0 a 3 3 0 1 0 " +
                "-6 0 Z M 240 124 m -3.5 0 a 3.5 3.5 0 1 0 7 0 a 3.5 3.5 0 1 0 -7 0 Z M 300 146 m -2.5 0 a 2.5 " +
                "2.5 0 1 0 5 0 a 2.5 2.5 0 1 0 -5 0 Z M 266 176 m -3 0 a 3 3 0 1 0 6 0 a 3 3 0 1 0 -6 0 Z M 224 " +
                "74 m -2.5 0 a 2.5 2.5 0 1 0 5 0 a 2.5 2.5 0 1 0 -5 0 Z M 248.3 150.24 L 253.57 150 L 259.44 " +
                "150.39 L 259.2 155.3 L 254.16 154.84 L 248 154.88 L 248.3 150.24 Z M 292.14 110 L 297.44 110.81 " +
                "L 302.94 110.57 L 303.22 115.67 L 297.62 115.11 L 292 115.26 L 292.14 110 Z",
        ),
        fill = SolidColor(colors.onSurfaceVariant),
    )
    addPath(
        pathData = addPathNodes(
            "M 79.2988 67.7122 L 89.4873 64.636 L 99.0831 62.8618 L 101.309 75.674 L 105.109 88.2276 L " +
                "95.3433 89.6112 L 85.3046 93.66 L 81.8203 80.7185 L 79.2988 67.7122 Z",
        ),
        fill = SolidColor(colors.surfaceContainerLow),
        stroke = SolidColor(colors.onSurface),
        strokeLineWidth = 1.6f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    )
    addPath(
        pathData = addPathNodes("M 85.1563 73.1206 L 95.7202 70.8062 L 96.335 72.8172 L 85.9647 75.7842 L 85.1563 73.1206 Z"),
        fill = SolidColor(colors.outlineVariant),
    )
    addPath(
        pathData = addPathNodes(
            "M 134.393 71.65 L 145.49 73.104 L 154.53 75.0241 L 151.832 88.3238 L 149.436 100.755 L 139.792 " +
                "99.0637 L 129.479 97.1407 L 132.998 84.2248 L 134.393 71.65 Z",
        ),
        fill = SolidColor(colors.surfaceContainerLow),
        stroke = SolidColor(colors.onSurface),
        strokeLineWidth = 1.6f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    )
    addPath(
        pathData = addPathNodes("M 138.145 78.5526 L 148.287 80.5138 L 148.284 83.4674 L 137.071 81.5629 L 138.145 78.5526 Z"),
        fill = SolidColor(colors.outlineVariant),
    )
    addPath(
        pathData = addPathNodes(
            "M 140.162 108.399 L 150.847 106.834 L 160.578 105.851 L 161.471 119.322 L 163.883 132.039 L " +
                "154.238 132.972 L 143.624 133.103 L 142.827 120.356 L 140.162 108.399 Z",
        ),
        fill = SolidColor(colors.surfaceContainerLow),
        stroke = SolidColor(colors.onSurface),
        strokeLineWidth = 1.6f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    )
    addPath(
        pathData = addPathNodes("M 145.762 114.507 L 156.818 113.506 L 157.193 115.548 L 146.291 116.482 L 145.762 114.507 Z"),
        fill = SolidColor(colors.outlineVariant),
    )
}.build()

private fun searchSignpostSceneVector(
    colors: ColorScheme,
): ImageVector = ImageVector.Builder(
    name = "SearchSignpostScene",
    defaultWidth = 260.dp,
    defaultHeight = 140.dp,
    viewportWidth = 260f,
    viewportHeight = 140f,
).apply {
    addPath(
        pathData = addPathNodes("M 18 118 L 22 108 L 26 118 H 18 Z M 238 118 L 242 108 L 246 118 H 238 Z"),
        fill = SolidColor(colors.onSurface),
    )
    addPath(
        pathData = addPathNodes(
            "M 164 44 L 173.69 44.1 L 173.62 70.08 L 173.14 94.64 L 172.83 119.26 L 165.75 118.59 L 165.4 " +
                "94.46 L 164.91 70.16 L 164 44 Z",
        ),
        fill = SolidColor(colors.tertiaryContainer),
        stroke = SolidColor(colors.onSurface),
        strokeLineWidth = 1.6f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    )
    addPath(
        pathData = addPathNodes(
            "M 170.45 52.94 L 170.05 70.82 L 152.09 70.97 L 133.49 70.14 L 114.14 71.55 L 106 62.23 L 114.18 " +
                "53.49 L 132.75 52 L 152.58 52.23 L 170.45 52.94 Z M 170.55 80.89 L 187.39 80.19 L 203.67 81.79 L " +
                "220.83 80 L 231.24 89.74 L 221.56 99.12 L 205.25 98.73 L 187.93 98.01 L 170 99.87 L 170.55 80.89 " +
                "Z",
        ),
        fill = SolidColor(colors.secondaryContainer),
        stroke = SolidColor(colors.onSurface),
        strokeLineWidth = 1.6f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    )
    addPath(
        pathData = addPathNodes(
            "M 116 60.31 L 126.86 60 L 137.38 61.09 L 148.54 60.55 L 148.63 63.32 L 137.97 64.08 L 126.71 " +
                "63.32 L 116.39 63.44 L 116 60.31 Z M 180.29 88.29 L 190.48 88.48 L 201.16 88 L 211.87 88.57 L " +
                "211.85 91.1 L 201.22 91.25 L 190.97 91.47 L 180 91.35 L 180.29 88.29 Z",
        ),
        fill = SolidColor(colors.outlineVariant),
    )
}.build()

private fun searchEmptyBoxSceneVector(
    colors: ColorScheme,
): ImageVector = ImageVector.Builder(
    name = "SearchEmptyBoxScene",
    defaultWidth = 260.dp,
    defaultHeight = 150.dp,
    viewportWidth = 260f,
    viewportHeight = 150f,
).apply {
    addPath(
        pathData = addPathNodes("M 16 128 L 20 118 L 24 128 H 16 Z M 242 128 L 246 118 L 250 128 H 242 Z"),
        fill = SolidColor(colors.onSurface),
    )
    addPath(
        pathData = addPathNodes(
            "M 81 41.09 L 101.2 41.82 L 122.72 41.77 L 140.77 40 L 161.48 40.44 L 181.98 40.33 L 200.83 40.05 " +
                "L 201.97 67.99 L 181.15 66.05 L 160.67 66.9 L 142.69 66.2 L 120.8 66.21 L 102.55 67.23 L 82.69 " +
                "67.92 L 81 41.09 Z",
        ),
        fill = SolidColor(colors.tertiaryContainer),
        stroke = SolidColor(colors.onSurface),
        strokeLineWidth = 1.6f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    )
    addPath(
        pathData = addPathNodes(
            "M 92.32 44 L 107.14 44.51 L 106.66 51.54 L 107.07 60.01 L 99.1 64.25 L 92 59.58 L 92.28 51.54 L " +
                "92.32 44 Z",
        ),
        fill = SolidColor(colors.surfaceContainerHigh),
        stroke = SolidColor(colors.onSurface),
        strokeLineWidth = 1.6f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    )
    addPath(
        pathData = addPathNodes(
            "M 76.82 40 L 86.44 40.21 L 87.01 51.88 L 86.25 63.63 L 77.24 64.25 L 76 52.6 L 76.82 40 Z M " +
                "196.66 40.32 L 207.77 40 L 207.58 50.81 L 207.31 62.74 L 196.87 63.07 L 196 50.69 L 196.66 40.32 " +
                "Z",
        ),
        fill = SolidColor(colors.secondaryContainer),
        stroke = SolidColor(colors.onSurface),
        strokeLineWidth = 1.6f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    )
    addPath(
        pathData = addPathNodes(
            "M 76.07 64.39 L 97.81 64 L 122.29 64.77 L 141.3 64.84 L 164.65 66.1 L 185.93 66.68 L 208.23 " +
                "65.05 L 208.94 88.06 L 207.15 109.2 L 206.78 128.75 L 186.17 128.54 L 162.88 128.59 L 142.26 " +
                "128.49 L 121.67 127.66 L 99.06 127.75 L 78.18 129.43 L 76.25 107.68 L 76 87.21 L 76.07 64.39 Z",
        ),
        fill = SolidColor(colors.secondaryContainer),
        stroke = SolidColor(colors.onSurface),
        strokeLineWidth = 2f,
        strokeLineCap = StrokeCap.Round,
    )
    addPath(
        pathData = addPathNodes(
            "M 116 91.47 L 128.42 91.16 L 140.67 90.11 L 153.38 90.23 L 166.1 90 L 166.65 108.29 L 152.99 " +
                "107.62 L 140.59 108.13 L 128.15 107.64 L 116.64 107.12 L 116 91.47 Z",
        ),
        fill = SolidColor(colors.surfaceContainerHigh),
        stroke = SolidColor(colors.onSurface),
        strokeLineWidth = 1.6f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    )
    addPath(
        pathData = addPathNodes(
            "M 126.05 97 L 135.94 97.98 L 145.53 97.31 L 156.21 97.45 L 155.9 101.02 L 146.31 100.2 L 136.26 " +
                "100.97 L 126 100.1 L 126.05 97 Z",
        ),
        fill = SolidColor(colors.outlineVariant),
    )
    addPath(
        pathData = addPathNodes(
            "M 229.978 95.4892 L 232.629 107.563 L 234.755 119.713 L 223.378 123.054 L 213.279 123.57 L " +
                "201.498 126.81 L 200.584 114.422 L 198.503 101.981 L 208.672 99.9056 L 220.121 97.4707 L 229.978 " +
                "95.4892 Z",
        ),
        fill = SolidColor(colors.surfaceContainerLow),
        stroke = SolidColor(colors.onSurface),
        strokeLineWidth = 1.6f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    )
}.build()
