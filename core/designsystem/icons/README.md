# Hand-drawn icons

Each vector drawable here is one icon's only source. `:core:designsystem`'s `generateKaigiIcons`
task turns every file into a `KaigiIcons.Default.<Name>` extension under
`build/generated/icons`, so no `ImageVector` is written by hand and none is committed.

`android:pathData` is SVG path syntax, which `addPathNodes` parses unchanged, so the task moves
geometry rather than reshaping it.

## Adding or changing an icon

1. Export the icon's 144-box master from the design file and write it here as `hd_<name>.xml`.
2. Run `./gradlew :core:designsystem:generateKaigiIcons`.
3. Reference it as `KaigiIcons.Default.<Name>` — `hd_play_circle.xml` becomes `PlayCircle`.

The file name decides the property name, so renaming a drawable renames the icon.

## Why the drawable is the source

The icon set began as hand-converted Kotlin with no upstream file, so a re-export could not be
diffed against what shipped. Three icons had silently lost a subpath in that conversion —
`hd_location_on` its inner dot, `hd_play_circle` its triangle, `hd_settings` its centre — and
nothing in the build could see it: the geometry compiled, rendered, and passed screenshot tests
as a shape that was merely wrong. With the drawable as the source, a re-export is a diff.
