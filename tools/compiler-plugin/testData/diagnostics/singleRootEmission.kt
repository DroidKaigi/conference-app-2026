import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.UiComposable
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy

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

private fun interface LabelSlot {
    @Composable
    @UiComposable
    operator fun invoke(label: String)
}

@Composable
private fun <!COMPOSABLE_EMITS_FLAT_SIBLINGS!>SlotInterfaceBesideEmission<!>(slot: LabelSlot, label: String) {
    slot(label)
    Text(label)
}

private fun interface UnannotatedLabelSlot {
    @Composable
    operator fun invoke(label: String)
}

@Composable
private fun UnannotatedSlotBesideEmission(slot: UnannotatedLabelSlot, label: String) {
    slot(label)
    Text(label)
}

private fun interface LabelSyncEffect {
    @Composable
    operator fun invoke(label: String)
}

@Composable
private fun EffectInterfaceBesideEmission(sync: LabelSyncEffect, label: String) {
    sync(label)
    Text(label)
}

@Composable
private fun EffectTypedParameterBesideEmission(syncEffect: @Composable () -> Unit, label: String) {
    syncEffect()
    Text(label)
}

private interface LabelSource {
    @Composable
    fun rememberLabel(): String
}

@Composable
private fun AbstractValueBesideEmission(source: LabelSource) {
    Text(source.rememberLabel())
}

@Composable
private fun LeavesLoopAfterEmitting(labels: List<String>) {
    for (label in labels) {
        if (label.isNotEmpty()) {
            Text(label)
            break
        }
    }
}

@Composable
private fun ReturnsFromLoopAfterEmitting(labels: List<String>) {
    for (label in labels) {
        Text(label)
        return
    }
}

@Composable
private fun <!COMPOSABLE_EMITS_FLAT_SIBLINGS!>EmitsAfterLeavingLoop<!>(labels: List<String>, label: String) {
    for (item in labels) {
        Text(item)
        break
    }
    Text(label)
}

context(_: ColumnScope)
@Composable
private fun ContextScopedSiblings(label: String) {
    Text(label)
    Text(label)
}

@Composable
private fun LazyGridItemScope.GridCellSiblings(label: String) {
    Text(label)
    Text(label)
}

@Composable
private fun <!COMPOSABLE_EMITS_FLAT_SIBLINGS!>EmitsInPropertyInitializer<!>(expanded: Boolean, label: String) {
    Text(label)
    val length = if (expanded) {
        Text(label)
        1
    } else {
        2
    }
    label.take(length)
}

@Composable
private fun <!COMPOSABLE_EMITS_FLAT_SIBLINGS!>EmitsInsideInlineLambda<!>(label: String) {
    run {
        Text(label)
        Text(label)
    }
}

private enum class Density { Compact, Comfortable }

@Composable
private fun ReturnsFromEveryBranch(density: Density, label: String) {
    when (density) {
        Density.Compact -> return
        Density.Comfortable -> return
    }
    Text(label)
    Text(label)
}

@Composable
private fun EarlyReturnBeforeFallback(compact: Boolean, label: String) {
    if (compact) {
        Text(label)
        return
    }
    Box {}
}

@Composable
private fun <!COMPOSABLE_EMITS_FLAT_SIBLINGS!>EmitsAfterConditionalReturn<!>(compact: Boolean, label: String) {
    Text(label)
    if (compact) return
    Box {}
}

@Composable
private fun ThrowsAfterEmitting(compact: Boolean, label: String) {
    if (compact) {
        Text(label)
        throw IllegalStateException(label)
    }
    Box {}
}

@Composable
private fun <!COMPOSABLE_EMITS_FLAT_SIBLINGS!>EmitsWhileRepeating<!>(pending: Boolean, label: String) {
    while (pending) {
        Text(label)
    }
}

@Composable
private fun <!COMPOSABLE_EMITS_FLAT_SIBLINGS!>SkipsIterationsBeforeEmitting<!>(labels: List<String>) {
    for (label in labels) {
        if (label.isEmpty()) {
            continue
        }
        Text(label)
    }
}

@Composable
private fun String.<!COMPOSABLE_EMITS_FLAT_SIBLINGS!>DecoratedSiblings<!>() {
    Text(this)
    Text(this)
}

@Composable
private fun SoleEmissionAsExpression(label: String) = Text(label)

@Composable
private fun <!COMPOSABLE_EMITS_FLAT_SIBLINGS!>EmitsThenContinues<!>(labels: List<String>) {
    for (label in labels) {
        Text(label)
        continue
    }
}

@Composable
private fun DeadCodeAfterContinue(labels: List<String>, label: String) {
    for (item in labels) {
        continue
        Text(label)
    }
}

@Composable
private fun <!COMPOSABLE_EMITS_FLAT_SIBLINGS!>EmitsThenContinuesOuter<!>(rows: List<String>, cells: List<String>) {
    outer@ for (row in rows) {
        for (cell in cells) {
            Text(cell)
            continue@outer
        }
    }
}

@Composable
private fun EffectsBesideEmission(label: String) {
    Text(label)
    LaunchedEffect(label) {}
    SideEffect {}
    DisposableEffect(label) { onDispose {} }
}

@Composable
private fun <!COMPOSABLE_EMITS_FLAT_SIBLINGS!>EmitsTwiceAfterRemember<!>(label: String) {
    val cached = remember { label }
    Text(cached)
    Text(cached)
}

@Composable
private fun <!COMPOSABLE_EMITS_FLAT_SIBLINGS!>EmitsTwiceAfterLambdaLocalReturn<!>(labels: List<String>, label: String) {
    labels.forEach { return@forEach }
    Text(label)
    Text(label)
}

@Composable
private fun <S : ColumnScope> S.BoundedScopeSiblings(label: String) {
    Text(label)
    Text(label)
}

@Composable
private fun RememberedValueBesideEmission(label: String) {
    val cached = remember { label }
    Text(cached)
}

@Composable
private fun <!COMPOSABLE_EMITS_FLAT_SIBLINGS!>ProvidedSiblings<!>(label: String) {
    CompositionLocalProvider {
        Text(label)
        Text(label)
    }
}

@Composable
private fun BackHandlerBesideEmission(label: String) {
    BackHandler(enabled = true) {}
    Text(label)
}

@Composable
private fun SyncLabel(label: String) {
    LaunchedEffect(label) {}
}

@Composable
private fun SyncBesideEmission(label: String) {
    SyncLabel(label)
    Text(label)
}

@Composable
private fun StyledLabel(label: String, content: @Composable (String) -> Unit) {
    CompositionLocalProvider {
        content(label)
    }
}

@Composable
private fun <!COMPOSABLE_EMITS_FLAT_SIBLINGS!>StyledSiblings<!>(label: String) {
    StyledLabel(label) {
        Text(it)
        Text(it)
    }
}

@Composable
private fun <!COMPOSABLE_EMITS_FLAT_SIBLINGS!>MeasuredSiblings<!>(measurePolicy: MeasurePolicy) {
    Layout(measurePolicy)
    Layout(measurePolicy)
}

@Composable
private fun <!COMPOSABLE_EMITS_FLAT_SIBLINGS!>EmitsInsideArgumentExpression<!>(label: String) {
    Text(
        run {
            Text(label)
            label
        },
    )
}

@Composable
private fun <!COMPOSABLE_EMITS_FLAT_SIBLINGS!>ProviderAfterEmission<!>(label: String) {
    Text(label)
    CompositionLocalProvider {
        Text(label)
    }
}

@Composable
private fun ProviderAroundOne(label: String) {
    CompositionLocalProvider {
        Text(label)
    }
}

@Composable
private fun EmitsInsideRepeatedLambda(labels: List<String>) {
    labels.forEach { Text(it.trim()) }
}
