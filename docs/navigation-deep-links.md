# Deep links (DeepLinkEffect)

A deep link is a navigation request arriving from outside the app's own UI — today an Android intent fired by the home-screen widget; the wasmJs URL entry is a future second emitter into the same flow. The design has three parts: a URI scheme, a buffering store, and one consuming effect.

## URI scheme

| URI | Destination |
| --- | --- |
| `droidkaigi://session/{id}` | Session detail for the `TimetableItemId` `{id}` |

`MainActivity` declares the matching `VIEW`/`BROWSABLE` intent-filter. It runs as `singleTask`, so a link tapped while the app is alive brings the existing task forward through `onNewIntent` instead of stacking a second activity.

## DeepLinkStore and DeepLinkEffect

Platform entry points parse their own input and emit a platform-neutral `DeepLink` into `DeepLinkStore` (`core:common`); the activity performs no navigation itself. The store buffers, so a link submitted before the first composition is not lost:

```kotlin
// MainActivity — both the cold-start intent and onNewIntent go through here.
private fun submitDeepLink(intent: Intent) {
    intent.toDeepLink()?.let(appGraph.deepLinkStore::submit)
}
```

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

`resolveDeepLink(link, backStack)` decides how a link lands; the rule is pure and unit-tested:

- **Cold start** — the stack still holds a single entry (the launch has not navigated yet, including a dev build sitting on its server-select override): the stack is **replaced** with `[TimetableNavKey, TimetableItemDetailNavKey(id)]`, so back from the detail lands on the timetable.
- **Warm** — any deeper stack: the detail is **pushed**; existing history stays intact.

A single-entry stack is the cold-start signal rather than an intent flag, so the rule stays platform-neutral and the dev server-select launch yields to the link for that launch.

## Widget trigger

The favorites widget deep-links only from a **live favorited session row** during the conference: each live schedule row (and the small widget's live band when the slot holds exactly one session) carries `droidkaigi://session/{id}`. A shared slot leaves the session choice open, so it — like every other state — launches the app plainly at its start destination.

Related: [Navigation overview](./navigation.md) · [Navigator](./navigation-navigator.md)
