# Preview & sample assets

Compose `@Preview`s need sample data and images, but those assets must **not ship in release builds**.

## Module split (production isolation)

- `:core:preview:api` — the contract and the sample data: a type-safe `PreviewImage` enum, the `PreviewImageResolver` interface, `LocalPreviewImageResolver` (default `null`), the `PreviewScope` marker, `NoopPreviewImageResolver` (the `@ContributesBinding(PreviewScope)` default, which resolves nothing), and the model `fake()` builders. No image binaries.
- `:core:preview:impl` — the image binaries (Compose Resources) and `DefaultPreviewImageResolver`, contributed with `@ContributesBinding(PreviewScope, replaces = [NoopPreviewImageResolver::class])` so it overrides the no-op default wherever `:impl` is on the classpath.
- `:core:preview:wrapper` — the Metro `PreviewGraph` (`@DependencyGraph(PreviewScope)`), `KaigiPreviewTheme`, and the wrapper features attach to their previews.

```text
core/preview/
├─ api/src/commonMain/kotlin/.../preview/
│    PreviewImage.kt            # the type-safe enum (generated)
│    PreviewImageResolver.kt    # contract + LocalPreviewImageResolver
│    PreviewScope.kt            # Metro scope marker
│    NoopPreviewImageResolver.kt # @ContributesBinding(PreviewScope) default; resolves nothing
│    TimetableFake.kt           # Timetable.fake(), TimetableItem.fake()
│    ContributorsFake.kt        # Contributors.fake()
│    SponsorsFake.kt            # Sponsors.fake()
├─ impl/src/commonMain/
│    ├─ kotlin/.../preview/impl/
│    │    DefaultPreviewImageResolver.kt   # @ContributesBinding(PreviewScope, replaces=[Noop]); URL -> resource
│    └─ composeResources/drawable/
│         *.png                            # the image binaries
└─ wrapper/src/commonMain/kotlin/.../preview/wrapper/
     PreviewGraph.kt                  # @DependencyGraph(PreviewScope)
     KaigiPreviewTheme.kt             # KaigiTheme + the resolver, for any preview to call
     KaigiPreviewWrapper.kt           # applies it under one fixed colour scheme
```

`KaigiPreviewTheme` and `PreviewGraph` live in `:wrapper`, not `:impl` or `:api`. The wrapper takes `:core:preview:impl` as a `compileOnly` dependency: Metro aggregates the contributed `DefaultPreviewImageResolver` binding at the wrapper's compile time, while `:impl` stays off production classpaths. Kotlin/Native and wasm rely on partial linkage to tolerate the dangling reference, and neither ever runs in production. Keeping the graph out of `:api` also avoids a cycle: `:api -> :impl` would clash with `:impl -> :api`.

Production depends on `:core:preview:wrapper` (through the feature convention, see below) but never on `:core:preview:impl`, so the image binaries are physically excluded from release. Only preview / test builds put `:impl` on the classpath, sharing the same sample data with screenshot tests and fake builds.

## Sample data

Sample values are `fake()` extensions on a companion object, declared next to the type they build. Model fakes live in `:core:preview:api`:

```kotlin
fun Timetable.Companion.fake(): Timetable = Timetable(/* sessions across both days, some bookmarked */)
```

A screen's `UiState` carries its own `fake()` beside its declaration in the feature module, assembled from the model fakes through the same mapping the presenter uses, so a preview renders a state production can also produce:

```kotlin
internal fun TimetableListSectionUiState.Companion.fake(): TimetableListSectionUiState {
    val timetable = Timetable.fake()
    return TimetableListSectionUiState(
        timeSlots = timetable.itemsOn(DroidKaigi2026Day.Day1).toTimeSlots(),
        bookmarks = timetable.bookmarks,
    )
}
```

A preview then passes `uiState = TimetableScreenUiState.fake()` and declares no fixture of its own; tests reach the same values through the same call.

## Preview wrappers

`KaigiPreviewTheme` is the envelope every preview renders inside: it applies `KaigiTheme` under the given colour scheme and provides the `PreviewImageResolver` — created lazily via `createGraph<PreviewGraph>().previewImageResolver` — through `LocalPreviewImageResolver`, so `RemoteImage` resolves `preview://` URLs to local drawables.

A preview reaches it one of two ways. Most previews take one fixed scheme, so `KaigiPreviewWrapper` supplies it and the preview carries only an annotation (`@PreviewWrapper`, also from `androidx.compose.ui.tooling.preview`):

```kotlin
@PreviewWrapper(KaigiPreviewWrapper::class)
@Preview
@Composable
private fun AboutScreenPreview() {
    AboutScreen(/* sample */)
}
```

A preview that chooses its own scheme cannot receive it through the wrapper, so it opens `KaigiPreviewTheme` itself (see [Multi-theme previews](#multi-theme-previews)). The `PreviewRequiresWrapperChecker` FIR checker accepts either form and rejects everything else — including a body that opens plain `KaigiTheme`, which would render without the resolver.

## Multi-theme previews

A preview whose content is `@ThemeSensitive` takes its colour scheme through `@PreviewParameter(KaigiSchemeProvider::class)` and passes it to `KaigiPreviewTheme`:

```kotlin
@Preview
@Composable
private fun TimetableScreenPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        TimetableScreen(uiState = /* sample */, onBookmarkClick = {}, /* … */)
    }
}
```

`PreviewParameterProvider` yields one render per value, so the tooling produces one full-size tile per `KaigiColorScheme` and lays the tiles out itself. Nothing declares a preview size: a render surface shrinks to its content but never grows past the device it renders on, so a single render holding every theme side by side would have to name a fixed width, while separate tiles each stay device-sized. `KaigiSchemeProvider` lives in `:core:preview:api`; the parameter type must be an enum, not an inline value class.

The `ThemeSensitivePreviewChecker` FIR checker rejects a theme-sensitive preview that takes no such parameter — see [Enforcement](./enforcement.md).

## Multi-locale previews

A preview whose content resolves a Compose Resources string (see [Localization](./localization.md)) carries `@LocalePreviews` in place of `@Preview`:

```kotlin
@LocalePreviews
@Composable
private fun EventMapScreenPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        EventMapScreen(uiState = /* sample */, onFloorClick = {})
    }
}
```

`@LocalePreviews` is a multi-preview annotation carrying `@Preview(locale = "en")` and `@Preview(locale = "ja")`, so the tooling renders one tile per locale, and per `@PreviewParameter` value where a preview also takes one. It lives in `:core:preview:api` beside `KaigiSchemeProvider`.

The `LocaleSensitivePreviewChecker` FIR checker rejects a locale-sensitive preview that does not carry it — see [Enforcement](./enforcement.md).

The type-safe preview-image enum is generated separately — see [Preview image enum generation](./preview-image-enum.md).

## Wiring (production stays asset-free)

The `droidkaigi.convention.kmp-feature` plugin gives every feature `implementation(project(":core:preview:wrapper"))` in `commonMain`, so `KaigiPreviewWrapper` is referenceable next to each `@Preview`. The wrapper carries `:core:preview:impl` only as `compileOnly`, so the image binaries and `DefaultPreviewImageResolver` never reach production runtime classpaths; where `:impl` is absent, the Metro graph falls back to `NoopPreviewImageResolver` and previews would render blank (which never happens in production, since previews render only on preview / screenshot classpaths).

`:core:preview:impl` still has to be on the classpath that *renders* previews, yet it is absent from `releaseRuntimeClasspath`. The non-production paths that pull it in:

- **Android Studio `@Preview` rendering** — the `kmp-feature` convention adds `"androidRuntimeClasspath"(project(":core:preview:impl"))` (and `compileOnly(project(":core:preview:impl"))` in `androidMain`) so the drawable resources are visible to the IDE preview renderer; the `kmp.compose` primitive adds `"androidRuntimeClasspath"(libs.composeUiTooling)` for `ComposeViewAdapter`. Neither configuration feeds the release runtime classpath.
- **Tests / CI** — depend on `:core:preview:impl` from a test source set (e.g. `jvmTest`). `:core:preview:wrapper`'s `PreviewWiringTest` proves `PreviewGraph` resolves the contributed `DefaultPreviewImageResolver` from there, and maps `preview://` URLs to `DrawableResource`s.

## Android Studio preview rendering

Android Studio renders these `commonMain` previews through the Android target: the `kmp.compose` primitive puts `ui-tooling` (`ComposeViewAdapter`) on the `androidRuntimeClasspath`, and the `kmp-feature` convention adds `:core:preview:impl` there so the drawable resources resolve at render time (see [Wiring](#wiring-production-stays-asset-free)). When the interactive pane cannot render a preview, building and running the app (`./gradlew :app-android:assembleDevDebug`) shows the real screens, and the Roborazzi + ComposablePreviewScanner pipeline renders previews headlessly — see [Testing overview](./testing.md).

## Screenshot tests

A preview is `private`: nothing but the tooling and the screenshot scan ever calls one. Roborazzi + ComposablePreviewScanner honour `@PreviewWrapper` / `@PreviewParameter` so the same previews drive the screenshot tests, on Android — see [Preview screenshot tests](./testing-preview-screenshot.md).

Related: [Testing overview](./testing.md) · [Convention plugins](./build-convention-plugins.md)
