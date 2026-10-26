import { C, Callout, CodeBlock, DocLink, DocTable, H2, H3, P, UL } from "@/components/prose";
import { SITE } from "../site";
import type { DocPage } from "../types";

function Api() {
  return (
    <>
      <P>
        Quill UI has one public API surface, covered by the compatibility policy in{" "}
        <a className="font-medium text-link underline decoration-primary decoration-2 underline-offset-[3px]" href={`${SITE.repoUrl}/blob/main/docs/COMPATIBILITY.md`} target="_blank" rel="noopener noreferrer">
          COMPATIBILITY.md
        </a>
        .
      </P>

      <H2>Packages</H2>
      <DocTable
        head={["Package", "Public classes"]}
        rows={[
          [<C key="1">com.quillui.api</C>, "QuillApi"],
          [<C key="2">com.quillui.theme</C>, "QuillThemeData, QuillTheme, QuillSpacing, QuillTypography, QuillRadii, QuillElevation, QuillColors"],
          [<C key="3">com.quillui.layout</C>, "QuillRect, QuillInsets, QuillColumn, QuillRow, QuillGrid"],
          [<C key="4">com.quillui.render</C>, "QuillRender, QuillTooltips"],
          [<C key="5">com.quillui.widget</C>, "QuillButton, QuillCard, QuillScrollList, QuillTabBar, QuillToggle"],
          [<C key="6">com.quillui.screen</C>, "QuillScreen"],
          [<C key="7">com.quillui.action</C>, "QuillActionSink, QuillCommandSink"],
        ]}
      />
      <P>
        Classes outside these packages are implementation details. They may change without notice and must
        not be mixed into.
      </P>

      <H2>QuillApi</H2>
      <CodeBlock
        code={`
public static final String MOD_ID = "quillui";
public static final int API_LEVEL = 1;

public static String version();          // loaded mod version, or "unknown"
public static boolean supports(int requiredLevel);`}
      />
      <UL>
        <li>
          <C>API_LEVEL</C> increases only when an existing public signature is removed or changed
          incompatibly.
        </li>
        <li>Additive classes, methods and behaviour-preserving changes do not increase it.</li>
        <li>
          Consumers can call <C>QuillApi.supports(level)</C> when they need to run against older runtimes.
        </li>
        <li>
          The mod version and the API level are related but not identical; a bug-fix release may keep the
          same level.
        </li>
      </UL>
      <Callout kind="note">
        <P>
          The current source is <C>0.1.0-alpha.5</C> with API level <C>1</C>. A package reorganisation is
          planned for the next minor alpha and will bump the level when it ships.
        </P>
      </Callout>

      <H2>Rules for consumers</H2>
      <UL>
        <li>Use public entry points; do not mix into Quill UI internals.</li>
        <li>Compose widgets instead of subclassing implementation details.</li>
        <li>Send actions through <C>QuillActionSink</C> or a project-specific payload.</li>
        <li>Test against the oldest Quill UI API level you claim to support.</li>
      </UL>
      <H3>Schema and internals</H3>
      <P>
        There are no public configuration files or registries in this release. If that changes, it will be
        documented here before a stable release.
      </P>
    </>
  );
}

function Compatibility() {
  return (
    <>
      <P>
        Quill UI targets <strong>one Minecraft version per release</strong>. A single jar is not expected to
        run across Minecraft versions.
      </P>

      <H2>Support matrix</H2>
      <DocTable
        head={["Loader", "Minecraft", "Status"]}
        rows={[
          ["Fabric", "26.3", "Supported (client only)"],
          ["NeoForge", "—", "Planned, not supported"],
          ["Forge", "—", "Not supported"],
        ]}
      />

      <H2>Versions</H2>
      <DocTable
        head={["Component", "Version"]}
        rows={[
          ["Quill UI", SITE.version],
          ["Minecraft", "26.3"],
          ["Fabric Loader", "0.19.5 or newer"],
          ["Fabric API", "0.164.0+26.3"],
          ["Java", "25 or newer"],
          ["Environment", "client"],
        ]}
      />
      <P>
        These values come from <C>gradle.properties</C> and <C>fabric.mod.json</C>. Dedicated servers do not
        need the jar; client screens that use it do.
      </P>

      <H2>Binary compatibility</H2>
      <UL>
        <li>
          <C>QuillApi.API_LEVEL</C> is the binary generation. It increments on incompatible public changes.
        </li>
        <li>
          CI compares each build against the previous release with japicmp and fails on incompatible changes
          that were not declared.
        </li>
        <li>Existing static <C>QuillTheme</C> values remain available during the theme transition.</li>
      </UL>
      <Callout kind="warning">
        <P>
          Do not assume NeoForge support or multi-version jars. See{" "}
          <a className="font-medium text-link underline decoration-primary decoration-2 underline-offset-[3px]" href={`${SITE.repoUrl}/blob/main/docs/SUPPORTED-VERSIONS.md`} target="_blank" rel="noopener noreferrer">
            SUPPORTED-VERSIONS.md
          </a>{" "}
          for the policy.
        </P>
      </Callout>
    </>
  );
}

function Colors() {
  return (
    <>
      <P>
        The default palette is defined by <C>QuillThemeData.DEFAULT</C>. Values are packed ARGB integers. Use{" "}
        <C>QuillThemeData.with(...)</C> or the builder to override individual roles.
      </P>

      <H2>Colour roles</H2>
      <DocTable
        head={["Role", "Default", "Use"]}
        rows={[
          [<C key="1">PANEL_BG</C>, "#F00E1217", "Screen panel background."],
          [<C key="2">PANEL_BORDER</C>, "#FF2B3440", "Screen panel border."],
          [<C key="3">PANEL_SHADOW</C>, "#66000000", "Panel drop shadow."],
          [<C key="4">HEADER_BG</C>, "#E60A0E14", "Header band."],
          [<C key="5">DIVIDER</C>, "#FF242C36", "Header and footer dividers."],
          [<C key="6">TITLE</C>, "#FFF2F5F8", "Titles and headings."],
          [<C key="7">TEXT</C>, "#FFC6D0DA", "Body text."],
          [<C key="8">TEXT_SOFT</C>, "#FFA9B5C1", "Subtitles and card headings."],
          [<C key="9">MUTED</C>, "#FF7F8B99", "Hints and secondary labels."],
          [<C key="10">MUTED_DARK</C>, "#FF5F6B78", "Footer text."],
          [<C key="11">ACCENT</C>, "#FFE0A93B", "Brand accent, focus and selection."],
          [<C key="12">ACCENT_HOVER</C>, "#FFF3C55E", "Accent on hover."],
          [<C key="13">PRIMARY_BG</C>, "#FF241C11", "Primary button body."],
          [<C key="14">PRIMARY_BG_HOVER</C>, "#FF2F2517", "Primary button body on hover."],
          [<C key="15">DANGER</C>, "#FFE06A6A", "Destructive actions and errors."],
          [<C key="16">DANGER_BG</C>, "#FF352024", "Danger surface."],
          [<C key="17">SUCCESS</C>, "#FF65D39A", "Success feedback."],
          [<C key="18">INFO</C>, "#FF7FB4F5", "Informational feedback."],
          [<C key="19">BUTTON_BG</C>, "#FF1A212A", "Secondary button body."],
          [<C key="20">BUTTON_HOVER</C>, "#FF232D38", "Secondary button body on hover."],
          [<C key="21">BUTTON_BORDER</C>, "#FF354150", "Secondary button border."],
          [<C key="22">BUTTON_DISABLED</C>, "#FF141920", "Disabled button body."],
          [<C key="23">BUTTON_DISABLED_BORDER</C>, "#FF232A33", "Disabled button border."],
          [<C key="24">BUTTON_DISABLED_TEXT</C>, "#FF626D7A", "Disabled text."],
          [<C key="25">SELECTED_BG</C>, "#FF2A3140", "Selected tab or button background."],
          [<C key="26">SURFACE</C>, "#99151C25", "Card surface."],
          [<C key="27">SURFACE_ALT</C>, "#661A222C", "Inset rows."],
          [<C key="28">SURFACE_BORDER</C>, "#FF26313D", "Card border."],
          [<C key="29">SURFACE_HEADER</C>, "#FF10161E", "Card top highlight."],
          [<C key="30">CHIP_BG</C>, "#FF1B242E", "Tab strip background."],
          [<C key="31">CHIP_BORDER</C>, "#FF33404E", "Tab strip border."],
        ]}
      />

      <H2>Spacing</H2>
      <DocTable
        head={["Token", "Pixels"]}
        rows={[
          [<C key="s0">NONE</C>, "0"],
          [<C key="s1">SPACE_1</C>, "4"],
          [<C key="s2">SPACE_2</C>, "8"],
          [<C key="s3">SPACE_3</C>, "12"],
          [<C key="s4">SPACE_4</C>, "16"],
          [<C key="s5">SPACE_5</C>, "20"],
          [<C key="s6">SPACE_6</C>, "24"],
        ]}
      />

      <H2>Radii and elevation</H2>
      <DocTable
        head={["Group", "Tokens"]}
        rows={[
          [<C key="r">QuillRadii</C>, "NONE 0 · SM 2 · MD 4 · LG 6 · PILL 999"],
          [<C key="e">QuillElevation</C>, "NONE 0 · SM 2 · MD 4 · LG 6 · PANEL_OFFSET 4"],
        ]}
      />

      <H2>QuillColors</H2>
      <CodeBlock
        code={`
QuillColors.rgb(224, 169, 59);        // 0xFFE0A93B
QuillColors.argb(128, 224, 169, 59);  // 0x80E0A93B
QuillColors.fromHex("#8CC8F0");       // accepts #RGB, #RRGGBB, #AARRGGBB, 0x...
QuillColors.withAlpha(color, 0.5F);
QuillColors.alpha(color);             // 0..255
QuillColors.red(color);
QuillColors.green(color);
QuillColors.blue(color);
QuillColors.toHex(color);             // "#FFE0A93B"
QuillColors.toRgbHex(color);          // "#E0A93B"`}
      />
      <P>
        <C>rgb</C>, <C>argb</C> and <C>withAlpha</C> validate or clamp their components instead of producing
        silently broken colours.
      </P>
    </>
  );
}

function Troubleshooting() {
  return (
    <>
      <P>Problems that come up when integrating Quill UI into a Fabric mod.</P>

      <H2>UnsupportedClassVersionError</H2>
      <P>
        Gradle or Minecraft is running an older Java than Quill UI requires. The mod needs Java 25. Check with{" "}
        <C>./gradlew --version</C> and compare with <DocLink to="reference/compatibility">Compatibility</DocLink>.
      </P>

      <H2>NoClassDefFoundError for a client class on a server</H2>
      <P>
        Quill UI and your screens reference client-only Minecraft classes. Keep them in a client package and
        only load them from client entry points. The mod declares <C>environment: "client"</C>.
      </P>

      <H2>The dependency cannot be resolved</H2>
      <UL>
        <li>The jar is not in <C>libs/</C>, or the file name in <C>build.gradle</C> is wrong.</li>
        <li>Gradle needs a refresh after copying the jar or editing the build script.</li>
        <li>There is no Maven publication yet; do not point at a repository coordinate.</li>
      </UL>

      <H2>Widgets do not appear</H2>
      <UL>
        <li>
          Register each widget with <C>addRenderableWidget(...)</C> inside <C>buildContent()</C>.
        </li>
        <li>Check the coordinates are inside the panel and below the 48 px header.</li>
        <li>
          A <C>QuillCard</C> is not a container: draw it with <C>render(...)</C> and register its children
          yourself.
        </li>
      </UL>

      <H2>A custom theme is ignored</H2>
      <P>
        Pass the <C>QuillThemeData</C> to every widget you create. Omitting it uses{" "}
        <C>QuillThemeData.DEFAULT</C>. The <C>QuillScreen</C> shell always uses the static facade in this
        release; see <DocLink to="guides/theming">Theming</DocLink>.
      </P>

      <H2>The scroll list does not scroll</H2>
      <UL>
        <li>The cursor must be over the list.</li>
        <li>Content must be taller than the list for a scrollbar and wheel handling to exist.</li>
        <li>
          Call <C>replaceItems(...)</C> after changing the data; it also resets the scroll to the top.
        </li>
      </UL>

      <H2>Text shows a translation key</H2>
      <P>
        The key is missing from <C>assets/&lt;modid&gt;/lang/en_us.json</C>, or the file is not under{" "}
        <C>src/main/resources</C>. Quill UI&apos;s own close-button strings live in{" "}
        <C>quillui</C>&apos;s language file.
      </P>
    </>
  );
}

export const reference: DocPage[] = [
  {
    slug: "api",
    title: "API Reference",
    description: "Public packages, QuillApi and binary compatibility.",
    component: Api,
  },
  {
    slug: "compatibility",
    title: "Compatibility",
    description: "Supported loaders, Minecraft and Java versions.",
    component: Compatibility,
  },
  {
    slug: "colors",
    title: "Theme Reference",
    description: "Every colour role, spacing token, radius and elevation value.",
    component: Colors,
  },
  {
    slug: "troubleshooting",
    title: "Troubleshooting",
    description: "Fixing common integration problems.",
    component: Troubleshooting,
  },
];
