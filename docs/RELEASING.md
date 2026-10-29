# Releasing

1. Bump the version in `gradle.properties`.
2. Build with `gradlew.bat build --console=plain --no-daemon`.
3. Let the `Build and API compatibility` workflow compare the core jar with the current baseline.
4. If the API break is intentional, update `API_LEVEL`, document the migration and update the baseline
   in `.github/workflows/build.yml` after the release.
5. Write two changelog files in `docs/releases/`: `<version>-core.md` and `<version>-extras.md`.
   Keep them self-contained; do not copy core-only changes into the extras file.
6. Publish the GitHub release as a pre-release for alpha/beta channels, using the core file followed
   by the extras file as the body.
7. On Modrinth, use the core file for the Quill UI version and the extras file for the Quill UI
   Extras version. Upload the matching jar to each project; do not upload the sources jars.
8. Update the dependent mod's Quill UI jars and build it again.

The binary compatibility check intentionally fails on removed or changed public signatures. It does
not fail on additive API changes.
