# Preview screenshot tests

Compose `@Preview`s double as screenshot tests. **ComposablePreviewScanner** discovers every `@Preview`, and **Roborazzi** renders each one through Robolectric and compares it to a recorded golden image. The pipeline runs as an **Android host (unit) test** — no device or emulator.

## How it is wired

A module opts in with a single line — the `droidkaigi.primitive.screenshot-test` convention plugin ([Convention plugins](./build-convention-plugins.md)):

```kotlin
// feature/sessions/build.gradle.kts
plugins {
    alias(libs.plugins.droidkaigiConventionKmpFeature)
    alias(libs.plugins.droidkaigiPrimitiveScreenshotTest)
}
```

The plugin owns everything the module would otherwise copy:

- applies the Roborazzi Gradle plugin (record / verify / compare tasks),
- enables `withHostTest { isIncludeAndroidResources = true }` on the KMP Android library target — the equivalent of the classic `testOptions.unitTests.isIncludeAndroidResources = true`; the source set is `androidHostTest` (not `androidUnitTest`),
- adds the `androidHostTest` dependencies (`:core:testing`, `:core:preview:impl`, and the artifacts the Roborazzi plugin verifies on the module itself),
- turns on Roborazzi's **`generateComposePreviewRobolectricTests`** with the module package (derived from the project path, matching the `android { namespace }` convention):

```kotlin
// droidkaigi.primitive.screenshot-test (excerpt)
generateComposePreviewRobolectricTests {
    enable.set(true)
    packages.set(listOf(screenshotPackage))
    testerQualifiedClassName.set("io.github.droidkaigi.confsched.core.testing.KaigiComposePreviewTester")
    robolectricConfig.set(mapOf("sdk" to "[34]", "qualifiers" to "\"w360dp-h800dp-xhdpi\""))
}
```

The Roborazzi plugin generates the parameterized Robolectric test class itself (`RoborazziPreviewParameterizedTests` under `build/generated/roborazzi/`) — the project maintains no test-class template.

## The custom tester

Roborazzi's default tester (`AndroidComposePreviewTester`) does not fit this module's multiplatform preview discovery, so the plugin points `testerQualifiedClassName` at `KaigiComposePreviewTester` in `:core:testing` (`androidMain`). It is an ordinary class with full IDE support — discovery and capture both live here:

```kotlin
@OptIn(ExperimentalRoborazziApi::class)
class KaigiComposePreviewTester : ComposePreviewTester<JUnit4TestParameter<CommonPreviewInfo>> {

    override fun testParameters(): List<JUnit4TestParameter<CommonPreviewInfo>> {
        val options = options()
        val lifecycleOptions =
            options.testLifecycleOptions as ComposePreviewTester.Options.JUnit4TestLifecycleOptions
        return CommonComposablePreviewScanner()
            .scanPackageTrees(*options.scanOptions.packages.toTypedArray())
            .getPreviews()
            .map { preview -> JUnit4TestParameter(lifecycleOptions.composeRuleFactory, preview) }
    }

    override fun test(testParameter: JUnit4TestParameter<CommonPreviewInfo>) {
        val preview = testParameter.preview
        val id = CommonPreviewScreenshotIdBuilder(preview).build()
        captureRoboImage(filePath = "screenshots/$id.png") { preview() }
    }
}
```

`ComposePreviewTester` is `@ExperimentalRoborazziApi`; the scan packages and Robolectric config flow in from the Gradle DSL via `options()`.

### Why `CommonComposablePreviewScanner`

Previews live in `commonMain`, and `CommonComposablePreviewScanner` (the `:common` ComposablePreviewScanner artifact) discovers them. The `:common` artifact is marked `@Deprecated` upstream, slated for removal in 0.10.0.

`ComposablePreviewScanner` is ClassGraph-based, so it scans **compiled classes** on the JVM classpath. The `androidHostTest` classpath includes the Android target's compiled output, which contains `commonMain`, so `commonMain` previews are visible without a source-set visibility workaround.

## `@PreviewParameter` expansion

`TimetableScreenPreview` takes a `@PreviewParameter(KaigiSchemeProvider::class)` colour scheme (see [Preview & sample assets](./preview.md)). The scanner honours `@PreviewParameter` and expands that single `@Preview` into **one `ComposablePreview` per `KaigiColorScheme`** — five parameterized cases, producing five goldens (`…TimetableScreenPreview_0.png` … `_4.png`).

## Tasks

| Task | Purpose |
| --- | --- |
| `:feature:sessions:recordRoborazziAndroidHostTest` | Render previews and (re)write the goldens under `feature/sessions/screenshots/`. |
| `:feature:sessions:verifyRoborazziAndroidHostTest` | Render and fail on any pixel diff against the committed goldens. |
| `:feature:sessions:compareRoborazziAndroidHostTest` | Render, compare, and emit diff images (no build failure). |

Goldens are written to `feature/sessions/screenshots/` and not committed (`screenshots/` is gitignored) — the baseline a pull request compares against is the artifact CI recorded for its base commit, described under [Continuous integration](#continuous-integration). Because previews already inject sample data and a `PreviewImageResolver`, the screenshots are deterministic and need no network — see [Preview image enum generation](./preview-image-enum.md).

## Desktop and iOS

The same previews are captured on desktop and iOS. Classpath scanning does not exist off the JVM, so `:tools:ksp-processor` generates a per-module **`PreviewRegistry`** — an object enumerating every `@Preview` function (following meta-annotations) as a composable lambda that applies the function's `@PreviewWrapper` and expands its `@PreviewParameter` across the provider's values, with names matching the JVM scanner's screenshot ids so goldens are comparable across platforms. The `screenshot-test` plugin generates a single `PreviewScreenshotTest` into `commonTest`; it calls `capturePreviews` (`:core:testing`), an expect/actual function whose desktop and iOS actuals render every registry entry through `runComposeUiTest` and capture it with Roborazzi's `roborazzi-compose-desktop` / `roborazzi-compose-ios` artifacts. The Android and wasmJs actuals are no-ops.

| Task | Output |
| --- | --- |
| `recordRoborazziJvm` | `screenshots/desktop/` in the module |
| `recordRoborazziIosSimulatorArm64` | `build/outputs/roborazzi/screenshots/ios/` |

The shared robot/presenter tests in `commonTest` also run on desktop (`jvmTest`) and iOS (`iosSimulatorArm64Test`). The Android host-test task is filtered to the Roborazzi-generated preview tests only — the shared tests expect a plain JVM or native environment and fail under Robolectric.

## Continuous integration

Three workflows turn a visual change into a pull request comment. They run the desktop capture, which needs neither the Android SDK nor Robolectric on the runner.

| Workflow | Trigger | Role |
| --- | --- | --- |
| `screenshot-record` | push to `main` | Runs `recordRoborazziJvm` and uploads the goldens as the `screenshots` artifact for that commit. |
| `screenshot-comparison` | pull request | Restores the base commit's artifact, runs `compareRoborazziJvm`, and uploads the `*_compare.png` images. |
| `screenshot-comparison-comment` | completion of the above | Pushes those images to an orphan `companion_<branch>` branch and posts the diff table on the pull request. |

A pull request comment cannot embed an artifact, which is why the images take the detour through a branch. The comment workflow runs on `workflow_run` because a pull request from a fork gets a read-only token; it treats the artifact's file names as untrusted and drops any name outside `[-0-9A-Za-z_./]`.

A visual difference fails the Roborazzi assertion, and the comparison job deliberately carries on: the difference is the report, and whether it is intended is the reviewer's call.

## Scope / limitations

- Web (wasmJs) is not covered: Roborazzi has no wasm artifact.
- Only `:feature:sessions` is wired today; other feature modules opt in by applying `droidkaigi.primitive.screenshot-test`.
- The `:common` scanner artifact is deprecated upstream; see the note above.
- `testAndroidHostTest` does not discover the Robolectric tests the Roborazzi plugin generates — the class compiles into `build/classes/kotlin/android/hostTest/` but the task reports `No tests found`. The Android tasks in the table above therefore capture nothing, and CI reports the desktop images.

Related: [Preview & sample assets](./preview.md) · [Testing overview](./testing.md) · [Convention plugins](./build-convention-plugins.md)
