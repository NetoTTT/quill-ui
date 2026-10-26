# Public API

Quill UI has one public API surface. Everything listed here is covered by the compatibility policy
in [COMPATIBILITY.md](COMPATIBILITY.md).

## Packages

| Package | Contents |
| --- | --- |
| `com.quillui` | `QuillTheme`, `QuillRender`, `QuillTooltips`, `QuillColors` |
| `com.quillui.action` | `QuillActionSink`, `QuillCommandSink` |
| `com.quillui.layout` | `QuillRect`, `QuillInsets`, `QuillColumn`, `QuillRow`, `QuillGrid` |
| `com.quillui.screen` | `QuillScreen` |
| `com.quillui.widget` | `QuillButton`, `QuillScrollList`, `QuillTabBar`, `QuillCard`, `QuillToggle` |
| `com.quillui.api` | `QuillApi` constants and compatibility helpers |

Classes outside these packages are implementation details. There are no such packages in
`0.1.0-alpha.4`; this boundary exists so future internals can change without becoming API.

## Binary level

`QuillApi.API_LEVEL` is the binary API generation:

- It increases when an existing public signature is removed or changed incompatibly.
- It does **not** increase for additive classes, methods, or behaviour-preserving changes.
- Consumers can call `QuillApi.supports(level)` when they need to support older runtimes.
- `QuillApi.version()` returns the loaded mod version.

The mod version and the API level are related but not identical. A bug-fix release may keep the same
API level; a breaking release must increment it.

## Rules for consumers

- Use public entry points; do not mixin into Quill UI internals.
- Treat widgets as owned by Quill UI; compose them instead of subclassing implementation details.
- Send actions through `QuillActionSink` or a project-specific payload; never mutate server state
  from a screen.
- Test against the oldest Quill UI API level you claim to support.
