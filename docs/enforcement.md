# Enforcement

On the premise that **AI is the primary author of the code**, this project's conventions — correctness rules and house style alike — are carried by the compiler wherever a compiler can carry them: made unrepresentable by the types, or rejected by a FIR checker. What neither can decide falls to review and tests.

## Principles (priority order of enforcement mechanisms)

1. **Make illegal states unrepresentable via API/types** — strongest, **Kotlin-version-independent**, understood by the IDE. Only valid code can be written.
2. **K2 FIR Checker** — turn **binary rules** that types cannot express into compile errors. The extension API is unstable and must be maintained per Kotlin version.
3. **Review / tests** — things that cannot be decided statically, such as data volume or semantic dependencies.

"Eliminate what can be eliminated at level 1 (write no plugin)," "use level 2 only for binaries that types can't express," "fuzzy goes to 3."

## Enforcement map

Violating any rule below fails compilation. Type/boundary rules need no plugin; FIR checkers each get a subsection below (rejected example and reason).

| Rule | Mechanism |
| --- | --- |
| Actions consumed only in the Presenter / results only in the Root | Type — `context(_: …Context)` |
| Results emitted only inside an effect | Type — `emit` is `suspend` |
| Features cannot touch the ScreenChannel receiving side | Visibility — `internal` + module boundary |
| Cross-feature isolation (no importing another feature's `NavKey`) | Module boundary — no Gradle edge between features (`:feature:debug`, dev-only tooling, is exempt) |
| `NavKey` is `@Serializable` | KSP-generated serializer registration — a miss is a compile error |
| Preview assets do not enter production | Module boundary — release excludes `:core:preview:impl` |
| `@MustBeSerializable` type arguments are serializable | FIR `MustBeSerializable` |
| `rememberSerializable` type arguments are serializable | FIR `RememberSerializable` |
| No direct `mutate` call | FIR `NoDirectMutate` |
| Presenter must not declare [`ScreenContext`](./screen-context.md) | FIR `PresenterMustNotDeclareScreenContext` |
| ScreenContext is not a subtype of PresenterContext | FIR `ScreenContextMustNotBePresenterContext` |
| No presenter effect in a screen root | FIR `NoPresenterEffectInScreenRoot` |
| `Navigator` confined to NavEntry | FIR `NavigatorConfinedToNavEntry` |
| Every [`MutationKey`](./soil-mutation.md) carries a `MutationTag` | FIR `MutationKeyMustCarryTag` |
| Screen does not read Soil directly (role-gated) | FIR `SoilReadConfinement` |
| `@Preview` requires a sanctioned wrapper | FIR `PreviewRequiresWrapper` |
| Nav-only click not routed through the presenter | FIR `NoForwardOnlyAction` |
| Theme-dependent previews use `@PreviewParameter` | FIR read + IR `@ThemeSensitive` metadata |
| Locale-dependent previews use `@LocalePreviews` / `@LocaleScreenPreviews` | FIR read + IR `@LocaleSensitive` metadata |
| Argument-forwarding lambdas use callable references | FIR `LambdaCanBeCallableReference` |
| Pass-through lambdas pass the function value itself | FIR `LambdaCanBePassedDirectly` |
| A `@Composable` lambda literal at the last parameter is trailing | FIR `ComposableLambdaMustBeTrailing` |
| Mutation effect handlers call `reset()` | FIR `MutationEffectMustReset` |
| Platform-confined common declarations carry a platform prefix | FIR `PlatformOnlyNaming` |
| A screen-level composable is the only component in its file | FIR `ScreenIsSoleComponentInFile` |
| Content lambdas nest at most four levels deep | FIR `ComposableNestingDepth` |
| A composable without a layout scope receiver emits one node at its root | FIR `SingleRootEmission` |
| A private property exposed by a wider one uses an explicit backing field | FIR `ExplicitBackingFieldRequired` |
| A private `var` exposed read-only uses `private set` | FIR `PrivateSetRequired` |
| A feature UI composable carries a preview in its file | FIR `UiComponentRequiresPreview` |
| A feature UI composable reads every property of the state it takes | FIR `UiComponentTakesWhatItReads` |
| A callback does not report back a value its own composable was given | FIR `NoCallerSuppliedCallbackArgument` |
| A remembered value is bound to a local before it is used | FIR `RememberResultMustBeBound` |
| A state read only through `.value` is declared with `by` | FIR `StateMustBeDelegated` |

> All implemented FIR checkers live in `:tools:compiler-plugin`. The `droidkaigi.primitive.enforcement` [convention plugin](./build-convention-plugins.md) puts them on each compilation's compiler-plugin classpath, and `droidkaigi.primitive.kmp` / `kmp.compose` apply it, so every module holding app code is covered. Two compilations are outside it: the build-time `:tools:*` modules, which apply neither primitive, and Swift Export's bridge compilation, whose sources the generator writes rather than this project. **Roles are identified by the context-parameter type together with `*Presenter`/`*ScreenRoot` naming, not by annotations.**

Each checker below is covered by a diagnostic test; for how to run and extend them, see [Enforcement checker tests](./testing-enforcement.md).

## FIR checkers (rejected example and reason)

### `NoDirectMutate`

```kotlin
// in a presenter
LaunchedEffect(Unit) {
    bookmarkMutation.mutate(itemId)     // ERROR: NoDirectMutate
    val m = bookmarkMutation.mutate     // ERROR: aliasing is rejected too
}
```

Why: `mutate` bypasses the `MutationState` transition, so `MutatedEffect` / `MutationErrorEffect` never fire — use `mutateAsync(...)`. Soil's `mutate` is a `val: suspend (S) -> T` property, and the checker forbids **any access** to that property, so both the direct call and a desugared alias are compile errors.

### `PresenterMustNotDeclareScreenContext`

```kotlin
context(_: SearchScreenContext, _: SearchPresenterContext) // ERROR on the ScreenContext param
@Composable
fun searchPresenter(): SearchUiState { … }
```

Why: a presenter takes ONLY a `PresenterContext`; declaring a `ScreenContext`-derived context parameter would let it consume Root-role dependencies.

### `ScreenContextMustNotBePresenterContext`

```kotlin
// ERROR: implements BOTH
class SearchScreenContext : ScreenContext, PresenterContext
```

Why: an is-a relationship leaks Root and presenter roles into one type. Use composition — hold a `PresenterContext` as a property: `class SearchScreenContext(val presenterContext: SearchPresenterContext) : ScreenContext`.

### `NoPresenterEffectInScreenRoot`

```kotlin
context(screenContext: SearchScreenContext)
@Composable
fun SearchScreenRoot(...) {
    context(screenContext.presenterContext) {
        searchPresenter()           // OK: presenter launch is the sole exception
        ActionEffect(channel) { … } // ERROR: presenter-only effect in the Root
    }
}
```

Why: the Root narrow-opens a `PresenterContext` scope only to invoke the `*Presenter` function; any other call requiring a `PresenterContext` context parameter (`ActionEffect` / `ScreenChannel.emit`, etc.) is presenter-only and seals that block's remaining hole.

### `NavigatorConfinedToNavEntry`

```kotlin
class SearchScreenContext(val navigator: SearchNavigator) : ScreenContext // ERROR
```

Why: navigation reaches the Root as lambdas — a `Navigator` type may appear only in NavEntryProvider wiring (and core nav infrastructure), never in a `ScreenContext`/`PresenterContext`, a presenter/`@Composable` signature, or a `UiState`/`Action`/`ActionResult`. A `Navigator` that can't be received can't be misused, so the signature-level check suffices.

### `MutationKeyMustCarryTag`

```kotlin
class BookmarkMutationKey(
    private val itemId: TimetableItemId, // ERROR: no MutationTag parameter
) : MutationKey<Unit, TimetableItemId> by buildMutationKey(
    id = MutationId("bookmark/$itemId"), // ERROR: tag not passed into the id
    …
)
```

Why: without a `MutationTag` folded into the `MutationId`, per-screen mutation caches collide (Query/Subscription keys are deliberately shared, mutation keys are not). Both the constructor parameter and its reference inside the `id` argument of a `build*MutationKey` delegation are required.

### `SoilReadConfinement`

```kotlin
@Composable
fun SearchResultList(...) {              // no ScreenContext/PresenterContext param
    val items = rememberQuery(key)       // ERROR: Root-role read outside the root
}
```

Why: reads are role-gated — `rememberQuery`/`rememberSubscription` require an enclosing `ScreenContext` context parameter (Root role); `rememberMutation` requires a `PresenterContext` one. Read Soil at the screen root, not deep in feature UI. (The app shell and `:core` infrastructure are out of scope.)

### `PreviewRequiresWrapper`

```kotlin
@Preview
@Composable
private fun SearchScreenPreview() {
    SearchScreen(uiState = fakeState) // ERROR: not wrapped
}
```

Why: every `@Preview` (JetBrains or AndroidX) must render inside `KaigiTheme` with the preview image resolver, which the wrapper supplies — annotate the function with `@PreviewWrapper(wrapper = KaigiPreviewWrapper::class)`, or make `KaigiPreviewTheme(colorScheme) { … }` the body's top-level statement when the preview picks its own colour scheme. Both checkers read the annotations through one level of meta-annotation, so a multi-preview annotation carrying them counts. See [Preview & sample assets](./preview.md).

### `NoForwardOnlyAction`

```kotlin
ActionEffect(channel) { action ->
    when (action) {
        is Search.ItemClicked -> screenChannel.emit(NavigateToDetail(action.id)) // ERROR
    }
}
```

Why: a handler whose only effectful statement is a `ScreenChannel.emit(...)` merely forwards the action back out as a result — meaningless indirection. Wire the UI callback straight from the Screen to the Root's navigation lambda (see [Error handling](./error-handling.md)). Only a branch/body reduced to that single `emit` is flagged.

### `MustBeSerializable`

```kotlin
// declaration side: the requirement is declared on the type parameter
inline fun <T : Any, @MustBeSerializable reified RESPONSE : Any> buildPersistedQueryKey(…)

// call site
data class SearchResponse(…)                                   // no @Serializable
buildPersistedQueryKey(id, persistKey = "…", byteStore = …,
    fetchResponse = { searchResponse },                        // ERROR: RESPONSE not @Serializable
    transformToDomainModel = { … })
```

Why: a missing `@Serializable` would only fail at runtime when persistence serializes; this checker restores the compile-time gate the reified serializer lookup removed. The check is driven by the `@MustBeSerializable` annotation (`:core:common`) on a type parameter — not a hard-coded callable and argument index — so signature changes cannot silently detach it, and any function can opt in. An unresolvable classifier (type parameter, local/anonymous type) is rejected rather than silently allowed.

A type argument qualifies as serializable when it carries `@Serializable`, is an enum class, or is one of the types `kotlinx.serialization.builtins` covers: the primitives and their unsigned counterparts, `String`, `Unit`, `Nothing`, `List`/`Set`/`Map` and their mutable forms, `Map.Entry`, `Pair`/`Triple`, `Array` and the primitive array types, `kotlin.time.Duration`, and `kotlin.uuid.Uuid`. A generic type additionally requires every one of its type arguments to qualify, so `List<Session>` is accepted only when `Session` is — and the diagnostic then names `Session`, not the container.

### `RememberSerializable`

```kotlin
class SearchFilters(val query: String)   // no @Serializable

@Composable
fun SearchScreenRoot() {
    val filters = rememberSerializable { mutableStateOf(SearchFilters("")) } // ERROR
}
```

Why: `rememberSerializable` (`androidx.compose.runtime.saveable`) resolves the serializer for its state through the same reified `serializer<T>()` lookup, so a type with no serializer compiles and then throws when the state is saved on process death. Serializability is decided as in [`MustBeSerializable`](#mustbeserializable). The overloads taking `serializer = …` / `stateSerializer = …` hand the lookup to the caller and are left alone; a type whose serializer reaches the call through a `SavedStateConfiguration` serializers module carries `@Suppress("REMEMBER_SERIALIZABLE_TYPE_NOT_SERIALIZABLE")`.

### `MutationEffectMustReset`

```kotlin
MutationErrorEffect(favoriteMutation) { error ->   // ERROR: no reset() in the handler
    screenChannel.emit(ShowMessage(error.toUserMessage()))
}
```

Why: the consumed Success/Error stays in the Soil cache beyond the screen instance, so a handler that never calls `mutation.reset()` re-fires the stale result on the next instance of the screen. Only the presence of a `reset()` call inside the handler is checked — where in the handler it runs is up to the use case.

### `PlatformOnlyNaming`

```kotlin
// commonMain
@PlatformOnly(TargetPlatform.Ios)
fun HapticsSyncEffect(...) { … }   // ERROR: name must start with "Ios"

fun IosHapticsSyncEffect(...) { … } // ERROR: "Ios" prefix without @PlatformOnly
```

Why: a declaration in a common source set that only has an effect on one platform must say so in its name, and a platform-prefixed name must be backed by `@PlatformOnly` (`:core:common`) so the prefix cannot lie or go stale. The reverse rule applies only to top-level declarations under `commonMain`; platform source sets use platform-prefixed names freely.

### `ScreenIsSoleComponentInFile`

```kotlin
// TimetableScreen.kt
@Composable
fun TimetableScreen(...) { … }

@Composable
private fun TimetableCard(...) { … }   // ERROR: move it to TimetableCard.kt
```

Why: the file path is the component's identity, so an agent locates and edits a component without reading the screen it happens to sit in. A file declaring a top-level `Unit`-returning `@Composable` named `*Screen`/`*ScreenRoot` may declare no other UI component; `@Preview` functions and value-returning composables (presenters) are exempt. The extracted component becomes `internal` — file-private visibility is not load-bearing here, since the module boundary already confines it to its feature.

### `LambdaCanBeCallableReference`

```kotlin
TimetableScreenRoot(
    onNavigateToDetail = { id -> navigator.openSessionDetail(id) }, // ERROR
    // OK: onNavigateToDetail = navigator::openSessionDetail
)
```

Why: a lambda whose entire body is one call forwarding the lambda parameters unchanged is noise — write the callable reference. The checker skips every shape a reference cannot substitute: `suspend` or receiver-typed function types, varargs, infix/operator calls, explicit type arguments, and receivers that are not a plain `this`/object/`val` chain (a reference captures its receiver once, so a mutable receiver would change semantics).

`@Composable` lambdas are excluded by choice rather than necessity. A composable reference compiles, but `::Title` at a content slot hides the call syntax that marks a composable in Compose code. Forwarding a composable value is covered by `LambdaCanBePassedDirectly` instead.

### `LambdaCanBePassedDirectly`

```kotlin
Wrapper(content = { content() })                     // ERROR: content = content
Modifier.clickable { onOpenSoilErrors() }            // ERROR: clickable(onClick = onOpenSoilErrors)
flow.collect { block(it) }                           // ERROR: collect(block)

RowScopeConsumer(content = { content() })            // OK: adapts () -> Unit to RowScope.() -> Unit
```

Why: a lambda that does nothing but invoke a function value already in scope is one indirection with no meaning — pass the value. This is not a callable reference, so it stays available where `LambdaCanBeCallableReference` does not apply, including `@Composable` and `suspend` function types.

The lambda's type and the value's type must be equal, which keeps adaptations out: a `@Composable () -> Unit` cannot reach a `@Composable RowScope.() -> Unit` parameter on its own. The value must also be a parameter or a `val`, since a `var` could change between the point the lambda is created and the point it runs.

### `ComposableLambdaMustBeTrailing`

```kotlin
CompositionLocalProvider(LocalContentColor provides contentColor, content = { Label() })  // ERROR

CompositionLocalProvider(LocalContentColor provides contentColor) {                       // OK
    Label()
}

Wrapper(label = "label", content = content)          // OK: a value, not a literal
LeadingSlot(icon = { Icon() }, label = "label")      // OK: `icon` is not the last parameter
PlainSlot(content = { })                             // OK: not a @Composable function type
```

Why: a content slot written as a named argument buries the block that produces the UI inside the argument list, where it reads as configuration rather than as the nested content it is. Trailing syntax puts it where every other content block in the file sits.

Only lambda literals bound to the callee's last value parameter are in scope. Named arguments may be written in any order, so a literal at the last parameter can always be moved out of the parentheses, even when another named argument follows it in source. The rule leaves alone what trailing syntax cannot express: a parameter that is not the last one, a `vararg` last parameter, an anonymous `fun` expression, and infix or operator calls.

### `ComposableNestingDepth`

```kotlin
@Composable
fun TimetableScreen(uiState: TimetableScreenUiState) {
    Scaffold { padding ->                 // 1
        Column(Modifier.padding(padding)) {   // 2
            LazyColumn {                  // 3
                items(uiState.sessions) { item ->  // 4
                    Card {                // ERROR: 5
                        Text(item.title)
                    }
                }
            }
        }
    }
}
```

Why: a deeply nested tree hides the structure of the screen. A `@Composable` function may nest content lambdas at most **four** levels deep; the fifth level must move into its own `@Composable` function (`TimetableCard` above). In a screen file, [`ScreenIsSoleComponentInFile`](#screenissolecomponentinfile) then gives that component its own file.

A lambda counts towards the depth only when its body emits UI, so `onClick`, `remember`, and coroutine bodies are free. Builder lambdas that wrap content — `forEach`, `LazyListScope` — do count, because they add a level of braces the reader has to follow. The error is reported on the call that owns the offending lambda.

### `SingleRootEmission`

```kotlin
@Composable
fun ItemIcon(selected: Boolean, icon: @Composable () -> Unit) {  // ERROR
    if (selected) {
        Box(Modifier.background(indicatorColor))
    }
    icon()
}

@Composable
fun BoxScope.ItemIcon(selected: Boolean, icon: @Composable () -> Unit) {  // OK
    if (selected) {
        Box(Modifier.background(indicatorColor))
    }
    icon()
}
```

Why: a composable with no root container leaves placement to whoever calls it. A centered `Box` stacks the two nodes in emission order, a `Row` or `Column` sets them side by side, so the component renders as intended at the call site it was written for and differently at the next one. A layout scope receiver — `BoxScope`, `RowScope`, `ColumnScope`, `LazyItemScope`, or a subtype such as `KaigiNavigationBarScope` — names the caller as the owner of placement, and is the sanctioned way to write a multi-emission component.

Emission counting follows a single control-flow path: branches of `if` and `when` contribute the largest of their paths, an emitter inside a loop counts as many, and a branch that returns takes the statements after it off its own path. A call emits when its own result type is `Unit`, which admits a generic composable substituted to `Unit` at the call site, such as `key`. Calls whose result is bound to a value, `remember` among them, produce no node.

An effect is named `…Effect`, and a composable carrying that name is read as running work rather than emitting. The name is taken from the declaration that owns it, so a `fun interface` effect is recognised by the interface name at its `invoke` site:

```kotlin
fun interface HistorySyncEffect {
    @Composable operator fun invoke(backStack: NavBackStack<NavKey>)
}

uiGraph.historySyncEffect(backStack)  // reads as HistorySyncEffect, so it emits nothing
```

### `ExplicitBackingFieldRequired`

```kotlin
class ServerEnvironmentStore {
    private val mutableEnvironment = MutableStateFlow(ServerEnvironment.Staging)
    val environment: StateFlow<ServerEnvironment> = mutableEnvironment.asStateFlow() // ERROR

    // OK:
    val environment: StateFlow<ServerEnvironment>
        field = MutableStateFlow(ServerEnvironment.Staging)
}
```

Why: a private property paired with a wider one that merely re-exposes it duplicates one piece of state under two names, and nothing keeps the pair in sync as the class grows. Kotlin's explicit backing field states the same intent in one declaration — the field type inside the class, the property type outside it. The checker fires only when the rewrite is mechanical: both properties are read-only, the private property's type is already a subtype of the exposed type, and its value comes from an initializer rather than a constructor parameter or a getter. Between the two, either a plain read or a read-only view of the same instance (`asStateFlow` / `asSharedFlow`) qualifies; a conversion that widens beyond a subtype (`Channel.receiveAsFlow()`) and a derived value (`Flow.map`) are left alone.

### `PrivateSetRequired`

```kotlin
class Counter {
    private var mutableCount = 0
    val count: Int get() = mutableCount // ERROR

    // OK:
    var count: Int = 0
        private set
}
```

Why: the same duplicated-state problem as `ExplicitBackingFieldRequired`, for the case the field's type cannot express — a private `var` reassigned inside the class and read from outside. `private set` narrows the setter alone, so the pair collapses into one declaration. The two rules partition by type: identical types are a `private set` job, a strictly narrower private type is an explicit backing field one.

### `UiComponentRequiresPreview`

```kotlin
// TimetableCard.kt
@Composable
internal fun TimetableCard(title: String, onClick: () -> Unit) { … } // ERROR: no preview here

// OK: a preview for it in the same file
@PreviewWrapper(KaigiPreviewWrapper::class)
@Preview
@Composable
fun TimetableCardPreview() { TimetableCard(title = /* sample */, onClick = {}) }
```

Why: a component with no preview cannot be inspected without running the app, so a reader has no way to see what it looks like. Every top-level `Unit`-returning `@Composable` under a feature package requires a `@Preview` **that renders it** in the same file — a preview elsewhere in the file does not count, so a file holding several components needs a preview reaching each one. The check reads the preview's body, descending into wrapper lambdas such as `KaigiPreviewTheme(colorScheme) { … }` and following helpers declared in the same file, so a preview may reach the component indirectly. [`ScreenIsSoleComponentInFile`](#screenissolecomponentinfile) exempts previews, so the preview sits beside the component it renders, and [`PreviewRequiresWrapper`](#previewrequireswrapper) then forces it through the sanctioned wrapper.

The rule holds for every feature module, `:feature:debug` included: a per-module exemption is invisible at the point of editing, so it reads as "this component needs no preview" and the next component written there inherits the gap. The exemptions that remain are all visible in the declaration itself:

| Exempt | Reason |
| --- | --- |
| A composable declaring a context parameter | Every `*ScreenRoot`; its `ScreenContext` comes from the screen's Metro graph, which a preview cannot build |
| `expect` / `actual` declarations | An `expect` has no body, and the tooling renders the common and Android views only |
| A composable named `*Effect` | Runs side effects and emits nothing, so its preview would be blank |
| A member composable | The rule reads top-level declarations; a composable on a class is reached through its owner |
| A composable returning anything but `Unit` | Every `*Presenter` returns a UiState rather than emitting UI |

A component that genuinely cannot be rendered on its own carries `@Suppress("UI_COMPONENT_WITHOUT_PREVIEW")` with the reason beside it. `SoilErrorBottomSheet` is the one case in the codebase: `ModalBottomSheet` renders into a popup window, which a preview captures as an empty tree, so its content is split into `SoilErrorSheetContent` and previewed there.

### `UiComponentTakesWhatItReads`

```kotlin
@Composable
internal fun SessionHeaderView(item: TimetableItem) { // ERROR: day, startsAt, endsAt, asset unread
    Text(item.room.name)
    Text(item.title.current())
    Text(item.speakers.joinToString { it.name })
}

// OK: the properties it reads
@Composable
internal fun SessionHeaderView(room: SessionRoom, title: String, speakers: List<TimetableSpeaker>) { … }
```

Why: a component that takes an aggregate for a few of its properties spreads that type further than its own reads justify — every caller must hold the whole state to render the component, the preview must build it, and Compose recomposes the component when a property it never reads changes. A feature UI `@Composable` must read **every** property of a parameter it selects from; otherwise declare a UiState type for the component holding only what it reads (`FavoritesListSectionUiState` is the shape to copy), or take those properties as separate parameters.

The parameter type in scope is a project-owned class with a primary constructor, and the properties counted are the ones that constructor declares. A parameter used as a value rather than selected from — passed on to another composable, compared, or a receiver of a member call — is out of scope, since its own shape is then load-bearing.

A list item stays cheap under this rule because the state it does not render belongs elsewhere: selection state (`isFavorite`) reaches it as its own parameter rather than a field on the model, and layout state its parent applies (`SponsorPlan`) never enters the item at all. An identifier the item only hands to a callback does not enter it either — see [`NoCallerSuppliedCallbackArgument`](#nocallersuppliedcallbackargument).

### `NoCallerSuppliedCallbackArgument`

```kotlin
@Composable
internal fun TimetableCard(
    id: TimetableItemId,
    title: String,
    onClick: (TimetableItemId) -> Unit,   // ERROR: reports back `id`
) {
    Card(modifier = Modifier.clickable { onClick(id) }) { Text(title) }
}

// OK: the caller holds the identifier, so the callback carries nothing
@Composable
internal fun TimetableCard(title: String, onClick: () -> Unit) {
    Card(modifier = Modifier.clickable(onClick = onClick)) { Text(title) }
}

// at the call site
items(slot.items) { item ->
    TimetableCard(title = item.title, onClick = { onItemClick(item.id) })
}
```

Why: a callback parameter exists to carry information the caller does not have. A value the caller passed in a moment earlier is not such information — the caller can close over it at the call site, and the round trip only widens the component's signature and forces the identifier into a state type that exists for rendering. A `@Composable` therefore must not invoke a `Unit`-returning function-typed parameter with one of its own value parameters, or with a property selected from one.

The argument must be a plain read to be rejected: an element bound by a `forEach`/`items` lambda, a `remember`ed value, and any computed expression (`count + 1`) are all values the component owns. `@Composable` and `suspend` function types carry their own function-type kinds, so content slots and suspending handlers are out of scope.

### `RememberResultMustBeBound`

```kotlin
remember { SnackbarHostState() }.showSnackbar(message)   // ERROR
retain { mutableStateOf(DroidKaigi2026Day.Day1) }.value  // ERROR

val snackbarHostState = remember { SnackbarHostState() } // OK
snackbarHostState.showSnackbar(message)

var selectedDay by retain { mutableStateOf(DroidKaigi2026Day.Day1) } // OK: a delegate
SnackbarHost(retain { SnackbarHostState() })                         // OK: argument position
rememberCoroutineScope().launch { … }                                // OK: a handle, not a value
```

Why: a call written directly as a receiver reads as a computation performed at that point, while what it produces is a value held across recompositions. The name a local gives it says so at the point it is created, and every later use then reads as a use of the held value. The chained form also hides the keys the call was given, which is what decides when the value is recomputed.

The subject is the family that remembers a **value**: `remember`, `rememberSaveable`, `rememberSerializable`, `rememberUpdatedState`, and `retain` — matched by resolved callable id, so every overload of each name qualifies and a same-named function in another package does not. A call that returns a **handle** whose immediate use is the idiomatic form is not in the family, which is why `rememberCoroutineScope().launch { … }` compiles. The Soil reads (`rememberQuery`, `rememberMutation`, `rememberSubscription`) and the `remember*`/`retain*` factories that produce navigation and scroll infrastructure are left out on the same ground.

Only the receiver position is in scope. Argument position is idiomatic and stays out, which also leaves `with(remember { … }) { … }` and every other paraphrase that passes the value as an argument alone; closing those would take a list of scoping functions, which [the principles above](#principles-priority-order-of-enforcement-mechanisms) reserve for review. The receiver position itself is read from the resolved call, so a receiver reached through `?.`, through an index access, or as the subject of a `for` loop counts, and so does a scoping call written as a receiver (`remember { … }.let { … }`).

### `StateMustBeDelegated`

```kotlin
val selectedDay = remember { mutableStateOf(DroidKaigi2026Day.Day1) } // ERROR
selectedDay.value = day
Text(selectedDay.value.name)

// OK: the delegate reads and writes the same state
var selectedDay by remember { mutableStateOf(DroidKaigi2026Day.Day1) }
selectedDay = day
Text(selectedDay.name)

// OK: the object leaves the scope, so `by` is unavailable
val selectedDay = remember { mutableStateOf(DroidKaigi2026Day.Day1) }
return selectedDay
```

Why: `.value` at every use is noise the language already removes. `androidx.compose.runtime.getValue` and `setValue` make `by` read and write the same state, so the delegated form is the declaration plus the plain name everywhere else — and the import pair is the only thing the rewrite adds.

The rule is stated over a **declaration**, not a use, because a delegate hands out the value and takes the object away. A declaration is rejected when it is a `val` without a delegate whose type is `androidx.compose.runtime.State` or a subtype, and **every** reference to it is a read or a write of its `value`. One use of the object itself — passed as an argument, returned, destructured, or the receiver of anything else — leaves it out, since demanding `by` there would demand a rewrite that does not exist. A declaration with no reference at all is out for the same reason: there is nothing to shorten.

That exclusion records what `by` cannot express, and is not a reason to reach for the object. A screen renders an immutable `UiState` and reports interaction through a callback, so a state type does not belong in a composable's parameter list — see [Building a screen](./building-a-screen.md#action-actionresult-uistate).

Visibility bounds the rule to what the compiler can see: only a local variable or a `private` property qualifies, because those are the declarations whose every reference lives in the file being compiled. A wider property may be read as an object from another module, and the checker would be judging it on partial evidence.

Three further exclusions follow from the rewrite rather than from visibility:

- A `var` is never reported. It is reassignable, and under `by` an assignment writes the state's value instead of rebinding the variable.
- A property with a custom getter, and a property the declaration does not initialize, are never reported. `by` needs a delegate expression at the declaration, and it does not recompute per read.
- `kotlinx.coroutines.flow.StateFlow` and `MutableStateFlow` are never reported. Their `value` is a different member on an unrelated type and no `getValue` operator applies; the checker matches the Compose `State` class id rather than the member name, so a value class whose property happens to be named `value` is out for the same reason.

## Review + tests (fuzzy)

Rules that depend on data volume or semantics stay out of static enforcement: heavy shaping belonging in the data layer, validity of an emitted result, and correctness of mutation-result handling — ensured by AI review rules + Presenter/Screen tests. Naming rules join them, because a name is judged against the domain vocabulary rather than against the types — see [Naming review](./naming-review.md). So does whether a value belongs in a `CompositionLocal`, which turns on intent rather than on types — see [CompositionLocal review](./compositionlocal-review.md).

**Eliminate via types what types can eliminate** (required serializer, context param, `suspend`, `internal`), **use FIR checkers for binaries types can't express**, and **leave fuzzy cases to review**. Even when AI writes the code, the type and FIR layers hold because **compilation fails**.

Related: [Architecture overview](./architecture-overview.md) · [Building a screen](./building-a-screen.md) · [ScreenContext design](./screen-context.md) · [Error handling](./error-handling.md) · [Naming review](./naming-review.md)
