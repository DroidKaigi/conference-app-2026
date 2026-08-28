# Presenter unit tests (Molecule)

A Composable presenter is a **function that returns the state of a screen**: it takes actions and data in and returns the `UiState` the screen renders, with no UI attached. It cannot be called like a plain function, though — `remember` and the effect APIs only work inside a running Compose runtime. Fortunately, combining a few tools removes that obstacle: Molecule drives the Compose runtime (no UI needed) and exposes the returned `UiState` as a `Flow`, and Turbine asserts its emissions — send an action, await the next state, assert.

## Tools

- **Molecule** (`app.cash.molecule:molecule-runtime`) … drives a `@Composable` and observes its return value (UiState) as a `Flow`.
- **A [test graph](./testing-graph.md)** … the `<Feature>PresenterContext` is resolved from DI rather than constructed by hand, so a new dependency on it reaches every test through one fake.
- **Turbine** … asserts `Flow` emissions.
- **kotlinx-coroutines-test** (`runTest` / `TestDispatcher`) … drives virtual time.

## Basic recipe

The Molecule scaffolding (drive the runtime, provide the `SwrClient`, supply the `PresenterContext`, assert with Turbine) is identical for every presenter test, so it is written **once**: `runPresenterTest` in `:core:testing` (`core/testing/src/commonMain/…/PresenterTest.kt`):

```kotlin
// core:testing — shared scaffolding, written once. The test drives the screen from both ends:
// `send` plays the Root's role (gated by ScreenContext), `uiStates` observes the presenter's
// return value, and `results` observes the ActionResults the presenter emits (captured by an
// ActionResultEffect composed with a test ScreenContext — the Root's role again).
class PresenterTestScope<A, R, S>(
    private val screenContext: ScreenContext,
    private val screenChannel: ScreenChannel<A, R>,
    val uiStates: ReceiveTurbine<S>,
    val results: ReceiveTurbine<R>,
) {
    fun send(action: A) = context(screenContext) { screenChannel.send(action) }
}

fun <C : PresenterContext, A, R, S> runPresenterTest(
    presenterContext: C,
    presenter: @Composable context(C) (ScreenChannel<A, R>) -> S,
    validate: suspend PresenterTestScope<A, R, S>.() -> Unit,
) = runTest {
    val screenContext = object : ScreenContext {
        override val logger: KaigiLogger = presenterContext.logger
    }
    val screenChannel = ScreenChannel<A, R>()
    val results = Channel<R>(Channel.BUFFERED)
    val uiStateFlow = moleculeFlow(RecompositionMode.Immediate) { // drive the Compose runtime
        val client = SwrCachePlus(backgroundScope)          // a real SwrCachePlus works under Molecule (no TestSwrClientPlus needed)
        compositionLocalProviderWithReturnValue(
            LocalSwrClient provides client,
            LocalQueryClient provides client,
            LocalMutationClient provides client,
            LocalSubscriptionClient provides client,
        ) {
            context(screenContext) {                        // play the Root: capture the result side
                ActionResultEffect(screenChannel) { results.send(it) }
            }
            context(presenterContext) {                     // supply the PresenterContext (do not use with=receiver)
                presenter(screenChannel)
            }
        }
    }
    turbineScope {                                          // one flat scope for both turbines
        // Molecule re-emits on every recomposition; equal consecutive states are noise to a test.
        val uiStates = uiStateFlow.distinctUntilChanged().testIn(backgroundScope)
        val resultsTurbine = results.receiveAsFlow().testIn(backgroundScope)
        PresenterTestScope(screenContext, screenChannel, uiStates, resultsTurbine).validate()
        uiStates.cancelAndIgnoreRemainingEvents()
        resultsTurbine.cancelAndIgnoreRemainingEvents()
    }
}

// CompositionLocalProvider's content is @Composable () -> Unit, so the UiState cannot be
// returned through it directly — provide the locals via Composer.startProviders/endProviders
// instead, which lets content return a value.
@OptIn(InternalComposeApi::class)
@Composable
fun <T> compositionLocalProviderWithReturnValue(
    vararg values: ProvidedValue<*>,
    content: @Composable () -> T,
): T {
    currentComposer.startProviders(values)
    val result = content()
    currentComposer.endProviders()
    return result
}
```

Each feature test is then just the resolved context, the presenter call, and the actions/assertions:

```kotlin
private val graph = createGraph<TimetableScreenTestGraph>()

@Test
fun bookmark_event_marks_session() = runPresenterTest(
    presenterContext = graph.presenterContext,
    presenter = { channel -> timetableScreenPresenter(channel, fakeTimetable) },
) {
    assertEquals(expectedInitial, uiStates.awaitItem())
    send(TimetableScreenAction.Bookmark(id))
    assertEquals(expectedBookmarked, uiStates.awaitItem())
    assertEquals(TimetableItemId("d1a"), graph.favoriteMutationKey.invocations.receive())
}
```

Both ends of the `ScreenChannel` are gated: `send` requires a [`ScreenContext`](./screen-context.md), and results can only be read through an `ActionResultEffect` (also `ScreenContext`-gated; the channels themselves are `internal` to `:core:common`). The scaffold therefore plays the Root's role with a test `ScreenContext` — `send` opens it around `screenChannel.send`, and an `ActionResultEffect` composed next to the presenter captures the emitted results into the `results` turbine.

The presenter has no loading-to-content transition: the Root's [`SoilDataBoundary`](./soil-data-boundary.md) and `rememberQuery` handle loading, and the presenter receives an already-loaded `Timetable`. Presenter tests assert state transitions only.

## Points of craft

1. **`RecompositionMode.Immediate`**: executes recomposition immediately without waiting for the frame clock (the crux of the test).
2. **Supplying dependencies**: the presenter requires `rememberMutation` / `SwrClientProvider` plus a `PresenterContext` (supplied via `context(presenterContext){}`). Inside the test composition, supply **a real `SwrCachePlus(backgroundScope)` (no `TestSwrClientPlus` needed) plus `runTest` virtual time plus the `PresenterContext` the test graph resolves**.
3. **Verifying success/failure**: to fire a `MutationSuccessEffect` / `MutationErrorEffect`, **arm the fake key with `failWith(…)` or leave it succeeding** and **assert the emission on the `results` turbine** → even the one-off wiring can be verified.
4. **Input/output**: feed actions via the scope's `send(action)` to drive UiState transitions. There are two assertion surfaces beyond `uiStates` — what the action reached in the data layer, on the fake's `invocations`, and what the presenter emitted back, on the `results` turbine. An action that only mutates state reaches neither.
5. **Multiplatform**: the presenter is pure logic in commonMain, so **running it on the JVM is sufficient** (rendering-free logic verification does not need any UI target).

## Position in the test pyramid

- **Presenter test (Molecule)**: fast verification of state and logic without rendering UI.
- **Screen test ([Robot](./testing-robot.md) + Roborazzi)**: rendering + screenshot + behavior.

The presenter layer sits beneath Robot/Roborazzi: cheap to run, so state transitions are covered here, leaving the screen layer to rendering concerns.

Related: [Testing overview](./testing.md) · [Test graph (TestingScope)](./testing-graph.md) · [Robot pattern tests](./testing-robot.md)
