# Input styles

Selection controls (`QuillCheckbox` and `QuillRadio`) keep layout, label, focus, keyboard input and
value state in the widget. The indicator is painted by a `QuillSelectionStyle`.

This means an application can replace the square checkbox, the radio circle, or their selected
marks without subclassing the widget or duplicating its input logic.

## Built-in styles

- `QuillSelectionStyles.CLASSIC` — square checkbox with checkmark and circular radio.
- `QuillSelectionStyles.FILLED` — stronger selected surface.
- `QuillSelectionStyles.MINIMAL` — outlined indicator with minimal fill.

```java
QuillCheckbox checkbox = new QuillCheckbox(
        x, y, 220, 20,
        Component.literal("Enable feature"),
        false,
        value -> { },
        theme,
        QuillSelectionStyles.FILLED
);

checkbox.setStyle(QuillSelectionStyles.MINIMAL);
```

## Custom style

`QuillSelectionStyle` receives a `Context` containing the control type, bounds, selected/hovered/
focused/enabled state and the active `QuillThemeData`.

```java
public final class DiamondStyle implements QuillSelectionStyle {
    @Override
    public void render(GuiGraphicsExtractor graphics, Context context) {
        int color = context.color(context.selected()
                ? QuillThemeData.ColorRole.ACCENT
                : QuillThemeData.ColorRole.BUTTON_BORDER);
        int centerX = context.x() + context.size() / 2;
        int centerY = context.y() + context.size() / 2;
        int half = context.size() / 2;

        for (int i = 0; i < half; i++) {
            graphics.fill(centerX - i, centerY - half + i, centerX + i + 1, centerY - half + i + 1, color);
            graphics.fill(centerX - i, centerY + half - i - 1, centerX + i + 1, centerY + half - i, color);
        }
    }
}
```

The context exposes `color(ColorRole)` as a shortcut to the current theme. Use `QuillRender` helpers
for lines, circles and borders so custom styles follow the same rendering primitives.

## Adapter rule

A custom style is responsible only for the indicator. Do not handle mouse clicks, labels, focus or
the selected value there. Those remain widget responsibilities, which keeps custom styles compatible
with future controls such as toggles, chips and segmented selectors.

## Extending the pattern

The same strategy can be applied to future input groups:

- slider track/knob style;
- dropdown popup style;
- segmented-control indicator style;
- switch track/knob style.

The public input package is the integration boundary for these adapters.
