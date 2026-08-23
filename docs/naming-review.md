# Naming review

A name is read far more often than the declaration it labels, and it is the only part of a declaration that reaches a call site. This page states when a name and its type disagree, when a name leaves out what it applies to or which of several like values it holds, and how a reviewer applies the rules. It covers value declarations — properties, parameters, and return values, event callbacks among them. It also states the prefix a composable takes from the call that holds the value it returns. Compose view naming is defined in [Building a screen](./building-a-screen.md#naming-conventions-for-compose-views).

## Name and type

The name states **what the value is**; the type states **how it is represented**. A name that denotes an entity on a general-purpose type breaks that split, because the value is not the entity — it is one attribute of it.

```kotlin
data class TimetableItem(
    val speaker: String,   // rejected: the value is not a speaker, it is a speaker's name
    …
)
```

A property named `speaker` promises a speaker, so one call site writes `item.speaker.name`; the type promises text, so another writes `Text(item.speaker)`. Nothing in the declaration says which reading holds. The name must denote the attribute it carries:

```kotlin
val speakerName: String
```

The check is one question: **is the value of this declaration a `<name>`?** For `speaker: String` the answer is no, and the correction is the `<entity><Attribute>` form — `speakerName`, `roomName`, `sponsorLogoUrl`. The bare entity name belongs to the declaration whose type models the entity, where the answer is yes: `ContributorsScreenUiState.contributors` holds `Contributor` and takes no suffix.

## Consequences

- **Call sites are written against the promise.** A name that reads as an entity invites `speaker.name` and `speaker.iconUrl` — code that has to be rewritten once the type is read.
- **A wrong operation looks right.** `sessions.groupBy { it.speaker }` reads as grouping by speaker and in fact groups by display text, merging two people who share a name. `groupBy { it.speakerName }` puts the defect in view at the line that has it.
- **The entity name is on loan.** `speaker: String` also decides, silently, that a session has one speaker identified by text. When an icon or a second speaker arrives the property is renamed anyway, whereas `speakerName` leaves `speaker` free for the type that will own it.

## Choosing the correction

Two corrections satisfy the rule; the difference is whether the entity already exists in the domain.

| Situation | Correction |
| --- | --- |
| The value is that one attribute — the app displays it and reads nothing else | Rename to `<entity><Attribute>` |
| Call sites already need two attributes together, or compare identity | Introduce the type, keep the bare name |

## Over-qualification

The suffix names the attribute, not the type. `title: String` is already an attribute name and stays as it is; `titleString` and `titleText` restate what the type declares. A suffix is added only where the name currently denotes an entity.

## Under-qualification

A name can agree with its type and still fail, by leaving out which part of the declaration's subject it reaches. The question in [Name and type](#name-and-type) answers yes — the value of `seed: Int` is a seed — and the reader is still left without the one fact needed to pass an argument.

```kotlin
@Composable
fun KaigiNavigationBarScope.KaigiNavigationBarItem(
    selected: Boolean,
    seed: Int,   // rejected: the destination draws an icon and an indicator, and this reaches only the indicator
    icon: @Composable () -> Unit,
)
```

The correction names the element: `indicatorSeed`, `dividerSeed`, `outlineSeed`. The bare word is reserved for a declaration whose whole subject is the one thing it applies to — `SketchShape.seed` seeds the shape it belongs to, and nothing else is in reach.

The question is asked of the whole API, not of one signature. `KaigiNavigationBar` draws a single outline, so `seed` reads unambiguously inside its body; but the bar and its items are written together, and the item already takes an `indicatorSeed`. Read side by side, a bare `seed` on the bar asks which of the two the caller is holding. It is `outlineSeed`.

The rule generalises past seeds: wherever a declaration selects among several parts of what its owner renders, its name states the part. A colour, a shape, or a size reaching one element of a composite is named for that element.

## Category names

Where more than one member of a category is in reach, a name that states the category rather than which member it holds under-qualifies.

```kotlin
val scope = rememberCoroutineScope()           // rejected
val coroutineScope = rememberCoroutineScope()  // required
```

`scope: CoroutineScope` passes the question in [Name and type](#name-and-type) — the value is a scope — and the reader still cannot tell which one. Compose code is full of scopes: `RowScope`, `BoxScope`, `CoroutineScope`, and the component scopes this repository declares (`KaigiNavigationBarScope`, `KaigiSingleChoiceSegmentedButtonRowScope`). The bare word names the category they all belong to, so the name states the member: `coroutineScope`, `rowScope`, `navigationBarScope`.

This is [under-qualification](#under-qualification) seen along the other axis. There the name omits which part of its owner the value reaches; here it omits which of several like values it holds. As there, the question is asked of the surroundings rather than of the declaration alone — a second member of the category arriving in the same file leaves the bare word ambiguous without the declaration itself changing.

## Event callbacks

A callback parameter declared on a view names **the event it reports**, not the work its handler goes on to do. What a parameter names shifts with the layer declaring it, which [Layer and register](#layer-and-register) sets out. Read the name as a phrase and ask whether the phrase describes what happened.

### Verb and object

The object is the thing the verb acts on, and it is a thing that verb can take.

```kotlin
onDescriptionToggleClick: () -> Unit   // rejected: the description reads the same before and after
```

What flips is whether the section is expanded, so the expansion is the noun: `onDescriptionExpansionToggleClick`. Naming a control for the thing it operates on holds only where it does operate on it — `onBookmarkClick` passes, because that control does add and remove the bookmark.

The verb is the one the widget already uses: `Click` for a press, `Change` for a value the user edits, `Dismiss` for a surface the user closes.

### Outcome and input

```kotlin
onMemoCommit: (String) -> Unit   // rejected: whether an edit is worth persisting is not the view's call
```

`Commit` promises that the text is final and will be written. A field reports each edit; a presenter decides which of them reaches storage. A name that states the outcome binds the view to one policy and turns false when the policy changes — a field that writes on focus loss and one that writes through on every keystroke raise the same event.

The name states the input: `onMemoChange`, matching the `onValueChange` it forwards. The outcome keeps its own name where it is decided, in the action the presenter handles.

### Layer and register

| Declared on | Names | Example |
| --- | --- | --- |
| A view — a component or `<Feature>Screen` | The input | `onBackClick`, `onBookmarkClick`, `onFloorClick` |
| The lambda `<Feature>ScreenRoot` forwards | The intent | `onNavigateBack`, `onNavigateToStaff` |
| A `<Feature>ScreenAction` | The change asked of the presenter | `SelectFloor`, `SaveMemo` |

```kotlin
@Composable
fun AboutScreen(
    onOpenStaff: () -> Unit,   // rejected: opening is what the root does with the press
)
```

The parameter sits on a row that can be read and can be pressed, and `onStaffClick` states what reached it; `AboutScreenRoot` supplies the meaning as `onStaffClick = onNavigateToStaff`. Under the intent name the two layers state the same thing twice, and the view carries knowledge it does not hold — the same press reached from a pane, a dialog, or a test binds to something other than opening.

Where a root's lambda does nothing but send one action, the two name one event at two altitudes: **the noun is the same and the verb belongs to the layer**. `onFloorClick` sends `SelectFloor`, `onDayClick` sends `SelectDay`, `onMemoChange` sends `SaveMemo` — Floor, Day and Memo carry across, while `Click` and `Change` report the input and `Select` and `Save` ask for the change. A noun that shifts on the way through is a rename left half-done or a translation the root performs silently: `onUiTypeChangeClick` sends `SwitchToGridView`, and the parameter a caller reads says nothing about a grid.

[Outcome and input](#outcome-and-input) therefore stops at the view rather than reversing below it. An action exists to ask for an outcome, so the verb it takes is an imperative. [Verb and object](#verb-and-object) applies to it unchanged: `Bookmark(id)` reads as adding one where the action toggles.

## Factory prefixes

A composable whose **return value is the value it holds across recompositions** takes the prefix of the call that holds it, as `remember` does. A `retain` call gives `retain<Value>`.

```kotlin
@Composable
fun <A, R> screenChannel(): ScreenChannel<A, R> = retain { ScreenChannel() }         // rejected

@Composable
fun <A, R> retainScreenChannel(): ScreenChannel<A, R> = retain { ScreenChannel() }
```

Without the prefix a call site reads as a fresh value produced at that point, and nothing at the call says the value outlives the recomposition or survives the transient destruction `retain` is there for.

The prefix follows the returned value, not the body. A presenter retains the state it flips and returns a `UiState` built from it; the retained state is not what the caller receives, so the presenter keeps its layer's name.

A factory that installs a mechanism names the mechanism instead, and sits outside this rule. `retainNavEntryDecorator` returns a decorator held by `remember`, and `retain` states what the decorator supplies to every entry under it — which is also what separates it from `rememberSnackbarNavEntryDecorator`.

## Related mismatches

The same disagreement appears wherever the type carries less structure than the name promises.

| Rejected | Read as | Required |
| --- | --- | --- |
| `val featuredSession: TimetableItemId` | a session | `featuredSessionId` — a property holding an identity says so, even though the value class also does |
| `val favorite: Boolean` | a favorite | `isFavorite` — the value answers an assertion, so a noun takes `is` / `has` / `can` |
| `val speaker: List<String>` | one speaker | `speakerNames` — a collection is plural, and the element attribute is named |

An adjective or a participle already reads as an assertion and stands on its own — `enabled`, `dataCleared`. Adding a prefix there is over-qualification, and it contradicts the Compose parameter it feeds (`Button(enabled = …)`). The prefix is required where the bare word also names a thing in this domain: a favorite is an element of `Timetable.bookmarks`, so `favorite` alone reads as one of them.

## Review procedure

For each declaration in the diff whose type is general-purpose (`String`, `Int`, `Boolean`, or a collection of those):

1. Read the name on its own and state the value it promises.
2. Compare that promise against the type. On a mismatch, pick a correction from [Choosing the correction](#choosing-the-correction) and report it together with the call site that reads worst under the current name.
3. On agreement, ask whether the owner has more than one part the value could reach. If it does, the name states which one; see [Under-qualification](#under-qualification).

The type gate covers those three steps only. [Event callbacks](#event-callbacks) applies to every function-typed parameter in the diff: read the name as a phrase, and check its object against what the verb acts on, that it states no outcome the handler decides, and that it sits at the register of the layer declaring it. [Factory prefixes](#factory-prefixes) applies to every composable returning a value. [Category names](#category-names) applies to every declaration in the diff whatever its type: read the name as a common noun, and where it names a category with more than one member in reach, the name states the member.

Domain models in `:core:model` and `UiState` properties come first: their names reach every feature that renders them.

## Scope of static enforcement

Separating an entity word from an attribute word requires the domain vocabulary — `title` and `speaker` are both nouns, and only the conference domain tells them apart. A FIR checker would need that vocabulary as a hard-coded list, and curating the list is the review it would replace. The rule therefore stays at level 3 of the [Enforcement](./enforcement.md) hierarchy.

[Category names](#category-names) stay at level 3 for a second reason: whether a second member of the category is in reach at a given point is not a property a checker can settle. A rule that permitted the bare word while only one member is in scope would turn a correct declaration into a violation the moment an unrelated edit brought a second one in, and the diagnostic would land on a line that edit never touched.

The rule in [Factory prefixes](#factory-prefixes) is decidable in the direct form — a composable whose returned expression is one of the producer calls [`RememberResultMustBeBound`](./enforcement.md#rememberresultmustbebound) already resolves — and a checker would read the same callable ids. What it cannot settle is the general case: a function exposing retained state through an object it builds needs the callee bodies its own body calls, which a module compiled separately does not carry. A rule written against "retains anywhere in the body" instead reports every presenter, which retains correctly under the name its layer gives it.

Related: [Enforcement](./enforcement.md) · [Building a screen](./building-a-screen.md)
