# Layout engine

The engine is the foundation for responsive screens. It is intentionally separate from the current
absolute-coordinate screens; phase 1 adds the core and tests without migrating any screen.

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
`Integer.MAX_VALUE` means unbounded.

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
| `QuillConstrainedBox` | Applies additional min/max constraints. |
| `QuillFlexBox` | Row/column with content-sized and weighted flexible children. |
| `QuillWrapBox` | Content-sized children wrapped into rows. |
| `QuillStackBox` | Overlays children with horizontal/vertical alignment. |

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

- Flex children use `START` cross-alignment; `stretch`, `center` and `end` are next.
- Scroll measurement is not part of phase 1.
- The engine is not wired into `QuillScreen` or `AbstractWidget` yet.
- Text measurement must be provided by the screen adapter in phase 2.

## Tests

`QuillBoxLayoutTest` covers constraints, padding, sizing, constraints boxes, flex rows/columns,
wrapping, stacking and a nested tree. Run:

```bat
gradlew.bat test --console=plain
```

## Next phases

1. Cross-axis alignment, scroll measurement, and a widget adapter.
2. Rebuild the test harness screens with the engine.
3. Migrate the Quill screens.
4. Migrate LaborCraft screens.
