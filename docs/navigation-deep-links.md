# Deep links (DeepLinkEffect)

A deep link is a navigation request arriving from outside the app's own UI — today an Android intent fired by the home-screen widget; the wasmJs URL entry is a future second emitter into the same flow. The design has three parts: a URI scheme, a buffering store, and one consuming effect.

## URI scheme

| URI | Destination |
| --- | --- |
| `droidkaigi2026://session/{id}` | Session detail for the `TimetableItemId` `{id}` |

`MainActivity` declares the matching `VIEW`/`BROWSABLE` intent-filter. It runs as `singleTask`, so a link tapped while the app is alive brings the existing task forward through `onNewIntent` instead of stacking a second activity.

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
    backStack = backStack,
    logger = uiGraph.logger,
    onNavigate = uiGraph.appNavigator::goTo,
)
```

## Back-stack synthesis

`DeepLinkEffect` holds each link until `TimetableNavKey` is on the stack, then `resolveDeepLink(link, backStack)` decides how it lands; the rule is pure and unit-tested:

- **Cold start** — the stack still holds a single entry (the launch has not navigated yet): the stack is **replaced** with `[TimetableNavKey, TimetableItemDetailNavKey(id)]`, so back from the detail lands on the timetable.
- **Warm** — any deeper stack: the detail is **pushed**; existing history stays intact.

A single-entry stack is the cold-start signal rather than an intent flag, so the rule stays platform-neutral. Waiting for the timetable root lets a dev build run its server-select flow first — that flow is what restores the persisted server environment, and the picker auto-skips when the preference says so — and the deep link then arrives as a push above the restored environment's timetable.

## Widget trigger

The favorites widget deep-links only from a **live favorited session row** during the conference: each live schedule row (and the small widget's live band when the slot holds exactly one session) carries `droidkaigi2026://session/{id}`. A shared slot leaves the session choice open, so it — like every other state — launches the app plainly at its start destination.

Related: [Navigation overview](./navigation.md) · [Navigator](./navigation-navigator.md)
