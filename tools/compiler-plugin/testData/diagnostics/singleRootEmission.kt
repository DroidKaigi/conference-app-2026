import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember

interface KaigiNavigationBarScope : RowScope

@Composable
private fun <!COMPOSABLE_EMITS_FLAT_SIBLINGS!>FlatSiblings<!>() {
    Text("first")
    Text("second")
}

@Composable
private fun <!COMPOSABLE_EMITS_FLAT_SIBLINGS!>IndicatorBeforeContent<!>(selected: Boolean, icon: @Composable () -> Unit) {
    if (selected) {
        Box {}
    }
    icon()
}

@Composable
private fun <!COMPOSABLE_EMITS_FLAT_SIBLINGS!>BranchEmitsTwo<!>(expanded: Boolean) {
    if (expanded) {
        Text("title")
        Text("body")
    } else {
        Text("title")
    }
}

@Composable
private fun <!COMPOSABLE_EMITS_FLAT_SIBLINGS!>EmitsPerIteration<!>(labels: List<String>) {
    for (label in labels) {
        Text(label)
    }
}

@Composable
private fun <!COMPOSABLE_EMITS_FLAT_SIBLINGS!>ProviderBeforeContent<!>(label: String) {
    CompositionLocalProvider {
        Text(label)
    }
    Text(label)
}

@Composable
private fun <!COMPOSABLE_EMITS_FLAT_SIBLINGS!>KeyedSiblings<!>(id: String, label: String) {
    key(id) {
        Text(label)
    }
    Text(label)
}

@Composable
private fun BoxScope.OverlaidSiblings(selected: Boolean, icon: @Composable () -> Unit) {
    if (selected) {
        Box {}
    }
    icon()
}

@Composable
private fun ColumnScope.StackedSiblings(label: String) {
    Text(label)
    Text(label)
}

@Composable
private fun KaigiNavigationBarScope.BarSiblings(label: String) {
    Text(label)
    Text(label)
}

@Composable
private fun LazyItemScope.ItemSiblings(label: String) {
    Text(label)
    Text(label)
}

@Composable
private fun SingleContainer(label: String) {
    Column {
        Text(label)
        Text(label)
    }
}

@Composable
private fun EitherBranch(expanded: Boolean, label: String) {
    if (expanded) {
        Text(label)
    } else {
        Box {}
    }
}

@Composable
private fun EffectsBesideEmission(label: String) {
    LaunchedEffect(label) {}
    SideEffect {}
    DisposableEffect(label) { onDispose {} }
    Text(label)
}

@Composable
private fun RememberedValueBesideEmission(label: String) {
    val cached = remember { label }
    Text(cached)
}
