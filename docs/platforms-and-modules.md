# Platforms & modules

The app targets four platforms — **Android / iOS / Desktop (JVM) / Web (wasmJs)**. This page defines the contracts that decide **where new code goes**. For the module list and the dependency graph, see [Module structure](./project-structure.md).

## What goes in `:core:model`

**model = the place for types shared across the project.** It contains three things:

1. **Core domain models** — entities / value classes / enums.
2. **Soil Key declarations (contracts)** — `*QueryKey` / `*SubscriptionKey` / `*MutationKey` typealiases ([Soil keys](./soil-keys.md)).
3. **Per-screen DI scope markers** (`<Screen>ScreenScope`) — see the caveats for why they are here.

**Key implementations (`Default*Key`) live in `:core:data`.** A feature depends only on the Key "types", not on their implementations:

```text
feature ──► model (types: domain models, *Key)
data    ──► model (implementations: Default*Key implements *Key)
DI (Metro) binds the data implementations to the model types
```

model must depend on neither data nor feature — it is the most downstream module.

**What does not go in model**

- API responses / DB entities → `:core:data` (mapped to model)
- UiState / Action / ActionResult → `feature`
- General-purpose utilities / Compose foundation → `:core:common`

**Caveats**

- The Key typealiases reference `soil.query.*`, so **`:core:model` has an api dependency on the Soil library** (accepted knowingly, since the app is built on Soil).
- **Per-screen DI scope markers** live in model, not `:core:common`: `:core:data` must reference them for per-screen key bindings, and data deliberately does not depend on `:core:common` (which api-exposes Compose / Navigation3 — the data layer stays UI-free).
- **Thin cross-cutting contracts** may also go in model — an interface in model, its implementation on the data/app side. Types and thin contracts only; implementation bodies stay out.

## Persistence goes in `:core:data`

- **Key-value settings** use **androidx DataStore Preferences on every platform**; only the backing differs per platform.
- **Binary blobs** go through the **`FileStorage`** seam (`suspend`, since the Web backing is genuinely asynchronous).
- **Soil query persistence** goes through `buildPersistedQueryKey`, whose `@MustBeSerializable` reified response type makes a non-serializable payload a compile error — see [Soil persistence](./soil-persistence.md).

## What goes in a feature module

One screen group = one feature module: the screen composables, UiState / Action / ActionResult, the screen contexts, and the navigation set (NavKey, entry provider, per-screen graph, navigator interface). The full cast and naming are defined in [Building a screen](./building-a-screen.md). Data fetching goes through model Keys + Soil — a feature never talks to an API client directly.

## What goes in the app layer

`app-shared` holds everything that must see every feature (navigation aggregation, cross-feature navigator implementations); the per-platform entry modules hold only what differs per platform — realizing [`AppGraph`](./di-app-graph.md) and providing the bindings that reach a platform SDK (the crash reporter, the licenses export). Storage backings are not among them: `:core:data` carries its own `androidMain` / `iosMain` / `jvmMain` / `wasmJsMain` actuals for DataStore and `FileStorage`. If code is identical across platforms, it belongs in `app-shared`, not in an entry module.

## Open source licenses

Each platform ships a different dependency set, so the licenses screen must read a per-platform list. The AboutLibraries Gradle plugin is therefore applied to **every entry module**, and each one exports its own `aboutlibraries.json` from its own resolved dependency graph:

| Module | Export task | How the app reads it |
| --- | --- | --- |
| `app-android` | `exportLibraryDefinitions<Variant>` | generated Android resource, `R.raw.aboutlibraries` |
| `app-desktop` | `exportLibraryDefinitionsJvm` | Compose resource, `files/aboutlibraries.json` |
| `app-web` | `exportLibraryDefinitionsWasmJs` | Compose resource, `files/aboutlibraries.json` |
| `app-ios-kotlin` | `exportLibraryDefinitionsIosArm64` | Compose resource, `files/aboutlibraries.json` |
| `app-ios` | `scripts/generate-swift-package-licenses.py` | bundle resource, passed to `KaigiAppHost` |

Each entry module provides its exports through `LicensesJsonProvider`. `:core:data` parses each one into AboutLibraries' `Libs`, merges them behind `LicensesQueryKey`, and never learns which platform it is running on. The screen renders that `Libs` with AboutLibraries' own `LibrariesContainer`, which owns its rows, detail expansion and license dialog, and opens the links it offers through `LocalUriHandler`.

The provider returns a list because one platform has two sources — which is what the rest of this section is about.

### iOS

`app-ios` is an Xcode shell with no Gradle dependency graph of its own, so the Kotlin dependencies are exported from `app-ios-kotlin`, the Gradle module the iOS artifact is built from. The plugin collects only from configurations named `*CompileClasspath` / `*RuntimeClasspath`, which Kotlin/Native does not produce, so the `droidkaigi.primitive.licenses-export` plugin mirrors `iosArm64CompileKlibraries` under a recognised name.

One iOS target stands for both. A Compose resource directory is registered per source set and the two targets share `iosMain`, so the export cannot vary between them; because they compile that one source set they resolve the same libraries, and the device target is the one that ships. Simulator builds package the same export and show the same list, differing only in the artifact suffix of the coordinates that carry one.

That export reaches the app bundle as a Compose resource, through the Swift Export build phase: the Compose Gradle plugin makes `embedSwiftExportForXcode` depend on the resource sync, registering it against the Swift Export binary `:app-ios-kotlin` declares — see [CMP on iOS (embedding)](./ios-cmp-embedding.md).

Swift packages are resolved by Xcode and appear in no Gradle configuration at all, so the iOS build describes them itself. `app-ios/scripts/generate-swift-package-licenses.py` runs as a build phase and writes a second export into the bundle, taking the package set and versions from `Package.resolved` and the license text from the checked-out sources. An SPDX identifier cannot be derived from a license file, so the script maps one per package and fails the build on a package it does not know.

The app reads that export and passes it to `KaigiAppHost`, which hands it to the graph factory — which is why the iOS graph takes a parameter the other platforms do not:

```kotlin
@DependencyGraph(scope = AppScope::class)
internal interface IosAppGraph : AppGraph {
    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides @SwiftPackageLicenses swiftPackageLicensesJson: String): IosAppGraph
    }
}
```

## Generated vs. hand-written

`:tools:ksp-processor` generates what would otherwise rot by omission — [NavKey serializer registration](./navigation-navkey-serializers.md), Soil key ids, the off-JVM preview registry. `NavEntryProvider` / `Navigator` are deliberately hand-written. The preview image enum is generated by a Gradle task, not KSP — see [Preview image enum generation](./preview-image-enum.md).

Related: [Module structure](./project-structure.md) · [Building a screen](./building-a-screen.md)
