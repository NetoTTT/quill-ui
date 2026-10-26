import { C, Callout, CodeBlock, DocLink, Example, H2, H3, OL, P, UL } from "@/components/prose";
import { McCard, McButton, McToggle } from "@/components/mc";
import type { DocPage } from "../types";

function FirstInterface() {
  return (
    <>
      <P>
        This guide builds a complete settings screen with a card, a toggle and two actions. Every class and
        method used here exists in the current release.
      </P>
      <Example
        preview={
          <McCard title="Profile" className="w-full max-w-xs">
            <div className="space-y-3">
              <McToggle label="Show online status" on />
              <div className="flex gap-2">
                <McButton primary className="flex-1">Save</McButton>
                <McButton className="flex-1">Cancel</McButton>
              </div>
            </div>
          </McCard>
        }
      >
        <CodeBlock
          title="ProfileScreen.java"
          code={`
import com.quillui.action.QuillCommandSink;
import com.quillui.layout.QuillColumn;
import com.quillui.layout.QuillRect;
import com.quillui.layout.QuillRow;
import com.quillui.render.*;
import com.quillui.screen.QuillScreen;
import com.quillui.theme.QuillSpacing;
import com.quillui.widget.*;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public final class ProfileScreen extends QuillScreen {
    private QuillCard card;

    public ProfileScreen() {
        super(Component.literal("Profile"), 360, 240);
    }

    @Override
    protected void buildContent() {
        int x = panelX + QuillSpacing.SPACE_4;
        int width = panelWidth() - QuillSpacing.SPACE_4 * 2;
        card = new QuillCard(x, panelY + 60, width, 120, Component.literal("Profile"));

        QuillRect body = card.content();
        QuillColumn column = new QuillColumn(
                body.x(), body.y(), body.width(), QuillSpacing.SPACE_2
        );

        QuillRect toggleSlot = column.next(18);
        addRenderableWidget(new QuillToggle(
                toggleSlot.x(), toggleSlot.y(), toggleSlot.width(), toggleSlot.height(),
                Component.literal("Show online status"),
                true,
                value -> QuillCommandSink.get().execute("profile online " + value)
        ));

        QuillRect actionSlot = column.next(20);
        QuillRow row = new QuillRow(
                actionSlot.x(), actionSlot.y(), actionSlot.height(), QuillSpacing.SPACE_2
        );

        QuillRect saveSlot = row.next(96);
        addRenderableWidget(new QuillButton(
                saveSlot.x(), saveSlot.y(), saveSlot.width(), saveSlot.height(),
                Component.literal("Save"),
                QuillButton.Variant.PRIMARY,
                () -> {
                    QuillCommandSink.get().execute("profile save");
                    onClose();
                }
        ));

        QuillRect cancelSlot = row.next(96);
        addRenderableWidget(new QuillButton(
                cancelSlot.x(), cancelSlot.y(), cancelSlot.width(), cancelSlot.height(),
                Component.literal("Cancel"),
                QuillButton.Variant.SECONDARY,
                this::onClose
        ));

        addRenderableWidget(closeButton(this::onClose));
    }

    @Override
    public void extractRenderState(
            GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta
    ) {
        card.render(graphics, font);
        super.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    @Override
    protected Component subtitle() {
        return Component.literal("Changes are sent to the server.");
    }
}`}
        />
      </Example>

      <H2>What each part does</H2>
      <OL>
        <li>
          <strong>The constructor</strong> sets the window title and a fixed 360×240 panel.
        </li>
        <li>
          <strong>buildContent()</strong> derives <C>x</C> / <C>width</C> from the panel, so the content
          follows window and GUI-scale changes.
        </li>
        <li>
          <strong>The card</strong> reserves a heading and returns <C>content()</C>, the interior rectangle
          for the controls.
        </li>
        <li>
          <strong>The column</strong> stacks the toggle and the action row with one consistent gap.
        </li>
        <li>
          <strong>The row</strong> splits the action area into two equal-width buttons.
        </li>
        <li>
          <strong>closeButton(...)</strong> is added explicitly, so the screen has a themed close control.
        </li>
      </OL>
      <Callout kind="tip">
        <P>
          <C>card</C> is created in <C>buildContent()</C>, not in the constructor, because it needs{" "}
          <C>panelX</C> / <C>panelY</C>. Guard the render method if your screen can be shown before content
          is built.
        </P>
      </Callout>
    </>
  );
}

function Theming() {
  return (
    <>
      <P>
        A theme is an immutable <C>QuillThemeData</C> palette. Derive one from the default and pass it to the
        widgets you create; no global state is changed.
      </P>

      <H2>A light palette</H2>
      <CodeBlock
        code={`
import com.quillui.theme.QuillColors;
import com.quillui.theme.QuillThemeData;
import com.quillui.theme.QuillThemeData.ColorRole;

QuillThemeData light = QuillThemeData.builder()
        .set(ColorRole.PANEL_BG,            QuillColors.fromHex("#F5FAFF"))
        .set(ColorRole.PANEL_BORDER,        QuillColors.fromHex("#BBD9EE"))
        .set(ColorRole.HEADER_BG,           QuillColors.fromHex("#EAF6FF"))
        .set(ColorRole.TITLE,               QuillColors.fromHex("#253746"))
        .set(ColorRole.TEXT,                QuillColors.fromHex("#253746"))
        .set(ColorRole.TEXT_SOFT,           QuillColors.fromHex("#4B657A"))
        .set(ColorRole.ACCENT,              QuillColors.fromHex("#23699A"))
        .set(ColorRole.ACCENT_HOVER,        QuillColors.fromHex("#3B95D0"))
        .set(ColorRole.BUTTON_BG,           QuillColors.fromHex("#EAF6FF"))
        .set(ColorRole.BUTTON_HOVER,        QuillColors.fromHex("#D6ECFA"))
        .set(ColorRole.BUTTON_BORDER,       QuillColors.fromHex("#8CC8F0"))
        .set(ColorRole.BUTTON_DISABLED_TEXT, QuillColors.fromHex("#8FA3B4"))
        .build();`}
      />

      <H2>Apply it</H2>
      <P>
        Widgets take the theme as their last constructor argument. A <C>QuillCard</C> takes it after the
        title.
      </P>
      <CodeBlock
        code={`
QuillCard card = new QuillCard(x, y, width, 96, Component.literal("Status"), light);

QuillButton save = new QuillButton(
        x, y, 120, 20,
        Component.literal("Save"),
        QuillButton.Variant.PRIMARY,
        this::save,
        light
);

QuillToggle toggle = new QuillToggle(
        x, y, 180, 20,
        Component.literal("Enabled"),
        true,
        this::setEnabled,
        light
);`}
      />

      <H2>What cannot be themed yet</H2>
      <Callout kind="warning">
        <P>
          The screen shell (<C>QuillScreen</C>) reads the static <C>QuillTheme</C> facade, so its panel and
          header keep the default palette in this release. Only widgets and cards receive a custom{" "}
          <C>QuillThemeData</C>.
        </P>
      </Callout>

      <H2>Choosing roles</H2>
      <UL>
        <li>
          Keep <C>ACCENT</C> and <C>ACCENT_HOVER</C> close enough that hover is visible but not jarring.
        </li>
        <li>
          Check contrast for <C>TEXT</C>, <C>TITLE</C> and <C>BUTTON_DISABLED_TEXT</C> on every surface.
        </li>
        <li>
          <C>DANGER</C>, <C>SUCCESS</C> and <C>INFO</C> are semantic; do not reuse them as decoration.
        </li>
        <li>
          The full role list is in <DocLink to="reference/colors">Colour reference</DocLink>.
        </li>
      </UL>
      <H3>Per-widget overrides</H3>
      <P>
        Passing a different theme to one widget works, but too many overrides break visual consistency.
        Prefer one palette per screen.
      </P>
    </>
  );
}

function BackendActions() {
  return (
    <>
      <P>
        Quill UI never mutates game state. A screen renders a state snapshot and emits actions through the{" "}
        <C>QuillActionSink</C> boundary.
      </P>

      <H2>Command-first</H2>
      <P>
        <C>QuillCommandSink</C> is the zero-dependency implementation. It sends a slash command as the local
        player through the vanilla client command path, and does nothing when there is no player or the
        action is blank.
      </P>
      <CodeBlock
        code={`
QuillCommandSink.get().execute("example config set page 2");`}
      />
      <P>
        This is simple, auditable and works with server-side commands. Use it when the server already owns
        the command surface.
      </P>

      <H2>Typed actions</H2>
      <P>
        For forms and frequent toggles, implement <C>QuillActionSink</C> with a project-specific C2S payload.
        The widgets do not change.
      </P>
      <CodeBlock
        code={`
public final class PacketSink implements QuillActionSink {
    @Override
    public void execute(String action) {
        // Parse an action id, or route to a dedicated payload type,
        // then send it to the server. Never write authoritative state here.
        ClientPlayNetworking.send(new UiActionPayload(action));
    }
}`}
      />

      <H2>Server responsibilities</H2>
      <OL>
        <li>Validate permission and value ranges.</li>
        <li>Apply the change and persist it.</li>
        <li>Send an updated state snapshot to the client.</li>
        <li>Reject stale revisions or rate-limit abusive requests.</li>
      </OL>
      <Callout kind="note">
        <P>
          A <C>QuillToggle</C> flips its local visual immediately. If the server rejects the change, send a
          new snapshot and call <C>setValue(...)</C> so the visual matches the authoritative state.
        </P>
      </Callout>
    </>
  );
}

function BestPractices() {
  return (
    <>
      <P>
        These rules apply to any Quill UI screen and keep the framework easy to extend.
      </P>

      <H2>Structure</H2>
      <UL>
        <li>Keep screens and widgets in client-only packages; Quill UI is a client mod.</li>
        <li>Build the widget tree in <C>buildContent()</C>; do not rebuild it every frame.</li>
        <li>Keep game logic out of widgets: callbacks should call your own services or an action sink.</li>
        <li>Derive every rectangle from the panel and layout primitives.</li>
      </UL>

      <H2>Visual consistency</H2>
      <UL>
        <li>Use <C>QuillThemeData</C> and token groups instead of literal colours and sizes.</li>
        <li>Stay on the 4 px spacing grid.</li>
        <li>One primary action per screen; use secondary for Cancel and ghost for utility actions.</li>
        <li>Reserve semantic colours for their meaning.</li>
      </UL>

      <H2>Usability</H2>
      <UL>
        <li>Buttons and toggles already support Enter and Space; keep a visible focus outline.</li>
        <li>Test at the smallest and largest GUI scale, and at a narrow window width.</li>
        <li>Use translation keys in <C>Component.translatable(...)</C> instead of hard-coded strings.</li>
        <li>Do not hide essential information in a tooltip.</li>
      </UL>

      <H2>Networking</H2>
      <UL>
        <li>Interface code runs on the client. Send a packet or command to change server state.</li>
        <li>Validate everything on the server; the client is never authoritative.</li>
        <li>Send a fresh snapshot after a change instead of assuming the UI state is correct.</li>
      </UL>
    </>
  );
}

export const guides: DocPage[] = [
  {
    slug: "first-interface",
    title: "Creating Your First Interface",
    description: "A complete settings screen built from real Quill UI classes.",
    component: FirstInterface,
  },
  {
    slug: "theming",
    title: "Theming",
    description: "Build and apply a custom QuillThemeData palette.",
    component: Theming,
  },
  {
    slug: "backend-actions",
    title: "Backend Actions",
    description: "Send UI actions to the server without mutating state on the client.",
    component: BackendActions,
  },
  {
    slug: "best-practices",
    title: "Best Practices",
    description: "Structure, consistency, usability and networking guidance.",
    component: BestPractices,
  },
];
