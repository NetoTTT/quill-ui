import { useEffect, useState } from "react";
import { cn } from "@/utils/cn";

interface Heading {
  id: string;
  text: string;
  level: 2 | 3;
}

/** Table of contents generated from the rendered article's h2/h3 elements. */
export function Toc({ articleKey }: { articleKey: string }) {
  const [headings, setHeadings] = useState<Heading[]>([]);
  const [active, setActive] = useState<string | null>(null);

  useEffect(() => {
    const els = Array.from(document.querySelectorAll<HTMLElement>("#article h2[id], #article h3[id]"));
    setHeadings(els.map((el) => ({ id: el.id, text: el.textContent ?? "", level: el.tagName === "H2" ? 2 : 3 })));
    setActive(els[0]?.id ?? null);

    const onScroll = () => {
      let current: string | null = els[0]?.id ?? null;
      for (const el of els) {
        if (el.getBoundingClientRect().top <= 110) current = el.id;
        else break;
      }
      // At the very bottom, highlight the last heading.
      if (window.innerHeight + window.scrollY >= document.documentElement.scrollHeight - 4 && els.length) current = els[els.length - 1].id;
      setActive(current);
    };
    onScroll();
    window.addEventListener("scroll", onScroll, { passive: true });
    window.addEventListener("resize", onScroll);
    return () => {
      window.removeEventListener("scroll", onScroll);
      window.removeEventListener("resize", onScroll);
    };
  }, [articleKey]);

  if (headings.length < 2) return null;

  return (
    <nav aria-label="On this page" className="text-sm">
      <div className="mb-3 text-[0.6875rem] font-semibold tracking-[0.12em] text-muted uppercase">On this page</div>
      <ul className="space-y-0.5 border-l border-line">
        {headings.map((h) => (
          <li key={h.id}>
            <a
              href={`#${h.id}`}
              onClick={(e) => {
                // Hash links would break the hash router, so scroll manually.
                e.preventDefault();
                document.getElementById(h.id)?.scrollIntoView({ behavior: "smooth", block: "start" });
                setActive(h.id);
              }}
              aria-current={active === h.id ? "location" : undefined}
              className={cn(
                "-ml-px block border-l-2 py-1 leading-snug transition-colors",
                h.level === 3 ? "pl-7" : "pl-4",
                active === h.id ? "border-primary font-medium text-[#1d5c87]" : "border-transparent text-muted hover:text-ink",
              )}
            >
              {h.text}
            </a>
          </li>
        ))}
      </ul>
    </nav>
  );
}
