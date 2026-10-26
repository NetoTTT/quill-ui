# Roadmap

## Structural roadmap

This is the agreed order. Stop after each item so the build and visuals can be tested.

1. **Separate token groups** — `QuillColors` (done), `QuillSpacing`, `QuillTypography`,
   `QuillRadii`, `QuillElevation`. Keep `QuillTheme` compatibility aliases.
2. **Theme instance + package layout** — move tokens to `com.quillui.theme` and rendering helpers
   to `com.quillui.render`, then add `QuillThemeData` plus a default theme, replacing static-only
   theme access. Breaking change; one `0.2.0-alpha.1` release; must happen before `1.0.0`.
3. **Composition** — `QuillPanel`/`QuillCard` accepting children and constraints, not only drawing
   a rectangle.
4. **Explicit local state** — a lightweight `QuillState` or screen rebuild after an action; no hooks.
5. **Focus and navigation** — tab order, visible focus, shortcuts and accessibility.
6. **Testing** — headless layout tests, golden/screenshot tests and a demo mod.

### Current position

- Item 1: implemented in `0.1.0-alpha.5`; waiting for build/visual validation.
- Items 2–6: pending.

## UI foundation backlog

- Form controls: text field, slider and dropdown.
- `QuillToast` / notification and empty-state components.
- Responsive helpers and a scroll container.
- Migrate LaborCraft's Employee, Station, Profile, Hiring and Config screens to the framework.

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
