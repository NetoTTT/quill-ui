# Layout

Quill UI replaces repeated magic coordinates with small layout primitives.

## Screen panel

`QuillScreen` treats the panel size passed to its constructor as a preferred size. On every
`init()` (including window resizes) the panel is clamped to the viewport minus `screenMargin()`
(16 px by default) and centered. Subclasses should read `panelWidth()`/`panelHeight()` instead of
keeping their own copies, so content can reflow on small windows.

```java
QuillRect content = new QuillRect(
        panelX + 24,
        panelY + 66,
        panelWidth() - 48,
        panelHeight() - 150
);
```

## QuillRect

`QuillRect(x, y, width, height)` has `right()`, `bottom()`, `centerX()`, `centerY()`,
`contains(mouseX, mouseY)`, `inset(...)`, `offset(dx, dy)`, `intersects(...)` and
`intersection(...)`.

## QuillInsets

`QuillInsets(top, right, bottom, left)`, with `all(value)` and `symmetric(vertical, horizontal)`.

## QuillColumn

A top-to-bottom stack. `next(height)` returns a `QuillRect` and advances the cursor by
`height + gap`.

```java
QuillColumn column = new QuillColumn(x, y, width, QuillSpacing.SPACE_2);
QuillRect first = column.next(24);
QuillRect second = column.next(24);
```

## QuillRow

The horizontal equivalent:

```java
QuillRow row = new QuillRow(x, y, 24, QuillSpacing.SPACE_2);
QuillRect left = row.next(120);
QuillRect right = row.next(120);
```

## QuillGrid

Fixed-column grid for forms and dashboards. The cell width is derived from the total width; cells
are addressed by index.

```java
QuillGrid grid = new QuillGrid(x, y, width, 2, QuillSpacing.SPACE_3, 24);
QuillRect a = grid.cell(0); // column 0, row 0
QuillRect b = grid.cell(1); // column 1, row 0
QuillRect c = grid.cell(2); // column 0, row 1
```

## QuillFlex

One-dimensional flex layout. Fixed items keep their basis; flexible items share the remaining space
by weight. If fixed items overflow, items with a positive basis shrink proportionally.

```java
List<QuillRect> slots = QuillFlex.row(content, QuillSpacing.SPACE_2)
        .add(120)
        .addFlexible(1)
        .addFlexible(2)
        .layout();
```

`QuillFlex.column(...)` uses the same API on the vertical axis.

## QuillWrap

Wraps equal-sized items into rows. The item width is clamped to the available width.

```java
QuillWrap wrap = new QuillWrap(content, 180, 64, QuillSpacing.SPACE_3);
QuillRect first = wrap.cell(0);
int height = wrap.contentHeight(itemCount);
```

## QuillStack

Aligns a child rectangle inside a parent. `fill()`, `center(width, height)` and
`place(..., QuillAlign.START/CENTER/END)` are the common cases.

```java
QuillRect dialog = new QuillStack(content).center(360, 190);
```

## QuillConstraints

Applies minimum and maximum width/height to a rectangle. `QuillFlex.add(basis, weight, constraints)`
accepts the same object per item.

```java
QuillConstraints limits = QuillConstraints.of(160, 40, 420, 120);
QuillRect clamped = limits.clamp(candidate);
```

## Breakpoints

`QuillBreakpoints` exposes `COMPACT`, `MEDIUM` and `EXPANDED` plus a column helper. The named
thresholds are 480 and 840 pixels; the column helper is independent of the names.

```java
QuillBreakpoints.Breakpoint size = QuillBreakpoints.of(viewportWidth);
int columns = QuillBreakpoints.columns(contentWidth, 180, 4, gap);
QuillGrid grid = new QuillGrid(x, y, contentWidth, columns, gap, rowHeight);
```

## QuillScrollView (legacy)

`QuillScrollView` is deprecated since `0.2.0-alpha.7`; prefer `QuillClipView` with a
`ContentRenderer` and `QuillScrollBar`. It still clips custom content, owns the wheel, click-jump
and scrollbar drag, and reports clicks in content coordinates. The renderer receives absolute
screen coordinates with scrolling already applied, so drawing uses the same `x`/`y` as a normal
widget.

```java
QuillScrollView view = new QuillScrollView(
        x, y, width, height,
        () -> rowCount * rowHeight,
        (graphics, vx, vy, vw, contentHeight, offset, mouseX, mouseY, delta) -> {
            // draw rows at vx, vy + index * rowHeight
        },
        (contentX, contentY) -> {
            // contentY already includes the scroll offset
            return true;
        },
        theme
);
addRenderableWidget(view);
```

Use `isContentVisible(rect)` to skip off-screen rows and `contentScreenY(y)` when a screen-level
widget must follow scrolled content.

## Composition

Prefer a small number of named regions per screen:

```java
int contentY = panelY + 88;
QuillGrid fields = new QuillGrid(x, contentY, width, 2, 12, 22);
QuillRect actionBar = new QuillRect(x, panelY + panelHeight - 46, width, 24);
```

Cards use `QuillRender.section(rect.x(), rect.y(), rect.width(), rect.height())`. Buttons and lists
should be positioned from the same rects, not from independent constants.
