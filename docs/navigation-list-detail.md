# List-detail scenes (ListDetailSceneStrategy)

On EXPANDED windows — 840dp wide and up — a list that opens a screen renders as **list-detail**: the list stays visible as one pane while the screen it opened takes the other. The pairs are the Timetable, Favorites and Search with the session detail; About with Settings, Sponsors, Contributors, Staff and Licenses; and the Event map with Stamp collecting. A MEDIUM window is one pane, because the Material-recommended directive splits the layout horizontally only from the expanded width breakpoint. The strategy is the standard Material3 adaptive one, `org.jetbrains.compose.material3.adaptive:adaptive-navigation3`, wrapped as `rememberKaigiListDetailSceneStrategy` (`app-shared/.../KaigiListDetailSceneStrategy.kt`) to carry the app's pane separation and drag behavior, and passed to `NavDisplay`'s `sceneStrategies`:

```kotlin
// rootSceneStrategy is FIRST: see "Ordering" below.
sceneStrategies = listOf(
    rootSceneStrategy,
    rememberLoneListPaneSceneStrategy(),
    rememberKaigiListDetailSceneStrategy(),
    SinglePaneSceneStrategy(),
)
```

The library resolves for every target this app ships: android, jvm (desktop), iosArm64, iosSimulatorArm64, and wasmJs.

## Declaring the panes via entry metadata

To use the strategy, give each `NavEntry` the metadata for the pane it plays — `listPane()` on the list entry, `detailPane()` on the detail entry. The strategy groups adjacent entries that share a scene key into one `ListDetailPaneScaffold`:

```kotlin
// Timetable (the list). It already carries RootSceneStrategy.root() for the home-root predictive-back
// behavior; listPane() is merged in so the same entry is also the list pane.
entry<TimetableNavKey>(metadata = RootSceneStrategy.root() + listPane()) { ... }

// TimetableItemDetail (the detail), shared by the Timetable, Favorites and Search.
entry<TimetableItemDetailNavKey>(metadata = detailPane()) { ... }
```

`listPane()` and `detailPane()` (`core/common`) wrap the library's pane metadata, `listPane()` adding the mark the lone-list strategy below reads. Both return `Map<String, Any>`, so they compose with the existing `RootSceneStrategy.root()` map with a plain `+`.

A list pane may sit above another list pane — Search opens over the Timetable — in which case the scaffold pairs the detail with the topmost list, and back from the detail returns to that list.

Window adaptivity is the library's own concern: `rememberListDetailSceneStrategy` reads the window size internally, collapses to a single pane on compact windows, and then returns `null` so the entry falls through to `SinglePaneSceneStrategy` — no window measurement is needed anywhere in this codebase.

## Strategy order matters

The Timetable entry carries both `RootSceneStrategy.root()` and `listPane()`, so whichever strategy comes first claims it. `rootSceneStrategy` is placed before `listDetailSceneStrategy`: with Timetable on top, back still exits the app ([`RootSceneStrategy`](./navigation-predictive-back-tabs.md)); with a detail on top, it yields and the two-pane scaffold forms. Reversed, one of the two strategies below it claims a lone Timetable entry and derives `previousEntries` from the entries beneath — back from home would reveal a stashed tab instead of exiting.

## A list with nothing open beside it

The scaffold fills every partition the window offers, expanding a detail pane even with no detail entry to put in it, so a list entry on top of the back stack would render at a pane's width against an empty placeholder. `rememberLoneListPaneSceneStrategy` (`core/common`) claims a `listPane()` entry in that position and renders it as a single pane; it comes before the list-detail strategy in `sceneStrategies` for that reason, and after `rootSceneStrategy`, which already claims the Timetable.

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
@Composable
fun paneStartInset(): Dp =
    if (LocalListDetailSceneScope.current != null) LocalPanePartitionSpacerSize.current else 0.dp
```

`paneStartInset()` (`core/ui`) is that gate, and a detail pane applies it to everything it draws: `TimetableItemDetailScreen` passes it to each section as padding or as a `startInset` parameter applied between a background and its content, and the other detail panes fold it into the start of a scrolling container's content padding. `KaigiLargeTopAppBar` applies it to its own back control and title, so a screen that uses that bar covers only its content. Nothing enforces the contract mechanically, and a pane that skips it butts its content against the seam.

A list pane must not take the inset: `LocalListDetailSceneScope` is non-null for both panes, but only the detail pane's start edge meets the seam.

## Window insets on an offset pane

`WindowInsets` describe the whole window, so both panes read the same edge values whatever their position. A pane held clear of a window edge by the pane beside it — the detail pane's start, the list pane's end — would still pad its content for that edge's system-bar or display-cutout inset, opening a gap against the seam.

Each entry names the edge it does not touch in its metadata, and one decorator consumes it:

```kotlin
entry<TimetableNavKey>(
    metadata = RootSceneStrategy.root() + listPane() +
        consumeListDetailPaneInsets(WindowInsetsSides.End),
) { ... }

entry<TimetableItemDetailNavKey>(
    metadata = detailPane() + consumeListDetailPaneInsets(WindowInsetsSides.Start),
) { ... }
```

`rememberListDetailPaneInsetsNavEntryDecorator`, in `KaigiNavDisplay`'s `entryDecorators`, wraps the entry in `Modifier.consumeWindowInsets` for the named edge behind the same `LocalListDetailSceneScope` gate as the pane separation: it applies only while the two-pane scaffold is live, so a single-pane screen keeps the full window insets. `Scaffold` and `KaigiTopAppBar` subtract consumed insets, so the screens read their padding unchanged.

## Resizing the split

The scaffold shows a drag handle on the seam (`paneExpansionDragHandle`). Releasing a drag settles the split onto the nearest of three anchors — list at `PaneMinWidth`, 50:50, and detail at `PaneMinWidth` — so the edge anchors are the panes' resting minimum widths. While the pointer is down, `PaneExpansionDragBounds` rubber-bands the seam past the edge anchors through the state's `consumeDragDelta` hook: resistance grows with distance and movement stops entirely at `PaneMaxOvershoot`, while deltas back toward the bounds pass through untouched so the release animation is unaffected.

The handle slot also draws a narrow gradient band on the list side of the seam. Panes are clipped to their own bounds, so the detail pane cannot cast a real shadow across the seam; the band, drawn from the handle slot placed above both panes, stands in for that elevation cue.

All of this lives in `app-shared/.../KaigiListDetailSceneStrategy.kt`; the geometry assumes left-to-right layout, which holds for every locale the app ships.

Related: [Root tab bar (RootTabSceneDecorator)](./navigation-root-tab-bar.md) · [Root NavEntry emulation (RootSceneStrategy)](./navigation-predictive-back-tabs.md)
