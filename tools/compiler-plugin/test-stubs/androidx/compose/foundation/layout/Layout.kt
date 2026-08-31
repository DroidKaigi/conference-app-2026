package androidx.compose.foundation.layout

import androidx.compose.runtime.Composable

interface BoxScope

interface RowScope

interface ColumnScope

@Composable
fun Box(content: @Composable BoxScope.() -> Unit) {
}

@Composable
fun Row(content: @Composable RowScope.() -> Unit) {
}

@Composable
fun Column(content: @Composable ColumnScope.() -> Unit) {
}
