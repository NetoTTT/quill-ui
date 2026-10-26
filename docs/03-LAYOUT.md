# Layout

Quill UI replaces repeated magic coordinates with small layout primitives.

## QuillRect

`QuillRect(x, y, width, height)` has `right()`, `bottom()`, `contains(mouseX, mouseY)`, `inset(...)`
and `offset(dx, dy)`.

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

## Composition

Prefer a small number of named regions per screen:

```java
int contentY = panelY + 88;
QuillGrid fields = new QuillGrid(x, contentY, width, 2, 12, 22);
QuillRect actionBar = new QuillRect(x, panelY + panelHeight - 46, width, 24);
```

Cards use `QuillRender.section(rect.x(), rect.y(), rect.width(), rect.height())`. Buttons and lists
should be positioned from the same rects, not from independent constants.
