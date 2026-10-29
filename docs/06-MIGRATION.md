# Migrating from LaborCraft's LC UI

Quill UI is extracted from LaborCraft's internal UI. The first migration can be mechanical.

| LaborCraft | Quill UI |
| --- | --- |
| `com.foque.laborcraft.client.ui.LcTheme` | `com.quillui.theme.QuillThemeData` |
| `com.foque.laborcraft.client.ui.LcRender` | `com.quillui.render.QuillRender` |
| `com.foque.laborcraft.client.ui.LcTooltips` | `com.quillui.render.QuillTooltips` |
| `...ui.widget.LcButton` | `com.quillui.widget.QuillButton` |
| `...ui.widget.LcScrollList` | `com.quillui.widget.QuillScrollList` |
| `LaborCraftScreen` | `com.quillui.screen.QuillScreen` (or a thin project adapter) |
| hardcoded row positions | `QuillColumn`, `QuillRow`, `QuillGrid` |

## Incremental path

1. Add the Quill UI jar to the project and install it at runtime.
2. Alias/replace theme and drawing classes; screens should keep compiling while visuals change.
3. Migrate one reference screen (LaborCraft's Admin screen) to `QuillColumn`/`QuillGrid`.
4. Migrate Employee, Station, Profile, Hiring and Config one at a time.
5. Keep LaborCraft-specific payloads/actions in LaborCraft; only visual primitives live in Quill UI.

## What stays in the host mod

- payloads and network handlers;
- commands and permission checks;
- config models;
- screen-specific content and translations;
- all gameplay state.

Quill UI only owns client-side presentation and layout.

## Quill 0.2 to 0.3

`0.3.0-alpha.1` raises `API_LEVEL` to `3` and removes the static compatibility facade:

| Removed | Replacement |
| --- | --- |
| `QuillTheme` colour fields | `theme.color(QuillThemeData.ColorRole.X)` or `QuillThemeData.DEFAULT.color(...)` |
| `QuillTheme.SPACE_*` | `QuillSpacing.SPACE_*` |
| `QuillButton` variant-only colours | `QuillButtonStyle`, plus `QuillButton.style(...)` |

New in the same line: `QuillThemeScope` for screen-inherited themes, `QuillTextNode`/`QuillLabel`
for measured text and `QuillScreen.add(widget, rect)` with `contentColumn()`/`contentRow()`.
