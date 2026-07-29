---
name: kotlin-style
description: Code style, formatting and naming conventions for Android projects in Kotlin — 120-char line limit, alphabetical imports, named arguments and trailing commas, blank-line rules, collection suffixes, UseCase/DataSource suffixes, extensions, delegates instead of lateinit, null-free state, no hardcoded strings, plus resource and drawable naming (svg_/ic_/img_/bg_/sel_/gif_). Use this skill whenever writing, generating, refactoring or reviewing Kotlin code for Android, and whenever creating or naming any drawable, string, dimen, color or layout resource — apply it even when the user does not explicitly ask about code style.
---

# Android Kotlin Style

Code and resource formatting conventions for Android projects in Kotlin.

## How to apply

- **When writing new code** — follow silently, don't comment on every rule.
- **In review/refactoring** — name the specific violated rule and give a fixed fragment right away, not a generic "this could be better."
- **On conflict with existing code in the file** — consistency within the project wins. If the file already uses a different style, say so first and ask: bring the file to the convention, or continue in the current style.
- The rules below are about formatting. They don't excuse worse architecture: if a rule and common sense disagree, pick common sense and explain why.

---

## Formatting

### Basic parameters

- Maximum line length — **120** characters. This is also the decision point: if a call doesn't fit in 120, it's laid out in a column per the rule below.
- Imports are sorted **alphabetically as a single list**, no grouping and no blank lines between them.

### Call arguments and declaration parameters

If a call or declaration doesn't fit on one line — each argument goes on its own line, all named, with a trailing comma at the end.

Naming is contagious: if at least one argument is named, all of them are named and the call is laid out in a column.

Exceptions where a name isn't given:
- a trailing lambda — pulled outside the call's parentheses;
- `vararg` arguments;
- the single argument of a one-line call.

### Blank lines

A blank line goes as the first line of a class body and the first line of a `companion object` body.

### Comments

Comment only what can't be derived from the code: non-obvious logic, gotchas, workarounds for specific bugs, the reason "why this way, not the obvious way." Don't restate in words what the code already does.

---

## Naming

### Classes and files

The suffix reflects the architectural role and goes at the end of the name: `UseCase`, `Repository`, `DataSource`, `Mapper`, `ViewModel`, `Adapter`, `Delegate`, `Provider`.

A long, clear name beats a short, cryptic one: a full descriptive name is preferable to one abbreviated past the point of meaning. Abbreviations are allowed only when commonly accepted (`id`, `url`, `api`, `db`, `ui`).

`Manager`, `Helper`, `Utils` aren't roles — they're a sign the responsibility isn't defined. Use them only when a more specific name genuinely can't be found, and flag it in review.

### Collections and streams

The name suffix is the container type (`List`, `Set`, `Map`, `Flow`), not an English plural at the end of the word. Exception — lambda parameters and local variables inside chains (`it`, `item`), where the type is obvious from context.

### Delegates

Initialization that's more expensive than a plain assignment goes into a delegate instead of a manual "field + null check."

`by lazy` — for something computed once and independent of the View lifecycle. For fields tied to the Fragment lifecycle, use specialized delegates (`viewBinding`, `viewModels`, `argument`), otherwise you get a View leak after `onDestroyView`.

---

## Code structure

### Reuse

Extract repeated logic: pure transformations go into `extensions`, work with shared state goes into a separate class with a well-defined role.

Extract starting from the second repetition, not "for the future." An extension with a single call site and no chance of a second one only smears logic across the project.

### return

Don't scatter `return` across a function's body. Acceptable scheme: guard conditions on top, the main result at the bottom — no exits from the middle of nested blocks or loops.

If an early exit is needed inside a loop — use `firstOrNull`/`any`/`none` and return the result at the bottom.

### null

Avoid `null` as a routine state. `null` is justified only when its absence complicates the code (an external API, a genuinely optional model field). Instead of nullable fields — non-null types with a default value: an empty collection, an empty string, `false`.

Don't use `!!`. If a value is guaranteed non-null, that's ensured by the type and the delegate, not by exclamation marks.

`lateinit` — only for fields filled in by a DI container (Dagger/Hilt, `@Inject lateinit var`). In every other case — a delegate (`by lazy`, `by viewModels`, `by viewBinding`) or a constructor.

### Hardcoding

Strings, dimensions, colors, keys, URLs, timings aren't written inline at the call site.

| What | Where |
|---|---|
| User-facing strings | `strings.xml` |
| Paddings, sizes | `dimens.xml` |
| Colors | `colors.xml` / theme |
| Argument, bundle, prefs keys | `const val` in a `companion object` or a keys object |
| Timings, limits, "magic numbers" | `const val` with a descriptive name |

Exception — technical strings that are never shown to the user and are used once (log tags next to their declaration, `BuildConfig` constants).

---

## Resources

A resource's name describes **the resource itself**, not the screen or feature that uses it. A resource outlives a feature. Exception — strings: it's fine to group them by screen with a prefix (`profile_title`, `profile_logout_button`), because text without screen context isn't reused.

### Drawable name scheme

```
[prefix]_[name](_[param](_[value])?)*
```

- `prefix` — required, determines the resource type;
- `name` — what's depicted, `snake_case`;
- `param` — a modifier: `rounded`, `transparent`. Can carry a value (`rounded_12`) or act as a flag (`transparent`).

Prefixes:

| Prefix | Meaning | Format |
|---|---|---|
| `sel_` | state-list / selector | xml |
| `bg_` | container background: shape, layer-list, ripple | xml |
| `svg_` | **any** vector image | xml (`VectorDrawable`) |
| `ic_` | small raster icon | png/webp |
| `img_` | large raster image, a third of the screen or more | png/webp |
| `gif_` | animation | gif |

Order for choosing a prefix:

1. Is it a selector (`<selector>`)? → `sel_`
2. Is it an element's background (shape/layer-list/ripple)? → `bg_`
3. Otherwise the format decides: vector → `svg_`, raster → by size: small icon `ic_`, large image `img_`.

In other words, `sel_` and `bg_` are determined by role and override format; everything else is determined by format.

A consequence worth remembering: when converting a raster icon to a vector, the resource is renamed `ic_*` → `svg_*`, and every `R.drawable.*` reference changes along with it. So it's better to start new icons as vectors right away.

`ic_` remains only for what genuinely can't be expressed as a vector (complex multicolor icons, ready-made assets from third-party SDKs), and requires a set of density folders.

---

## Review checklist

Go through the list before handing off the code:

1. No line longer than 120 characters; imports sorted alphabetically as a single list.
2. Multi-line calls — named arguments + trailing comma.
3. No partially named calls.
4. Blank line at the start of a class body and a `companion object`.
5. Collections with a type suffix (`List`/`Set`/`Map`), not an "s".
6. Classes have a correct role suffix; no unnamed `Manager`/`Helper`.
7. No `!!` and no nullable fields where a default value would fit.
8. `lateinit` only under `@Inject`; delegates everywhere else.
9. `return` only at the top (guard) and at the bottom (result).
10. No hardcoded strings, sizes, keys, "magic numbers".
11. Resource names describe the resource, not the feature; drawable prefix chosen by the `sel_`/`bg_` → format rule.
12. Comments explain "why," not restate "what."
