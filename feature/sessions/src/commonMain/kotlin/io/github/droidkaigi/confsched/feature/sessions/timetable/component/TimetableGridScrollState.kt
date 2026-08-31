package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.draggable2D
import androidx.compose.foundation.gestures.rememberDraggable2DState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.ScrollAxisRange
import androidx.compose.ui.semantics.horizontalScrollAxisRange
import androidx.compose.ui.semantics.scrollBy
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.verticalScrollAxisRange
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Scroll position of the grid on both axes at once.
 *
 * Offsets grow towards the bottom-right; drag and fling deltas arrive in pointer direction
 * (content following the finger) and are negated here.
 */
@Stable
internal class TimetableGridScrollState(
    initialScrollX: Float,
    initialScrollY: Float,
) {
    var scrollX by mutableFloatStateOf(initialScrollX)
        private set
    var scrollY by mutableFloatStateOf(initialScrollY)
        private set
    var maxScrollX by mutableFloatStateOf(0f)
        private set
    var maxScrollY by mutableFloatStateOf(0f)
        private set

    fun updateBounds(maxScrollX: Float, maxScrollY: Float) {
        this.maxScrollX = maxScrollX.coerceAtLeast(0f)
        this.maxScrollY = maxScrollY.coerceAtLeast(0f)
        scrollX = scrollX.coerceIn(0f, this.maxScrollX)
        scrollY = scrollY.coerceIn(0f, this.maxScrollY)
    }

    fun dragBy(delta: Offset): Offset {
        val nextX = (scrollX - delta.x).coerceIn(0f, maxScrollX)
        val nextY = (scrollY - delta.y).coerceIn(0f, maxScrollY)
        val consumed = Offset(scrollX - nextX, scrollY - nextY)
        scrollX = nextX
        scrollY = nextY
        return consumed
    }

    fun dragBy(delta: Offset, dispatcher: NestedScrollDispatcher, source: NestedScrollSource): Offset {
        val preConsumed = dispatcher.dispatchPreScroll(available = delta, source = source)
        val remaining = delta - preConsumed
        val consumed = dragBy(remaining)
        dispatcher.dispatchPostScroll(
            consumed = preConsumed + consumed,
            available = remaining - consumed,
            source = source,
        )
        return preConsumed + consumed
    }

    suspend fun fling(
        velocity: Velocity,
        decay: DecayAnimationSpec<Float>,
        dispatcher: NestedScrollDispatcher,
    ) = coroutineScope {
        val preConsumed = dispatcher.dispatchPreFling(velocity)
        val remaining = velocity - preConsumed
        val consumedX = async { flingAxis(remaining.x, decay) { delta -> dragBy(Offset(delta, 0f)).x } }
        // Only the vertical fling reaches the parent: the collapsing header above the grid
        // reacts to vertical motion alone.
        val consumedY = flingAxis(remaining.y, decay) { delta ->
            dragBy(Offset(0f, delta), dispatcher, NestedScrollSource.SideEffect).y
        }
        val consumed = Velocity(consumedX.await(), consumedY)
        dispatcher.dispatchPostFling(
            consumed = preConsumed + consumed,
            available = remaining - consumed,
        )
    }

    private suspend fun flingAxis(
        velocity: Float,
        decay: DecayAnimationSpec<Float>,
        drag: (Float) -> Float,
    ): Float {
        var lastValue = 0f
        var lastVelocity = velocity
        AnimationState(initialValue = 0f, initialVelocity = velocity).animateDecay(decay) {
            val delta = value - lastValue
            lastValue = value
            lastVelocity = this.velocity
            if (kotlin.math.abs(drag(delta) - delta) > 0.5f) cancelAnimation()
        }
        return velocity - lastVelocity
    }

    companion object {
        val Saver = listSaver<TimetableGridScrollState, Float>(
            save = { listOf(it.scrollX, it.scrollY) },
            restore = { TimetableGridScrollState(initialScrollX = it[0], initialScrollY = it[1]) },
        )
    }
}

@Composable
internal fun rememberTimetableGridScrollState(): TimetableGridScrollState =
    rememberSaveable(saver = TimetableGridScrollState.Saver) {
        TimetableGridScrollState(initialScrollX = 0f, initialScrollY = 0f)
    }

/**
 * Two-axis drag, fling, mouse wheel, and scroll semantics for the grid.
 *
 * `Modifier.scrollable` locks a gesture to one orientation once touch slop is passed, so a
 * diagonal drag is handled here directly instead of by nesting two scrollables.
 */
@Composable
internal fun Modifier.timetableGridScroll(state: TimetableGridScrollState): Modifier {
    val scope = rememberCoroutineScope()
    val decay = rememberSplineBasedDecay<Float>()
    val dispatcher = remember { NestedScrollDispatcher() }
    val connection = remember { object : NestedScrollConnection {} }
    var flingJob by remember { mutableStateOf<Job?>(null) }
    val wheelStepPx = with(LocalDensity.current) { TimetableGridWheelStep.toPx() }
    val draggableState = rememberDraggable2DState { delta ->
        state.dragBy(delta, dispatcher, NestedScrollSource.UserInput)
    }
    return this
        .nestedScroll(connection, dispatcher)
        .semantics {
            horizontalScrollAxisRange = ScrollAxisRange(
                value = state::scrollX,
                maxValue = state::maxScrollX,
            )
            verticalScrollAxisRange = ScrollAxisRange(
                value = state::scrollY,
                maxValue = state::maxScrollY,
            )
            scrollBy { x, y ->
                state.dragBy(Offset(-x, -y), dispatcher, NestedScrollSource.UserInput)
                true
            }
        }
        .pointerInput(state) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent()
                    if (event.type != PointerEventType.Scroll) continue
                    val change = event.changes.first()
                    val consumed = state.dragBy(
                        delta = change.scrollDelta * -wheelStepPx,
                        dispatcher = dispatcher,
                        source = NestedScrollSource.UserInput,
                    )
                    if (consumed != Offset.Zero) change.consume()
                }
            }
        }
        .draggable2D(
            state = draggableState,
            onDragStarted = { flingJob?.cancel() },
            onDragStopped = { velocity ->
                flingJob = scope.launch { state.fling(velocity, decay, dispatcher) }
            },
        )
}

/** Distance one wheel notch travels; wheel deltas arrive in notches rather than pixels. */
private val TimetableGridWheelStep = 48.dp
