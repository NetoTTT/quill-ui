# Public API

Quill UI has one public API surface. Everything listed here is covered by the compatibility policy
in [COMPATIBILITY.md](COMPATIBILITY.md).

## Packages

| Package | Contents |
| --- | --- |
| `com.quillui.theme` | `QuillThemeData`, `QuillThemeScope`, `QuillSpacing`, `QuillTypography`, `QuillRadii`, `QuillElevation`, `QuillColors` |
| `com.quillui.render` | `QuillRender`, `QuillTooltips`, `QuillScrollHints` |
| `com.quillui.action` | `QuillActionSink`, `QuillCommandSink` |
| `com.quillui.layout` | `QuillRect`, `QuillSize`, `QuillInsets`, `QuillConstraints`, `QuillBoxConstraints`, `QuillAlign`, `QuillLayoutNode`, `QuillNode`, `QuillStack`, `QuillFlex`, `QuillWrap`, `QuillBreakpoints`, `QuillColumn`, `QuillRow`, `QuillGrid`, `QuillSizedBox`, `QuillPaddingBox`, `QuillMarginBox`, `QuillConstrainedBox`, `QuillFlexBox`, `QuillWrapBox`, `QuillStackBox`, `QuillScrollBox`, `QuillLayoutValidator` |
| `com.quillui.screen` | `QuillScreen` |
| `com.quillui.widget` | `QuillButton`, `QuillScrollList`, `QuillScrollView`, `QuillScrollBar`, `QuillClipView`, `QuillWidgetBox`, `QuillMeasuredWidget`, `QuillLabel`, `QuillButtonStyle`, `QuillTabBar`, `QuillCard`, `QuillPanel`, `QuillToggle`, `QuillTextField`, `QuillSlider`, `QuillCheckbox`, `QuillRadio`, `QuillRadioGroup`, `QuillDropdown` |
| `com.quillui.map` | `QuillMap`, `QuillMapZone`, `QuillMapMarker` |
| `com.quillui.hud` | `QuillTargetIndicator`, `QuillTargetIndicatorHud` |
| `com.quillui.motion` | `QuillTicker`, `QuillAnimation`, `QuillCurves`, `QuillCurve`, `QuillTransition`, `QuillMotion` |
| `com.quillui.text` | `QuillText`, `QuillTextStyle`, `QuillTextMeasurer`, `QuillTextNode` |
| `com.quillui.input` | `QuillSelectionStyle`, `QuillSelectionStyles` |
| `com.quillui.i18n` | `QuillLanguage` |
| `com.quillui.overlay` | `QuillOverlay`, `QuillOverlayStack`, `QuillPopupMenu`, `QuillModal`, `QuillToast` |
| `com.quillui.api` | `QuillApi` constants and compatibility helpers |

Classes outside these packages are implementation details. There are no such packages in
`0.2.0-alpha.7`; this boundary exists so future internals can change without becoming API.

`QuillScrollView` is deprecated since `0.2.0-alpha.7`; use `QuillClipView` with a `ContentRenderer`
and `QuillScrollBar` for new screens.

`API_LEVEL` is `3` since `0.3.0-alpha.1`. The static `QuillTheme` facade was removed; use
`QuillThemeData` instances (`QuillThemeData.DEFAULT` for the default palette) and `QuillThemeScope`
for screen-inherited themes.

## Target indicator

`QuillTargetIndicator` uses theme roles by default; raw ARGB values are only low-level overrides.

```java
QuillTargetIndicator indicator = new QuillTargetIndicator(theme, QuillThemeData.ColorRole.ACCENT)
        .setTarget(targetPos)
        .setBackgroundColor(QuillThemeData.ColorRole.SURFACE_HEADER);
```

Register `QuillTargetIndicatorHud` with Fabric's HUD registry, or call `indicator.render(graphics)`
from an existing HUD element. The colour follows the active theme unless `setColor(int)` is used
explicitly.

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
