# Localization

The app ships two locales: English is the base resource set and Japanese is the translation. Every string the UI draws is a Compose Resources string; a string literal in a composable is a defect.

## Where strings live

Each module owns the strings its own code draws, under its source set:

```text
feature/eventmap/src/commonMain/composeResources/
├─ values/strings.xml        # English, the base
└─ values-ja/strings.xml     # Japanese
```

The `droidkaigi.primitive.kmp.compose` [convention plugin](./build-convention-plugins.md) derives the generated `Res` class package from the module path, so `:feature:eventmap` reads its own strings through `io.github.droidkaigi.confsched.feature.eventmap.generated.resources.Res` and no module declares that package itself. Compose Resources generates the class only where a `composeResources` directory exists.

A string with no `values-ja` entry falls back to the English base, so a missing translation shows through rather than failing the build. A string whose value is the same in both locales is declared in `values/` only; a `values-ja` entry repeating it is redundant.

## Reading a string

```kotlin
KaigiTopAppBar(title = stringResource(Res.string.event_map_title))
```

A count takes a plural resource, whose categories differ per language — English declares `one` and `other`, Japanese only `other`:

```kotlin
Text(pluralStringResource(Res.plurals.contributors_count, count, count))
```

Resolution belongs at the point of display. A presenter is `@Composable` and may read a resource where the string is part of the state it builds, but a string the screen draws unconditionally is UI chrome and is read in the screen.

## Text the server supplies

The conference API returns session titles in both languages. That pair travels to the UI as `MultiLangText` and is resolved where it is drawn:

```kotlin
TimetableItemCard(title = item.title.current(), /* … */)
```

Resolving in the data layer would bake a display decision into the [Soil](./soil-keys.md) cache, so a locale change would keep showing the previous language until the cache was invalidated. `MultiLangText.current()` reads `androidx.compose.ui.text.intl.Locale`, which is the same locale Compose Resources resolves its own strings against, so both kinds of localized text always agree.

## Previews

A composable that reads a string resource or a `MultiLangText` is locale-sensitive, and the compiler propagates that to every caller. Such a preview carries `@LocalePreviews` so the tooling renders it under both locales — see [Multi-locale previews](./preview.md#multi-locale-previews) and [Enforcement](./enforcement.md).

## Text that is not localized

- `:feature:debug` — developer tooling, present in debug builds only.
- Preview and sample values, which are placeholders rather than product copy.
- Values carrying no words: a room name, a floor label, a time range.

Related: [Preview & sample assets](./preview.md) · [Enforcement](./enforcement.md)
