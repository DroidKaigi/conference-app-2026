# AI-assisted development

This project assumes **AI is a primary author of the code** (see [Enforcement](./enforcement.md)): the architecture is designed so that incorrect code fails to compile rather than relying on review. On top of those guardrails, the repository ships tooling that AI agents (and humans) use directly.

## Scaffolding a new screen

`scripts/new-screen.sh` generates every file a screen needs, across modules, in one run:

```sh
scripts/new-screen.sh --feature sponsors --screen Sponsors
```

| Where | Generated |
| --- | --- |
| `:core:model` | `<Screen>ScreenScope` (scope marker) |
| `feature/<f>` | [`ScreenContext`](./screen-context.md)/`PresenterContext`, `@GraphExtension` graph, `Action` / `ActionResult` / `UiState` (one file each), Presenter, Screen, ScreenRoot, `NavKey`, `NavEntryProvider`, `ScreenNavigator` |
| `app-shared` | `Default<Screen>ScreenNavigator` |

If the feature module does not exist, pass `--create-module` to scaffold the module itself (build file, `settings.gradle.kts` include, `app-shared` dependency) — without the flag a missing module is an error, so a typo in `--feature` cannot silently create a module. Existing files are never overwritten, and the script prints a summary of what it created. The generated code compiles on all four targets and passes every FIR checker.

For Claude Code, the **`new-screen` skill** (`.claude/skills/new-screen`) wraps the script — asking Claude to "add a sponsors screen" runs it, verifies the build, and reports the summary.

## Guardrails AI relies on

- **FIR checkers** (`:tools:compiler-plugin`) turn the architecture rules into compile errors — role-context gating, forbidden direct `mutate`, Navigator confinement, Soil read confinement, mutation-tag isolation, preview wrapper. The full map lives in [Enforcement](./enforcement.md). For developing and maintaining the plugin itself, the [kotlin-compiler-plugin-skills](https://github.com/kitakkun/kotlin-compiler-plugin-skills) agent skills cover FIR/IR extension work with sourced references.
- **Codegen removes the boilerplate AI would otherwise get subtly wrong**: [NavKey serializer registration](./navigation-navkey-serializers.md), Soil key ids (`SoilIds`), the off-JVM preview registry — all KSP-generated (`:tools:ksp-processor`).
- **Review rules for AI reviewers** encode the same architecture decisions as detectable review criteria — including [Naming review](./naming-review.md), which a checker cannot decide because it is judged against the domain vocabulary.

## Parallel working trees

Agents work in their own `git worktree`, so several checkouts exist side by side. Each one otherwise repeats the Swift Package Manager import — roughly 3 GB and most of a sync — because those results reach neither the Gradle build cache nor the next working tree. Running `scripts/link-swiftpm-cache.sh --install-hook` once makes every working tree added from the clone share a single store. For details, see [SwiftPM import cache across worktrees](./build-worktree-swiftpm-cache.md).

## Verifying changes

- Presenter logic: `runPresenterTest` harness (`:core:testing`) — see [Presenter unit tests](./testing-presenter.md).
- Screen behaviour: the Robot BDD layer — see [Robot pattern tests](./testing-robot.md).
- Rendering: Roborazzi preview screenshots — see [Preview screenshot tests](./testing-preview-screenshot.md).
- Full multi-target check: `./gradlew :app-desktop:compileKotlinJvm :app-web:compileKotlinWasmJs :app-android:compileDevDebugKotlin :app-ios-kotlin:compileKotlinIosSimulatorArm64 :feature:sessions:jvmTest`. The iOS half of that only compiles Kotlin.
- Swift boundary: `app-ios/scripts/check-swift-export.sh` runs Swift Export, compiles the Swift package it generates and type-checks `app-ios/Sources` against the exported module. It needs a Mac but no Xcode project build, and is what a pull request runs. Linking the app is left to the iOS build workflow on `main`.

Related: [Enforcement](./enforcement.md) · [Building a screen](./building-a-screen.md)
