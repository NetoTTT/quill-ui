# Widgets and screen

## QuillButton

`QuillButton` has four variants:

- `PRIMARY`: main action; amber outline.
- `SECONDARY`: normal editing action.
- `DANGER`: destructive action.
- `GHOST`: navigation/utility.

`selected(boolean)` adds a persistent accent bar. `tip(Tooltip)` uses `QuillTooltips.of(title, body,
command)`.

## QuillScrollList<T>

A fixed-row scroll list with a thin scrollbar and keyboard/scroll support:

```java
QuillScrollList<Entry> list = new QuillScrollList<>(
        x, y, width, height, rowHeight,
        this::drawRow,
        (entry, index) -> select(entry)
);
list.replaceItems(entries);
addRenderableWidget(list);
```

Draw rows using the coordinates passed to the row renderer; do not assume absolute positions.

## QuillTabBar

`QuillTabBar` creates equal-width tab buttons and keeps the selected visual:

```java
QuillTabBar tabs = new QuillTabBar(
        x, panelY + 54, width, 20, 4,
        List.of(Component.literal("General"), Component.literal("Advanced")),
        this::switchTab
);
tabs.addTo(this::addRenderableWidget);
tabs.select(0);
```

## QuillScreen

`QuillScreen` provides the panel, 48 px header, optional footer, accent title bar and close button.
Override `buildContent()` and optionally `subtitle()`/`footer()`. Use `panelX`, `panelY`,
`panelWidth()` and `panelHeight()` to derive content coordinates.

## QuillRender

- `panel`: outer shadow/panel/border.
- `header`: header band + bottom divider.
- `tabStrip`: inset strip for tabs.
- `section`: card surface + top highlight + border.
- `inset`: low-contrast inner row.
- `border` and `divider`: primitives.

The framework intentionally does not use vanilla button textures, so consumers get one consistent
look instead of blending vanilla widgets with custom panels.
