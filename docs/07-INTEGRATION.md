# Integrating Quill UI

Quill UI is a **client-side library mod**. It contains no gameplay content and is not required on
dedicated servers.

There are three integration modes. **Jar-in-Jar (JiJ) is recommended** for mods that work on both
the client and the server.

## Recommended: Jar-in-Jar

The consumer bundles Quill UI inside its own jar. Players install only the consumer mod; Fabric
Loader loads the nested `quillui` mod on the client and ignores it on the server.

```gradle
repositories {
    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = "https://api.modrinth.com/maven"
            }
        }
        filter { includeGroup "maven.modrinth" }
    }
}

dependencies {
    implementation "maven.modrinth:quill-ui:0.1.0-alpha.3"
    include "maven.modrinth:quill-ui:0.1.0-alpha.3"
}
```

If your Loom version uses remapped mod dependencies, replace `implementation` with
`modImplementation`.

Do **not** declare a hard `depends` on `quillui` in a universal mod. Quill UI is marked
`"environment": "client"`, so a hard dependency would make a dedicated server fail even though the
server never uses the UI.

Notes:

- The Loader selects one version when several mods bundle Quill UI; the newest compatible version
  wins. See [COMPATIBILITY.md](COMPATIBILITY.md).
- Include the Quill UI license notice in your distribution. Quill UI is LGPL-3.0-or-later.
- If a player also installs Quill UI standalone, the Loader still loads only one version.

## Standalone

The consumer compiles against Quill UI but does not bundle it. Players install
`quill-ui-0.1.0-alpha.3.jar` on the client. This is appropriate when the consumer is client-only or
when a modpack already ships Quill UI.

```gradle
dependencies {
    implementation "maven.modrinth:quill-ui:0.1.0-alpha.3"
}
```

A universal mod should keep this optional at runtime. Do not declare a hard dependency in
`fabric.mod.json`; gate client-only screen registration behind a check and keep Quill UI classes in
an isolated client class:

```java
if (FabricLoader.getInstance().isModLoaded("quillui")) {
    MyQuillIntegration.register();
}
```

Never reference Quill UI classes from common/server initialization. See
[Fabric's side documentation](https://wiki.fabricmc.net/tutorial:side).

## Hard dependency

Only use a hard dependency when the consumer itself is client-only:

```json
{
  "depends": {
    "quillui": ">=0.1.0-alpha.3"
  }
}
```

For universal mods this mode is not recommended: it forces dedicated servers to install a
client-only library.

## Maven hosting

Modrinth automatically publishes every uploaded version through its Maven repository at
`https://api.modrinth.com/maven`. No separate Maven server is required. Use the project slug
`quill-ui` and the version number or version ID:

```text
maven.modrinth:quill-ui:0.1.0-alpha.3
```

The Modrinth Maven does not provide transitive dependencies; Quill UI currently has no third-party
dependencies beyond Fabric API and Fabric Loader.

## What not to do

- Do not shade Quill UI into a different package; that creates duplicate API state.
- Do not make every consumer implement a custom version-check screen; the Loader and the
  compatibility policy already cover normal version selection.
- Do not use internal implementation classes. The public surface is listed in [API.md](API.md).
