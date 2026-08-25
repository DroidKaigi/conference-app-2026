# DroidKaigi/conference-app-2026 Architecture Documentation

A set of documents covering the architecture and implementation policy of the DroidKaigi 2026 conference app.

## Document map

- [Module structure](./project-structure.md) … the `:core:*` / `:feature:*` / `:app-*` / tooling module groups and what each contains
- [Platforms & modules](./platforms-and-modules.md) … the four platforms and what belongs in each module
- [Architecture overview](./architecture-overview.md) … how the app works end to end, from the platform entry point to a rendered, interactive screen
- [Error handling](./error-handling.md) … the two-layer error model and how one-off events (navigation, messages) flow through Soil-derived effects and the ScreenChannel
- [Presenter performance](./presenter-performance.md) … dividing responsibilities by pushing heavy computation into the data layer
- [Enforcement](./enforcement.md) … making invalid code uncompilable via types and FIR checkers
- [Naming review](./naming-review.md) … the naming rules a reviewer applies where the compiler cannot: a name states what the value is, a type states how it is represented
- [CompositionLocal review](./compositionlocal-review.md) … the one question that decides whether a value belongs in a `CompositionLocal`, and the seams it reaches through when the answer is no
- [Building a screen](./building-a-screen.md) … implementing one screen end to end using TimetableScreen as an example (steps and checklist)
- [ScreenContext design](./screen-context.md) … concrete class + retain, role-context separation (composition, capability gating)
- [Navigation overview](./navigation.md) … a per-screen Navigator (hand-written) + `@ContributesIntoSet` + KSP-generated [NavKey serializers](./navigation-navkey-serializers.md) for "no central editing, no missed registrations"
- [Soil mutation](./soil-mutation.md) … `mutateAsync` + `MutationSuccessEffect` + failure handling
- [BuildKonfig (build-time values)](./build-config-buildkonfig.md) … exposing build-time values (version and other build state) to common code from a single source
- [iOS overview](./ios.md) … almost full CMP with only the tab bar in Liquid Glass
- [iOS top bar](./ios-top-bar.md) … why the top bar stays Compose while the tab bar is native, and the one condition that would reopen it
- [Logging (Kermit)](./logging.md) … a single AppScope Kermit `Logger` with KMP-native writers per platform (incl. wasmJs=console), no expect/actual
- [Clock (KaigiClock)](./clock.md) … one injected time seam: the system clock in production, a shiftable clock in dev builds, `FakeClock` in tests

