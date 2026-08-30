# List-detail scenes (ListDetailSceneStrategy)

On EXPANDED windows — 840dp wide and up — the session screens render as **list-detail**: the timetable stays visible as the list pane while the session detail opens beside it. A MEDIUM window is one pane, because the Material-recommended directive splits the layout horizontally only from the expanded width breakpoint. The strategy is the standard Material3 adaptive one, `org.jetbrains.compose.material3.adaptive:adaptive-navigation3`, wrapped as `rememberKaigiListDetailSceneStrategy` (`app-shared/.../KaigiListDetailSceneStrategy.kt`) to carry the app's pane separation and drag behavior, and passed to `NavDisplay`'s `sceneStrategies`:

```kotlin
// rootSceneStrategy is FIRST: see "Ordering" below.
sceneStrategies = listOf(rootSceneStrategy, rememberKaigiListDetailSceneStrategy(), SinglePaneSceneStrategy())
```

The library resolves for every target this app ships: android, jvm (desktop), iosArm64, iosSimulatorArm64, and wasmJs.

## Declaring the panes via entry metadata

To use the strategy, give each `NavEntry` the metadata for the pane it plays — `listPane()` on the list entry, `detailPane()` on the detail entry. The strategy groups adjacent entries that share a scene key into one `ListDetailPaneScaffold`:

```kotlin
// Timetable (the list). It already carries RootSceneStrategy.root() for the home-root predictive-back
// behavior; listPane() is merged in so the same entry is also the list pane.
entry<TimetableNavKey>(metadata = RootSceneStrategy.root() + ListDetailSceneStrategy.listPane()) { ... }

// TimetableItemDetail (the detail).
entry<TimetableItemDetailNavKey>(metadata = ListDetailSceneStrategy.detailPane()) { ... }
```

`ListDetailSceneStrategy.listPane()` / `.detailPane()` return `Map<String, Any>` metadata, so they compose with the existing `RootSceneStrategy.root()` map with a plain `+`.

Window adaptivity is the library's own concern: `rememberListDetailSceneStrategy` reads the window size internally, collapses to a single pane on compact windows, and then returns `null` so the entry falls through to `SinglePaneSceneStrategy` — no window measurement is needed anywhere in this codebase.

## Strategy order matters

The Timetable entry carries both `RootSceneStrategy.root()` and `listPane()`, so whichever strategy comes first claims it. `rootSceneStrategy` is placed before `listDetailSceneStrategy`: with Timetable on top, back still exits the app ([`RootSceneStrategy`](./navigation-predictive-back-tabs.md)); with a detail on top, it yields and the two-pane scaffold forms. Reversed, the library claims a lone list entry on wide windows (a single-list scene with a detail placeholder) and derives `previousEntries` from the entries beneath — back from home would reveal a stashed tab instead of exiting.

## The adaptive back icon

The same detail screen appears in two situations with different affordances:

- **Single pane** (compact window, or reached by navigation) — the top bar shows **←**: "go back".
- **Detail pane** (beside the list) — **←** reads wrongly because the list is still on screen; the natural affordance is **✕**: "close this pane".

Both perform the same action (pop the detail's `NavKey`); only the icon differs, and the screen — pure `commonMain` — must not read a window size.

The library exposes exactly the signal we need to content rendered inside its scaffold: `LocalListDetailSceneScope`, a `CompositionLocal<ListDetailSceneScope?>`. It is non-null **only** while an entry is composed inside the list-detail scaffold; because the strategy yields to single-pane rendering whenever `paneCount <= 1`, a non-null scope reliably means "I am the detail pane beside a list". The detail screen reads it directly:

```kotlin
IconButton(onClick = onBack) { // the same pop, whichever icon shows
    if (LocalListDetailSceneScope.current != null) {
        Icon(Icons.Filled.Close, contentDescription = "Close")
    } else {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
    }
}
```

So the adaptive icon comes entirely from a library-provided local.

## Pane separation (LocalPanePartitionSpacerSize)

The scaffold's own gutter is closed: `rememberKaigiListDetailSceneStrategy` copies the Material directive with `horizontalPartitionSpacerSize = 0.dp`, so the two panes' backgrounds meet at the seam with no stripe of window background between them. The separation belongs to the panes instead, and travels as a `CompositionLocal`:

- **Provider** — `KaigiNavDisplay` provides `LocalPanePartitionSpacerSize` (`core/ui`) around the whole `NavDisplay`. The value is the width a pane must reserve toward the shared boundary.
- **Consumer** — a pane applies the value only while it is actually beside another pane, using the same `LocalListDetailSceneScope` gate as the adaptive icon. The inset must sit **inside** any background that runs to the pane edge; padding applied outside the background reopens the stripe the directive closed.

```kotlin
val paneSpacerInset = if (LocalListDetailSceneScope.current != null) {
    LocalPanePartitionSpacerSize.current
} else {
    0.dp
}
```

`TimetableItemDetailScreen` is the reference consumer: its top-bar close button and summary card take the inset as padding, and `TimetableItemDetailHeadline` takes it as a `startInset` parameter applied between its background and its content. A new detail pane must follow the same pattern — nothing enforces the contract mechanically, and a pane that skips it butts its content against the seam.

## Window insets on an offset pane

`WindowInsets` describe the whole window, so both panes read the same edge values whatever their position. A pane held clear of a window edge by the pane beside it — the detail pane's start, the list pane's end — would still pad its content for that edge's system-bar or display-cutout inset, opening a gap against the seam.

Each entry names the edge it does not touch in its metadata, and one decorator consumes it:

```kotlin
entry<TimetableNavKey>(
    metadata = RootSceneStrategy.root() + ListDetailSceneStrategy.listPane() +
        consumeListDetailPaneInsets(WindowInsetsSides.End),
) { ... }

entry<TimetableItemDetailNavKey>(
    metadata = ListDetailSceneStrategy.detailPane() + consumeListDetailPaneInsets(WindowInsetsSides.Start),
) { ... }
```

`rememberListDetailPaneInsetsNavEntryDecorator`, in `KaigiNavDisplay`'s `entryDecorators`, wraps the entry in `Modifier.consumeWindowInsets` for the named edge behind the same `LocalListDetailSceneScope` gate as the pane separation: it applies only while the two-pane scaffold is live, so a single-pane screen keeps the full window insets. `Scaffold` and `KaigiTopAppBar` subtract consumed insets, so the screens read their padding unchanged.

## Resizing the split

The scaffold shows a drag handle on the seam (`paneExpansionDragHandle`). Releasing a drag settles the split onto the nearest of three anchors — list at `PaneMinWidth`, 50:50, and detail at `PaneMinWidth` — so the edge anchors are the panes' resting minimum widths. While the pointer is down, `PaneExpansionDragBounds` rubber-bands the seam past the edge anchors through the state's `consumeDragDelta` hook: resistance grows with distance and movement stops entirely at `PaneMaxOvershoot`, while deltas back toward the bounds pass through untouched so the release animation is unaffected.

The handle slot also draws a narrow gradient band on the list side of the seam. Panes are clipped to their own bounds, so the detail pane cannot cast a real shadow across the seam; the band, drawn from the handle slot placed above both panes, stands in for that elevation cue.

All of this lives in `app-shared/.../KaigiListDetailSceneStrategy.kt`; the geometry assumes left-to-right layout, which holds for every locale the app ships.

Related: [Root tab bar (RootTabSceneDecorator)](./navigation-root-tab-bar.md) · [Root NavEntry emulation (RootSceneStrategy)](./navigation-predictive-back-tabs.md)
