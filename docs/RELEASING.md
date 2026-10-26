# Releasing

1. Bump the version in `gradle.properties`.
2. Build with `gradlew.bat build --console=plain --no-daemon`.
3. Let the `Build and API compatibility` workflow compare the jar with the current baseline.
4. If the API break is intentional, update `API_LEVEL`, document the migration and update the baseline
   in `.github/workflows/build.yml` after the release.
5. Publish the GitHub release as a pre-release for alpha/beta channels.
6. Upload the main jar (not the sources jar) to the matching Modrinth channel.
7. Update the dependent mod's Quill UI jar and build it again.

The binary compatibility check intentionally fails on removed or changed public signatures. It does
not fail on additive API changes.
