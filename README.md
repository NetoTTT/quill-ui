# Quill UI

A small, opinionated **client-side UI framework for Fabric** (Minecraft 26.3, Java 25).

Quill UI provides the visual and layout foundation used by LaborCraft: theme tokens, panel/header
primitives, cards, tabs, buttons, scrollable lists and a backend action boundary. It is deliberately
independent from any economy, jobs or gameplay code, so other mods can use it.

## Status

`0.1.0` — first usable foundation. The API is intentionally small and may evolve before a stable
`1.0.0`.

## Modules

| Area | Entry points |
| --- | --- |
| Theme | `QuillTheme` |
| Drawing | `QuillRender` |
| Layout | `QuillRect`, `QuillInsets`, `QuillColumn`, `QuillRow`, `QuillGrid` |
| Widgets | `QuillButton`, `QuillScrollList`, `QuillTabBar` |
| Screen | `QuillScreen` |
| Tooltips | `QuillTooltips` |
| Backend boundary | `QuillActionSink`, `QuillCommandSink` |

## Build

```bat
gradlew.bat build --console=plain --no-daemon
```

The jar is written to `build/libs/quill-ui-0.1.0.jar`.

## Install

Quill UI is a separate client mod. Put its jar next to Fabric API in `mods/`. Mods that compile
against it should declare it as a dependency; it has no LaborCraft dependency.

## Quick start

```java
public final class ExampleScreen extends QuillScreen {
    public ExampleScreen() {
        super(Component.literal("Example"), 420, 260);
    }

    @Override
    protected void buildContent() {
        int x = panelX + QuillTheme.SPACE_4;
        int width = panelWidth() - QuillTheme.SPACE_4 * 2;
        QuillColumn column = new QuillColumn(x, panelY + 60, width, QuillTheme.SPACE_2);

        QuillRect first = column.next(24);
        QuillButton button = new QuillButton(
                first.x(), first.y(), first.width(), first.height(),
                Component.literal("Run action"),
                QuillButton.Variant.PRIMARY,
                () -> QuillCommandSink.get().execute("example run")
        );
        addRenderableWidget(button);
        addRenderableWidget(closeButton(this::onClose));
    }

    @Override
    protected Component subtitle() {
        return Component.literal("A Quill UI example.");
    }
}
```

## Design rules

- Use `QuillTheme` tokens instead of literal colours/spacing.
- Derive positions with layout primitives; do not repeat magic `x/y` values across screens.
- A screen renders state; it never mutates game state. Every action goes through an
  `QuillActionSink` (or a project-specific C2S payload).
- Keep cards and action rows on the same 4 px grid.

## Documentation

- [Getting started](docs/01-GETTING-STARTED.md)
- [Theme tokens](docs/02-THEME.md)
- [Layout](docs/03-LAYOUT.md)
- [Widgets and screen](docs/04-WIDGETS.md)
- [Actions and backend](docs/05-ACTIONS.md)
- [Migrating from LaborCraft's LC UI](docs/06-MIGRATION.md)
