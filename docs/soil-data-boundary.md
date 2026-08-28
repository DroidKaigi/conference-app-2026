# SoilDataBoundary

`SoilDataBoundary` separates data fetching from UI. Inside its content lambda every reply is guaranteed available, so the UI never deals with loading or error states directly.

```kotlin
SoilDataBoundary(
    state1 = rememberQuery(screenContext.timetableQueryKey),
    state2 = rememberSubscription(screenContext.favoriteTimetableIdsSubscriptionKey),
) { timetable, favoriteIds ->
    // both values are non-null here
}
```

## Composition

It is a thin composition of three soil-reacty primitives — `ErrorBoundary` (fatal / load failure → full-screen fallback) wrapping `Suspense` (loading fallback) wrapping `Await` (renders content once every reply is available):

```kotlin
context(_: SoilDataContext)
@Composable
fun <T> SoilDataBoundary(
    state: DataModel<T>,
    modifier: Modifier = Modifier,
    fallback: SoilFallback = SoilFallbackDefaults.default(),
    content: @Composable (T) -> Unit,
) {
    ErrorBoundary(
        fallback = fallback.errorFallback,
        onReset = rememberQueriesErrorReset(), // retry: resume every failed query (soil-query-compose util)
        modifier = modifier,
    ) {
        Suspense(fallback = fallback.suspenseFallback) {
            Await(state = state, content = content)
        }
    }
}
```

- Single- and two-source overloads; add higher-arity variants as screens need them.
- It is **gated to a `SoilDataContext`** (a context parameter), so it can only appear where data reading is sanctioned — the compiler rejects stray data boundaries in presenters or plain composables. [`ScreenContext`](./screen-context.md) extends `SoilDataContext`, so every screen root qualifies; the app shell's `AppGraph` also implements it so the theme subscription renders through the same boundary.

## Customizing the fallback screens

The loading and error UI plug in through the `fallback: SoilFallback` parameter. The default (`SoilFallbackDefaults.default()`) shows a centered `CircularProgressIndicator` while loading and a "Failed to load" message with the localized `AppError` category and a retry button on error; a screen that wants its own look passes `SoilFallbackDefaults.custom(...)`:

```kotlin
SoilDataBoundary(
    state1 = rememberQuery(screenContext.timetableQueryKey),
    fallback = SoilFallbackDefaults.custom(
        suspenseFallback = { TimetableSuspenseFallback() },
        errorFallback = { TimetableErrorFallback() },
    ),
) { timetable -> … }

// The error view declares the context parameter and reads the cause from it.
context(errorContext: SoilErrorContext)
@Composable
fun TimetableErrorFallback(modifier: Modifier = Modifier) {
    Text("Failed to load: ${errorContext.errorBoundaryContext.err.message}", modifier = modifier)
}
```

Each fallback composes inside a `Box` (the lambdas are `BoxScope` extensions) and runs with a context parameter — `SoilSuspenseContext` for loading, `SoilErrorContext` for errors, which exposes soil-reacty's `ErrorBoundaryContext` (the thrown error). Declaring that context on your own fallback composable, as above, is how it reaches the error details.

Related: [Soil keys](./soil-keys.md) · [Error handling](./error-handling.md)
