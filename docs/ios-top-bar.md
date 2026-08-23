# iOS top bar

The top bar on iOS is drawn by Compose Multiplatform — `KaigiTopAppBar` and `KaigiLargeTopAppBar` in `:core:ui` — not by a native `UINavigationBar`. The root tab bar is the one native surface on iOS; see [Liquid Glass tab bar](./ios-liquid-glass.md). This page states why the top bar stays Compose and the one condition that would reopen the question.

## Why the tab bar's technique does not carry over

The tab bar is native because its state is small and shared: one `RootTab?` enum, identical for every screen and bridged once through `RootTabNavigator` — see the [state bridge](./ios-liquid-glass.md#state-bridge). What the theme still decides reaches it as a separate `RootTabBarPalette` (the accent colour and whether the scheme is dark), published by `RootTabBarAppearance` — see the [theme bridge](./ios-liquid-glass.md#theme-bridge) — and it too is one value for the whole app. A top bar's state is per-screen, and it is neither small nor uniform.

## Per-screen top bar state

The production screens render this today; the dev-only `DebugScreen` and `SoilErrorsScreen` use a Material3 `TopAppBar` directly.

| Screen | Bar | Title | Leading control | Actions | Chrome sharing the band |
| --- | --- | --- | --- | --- | --- |
| Timetable | `KaigiTopAppBar` | static | none | search (navigates), grid toggle (to the presenter) | the day picker (`DayTabRow`) |
| Favorites, Event map | `KaigiTopAppBar` | static | none | none | none |
| About | `KaigiTopAppBar` | from `UiState` | none | none | none |
| Contributors, Sponsors, Staff, Licenses | `KaigiLargeTopAppBar` | static | back | none | none; the bar accepts a `scrollBehavior`, no screen passes one |
| Session detail | `KaigiTopAppBar` | empty | back or close | none | the headline continues the bar's surface |

## What a native bar would have to receive

A native top bar cannot be expressed as one small model — a title, a back flag and a list of actions — the way the tab bar is:

- **Actions are sub-models, not a flag.** Each action carries an icon whose Material `ImageVector` does not cross Swift Export — the tab bar already meets this by mapping each destination to an SF Symbol name on the Swift side — plus a content description, an enabled state, and a callback that does per-screen work. Each action is its own bridged value with a reverse call channel, and the set tracks the screen graph rather than sealing one component.
- **The leading control is not a boolean.** It is one of none, back, or close, and it changes with runtime state: the session detail shows a close control in the list-detail pane and a back arrow otherwise.
- **The title can be dynamic.** About reads its bar title from its `UiState`.
- **The band is shared with hand-drawn chrome.** The Timetable day picker sits on the bar's surface, and the session detail headline continues it. A native bar draws its own material, so this seam would split mid-surface.

## Scroll-driven behavior

The behaviors that would justify a native bar — the large-title collapse and the scroll-edge effect — read a `UIScrollView` the system owns. The content here is Compose, which is invisible to UIKit, so a native bar sits static. This is the same limitation that leaves `UITabBarController.tabBarMinimizeBehavior` inert for the tab bar, and it holds for the [per-tab embedding](./ios-liquid-glass.md#alternative-one-compose-instance-per-tab) as well.

## Decision

The top bar stays Compose. Its state is per-screen and grows with the screen graph, so bridging it would turn the interop surface from a seam around one component into a mirror of the screens; the behaviors a native bar would add cannot function over Compose content; and giving the top bar to the system would split the hand-drawn `SketchShape` language further than the tab bar already does.

## When to revisit

This changes if iOS moves to per-tab stacks, the route costed out under [one Compose instance per tab](./ios-liquid-glass.md#alternative-one-compose-instance-per-tab) and settled alongside the tab-switching semantics in [Root tab bar](./navigation-root-tab-bar.md). A `UINavigationController` owning each tab's stack draws the top bar as a side effect, so the question becomes which bar content to hand that controller, not whether to build the bridge from nothing. The decision belongs there, and not before.

Related: [iOS overview](./ios.md) · [Liquid Glass tab bar](./ios-liquid-glass.md) · [Root tab bar](./navigation-root-tab-bar.md) · [Swift ↔ Kotlin interop](./ios-interop.md)
