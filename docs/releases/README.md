# Release notes

Each release keeps two separate changelog files:

- `<version>-core.md` - everything that changed in `quill-ui`.
- `<version>-extras.md` - everything that changed in `quill-ui-extras`.

The GitHub release body is the two files combined, starting with the core file. Copy the extras file
to the Modrinth **Quill UI Extras** version and the core file to the Modrinth **Quill UI** version.
This keeps each project's changelog self-contained.

Rules:

- Write to the user, not to the commit log.
- Use `Added`, `Changed`, `Fixed` and `Breaking changes` sections.
- Do not mention core-only changes in the extras file.
- Do not mention extras-only changes in the core file.
- Always include the jar name, the required core version (for extras) and the target versions.
- Keep breaking changes and migration notes in the core file.
