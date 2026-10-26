# Contributing

## Requirements

- JDK 25
- Gradle wrapper (do not install a separate Gradle)
- Minecraft 26.3 / Fabric Loader 0.19.5+ / Fabric API 0.164.0+26.3

## Build and test

```bat
gradlew.bat build --console=plain --no-daemon
```

The build runs the unit tests. The GitHub workflow also runs `japicmp` against the previous public
baseline.

## Code rules

- Public API lives only in the packages listed in `ARCHITECTURE.md`.
- New public classes and methods need Javadoc.
- Use `QuillTheme` / token classes; do not scatter literal colours or spacing in widgets.
- Keep widgets presentational. Actions go through `QuillActionSink` or a host-owned payload.
- Do not add a hard client-only dependency to a universal host mod; use the JiJ guidance in
  `docs/07-INTEGRATION.md`.
- Additive API changes keep `QuillApi.API_LEVEL`; removed or changed signatures increment it.
- Do not commit build output, IDE files or temporary logs.

## Pull requests

1. Explain the user-facing or API change.
2. Include tests for logic that can run headlessly.
3. Update `docs/` when behaviour or integration changes.
4. Mention any API break and the migration path.
5. Keep the diff focused; separate refactors from features.

## License

By contributing, you agree that your contribution is licensed under **LGPL-3.0-or-later**.
