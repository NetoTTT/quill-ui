# Architecture

Quill UI is a client-side UI library for Fabric. It is deliberately split by responsibility so the
core can later be reused by a NeoForge client port.

## Target package map

| Package | Responsibility | Minecraft/Fabric |
| --- | --- | --- |
| `com.quillui.api` | API level and compatibility metadata | Fabric Loader |
| `com.quillui.theme` | Colour, spacing, typography, radius and elevation tokens; `QuillThemeData` | mostly pure |
| `com.quillui.layout` | Rectangles, insets, columns, rows and grids | pure |
| `com.quillui.render` | Drawing primitives and tooltip composition | Minecraft client |
| `com.quillui.widget` | Buttons, cards, lists, tabs and toggles | Minecraft client |
| `com.quillui.screen` | Screen shell and content lifecycle | Minecraft client |
| `com.quillui.action` | Backend boundary and command sink | Minecraft/Fabric client |
| `com.quillui.map` | Optional world map widget (`quill-ui-extras`) | Minecraft client |
| `com.quillui.hud` | Optional HUD indicator (`quill-ui-extras`) | Minecraft client |
| `com.quillui.internal` | Implementation details; never public | any |

The repository builds two modules:

- `:core` produces `quill-ui-<version>.jar`; it has no reference to map or HUD classes.
- `:extras` produces `quill-ui-extras-<version>.jar`; it depends on core and ships map/HUD only.

The root package should contain no public classes once the layout migration is complete. It may keep
only module-level documentation.

Current classes that move in the breaking layout release:

- `QuillColors`, `QuillSpacing`, `QuillTypography`, `QuillRadii`, `QuillElevation`
  → `com.quillui.theme`.
- `QuillRender`, `QuillTooltips` → `com.quillui.render`.

## Dependency direction

```text
api
theme  -> (no Quill dependencies)
layout -> (no Quill dependencies)
render -> theme, layout
widget -> theme, render, layout, action
screen -> theme, render, widget, layout, action
action -> Minecraft/Fabric only
internal -> all of the above
extras -> core packages + Minecraft client
```

Rules:

1. A public package may not depend on `internal`.
2. `theme` and `layout` must stay usable without a live Minecraft screen.
3. Widgets never contain commands, payloads or gameplay state.
4. Screens compose widgets; they do not reimplement their drawing.
5. The host mod keeps domain payloads and screen content; Quill owns reusable presentation.
6. No public API class is added without Javadoc and a compatibility check.

## Minecraft versions

Quill UI targets one Minecraft version per release. A single jar is not expected to run across
Minecraft versions, and version-specific client features are exposed through fallbacks, capability
flags, or optional modules. See [docs/SUPPORTED-VERSIONS.md](docs/SUPPORTED-VERSIONS.md) and
[docs/decisions/0002-multi-version.md](docs/decisions/0002-multi-version.md).

## Breaking-change policy

The package move and `QuillThemeData` shipped together in `0.2.0-alpha.1`. The `0.3.0-alpha.1` line
removes the remaining compatibility facade:

- `QuillApi.API_LEVEL` is `3`.
- Migration notes list old → new imports.
- The static `QuillTheme` facade was removed; use `QuillThemeData` instances.
- The compatibility workflow compares against the last pre-break baseline.

## Why not one giant root package?

Small frameworks look fine with eight root classes; robust ones do not. Grouping by responsibility:

- keeps API review focused;
- makes the future `quill-ui-core` / `quill-ui-fabric` / `quill-ui-neoforge` split mechanical;
- prevents renderer and widget internals from leaking into the public surface;
- lets contributors know where a class belongs before writing it.
