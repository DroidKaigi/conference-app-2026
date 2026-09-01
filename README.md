![DroidKaigi 2026](assets/readme_header.png)

# DroidKaigi 2026 official app

[DroidKaigi](https://2026.droidkaigi.jp) is a conference for Android developers, now in its
twelfth year. It runs for three days, 1–3 September 2026, at Bellesalle Shibuya Garden in Tokyo.

The official app is built in the open by the community that attends it — a Compose Multiplatform
app for **Android / iOS / Desktop (JVM) / Web (wasmJs)**. Anyone is welcome to help build it —
see [Contributing](#contributing).

## Features

The DroidKaigi 2026 official app offers a variety of features to enhance your conference
experience:

- **Timetable**: Browse the schedule and bookmark the sessions you want to see.
- **Event map**: Find your way around the venue.
- **Contributors**: Discover the contributors behind the app.

...and more!

## Try it out!

### Web

**[droidkaigi.github.io/conference-app-2026](https://droidkaigi.github.io/conference-app-2026/)**

### Android

<a href="https://play.google.com/store/apps/details?id=io.github.droidkaigi.confsched2026"><img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" height="70" alt="Get it on Google Play"></a>

### iOS

<a href="https://apps.apple.com/app/id6801159161"><img src="https://toolbox.marketingtools.apple.com/api/v2/badges/download-on-the-app-store/black/en-us" height="48" alt="Download on the App Store"></a>

## Contributing

We welcome contributions.

For a step-by-step guide, see [CONTRIBUTING.md](CONTRIBUTING.md). It walks you through everything
from setting up your environment to submitting a pull request.

コントリビューションの詳細な手順については [CONTRIBUTING.ja.md](CONTRIBUTING.ja.md) をご覧ください。
初めての方でもわかりやすいステップバイステップのガイドを用意しています。

> [!NOTE]
> **Issue assignment rules changed this year.** To give as many people as possible a chance to take
> part, each contributor holds **one open Issue at a time** — finish the one you have before
> picking up the next. An assigned Issue with no activity receives a reminder, and is unassigned
> automatically if it stays quiet after that. A comment, or an open pull request linked to
> the Issue (a draft counts), keeps it yours, and you are welcome to pick it up again at any time.
>
> **今年からIssueのアサイン運用が変わりました。**
> より多くの方に参加していただけるよう、一人が同時に持てるIssueは**1件まで**としています。
> 次のIssueに取りかかる前に、いま持っているものを完了させてください。
> アサインされたIssueに動きがない場合はリマインドのコメントが入り、その後も動きがなければ自動的にアサインが解除されます。
> Issueへのコメント、または紐づいたオープンなPull Request（ドラフトでも構いません）があれば、アサインは維持されます。
> 解除されたあとも、いつでも再度お引き受けいただけます。

## Requirements

- **Android Studio**: the latest stable release, from [this page](https://developer.android.com/studio).
- **JDK 21** or higher.
- **Xcode**, to build and run the iOS app.

Gradle comes from the wrapper, so there is nothing else to install.

## Design

The screens are designed in Figma, at
**[DroidKaigi 2026 App UI](https://www.figma.com/design/tpllAs1pnsj03a9rimcFnp/DroidKaigi-2026-App-UI)**.

**Designers**: [@kitakkun](https://github.com/kitakkun), [@chihokotaro](https://x.com/chihokotaro)

## Architecture

This year the architecture and design are written up in detail, and the whole set is published at
**[droidkaigi.github.io/conference-app-2026/docs](https://droidkaigi.github.io/conference-app-2026/docs/)**.

The overall structure has not changed much from last year: Compose Multiplatform shares the UI
across platforms, [Metro](https://github.com/ZacSweers/metro) resolves dependencies at compile
time, [Soil](https://github.com/soil-kt/soil) backs the data layer, and Navigation3 owns the back
stack. If you contributed to the 2025 app, the shape will be familiar.

Start with [Module structure](https://droidkaigi.github.io/conference-app-2026/docs/project-structure)
for how the modules divide up, then the
[Architecture overview](https://droidkaigi.github.io/conference-app-2026/docs/architecture-overview)
for how a screen works end to end.

The pages live in [`docs/`](./docs/index.md); `docs-site/` holds the VitePress setup for previewing
them locally.

## This Year's Challenges

What follows starts from one premise — **AI is a primary author** of this code — and goes on to
what the app gained this year.

### An architecture the compiler keeps in shape

Review does not catch every bug or every drifting convention, so the architecture is built to
reject them earlier: **wherever the compiler can decide a rule, breaking it fails the build.**

Enforcement is applied in a fixed order of preference:

1. **Types**, for what can be made unrepresentable. Context parameters and receivers narrow the
   scopes a declaration may be called from, so a call in the wrong place does not resolve. This
   layer needs no tooling and survives compiler upgrades untouched.
2. **A checker in the Kotlin compiler frontend (FIR)**, for rules that have a yes-or-no answer the
   type system cannot express.
3. **Review and tests**, for what cannot be decided statically.

`:tools:compiler-plugin` implements over twenty FIR checkers on that second layer. They cover the
conventions that keep code readable, the boundaries that confine a role to its own layer, and the
API misuse that would otherwise surface only at runtime; breaking any of them is a compile error.
Every checker is covered by the Kotlin compiler test framework, so the rules themselves are tested
like any other code. Each rule, with the code it rejects and why, is in
[Enforcement](./docs/enforcement.md).

### A structure that keeps changes apart

An AI author edits code quickly, and often in several places at once. Two measures keep those
edits from colliding, and keep each one small enough to review.

**Features meet through interfaces, not shared files.** A feature contributes its navigation entry
through `NavEntryProvider`, a single-method interface, and annotates its implementation with
`@ContributesIntoSet`. Metro collects the implementations into one set at compile time, and
`AppEntryProvider` iterates that set without naming any feature. Kotlin Symbol Processing (KSP)
generates each feature's `NavKey` serializer registration, so that needs no central edit either.
Adding a screen adds files instead of changing existing ones, and features cannot reach one
another: the Gradle module graph has no edge between them, dev-only tooling aside, so the build
enforces the isolation rather than a convention. See
[NavEntry aggregation](./docs/navigation-entry-aggregation.md).

**UI that cannot grow into one large file.** Two checkers keep feature UI divided across files:
`ComposableNestingDepth` caps content lambdas at four levels, so a fifth level has to become its
own composable, and in a screen file `ScreenIsSoleComponentInFile` then requires that composable to
sit in a file of its own. Edits to two components land in two files, so a reviewer reads two small
diffs instead of one large one.

`scripts/new-screen.sh` writes the whole file set a new screen needs, and its output compiles on
all four targets and satisfies the checkers.
[AI-assisted development](./docs/ai-development.md) covers the tooling an AI author leans on.

### Four targets, one shared app

Web (wasmJs) is the fourth target this year. Each platform owns only a small terminal module: it
builds the Metro dependency graph, launches the shared `KaigiApp`, and holds nothing else.
[Platforms & modules](./docs/platforms-and-modules.md) sets out what belongs where.

Navigation3 now reaches all four targets; last year iOS fell back to `navigation-compose`. See
[Navigation overview](./docs/navigation.md).

### Roles carried by context parameters

Each screen is built from three parts — Root, Presenter, and Screen — and a bidirectional
`ScreenChannel` carries an `Action` from the Root to the Presenter and an `ActionResult` back. Each
end of the channel is reachable only with the matching context parameter in scope, so reaching the
wrong end from the wrong layer is a compile error. See
[Architecture overview](./docs/architecture-overview.md).

### Offline-first by default

Queries built with `buildPersistedQueryKey` store the raw server response and restore it on launch,
so a screen renders from cache before the network responds. The compiler rejects a persisted type
that is not `@Serializable`, so the failure appears at build time rather than the first time
persistence runs. See [Soil persistence](./docs/soil-persistence.md).

### iOS: Compose Multiplatform with a Liquid Glass tab bar

Every screen on iOS is drawn by Compose Multiplatform. The one native piece is the root tab bar: a
SwiftUI view laid over the Compose view, its surface the system Liquid Glass material on iOS 26.

Two experimental mechanisms keep the implementation on the Kotlin side. **Swift Export** generates
idiomatic Swift for the Kotlin that Swift calls — a `Flow` arrives as an `AsyncSequence`, a Kotlin
enum as a Swift enum — rather than an Objective-C header. **Swift Package Import** goes the other
way, so Kotlin reaches Apple frameworks without a line of Swift. What is left in Swift is the tab
bar and an entry point. See [iOS overview](./docs/ios.md).

## Trademarks

Google Play and the Google Play logo are trademarks of Google LLC. Apple and the Apple logo are trademarks of Apple Inc., registered in the U.S. and other countries.
