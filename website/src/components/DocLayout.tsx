import { useEffect, useRef, useState } from "react";
import { findPage, FIRST_PAGE } from "@/docs";
import { SITE } from "@/docs/site";
import { href } from "@/lib/router";
import { Sidebar } from "./Sidebar";
import { Toc } from "./Toc";
import { LogoMark } from "./Logo";
import { Icon } from "./Icons";
import { C, Callout, DocLink, P } from "./prose";
import { cn } from "@/utils/cn";

export function DocLayout({ path, section, onSearch }: { path: string; section: string | null; onSearch: () => void }) {
  const found = findPage(path);
  const [drawer, setDrawer] = useState(false);
  const titleRef = useRef<HTMLHeadingElement>(null);

  // Close drawer on navigation.
  useEffect(() => setDrawer(false), [path]);

  // Title, scroll position and focus on route change.
  const pageTitle = found?.page.title;
  useEffect(() => {
    document.title = pageTitle ? `${pageTitle} · ${SITE.name} Docs` : `Not found · ${SITE.name} Docs`;
    if (section) {
      requestAnimationFrame(() => document.getElementById(section)?.scrollIntoView({ block: "start" }));
    } else {
      window.scrollTo({ top: 0, behavior: "instant" as ScrollBehavior });
      titleRef.current?.focus({ preventScroll: true });
    }
  }, [path, section, pageTitle]);

  // Drawer: Escape closes, body scroll locked.
  useEffect(() => {
    if (!drawer) return;
    const onKey = (e: KeyboardEvent) => e.key === "Escape" && setDrawer(false);
    document.addEventListener("keydown", onKey);
    const prev = document.body.style.overflow;
    document.body.style.overflow = "hidden";
    return () => {
      document.removeEventListener("keydown", onKey);
      document.body.style.overflow = prev;
    };
  }, [drawer]);

  const Page = found?.page.component;

  return (
    <div className="min-h-screen bg-white">
      <button
        type="button"
        className="sr-only z-[70] rounded-md bg-ink px-3 py-2 text-sm text-white focus:not-sr-only focus:fixed focus:top-3 focus:left-3"
        onClick={() => titleRef.current?.focus()}
      >
        Skip to content
      </button>

      <Sidebar activePath={path} open={drawer} onClose={() => setDrawer(false)} onSearch={onSearch} />

      <div className="lg:pl-72">
        {/* Mobile / tablet top bar */}
        <header className="sticky top-0 z-30 flex h-14 items-center gap-2 border-b border-line bg-white/90 px-3 backdrop-blur lg:hidden">
          <button
            type="button"
            onClick={() => setDrawer(true)}
            className="rounded-md p-2 text-ink hover:bg-mist"
            aria-label="Open navigation"
            aria-expanded={drawer}
            aria-controls="site-nav"
          >
            <Icon name="menu" size={20} />
          </button>
          <a href={href("")} className="flex flex-1 items-center gap-2 rounded-md font-semibold tracking-tight">
            <LogoMark size={26} />
            Quill UI
          </a>
          <button type="button" onClick={onSearch} className="rounded-md p-2 text-ink hover:bg-mist" aria-label="Search documentation">
            <Icon name="search" size={19} />
          </button>
        </header>

        <div className="mx-auto flex max-w-[76rem] justify-center gap-10 px-5 sm:px-8 xl:px-10">
          <main className="min-w-0 max-w-[46rem] flex-1 py-8 sm:py-12 lg:py-14">
            {found && Page ? (
              <>
                <nav aria-label="Breadcrumb" className="mb-5 text-[0.8125rem] text-muted">
                  <ol className="flex flex-wrap items-center gap-1.5">
                    <li>
                      <a href={href("")} className="rounded hover:text-link">
                        Home
                      </a>
                    </li>
                    <Icon name="chevron-right" size={12} />
                    <li>
                      <a href={href(found.page.section.pages[0] ? `${found.page.section.id}/${found.page.section.pages[0].slug}` : "")} className="rounded hover:text-link">
                        {found.page.section.title}
                      </a>
                    </li>
                    <Icon name="chevron-right" size={12} />
                    <li aria-current="page" className="font-medium text-ink/80">
                      {found.page.title}
                    </li>
                  </ol>
                </nav>

                <header className="mb-8 border-b border-line pb-7">
                  <h1 ref={titleRef} tabIndex={-1} className="text-[2.125rem] leading-[1.15] font-semibold tracking-tight text-ink outline-none sm:text-[2.5rem]">
                    {found.page.title}
                  </h1>
                  <p className="mt-3 text-[1.0625rem] leading-relaxed text-muted">{found.page.description}</p>
                  {found.page.status && (
                    <div className="mt-4">
                      <span
                        className={cn(
                          "inline-flex items-center gap-1.5 rounded-full border px-2.5 py-0.5 text-xs font-medium",
                          found.page.status === "stub" ? "border-[#f0d49a] bg-[#fffaf0] text-[#8a5d0c]" : "border-baby bg-mist text-link",
                        )}
                      >
                        <Icon name="pencil" size={12} />
                        {found.page.status === "stub" ? "Stub: outline only" : "Draft: content may change"}
                      </span>
                    </div>
                  )}
                </header>

                <article id="article" key={path} className="fade-in">
                  <Page />
                </article>

                <nav aria-label="Previous and next" className="mt-16 grid gap-3 border-t border-line pt-8 sm:grid-cols-2">
                  {found.prev ? (
                    <a href={href(found.prev.path)} className="group rounded-md border border-line px-4 py-3 transition-colors hover:border-primary hover:bg-mist/50">
                      <span className="flex items-center gap-1.5 text-xs text-muted">
                        <Icon name="arrow-left" size={13} /> Previous
                      </span>
                      <span className="mt-0.5 block font-semibold text-ink group-hover:text-link">{found.prev.title}</span>
                    </a>
                  ) : (
                    <span />
                  )}
                  {found.next && (
                    <a href={href(found.next.path)} className="group rounded-md border border-line px-4 py-3 text-right transition-colors hover:border-primary hover:bg-mist/50 sm:col-start-2">
                      <span className="flex items-center justify-end gap-1.5 text-xs text-muted">
                        Next <Icon name="arrow-right" size={13} />
                      </span>
                      <span className="mt-0.5 block font-semibold text-ink group-hover:text-link">{found.next.title}</span>
                    </a>
                  )}
                </nav>
              </>
            ) : (
              <div id="article">
                <h1 ref={titleRef} tabIndex={-1} className="text-[2.25rem] font-semibold tracking-tight outline-none">
                  Page not found
                </h1>
                <P>
                  There is no documentation page at <C>{path || "/"}</C>.
                </P>
                <Callout kind="note">
                  <P>
                    Try the search, or start from <DocLink to={FIRST_PAGE.path}>{FIRST_PAGE.title}</DocLink>.
                  </P>
                </Callout>
              </div>
            )}

            <footer className="mt-12 flex flex-wrap items-center justify-between gap-2 text-xs text-muted">
              <span>
                Quill UI documentation · {SITE.version} · {SITE.licenseLabel}
              </span>
              <a
                href={SITE.issuesUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="rounded hover:text-link"
              >
                Report a problem
              </a>
            </footer>
          </main>

          <aside className="hidden w-56 shrink-0 xl:block" aria-label="Page outline">
            <div className="sticky top-12 max-h-[calc(100vh-6rem)] overflow-y-auto py-14 pr-1">{found && <Toc articleKey={path} />}</div>
          </aside>
        </div>
      </div>
    </div>
  );
}
