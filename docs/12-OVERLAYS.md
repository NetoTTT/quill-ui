# Overlays

Overlays render above the normal widget tree and own their input routing. `QuillScreen` exposes a
small stack:

```java
screen.openOverlay(overlay);
screen.closeOverlay(overlay);
screen.closeOverlays();
```

`QuillOverlay` can implement rendering, mouse/key input, outside-click dismissal and input blocking.
The screen renders open overlays after the screen footer and prunes closed overlays automatically.
The stack itself is reusable: `QuillOverlayStack.dispatch(...)` walks open overlays from top to
bottom and `render(...)` draws them in insertion order.

## Popup menu

`QuillPopupMenu` is the first built-in overlay. It renders an anchored list, handles selection,
closes on an outside click and closes with Escape.

```java
screen.openOverlay(new QuillPopupMenu(
        anchorX,
        anchorY + anchorHeight,
        anchorWidth,
        18,
        options,
        selectedIndex,
        theme,
        index -> selected = index,
        null
));
```

## Dropdown

`QuillDropdown` uses `QuillPopupMenu` internally:

```java
QuillDropdown dropdown = new QuillDropdown(
        x, y, 180, 20,
        Component.translatable("mymod.config.mode"),
        List.of(
                Component.translatable("mymod.config.mode.low"),
                Component.translatable("mymod.config.mode.high")
        ),
        0,
        index -> saveMode(index),
        theme
);
addRenderableWidget(dropdown);
```

The popup is screen-level rather than a child of the dropdown, so it stays above later widgets.

## Modal

`QuillModal` renders a centered dialog above the screen and blocks click-through. Escape cancels,
Enter confirms, and outside clicks are ignored.

```java
screen.openOverlay(new QuillModal(
        Component.translatable("mymod.confirm.title"),
        Component.translatable("mymod.confirm.body"),
        Component.translatable("gui.ok"),
        Component.translatable("gui.cancel"),
        this::confirm,
        this::cancel,
        theme
));
```

`close()` runs the cancel callback if the modal was not confirmed, so closing a screen or calling
`closeOverlays()` does not leave a pending action behind.

## Toast

`QuillToast` is a short-lived corner notification. It never captures input, so a toast stacked over
a popup or a modal does not stop that overlay from receiving clicks or keys.

```java
screen.showToast(Component.translatable("mymod.saved"));
screen.showToast(Component.translatable("mymod.saved"), 2.5F); // seconds
```

The lifetime is wall-clock based, fades in/out and the overlay is pruned automatically when it
expires. Toasts render top-right.

## Rules

- A dropdown/menu should not mutate game state; send an action after selection.
- Use `dismissOnOutsideClick()` for menus and non-modal popups.
- Set `blocksInput()` for modal surfaces.
- Leave both `dismissOnOutsideClick()` and `blocksInput()` false for passive overlays such as toasts.
- Use `QuillPopupMenu`/overlays for new controls instead of rendering an unclipped list inside a widget.
