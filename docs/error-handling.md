# Error handling

Errors and one-off events (a completed navigation, a transient message, a success notification) are delivered through Compose effects derived from Soil state, never through flags on `UiState`. This page states the rules; for where these pieces sit in the request flow, see [Architecture overview](./architecture-overview.md).

## Two-layer error model

Errors split by severity, and each layer has one sink:

- **Fatal / load failure — full screen.** A query or subscription that cannot produce content surfaces through the `ErrorBoundary` of [`SoilDataBoundary`](./soil-data-boundary.md), which replaces the whole screen with a fallback. The fallback is replaceable through `SoilFallback` (`SoilFallbackDefaults.default()` or `custom()`).
- **Transient / partial, plus success notifications — snackbar.** A write that fails, or a partial failure that leaves content on screen, and success notifications go to a `SnackbarHostState`. The host is per screen: `rememberSnackbarNavEntryDecorator` (`:core:common`) creates one `SnackbarHostState` per navigation entry, wraps the entry in a `Scaffold`, and exposes it through `LocalSnackbarHostState`. A Root reads that local rather than nesting its own `Scaffold`. There is no app-global host.

Separately from both layers, `SoilErrorMonitor` (`:core:common`) renders an app-global overlay listing the errors Soil reports. Its production binding is a no-op; `:feature:debug` replaces it, so the overlay is a development aid rather than a user-facing sink — see [Debugging](./debugging.md).

`Throwable.toUserMessage()` (`:core:common`) classifies the exception into an `AppError` and wraps it in a `UserMessage`; `SnackbarHostState.showSnackbar(UserMessage)` (`:core:ui`) resolves the localized text for that category. Exception messages never reach the user.

## One-off events

One-off events are Compose effects that observe Soil mutation state and fire exactly once per transition. Deriving a one-off from a boolean on `UiState` is forbidden.

- `MutationSuccessEffect(mutation) { … }` fires on success, keyed by the reply timestamp so it runs once per completed mutation.
- `MutationErrorEffect(mutation) { … }` fires on failure under the same one-shot keying.

Both live in `:core:common`.

## ScreenChannel

A screen's actions (input) and results (output) are aggregated into one `ScreenChannel<Action, ActionResult>`. It is backed by two kotlinx `Channel`s at `Channel.BUFFERED` capacity, so every event is delivered exactly once with no replay — the shape a one-off needs.

The Root creates the channel with `retainScreenChannel()`. It is retained rather than remembered so that buffered, not-yet-consumed events survive transient destruction of the entry.

`ActionEffect` and `ActionResultEffect` both hold to three rules, so a screen never has to restate them:

- **One handler per event.** Each event is handled in a child coroutine of the effect, so an event whose handler is still running — a mutation in flight, a snackbar still on screen — does not hold up the events behind it. Handlers therefore overlap: a screen that requires one write to land before the next orders them itself.
- **The handler runs against the latest composition.** The effect is keyed by the channel alone, so it outlives the composition that launched it; it reads the handler through `rememberUpdatedState`. Without that, a handler would close over the values of the composition it was launched in and write back state the screen has already moved past.
- **A handler that throws is reported, not fatal.** The failure goes to `KaigiLogger.error`, which forwards it to `CrashReporter` as a non-fatal report, and the screen keeps consuming. A failure here is a defect in the screen rather than a condition the user can act on; what the user should see goes through the snackbar sink above.

Each end is gated by a context parameter, so using the wrong end from the wrong layer is an ordinary compile error:

| Operation | Direction | Context | Form |
| --- | --- | --- | --- |
| `send(action)` | Root produces | `ScreenContext` | non-suspend |
| `ActionEffect(channel) { … }` | Presenter consumes | `PresenterContext` | effect |
| `emit(result)` | Presenter produces | `PresenterContext` | suspend |
| `ActionResultEffect(channel) { … }` | Root consumes | `ScreenContext` | effect |

`emit` is `suspend` and `PresenterContext`-gated, so it can only run inside an effect, never from the composition body. This holds because the presenter declares only `PresenterContext` and never `ScreenContext` (the role-context separation in [ScreenContext](./screen-context.md), enforced by the `PresenterMustNotDeclareScreenContextChecker` FIR checker — see [enforcement](./enforcement.md)).

```kotlin
// Presenter (context: PresenterContext)
ActionEffect(screenChannel) { action -> if (action is Save) mutation.mutateAsync(action.value) }
MutationSuccessEffect(mutation) { screenChannel.emit(NavigateToCard) }
MutationErrorEffect(mutation) { screenChannel.emit(ShowMessage(it.toUserMessage())) }

// Root (context: ScreenContext)
ActionResultEffect(screenChannel) { result ->
    when (result) {
        NavigateToCard -> onSaved()
        is ShowMessage -> snackbarHostState.showSnackbar(result.message)
    }
}
// UI input: onSaveClick = { screenChannel.send(Save(value)) }
```

A read-only screen with no action and no one-off needs no `ScreenChannel`.

## Navigation-only clicks

A click that only navigates (tap → open a screen, nothing else) does not travel through the presenter. The Root forwards its `on*` navigation lambda straight into the Screen's matching parameter, and the Screen invokes it from the control the user taps. Routing such a click through the channel only to emit it back unchanged is rejected by the `NoForwardOnlyActionChecker` FIR checker.

Navigation that a presenter originates does flow back as a result: a mutation succeeds → `emit(NavigateToCard)` → `ActionResultEffect` → the Root's navigation lambda.

## Duplicate-navigation idempotency

A successful navigation is protected against duplicate firing at three layers:

1. The channel consumes each result exactly once.
2. `MutationSuccessEffect` fires once per successful mutation, so a single navigation emits a single result.
3. `NavigatorEffect` skips a `Push` whose key already sits on top of the back stack — see [back stack guards](./navigation-navigator.md#back-stack-guards).

Related: [Architecture overview](./architecture-overview.md) · [Soil mutation](./soil-mutation.md) · [SoilDataBoundary](./soil-data-boundary.md) · [Building a screen](./building-a-screen.md)
