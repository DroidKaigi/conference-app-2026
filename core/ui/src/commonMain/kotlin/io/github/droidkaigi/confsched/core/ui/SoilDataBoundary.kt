package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.droidkaigi.confsched.core.common.SoilDataContext
import soil.plant.compose.reacty.Await
import soil.plant.compose.reacty.ErrorBoundary
import soil.plant.compose.reacty.Suspense
import soil.query.compose.util.rememberQueriesErrorReset
import soil.query.core.DataModel

@Composable
context(_: SoilDataContext)
fun <T> SoilDataBoundary(
    state: DataModel<T>,
    modifier: Modifier = Modifier,
    fallback: SoilFallback = SoilFallbackDefaults.default(),
    content: @Composable (T) -> Unit,
) {
    ErrorBoundary(
        fallback = fallback.errorFallback,
        onReset = rememberQueriesErrorReset(),
        modifier = modifier,
    ) {
        Suspense(fallback = fallback.suspenseFallback) {
            Await(state = state, content = content)
        }
    }
}

@Composable
context(_: SoilDataContext)
fun <T1, T2> SoilDataBoundary(
    state1: DataModel<T1>,
    state2: DataModel<T2>,
    modifier: Modifier = Modifier,
    fallback: SoilFallback = SoilFallbackDefaults.default(),
    content: @Composable (T1, T2) -> Unit,
) {
    ErrorBoundary(
        fallback = fallback.errorFallback,
        onReset = rememberQueriesErrorReset(),
        modifier = modifier,
    ) {
        Suspense(fallback = fallback.suspenseFallback) {
            Await(state1 = state1, state2 = state2, content = content)
        }
    }
}

@Composable
context(_: SoilDataContext)
fun <T1, T2, T3> SoilDataBoundary(
    state1: DataModel<T1>,
    state2: DataModel<T2>,
    state3: DataModel<T3>,
    modifier: Modifier = Modifier,
    fallback: SoilFallback = SoilFallbackDefaults.default(),
    content: @Composable (T1, T2, T3) -> Unit,
) {
    ErrorBoundary(
        fallback = fallback.errorFallback,
        onReset = rememberQueriesErrorReset(),
        modifier = modifier,
    ) {
        Suspense(fallback = fallback.suspenseFallback) {
            Await(state1 = state1, state2 = state2, state3 = state3, content = content)
        }
    }
}

@Composable
context(_: SoilDataContext)
fun <T1, T2, T3, T4> SoilDataBoundary(
    state1: DataModel<T1>,
    state2: DataModel<T2>,
    state3: DataModel<T3>,
    state4: DataModel<T4>,
    modifier: Modifier = Modifier,
    fallback: SoilFallback = SoilFallbackDefaults.default(),
    content: @Composable (T1, T2, T3, T4) -> Unit,
) {
    ErrorBoundary(
        fallback = fallback.errorFallback,
        onReset = rememberQueriesErrorReset(),
        modifier = modifier,
    ) {
        Suspense(fallback = fallback.suspenseFallback) {
            // Await tops out at three states, so the fourth is awaited inside the same boundary.
            Await(state1 = state1, state2 = state2, state3 = state3) { value1, value2, value3 ->
                Await(state = state4) { value4 -> content(value1, value2, value3, value4) }
            }
        }
    }
}

@Composable
private fun ErrorBoundary(
    modifier: Modifier = Modifier,
    fallback: @Composable context(SoilErrorContext) BoxScope.() -> Unit,
    onReset: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    ErrorBoundary(
        fallback = { with(DefaultSoilErrorContext(it)) { fallback() } },
        onReset = onReset,
        modifier = modifier,
        content = content,
    )
}

@Composable
private fun Suspense(
    fallback: @Composable context(SoilSuspenseContext) BoxScope.() -> Unit,
    content: @Composable () -> Unit,
) {
    Suspense(
        fallback = { with(DefaultSoilSuspenseContext()) { fallback() } },
        content = content,
    )
}
