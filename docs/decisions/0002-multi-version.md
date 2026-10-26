# ADR 0002 — Multi-version strategy

- **Status:** proposed
- **Date:** 2026-10-26

## Context

Quill UI is currently built for Minecraft 26.3. Future Minecraft releases may add client APIs that
do not exist in 26.3. A single jar cannot be expected to run across game versions, and a new feature
must not silently break the current target.

## Decision

- Keep one active target per release and publish one artifact per Minecraft version.
- Do not promise cross-version jars.
- Keep the Quill API separate from Minecraft version details.
- Use capability flags for optional version-specific features.
- When a second Minecraft version becomes active, choose between version branches, Stonecutter, or
  core/adapter modules based on the size of the differences.

## Consequences

- Versioned artifacts include a `+mcX.Y` suffix.
- The public API may be identical across versions while adapters differ.
- A feature that cannot be emulated on an older version is exposed as a capability or optional
  module, not as a broken common method.
- CI and compatibility baselines are per Minecraft line.
