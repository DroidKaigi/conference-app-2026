package androidx.compose.ui.layout

import androidx.compose.runtime.Composable
import androidx.compose.ui.UiComposable

interface MeasurePolicy

@Composable
@UiComposable
fun Layout(measurePolicy: MeasurePolicy) {
}
