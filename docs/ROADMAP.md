# Roadmap

## Current focus — UI foundation

- `QuillCard` / `QuillSection` for reusable content grouping.
- Form controls: text field, toggle, slider and dropdown.
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
