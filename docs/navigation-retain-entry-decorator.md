# Entry retention (RetainNavEntryDecorator)

We want values such as a screen's graph — `retain { screenGraphFactory.createTimetableScreenGraph() }` — to live **exactly as long as the screen's `NavEntry` stays on the back stack**. Navigation3 ships no integration with Compose's `retain {}` API, so we wire one up ourselves with a custom `NavEntryDecorator`.

## How: a per-entry RetainScope via a decorator

Following the integration shown in the official [Navigation 3 retain recipe](https://developer.android.com/guide/navigation/navigation-3/recipes/retain), `RetainNavEntryDecorator` keeps **one `RetainedValuesStoreRegistry` for the whole `NavDisplay`** and supplies a **per-key store** to each `NavEntry`. Retained values then live as long as their entry is on the back stack and are discarded when the entry is popped (no leak) — i.e. they are **synchronized with the `NavEntry` lifecycle**.

```kotlin
@Composable
fun <T : Any> retainNavEntryDecorator(): NavEntryDecorator<T> {
    val registry = retainRetainedValuesStoreRegistry()              // one per NavDisplay (retained)
    return remember(registry) {
        NavEntryDecorator(
            onPop = { contentKey -> registry.clearChild(contentKey) }, // discard on pop
            decorate = { entry ->
                registry.LocalRetainedValuesStoreProvider(entry.contentKey) { entry.Content() }
            },
        )
    }
}
```

The registry is created **outside** the `decorate` lambda (which runs per entry), so all entries share one registry while each gets a distinct per-key store.

## One call site per entry

Every decorator between `RetainNavEntryDecorator` and the entry content must call `entry.Content()` from a single call site for the entry's whole life. A decorator that picks between two call sites — an `if` around `entry.Content()` whose condition depends on the scene the entry currently sits in, such as `LocalListDetailSceneScope` — moves the content to a new position the moment a detail pane opens beside a list. Compose discards the old position, and every `remember` and `retain` below it is lost.

Retention does not cover that move. The store retains exiting values only while its own provider has left the composition, and the provider stays composed here, so the values are retired instead of kept. Branch on the entry's metadata, which is fixed for the entry, and vary what the single branch passes to its content.

Related: [Root NavEntry emulation (RootSceneStrategy)](./navigation-predictive-back-tabs.md) · [ScreenContext](./screen-context.md)
