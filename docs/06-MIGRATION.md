# Migrating from LaborCraft's LC UI

Quill UI is extracted from LaborCraft's internal UI. The first migration can be mechanical.

| LaborCraft | Quill UI |
| --- | --- |
| `com.foque.laborcraft.client.ui.LcTheme` | `com.quillui.QuillTheme` |
| `com.foque.laborcraft.client.ui.LcRender` | `com.quillui.QuillRender` |
| `com.foque.laborcraft.client.ui.LcTooltips` | `com.quillui.QuillTooltips` |
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
