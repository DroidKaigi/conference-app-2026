# Copilot review instructions

This repository is a Kotlin Multiplatform / Compose Multiplatform application that builds against **Kotlin 2.4** ([`gradle/libs.versions.toml`](../gradle/libs.versions.toml)). It uses recent language features and enforces its architecture with a custom compiler plugin, so a review that assumes a conventional Kotlin/Android setup reports problems that do not exist. Review design, correctness, and clarity.

The conventions this project applies are stated in [`docs/`](../docs/index.md), and the perspectives a review is responsible for are listed under [Review perspectives](#review-perspectives) below. Each perspective names the page that owns it; read that page before judging, and cite it in the finding.

## Do not report compilation failure

Do not claim that code "will not compile", that a symbol is unresolved, or that an import is missing. A review sees a diff, not the compilation: source sets, generated sources, and default imports are all outside it, and the build is the authority. Compilation is already covered — the test workflow runs `test jvmTest` across every module, the iOS build workflow builds the iOS application, and the format check runs Spotless.

Two shapes that are correct here and have been reported as errors:

- `flow.collect { value -> … }` requires no `kotlinx.coroutines.flow.collect` import. `FlowCollector` is a `fun interface`, so the lambda converts and the call resolves to the member `Flow.collect(collector: FlowCollector<T>)`. See [`NavigatorEffect.kt`](../core/common/src/commonMain/kotlin/io/github/droidkaigi/confsched/core/common/NavigatorEffect.kt).
- A symbol with no declaration in the source tree is generated: dependency-injection graphs come from Metro (`dev.zacsweers.metro`), navigation-key serializer registration and Soil identifiers from `:tools:ksp-processor`, and `BuildKonfig` from the build configuration.

## Kotlin 2.4 language features

**Explicit backing fields.** A property may declare a `field` whose type is narrower than the property's own. Inside the declaring class the name resolves to the field, outside it to the property.

```kotlin
class ServerEnvironmentStore {
    val environment: StateFlow<ServerEnvironment>
        field = MutableStateFlow(ServerEnvironment.Staging)

    fun select(environment: ServerEnvironment) {
        this.environment.value = environment // the field: a MutableStateFlow
    }
}
```

This is enabled by default and needs no compiler flag. Writing to the property inside the class is not an error. The customary alternative — a private `MutableStateFlow` plus a public `StateFlow` — is a compile error in this repository, rejected by the `ExplicitBackingFieldRequired` checker, so suggesting it breaks the build.

**Context parameters.** `context(presenterContext: FavoritesPresenterContext)` on a function is stable syntax and needs no compiler flag. These declarations carry the architecture's role gating, and the checkers below decide which role may declare which context parameter.

## Architecture rules are compiler-enforced

A custom K2 frontend (FIR) compiler plugin in `:tools:compiler-plugin` turns this project's architecture rules into compile errors, and every module holding application code is covered. The full map of checkers, each with a rejected example and its reason, is [`docs/enforcement.md`](../docs/enforcement.md).

Read that page before suggesting a restructure. Several suggestions that read as ordinary Kotlin or Compose advice are rejected there, among them:

- Re-exposing a private property through a wider one (`ExplicitBackingFieldRequired`, `PrivateSetRequired`).
- Passing a value to a component so that its callback can hand the value back (`NoCallerSuppliedCallbackArgument`).
- Wrapping a function value in a lambda that only invokes it, or a call that only forwards its arguments (`LambdaCanBePassedDirectly`, `LambdaCanBeCallableReference`).
- Declaring a helper composable beside the screen it serves, or one without a preview (`ScreenIsSoleComponentInFile`, `UiComponentRequiresPreview`).
- Reading Soil, calling `mutate`, or holding a `Navigator` outside the role that owns it (`SoilReadConfinement`, `NoDirectMutate`, `NavigatorConfinedToNavEntry`).

A rule a checker decides needs no manual re-check: a violation fails the build before the pull request is readable. Do not spend a comment restating one. Link to the relevant section of that page rather than restating the rule.

## Scope of a review

A pull request answers the issue it closes, and that change is what the review judges. A contributor here holds one issue at a time ([`CONTRIBUTING.md`](../CONTRIBUTING.md)), so a comment asking for work the issue does not name asks someone to exceed their assignment.

- Judge the diff. A defect in code the change does not touch stays out of the review even where the diff brings it into view, and a line shown as context is not under review.
- Do not ask for work the change did not set out to do: an adjacent screen, a rewrite of the surrounding file, a rename reaching past the lines being changed, or a capability the issue does not name.
- What the change itself breaks is in scope wherever it lives — a call site the new signature invalidates, a test the new behaviour should have updated, a documentation page a rename leaves wrong. Reporting that is what the review is for.
- Something out of scope but worth fixing is worth one sentence that says so, left for a separate issue rather than raised as a change to this pull request.

## Review perspectives

What the type system and the checkers cannot decide is what a review is for — data volume, domain vocabulary, intent, and test coverage ([`docs/enforcement.md`](../docs/enforcement.md#review--tests-fuzzy)). Work through the perspectives the diff touches, in the order below.

### Layer placement

[`docs/platforms-and-modules.md`](../docs/platforms-and-modules.md) · [`docs/project-structure.md`](../docs/project-structure.md)

- `:core:model` holds domain models, Soil key `typealias` contracts, and per-screen scope markers. API responses and `Default*Key` implementations belong to `:core:data`; `UiState` / `Action` / `ActionResult` to the feature module.
- A feature depends on the key `typealias`, never on an implementation, and never talks to an API client directly.
- No feature imports another feature (`:feature:debug`, dev-only tooling, is exempt).
- Code identical across platforms belongs to `app-shared`; a per-platform entry module holds only what reaches a platform SDK.

### Naming

[`docs/naming-review.md`](../docs/naming-review.md) · [`docs/building-a-screen.md`](../docs/building-a-screen.md#naming-conventions-for-compose-views)

Apply the procedure to every declaration in the diff whose type is general-purpose (`String`, `Int`, `Boolean`, or a collection of those), and to `:core:model` and `UiState` properties first — their names reach every feature that renders them.

- The name states what the value is; the type states how it is represented. `speaker: String` is not a speaker, so the name is `speakerName`. `featuredSession: TimetableItemId` is `featuredSessionId`.
- A noun answering an assertion takes `is` / `has` / `can` (`favorite` → `isFavorite`); an adjective or participle already reads as one and stands alone (`enabled`).
- A suffix that restates the type is over-qualification (`titleString`, `titleText`).
- Where a value reaches one part of what its owner renders, the name states the part (`seed` → `indicatorSeed`), and where more than one member of a category is in reach, it states the member (`scope` → `coroutineScope`).
- Compose views are named `<Feature>ScreenRoot`, `<Feature>Screen`, and `<Name><Kind>` — every other view carries a widget-kind suffix.

### Presenter responsibility

[`docs/presenter-performance.md`](../docs/presenter-performance.md) · [`docs/building-a-screen.md`](../docs/building-a-screen.md#presenter--compute-light-presentercontext-only)

- `groupBy` / `sortedBy` / `associate` / `distinctBy`, a join, or index construction in the body of a `@Composable` presenter runs on the main thread on every recomposition. Raise it as a nudge toward the data layer — heaviness depends on data volume, so it is a judgement, not a verdict.
- `rememberQuery(key, select)` and `transform` run on the main thread and are not a performance fix.
- `flowOn` or `withContext(Dispatchers.Default)` inside `fetch` / `subscribe` is redundant: both already run on the SwrCache worker.
- A `QueryKey` may return a reusable, UI-independent domain-convenience model; it must not return a screen-specific `UiState`.
- `UiState` is a plain data class of render-ready values — no callbacks, no mutable state, no Soil objects.
- State that must survive entry recreation uses `retain { mutableStateOf(…) }` rather than `remember`, and a `collectAsState` initial value must not flash a wrong state.

### Data layer (Soil)

[`docs/soil-keys.md`](../docs/soil-keys.md) · [`docs/soil-persistence.md`](../docs/soil-persistence.md) · [`docs/soil-mutation.md`](../docs/soil-mutation.md) · [`docs/soil-data-boundary.md`](../docs/soil-data-boundary.md)

- One file per key on both sides; a shared index file is a merge point every pull request touches.
- A second view of the same data reuses the shared key with `rememberQuery(key, select = …)`. A new key is warranted only where a dedicated API exists.
- A query does not read another query. A fetch that needs another request's result chains both inside one `fetchResponse`.
- Two `typealias`es erasing to the same underlying type collide as one Metro binding. The fix is a qualifier on every key in the group and every injection site, declared in the same file as the key it qualifies.
- Persisted queries take an explicit, stable `persistKey`, persist the server response rather than the domain model, and inject `ServerEnvironmentScopedFileStorage` so caches do not leak across server environments.
- A mutation wraps its input or result in a dedicated class only at two or more values; the type arguments are `MutationKey<Result, Variable>`.
- Binary payloads go through `FileStorage`; key-value settings go through DataStore behind a `*Store` class.
- Response shaping happens in `fetch`, and loading and error states are the boundary's concern, not the caller's.

### Error handling and one-off events

[`docs/error-handling.md`](../docs/error-handling.md)

- A load failure surfaces through `SoilDataBoundary`'s error fallback; a transient failure or a success notice goes to the snackbar read from `LocalSnackbarHostState`. A Root reads that local rather than nesting its own `Scaffold`.
- A one-off event is an effect keyed to a transition, never a boolean on `UiState`.
- The channel ends stay in their roles: `send` and `ActionResultEffect` in the Root, `ActionEffect` and `emit` in the presenter.
- A click that only navigates is wired from the Root straight into the Screen's `on*` parameter; a screen with no action and no one-off needs no `ScreenChannel`.
- A `MutationSuccessEffect` / `MutationErrorEffect` handler calls `reset()` at the call site, and the follow-up travels back as an `ActionResult`.

### Navigation

[`docs/navigation-navigator.md`](../docs/navigation-navigator.md) · [`docs/navigation-entry-aggregation.md`](../docs/navigation-entry-aggregation.md) · [`docs/navigation-root-tab-bar.md`](../docs/navigation-root-tab-bar.md) · [`docs/navigation-list-detail.md`](../docs/navigation-list-detail.md) · [`docs/navigation-predictive-back-tabs.md`](../docs/navigation-predictive-back-tabs.md)

- Outgoing navigation goes through the feature's `<Feature>ScreenNavigator`; its `Default…` implementation lives in `app-shared` and is bound into the screen's scope. The Root receives a plain lambda.
- A destination outside the app has no `NavKey`: the NavEntry supplies `LocalUriHandler` as the Root's lambda.
- `RootSceneStrategy.root()` marks the home root entry only — it is the predictive-back marker, not a tab marker; other root tabs carry `instantNavTransition()`.
- `rootSceneStrategy` precedes `listDetailSceneStrategy` in `sceneStrategies`.
- The back stack is mutated in `NavigatorEffect` alone, which already guards a duplicate top push and an over-pop; a call site adds no guard of its own.
- A screen in `commonMain` does not read a window size. The detail pane distinguishes its affordance through `LocalListDetailSceneScope`.

### Dependency injection and role contexts

[`docs/screen-context.md`](../docs/screen-context.md) · [`docs/di-screen-graph.md`](../docs/di-screen-graph.md) · [`docs/di-app-graph.md`](../docs/di-app-graph.md)

- `ScreenContext` holds Root-role dependencies (query and subscription keys) and the `PresenterContext` instance as a property; `PresenterContext` holds presenter-role dependencies (mutation keys, stores, the clock). Neither smuggles the other's.
- Bindings exposed as graph accessors are `@SingleIn(scope)`; bindings a constructor consumes stay unscoped.
- A graph-extension factory method carries its screen's name.
- The NavEntryProvider retains the graph once — keyed by the `NavKey` for a keyed screen — and reads the accessors; the navigator never enters the `ScreenContext`.
- Optional or debug-only behaviour is a Noop default plus `@ContributesBinding(…, replaces = […])`.

### UI composition

[`docs/building-a-screen.md`](../docs/building-a-screen.md#screen--rendering-only)

- `<Feature>Screen` renders only: its inputs are a `UiState` and callbacks — no dependency injection, no Soil, no navigation types.
- Material 3 idioms and theme tokens: a hardcoded colour or dimension where a token exists is a finding.
- Window insets are handled where an entry draws to the edge, and empty, loading, and error states exist wherever the data can be empty or fail.
- Compose animation APIs (`animate*AsState`, `Animatable`, `rememberInfiniteTransition`, `AnimatedVisibility`) honour `MotionDurationScale`, which the platform sets to zero under its reduced-motion setting, so an animation built on them already stops there. Do not ask for an explicit reduced-motion check; only motion outside those APIs, such as a `withFrameNanos` loop or video playback, needs one.
- A value that ticks reaches the UI as a `UiState` field the presenter computed.

### CompositionLocal

[`docs/compositionlocal-review.md`](../docs/compositionlocal-review.md)

- For each `CompositionLocal` added, name the two positions that read different values. "Convenient to reach" is not one of them; reaching for one because a parameter would thread through several composables is a signal about the component tree.
- Read its default: `error(…)` states that a provider is required, `null` that absence is a real case, and a usable value must be a behaviour someone chose.
- A service reaches its user through the dependency graph and a role context, data through a `UiState`, and a caller-owned value through a parameter.
- A read from a presenter makes its result depend on where it was composed, which its unit test cannot vary.

### Localization

[`docs/localization.md`](../docs/localization.md)

- A string literal drawn by a composable is a defect: every displayed string is a Compose Resources string, owned by the module that draws it, with the English base in `values/` and the translation in `values-ja/`.
- A string whose value is the same in both locales is declared in `values/` only. Do not ask for a `values-ja/` entry that repeats the base value.
- A count takes `pluralStringResource`.
- Text the server supplies travels as `MultiLangText` and resolves at the point of display, never in the data layer.
- Not localized: `:feature:debug`, preview and sample values, and values carrying no words.

### Injected seams

[`docs/clock.md`](../docs/clock.md) · [`docs/logging.md`](../docs/logging.md)

- The time is read through `KaigiClock`, taken from the `PresenterContext` like any other dependency. `Clock.System.now()` appears only where the clock itself is built, and a UI composable does not read the clock at all. Elapsed time for an animation comes from a monotonic source instead.
- Logging goes through `KaigiLogger`; a call to Kermit's `Logger` bypasses injection.

### Preview and sample data

[`docs/preview.md`](../docs/preview.md) · [`CLAUDE.md`](../CLAUDE.md)

- A preview is `private`, renders inside `KaigiPreviewWrapper` or `KaigiPreviewTheme`, takes `@PreviewParameter(KaigiSchemeProvider::class)` when its content is theme-sensitive, and carries `@LocalePreviews` when it resolves a string resource.
- Sample values come from a `fake()` builder declared next to the type it builds; a preview declares no fixture of its own.
- Production code never depends on `:core:preview:impl`.
- A sample value must not name a real person or organization, or read as one — names, affiliations, handles, account ids, email addresses, and URLs alike. Placeholders stay recognizable (`Speaker A`, `Session 1`, `https://example.com/…`); naming a technology is subject matter and is fine.

### Tests

[`docs/testing.md`](../docs/testing.md) · [`docs/testing-presenter.md`](../docs/testing-presenter.md) · [`docs/testing-robot.md`](../docs/testing-robot.md) · [`docs/testing-preview-screenshot.md`](../docs/testing-preview-screenshot.md) · [`docs/testing-graph.md`](../docs/testing-graph.md)

- A presenter change comes with a `runPresenterTest` case per Action, resolving its context from the screen's test graph rather than constructing it by hand. A navigation-only click has no presenter path to test.
- Screen behaviour is covered by a Robot test, and rendering by Roborazzi screenshots of the previews.
- A test that depends on the time drives `FakeClock`.
- A new or changed FIR checker comes with a diagnostic test.
- Report a missing test with the first case to add, not as a bare observation.

### Build configuration

[`docs/build-version-catalog.md`](../docs/build-version-catalog.md) · [`docs/build-convention-plugins.md`](../docs/build-convention-plugins.md) · [`docs/build-dev-only-exclusion.md`](../docs/build-dev-only-exclusion.md)

- Every dependency and plugin version lives in `gradle/libs.versions.toml` and is reached through `libs.*` / `alias(libs.plugins.*)`; a coordinate or version written into a module's build script is a finding. Aliases are flat camelCase.
- A module's build script applies the convention plugin that fits its role rather than repeating the configuration it carries.
- Dev-only code stays out of release through the dependency graph, never through a runtime `if`.

### Documentation changes

[`CLAUDE.md`](../CLAUDE.md)

- Every page is rules, map, or guide, and stays at one altitude: module and overview pages state responsibilities rather than listing classes.
- One canonical home per topic; other pages link to it with positive phrasing.
- Headings are topic nouns. Conclusions are stated as facts — prose narrating the investigation that produced them is deleted.
- The sidebar label, the page heading, and the text of links pointing at the page agree, and relative links resolve.

## Wording of review comments

[`CLAUDE.md`](../CLAUDE.md) at the repository root states the conventions this project applies to comments, documentation, and commit messages: English throughout, neutral and declarative present tense, and no comment that restates what the code already shows. Judge the diff against those conventions, and write review comments in the same register.

State the finding, the rule it violates, and the page that owns the rule. Prefer few findings that survive that citation over broad coverage: a comment that restates a compiler-enforced rule, or that repeats architecture-level knowledge at a call site, is noise.
