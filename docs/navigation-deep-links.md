# Deep links (DeepLinkEffect)

A deep link is a navigation request arriving from outside the app's own UI — today an Android intent fired by the home-screen widget; the wasmJs URL entry is a future second emitter into the same flow. The design has three parts: a URI scheme, a buffering store, and one consuming effect.

## URI scheme

The path names the surfaces a reader would have walked through, so the synthesized history follows from the URI alone:

| URI | Destination |
| --- | --- |
| `droidkaigi2026://session/{id}` | Session detail for the `TimetableItemId` `{id}` |
| `droidkaigi2026://favorites` | The favorites tab |
| `droidkaigi2026://favorites/session/{id}` | Session detail reached through the favorites surface |
| `droidkaigi2026://about` | The about tab |
| `droidkaigi2026://timetable/day1` \| `.../day2` | The timetable tab showing that conference day |

`DeepLink.parse` (`core:common`) is the grammar's one home; platform entry points hand it the raw URL string. `MainActivity` declares the matching `VIEW`/`BROWSABLE` intent-filter. It runs as `singleTask`, so a link tapped while the app is alive brings the existing task forward through `onNewIntent` instead of stacking a second activity.

## DeepLinkStore and DeepLinkEffect

Platform entry points parse their own input and emit a platform-neutral `DeepLink` into `DeepLinkStore` (`core:common`); the activity performs no navigation itself. The store buffers, so a link submitted before the first composition is not lost:

```kotlin
// MainActivity — both the cold-start intent and onNewIntent go through here.
private fun submitDeepLink(intent: Intent) {
    intent.toDeepLink()?.let(appGraph.deepLinkStore::submit)
}
```

The launch intent is consumed only on fresh creation (`savedInstanceState == null`): recreation — a configuration change or process death — redelivers the task's original intent while the restored back stack already reflects the link.

## Launch scenarios

`MainActivity` is the app's only activity and runs as `singleTask`, so every launch path resolves to the one existing task:

| Scenario | Behavior | Guaranteed by |
| --- | --- | --- |
| Widget tap while the app is backgrounded | The existing task comes to the front; the link arrives through `onNewIntent` as a push | `singleTask`; Glance builds the `PendingIntent` with `FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT` and no activity flags, and the session URI keeps each row's `PendingIntent` distinct |
| Deep link fired while another app is foreground | The system hops to the app's own task; the other app's task is untouched | `singleTask` |
| Widget tap while the app sits in a split-screen pair | The same task routing applies; the system offers no second-instance affordance | `singleTask`; no `documentLaunchMode` or multi-instance attribute is set |
| Launcher or recents relaunch after process death of a deep-linked task | The app boots through its normal flow; the task's original `VIEW` intent is not resubmitted | the `savedInstanceState` guard |

`DeepLinkEffect` (app-shared, wired in `KaigiApp` beside [`NavigatorEffect`](./navigation-navigator.md)) is the single consumer. Warm navigation goes through the navigator as a lambda, keeping `Navigator` types out of composable signatures per [enforcement](./enforcement.md):

```kotlin
DeepLinkEffect(
    deepLinkStore = uiGraph.deepLinkStore,
    timetableDayRequestStore = uiGraph.timetableDayRequestStore,
    backStack = backStack,
    logger = uiGraph.logger,
    onNavigate = uiGraph.appNavigator::moveToTop,
)
```

## Back-stack synthesis

`DeepLinkEffect` holds each link until no `StartupNavKey` remains on the stack, then lands it; `buildSyntheticBackStack(link)` is pure and unit-tested — `[TimetableNavKey, TimetableItemDetailNavKey(id)]` for a plain session link, `[TimetableNavKey, FavoritesNavKey]` for the favorites tab, `[TimetableNavKey, AboutNavKey]` for the about tab, `[TimetableNavKey, FavoritesNavKey, TimetableItemDetailNavKey(id)]` for a favorites session link, `[TimetableNavKey]` for a timetable day link:

- **Cold start** — the stack still holds a single entry (the launch has not navigated yet): the stack is **replaced** with the synthetic stack, so back walks the named surfaces down to the timetable. On a two-pane scene the favorites list stays on screen beside the detail.
- **Warm** — any deeper stack keeps its history: the synthetic root is already beneath every stack, and the remaining entries land through **move-to-top**, so the same order forms on top — a favorites session link raises (or pushes) the favorites tab, then the detail above it. A synthetic stack that holds the root alone names the root itself, which move-to-top raises the same way the tab bar does.

A link that names a state of its destination rather than a destination of its own carries that state beside the stack: `DeepLink.Timetable` holds the day segment its URI names, and `DeepLinkEffect` writes the day into `TimetableDayRequestStore` (`:feature:sessions`, an `AppScope` singleton) before it navigates. The store buffers one request and hands it over once, so the timetable presenter applies it to `selectedDay` whenever the screen composes and a day the reader picks afterwards stands.

A single-entry stack is the cold-start signal rather than an intent flag, so the rule stays platform-neutral. `StartupNavKey` marks destinations that host startup flow and leave the stack when done — the dev server picker implements it, restores the persisted server environment (auto-skipping when the preference says so), and replaces itself with the timetable; the deep link resolves only after that.

## Widget trigger

The favorites widget routes taps by its state:

| Widget state | Tap target | URI |
| --- | --- | --- |
| Schedule / live — a session row, or the small widget's live band holding exactly one session | That session through the favorites surface | `droidkaigi2026://favorites/session/{id}` |
| Schedule — the widget background | The favorites tab | `droidkaigi2026://favorites` |
| Post-conference — any tap | The about tab | `droidkaigi2026://about` |
| Empty, today done — any tap | The timetable tab on the widget's day | `droidkaigi2026://timetable/day1` \| `.../day2` |
| Day wrap-up — any tap | The timetable tab on Day 2 | `droidkaigi2026://timetable/day2` |
| Countdown, event day — any tap | Plain launch at the start destination | — |

A shared slot leaves the session choice open, so its live band launches like the background.

Related: [Navigation overview](./navigation.md) · [Navigator](./navigation-navigator.md)
