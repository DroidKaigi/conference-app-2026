package io.github.droidkaigi.confsched.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

val KaigiIcons.Default.Person: ImageVector
    get() = cachedPerson ?: ImageVector.Builder(
        name = "KaigiIcons.Default.Person",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    )
        .addPath(
            pathData = addPathNodes("M4.30664 20.3574C4.30664 20.3574 4.64564 18.0674 5.15064 17.0894C5.65964 16.1034 6.45664 15.0994 7.35864 14.4684C8.25764 13.8414 9.45864 13.4754 10.5476 13.3074C11.6216 13.1424 12.8036 13.1834 13.8466 13.4514C14.8866 13.7184 15.9606 14.2414 16.7966 14.9124C17.6346 15.5844 18.3846 16.5224 18.8676 17.4814C19.3496 18.4384 19.6936 20.6574 19.6936 20.6574M12.3496 3.34635C12.9346 3.38035 13.5966 3.68035 14.0646 4.02835C14.5166 4.36535 14.8316 4.90535 15.1326 5.37735C15.4236 5.83335 15.6796 6.29935 15.8536 6.80935C16.0336 7.33835 16.2356 7.94835 16.1806 8.50235C16.1246 9.06935 15.8426 9.69435 15.4996 10.1694C15.1516 10.6514 14.6326 11.1024 14.0976 11.3674C13.5596 11.6354 12.8786 11.7874 12.2796 11.7654C11.6856 11.7424 11.0326 11.5334 10.5186 11.2394C10.0106 10.9494 9.55664 10.4914 9.20664 10.0234C8.85864 9.55735 8.54164 8.99835 8.42264 8.44135C8.30264 7.88435 8.35764 7.24135 8.49064 6.68035C8.62264 6.11935 8.88064 5.54735 9.21864 5.07535C9.56264 4.59535 10.0266 4.11635 10.5436 3.82935C11.0676 3.53735 11.7616 3.31135 12.3496 3.34635Z"),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
        .build()
        .also { cachedPerson = it }

private var cachedPerson: ImageVector? = null
