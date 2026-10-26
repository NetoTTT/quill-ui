import { useEffect, useId, useMemo, useRef, useState } from "react";
import { highlightParts, search } from "@/lib/search";
import { navigate } from "@/lib/router";
import { PAGES } from "@/docs";
import { Icon } from "./Icons";
import { Kbd } from "./prose";
import { cn } from "@/utils/cn";

function Marked({ text, query }: { text: string; query: string }) {
  return (
    <>
      {highlightParts(text, query).map((p, i) =>
        p.hit ? (
          <mark key={i} className="rounded-[2px] bg-baby px-px text-ink">
            {p.text}
          </mark>
        ) : (
          <span key={i}>{p.text}</span>
        ),
      )}
    </>
  );
}

export function SearchDialog({ open, onClose }: { open: boolean; onClose: () => void }) {
  const [query, setQuery] = useState("");
  const [active, setActive] = useState(0);
  const inputRef = useRef<HTMLInputElement>(null);
  const listRef = useRef<HTMLUListElement>(null);
  const listId = useId();

  const hits = useMemo(() => (open ? search(query) : []), [query, open]);

  useEffect(() => {
    if (open) {
      setQuery("");
      setActive(0);
      const opener = document.activeElement as HTMLElement | null;
      requestAnimationFrame(() => inputRef.current?.focus());
      const prev = document.body.style.overflow;
      document.body.style.overflow = "hidden";
      return () => {
        document.body.style.overflow = prev;
        // Return focus to whatever opened the dialog (unless navigation moved it).
        if (opener && document.contains(opener) && document.activeElement === document.body) opener.focus();
        else if (opener && document.contains(opener) && document.activeElement?.tagName === "INPUT") opener.focus();
      };
    }
  }, [open]);

  useEffect(() => setActive(0), [query]);

  useEffect(() => {
    listRef.current?.querySelector<HTMLElement>(`[data-idx="${active}"]`)?.scrollIntoView({ block: "nearest" });
  }, [active]);

  if (!open) return null;

  const go = (i: number) => {
    const h = hits[i];
    if (!h) return;
    navigate(h.entry.path, h.entry.anchor);
    onClose();
  };

  const onKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Escape") {
      e.preventDefault();
      onClose();
    } else if (e.key === "ArrowDown") {
      e.preventDefault();
      setActive((a) => Math.min(a + 1, Math.max(hits.length - 1, 0)));
    } else if (e.key === "ArrowUp") {
      e.preventDefault();
      setActive((a) => Math.max(a - 1, 0));
    } else if (e.key === "Enter") {
      e.preventDefault();
      go(active);
    } else if (e.key === "Tab") {
      // Keep focus inside the dialog: the input is the only tab stop.
      e.preventDefault();
      inputRef.current?.focus();
    }
  };

  return (
    <div className="fixed inset-0 z-[60] flex items-start justify-center px-3 pt-[10vh] sm:px-6" onKeyDown={onKeyDown}>
      <div className="absolute inset-0 bg-ink/30 backdrop-blur-[2px]" onClick={onClose} aria-hidden="true" />
      <div role="dialog" aria-modal="true" aria-label="Search documentation" className="fade-in relative w-full max-w-xl overflow-hidden rounded-lg border border-line bg-white shadow-[0_24px_60px_-20px_rgb(37_55_70/0.35)]">
        <div className="flex items-center gap-3 border-b border-line px-4">
          <Icon name="search" size={18} className="shrink-0 text-muted" />
          <input
            ref={inputRef}
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            type="search"
            placeholder="Search the documentation…"
            className="h-14 min-w-0 flex-1 bg-transparent text-base text-ink outline-none placeholder:text-muted/80 [&::-webkit-search-cancel-button]:hidden"
            role="combobox"
            aria-expanded={hits.length > 0}
            aria-controls={listId}
            aria-activedescendant={hits.length ? `${listId}-${active}` : undefined}
            aria-autocomplete="list"
            autoComplete="off"
            spellCheck={false}
          />
          <button type="button" onClick={onClose} className="rounded-xs border border-line px-1.5 py-0.5 font-mono text-[0.6875rem] text-muted hover:bg-mist" aria-label="Close search">
            esc
          </button>
        </div>

        <div className="max-h-[min(60vh,28rem)] overflow-y-auto">
          {!query.trim() && (
            <div className="px-4 py-3">
              <div className="px-1 pb-2 text-[0.6875rem] font-semibold tracking-[0.12em] text-muted uppercase">Jump to</div>
              <ul>
                {PAGES.filter((p) => ["introduction/quick-start", "components/overview", "guides/first-interface", "reference/troubleshooting"].includes(p.path)).map((p) => (
                  <li key={p.path}>
                    <button
                      type="button"
                      onClick={() => {
                        navigate(p.path);
                        onClose();
                      }}
                      className="flex w-full items-center justify-between rounded-md px-2 py-2 text-left text-sm hover:bg-mist"
                    >
                      <span className="font-medium text-ink">{p.title}</span>
                      <span className="text-xs text-muted">{p.section.title}</span>
                    </button>
                  </li>
                ))}
              </ul>
            </div>
          )}

          {query.trim() && hits.length === 0 && (
            <div className="px-6 py-10 text-center text-sm text-muted" role="status">
              No results for <span className="font-medium text-ink">&ldquo;{query}&rdquo;</span>.
            </div>
          )}

          {hits.length > 0 && (
            <ul ref={listRef} id={listId} role="listbox" aria-label="Search results" className="p-2">
              {hits.map((h, i) => (
                <li key={`${h.entry.path}-${h.entry.anchor}-${i}`} role="option" id={`${listId}-${i}`} aria-selected={i === active} data-idx={i}>
                  <button
                    type="button"
                    tabIndex={-1}
                    onMouseMove={() => setActive(i)}
                    onClick={() => go(i)}
                    className={cn("flex w-full items-start gap-3 rounded-md px-3 py-2.5 text-left", i === active ? "bg-mist" : "")}
                  >
                    <Icon name={h.entry.heading ? "hash" : "file"} size={16} className={cn("mt-0.5 shrink-0", i === active ? "text-link" : "text-muted")} />
                    <span className="min-w-0 flex-1">
                      <span className="block truncate text-sm font-semibold text-ink">
                        <Marked text={h.entry.heading ?? h.entry.pageTitle} query={query} />
                      </span>
                      <span className="block truncate text-xs text-muted">
                        {h.entry.group} › {h.entry.pageTitle}
                      </span>
                      {h.snippet && (
                        <span className="mt-1 line-clamp-2 block text-[0.8125rem] leading-snug text-ink/75">
                          <Marked text={h.snippet} query={query} />
                        </span>
                      )}
                    </span>
                    {i === active && <Icon name="enter" size={14} className="mt-1 shrink-0 text-link" />}
                  </button>
                </li>
              ))}
            </ul>
          )}
        </div>

        <div className="flex items-center gap-4 border-t border-line bg-mist/50 px-4 py-2 text-xs text-muted">
          <span className="flex items-center gap-1">
            <Kbd>↑</Kbd>
            <Kbd>↓</Kbd> navigate
          </span>
          <span className="flex items-center gap-1">
            <Kbd>↵</Kbd> open
          </span>
          <span className="ml-auto hidden sm:inline">Searches all pages offline</span>
        </div>
      </div>
    </div>
  );
}
