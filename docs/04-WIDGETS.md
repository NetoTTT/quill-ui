# Widgets and screen

## QuillButton

`QuillButton` has four variants:

- `PRIMARY`: main action; amber outline.
- `SECONDARY`: normal editing action.
- `DANGER`: destructive action.
- `GHOST`: navigation/utility.

`selected(boolean)` adds a persistent accent bar. `tip(Tooltip)` uses `QuillTooltips.of(title, body,
command)`.

For screens, use the concise factories and `QuillScreen.add(widget, rect)` together with
`contentColumn(...)`:

```java
QuillColumn column = contentColumn(QuillSpacing.SPACE_2);
add(QuillButton.primary("Run action", this::run), column.next(24));
add(QuillButton.ghost("Cancel", this::cancel), column.next(24));
```

Widgets created during `buildContent()` inherit the screen theme through `QuillThemeScope`; the
explicit `theme` constructor remains available when a component needs a different theme.

Variant colours are resolved by `QuillButtonStyle` (`primary`, `secondary`, `danger`, `ghost`,
`selected`, `disabled`). Pass `style(...)` to reuse a custom component style across buttons without
adding a variant.

## QuillLabel

`QuillLabel` is a non-interactive text widget. It never receives focus, and `measure(font, maxWidth)`
returns the preferred size so rows can be sized from their content:

```java
QuillLabel label = QuillLabel.of(Component.literal("Status"), QuillTextStyle.DEFAULT);
QuillSize size = label.measure(font, columnWidth);
```

For pure layout trees, `QuillTextNode` offers the same measurement without a widget.

## Form controls

The input widgets have concise factories with standard control sizes and theme inheritance:

```java
QuillCheckbox checkbox = QuillCheckbox.of(text, false, value -> { });
QuillRadio radio = QuillRadio.of(text, true, value -> { });
QuillSlider slider = QuillSlider.of(label, 0.35F, value -> { });
QuillDropdown dropdown = QuillDropdown.of(placeholder, options, 0, index -> { });
QuillToggle toggle = QuillToggle.of(text, true, value -> { });
QuillTextField field = QuillTextField.of(font, hint);

column.add(control(checkbox));
column.add(control(slider));
setContent(column);
```

Defaults are `220x20`, except `QuillSlider` (`320x20`). They follow `QuillThemeMetrics`, so a theme
can change control sizes globally. The `x`/`y`/`width`/`height` constructors remain available for
absolute layouts.

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

For measured trees, use `QuillCardBox` instead; it measures the heading, padding and child and can
be passed directly to `QuillScreen.setContent(...)`. Paintable nodes render behind the controls.

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
to derive content coordinates. `contentBounds()` returns the flexible area between header and footer using `contentInset()`; `contentBounds(24, 64)` is useful when a fixed control sits below the content.

For a measured content tree, call `setContent(root)` with `text(...)` and `control(...)` leaves. The
tree gets a scrollbar automatically when it is taller than the content area; pass an explicit
content rectangle when a fixed footer sits below it.

```java
@Override
protected void buildContent() {
    QuillFlexBox column = QuillFlexBox.column(QuillSpacing.SPACE_2)
            .crossAxisAlignment(QuillCrossAxisAlignment.STRETCH);
    column.add(text(Component.literal("Status")));
    column.add(control(QuillButton.primary("Save", this::save), 20));
    setContent(column);
}
```

## QuillRender

- `panel`: outer shadow/panel/border.
- `header`: header band + bottom divider.
- `tabStrip`: inset strip for tabs.
- `section`: card surface + top highlight + border.
- `inset`: low-contrast inner row.
- `border` and `divider`: primitives.

The framework intentionally does not use vanilla button textures, so consumers get one consistent
look instead of blending vanilla widgets with custom panels.
