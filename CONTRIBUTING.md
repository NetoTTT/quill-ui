# Contributing

## Requirements

- JDK 25
- Gradle wrapper (do not install a separate Gradle)
- Current Minecraft target: 26.3 / Fabric Loader 0.19.5+ / Fabric API 0.164.0+26.3 (see [SUPPORTED-VERSIONS.md](docs/SUPPORTED-VERSIONS.md))

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

## Commit messages

Use Conventional Commits for new commits:

```text
<type>(<scope>): <imperative summary>

<optional body>

Milestone: <version>
```

- `type`: `feat`, `fix`, `refactor`, `test`, `docs`, `build`, `ci`, `chore`, `perf`.
- `scope` is optional but preferred when it identifies the area: `layout`, `screen`, `widget`,
  `overlay`, `theme`, `map`, `ci`, `api`, `test`, `docs`.
- Summary in the imperative and lower-case, without a trailing period.
- Keep the subject short (about 72 characters). Put details in the body.
- Use `Milestone: 0.2.0-alpha.N` when the commit belongs to a specific alpha.
- Use a `BREAKING CHANGE:` footer when public API compatibility changes.
- Release commits use `chore(release): 0.2.0-alpha.N`.

Examples:

```text
fix(screen): keep accent bar inside collapsed header

The accent bar used a fixed height while the title-only header is shorter.

Milestone: 0.2.0-alpha.7
```

```text
feat(layout): add scissor clip host
test(layout): cover clip input mapping
docs(layout): document scroll affordances
chore(release): 0.2.0-alpha.8
```

The version is primarily carried by the git tag; the `Milestone` footer is optional but useful while
several alpha changes land on `main`. Do not rewrite old history only to apply this format.

## Pull requests

1. Explain the user-facing or API change.
2. Include tests for logic that can run headlessly.
3. Update `docs/` when behaviour or integration changes.
4. Mention any API break and the migration path.
5. Keep the diff focused; separate refactors from features.

## License

By contributing, you agree that your contribution is licensed under **LGPL-3.0-or-later**.
