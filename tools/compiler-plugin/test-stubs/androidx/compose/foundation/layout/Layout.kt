package androidx.compose.foundation.layout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposableInferredTarget

interface BoxScope

interface RowScope

interface ColumnScope

@Composable
@ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
fun Box(content: @Composable BoxScope.() -> Unit) {
}

@Composable
@ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
fun Row(content: @Composable RowScope.() -> Unit) {
}

@Composable
@ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
fun Column(content: @Composable ColumnScope.() -> Unit) {
}
