# Layout engine

The engine is the foundation for responsive screens. It is intentionally separate from the current
absolute-coordinate screens. Phase 1 added the core and tests; phase 2 added cross-axis alignment,
scroll measurement and the widget adapter.

## Protocol

The engine follows the same rule as Flutter:

> Constraints go down. Sizes go up. Parent sets position.

A node receives `QuillBoxConstraints`, chooses a `QuillSize` inside them, and is later given a
`QuillRect` by its parent.

```java
public interface QuillLayoutNode {
    QuillSize measure(QuillBoxConstraints constraints);
    void arrange(QuillRect bounds);
    QuillRect bounds();
}
```

`QuillNode` stores the final rectangle. Composite nodes keep the measured child sizes between the
measure and arrange passes.

## Constraints

`QuillBoxConstraints` contains `minWidth`, `minHeight`, `maxWidth` and `maxHeight`.
`Integer.MAX_VALUE` means unbounded. Layout sums saturate at that value instead of overflowing
into a negative size.

- `tight(size)` — exact size.
- `loose(max)` — zero minimum, bounded maximum.
- `unbounded()` — zero minimum, unbounded maximum.
- `deflate(insets)` — remove padding before measuring a child.
- `enforce(parent)` — clamp extra constraints into the parent range.
- `constrain(size)` — clamp a chosen size.

## Containers

| Node | Behaviour |
| --- | --- |
| `QuillSizedBox` | Fixed or preferred width/height around an optional child. |
| `QuillPaddingBox` | Deflates child constraints and adds insets to the result. |
| `QuillMarginBox` | Adds outer spacing around a child. |
| `QuillConstrainedBox` | Applies additional min/max constraints. |
| `QuillFlexBox` | Row/column with content-sized and weighted flexible children. |
| `QuillWrapBox` | Content-sized children wrapped into rows. |
| `QuillStackBox` | Overlays children with horizontal/vertical alignment. |
| `QuillScrollBox` | Measures unbounded content and exposes a scroll offset. |
| `QuillWidgetBox` | Adapts an `AbstractWidget` to the layout tree. |
| `QuillTextNode` | Measures and draws a text leaf with `QuillTextMeasurer`. |
| `QuillCardBox` | Card surface with an optional heading and a padded child tree. |

`QuillFlexBox.crossAxisAlignment(...)` supports `START`, `CENTER`, `END` and `STRETCH`. Stretch
measures children with a tight cross axis and arranges them to the container cross size.

`QuillScrollBox` measures the child with an unbounded main axis. `scrollTo`/`scrollBy` clamp to
`maxScroll()`. Rendering and clipping are still the widget layer's responsibility, so scroll boxes
are not wired into `QuillScreen` yet.

`QuillScrollBar` is a thin viewport scrollbar. The owner supplies `maxScroll`, the current offset and
the callback that re-arranges the content; the bar paints the thumb and handles thumb dragging.
`QuillScrollHints` draws top/bottom chevrons when content is hidden.

`QuillWidgetBox.clipTo(rect)` is the first clipping guard: widgets outside the rect are hidden
instead of drawn over the next section.

`QuillClipView` is the scissor host: it owns child widgets, clips them to a viewport, and maps mouse
input through the current scroll offset. Children keep content coordinates, so partially visible
widgets are clipped instead of hidden entirely.

`QuillLayoutValidator` checks arranged trees for negative sizes and children outside their parent.
`QuillScrollBox` children are allowed to overflow, as expected.

`QuillWidgetBox` is the first adapter: `measure` reads the widget's current size and `arrange`
applies the final rectangle with `setX`/`setY`/`setWidth`/`setHeight`. Widgets implementing
`QuillMeasuredWidget` (such as `QuillLabel`) report their own preferred size, so text wraps at the
incoming width instead of a hardcoded rectangle.

`QuillScreen.setContent(root)` measures and arranges a tree inside `contentBounds()`, then registers
every `QuillWidgetBox` in it. When the natural height exceeds the viewport, the tree is hosted in a
`QuillClipView` with a `QuillScrollBar`, so scrolled content keeps content coordinates and focus
handling. Use `setContent(root, contentBounds(24, 64))` when the screen reserves space for fixed
footer controls, and `contentScrollOffset()`/`contentMaxScroll()` to draw scroll hints.
`refreshContent()` marks the tree for a next-tick rebuild after local state changes.
`control(widget, height)` and `text(component)` are the small constructors used by screens:

```java
@Override
protected void buildContent() {
    QuillFlexBox column = QuillFlexBox.column(QuillSpacing.SPACE_2)
            .crossAxisAlignment(QuillCrossAxisAlignment.STRETCH);

    column.add(text(Component.literal("Configuration")));
    column.add(control(QuillButton.primary("Save", this::save), 20));
    column.add(control(QuillButton.ghost("Cancel", this::onClose), 20));

    setContent(new QuillPaddingBox(QuillInsets.all(QuillSpacing.SPACE_3), column));
}
```

`QuillTextNode` is the text leaf. It wraps at the tightest of `QuillTextStyle.maxWidth()` and the
incoming constraints, respects `maxLines`, and reports one line of height for empty text.

`QuillCardBox` groups a measured subtree inside a card. It reserves the heading and padding, then
arranges the child inside `content()`:

```java
QuillFlexBox form = QuillFlexBox.column(QuillSpacing.SPACE_2);
form.add(control(QuillTextField.of(font, Component.literal("Name"))));
setContent(new QuillCardBox(Component.literal("Employee"), form));
```

Nodes implementing `QuillPaintable` are painted before the widget layer, and inside the scroll host
when the content overflows, so cards stay behind their controls.

```java
QuillTextNode label = new QuillTextNode(
        Component.literal("A longer description"),
        QuillTextStyle.DEFAULT,
        QuillTextMeasurer.of(font)
);
QuillSize size = label.measure(QuillBoxConstraints.loose(120, 200));
```

The older `QuillFlex`, `QuillWrap` and `QuillStack` classes remain as immediate rectangle helpers for
existing screens. The `*Box` classes are the measured tree used by the next migration phase.

## Example

```java
QuillFlexBox row = QuillFlexBox.row(12)
        .add(new QuillSizedBox(120, 24))
        .addFlexible(new QuillPaddingBox(QuillInsets.all(8), content), 1);

QuillSize size = row.measure(QuillBoxConstraints.loose(400, 200));
row.arrange(new QuillRect(20, 20, size.width(), size.height()));
```

## Current limits

- `QuillScreen` consumes a measured content tree through `setContent`; overflow uses
  `QuillClipView` plus `QuillScrollBar`. `QuillScrollBox` is still the engine-only measurement path.
- `QuillWidgetBox.clipTo` hides widgets outside the viewport, but does not scissor partially visible widgets.
- `QuillTextNode` measures wrapped lines but does not yet draw ellipsized multi-line text.

## Tests

`QuillBoxLayoutTest` covers constraints, padding, sizing, constraints boxes, flex rows/columns,
cross-axis alignment, wrapping, stacking, margin, scroll measurement, a nested tree and the layout validator. `QuillWidgetBoxTest`
covers the widget adapter and `QuillTextNodeTest` covers text measurement with a deterministic
measurer. Run:

```bat
gradlew.bat test --console=plain
```

## Next phases

1. Rebuild the test harness screens with the engine and add scroll clipping.
2. Migrate the Quill screens.
3. Migrate LaborCraft screens.
