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

## QuillCard

`QuillCard` is a non-interactive render/layout helper: a bordered surface with an optional compact
heading and a `content()` rectangle for child controls.

```java
QuillCard card = new QuillCard(x, y, width, 96, Component.literal("Status"));
card.render(graphics, font);
QuillRect body = card.content();
```

## QuillPanel

`QuillPanel` composes controls inside a `QuillCard`. It positions children in a vertical column and
offers a two-column action row.

```java
QuillPanel panel = new QuillPanel(x, y, width, height, Component.literal("Settings"), theme);
QuillToggle enabled = panel.add(new QuillToggle(0, 0, 0, 20, Component.literal("Enabled"), true, this::setEnabled, theme));
QuillButton save = new QuillButton(0, 0, 0, 20, Component.literal("Save"), QuillButton.Variant.PRIMARY, this::save, theme);
QuillButton cancel = new QuillButton(0, 0, 0, 20, Component.literal("Cancel"), QuillButton.Variant.GHOST, this::cancel, theme);
panel.addRow(save, cancel);
panel.addTo(this::addRenderableWidget);
```

Render the panel before its widgets in the screen render pass. The panel only lays out and draws
the card; it does not mutate game state.

## QuillToggle

`QuillToggle` is a compact labelled on/off control. It changes its local visual state and calls the
consumer; the screen still sends the real action through its backend.

```java
addRenderableWidget(new QuillToggle(
        x, y, 180, 20,
        Component.literal("Enabled"),
        currentValue,
        value -> QuillCommandSink.get().execute("example set enabled " + value)
));
```

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

`QuillScreen` provides the panel, a 48 px header (about 34 px when `subtitle()` is blank), optional
footer, accent title bar and close button. Override `buildContent()` and optionally
`subtitle()`/`footer()`. Use `panelX`, `panelY`, `contentTop()`, `panelWidth()` and `panelHeight()`
to derive content coordinates.

## QuillRender

- `panel`: outer shadow/panel/border.
- `header`: header band + bottom divider.
- `tabStrip`: inset strip for tabs.
- `section`: card surface + top highlight + border.
- `inset`: low-contrast inner row.
- `border` and `divider`: primitives.

The framework intentionally does not use vanilla button textures, so consumers get one consistent
look instead of blending vanilla widgets with custom panels.
