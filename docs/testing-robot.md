# Robot pattern tests

End-to-end screen behaviour is tested with the **Robot pattern** in a BDD (behavior-driven development) style, so a scenario reads as behaviour. The scaffolding lives in `:core:testing`; the first real screen test is `TimetableScreenRobotTest` in `:feature:sessions`.

## What

- A **`Robot`** per screen encapsulates that screen's interactions (`setupContent`, taps) and assertions (`check…`) over a `ComposeUiTest`. Scenarios compose them.
- A small **BDD DSL** — `describe` / `doIt` / `itShould` — builds a scenario tree that flattens to one runnable block per `itShould`: each block replays the `doIt` steps in scope, then runs its assertion against a fresh composition, so assertions stay isolated.

## How

Screen-level Compose testing runs through Compose Multiplatform's `runComposeUiTest` (from `org.jetbrains.compose.ui:ui-test`; the desktop actual and — via `compose.desktop.currentOs` — the Skiko native runtime ship alongside it). The BDD DSL itself is pure Kotlin in `commonMain`, so a scenario runs on every target: `iosSimulatorArm64Test` for its assertions, and `jvmTest` for those plus a screenshot of each `itShould`.

A test class extends `RobotTest`, the common base that carries any per-target test scaffolding an actual needs; on the JVM it is plain.

```kotlin
class TimetableScreenRobotTest : RobotTest() {

    @Test
    fun timetable_screen_behaviour() = runRobotTest(…)
}
```

Each `itShould` is captured after its assertions, so the image shows the state the sentence describes. The file is named after the Robot's screen and the scenario — `LicensesScreen.when_the_libraries_fail_to_load___show_the_error_fallback.png` — and lands beside the preview goldens; see [Preview screenshot tests](./testing-preview-screenshot.md) for how the goldens are recorded and compared.

A Robot takes the screen's [`ScreenContext`](./screen-context.md) from a [test graph](./testing-graph.md). Arranging the data and showing the screen are separate steps, so a scenario reads as a state the data layer is in followed by the screen opening on it:

```kotlin
private val graph = createGraph<SponsorsScreenTestGraph>()

fun setupSponsors(sponsors: Sponsors) {
    graph.sponsorsQueryKey.set(sponsors)
}

fun setupContent() {
    setScreenContent {
        context(graph.screenContext) { SponsorsScreenRoot(…) }
    }
}
```

`setScreenContent` is the harness on `Robot`: it stands in for what a nav entry supplies in production — a fresh `SwrClient` and the `LocalSnackbarHostState` that `snackbarNavEntryDecorator` provides — then waits for idle.

Because the Root owns the [`SoilDataBoundary`](./soil-data-boundary.md), the loading and error fallbacks are a Robot concern rather than a presenter one: `hold()` keeps a query suspended so the loading fallback can be asserted, and `failWith(…)` sends the boundary to its error fallback. See [Test graph](./testing-graph.md).

## The real scenario

```kotlin
runRobotTest(robotFactory = { TimetableScreenRobot(this) }) {
    describe("when the timetable has loaded") {
        doIt {
            setupTimetable(sampleTimetable)
            setupContent()
        }
        itShould("show Day1 sessions") {
            checkSessionDisplayed("Day1 A")
            checkSessionDoesNotExist("Day2 A")
        }
        describe("and the Day2 tab is tapped") {
            doIt { clickDayTab(DroidKaigi2026Day.Day2) }
            itShould("swap the list to Day2 sessions") {
                checkSessionDisplayed("Day2 A")
                checkSessionDoesNotExist("Day1 A")
            }
        }
    }
}
```

Complements the per-screen [Preview screenshot tests](./testing-preview-screenshot.md) (which cover static rendering) by exercising interaction and state.

Related: [Testing overview](./testing.md) · [Test graph (TestingScope)](./testing-graph.md) · [Presenter unit tests (Molecule)](./testing-presenter.md)
