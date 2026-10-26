import { useEffect, useState } from "react";
import { SECTIONS } from "@/docs";
import { SITE, repoLinkTitle } from "@/docs/site";
import { href } from "@/lib/router";
import { Logo } from "./Logo";
import { Icon } from "./Icons";
import { cn } from "@/utils/cn";

const isMac = typeof navigator !== "undefined" && /Mac|iPhone|iPad/.test(navigator.platform);

export function Sidebar({
  activePath,
  open,
  onClose,
  onSearch,
}: {
  activePath: string;
  open: boolean;
  onClose: () => void;
  onSearch: () => void;
}) {
  const activeSection = activePath.split("/")[0];
  // Groups the reader expanded or collapsed manually. The active group is open by default.
  const [manual, setManual] = useState<Record<string, boolean>>({});

  // When navigating into a group, make sure it is expanded.
  useEffect(() => {
    setManual((m) => (m[activeSection] === false ? { ...m, [activeSection]: true } : m));
  }, [activeSection]);

  return (
    <>
      <div
        className={cn("fixed inset-0 z-40 bg-ink/30 backdrop-blur-[2px] transition-opacity lg:hidden", open ? "opacity-100" : "pointer-events-none opacity-0")}
        onClick={onClose}
        aria-hidden="true"
      />
      <aside
        id="site-nav"
        className={cn(
          "fixed inset-y-0 left-0 z-50 flex w-[18.5rem] max-w-[86vw] flex-col border-r border-line bg-white transition-[transform,visibility] duration-200 ease-out lg:w-72 lg:translate-x-0",
          open ? "translate-x-0 shadow-soft" : "max-lg:invisible max-lg:-translate-x-full",
        )}
        aria-label="Documentation sidebar"
      >
        <div className="flex items-center justify-between px-5 pt-5 pb-4">
          <a href={href("")} className="inline-flex items-center gap-2 rounded-md" aria-label="Quill UI home">
            <Logo size={30} />
          </a>
          <div className="flex items-center gap-2">
            <span className="rounded-full border border-line bg-mist px-2 py-0.5 text-[0.6875rem] font-medium text-link">{SITE.version}</span>
            <button type="button" onClick={onClose} className="-mr-1 rounded-md p-1.5 text-muted hover:bg-mist lg:hidden" aria-label="Close navigation">
              <Icon name="close" size={18} />
            </button>
          </div>
        </div>

        <div className="px-4 pb-3">
          <button
            type="button"
            onClick={onSearch}
            className="flex h-9 w-full items-center gap-2 rounded-md border border-line bg-white px-2.5 text-sm text-muted transition-colors hover:border-primary hover:bg-mist/60"
          >
            <Icon name="search" size={15} />
            <span className="flex-1 text-left">Search docs</span>
            <span className="hidden gap-0.5 font-mono text-[0.6875rem] sm:flex">
              <kbd className="rounded-xs border border-line px-1">{isMac ? "⌘" : "Ctrl"}</kbd>
              <kbd className="rounded-xs border border-line px-1">K</kbd>
            </span>
          </button>
        </div>

        <nav className="flex-1 overflow-y-auto px-3 pb-6" aria-label="Documentation">
          {SECTIONS.map((section) => {
            const isActiveGroup = section.id === activeSection;
            const expanded = manual[section.id] ?? isActiveGroup;
            const panelId = `nav-${section.id}`;
            return (
              <div key={section.id} className="mt-1">
                <button
                  type="button"
                  aria-expanded={expanded}
                  aria-controls={panelId}
                  onClick={() => setManual((m) => ({ ...m, [section.id]: !expanded }))}
                  className={cn(
                    "group flex w-full items-center gap-2.5 rounded-md px-2 py-2 text-left text-[0.8125rem] font-semibold tracking-wide transition-colors hover:bg-mist/70",
                    isActiveGroup ? "text-ink" : "text-ink/80",
                  )}
                >
                  <Icon name={section.icon} size={16} className={isActiveGroup ? "text-link" : "text-muted"} />
                  <span className="flex-1">{section.title}</span>
                  <Icon name="chevron-right" size={14} className={cn("text-muted transition-transform", expanded && "rotate-90")} />
                </button>
                <ul id={panelId} hidden={!expanded} className="relative mt-0.5 mb-2 ml-[1.15rem] border-l border-line">
                  {section.pages.map((p) => {
                    const path = `${section.id}/${p.slug}`;
                    const active = path === activePath;
                    return (
                      <li key={path}>
                        <a
                          href={href(path)}
                          aria-current={active ? "page" : undefined}
                          className={cn(
                            "relative -ml-px flex items-center justify-between gap-2 rounded-r-md border-l-2 py-1.5 pr-2 pl-4 text-[0.875rem] transition-colors",
                            active
                              ? "border-primary bg-mist font-semibold text-[#1d5c87]"
                              : "border-transparent text-muted hover:border-baby hover:bg-mist/50 hover:text-ink",
                          )}
                        >
                          <span>{p.title}</span>
                          {p.status === "stub" && <span className="rounded-full border border-line px-1.5 text-[0.625rem] font-medium tracking-wide text-muted uppercase">stub</span>}
                        </a>
                      </li>
                    );
                  })}
                </ul>
              </div>
            );
          })}
        </nav>

        <div className="flex items-center justify-between gap-2 border-t border-line px-5 py-3.5 text-xs text-muted">
          <a href={SITE.repoUrl} target="_blank" rel="noopener noreferrer" title={repoLinkTitle} className="inline-flex items-center gap-1.5 rounded-md font-medium text-ink/80 hover:text-link">
            <Icon name="github" size={15} />
            GitHub
          </a>
          <a href={href("contributing/license")} className="rounded-md hover:text-link">
            {SITE.licenseLabel}
          </a>
        </div>
      </aside>
    </>
  );
}
