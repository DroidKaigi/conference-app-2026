# Voice and format

## Severity classes

| Class | Marker in the review | When |
| --- | --- | --- |
| Blocking | "I'd like this fixed before we merge" / "the important bit" | The change makes CI or the screenshot comparison unstable, a control is dead, a screen diverges from the design it implements, a component is duplicated across features, or a rule the compiler will enforce later is broken now (one component per file, `component/` package, kind suffix, preview). |
| Requested | "Could you …?" / "I'd like …" | A concrete improvement inside the issue's scope: leaner API surface, ownership, semantics, comment shape. Approval may still be given with a request outstanding when the maintainer will merge either way. |
| Nitpick | `[nitpick]` / "Totally optional" / "feel free to leave as is" | Naming, blank lines, redundant annotations, `rememberSaveable`, a close button. Never blocks. |
| Follow-up, maintainer's side | "That's on me to sort out" / "I'll rename it after this PR is merged" / "I'll file a separate issue" | Debt the maintainer owns, or work past the issue's scope. Nothing for the contributor to do. |
| Out-of-scope note | one sentence | Something worth fixing that the change did not set out to do. |

One review has one important finding at most; say which it is. Everything else is labelled as one of the other classes.

In the printed review, the class follows the finding's header: `**path:line** — Requested (the important one)`, `**path:line** — [nitpick]`, `**Follow-up, maintainer's side** —`, `**Out-of-scope note** —`. When posting, the label stays in the comment text so the contributor can see what blocks and what does not.

## Shape of an inline finding

Observation, mechanism, ask. The mechanism is the part that makes the comment reviewable by the next reader.

```
<what the code does now, in one sentence>. <why that is a problem, naming the mechanism: which
API, which test, which reader>. Could you <the concrete change>?

<code block with the suggested shape, when the fix has one>
```

Examples from the record:

- "A process-global `lazy` random makes the screenshot comparison flaky: robot tests capture every step into the directory `compareRoborazziJvm` compares, and the base recording and the PR run are different JVMs, so they pick different scenes and those step images diff on every PR. Could the scene be pinned the way the sketch seed is?" (#330)
- "Switching to the grid view is screen state, not navigation, so it does not belong on the Root's signature. Please make it an `Action` the presenter handles. `onNavigateToSearch` is fine as it is." (#18)
- "The nested `Row` can go: keep `spacedBy(12.dp)` on the outer `Row` and give the `Text` that shows `text` `Modifier.weight(1f)`. Without a weight the ellipsis never kicks in." (#228)

A question is asked as a question when the reviewer is not sure: "Is there a particular reason for the `pinnedHeader` slot? … If you have something in mind that the pinned side needs to draw over the folding part, though, keeping it as is makes sense!" (#210)

## Shape of the summary

1. Thanks, tied to what the change does for the user or the codebase ("the progress line looks great", "the seam is gone, and our app now looks more solid").
2. How many comments were left and which one matters ("The `sceneOfLaunch` one is the important bit … The others are minor.").
3. What happens next ("I'll merge once CI is green", "fine to merge as is", "Review will take a bit longer than usual").
4. For a contributor's PR, a closing thanks. For a first contribution, an invitation to pick up another issue.

Approval example: "Looks good to me! I left one non-blocking question inline — happy to see it addressed in a follow-up, or left as is if it's intentional." (#210)

Changes-requested example: "Thanks for the rework — it's great to see the About screen in its new shape. Comparing against the Figma, I noticed a few spots that still differ in detail (spacing, dividers, text styles, theme colors), so I've left inline comments for each. Could you take another look against the design and adjust them? The hero edge can stay as the follow-up you mentioned." (#197)

## Register

- English, neutral, present tense. Contractions are fine in the summary; the finding itself is declarative.
- Softeners are real, not decorative: "Could you", "I'd like", "I think", "as far as I can tell". A finding the reviewer verified uses "Confirmed", "Verified on", "Measured".
- Emoji appear only in the summary, at most one (🚀 on merge, 🙏🏻 on an ask).
- The reviewer owns their own mistakes in the thread ("Sorry, this text comes from my mistake in the design file", "Correction to my own suggestion", "Sorry for the back and forth").
- When asking for more than the issue named: "Sorry for the extra scope", then an exact list.
- No restating a rule the compiler enforces, no "will not compile", no generic praise without the concrete effect that earned it.

## Reply as the author

When the skill is used to self-review before submission, the same standard applies to the PR body and to replies:

- PR body: a Summary of what changed per module, Design notes for each alternative rejected and why, Verification listing the exact Gradle command that passed and the device cases observed with their clock values. (#263, #103)
- A reply to a finding names the commit that fixes it ("Fixed in b9d5453f: …") or says why no change is needed with the fact that decides it ("`String.lowercase()` is already locale-invariant"). (#255, #253)
- A finding that no longer applies after a rebase is closed as such ("No longer applies: the branch now builds on … from main"). (#275)
