---
name: kitakkun-review
description: Review a pull request, branch, or working-tree diff of this repository the way its maintainer kitakkun reviews — verify before claiming, judge against the owning docs page and the Figma design, keep the API surface lean, and write the review in their register (thanks, the important finding first, blocking vs nitpick vs follow-up made explicit). Use when the user asks to "review like kitakkun", "review this PR as the maintainer would", "what would kitakkun say about this", or wants a pre-submission self-review before requesting a review from kitakkun.
arguments:
  - target
---

# kitakkun-review

Reviews `target` (a PR number, a branch, or nothing for the working tree) against the standard the maintainer applies. Derived from every review comment, review summary, and pull request kitakkun wrote on this repository up to September 2026; the evidence is catalogued in [references/review-patterns.md](references/review-patterns.md) with a PR number per pattern, and the phrasing in [references/voice-and-format.md](references/voice-and-format.md).

This skill never posts to GitHub on its own. It prints the review; posting with `gh pr review` / `gh pr comment` happens only when the user asks for it in this conversation.

## Ground truth

The rule set is already written. Read before judging, and cite the owning page in every finding.

- `.github/copilot-instructions.md` is the index of review perspectives (layer placement, naming, presenter, Soil, error handling, navigation, DI, UI composition, CompositionLocal, localization, injected seams, preview and sample data, tests, build configuration, documentation). Its "Do not report compilation failure", "Architecture rules are compiler-enforced", and "Scope of a review" sections apply verbatim.
- `docs/` pages own the rules; `docs/enforcement.md` lists what the compiler already decides, and a checker-decided rule is never restated in a review.
- `CLAUDE.md` owns comment, wording, and sample-content rules.
- `CONTRIBUTING.md` owns scope: one issue per contributor, roughly 1,000 lines per PR.

What this skill adds is the maintainer's lens on top of those rules: what they verify, what they block on, what they let go, and how they say it.

## Procedure

1. **Collect the change.**
   - PR number: `gh pr view <n> --json title,body,author,url,headRefName,baseRefName,files,statusCheckRollup`, `gh pr diff <n>`, and the linked issue body. Fetch the head with `git fetch origin pull/<n>/head:pr-<n>` and read files at that commit (`git show pr-<n>:<path>`) so `path:line` references match the PR; a local diff against `main` is three-dot (`git diff main...pr-<n>`), never two-dot, or `main` having moved on shows up as changes the PR does not make.
   - Existing discussion, so a raised finding is not repeated: `gh api repos/{owner}/{repo}/pulls/<n>/comments` (inline), `gh api repos/{owner}/{repo}/issues/<n>/comments` (issue comments, where the screenshot-diff bot and Copilot summaries land), `gh api repos/{owner}/{repo}/pulls/<n>/reviews` (review bodies and states).
   - Contributor history for the calibration below: `gh pr list --author <login> --state merged --limit 5`.
   - Branch: `git diff main...<branch>`; working tree: `git diff` plus `git status`.
   - Note screenshot changes under `*/screenshots/` and any screenshot-diff report comment: the snapshot diff is evidence and is named as such in the review.
2. **Fix the scope.** The issue the PR closes is the yardstick. Anything the issue does not name is either a one-sentence follow-up note or, when the maintainer would own it, marked "mine to pick up". What the change itself breaks (call sites, tests, docs left wrong) is in scope wherever it lives: grep `docs/` for every declaration the diff renames, extends, or removes, since the owning pages quote code verbatim.
3. **Read the owning docs** for every perspective the diff touches, in the order `copilot-instructions.md` lists them. Path to perspective, for the common cases:

   | Diff touches | Read |
   | --- | --- |
   | `core/common/**/*Navigator*`, `*NavEntryProvider*`, `NavigatorEffect` | `docs/navigation-navigator.md`, `docs/navigation-root-tab-bar.md`, `docs/navigation-list-detail.md` |
   | `Local*` declarations or `CompositionLocalProvider` | `docs/compositionlocal-review.md` |
   | `feature/**/component/`, `*Screen.kt`, `*ScreenRoot.kt` | `docs/building-a-screen.md`, `docs/naming-review.md` |
   | `*Presenter.kt`, `*UiState.kt`, `*Action*.kt` | `docs/presenter-performance.md`, `docs/error-handling.md` |
   | `core/data/**`, `*Key.kt` | `docs/soil-keys.md`, `docs/soil-persistence.md`, `docs/soil-mutation.md` |
   | `*Robot*.kt`, `*Test.kt`, `screenshots/` | `docs/testing-robot.md`, `docs/testing-presenter.md`, `docs/testing-preview-screenshot.md` |
   | `composeResources/values*/strings.xml` | `docs/localization.md` |
   | `*.gradle.kts`, `libs.versions.toml` | `docs/build-version-catalog.md`, `docs/build-convention-plugins.md` |
   | `docs/**` | `CLAUDE.md` (Documentation) |

   For a PR that adds a `*Screen` / `*ScreenRoot` or restyles one screen, the `screen-review` agent walks that screen's full file set and its output feeds this step; a cross-cutting change that touches several screens skips it.
4. **Verify, do not reason.** Every claim in the review is backed by one of:
   - running it: `./gradlew :app-desktop:compileKotlinJvm :app-web:compileKotlinWasmJs :app-android:compileDevDebugKotlin :app-ios-kotlin:compileKotlinIosSimulatorArm64` and the touched module's `jvmTest`; a Robot or presenter test; the app on a device or the desktop target when behaviour is the question;
   - a green check in `statusCheckRollup` (unit tests, screenshot compare, iOS build, spotless), cited as such ("CI's compare run is green") when the local run is skipped;
   - reading the library source or spec that decides it (Compose `InfiniteTransition`, Coil `SingletonImageLoader`, WHATWG Fetch, androidx issue tracker);
   - the snapshot diff.
   A claim that cannot be verified is stated as a feeling and not as a finding, and does not block the merge.
   When the PR template's Screenshot / Movie cells are empty and the change reaches the UI or the issue lists platforms under its checks, ask for a capture on every platform the change reaches, naming the case to show.
5. **Check design fidelity** when the PR implements or restyles a screen. Compare against the Figma frame named in the issue or PR: dp values, text styles, color tokens, dividers and their insets, which rows carry supporting text, hand-drawn variants (`Sketch*` components, `Modifier.sketchBorder`, `KaigiIcons`) in place of stock Material, vector colors bound to theme tokens across all color schemes.
6. **Apply the lens** in [references/review-patterns.md](references/review-patterns.md). The recurring questions, in the order they usually surface:
   - Is anything here more than the change needs? (a `Modifier` parameter where a computing lambda suffices, a nullable where the caller already narrowed, a `UiState` field with one fixed value, a `Crossfade` with nothing to animate, a custom `Layout` a `Column` expresses, a CompositionLocal with one reading position, a reduced-motion branch Compose already honours)
   - Is each thing in the place that owns it? (screen state as an `Action`, not a Root parameter; system-bar appearance in the shell, not a component; a component shared by two features in `core/ui`; one component per file under `component/` with a kind suffix; a component's `UiState` beside the component)
   - Does the component stand on its own? (its own container rather than relying on the caller's `Box`; `weight(1f)` on the `Text` that must ellipsize; a `Preview` per component)
   - Are the semantics right? (`Role.Button`, `Modifier.selectable` with `Role.Tab` / `Role.RadioButton`, `selectableGroup()` on the container, a content description robots can locate)
   - Will the screenshot comparison stay stable? (no process-global randomness; seeds pinned through `LocalSketchBaseSeed`-style locals; robot locators by `testTag`, not display strings; a temporary locale pin marked temporary with its issue)
   - Are the coroutine and persistence edges handled? (`CancellationException` rethrown; conflated signals with `DROP_OLDEST`; `continuation.isActive` before `resume`; a missing payload falls back the way the render path does; a failed write cleans up what it created)
   - Do the comments state a constraint in one line, and nothing else? (no comment describing another file, no restated default behaviour, a workaround labelled `Temporary:` with its issue, KDoc attached to its declaration)
   - Do the names say what the value is and reveal the mechanism? (`ToggleViewMode` for a toggle, `ListDetailPane*` for what ties to `ListDetailSceneStrategy`, `onOpenVenueWithMap` when "map" is ambiguous, the Japanese action label in verb form; a non-emitting composable carries the `Effect` suffix, and a `Box` appearing around a lone `LazyColumn` in the diff is the symptom of a missing one, since `SingleRootEmission` reads the suffix)
7. **Classify each finding** with the table in [references/voice-and-format.md](references/voice-and-format.md): blocking, requested, nitpick, follow-up on the maintainer's side, or out-of-scope note. Write the class as a label after each finding's `path:line` header. Say which one is the important one.
8. **Write the review** in the format in the same file: summary first, then inline findings as `path:line` blocks, each with observation, mechanism, and a concrete ask (a code block when the fix has a shape). English throughout.

## Calibration

Two judgement calls change the review more than any rule, and the user may tune them here.

- **Near a release**, the maintainer skips fine-grained review, merges, and lists what they will fix in follow-ups on their side. Treat a PR as near-release when the user says so or a version bump is in flight on `main`.
- **A first-time contributor** gets the same findings but more of the "why", an explicit "no need to address in this PR" on every nitpick, and an offer to help.

## Output

Print the full review. When the user asks to post it: the summary goes through `gh pr review <n> --body-file <file>` with `--request-changes` when a blocking finding is present, `--approve` when none is, and `--comment` for a mid-thread reply; inline findings go through `gh api repos/{owner}/{repo}/pulls/<n>/comments` with `commit_id`, `path`, and `line`. Confirm each post.
