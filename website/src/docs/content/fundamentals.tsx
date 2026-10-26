import { C, Callout, CodeBlock, DocLink, DocTable, H2, P, UL } from "@/components/prose";
import type { DocPage } from "../types";

function CoreConcepts() {
  return (
    <>
      <P>
        Quill UI groups responsibilities into packages so a screen only has to know about the parts it uses.
        These are the terms used throughout the documentation.
      </P>

      <H2>Vocabulary</H2>
      <DocTable
        head={["Term", "What it is"]}
        rows={[
          ["Widget", <>Anything that draws and receives input. In practice, a Minecraft <C>AbstractWidget</C> such as <C>QuillButton</C>.</>],
          ["Screen", <>The Minecraft screen that hosts the widgets: <C>QuillScreen</C>.</>],
          ["Render helper", <>A non-interactive drawing object such as <C>QuillCard</C>. It has no input and is rendered by your screen.</>],
          ["Layout primitive", <>A small class that returns rectangles: <C>QuillColumn</C>, <C>QuillRow</C>, <C>QuillGrid</C>.</>],
          ["Theme", <>An immutable <C>QuillThemeData</C> palette plus static token groups (spacing, typography, radii, elevation).</>],
          ["Action", <>A string emitted through <C>QuillActionSink</C>. Screens never write authoritative state.</>],
        ]}
      />

      <H2>The screen lifecycle</H2>
      <UL>
        <li>
          <C>init()</C> computes <C>panelX</C> / <C>panelY</C> from the window size and calls your{" "}
          <C>buildContent()</C>.
        </li>
        <li>
          Your <C>buildContent()</C> derives rectangles and registers widgets with <C>addRenderableWidget(...)</C>.
        </li>
        <li>
          <C>extractBackground</C> draws the panel; <C>extractRenderState</C> draws the header, content and
          optional footer.
        </li>
        <li>
          Widgets receive input from vanilla once registered. Quill UI does not replace Minecraft&apos;s screen
          loop.
        </li>
      </UL>

      <H2>Dependency direction</H2>
      <P>
        The public packages follow a one-way dependency graph. Keeping it intact is what makes a future
        NeoForge port possible without rewriting the widgets.
      </P>
      <CodeBlock
        lang="text"
        status="none"
        code={`
api
theme  -> (no Quill dependencies)
layout -> (no Quill dependencies)
render -> theme, layout
widget -> theme, render, layout, action
screen -> theme, render, widget, layout, action
action -> Minecraft/Fabric only`}
      />
      <Callout kind="note">
        <P>
          Public packages must not depend on internals, and <C>theme</C> / <C>layout</C> must stay usable
          without a live Minecraft screen. Widgets never contain commands or gameplay state.
        </P>
      </Callout>

      <H2>Continue</H2>
      <UL>
        <li>
          <DocLink to="fundamentals/theme">Theme and tokens</DocLink>
        </li>
        <li>
          <DocLink to="fundamentals/layout">Layout primitives</DocLink>
        </li>
        <li>
          <DocLink to="fundamentals/screen">Screens</DocLink>
        </li>
      </UL>
    </>
  );
}

function Theme() {
  return (
    <>
      <P>
        Visual constants live in <C>com.quillui.theme</C>. New code should pass a <C>QuillThemeData</C>{" "}
        instance to widgets and screens; <C>QuillTheme</C> remains as a static compatibility facade that
        resolves to the default palette.
      </P>

      <H2>QuillThemeData</H2>
      <P>
        An immutable palette keyed by <C>ColorRole</C>. Derive a variant with <C>with(...)</C>, or build a
        custom palette with the builder. Every widget constructor accepts an optional theme.
      </P>
      <CodeBlock
        code={`
QuillThemeData accent = QuillThemeData.DEFAULT
        .with(QuillThemeData.ColorRole.ACCENT, QuillColors.fromHex("#8CC8F0"))
        .with(QuillThemeData.ColorRole.ACCENT_HOVER, QuillColors.fromHex("#A9D9F7"));

QuillButton button = new QuillButton(
        x, y, width, 20,
        Component.literal("Apply"),
        QuillButton.Variant.PRIMARY,
        this::apply,
        accent
);`}
      />
      <P>The builder starts from the default palette, so you only override what changes:</P>
      <CodeBlock
        code={`
QuillThemeData light = QuillThemeData.builder()
        .set(QuillThemeData.ColorRole.PANEL_BG, 0xF0FFFFFF)
        .set(QuillThemeData.ColorRole.TEXT, 0xFF253746)
        .build();`}
      />
      <Callout kind="tip">
        <P>
          The full role list and default values are on <DocLink to="reference/colors">Colour reference</DocLink>.
        </P>
      </Callout>

      <H2>Static facade</H2>
      <P>
        <C>QuillTheme</C> exposes <C>SPACE_1</C>…<C>SPACE_6</C> and every colour role as a static constant.
        It is kept for source compatibility; prefer <C>QuillSpacing</C> and <C>QuillThemeData</C> in new
        code. <C>QuillScreen</C> itself still reads the facade.
      </P>

      <H2>Spacing</H2>
      <DocTable
        head={["Token", "Pixels"]}
        rows={[
          [<C key="0">QuillSpacing.NONE</C>, "0"],
          [<C key="1">SPACE_1</C>, "4"],
          [<C key="2">SPACE_2</C>, "8"],
          [<C key="3">SPACE_3</C>, "12"],
          [<C key="4">SPACE_4</C>, "16"],
          [<C key="5">SPACE_5</C>, "20"],
          [<C key="6">SPACE_6</C>, "24"],
        ]}
      />

      <H2>Typography</H2>
      <DocTable
        head={["Token", "Value", "Use"]}
        rows={[
          [<C key="a">TEXT_HEIGHT</C>, "8", "Rendered glyph height."],
          [<C key="b">LINE_HEIGHT</C>, "9", "Glyph height plus one pixel of leading."],
          [<C key="c">SMALL_LINE_HEIGHT</C>, "8", "Compact rows."],
          [<C key="d">TITLE_SCALE</C>, "1.0", "Title multiplier."],
          [<C key="e">BODY_SCALE</C>, "1.0", "Body multiplier."],
          [<C key="f">SMALL_SCALE</C>, "0.8", "Small text multiplier."],
        ]}
      />
      <P>
        Helpers: <C>QuillTypography.centeredTextY(height)</C> and{" "}
        <C>scaledLineHeight(scale)</C>.
      </P>

      <H2>Radii and elevation</H2>
      <DocTable
        head={["Token group", "Values"]}
        rows={[
          [<C key="r">QuillRadii</C>, "NONE 0, SM 2, MD 4, LG 6, PILL 999"],
          [<C key="e">QuillElevation</C>, "NONE 0, SM 2, MD 4, LG 6, PANEL_OFFSET 4"],
        ]}
      />
      <Callout kind="note">
        <P>
          Rounded rendering is not implemented yet; the radius tokens exist so widgets can adopt it later
          without renaming anything.
        </P>
      </Callout>

      <H2>Colour helpers</H2>
      <P>
        Minecraft packs colours as <C>0xAARRGGBB</C>. <C>QuillColors</C> converts and inspects them:
      </P>
      <CodeBlock
        code={`
int accent = QuillColors.rgb(224, 169, 59);       // opaque amber
int soft   = QuillColors.withAlpha(accent, 0.5F); // half opacity
int parsed = QuillColors.fromHex("#8CC8F0");      // #RRGGBB -> opaque
String hex = QuillColors.toHex(accent);           // "#FFE0A93B"`}
      />
    </>
  );
}

function Layout() {
  return (
    <>
      <P>
        Layout primitives replace repeated magic coordinates with rectangles. They are pure Java and do not
        need a live screen, which makes them easy to test.
      </P>

      <H2>QuillRect</H2>
      <P>
        <C>QuillRect(x, y, width, height)</C> is an immutable record with <C>right()</C>, <C>bottom()</C>,{" "}
        <C>contains(mouseX, mouseY)</C>, <C>inset(insets)</C> and <C>offset(dx, dy)</C>.
      </P>

      <H2>QuillInsets</H2>
      <P>
        <C>QuillInsets(top, right, bottom, left)</C> with <C>all(value)</C> and{" "}
        <C>symmetric(vertical, horizontal)</C>. Use <C>horizontal()</C> and <C>vertical()</C> to subtract
        padding from a width or height.
      </P>

      <H2>QuillColumn and QuillRow</H2>
      <P>
        Fixed-size slots with a fixed gap. <C>next(size)</C> returns a rectangle and advances the cursor.
        <C>next(size, insets)</C> returns the rectangle already inset.
      </P>
      <CodeBlock
        code={`
QuillColumn column = new QuillColumn(x, y, width, QuillSpacing.SPACE_2);
QuillRect first  = column.next(24);
QuillRect second = column.next(24);

QuillRow row = new QuillRow(x, y, 20, QuillSpacing.SPACE_1);
QuillRect cancel = row.next(96);
QuillRect save   = row.next(96);`}
      />

      <H2>QuillGrid</H2>
      <P>
        A fixed-column grid for forms and dashboard cards. Cell width is derived from the total width and
        the gap; cells are addressed by index, row by row.
      </P>
      <CodeBlock
        code={`
QuillGrid grid = new QuillGrid(x, y, width, 2, QuillSpacing.SPACE_3, 24);
QuillRect a = grid.cell(0); // column 0, row 0
QuillRect b = grid.cell(1); // column 1, row 0
QuillRect c = grid.cell(2); // column 0, row 1`}
      />

      <H2>Composition</H2>
      <P>
        Prefer a few named regions per screen. Cards, buttons and lists should all be positioned from the
        same rectangles, not from independent constants.
      </P>
      <CodeBlock
        code={`
int contentY = panelY + 60;
QuillGrid fields = new QuillGrid(x, contentY, width, 2, QuillSpacing.SPACE_3, 22);
QuillRect actions = new QuillRect(x, panelY + panelHeight() - 40, width, 20);`}
      />
      <Callout kind="tip">
        <P>
          Keep every value on the 4 px grid. Minecraft&apos;s GUI scale is an integer, so non-multiples of 4
          drift at high scales.
        </P>
      </Callout>
    </>
  );
}

function Screen() {
  return (
    <>
      <P>
        <C>QuillScreen</C> is the host screen. It draws a centered panel, a 48 px header, an optional footer
        and a themed close button, then lets your content do the rest.
      </P>

      <H2>Structure</H2>
      <DocTable
        head={["Member", "Purpose"]}
        rows={[
          [<C key="a">QuillScreen(title, panelWidth, panelHeight)</C>, "Sets the window title and the fixed panel size."],
          [<C key="b">buildContent()</C>, "Abstract. Called from init(); register widgets here."],
          [<C key="c">subtitle()</C>, "Optional second header line."],
          [<C key="d">footer()</C>, "Optional centered footer; a divider is drawn automatically when non-blank."],
          [<C key="e">panelX / panelY</C>, "Protected content origin, recalculated on resize."],
          [<C key="f">panelWidth() / panelHeight()</C>, "Panel dimensions."],
          [<C key="g">closeButton(action)</C>, "Builds the header close button; you register it."],
        ]}
      />
      <P>
        The header uses a 3 px accent bar, the title at <C>+22,+14</C> and the subtitle at <C>+22,+29</C>.
        The close button is 20×20 at the top right and carries a tooltip from{" "}
        <C>quillui.close</C> / <C>quillui.close.tip</C>.
      </P>

      <H2>A complete screen</H2>
      <CodeBlock
        title="SettingsScreen.java"
        code={`
import com.quillui.action.QuillCommandSink;
import com.quillui.layout.QuillColumn;
import com.quillui.layout.QuillRect;
import com.quillui.screen.QuillScreen;
import com.quillui.theme.QuillSpacing;
import com.quillui.widget.QuillButton;
import com.quillui.widget.QuillToggle;
import net.minecraft.network.chat.Component;

public final class SettingsScreen extends QuillScreen {
    public SettingsScreen() {
        super(Component.literal("Settings"), 360, 220);
    }

    @Override
    protected void buildContent() {
        int x = panelX + QuillSpacing.SPACE_4;
        int width = panelWidth() - QuillSpacing.SPACE_4 * 2;
        QuillColumn column = new QuillColumn(x, panelY + 60, width, QuillSpacing.SPACE_2);

        QuillRect toggleSlot = column.next(20);
        addRenderableWidget(new QuillToggle(
                toggleSlot.x(), toggleSlot.y(), toggleSlot.width(), toggleSlot.height(),
                Component.literal("Sound effects"),
                true,
                value -> QuillCommandSink.get().execute("example set sound " + value)
        ));

        QuillRect actionSlot = column.next(20);
        addRenderableWidget(new QuillButton(
                actionSlot.x(), actionSlot.y(), 96, actionSlot.height(),
                Component.literal("Save"),
                QuillButton.Variant.PRIMARY,
                () -> QuillCommandSink.get().execute("example save")
        ));

        addRenderableWidget(closeButton(this::onClose));
    }

    @Override
    protected Component subtitle() {
        return Component.literal("Changes are applied by the server.");
    }

    @Override
    protected Component footer() {
        return Component.literal("Quill UI example");
    }
}`}
      />
      <Callout kind="warning">
        <P>
          <C>QuillScreen</C> does not pause the game (<C>isPauseScreen()</C> returns false). Do not assume
          the world is frozen while your interface is open.
        </P>
      </Callout>
    </>
  );
}

export const fundamentals: DocPage[] = [
  {
    slug: "core-concepts",
    title: "Core Concepts",
    description: "The vocabulary, package model and screen lifecycle.",
    component: CoreConcepts,
  },
  {
    slug: "theme",
    title: "Theme and Tokens",
    description: "Colour roles, spacing, typography, radii and elevation.",
    component: Theme,
  },
  {
    slug: "layout",
    title: "Layout Primitives",
    description: "Rectangles, insets, columns, rows and grids.",
    component: Layout,
  },
  {
    slug: "screen",
    title: "Screens",
    description: "The QuillScreen shell, header, footer and close button.",
    component: Screen,
  },
];
