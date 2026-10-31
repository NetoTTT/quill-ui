# Widgets and screen

## QuillButton

`QuillButton` has four variants:

- `PRIMARY`: main action; amber outline.
- `SECONDARY`: normal editing action.
- `DANGER`: destructive action.
- `GHOST`: navigation/utility.

`icon(QuillIcon)` or `icon(ItemStack)` draws a glyph inside the button; with a label the icon sits before the text, and `iconGap(int)` controls the spacing. Icon-only buttons should carry a tooltip. `selected(boolean)` adds a persistent accent bar. `tip(Tooltip)` uses `QuillTooltips.of(title, body,
command)`. `enabled(boolean)` is the fluent replacement for the public `active` field.

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

## QuillChip and header accessories

`QuillChip` is a compact read-only pill with optional icon and text, used for short status values:

```java
QuillChip balance = QuillChip.of(Component.literal("1.240"))
        .icon(new ItemStack(Items.GOLD_INGOT))
        .accent();

@Override
protected QuillLayoutNode headerAccessory() {
    return QuillNodes.widget(balance);
}
```

The accessory is measured, right-aligned before the close control and rebuilt with the content
on resize and refresh. Keep it short; long titles leave less room for it.

## QuillLabel

`QuillLabel` is a non-interactive text widget. It never receives focus, and `measure(font, maxWidth)`
returns the preferred size so rows can be sized from their content:

```java
QuillLabel label = QuillLabel.of(Component.literal("Status"), QuillTextStyle.DEFAULT);
QuillSize size = label.measure(font, columnWidth);
```

Styles chain fluently from the ambient theme: `label(c).muted()`, `.title()`, `.accent()` or
`.color(role)`. Wrap with `control(...)` to place the label in a measured tree.

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
`setSelected(item)`/`selectedItem()`/`isSelected(item)` keep selection by item value, so replacing
the list or filtering it does not leave a stale index. `scrollOffset()`/`setScrollOffset(int)` let a
screen preserve the row scroll across a content rebuild.

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

## Measured tree helpers

`QuillNodes` builds measured trees without repeated constructor noise:

```java
QuillFlexBox root = QuillNodes.column(QuillSpacing.SPACE_2,
        QuillNodes.row(6, tabWork, tabConfig),
        QuillNodes.flexible(body, 1),
        QuillNodes.row(QuillSpacing.SPACE_2,
                QuillNodes.sized(170, 24, control(save)),
                QuillNodes.flexible(QuillNodes.spacer()),
                QuillNodes.sized(96, 24, control(close)))
);
setContent(root);
```

`flexible(node)` and `flexible(node, weight)` are unwrapped by `QuillFlexBox.add(...)`, so weighted
children fit in the same list as fixed ones. `fixedWidth`/`fixedHeight` keep one axis content-sized.

Tabs are a node too:

```java
root.add(QuillNodes.tabs(
        theme(),
        6,
        List.of(literal("Work"), literal("Config"), literal("Storage")),
        tab.ordinal(),
        index -> switchTab(Tab.values()[index])
));
```

`tabs(...)` builds equal-width primary/ghost buttons, so the screen does not calculate tab widths.

## QuillListView<T>

A node-based list: each row is a measured layout tree, so row actions are real widgets with their own
clicks, tooltips and focus.

```java
QuillListView<Worker> list = new QuillListView<>(
        0, 0, 208, 190, 36,
        (worker, index) -> row(6,
                flexible(column(2,
                        text(Component.literal(worker.name())),
                        text(Component.literal(worker.rank() + " - " + worker.job()))
                )),
                widget(QuillButton.secondary("Pay", () -> pay(worker)), 46, 16)
        ),
        (worker, index) -> select(worker)
);
list.replaceItems(workers);
list.setSelected(selected);
```

The list paints the row surface (including hover and selection), clips and scrolls, and routes mouse
and keyboard input to the widgets inside the visible rows. Selection is by item value, and
`scrollOffset()`/`setScrollOffset(int)` survive a content rebuild.

Row builders run inside the list's theme scope, so `QuillButton.primary(...)` and the other factories
create widgets with the correct light/dark palette. `padding(insets)` reserves space between the row
surface and its content; the default is 10 px horizontally.

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

When local state changes, mutate the field and call `refreshContent()`. The rebuild runs on the next
tick, so the widget list is never changed during input dispatch, and the content scroll offset is
kept.

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
