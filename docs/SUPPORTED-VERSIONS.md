# Supported Minecraft versions

## Current matrix

| Quill UI | Minecraft | Fabric Loader | Status |
| --- | --- | --- | --- |
| `0.2.0-alpha.1` | 26.3 | 0.19.5+ | Current |

## Policy

- A Quill UI release targets **one Minecraft version**; there is no expectation that one jar runs on
  multiple Minecraft versions.
- The current version is the primary target. When a new Minecraft version is adopted, the previous
  version may be supported for one transition cycle, then dropped.
- Each Minecraft line gets its own build artifact, GitHub release and Modrinth version.
- Changes to Minecraft classes are handled by version-specific adapters, not by leaking version
  checks into every widget.

## Version naming

Use SemVer for the Quill API and a build suffix for the game version:

```text
0.2.0-alpha.1+mc26.3
0.2.0-alpha.1+mc26.4
```

The two artifacts may expose the same Quill API level while using different Minecraft adapters.

## When Minecraft adds a feature

Example: 26.4 adds a new image-rendering helper that 26.3 does not have.

Do **not** add a 26.4-only method to the common public API and leave 26.3 broken.

Choose one:

1. **Implement a fallback** in the 26.3 adapter using existing primitives. The common method stays
   available and `QuillApi.supports(feature)` returns true on both.
2. **Expose a capability** (`QuillFeatures.IMAGE_RENDER`) that returns false on 26.3. Consumers that
   need it branch explicitly and provide their own fallback.
3. **Ship an optional module** (`quill-ui-image`) only for 26.4. Universal hosts must treat it as an
   optional client dependency.

The common API should stay stable across supported versions wherever practical. If a feature cannot
be emulated and is essential, make the new Minecraft version the minimum supported version in the
next major release.

## Build strategy

Start with one source tree per active Minecraft version. When 26.4 arrives, evaluate:

- **Version branches** (`mc/26.3`, `mc/26.4`) — simple for a small number of supported versions.
- **Stonecutter** or a similar multi-version Gradle tool — one source tree with versioned source
  sets and preprocessing.
- **Core + adapters** — `quill-ui-core` (tokens/layout/contracts) plus `quill-ui-mc26_3`,
  `quill-ui-mc26_4` and later loader adapters.

The architecture already separates `theme`/`layout` from `render`/widgets so this split remains
possible.

## CI

- Build and test each supported Minecraft line.
- Run API compatibility against the previous release **for that Minecraft line**.
- Publish separate jars; never upload a 26.3 build to a 26.4 version entry.
