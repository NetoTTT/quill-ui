# ADR 0001 — Package layout

- **Status:** accepted, implementation pending in `0.2.0-alpha.1`
- **Date:** 2026-10-26

## Context

The first usable alpha kept tokens and rendering helpers in the root `com.quillui` package. That
was enough for a small library, but the next steps add theme instances, composition, state, focus
and more widgets. Leaving everything at the root would make the public surface and the future
NeoForge split harder to reason about.

## Decision

Adopt responsibility-based packages:

```text
com.quillui.api
com.quillui.theme
com.quillui.layout
com.quillui.render
com.quillui.widget
com.quillui.screen
com.quillui.action
com.quillui.internal
```

Move tokens to `theme` and rendering helpers to `render` in the same breaking release as
`QuillThemeData`. Do not keep long-lived root wrappers; this is a pre-1.0 alpha.

## Consequences

- Existing imports break once; migration is documented.
- `API_LEVEL` becomes `2`.
- The core/theme/layout packages stay independent of Minecraft client classes.
- Future loader-specific modules can depend on the core instead of forking widgets.
- Contributors have a clear home for new code.
