# Liquid Glass tab bar

On iOS the only native UI is the root tab bar: a `UITabBar`, the system's own bar, which renders Liquid Glass on iOS 26. Every screen behind it is drawn by Compose Multiplatform. Navigation logic (the back stack) is owned by Navigation3 on every platform, and iOS mirrors the tab-related part of that state into the native bar.

The bar is the system component rather than a reimplementation, so its material, metrics, selection animation and accessibility traits are UIKit's. Each OS version draws the bar its own way — iOS 26 the floating glass platter, earlier versions the standard bar — so the app carries no availability branch and no fallback material of its own.

## Overlay embedding

The bar is chrome only. `UITabBar` sizes itself, so the view occupies the bar's area and nothing more, bottom-aligned over the full-screen `ComposeUIViewController`:

```swift
ZStack(alignment: .bottom) {
    KaigiAppView(host: host)                            // full-screen ComposeUIViewController
    RootTabBarView(                                     // only the bar's own area
        currentTab: host.currentTab.asAsyncSequence().map { $0?.tab },
        palette: host.tabBarPalette.asAsyncSequence(),
        select: host.selectTab(tab:)
    )
}
.ignoresSafeArea()
```

That is what keeps the embedding free of container plumbing: no `hitTest` override, no background clearing, and no view controller around the bar. A tap inside the bar is claimed by `UITabBar` and never reaches the Compose layer; every point outside it — the margins beside the platter and the strip below it included — belongs to Compose. When a detail screen hides the bar, its area returns to Compose as well.

The Compose view controller is a sibling of the bar rather than its parent, so it does not inherit a bottom inset: root destinations reserve the room themselves, and a scrollable adds `KaigiNavigationBarDefaults.occupiedHeight` to its bottom content padding, which covers the height UIKit gives the bar.

Scroll-driven bar behaviors are unavailable: Compose scrolling is invisible to UIKit, so `UITabBarController.tabBarMinimizeBehavior` has nothing to observe and the bar stays fully visible. The glass itself still refracts and tints the scrolling Compose content behind it.

`TabView` cannot take this role. SwiftUI's `TabView` insists on hosting full-screen content: it paints an opaque background over the Compose layer and claims every touch outside the bar, and `allowsHitTesting(false)` on its content does not restore the fall-through. A `UITabBarController` can be overlaid instead, but only inside a container view whose `hitTest` returns a hit when it lands in the bar's subtree and `nil` everywhere else; it buys `tabBarMinimizeBehavior` and `bottomAccessory`, neither of which applies here, at the price of that container and a placeholder view controller per tab.

## State bridge

`RootTab` and `RootTabNavigator` (`:app-shared`, free of UI types) form the model both sides share:

- **Kotlin → Swift**: `RootTabNavigator.currentTab: StateFlow<RootTab?>` drives the bar's selection; `null` (a non-tab entry on top, that is, a detail screen) hides the bar. Swift reaches it as `KaigiAppHost.currentTab: Flow<RootTabSelection?>`, the enum wrapped in a class because [Swift Export cannot carry an enum through a `Flow`](./ios-interop.md).
- **Swift → Kotlin**: tab taps call `select(tab)`; `IosTabBarSyncEffect` (inside `KaigiApp`) turns each selection into `AppNavigator.moveToTop(tab.key)` — the same command the Compose bar issues on the other platforms.

`RootTab.label` names each destination on both sides: the Compose bar gives it to its icon as a content description, and the native bar shows it under the icon as the tab's title. The icon has no shared form — Compose names a destination with a Material `ImageVector` and UIKit with an SF Symbol — so the symbol names live in the Swift bar.

## Theme bridge

The bar draws its own material, so the theme reaches it as the little it still decides. `RootTabBarAppearance` publishes a `RootTabBarPalette` — the theme's accent as sRGB ARGB, and whether the scheme in force is a dark one — which the bar takes as its `tintColor` and its `overrideUserInterfaceStyle`. The style override is what keeps the bar with the app rather than the device: the app picks its scheme itself, and two of the five are dark.

The palette carries nothing further because nothing further has an effect: on the iOS 26 platter, `unselectedItemTintColor` and `UITabBarAppearance.selectionIndicatorTintColor` are overridden by the material.

`RootTabSceneDecorator` (the Compose bottom bar) is not applied on iOS; the native bar replaces it. `rememberRootTabSceneDecorator` returns `null` when `currentPlatform` is `TargetPlatform.Ios`. For the tab-switching semantics, see [Root tab bar](./navigation-root-tab-bar.md).

## Cross-renderer compositing

The Liquid Glass bar refracts and tints the CMP (Skia/Metal) backdrop behind it: the glass samples the live Compose Metal layer, so as content scrolls the glass tracks the CMP colors underneath. This compositing requires iOS 26.

![iOS 26 Liquid Glass refracting and tinting the CMP (Skia/Metal) content behind the top bar and bottom tab capsule](./images/ios-liquid-glass-cmp-backdrop.png)

The top bar picks up the red card behind it and is tinted red, while the bottom floating tab capsule refracts the text behind it through glass. Captured on the iOS 26.2 simulator in light mode.

CMP requires `CADisableMinimumFrameDurationOnPhone=true` in `Info.plist`, or it aborts on launch at `PlistSanityCheck`.

## Alternative: one Compose instance per tab

An alternative embedding gives each tab of a `UITabBarController` its own `ComposeUIViewController` as real content instead of the overlay. It buys native tab-switch transitions and automatic safe-area propagation, and requires:

- **Per-tab back stacks.** The single `NavBackStack` splits into one stack per tab. `AppNavigator.moveToTop` reordering — the cross-platform tab-switch model — no longer applies on iOS, and navigator commands must route to the selected tab's stack.
- **Per-tab state plumbing.** Back-stack persistence, `RetainNavEntryDecorator` scopes, and the snackbar and overlay hosts multiply per stack, and back semantics diverge from the other platforms: back no longer falls through stashed tabs, so the [`RootSceneStrategy`](./navigation-predictive-back-tabs.md) model does not carry over.
- **Deep-link routing.** A deep link resolves to a tab first, then pushes onto that tab's stack.

Scroll-driven bar behaviors remain unavailable in this embedding too — the content inside each tab is still Compose, not a native `UIScrollView`. The overlay embedding is the default because it keeps the navigation model identical across platforms and requires no change to the shared navigation code.

Related: [iOS overview](./ios.md) · [iOS top bar](./ios-top-bar.md) · [Root tab bar](./navigation-root-tab-bar.md) · [Navigation](./navigation.md)
