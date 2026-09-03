# Review patterns

Each pattern names the pull request where the maintainer applied it, so a reviewer can open the thread and see the finding in context. Patterns are grouped by the question they answer. "As author" marks a pattern taken from the maintainer's reply on their own PR; the standard is the same on both sides.

## Verify before claiming

- Run a workflow on a private repository before approving it; state what was verified (reminder → dedup → unassign lifecycle). #1, #2
- Run the app on a device or emulator and list the observed cases with the clock values used. #207, #255, #263, #312 (as author and as reviewer)
- Pull the branch and check the actual capture size when the concern is a rendering configuration. #142
- Read the library source before asking for a workaround: `rememberInfiniteTransition` already honours `MotionDurationScale`, so a reduced-motion branch is dropped, and the review instruction that prompted it is corrected in a separate PR. #218, #222
- Read the API contract before flagging a leak: `setSingletonImageLoaderFactory` invokes its factory at most once per process. #264
- Measure a browser-dependent behaviour per engine and present the table; take the option that gives a single place to look. #51
- Instrument and measure before removing a guard, and state what the measurement does not cover. #103
- Confirm against the upstream tracker that a workaround is still needed before accepting it. #245
- A claim the reviewer could not verify is stated as such and does not block: "I have a feeling the branch might not be needed, but I don't have time to dig into it right now, so merging as is." #299
- Correct a previous comment of one's own in the thread, with the source link. #57, #218
- Do not report a missing import or a compile failure; a member needs no import, and the branch compiles. #65 (as author)

## Keep the API surface lean

- A child takes a computing lambda, not a `Modifier`: `timeRangeTranslationY: (heightPx: Float) -> Float` keeps the draw-phase read in place and narrows what the caller can do. #184
- A callee takes the narrowed type the caller already established: `floor: Floor`, not `room: Room?` plus a second null check. #228
- A `UiState` carries no field with one fixed value (`title` on `AboutScreenUiState`). #197
- Copy not in the design is removed together with its string resources (supporting text on Licenses and Debug menu). #197
- A `Crossfade` whose value never changes while shown is replaced by the content itself. #228
- A custom `Layout` needs a reason a `BoxWithConstraints` + `Column` cannot express; if there is one, it is a one-line comment. #330
- A CompositionLocal is introduced only for a use case that exists; "might be useful someday" is not one. #330
- A slot (`pinnedHeader`) needs a case the simpler `header` / `content` shape cannot cover. #210
- Handling that a screen can never trigger (`FavoriteAdded` on a screen that only removes) is dropped "to keep the code lean". #312
- A constructor default shared by every instance reads better as a shared constant. #241
- `@Immutable` on an `enum class` is redundant; Compose treats enums as stable. #241
- A `Text` given `LocalContentColor` through a provider does not repeat it as `color`. #56 (as author)
- Migration code for a state only one machine could be in is deleted, not kept. #42 (as author)
- Reusing an existing seam (`currentPlatform`) removes an `expect` / `actual` pair and four files from the diff. #51 (as author)
- Generic handling that is not needed is not added: a regex is relaxed to exactly what the message asks for. #253 (as author)

## Put each thing where it belongs

- Switching a view mode is screen state, so it is an `Action` the presenter handles, not a Root navigation parameter; a real navigation (`onNavigateToSearch`) stays on the Root. #18
- A screen file holds the screen and its previews; every component moves to its own file under `component/` and carries a kind suffix (`DayTabRow`, `TimetableListSection`). #18, #117
- A component's state class lives beside the component (`component/TimetableListSectionUiState.kt`). #18
- A component copied between two features (`SketchCard`) lives once in `core/ui`. #228
- A drawable duplicated between features with a baked-in label moves to `core:ui` with the label drawn as `Text`. #228
- A helper any `listPane()` entry will need (`rememberListDetailSceneAwareLazyListState`) is offered a home in `core/ui`, as a non-blocking suggestion. #245
- System-bar appearance is not a UI component's concern; it belongs to the screen root or, while every screen paints the same band, to `KaigiApp` once. #201
- A platform effect reuses the platform's own entry (`enableEdgeToEdge(statusBarStyle = …)`) instead of threading `Window` and `LocalView`. #201
- A navigation-flavoured effect matches the invocation style of its siblings: `fun interface` with `operator fun invoke`. #171
- An option should be pinned the way a sibling mechanism already pins it (`LocalSketchBaseSeed` for the error scene). #330
- Reduce a global `CompositionLocal` fallback to `error(…)` when the shell always provides it; the guard is worth keeping. #241
- Server integration the issue did not ask for is a separate issue, offered to the contributor as optional. #141

## A component stands on its own

- A composable that emits two flat siblings and relies on the caller's centred `Box` gets its own container. #135
- The `Text` that must ellipsize takes `Modifier.weight(1f)`; a nested `Row` with `SpaceBetween` is removed. #228
- Every component-level composable carries a private `@Preview`. #25
- A ripple must stay clipped to the sketch shape while the border keeps its full width. #125
- A `remember…` function that holds its state with `retain` is a naming mismatch; exposing `offset` on `KaigiClock` and driving the ticker with `produceState` + `collectLatest` fixes both the mismatch and the stale debug offset. #194
- `rememberSaveable` keeps a dialog open across process recreation; offered as "totally optional". #228
- A close button on a dialog makes dismissal clearer; "totally optional". #228

## Semantics and accessibility

- A clickable that acts as a button carries `role = Role.Button`. #56 (as author)
- A selectable item uses `Modifier.selectable(selected, role = Role.Tab / Role.RadioButton)` so the selected state is announced, not only the role. #56 (as author)
- A group of selectable options carries `Modifier.selectableGroup()` on the container. #211 (as author)
- Robots select an option by a localized content description, not by ordinal. #253 (as author)
- A shared `MutableInteractionSource` that separates click handling from ripple indication fixes TalkBack while keeping the ripple clipped; praised as a technique. #326
- Proper semantics matter "for both humans and AI". #345

## Design fidelity

- Compare against the Figma frame and list every difference inline: 52dp row height, `titleSmall` label, 24dp icon + 12dp gap, 16dp chevron, 40dp start inset; 44dp horizontal padding; `onSurfaceVariant` heading; 32/24dp footer padding with Courier Prime 9sp trademark. #197
- Use the hand-drawn primitives the design uses: `SketchHorizontalDivider` (inset 24dp, between rows only), `Modifier.sketchBorder`, 20dp icons. #197
- Vectors must not bake in one scheme's colors; tint from `MaterialTheme.colorScheme` so all five themes follow. #197, #132
- Convert an SVG to vector XML through Android Studio and place it in `commonMain/composeResources/drawable`; set `tint` for the themes. #132
- Fixed English artwork is outlined into the vector, which removes positioning constants and a font gap. #132
- A card around a white-background remote image is white itself, so the boundary is invisible; the reviewer owns a mistake in the design file and says so. #132
- A missing batten under the app bar is asked for; the already-declared follow-up (the scalloped edge) is left as a follow-up. #197
- Squashed images in the screenshot are pointed out with the screenshot attached. #330
- A snapshot diff that shows exactly the intended change is named as the evidence for approval. #283, #289
- A slight screenshot difference that looks fine is accepted and said to be fine. #167

## Screenshot and robot stability

- A process-global `lazy { random }` makes the Roborazzi comparison diff on every PR; the important finding of that review, fixed before merge. #330
- Robot locators use `testTag`, not display strings; new robot code that uses strings is asked to change, and existing ones are tracked in #229 / #260. #228, #289
- A second locale pin duplicating the one in `RunRobotComposeUiTest.jvm.kt` is dropped; the plan is to remove the pin, not to add one. #228
- A locale pin that is a workaround is commented `Temporary:` with the issue that removes it. #209
- Robot captures must stay at a phone-like size (360x800dp at 2x) when the test runner changes. #142
- A capture that assumes a single root breaks when a popup is open; the maintainer pushes the fix to the contributor's branch when near release. #236

## Coroutines, persistence, and lifecycle (as author, in reply to review)

- `read()` rethrows `CancellationException` and swallows only other exceptions. #275
- An update signal is conflated with `BufferOverflow.DROP_OLDEST` so one signal is always retained. #263
- Every `resume` is guarded with `continuation.isActive`. #263
- A missing persisted timetable falls back to an empty one, matching the render path. #255
- A failed preferences write deletes the file it created and rethrows. #281
- A fresh path per save is required because the store re-emits only when the path changes. #281
- Channel handlers start undispatched so a non-suspending handler completes before the next event. #212
- `URL.revokeObjectURL` is deferred with `setTimeout` after the click. #253
- Crop and scale run through one `createBitmap` with a `Matrix`, no intermediate bitmap. #279
- A system broadcast reaches a non-exported receiver; verified after `adb reboot`. #263
- A widget snapshot is dated in system time but evaluated at the shifted instant, like the timeline. #270

## Comments and documentation

- A comment is one line stating the constraint; a multi-line rationale is shortened. #189, #261, #279, #281, #6 (as author)
- A comment that describes what lives in another file is removed. #218
- A comment restating default behaviour (resource fallback) is removed. #218
- A constant between a KDoc and its function detaches the doc; move it above. #260
- A workaround comment is marked temporary and points at its issue. #209
- Docs quote code exactly; the simplification a page is allowed is dropping noise such as `@OptIn`, not altering a literal. #103 (as author)
- Docs examples point at real declarations a reader can open, not illustrative markers. #30 (as author)
- Docs say "during startup, before the first composition", not "at process start", when that is what the code does. #46 (as author)
- Docs are kept in sync with module changes; drift is "easy to miss", and catching it is thanked. #257, #258

## Naming

- `SwitchToGridView` toggles, so `ToggleViewMode`; renamed by the maintainer after merge, no action for the contributor. #225
- `PaneEdgeInset*` becomes `ListDetailPane*` so the tie to `ListDetailSceneStrategy` shows at the call site. #298
- `onNavigateToEventMap` means a real map, so `onOpenVenueWithMap`. #197
- A Japanese action label uses the verb form ("折りたたむ", not "折りたたみ"). #151
- A Kotlin `String.lowercase()` is already locale-invariant; no `Locale.ROOT` change. #253 (as author)

## Formatting and hygiene

- Run `./gradlew spotlessApply` for a lint failure. #185
- A blank line between imports and the first declaration; the ktlint rule is enabled in a separate PR. #203, #224
- Run Reformat Code on the whole XML file and push that result; if the IDE differs, share it so the style setting can be committed. #204
- A failing CI test is named (`SponsorsScreenRobotTest`) and the contributor is asked to look. #132
- A merge conflict is pointed out with the promise to merge once resolved. #155, #259
- Linking the commit for each fix makes re-review easy and is thanked. #132

## Scope

- Work the issue does not name is filed as a separate issue rather than requested. #141, #136
- When the review does ask for more than the issue, the ask is prefixed "Sorry for the extra scope" and lists exactly what to cover. #260, #213
- A large PR near release is merged with details taken over on the maintainer's side. #157, #225, #236
- A debt the maintainer owns (`LocalUriHandler` consolidation, `Room.UNKNOWN` split, the outermost `SoilDataBoundary` outside `KaigiTheme`) is named "mine to pick up", with nothing for the contributor to do. #25, #225, #241
- A timeout raise is accepted with the number negotiated (35 minutes, not 60) and the root cause (cache misses) explained and offered as work either side can take. #127
- An alternative design is offered on a draft "early", with a code sketch and "totally up to you". #338
- A trade-off the contributor argued well is conceded explicitly, naming what is nice about their design. #338

## Approval

- Approval names the user-facing effect the change has ("Now everyone can easily see which sessions are in progress", "the seam is gone"). #218, #288, #302, #311
- Approval with nitpicks says they are non-blocking and whether the maintainer will merge as is. #171, #210, #298
- Copilot findings are triaged for the contributor: which are valid and may go to a follow-up, which need no action. #155, #197, #235
