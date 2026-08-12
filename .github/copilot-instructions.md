# Copilot review instructions

This repository is a Kotlin Multiplatform / Compose Multiplatform application that builds against **Kotlin 2.4** ([`gradle/libs.versions.toml`](../gradle/libs.versions.toml)). It uses recent language features and enforces its architecture with a custom compiler plugin, so a review that assumes a conventional Kotlin/Android setup reports problems that do not exist. Review design, correctness, and clarity.

## Do not report compilation failure

Do not claim that code "will not compile", that a symbol is unresolved, or that an import is missing. A review sees a diff, not the compilation: source sets, generated sources, and default imports are all outside it, and the build is the authority. Compilation is already covered — the test workflow runs `test jvmTest` across every module, the iOS build workflow compiles the Kotlin, the Swift it exports and the app's Swift sources, and the format check runs Spotless.

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

Link to the relevant section of that page rather than restating the rule.

## Wording of review comments

[`CLAUDE.md`](../CLAUDE.md) at the repository root states the conventions this project applies to comments, documentation, and commit messages: English throughout, neutral and declarative present tense, and no comment that restates what the code already shows. Judge the diff against those conventions, and write review comments in the same register.
