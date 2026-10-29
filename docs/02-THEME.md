# Theme tokens

The theme is a `QuillThemeData` instance. Widgets and screens accept one and fall back to
`QuillThemeData.DEFAULT`. Screens may scope a theme through `QuillThemeScope`, so widgets built
inside `buildContent()` inherit it.

```java
QuillThemeData theme = QuillThemeData.DEFAULT
        .with(QuillThemeData.ColorRole.ACCENT, QuillColors.rgb(102, 204, 255));
```

Do not copy hex values into screens.

## Metrics

`QuillThemeData` also carries `QuillThemeMetrics`, read by the concise factories and content helpers:

```java
QuillThemeMetrics compact = new QuillThemeMetrics(
        18, 180, 260, 100, 6, 16, 6
);
QuillThemeData theme = QuillThemeData.DEFAULT.withMetrics(compact);
```

Fields in order: `controlHeight`, `fieldWidth`, `sliderWidth`, `buttonWidth`, `rowGap`,
`contentInset`, `cardPadding`. Button and input factories, `QuillScreen.contentInset()`, and the
padding of `QuillCard`/`QuillCardBox` follow these values.

## Spacing

| Token | Pixels |
| --- | ---: |
| `SPACE_1` | 4 |
| `SPACE_2` | 8 |
| `SPACE_3` | 12 |
| `SPACE_4` | 16 |
| `SPACE_5` | 20 |
| `SPACE_6` | 24 |

## Surfaces and text

| Token | Use |
| --- | --- |
| `PANEL_BG` / `PANEL_BORDER` | outer screen panel |
| `HEADER_BG` | screen header band |
| `SURFACE` / `SURFACE_BORDER` | cards and grouped sections |
| `SURFACE_ALT` | inset rows |
| `TITLE` | primary headings |
| `TEXT` | normal body |
| `TEXT_SOFT` | subtitles |
| `MUTED` / `MUTED_DARK` | hints and footers |
| `ACCENT` / `ACCENT_HOVER` | brand/action accent |
| `DANGER` / `DANGER_BG` | destructive actions/errors |
| `SUCCESS` / `INFO` | status feedback |

## Interactive

| Token | Use |
| --- | --- |
| `BUTTON_BG` / `BUTTON_HOVER` / `BUTTON_BORDER` | secondary button |
| `PRIMARY_BG` / `PRIMARY_BG_HOVER` | primary button body |
| `SELECTED_BG` | selected row/tab |
| `BUTTON_DISABLED_*` | disabled controls |

Changing a token affects every consumer. Keep the 4 px spacing grid and dark surfaces with one
warm accent; semantic colours are information, not decoration.
