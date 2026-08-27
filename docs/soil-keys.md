# Soil keys

Soil replaces repository classes. Each piece of server or database state is a typed **key**; the `SwrClient` handles runtime caching. Declarations live in `:core:model` (as `typealias`), implementations (`Default*Key`) in `:core:data`.

Which key to reach for:

| Key | Use it for | Example |
| --- | --- | --- |
| `QueryKey<T>` | A **one-shot read** — fetch a value once and cache it | fetching the timetable from the API |
| `SubscriptionKey<T>` | A **continuously updated read** — observe a `Flow` and re-render on every emission | favorite ids or the theme stored in DataStore |
| `MutationKey<R, V>` | A **write** — update a store or call an API, observing success/failure | toggling a favorite, updating the profile |

```kotlin
// :core:model — the contract a feature depends on
typealias TimetableQueryKey = QueryKey<Timetable>
typealias FavoriteTimetableIdsSubscriptionKey = SubscriptionKey<PersistentSet<TimetableItemId>>
typealias FavoriteTimetableItemIdMutationKey = MutationKey<Unit, TimetableItemId>
```

```kotlin
// :core:data — implementation, bound into the graph
@ContributesBinding(AppScope::class)
@Inject
class DefaultTimetableQueryKey(
    private val api: TimetableApi,
    private val fileStorage: ServerEnvironmentScopedFileStorage,
) : TimetableQueryKey by buildPersistedQueryKey(
        id = SoilIds.timetableQuery, // generated; no hand-written namespace string
        persistKey = "timetable",    // stable, explicit persisted-cache identity
        fileStorage = fileStorage,
        fetchResponse = { api.getTimetable() },
        transformToDomainModel = { response -> Timetable(items = response.toTimetableItems().toPersistentList()) },
    )
```

- Features depend only on the `typealias` (model), never on the `Default*Key` impls.
- File placement: **one file per key**, on both sides — the contract file (named after the typealias) carries the `typealias` plus any dedicated input/result data classes, and implementations are one file per `Default*Key`. A shared index file would be a merge point every pull request touches.
- Heavy shaping of the raw API response into the model happens inside `fetch` (the data layer), not the presenter.
- A **derived read** reuses a shared key rather than adding a new one: the detail screen selects its single item from the shared timetable cache with `rememberQuery(key, select = { it.items.first { … } })`, so it never refetches the whole timetable. An independent per-id key is warranted only when a dedicated detail API exists.
- A key that genuinely needs a runtime argument cannot be an app-wide singleton. Bind it into that screen's **`@GraphExtension` scope** instead; the screen's graph factory supplies the id. See [ScreenContext](./screen-context.md).

## Generated key ids (`SoilIds`)

Every runtime id (`QueryId`/`SubscriptionId`/`MutationId`) is **generated**, never hand-written. A KSP processor (`SoilIdsGenerator` in `:tools:ksp-processor`, wired via `kspCommonMainMetadata` on `:core:model` and `:core:data`) scans the compiling module for `typealias`es whose expanded type is a Soil key and emits a `SoilIds` object into that module. The namespace of each id is the **contract typealias FQN** — read at compile time, so the wasm `qualifiedName` limitation never applies, and identity stays stable across impl renames/swaps.

```kotlin
// generated into :core:model
public object SoilIds {
  public val timetableQuery: QueryId<Timetable> =
      QueryId("io.github.droidkaigi.confsched.core.model.TimetableQueryKey")
  public val favoriteTimetableIdsSubscription: SubscriptionId<PersistentSet<TimetableItemId>> = …
  // mutations become a factory taking the per-screen MutationTag (see below)
  public fun favoriteTimetableItemIdMutation(extraTag: MutationTag): MutationId<Unit, TimetableItemId> =
      MutationId("io.github.droidkaigi.confsched.core.model.FavoriteTimetableItemIdMutationKey", extraTag.value)
}
```

- Property name = the typealias simple name minus a trailing `Key`, lowerCamel (`TimetableQueryKey` → `timetableQuery`).
- Query/Subscription ids are `val`s; a **Mutation** id is a `fun` that takes the per-screen `MutationTag` and bakes it into the `MutationId` — this satisfies the `MutationKeyMustCarryTag` checker (the tag flows into the `id` argument via the factory call).
- **Cross-key side effects share the constant.** Because the id is a shared symbol, one key's `mutate` can invalidate another's cache without duplicating a namespace string:

```kotlin
// after toggling a favorite, invalidate the timetable query so the grid re-reads
queryClient.invalidateQueriesBy(SoilIds.timetableQuery)
```

## Composing data sources

When one piece of data is needed in more than one shape, or one fetch needs another's result:

1. **Same data, another view** — reuse the shared key with `rememberQuery(key, select = …)`; do not add a key.
2. **A fetch that needs another request's result** — chain the requests inside one key's `fetchResponse` (the API layer owns request construction, so the chain is `api.a()` + `api.b(…)`); the aggregate response persists as one payload and fails atomically.

Queries deliberately do not read other queries: a cross-query dependency hides one cache's freshness rules inside another. If a chain's intermediate result also needs its own screen, give it its own key and fetch it independently — the API layer keeps the request construction shared.

## `typealias` and Metro binding

Each key is a `typealias`, which erases to its underlying type — it is not a new type. So if two keys resolve to the **same** underlying type, `@ContributesBinding` registers two bindings for that one type and Metro fails the build with a **duplicate-binding** error (distinct `typealias` *names* do not help — only the underlying type is compared):

```kotlin
// both erase to MutationKey<Unit, TimetableItemId> → duplicate binding
typealias UpdateFavoriteSessionMutationKey = MutationKey<Unit, TimetableItemId>
typealias MarkSessionSeenMutationKey       = MutationKey<Unit, TimetableItemId>
```

Queries rarely hit this (each `QueryKey<T>` carries a distinct payload). Mutations are more prone to it, because the empty cases collapse to `Unit` — every no-argument action is `MutationKey<Unit, Unit>`.

When two keys genuinely need the same type, disambiguate with a Metro **qualifier** (`@Qualifier` / `@Named`, from `dev.zacsweers.metro`): annotate each `Default*Key` with its own qualifier and `@ContributesBinding` propagates it to the generated binding, so the two become distinct graph slots — the `typealias` and the `… by buildMutationKey(...)` delegation stay intact. (This is separate from the Soil `MutationId`/`QueryId`, which disambiguates the runtime **cache** entry, not the DI binding.)

**Every key in the group carries one, and so does every injection site.** Qualifying only one of a colliding pair compiles and says nothing: the site that asks without a qualifier resolves to whichever binding is unqualified, so a screen reads the key its parameter is not named after. With all of them qualified there is no unqualified binding to land on, and omitting the annotation is a missing-binding error instead:

```kotlin
@Qualifier annotation class ClockOverlayEnabled

@Inject
@ClockOverlayEnabled
@ContributesBinding(DebugScreenScope::class)
class DefaultClockOverlayEnabledMutationKey(…) : ClockOverlayEnabledMutationKey by buildMutationKey(…)

class DebugPresenterContext(
    @ClockOverlayEnabled val clockOverlayEnabledMutationKey: ClockOverlayEnabledMutationKey,
)
```

**The qualifier is declared in the same file as the key it qualifies**, so the pair is read together rather than looked up. Which module that file sits in follows the key: one belonging to a single feature lives in that feature's module (`ClockOverlayEnabledMutationKey` is `:feature:debug`, and being dev-only it is one that must not sit anywhere a production build can reach), and any other lives in `:core:model` beside the rest of the contracts. `:core:model` carries no DI dependency today, so the first qualifier declared there adds the Metro plugin to its build script.

`:core:common` is the wrong home even though it already has Metro and every feature sees it: a qualifier names one key, and the module holds the scaffolding every screen is built from. It would also put the qualifier a module away from the key, which is what declaring them together avoids.

The qualifier stays a fact about the graph rather than about the value: `MutationKey<Unit, Boolean>` describes both keys correctly, and they are already told apart by name at the declaration, by parameter name at the injection site, and by `MutationId` in the cache. Only binding resolution cannot see the difference, which is the layer the qualifier addresses.

## Mutation input & result types

`MutationKey<Result, Variable>` takes two type arguments — mind the order: **`Result` first** (what `mutate` returns), **`Variable` second** (the input). What goes in each:

| Values | Input (`Variable`) | Result (`Result`) |
| --- | --- | --- |
| 0 | `Unit` | `Unit` |
| 1 | the domain type directly | the domain type directly |
| 2+ | a dedicated `…Input` data class | a dedicated `…Result` data class |

Wrap **only** when there are genuinely two or more values, to avoid positional `Pair` / `Triple` arguments. The key, its input class, and its result class share the **same verb-led stem** and all live in `:core:model` next to the `typealias`, so they read as one set:

```kotlin
// :core:model — a mutation that needs several inputs and returns several values
typealias SubmitFeedbackMutationKey =
    MutationKey<SubmitFeedbackResult, SubmitFeedbackInput>

data class SubmitFeedbackInput(val sessionId: TimetableItemId, val comment: String, val rating: Int)
data class SubmitFeedbackResult(val id: FeedbackId, val createdAt: Instant)
```

A single-value mutation needs no wrappers — the existing `FavoriteTimetableItemIdMutationKey = MutationKey<Unit, TimetableItemId>` (toggle a favorite by id; returns nothing, success observed via the favorites subscription) is the minimal shape.

## Per-screen mutation tag isolation

Soil keeps mutation state in the `SwrClient` keyed by `MutationId(namespace, *tags)`. If two screens resolve the **same** `MutationKey` binding, they share one cache slot — residual `Success`/`Error` state left by one screen makes the other screen's success/error effects mis-fire on entry. Query and Subscription caches are shared by design (a read is a read), but mutation state is per-interaction and must be **per-screen**.

The isolation is mechanical, not conventional: every `MutationKey` implementation takes a `MutationTag` (a `value class` in `:core:model`) and bakes it into the `MutationId`; each per-screen `@GraphExtension` `@Provides` its own screen-type tag. The mutation-key binding moves from `AppScope` to the per-screen scopes:

```kotlin
// :core:data — one impl, bound into each screen scope that toggles favorites
@Inject
@ContributesBinding(TimetableScreenScope::class)
@ContributesBinding(TimetableItemDetailScreenScope::class)
@ContributesBinding(FavoritesScreenScope::class)
@ContributesBinding(SearchScreenScope::class)
class DefaultFavoriteTimetableItemIdMutationKey(
    extraTag: MutationTag,
    private val store: FavoritesStore,
) : FavoriteTimetableItemIdMutationKey by buildMutationKey(
    id = SoilIds.favoriteTimetableItemIdMutation(extraTag), // generated factory bakes the tag in
    mutate = { id -> store.toggle(id) },
)
```

Forgetting either half is a compile error: the `MutationKeyMustCarryTag` FIR checker rejects any `MutationKey` implementation that lacks a `MutationTag` constructor parameter or does not pass it into the `MutationId` — see [Enforcement](./enforcement.md).

Related: [SoilDataBoundary](./soil-data-boundary.md) (consuming keys at the screen root) · [Soil mutation](./soil-mutation.md) · [Soil persistence](./soil-persistence.md)
