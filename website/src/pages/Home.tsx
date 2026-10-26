import { useEffect } from "react";
import { SITE, repoLinkTitle } from "@/docs/site";
import { href } from "@/lib/router";
import { Logo, LogoMark } from "@/components/Logo";
import { Icon, type IconName } from "@/components/Icons";
import { CodeBlock } from "@/components/prose";
import { McButton, McCard, McList, McTabs, McTipButton, McToggle, McTooltip } from "@/components/mc";

const NAV = [
  { label: "Docs", to: "introduction/overview" },
  { label: "Components", to: "components/overview" },
  { label: "Guides", to: "guides/first-interface" },
  { label: "Reference", to: "reference/api" },
];

function HeroScene() {
  return (
    <div className="relative mx-auto w-full max-w-[34rem]" aria-label="Preview of Quill UI components">
      <div className="overflow-hidden rounded-lg border border-line bg-white shadow-soft">
        <div className="flex items-center gap-2 border-b border-line bg-mist/70 px-4 py-2.5 text-xs text-muted">
          <span className="h-2 w-2 rounded-full bg-primary" />
          <span className="font-medium">Component preview</span>
          <span className="ml-auto font-mono">HTML + CSS</span>
        </div>
        <div className="dot-grid relative px-5 pt-8 pb-14 sm:px-8">
          <div className="grid items-start gap-5 sm:grid-cols-[1fr_auto]">
            <McCard title="Quill Settings" className="w-full">
              <div className="space-y-4">
                <McTabs items={["General", "Advanced"]} active={0} />
                <McToggle label="Sound effects" on />
                <McToggle label="Reduced motion" />
                <div className="flex items-center gap-2 pt-1">
                  <McTipButton
                    label="Save"
                    primary
                    lines={["Save settings", "Sends the current values.", "/example save"]}
                  />
                  <McButton className="flex-1">Cancel</McButton>
                </div>
              </div>
            </McCard>
            <div className="sm:mt-8">
              <McList
                className="w-full sm:w-44"
                items={["General", "Controls", "Audio", "Video", "Accessibility"]}
              />
            </div>
          </div>
        </div>
      </div>
      <p className="mt-3 text-center text-xs text-muted">
        Illustrative: the same components, approximated in plain HTML/CSS.
      </p>
    </div>
  );
}

const QUICK_STEPS = [
  { t: "Install the jar", d: "Minecraft 26.3, Fabric Loader 0.19.5+, Fabric API and Java 25.", to: "introduction/installation" },
  { t: "Extend QuillScreen", d: "Build the content in buildContent() with real widgets.", to: "introduction/quick-start" },
  { t: "Send actions", d: "Route every change through an action sink, never client state.", to: "guides/backend-actions" },
];

const COMPONENTS: { name: string; desc: string; to: string; preview: React.ReactNode }[] = [
  {
    name: "Buttons",
    desc: "Primary, secondary, danger and ghost variants with selection and tooltips.",
    to: "components/button",
    preview: (
      <>
        <McButton primary>Save</McButton>
        <McButton disabled>Locked</McButton>
      </>
    ),
  },
  {
    name: "Cards",
    desc: "Bordered surfaces with an optional heading and a content rectangle.",
    to: "components/card",
    preview: (
      <McCard title="Status" className="w-40">
        <span className="mc-label-dim">Ready</span>
      </McCard>
    ),
  },
  {
    name: "Scroll lists",
    desc: "Fixed-row lists with wheel scrolling, clipping and a passive scrollbar.",
    to: "components/scroll-list",
    preview: <McList className="w-40" items={["General", "Controls", "Audio"]} />,
  },
  {
    name: "Tabs",
    desc: "Equal-width tab buttons that keep the selected visual in sync.",
    to: "components/tabs",
    preview: <McTabs className="w-44" items={["General", "Advanced"]} active={0} />,
  },
  {
    name: "Toggles",
    desc: "Compact on/off switches with keyboard support.",
    to: "components/toggle",
    preview: (
      <div className="w-44 space-y-2">
        <McToggle label="Sound" on />
        <McToggle label="Motion" />
      </div>
    ),
  },
  {
    name: "Tooltips",
    desc: "Title, gray body and an optional gold command hint.",
    to: "components/tooltips",
    preview: <McTooltip lines={["Save", "Sends values", "/example save"]} />,
  },
];

const FEATURES: { icon: IconName; title: string; body: string; to: string }[] = [
  {
    icon: "pencil",
    title: "Theme tokens",
    body: "One immutable palette plus spacing, typography, radius and elevation tokens.",
    to: "fundamentals/theme",
  },
  {
    icon: "layers",
    title: "Layout primitives",
    body: "Rects, insets, columns, rows and grids that keep screens on the 4 px grid.",
    to: "fundamentals/layout",
  },
  {
    icon: "box",
    title: "Real widgets",
    body: "Themed buttons, cards, scroll lists, tabs and toggles that render themselves.",
    to: "components/overview",
  },
  {
    icon: "branch",
    title: "Backend boundary",
    body: "Screens emit actions through QuillActionSink and never mutate game state.",
    to: "guides/backend-actions",
  },
];

export function Home({ onSearch }: { onSearch: () => void }) {
  useEffect(() => {
    document.title = `${SITE.name} · ${SITE.tagline}`;
    window.scrollTo({ top: 0, behavior: "instant" as ScrollBehavior });
  }, []);

  return (
    <div className="min-h-screen bg-white">
      <header className="sticky top-0 z-30 border-b border-line/80 bg-white/85 backdrop-blur">
        <div className="mx-auto flex h-14 max-w-6xl items-center gap-2 px-4 sm:px-6">
          <a href={href("")} className="rounded-md" aria-label="Quill UI home">
            <Logo />
          </a>
          <nav aria-label="Primary" className="ml-6 hidden items-center gap-1 md:flex">
            {NAV.map((n) => (
              <a key={n.to} href={href(n.to)} className="rounded-md px-3 py-1.5 text-sm font-medium text-ink/80 transition-colors hover:bg-mist hover:text-link">
                {n.label}
              </a>
            ))}
          </nav>
          <div className="ml-auto flex items-center gap-1">
            <a href={href("introduction/overview")} className="rounded-md px-3 py-1.5 text-sm font-medium text-ink/80 hover:bg-mist hover:text-link md:hidden">
              Docs
            </a>
            <button type="button" onClick={onSearch} className="flex h-9 items-center gap-2 rounded-md border border-line px-2.5 text-sm text-muted hover:border-primary hover:bg-mist/60" aria-label="Search documentation">
              <Icon name="search" size={15} />
              <span className="hidden sm:inline">Search</span>
              <kbd className="hidden rounded-xs border border-line px-1 font-mono text-[0.6875rem] sm:inline">/</kbd>
            </button>
            <a href={SITE.repoUrl} target="_blank" rel="noopener noreferrer" title={repoLinkTitle} className="rounded-md p-2 text-ink/80 hover:bg-mist hover:text-link" aria-label="GitHub repository">
              <Icon name="github" size={20} />
            </a>
          </div>
        </div>
      </header>

      <main>
        {/* Hero */}
        <section className="hero-bg relative overflow-hidden border-b border-line">
          <div className="pointer-events-none absolute -top-10 -right-16 h-[26rem] w-[26rem] rotate-6 opacity-15">
            <LogoMark size={416} />
          </div>
          <div className="relative mx-auto grid max-w-6xl items-center gap-12 px-4 py-14 sm:px-6 sm:py-20 lg:grid-cols-[1.05fr_1fr] lg:gap-10 lg:py-24">
            <div>
              <span className="inline-flex items-center gap-2 rounded-full border border-baby bg-white px-3 py-1 text-xs font-medium text-link">
                <span className="h-1.5 w-1.5 rounded-full bg-primary" />
                Client-side UI framework for Fabric · {SITE.version}
              </span>
              <h1 className="mt-5 text-[2.5rem] leading-[1.08] font-semibold tracking-tight text-ink sm:text-[3.25rem]">
                Build consistent <span className="whitespace-nowrap">Minecraft</span> interfaces.
              </h1>
              <p className="mt-5 max-w-[34rem] text-[1.0625rem] leading-relaxed text-muted">
                Quill UI gives Fabric mods a shared visual language: theme tokens, layout primitives,
                panels, cards, tabs, buttons, scroll lists and a clean backend action boundary.
              </p>
              <div className="mt-8 flex flex-wrap gap-3">
                <a href={href("introduction/quick-start")} className="inline-flex h-11 items-center gap-2 rounded-md bg-primary px-5 text-[0.9375rem] font-semibold text-ink shadow-[0_1px_0_rgb(255_255_255/0.6)_inset,0_1px_2px_rgb(37_55_70/0.15)] transition-colors hover:bg-[#79bdea]">
                  Get Started <Icon name="arrow-right" size={16} />
                </a>
                <a href={href("components/overview")} className="inline-flex h-11 items-center rounded-md border border-line bg-white px-5 text-[0.9375rem] font-semibold text-ink transition-colors hover:border-primary hover:bg-mist">
                  Explore Components
                </a>
              </div>
              <p className="mt-6 max-w-[34rem] text-[0.8125rem] leading-relaxed text-muted">
                Minecraft {SITE.minecraftVersion} · Java {SITE.javaVersion} · Fabric ·{" "}
                <a href={SITE.repoUrl} target="_blank" rel="noopener noreferrer" className="font-medium text-link hover:underline">
                  {SITE.repoUrl.replace("https://github.com/", "github.com/")}
                </a>
              </p>
            </div>
            <HeroScene />
          </div>
        </section>

        {/* Quick start */}
        <section className="mx-auto max-w-6xl px-4 py-16 sm:px-6 sm:py-20" aria-labelledby="quickstart">
          <div className="grid items-start gap-10 lg:grid-cols-[0.9fr_1.1fr]">
            <div>
              <p className="text-xs font-semibold tracking-[0.14em] text-link uppercase">Quick start</p>
              <h2 id="quickstart" className="mt-2 text-[1.75rem] font-semibold tracking-tight">From jar to screen in three steps</h2>
              <ol className="mt-6 space-y-0">
                {QUICK_STEPS.map((s, i) => (
                  <li key={s.t} className="border-t border-line first:border-0">
                    <a href={href(s.to)} className="group flex gap-4 py-4">
                      <span className="mt-0.5 flex h-7 w-7 shrink-0 items-center justify-center rounded-full border border-baby bg-mist text-sm font-semibold text-link">{i + 1}</span>
                      <span>
                        <span className="block font-semibold text-ink group-hover:text-link">{s.t}</span>
                        <span className="block text-sm text-muted">{s.d}</span>
                      </span>
                    </a>
                  </li>
                ))}
              </ol>
            </div>
            <CodeBlock
              title="ExampleScreen.java"
              status="none"
              code={`
public final class ExampleScreen extends QuillScreen {
    public ExampleScreen() {
        super(Component.literal("Example"), 420, 240);
    }

    @Override
    protected void buildContent() {
        QuillColumn column = new QuillColumn(
                panelX + QuillSpacing.SPACE_4,
                panelY + 60,
                panelWidth() - QuillSpacing.SPACE_4 * 2,
                QuillSpacing.SPACE_2
        );

        QuillRect slot = column.next(24);
        addRenderableWidget(new QuillButton(
                slot.x(), slot.y(), slot.width(), slot.height(),
                Component.literal("Run action"),
                QuillButton.Variant.PRIMARY,
                () -> QuillCommandSink.get().execute("example run")
        ));

        addRenderableWidget(closeButton(this::onClose));
    }
}`}
            />
          </div>
        </section>

        {/* Component overview */}
        <section className="border-y border-line bg-mist/40" aria-labelledby="components">
          <div className="mx-auto max-w-6xl px-4 py-16 sm:px-6 sm:py-20">
            <div className="flex flex-wrap items-end justify-between gap-3">
              <div>
                <p className="text-xs font-semibold tracking-[0.14em] text-link uppercase">Components</p>
                <h2 id="components" className="mt-2 text-[1.75rem] font-semibold tracking-tight">A small set, fully themed</h2>
              </div>
              <a href={href("components/overview")} className="inline-flex items-center gap-1.5 text-sm font-semibold text-link hover:underline">
                All components <Icon name="arrow-right" size={14} />
              </a>
            </div>
            <div className="mt-8 grid gap-px overflow-hidden rounded-md border border-line bg-line sm:grid-cols-2 lg:grid-cols-3">
              {COMPONENTS.map((c) => (
                <div key={c.name} className="flex flex-col bg-white">
                  <div className="dot-grid flex min-h-36 flex-wrap items-center justify-center gap-3 border-b border-line bg-mist/50 p-5">{c.preview}</div>
                  <a href={href(c.to)} className="group flex-1 p-5">
                    <h3 className="font-semibold text-ink group-hover:text-link">
                      {c.name} <Icon name="arrow-right" size={14} className="inline -translate-x-1 opacity-0 transition group-hover:translate-x-0 group-hover:opacity-100" />
                    </h3>
                    <p className="mt-1 text-sm leading-relaxed text-muted">{c.desc}</p>
                  </a>
                </div>
              ))}
            </div>
          </div>
        </section>

        {/* Core features */}
        <section className="mx-auto max-w-6xl px-4 py-16 sm:px-6 sm:py-20" aria-labelledby="features">
          <p className="text-xs font-semibold tracking-[0.14em] text-link uppercase">Core ideas</p>
          <h2 id="features" className="mt-2 text-[1.75rem] font-semibold tracking-tight">What Quill UI is built around</h2>
          <div className="mt-8 grid gap-x-10 gap-y-8 sm:grid-cols-2">
            {FEATURES.map((f) => (
              <a key={f.title} href={href(f.to)} className="group flex gap-4">
                <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-md border border-baby bg-mist text-link">
                  <Icon name={f.icon} size={19} />
                </span>
                <span>
                  <span className="block font-semibold text-ink group-hover:text-link">{f.title}</span>
                  <span className="mt-1 block text-[0.9375rem] leading-relaxed text-muted">{f.body}</span>
                </span>
              </a>
            ))}
          </div>
          <p className="mt-8 text-sm text-muted">
            Quill UI targets one Minecraft version per release. The current build targets Fabric{" "}
            {SITE.minecraftVersion} on Java {SITE.javaVersion}.
          </p>
        </section>
      </main>

      <footer className="border-t border-line bg-mist/40">
        <div className="mx-auto flex max-w-6xl flex-wrap items-center justify-between gap-4 px-4 py-6 text-sm text-muted sm:px-6">
          <Logo size={24} />
          <nav aria-label="Footer" className="flex flex-wrap items-center gap-x-5 gap-y-2">
            <a href={SITE.repoUrl} target="_blank" rel="noopener noreferrer" title={repoLinkTitle} className="hover:text-link">Repository</a>
            <a href={href("introduction/overview")} className="hover:text-link">Documentation</a>
            <a href={SITE.licenseUrl} target="_blank" rel="noopener noreferrer" className="hover:text-link">{SITE.licenseLabel}</a>
          </nav>
        </div>
      </footer>
    </div>
  );
}
