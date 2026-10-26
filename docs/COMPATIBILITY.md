# Compatibility policy

Quill UI follows a small, explicit compatibility contract so consumers do not need per-mod version
checks.

## Versioning

| Version | Promise |
| --- | --- |
| `0.x` | Alpha/beta; additive changes preferred, but breaking changes are possible between minor versions. |
| `1.0.0+` | SemVer: `1.y` is binary compatible with `1.0`; breaking changes require `2.0.0`. |

Within a stable major line:

1. Existing public methods and classes are not removed or changed incompatibly.
2. New methods/classes may be added in minor releases.
3. Deprecated API remains for at least one minor release before removal at the next major.
4. Behavioural changes are documented; a method's contract changes are treated as breaking.

## API level

`QuillApi.API_LEVEL` is the machine-readable generation. Increment it for a binary-incompatible
change, even during `0.x`. Do not increment it for additive changes.

## Duplicate versions

Fabric Loader resolves duplicate mod IDs by selecting the **latest compatible version** across the
mods folder and nested jars. A lower bundled version is not loaded when a newer compatible one is
present. Consumers should not implement their own “disable the older copy” logic.

This means compatibility is a property of the library, not a workaround in every consumer:

- Keep public signatures compatible.
- Test the new build against the previous release with `japicmp`.
- If a break is intentional, release a new major/minor according to the table above and document it.

## Binary compatibility check

The repository CI compares the built jar with the previous published baseline using `japicmp`:

```text
--only-incompatible --ignore-missing-classes --error-on-binary-incompatibility
```

The baseline is updated when a new compatibility line starts. A red check means an existing public
signature changed incompatibly.

## Deprecation and adapters

Deprecated methods should delegate to the replacement, not disappear:

```java
@Deprecated(forRemoval = true)
public static void oldCall() {
    newCall();
}
```

Adapters are allowed to keep old consumers running; they are not a substitute for a major version
when the old and new contracts cannot coexist.

## What is not promised

- Internal classes, package-private state and renderer implementation details.
- Pixel-perfect visuals across Minecraft versions.
- Compatibility across Minecraft versions; Quill UI targets the version declared in
  `fabric.mod.json`.
