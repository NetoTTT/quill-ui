# Colours

Minecraft UI colours are packed **ARGB** integers:

```text
0xAARRGGBB
  AA      red green blue
  opacity
```

`FF` means fully opaque. For example, Quill's amber accent is `0xFFE0A93B`.

Prefer `QuillColors` over raw bit literals; it makes intent explicit and keeps alpha handling in one
place:

```java
int accent = QuillColors.rgb(224, 169, 59);         // opaque
int translucent = QuillColors.argb(128, 224, 169, 59); // 50% alpha
int parsed = QuillColors.fromHex("#E0A93B");        // external/config input
```

Raw hex literals are reserved for the built-in palette in `QuillThemeData` and for parsing values
that arrive as text. Widget and screen code should use `rgb`/`argb` or theme roles.

## Helpers

| Method | Result |
| --- | --- |
| `QuillColors.rgb(r, g, b)` | opaque ARGB |
| `QuillColors.argb(a, r, g, b)` | ARGB from components |
| `QuillColors.fromHex("#RRGGBB")` | opaque ARGB |
| `QuillColors.fromHex("#AARRGGBB")` | ARGB with alpha |
| `QuillColors.toHex(color)` | `#AARRGGBB` |
| `QuillColors.toRgbHex(color)` | `#RRGGBB` |
| `QuillColors.alpha/red/green/blue(color)` | channel value |
| `QuillColors.withAlpha(color, 0..255)` | replace alpha |
| `QuillColors.withAlpha(color, 0.0..1.0)` | replace alpha from float |

Hex parsing accepts `#RRGGBB`, `RRGGBB`, `#AARRGGBB`, `AARRGGBB`, `#RGB`, and optional `0x` prefixes.

## In the theme

Use `QuillColors` for raw colour construction and a `QuillThemeData` instance for semantic
colours; do not add raw bit expressions to screens. The static `QuillTheme` aliases were removed in
`0.3.0-alpha.1`.
