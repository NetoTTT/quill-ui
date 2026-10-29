# Tokens

Quill UI separates primitives into small token groups.

| Group | Purpose |
| --- | --- |
| `QuillColors` | RGB/ARGB/HEX parsing and channel helpers |
| `QuillSpacing` | 4 px spacing scale |
| `QuillTypography` | bitmap-font measurements and scale values |
| `QuillRadii` | corner-radius scale |
| `QuillElevation` | shadow offsets |

## Spacing

```java
int gap = QuillSpacing.SPACE_2;
QuillColumn column = new QuillColumn(x, y, width, gap);
```

The static `QuillTheme` spacing aliases were removed in `0.3.0-alpha.1`; use `QuillSpacing`.

## Typography

```java
int textY = QuillTypography.centeredTextY(20);
int lineY = QuillTypography.LINE_HEIGHT;
```

`TEXT_HEIGHT` is the rendered glyph height. `LINE_HEIGHT` includes the normal one-pixel leading.
The scale constants are multipliers for future scaled-text helpers.

## Radii and elevation

```java
int radius = QuillRadii.MD;
int shadowOffset = QuillElevation.MD;
```

The panel renderer already uses `QuillElevation.PANEL_OFFSET`. Rounded primitives will consume
`QuillRadii` in a later rendering pass; existing square widgets are unchanged.

## Compatibility

Token classes are public API. New widgets accept a `QuillThemeData` instance and fall back to
`QuillThemeData.DEFAULT`; the static `QuillTheme` facade was removed in `0.3.0-alpha.1`.
