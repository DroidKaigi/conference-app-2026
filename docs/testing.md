# Testing overview

Three layers, each covering a different slice:

- [Presenter unit tests (Molecule)](./testing-presenter.md) — a presenter is a `@Composable` that turns events plus data into a `UiState`, so it is tested as pure logic with Molecule (no UI, fast).
- [Preview screenshot tests](./testing-preview-screenshot.md) — every `@Preview`, and every state a Robot scenario reaches, is rendered and compared to a golden image with Roborazzi.
- [Robot pattern tests](./testing-robot.md) — end-to-end screen behaviour via a BDD (behavior-driven development) style DSL over Compose UI test.

Presenter and Robot tests share their wiring through the [Test graph (TestingScope)](./testing-graph.md): one `TestingScope` graph per screen resolves the contexts from fakes contributed in `:core:testing`.

The compile-time rules are tested separately: [Enforcement checker tests](./testing-enforcement.md) compile Kotlin source with the plugin loaded and assert the diagnostics each FIR checker reports.
