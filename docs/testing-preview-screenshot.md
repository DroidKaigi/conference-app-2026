# Preview screenshot tests

Compose `@Preview`s double as screenshot tests. **ComposablePreviewScanner** discovers every `@Preview`, and **Roborazzi** renders each one on the **Compose Desktop (JVM) target** and compares it to a recorded golden image. The pipeline runs as a plain JVM test — no device, emulator, or Robolectric sandbox, and no other target.

[Robot scenarios](./testing-robot.md) are captured by the same task, one image per `itShould`, so the states a preview cannot reach — loading, error, and whatever a tap leads to — are covered too.

## How it is wired

Every feature module is covered: the `droidkaigi.convention.kmp-feature` convention applies the `droidkaigi.primitive.screenshot-test` primitive ([Convention plugins](./build-convention-plugins.md)), so a module needs no line of its own.

```kotlin
// droidkaigi.convention.kmp-feature (excerpt)
plugins {
    id("droidkaigi.primitive.kmp")
    id("droidkaigi.primitive.kmp.compose")
    id("droidkaigi.primitive.screenshot-test")
    …
}
```

The primitive owns everything the module would otherwise copy:

- applies the Roborazzi Gradle plugin (record / verify / compare tasks),
- adds the `jvmTest` dependencies (`:core:testing`, `:core:preview:impl`, and the artifacts the Roborazzi plugin verifies on the module itself),
- turns on Roborazzi's **`generateComposePreviewDesktopTests`** with the module package (derived from the project path, matching the `android { namespace }` convention):

```kotlin
// droidkaigi.primitive.screenshot-test (excerpt)
generateComposePreviewDesktopTests {
    enable.set(true)
    packages.set(listOf(screenshotPackage))
    includePrivatePreviews.set(true)
}
```

The Roborazzi plugin generates the parameterized test class itself (under `build/generated/roborazzi/preview-screenshot/`) — the project maintains no test-class template. The generated tests join the module's `jvmTest` source set, beside the shared robot and presenter tests.

## Discovery

Roborazzi's default desktop tester discovers and captures the previews; the project maintains no tester of its own. It scans `androidx.compose.ui.tooling.preview.Preview`, which is the annotation Compose Multiplatform previews carry (see [Preview & sample assets](./preview.md)).

`ComposablePreviewScanner` is ClassGraph-based, so it scans **compiled classes** on the JVM classpath. The `jvmTest` classpath includes the JVM target's compiled output, which contains `commonMain`, so `commonMain` previews are visible without a source-set visibility workaround.

Every preview is `private`, and the scanner skips a private method unless told otherwise, so `includePrivatePreviews.set(true)` above is load-bearing: without it a module's previews scan to nothing and the generated test class fails the run with `No tests found`.

## `@PreviewParameter` expansion

`TimetableScreenPreview` takes a `@PreviewParameter(KaigiSchemeProvider::class)` colour scheme (see [Preview & sample assets](./preview.md)). The scanner honours `@PreviewParameter` and expands that single `@Preview` into **one `ComposablePreview` per `KaigiColorScheme`** — five parameterized cases, producing five goldens (`…TimetableScreenPreview_0.png` … `_4.png`).

## Tasks

Each task runs across every feature module; prefix it with a project path (`:feature:sessions:…`) to scope it to one.

| Task | Purpose |
| --- | --- |
| `recordRoborazziJvm` | Render previews and (re)write the goldens. |
| `verifyRoborazziJvm` | Render and fail on any pixel diff against the recorded goldens. |
| `compareRoborazziJvm` | Render, compare, and emit diff images (no build failure). |

Goldens are written to `<module>/build/outputs/roborazzi/` and are not committed, so `verify` needs a `record` run to compare against; a CI golden store is an open decision. Because previews already inject sample data and a `PreviewImageResolver`, the screenshots are deterministic and need no network — see [Preview image enum generation](./preview-image-enum.md).

## One target only

Screenshots are captured on the JVM alone. Android, desktop, and iOS render the same Compose code through the same Skia backend, so a second and third image of a preview restated what the JVM one already showed. Rendering on the host's Skia rather than through an Android environment also keeps the run free of the per-module Robolectric sandbox cost.

The shared robot/presenter tests in `commonTest` still run on iOS (`iosSimulatorArm64Test`) — what they no longer do there is capture. On the JVM they run in the same `jvmTest` task as the generated preview tests; under a plain `jvmTest` run (no Roborazzi task) the captures are inert.

## Scope / limitations

- Web (wasmJs) is not covered: Roborazzi has no wasm artifact.
- The images come from the host's Skia, not an Android device configuration: there is no SDK level or device qualifier, and a preview's size comes from its `@Preview` options (`widthDp` / `heightDp`) or its content.

Related: [Preview & sample assets](./preview.md) · [Testing overview](./testing.md) · [Convention plugins](./build-convention-plugins.md)
