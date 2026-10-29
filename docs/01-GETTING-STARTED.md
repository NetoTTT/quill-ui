# Getting started

## Requirements

- Minecraft 26.3
- Fabric Loader 0.19.5+
- Fabric API 0.164.0+26.3
- Java 25

Quill UI is a **separate client mod**. Put `quill-ui-0.2.0-alpha.7.jar` in `mods/` next to Fabric API.

## Add as a dependency

For local development, put the built jar in your project's `libs/` folder and add:

```groovy
dependencies {
    implementation files("libs/quill-ui-0.2.0-alpha.7.jar")
}
```

A Maven/Modrinth publication is planned before `1.0.0`.

## First screen

```java
import com.quillui.theme.QuillSpacing;
import com.quillui.action.QuillCommandSink;
import com.quillui.layout.QuillColumn;
import com.quillui.layout.QuillRect;
import com.quillui.screen.QuillScreen;
import com.quillui.widget.QuillButton;
import net.minecraft.network.chat.Component;

public final class ExampleScreen extends QuillScreen {
    public ExampleScreen() {
        super(Component.literal("Example"), 420, 260);
    }

    @Override
    protected void buildContent() {
        int x = panelX + QuillSpacing.SPACE_4;
        int width = panelWidth() - QuillSpacing.SPACE_4 * 2;
        QuillColumn column = new QuillColumn(x, panelY + 60, width, QuillSpacing.SPACE_2);

        QuillRect slot = column.next(24);
        addRenderableWidget(new QuillButton(
                slot.x(), slot.y(), slot.width(), slot.height(),
                Component.literal("Run action"),
                QuillButton.Variant.PRIMARY,
                () -> QuillCommandSink.get().execute("example run")
        ));

        addRenderableWidget(closeButton(this::onClose));
    }

    @Override
    protected Component subtitle() {
        return Component.literal("A Quill UI example.");
    }
}
```

## Rules

1. The screen only renders server state.
2. Actions go through an `QuillActionSink` or a project-specific C2S payload.
3. Use layout primitives; do not repeat hardcoded row positions.
4. Use `QuillTheme` tokens for all colours, spacing and surfaces.
