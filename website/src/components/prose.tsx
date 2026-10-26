import { Children, isValidElement, useCallback, useRef, useState, type ReactNode } from "react";
import { Highlighted, type Lang } from "@/lib/highlight";
import { href } from "@/lib/router";
import { Icon, type IconName } from "./Icons";
import { cn } from "@/utils/cn";

/* ------------------------------------------------------------------ */
/* Headings                                                            */
/* ------------------------------------------------------------------ */

export function textOf(node: ReactNode): string {
  if (node == null || typeof node === "boolean") return "";
  if (typeof node === "string" || typeof node === "number") return String(node);
  if (Array.isArray(node)) return node.map(textOf).join("");
  if (isValidElement<{ children?: ReactNode }>(node)) return textOf(node.props.children);
  return "";
}

export function slugify(s: string) {
  return s
    .toLowerCase()
    .replace(/[^a-z0-9\s-]/g, "")
    .trim()
    .replace(/\s+/g, "-");
}

export function H2({ children }: { children: ReactNode }) {
  return (
    <h2 id={slugify(textOf(children))} className="mt-14 mb-4 border-t border-line pt-8 text-[1.5rem] leading-tight font-semibold tracking-tight text-ink first:mt-0 first:border-0 first:pt-0">
      {children}
    </h2>
  );
}

export function H3({ children }: { children: ReactNode }) {
  return (
    <h3 id={slugify(textOf(children))} className="mt-9 mb-3 text-[1.1875rem] leading-snug font-semibold tracking-tight text-ink">
      {children}
    </h3>
  );
}

/* ------------------------------------------------------------------ */
/* Text                                                                */
/* ------------------------------------------------------------------ */

export function P({ children }: { children: ReactNode }) {
  return <p className="my-4 text-[1rem] leading-[1.75] text-ink/90">{children}</p>;
}

/** Inline code. */
export function C({ children }: { children: ReactNode }) {
  return (
    <code className="rounded-xs border border-line bg-mist px-[0.35em] py-[0.1em] font-mono text-[0.86em] font-medium text-[#1d5c87] break-words">
      {children}
    </code>
  );
}

export function UL({ children }: { children: ReactNode }) {
  return (
    <ul className="my-4 space-y-2 pl-1 text-[1rem] leading-[1.7] text-ink/90 [&>li]:relative [&>li]:pl-6 [&>li]:before:absolute [&>li]:before:top-[0.72em] [&>li]:before:left-1.5 [&>li]:before:h-1.5 [&>li]:before:w-1.5 [&>li]:before:rounded-full [&>li]:before:bg-primary [&>li]:before:content-['']">
      {children}
    </ul>
  );
}

export function OL({ children }: { children: ReactNode }) {
  return (
    <ol className="my-4 list-decimal space-y-2 pl-6 text-[1rem] leading-[1.7] text-ink/90 marker:font-semibold marker:text-link">
      {children}
    </ol>
  );
}

export const li = (children: ReactNode) => <li>{children}</li>;

const linkClass =
  "font-medium text-link underline decoration-primary decoration-2 underline-offset-[3px] hover:decoration-link transition-colors";

/** Link to another documentation page, e.g. <DocLink to="components/buttons">. */
export function DocLink({ to, section, children }: { to: string; section?: string; children: ReactNode }) {
  return (
    <a href={href(to, section)} className={linkClass}>
      {children}
    </a>
  );
}

export function ExtLink({ href: url, children }: { href: string; children: ReactNode }) {
  return (
    <a href={url} target="_blank" rel="noopener noreferrer" className={linkClass}>
      {children}
    </a>
  );
}

export function Kbd({ children }: { children: ReactNode }) {
  return (
    <kbd className="rounded-xs border border-line border-b-2 bg-white px-1.5 py-0.5 font-mono text-[0.75rem] font-medium text-muted">
      {children}
    </kbd>
  );
}

/* ------------------------------------------------------------------ */
/* Callouts                                                            */
/* ------------------------------------------------------------------ */

type CalloutKind = "note" | "tip" | "warning" | "draft";

const CALLOUT: Record<CalloutKind, { icon: IconName; label: string; wrap: string; icon_cls: string }> = {
  note: { icon: "info", label: "Note", wrap: "border-primary/60 bg-mist", icon_cls: "text-link" },
  tip: { icon: "lightbulb", label: "Tip", wrap: "border-baby bg-white", icon_cls: "text-link" },
  warning: { icon: "alert", label: "Heads up", wrap: "border-[#f0d49a] bg-[#fffaf0]", icon_cls: "text-[#9a6a10]" },
  draft: { icon: "pencil", label: "Draft", wrap: "border-dashed border-primary bg-white", icon_cls: "text-link" },
};

export function Callout({ kind = "note", title, children }: { kind?: CalloutKind; title?: string; children: ReactNode }) {
  const c = CALLOUT[kind];
  return (
    <aside className={cn("callout my-6 flex gap-3 rounded-md border border-l-4 px-4 py-3.5", c.wrap)} role="note">
      <Icon name={c.icon} size={18} className={cn("mt-[3px] shrink-0", c.icon_cls)} />
      <div className="min-w-0 text-[0.9375rem] leading-[1.65] text-ink/90 [&>p]:my-1.5">
        <div className="font-semibold text-ink">{title ?? c.label}</div>
        {children}
      </div>
    </aside>
  );
}

/* ------------------------------------------------------------------ */
/* Code blocks                                                         */
/* ------------------------------------------------------------------ */

const LANG_LABEL: Record<Lang, string> = {
  java: "Java",
  groovy: "Gradle (Groovy)",
  kotlin: "Kotlin",
  bash: "Shell",
  json: "JSON",
  toml: "TOML",
  properties: "Properties",
  text: "Text",
};

async function copyText(text: string) {
  try {
    await navigator.clipboard.writeText(text);
    return true;
  } catch {
    // Fallback for non-secure contexts / older browsers.
    const ta = document.createElement("textarea");
    ta.value = text;
    ta.setAttribute("readonly", "");
    ta.style.position = "fixed";
    ta.style.opacity = "0";
    document.body.appendChild(ta);
    ta.select();
    let ok = false;
    try {
      ok = document.execCommand("copy");
    } catch {
      ok = false;
    }
    document.body.removeChild(ta);
    return ok;
  }
}

export function CopyButton({ text, className }: { text: string; className?: string }) {
  const [state, setState] = useState<"idle" | "copied" | "failed">("idle");
  const timer = useRef<number | undefined>(undefined);
  const onCopy = useCallback(async () => {
    const ok = await copyText(text);
    setState(ok ? "copied" : "failed");
    window.clearTimeout(timer.current);
    timer.current = window.setTimeout(() => setState("idle"), 1800);
  }, [text]);
  return (
    <button
      type="button"
      onClick={onCopy}
      className={cn(
        "inline-flex h-7 items-center gap-1.5 rounded-xs px-2 text-xs font-medium text-[#b8cddd] transition-colors hover:bg-white/10 hover:text-white focus-visible:outline-primary",
        className,
      )}
      aria-label={state === "copied" ? "Copied to clipboard" : "Copy code to clipboard"}
    >
      <Icon name={state === "copied" ? "check" : "copy"} size={14} className={state === "copied" ? "text-primary" : ""} />
      <span aria-live="polite">{state === "copied" ? "Copied" : state === "failed" ? "Failed" : "Copy"}</span>
    </button>
  );
}

/**
 * Code block with language label, copy button and horizontal scrolling.
 * `status` adds an optional badge; omit it for examples verified against the
 * Quill UI source.
 */
export function CodeBlock({
  code,
  lang = "java",
  title,
  status = "none",
}: {
  code: string;
  lang?: Lang;
  title?: string;
  status?: "conceptual" | "verified" | "none";
}) {
  const clean = code.replace(/^\n+|\s+$/g, "");
  return (
    <figure className="code-block my-6 overflow-hidden rounded-md border border-[#243a4e] bg-code text-code-text shadow-soft">
      <figcaption className="flex min-h-9 items-center justify-between gap-3 border-b border-white/10 bg-white/[0.03] py-1 pr-1.5 pl-4">
        <span className="flex min-w-0 items-center gap-2.5 text-xs">
          <span className="font-semibold tracking-wide text-primary uppercase">{LANG_LABEL[lang]}</span>
          {title && <span className="truncate font-mono text-[#9fb6c8]">{title}</span>}
          {status === "conceptual" && (
            <span
              className="shrink-0 rounded-full border border-[#f3c98b]/40 px-2 py-px text-[0.6875rem] font-medium text-[#f3c98b]"
              title="Names and signatures in this example are illustrative and have not been verified against the Quill UI source."
            >
              conceptual
            </span>
          )}
          {status === "verified" && <span className="shrink-0 rounded-full border border-[#b5e3a8]/40 px-2 py-px text-[0.6875rem] font-medium text-[#b5e3a8]">verified</span>}
        </span>
        <CopyButton text={clean} />
      </figcaption>
      <pre className="overflow-x-auto px-4 py-4 font-mono text-[0.8125rem] leading-[1.7] [tab-size:4]" tabIndex={0}>
        <code>
          <Highlighted code={clean} lang={lang} />
        </code>
      </pre>
    </figure>
  );
}

/** Rendered preview with the code that produces it directly beneath. */
export function Example({ preview, children }: { preview: ReactNode; children: ReactNode }) {
  return (
    <div className="my-6">
      <div className="-mb-px rounded-t-md border border-line bg-mist/60 p-6 sm:p-8">
        <div className="mb-3 text-[0.6875rem] font-semibold tracking-[0.12em] text-muted uppercase">Illustrative preview</div>
        <div className="dot-grid flex flex-wrap items-center justify-center gap-4 rounded-sm py-4">{preview}</div>
      </div>
      <div className="[&>figure]:my-0 [&>figure]:rounded-t-none">{children}</div>
    </div>
  );
}

/* ------------------------------------------------------------------ */
/* Tables                                                              */
/* ------------------------------------------------------------------ */

export function DocTable({ head, rows }: { head: string[]; rows: ReactNode[][] }) {
  return (
    <div className="my-6 overflow-x-auto rounded-md border border-line">
      <table className="w-full min-w-[34rem] border-collapse text-left text-[0.9rem]">
        <thead>
          <tr className="border-b border-line bg-mist">
            {head.map((h) => (
              <th key={h} scope="col" className="px-4 py-2.5 text-xs font-semibold tracking-wide text-muted uppercase">
                {h}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map((r, i) => (
            <tr key={i} className="border-b border-line last:border-0 align-top">
              {r.map((cell, j) => (
                <td key={j} className={cn("px-4 py-3 leading-relaxed text-ink/90", j === 0 && "font-medium whitespace-nowrap")}>
                  {cell}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

/** Convenience: flatten children into an array (used by a few pages). */
export const toArray = Children.toArray;
