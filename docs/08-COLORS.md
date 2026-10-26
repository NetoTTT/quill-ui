# Colours

Minecraft UI colours are packed **ARGB** integers:

```text
0xAARRGGBB
  AA      red green blue
  opacity
```

`FF` means fully opaque. For example, Quill's amber accent is `0xFFE0A93B`.

You can keep using literals, but `QuillColors` avoids repeated bit arithmetic and makes intent clear:

```java
int accent = QuillColors.hex("#E0A93B");          // 0xFFE0A93B
int translucent = QuillColors.hex("#80E0A93B");   // 50% alpha
int same = QuillColors.rgb(224, 169, 59);         // opaque
int alpha = QuillColors.argb(128, 224, 169, 59);  // 50% alpha
```

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

`QuillTheme` keeps its tokens as compile-time constants so existing consumers remain source
compatible. Use `QuillColors` for new colours, overlays and semantic states; do not add raw bit
expressions to screens.
