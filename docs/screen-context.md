# ScreenContext design

A screen's dependencies are injected through two **role contexts** carried by context parameters, built on a per-screen Metro graph. This page defines that structure and the rules that keep the two roles apart.

## Overview

- **Two role contexts**, both concrete `@Inject` classes: `ScreenContext` (the Root's dependencies — [QueryKey / SubscriptionKey](./soil-keys.md), etc.) and `PresenterContext` (the presenter's — [MutationKey](./soil-mutation.md), etc.).
- **Every screen uses the same three parts**: a scope marker, a per-screen `@GraphExtension`, and a `@SingleIn` ScreenContext class.
- **ScreenContext holds the PresenterContext as a property** and must never implement it — the roles stay separate types.
- **Input/output is gated by the context types**: actions can only be consumed under `PresenterContext`, results under `ScreenContext`.

## The per-screen structure

Three declarations per screen (there is no "does the screen take a NavKey argument?" fork — keyed screens use the same shape):

1. A **scope marker** in `:core:model` (`sealed interface TimetableScreenScope`) — the lowest layer, referenced by `:core:data` bindings and the feature's extension alike.
2. A **per-screen `@GraphExtension`** in the feature, exposing `val screenContext` (plus `val screenNavigator` where the feature has a Navigator facade).
3. A concrete **`@Inject @SingleIn(<ScreenScope>::class)` ScreenContext class**.

```kotlin
// :core:model — the scope marker
sealed interface TimetableScreenScope

// feature — the per-screen graph extension
@GraphExtension(TimetableScreenScope::class)
interface TimetableScreenGraph {
    val screenContext: TimetableScreenContext
    val screenNavigator: TimetableScreenNavigator

    @GraphExtension.Factory
    @ContributesTo(UiScope::class)
    fun interface Factory {
        fun createTimetableScreenGraph(): TimetableScreenGraph
    }
}

// feature — both role contexts are concrete @Inject classes
@Inject
class TimetablePresenterContext(
    val favoriteTimetableItemIdMutationKey: FavoriteTimetableItemIdMutationKey,
) : PresenterContext

@Inject
@SingleIn(TimetableScreenScope::class)
class TimetableScreenContext(
    val timetableQueryKey: TimetableQueryKey,
    val favoriteTimetableIdsSubscriptionKey: FavoriteTimetableIdsSubscriptionKey,
    val presenterContext: TimetablePresenterContext, // the instance, held as a property
) : ScreenContext
```

## Keeping the roles apart

- **ScreenContext holds the `PresenterContext` instance as a property; it must not also implement `PresenterContext`** (composition, not inheritance). Implementing it would make presenter-only effects compilable throughout the Root — the `ScreenContextMustNotBePresenterContext` checker forbids it.
- The held value is the **instance, not a `Provider`**: a `PresenterContext` is a stateless dependency bag (Soil's mutation state lives in the `SwrClient`, keyed by id), so re-minting it per entry would reset nothing.
- **The context types also unlock the scoped helpers**: helper functions declare a `context(_: ScreenContext)` / `context(_: PresenterContext)` parameter, so they compile only where that context is in scope — `ActionResultEffect` needs a `ScreenContext`, `ActionEffect` a `PresenterContext`.
- **The Root narrow-supplies the presenter context to the presenter call only**: `val uiState = context(screenContext.presenterContext) { xxxScreenPresenter(...) }`, with the Screen call outside the block. The `NoPresenterEffectInScreenRoot` checker seals that block against any other presenter-role call.

## Lifetime and confinement

- The NavEntryProvider `retain`s the **graph** once; because the ScreenContext is `@SingleIn(scope)`, every accessor read returns the same instance for as long as the graph is retained — including across navigation round trips (Timetable → Detail → back).
- `@SingleIn` also **confines**: the ScreenContext is unresolvable from the app and UI graphs — the graph factory is the only path to it. The same holds for the screen-scoped Navigator bindings ([Navigator](./navigation-navigator.md)).

## Factory naming

Every contributed `@GraphExtension.Factory` merges into the `UiGraph`, where no-arg `create()` overloads differing only in return type clash (a Kotlin error). Factory methods therefore carry their screen's name — a uniform rule with no exceptions: `createTimetableScreenGraph()`, `createTimetableItemDetailScreenGraph(id)`, … Relying on signature uniqueness for a bare `create(id)` would break as soon as a second id-taking screen appears.

## Scoping: accessor vs. constructor

- Bindings **exposed as graph accessors** (`screenContext`, `screenNavigator`) are `@SingleIn(scope)` — an accessor must be stable while the graph is retained.
- Bindings **consumed by a constructor** (e.g. the favorite mutation key, bound in `:core:data` with `@ContributesBinding(scope)` and taking the per-screen `MutationTag`) stay **unscoped** — the consuming `val` pins them.

For a keyed screen, the NavKey's id enters the scope through the factory's `@Provides` parameter; it is held on the `@SingleIn` ScreenContext, which uses it to `select` the single item from the shared timetable query (no separate id-derived key). The per-screen `MutationTag` is provided the same way:

```kotlin
@GraphExtension(TimetableItemDetailScreenScope::class)
interface TimetableItemDetailScreenGraph {
    val screenContext: TimetableItemDetailScreenContext

    @Provides
    private fun provideMutationTag(): MutationTag = MutationTag("TimetableItemDetailScreen")

    @GraphExtension.Factory
    @ContributesTo(UiScope::class)
    fun interface Factory {
        fun createTimetableItemDetailScreenGraph(
            @Provides timetableItemId: TimetableItemId,
        ): TimetableItemDetailScreenGraph
    }
}
```

Any dependency that should be confined to a screen's graph is injected the same way — a `@Provides` on the factory (or the graph) or a `@ContributesBinding` into the screen scope; the codebase currently has no instance beyond these.

## NavEntryProvider (both shapes)

The NavEntryProvider injects only the graph `Factory` (plus `AppNavigator` where `back()` is needed), retains the graph once, and reads the accessors:

```kotlin
// (1) No NavKey argument: retain the graph once
entry<TimetableNavKey> {
    val graph = retain { screenGraphFactory.createTimetableScreenGraph() }
    context(graph.screenContext) {
        TimetableScreenRoot(onNavigateToDetail = { graph.screenNavigator.openSessionDetail(it) })
    }
}

// (2) With a NavKey argument: retain the graph per id
entry<TimetableItemDetailNavKey> { key ->
    val graph = retain(key) { screenGraphFactory.createTimetableItemDetailScreenGraph(key.id) }
    context(graph.screenContext) {
        TimetableItemDetailScreenRoot(onNavigateBack = { appNavigator.back(origin = key) })
    }
}
```

## Rejected alternatives

- **Graph-as-ScreenContext** (the graph extension itself implements ScreenContext): reintroduces per-recomposition key churn, and puts the navigator accessor on the ScreenContext — leaking it into the Root, which the `NavigatorConfinedToNavEntry` checker forbids.
- **ScreenContext implementing PresenterContext**: downgrades the type-level ScreenChannel gating to naming conventions — see "Keeping the roles apart".

Related: [Per-screen graphs](./di-screen-graph.md) · [Building a screen](./building-a-screen.md) · [Enforcement](./enforcement.md) · [Architecture overview](./architecture-overview.md)
