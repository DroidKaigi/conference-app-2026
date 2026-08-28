# iOS favorites widget

The home-screen widget is a WidgetKit extension, `FavoritesWidget`, that links none of the exported Kotlin. The app writes it a snapshot of everything it draws into a shared App Group container, and the extension renders from that file alone. The Android widget is the design reference; both compute the same state from the same rules.

## Snapshot contract

The app writes `favorites-widget-snapshot.json` into the App Group container. `FavoritesWidgetSnapshot.kt` and `FavoritesWidgetSnapshotStore.kt` (`:app-ios-kotlin`) own the writer; `FavoritesWidgetSnapshot.swift` owns the reader. The write is staged and moved into place atomically, because a widget refresh may land mid-write.

| Field | Contents |
| --- | --- |
| `schemaVersion` | The shape's version; a file carrying any other value is discarded |
| `clockOffsetSeconds` | The debug clock offset, so the widget draws the same instant the app does |
| `conference` | The conference window as epoch seconds, its UTC offset, the two day dates, and each day's last session end |
| `colors` | `surface`, `onSurface`, `onSurfaceVariant`, `primary`, `onPrimary` as `#AARRGGBB`, resolved from the selected color scheme, plus `isDark` |
| `favorites` | Every favorited session of both days: id, conference day, both title languages, wall-clock and epoch start and end, and its room chip label and colors |

Everything the extension draws must reach it through this file. The extension resolves no color, no room theme and no locale-dependent title from shared code — it holds only the copy in its own string catalog.

The snapshot must stay renderable on its own: a field the extension needs is added to the writer and the reader together, and `schemaVersion` is raised whenever a reader written against the older shape would misread the file.

## App Group and schema version

`app-ios/project.yml` is the single source for the App Group identifier and the schema version. It stamps both into every bundle that takes part in the exchange — the app, the extension and the test bundle — and aliases the same node into both targets' generated entitlements. `FavoritesWidgetContract` reads them back from the running bundle, and the app hands them to Kotlin through `KaigiAppHost`. Neither value may be written as a literal anywhere else.

Raising the schema version is therefore one edit. Because the app and the extension ship together, a mismatch can only mean a file left behind by an earlier install, which is exactly what the reader rejects.

## Timeline and boundaries

`FavoritesWidgetState.swift` ports `computeFavoritesWidgetState`, `nextFavoritesWidgetBoundary` and `toFavoritesWidgetRows` from `core/model/.../FavoritesWidgetState.kt`. The two must stay in step; `FavoritesWidgetStateTests` mirrors the Kotlin test cases over a fixture snapshot.

The states are countdown, event day, then per conference day — day wrap-up once Day 1's programme is over, otherwise schedule, today done or empty — and post-conference. A day's programme is over once its last session has ended, which the conference block's per-day session end states; a day with no timetable item leaves that end unknown and is never over. Everything the state needs about the current day — which favorites fall on it, how many fall on the other one — comes from each favorite's own day, because the extension never sees the timetable.

A boundary is the earliest instant after now at which the state changes without new input: the next conference midnight before the event day, Day 1's midnight on the event day, and during a day the earliest of that day's favorited starts and ends, the day's last session end, the day's own midnight and the conference end. After the conference there is none.

`FavoritesWidgetProvider` walks those boundaries and emits one entry at each, capped so WidgetKit is never handed an unbounded timeline; the reload policy picks the schedule up from the last entry when the cap is reached, and is `never` otherwise. Entry dates are system time while the state is computed at the snapshot's own clock, which the debug offset separates.

## Refresh

The app collects `KaigiAppHost.favoritesWidgetSnapshots`, a flow over the favorites, the color scheme and the clock offset. Each emission follows a completed write, so the reload it triggers always finds a file the extension can already read:

```swift
for try await _ in host.favoritesWidgetSnapshots.asAsyncSequence() {
    WidgetCenter.shared.reloadAllTimelines()
}
```

An emission stands for a completed write, so a write the App Group container cannot take emits nothing and the writer reports the missing container through `KaigiLogger` once. Nothing else may reload the timelines: a reload that does not follow a write shows the same content again.

## Taps

Each state sets a `widgetURL`, and a live session row sets its own `Link`. The URLs are the shared deep-link scheme, which `onOpenURL` hands to `KaigiAppHost.submitDeepLink(url:)`. For the state-to-URL table, see [Deep links](./navigation-deep-links.md).

## Frame and artwork

The frame is `SketchFrame`, a port of `SketchRoundRectShape` carrying the seeds, reference sizes and wobble parameters `app-android/.../widget/SketchBorderBitmap.kt` pins. Its noise is specified so a second implementation reproduces it exactly, so both platforms draw the same outline; a change to either side must be mirrored.

The symbol mark and the mascots are `WidgetArtworks`, the stroke outlines of the Android vector drawables under `app-android/src/main/res/drawable`, tinted at each call site. They are transcribed from those files and must be retranscribed when the drawables change.

Related: [iOS overview](./ios.md) · [Deep links](./navigation-deep-links.md) · [Swift ↔ Kotlin interop](./ios-interop.md)
