# CompositionLocal review

A `CompositionLocal` carries a value that **differs by position in the composition**. That is the whole of what it adds over passing the value: any subtree can install its own, and a reader gets whichever one is nearest. A value that is the same wherever it is read gains nothing from it, and gains a freedom no code needs and no test checks — two parts of one screen may quietly disagree.

So the question to answer before adding one is not "would this be convenient to reach" but **"which two positions read different values, and why should they?"**

## The ones the app has

Each is calibration for that question.

| | Why it varies by position |
| --- | --- |
| `LocalSnackbarHostState` | a nav entry owns its host, so a message shows on the screen that raised it |
| `LocalPreviewImageResolver` | a preview draws its images from local drawables where a release build goes to the network |
| `LocalSchemeIsDark` | the theme provides it, so a subtree themed differently reads differently |
| `LocalDeviceTiltSource` | the app installs the platform sensor, a preview reads the level default so a golden does not move |

## When the answer is no

The value still has to reach where it is used, by the seam that fits what it is:

| The value is | It reaches through |
| --- | --- |
| a service the app owns (a clock, a logger, a reporter) | the DI graph, into a [role context](./screen-context.md) |
| data, or anything derived from it | a `UiState` the presenter computes |
| something one component needs and its caller has | a parameter |

Reaching for a `CompositionLocal` because the parameter would thread through several composables is a signal about the component tree, not about the value — see [`UiComponentTakesWhatItReads`](./enforcement.md#uicomponenttakeswhatitreads).

## The default that hides a missing provider

The default decides what a read with no provider installed means, so it states which of three cases applies:

| Default | States |
| --- | --- |
| `error("…")` | a provider is required; reading without one is a wiring mistake, and `LocalSnackbarHostState` names the decorator that installs it |
| `null` | absence is a real case — a composition with no `LocalPreviewImageResolver` loads images from the network |
| a usable value | reading without a provider works, and a provider only narrows the scope: `LocalSchemeIsDark` reads as the light scheme until `KaigiTheme` states otherwise |

The one to justify is the third. A working default turns a forgotten provider into a silently different answer rather than a crash, so it is right only where the un-provided behaviour is one you would choose.

## Review procedure

For each `CompositionLocal` added in the diff:

1. Name the two positions that read different values. If the answer is "none today, but maybe later", it is not one.
2. Read its default. If a reader with no provider gets a usable value, confirm that case is intended rather than a hole.
3. Read its type. A service or a data type belongs to one of the seams in [When the answer is no](#when-the-answer-is-no).

For each **read** of one in the diff, check the reader is UI: a `CompositionLocal` reached from a presenter makes the presenter's result depend on where it was composed, which its unit test cannot vary.

## Scope of static enforcement

Whether a value varies by position is a fact about intent, not about types, so no checker decides it. What the FIR layer does cover is the shape that usually motivates reaching for one: a component taking more than it renders ([`UiComponentTakesWhatItReads`](./enforcement.md#uicomponenttakeswhatitreads)) and Soil reads outside their role ([`SoilReadConfinement`](./enforcement.md#soilreadconfinement)).

Related: [Enforcement](./enforcement.md) · [ScreenContext design](./screen-context.md) · [Clock (KaigiClock)](./clock.md)
