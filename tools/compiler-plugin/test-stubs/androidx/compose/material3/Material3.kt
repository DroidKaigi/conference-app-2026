package androidx.compose.material3

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposableInferredTarget
import androidx.compose.runtime.ComposableTarget

class ColorScheme

class Typography

class Shapes

object MaterialTheme {
    val colorScheme: ColorScheme
        @Composable get() = ColorScheme()

    val typography: Typography
        @Composable get() = Typography()

    val shapes: Shapes
        @Composable get() = Shapes()
}

class PaddingValues

@Composable
@ComposableTarget("androidx.compose.ui.UiComposable")
fun Text(text: String) {
}

@Composable
@ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
fun Button(onClick: () -> Unit, content: @Composable () -> Unit) {
}

@Composable
@ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
fun Card(content: @Composable () -> Unit) {
}

@Composable
@ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
fun Column(content: @Composable () -> Unit) {
}

@Composable
@ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
fun Row(content: @Composable () -> Unit) {
}

@Composable
@ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
fun Scaffold(content: @Composable (PaddingValues) -> Unit) {
}

class LazyListScope {
    fun <T> items(items: List<T>, itemContent: @Composable (T) -> Unit) {
    }
}

@Composable
@ComposableTarget("androidx.compose.ui.UiComposable")
fun LazyColumn(content: LazyListScope.() -> Unit) {
}
