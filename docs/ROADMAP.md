# Roadmap

## Structural roadmap

This is the agreed order. Stop after each item so the build and visuals can be tested.

1. **Separate token groups** - `QuillColors` (done), `QuillSpacing`, `QuillTypography`,
   `QuillRadii`, `QuillElevation`. The static `QuillTheme` aliases were removed in `0.3.0-alpha.1`.
2. **Theme instance + package layout** — move tokens to `com.quillui.theme` and rendering helpers
   to `com.quillui.render`, then add `QuillThemeData` plus a default theme, replacing static-only
   theme access. Shipped in `0.2.0-alpha.1`; must be validated before the next step.
3. **Composition** — `QuillPanel`/`QuillCard` accepting children and constraints, not only drawing
   a rectangle.
4. **Explicit local state** — a lightweight `QuillState` or screen rebuild after an action; no hooks.
5. **Focus and navigation** — tab order, visible focus, shortcuts and accessibility.
6. **Testing** — headless layout tests, golden/screenshot tests and a demo mod.

### Current position

- Item 1: implemented in `0.1.0-alpha.5`.
- Item 2: implemented in `0.2.0-alpha.1`.
- Item 3: implemented in `0.2.0-alpha.2`; waiting for build/visual validation.
- Form controls and screen overlays shipped in `0.2.0-alpha.6`.
- Responsive layout primitives and `QuillScrollView` shipped in `0.2.0-alpha.7`.
- Layout engine phase 1 (`QuillBoxConstraints`, measure/arrange nodes, headless tests) implemented; not wired to screens yet.
- Items 4-6: local state rebuild (`refreshContent`) shipped in `0.3.0-alpha.1`; focus shipped in
  `0.2.0-alpha.8`; widget/golden tests and the public demo mod remain.
- `0.3.0-alpha.1` starts the ergonomics line: measured text (`QuillTextNode`/`QuillLabel`),
  `QuillButtonStyle`, saturating layout sums, the overlay stack, and removal of the static
  `QuillTheme` facade (`API_LEVEL = 3`).
- `QuillNodes` builders, node-based `QuillListView`/tabs, theme sizing metrics and `QuillCardBox`
  also shipped in `0.3.0-alpha.1`.
- The map and HUD moved to the optional add-on module (`quill-ui-extras`); they are its current
  contents, not a fixed scope.

## UI foundation backlog

- Form controls: done in `0.2.0-alpha.5` (text field, slider) and `0.2.0-alpha.6` (dropdown, radio group).
- Overlays and `QuillToast`: done in `0.2.0-alpha.6`.
- Responsive helpers and a reusable scroll container: done in `0.2.0-alpha.7`.
- Migrate LaborCraft's Employee, Station, Profile, Hiring and Config screens to the framework.
- Focus traversal, widget/golden tests and a public demo mod: next.

## Future — NeoForge client port

A NeoForge port is possible and is a future goal, not current work.

Suggested shape:

- `quill-ui-core`: theme, geometry, layout and loader-independent contracts.
- `quill-ui-fabric`: Fabric client entrypoint, events and packaging.
- `quill-ui-neoforge`: NeoForge client entrypoint, events and packaging.

The Minecraft client screen/render classes are shared in concept, but loader APIs, mappings and
client lifecycle differ. A port should reuse the core instead of forking all widgets.

No ETA. Do not start it until the Fabric API surface and compatibility policy are stable.

## Exploratory — Paper/Spigot bridge

A direct Paper port is not possible: Paper is server-side and has no client screen/render access.

A bridge is technically possible:

- A Paper plugin sends UI state over a plugin channel.
- A client companion mod (Fabric first, NeoForge later) receives the state and renders with Quill.
- The client must have the loader, Quill and the companion; vanilla clients need a fallback
  inventory/dialog UI.
- The server owner opts in; the plugin cannot force a client mod to be installed.

This is a separate project with protocol, security, versioning and fallback work. It is **not planned
now**. The no-mod alternative is Minecraft's server-side dialog/inventory UI, which cannot reproduce
the Quill visual language.

## Compatibility work

- Keep `API_LEVEL` accurate.
- Keep `japicmp` green against the previous baseline.
- Update the baseline only when starting a new compatibility line.
