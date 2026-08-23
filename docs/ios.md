# iOS overview

iOS runs the shared Compose Multiplatform UI for every screen, with one native exception: the root tab bar, the system `UITabBar` rendering the Liquid Glass design. The top bar stays Compose; [iOS top bar](./ios-top-bar.md) records why.

- The Swift implementation is minimal and the app runs on a Compose Multiplatform base. `KaigiApp` runs on a `ComposeUIViewController`, and every screen uses the shared CMP UI. Per-screen SwiftUI with KMP Presenter integration is not carried forward.
- The one native exception is the root tab bar, layered over that view controller; every screen — including screen-transition chrome — is drawn by CMP. Navigation3 owns the back stack across all platforms, and iOS mirrors the tab-related part of that state into the native bar.

## Relationship to navigation

Navigation3 owns the back stack on all four platforms. iOS reflects that state in the native tab bar through `RootTabNavigator`, a model in `:app-shared` free of UI types: Kotlin publishes the current tab (`null` hides the bar), and native tab taps come back as selections that turn into the same back-stack command the Compose bar issues on the other platforms.

## Swift ↔ Kotlin interop

The Swift ↔ Kotlin boundary stays small, around the tab bar: Swift calls Kotlin (the tab selection, the theme the bar draws itself with, the view-controller factory) through the Swift-exported `AppShared` module, and Kotlin reaches Apple frameworks through Swift Package Import. Both are experimental as of 2026.

For details, see [Swift ↔ Kotlin interop](./ios-interop.md).

## Targets

iOS targets iosArm64 + iosSimulatorArm64. The exported `AppShared` module links Metro, Soil, both Navigation3 groups and runtime-retain as native klibs, with screens staying entirely in `commonMain`. iosX64 (Intel simulator) is out of scope because CMP `compose.ui`, `runtime-retain`, and the Navigation3 groups are not published for the deprecated target — which is also why an `xcodebuild` must name a concrete arm64 simulator rather than a `generic/platform=iOS Simulator` destination.

The native Liquid Glass tab bar composites over the CMP backdrop on iOS 26. For the embedding shape, see [CMP on iOS (embedding)](./ios-cmp-embedding.md); for the tab bar, see [Liquid Glass tab bar](./ios-liquid-glass.md).

Related: [CMP on iOS (embedding)](./ios-cmp-embedding.md) · [Liquid Glass tab bar](./ios-liquid-glass.md) · [iOS top bar](./ios-top-bar.md) · [Swift ↔ Kotlin interop](./ios-interop.md)
