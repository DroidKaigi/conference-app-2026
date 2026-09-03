# Debugging

Debug-only tooling lives in a dedicated **`:feature:debug`** module so it is wired into development builds only and never ships in production. The module carries two things: the in-app debug screen, and the JetWhale agent that connects the running app to a desktop debugger. Keeping it in its own feature module means the rest of the app has no dependency on debug code, and the tooling can use the same DI / navigation seams as a normal feature. The module is not depended on unconditionally: every platform excludes it by default and adds it back only for a build it recognises as a development build — the Android dev product flavor, `:app-desktop:run`, `:app-web:wasmJsBrowserDevelopmentRun`, and an Xcode Debug build of the iOS app. Because both the screen and the agent reach the graph purely through Metro aggregation, dropping the dependency drops them with no other code changes. For the per-platform wiring and how to verify the exclusion, see [Keeping dev-only code out of release](./build-dev-only-exclusion.md).

The `DebugScreen` is wired like any other screen (`DebugNavKey` + `DebugScreenContext` + `DebugScreenGraph` + `DebugNavEntryProvider`). It shows the real app version via `BuildConfigProvider`, offers a **Clear persisted data** action, controls the Soil error overlay (a toggle plus a live error count opening `SoilErrorsScreen`), and carries a **Clock** section that shifts the app's time — see [Clock (KaigiClock)](./clock.md) — and a **Device tilt** section showing the live pitch and roll. Production logging is [Kermit-based](./logging.md); HTTP traffic is inspected through JetWhale rather than an in-app log.

## Clear persisted data

The menu's **Clear persisted data** button wipes the app's persisted (and in-memory session) state in one call, so a tester can return the app to a clean slate. It is aggregated by `PersistedDataResetter` (`:core:data`, `AppScope`):

- **Preferences DataStore** (theme) — `ThemeStore.clear()` (`dataStore.edit { it.clear() }`, clearing the whole preferences file).
- **Favorites** — `FavoritesStore.clear()` (clearing the favorites preferences file).
- **Blobs** (e.g. the profile image) — `FileStorage.clear()`, implemented per platform: delete the blob directory on JVM/Android/iOS, and `IDBObjectStore.clear()` on the Web (wasmJs) IndexedDB actual.

`DebugPresenterContext` holds the `PersistedDataResetter`. The button sends a `ClearData` action, and the presenter runs `clearAll()` inside `ActionEffect`, flipping the state that shows a "cleared ✓" confirmation.

## JetWhale agent

[JetWhale](https://kitakkun.github.io/JetWhale/) is a desktop debugger that a running app connects to over a WebSocket. Dev builds attach three of its plugins: the **Nav3 Navigator** (the live Navigation 3 back stack, with push / pop / reorder driven from the host), the **Network Inspector** (HTTP transactions and response mocking), and the **Compose Semantics Inspector** (the Compose node tree, with each node's own semantics actions invocable). Alongside them runs **DroidKaigi 2026**, the app's own plugin, which carries the debug controls the host reaches: the clock (see [Clock (KaigiClock)](./clock.md)) and a device tilt override that pins the tilt-driven effects at a chosen angle. The host exposes all four over an MCP server as well, so an AI agent can drive the app through the same operations.

### Running it

Install the host from the [JetWhale releases page](https://github.com/kitakkun/JetWhale/releases) and launch it; it listens for debuggees on port **5080**. Run a dev build, and the app appears as a session in the host during startup, before the first composition. Android devices and emulators reach the host through `adb reverse tcp:5080 tcp:5080`, which the host wires up automatically unless ADB auto port mapping is turned off in its settings.

| Target | Reaches the host | Compose Semantics Inspector |
| --- | --- | --- |
| Android (dev flavor) | via `adb reverse`, automatic | available |
| Desktop | via `localhost` | available |
| Web | via `localhost` | unavailable |
| iOS Simulator | via `localhost` | unavailable |
| iOS device | via the build machine's address over `wss`, baked in at compile time | unavailable |

The Nav3 Navigator and the Network Inspector work on every target. The Compose Semantics Inspector needs a probe that finds the platform's Compose roots, and JetWhale ships one for Android and desktop only; elsewhere it reports an empty tree.

`JetWhaleDebugger` lists `ws("localhost", 5080)` first and `buildMachineWss(5443)` after it. Loopback covers every target that shares the host machine's network namespace; a physical iPhone does not, so it falls through to the second candidate, which the `com.kitakkun.jetwhale.agent` Gradle plugin rewrites at compile time into this machine's own address — see [Baking in the build machine's address](https://kitakkun.github.io/JetWhale/guide/getting-started#baking-in-the-build-machine-s-address-no-browse).

That plugin is applied to `:feature:debug` alone. The address it bakes in is a compile task input, so the module recompiles whenever the developer moves between networks and those compilations never come from a shared build cache; applying it project-wide would spread that cost across every module.

A physical iPhone also needs `NSLocalNetworkUsageDescription` in the iOS app's `Info.plist` so iOS permits local-network access. The upstream [Secure connections (wss)](https://kitakkun.github.io/JetWhale/guide/getting-started#secure-connections-wss) guide covers the certificate side.

### How it is wired

Production code sees three seams in `:core:common`. `AppInitializer` is the process-wide startup hook; the other two are `fun interface`s with a `@Composable operator fun invoke`, so an injected instance is called exactly like the plain composable effects beside it:

```kotlin
fun interface AppInitializer {
    fun initialize()
}

fun interface BackStackDebuggingEffect {
    @Composable
    operator fun invoke(backStack: NavBackStack<NavKey>)
}

fun interface SemanticsDebuggingEffect {
    @Composable
    operator fun invoke()
}
```

Each platform entry point runs the initializer once, before any UI exists — `Application.onCreate` on Android, `main()` on desktop and Web, the `App` struct's `init` on iOS:

```kotlin
appGraph.appInitializer.initialize()
```

`KaigiApp` composes the two effects next to the `NavDisplay`:

```kotlin
uiGraph.backStackDebuggingEffect(backStack)
uiGraph.semanticsDebuggingEffect()
```

The bindings are `NoopAppInitializer`, `NoopBackStackDebuggingEffect` and `NoopSemanticsDebuggingEffect` unless `:feature:debug` is on the classpath, in which case one `JetWhaleDebugger` replaces all three through Metro — the same aggregation the debug screen uses.

`JetWhaleDebugger` is an `AppScope` singleton whose `initialize()` opens the connection and attaches the plugins. Each plugin reaches the app through a seam that already exists:

- **Nav3** — `Nav3KeyCodec.openPolymorphic` takes the merged `SerializersModule` the back stack is already built from, so the host can decode and construct every `NavKey` in the app. See [NavKey serializer aggregation](./navigation-navkey-serializers.md).
- **Network** — the interceptor attaches to the injected `HttpClient` through Ktor's `HttpSend`, leaving the `:core:data` provider untouched. `HttpSend` cannot unregister an interceptor and does not reject duplicates, so the singleton scope plus the single entry-point call is what keeps each transaction recorded once.
- **Compose semantics** — `SemanticsProbe()` is an `expect` function; the Android and desktop actuals install JetWhale's probe, and the iOS and Web actuals do nothing.

Because the entry point runs the initializer before the first composition, requests issued during startup are captured too.

Related: [Keeping dev-only code out of release](./build-dev-only-exclusion.md) · [Logging (Kermit)](./logging.md)
