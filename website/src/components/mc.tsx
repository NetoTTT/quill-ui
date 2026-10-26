import type { ReactNode } from "react";
import { cn } from "@/utils/cn";

/**
 * HTML/CSS approximations of the Quill UI widgets (see `.mc-*` in index.css).
 * They illustrate the visual language and interaction; they are not the runtime.
 */

export function McButton({
  children,
  disabled,
  primary,
  danger,
  ghost,
  selected,
  onClick,
  className = "",
}: {
  children: ReactNode;
  disabled?: boolean;
  primary?: boolean;
  danger?: boolean;
  ghost?: boolean;
  selected?: boolean;
  onClick?: () => void;
  className?: string;
}) {
  return (
    <button
      type="button"
      disabled={disabled}
      onClick={onClick}
      className={cn(
        "mc-btn",
        primary && "is-primary",
        danger && "is-danger",
        ghost && "is-ghost",
        selected && "is-selected",
        className,
      )}
    >
      {children}
    </button>
  );
}

export function McCard({ title, children, className = "" }: { title?: string; children: ReactNode; className?: string }) {
  return (
    <div className={cn("mc mc-card", className)}>
      {title && <div className="mc-card-title">{title}</div>}
      <div className="p-3">{children}</div>
    </div>
  );
}

export function McToggle({ label, on, className = "" }: { label: string; on?: boolean; className?: string }) {
  return (
    <button type="button" role="switch" aria-checked={!!on} className={cn("mc-toggle", className)}>
      <span className="font-semibold">{label}</span>
      <span className={cn("mc-switch", on && "is-on")} aria-hidden="true">
        <span className="mc-knob" />
      </span>
    </button>
  );
}

export function McTabs({ items, active = 0, className = "" }: { items: string[]; active?: number; className?: string }) {
  return (
    <div className={cn("mc-tabs", className)} role="tablist" aria-label="Example tabs">
      {items.map((item, i) => (
        <span key={item} role="tab" aria-selected={i === active} className={cn("mc-tab", i === active && "is-selected")}>
          {item}
        </span>
      ))}
    </div>
  );
}

export function McList({ items, className = "" }: { items: string[]; className?: string }) {
  return (
    <div className={cn("mc mc-list", className)} role="listbox" aria-label="Example list">
      {items.map((item, i) => (
        <div key={`${item}-${i}`} role="option" aria-selected={false} className={cn("mc-list-row", i === 0 && "is-hover")}>
          <span className="font-semibold">{item}</span>
          <span className="mc-label-dim">{i + 1}</span>
        </div>
      ))}
      <div className="mc-list-bar" aria-hidden="true">
        <span />
      </div>
    </div>
  );
}

export function McTooltip({ lines, className = "" }: { lines: string[]; className?: string }) {
  return (
    <div className={cn("mc mc-tooltip", className)}>
      {lines.map((line, i) => (
        <div key={i} className={i === 0 ? "font-bold" : i === lines.length - 1 && line.startsWith("/") ? "text-[#f3c98b]" : "text-[#9fb6c8]"}>
          {line}
        </div>
      ))}
    </div>
  );
}

/** Button that reveals a tooltip on hover and keyboard focus. */
export function McTipButton({ label, lines, primary }: { label: string; lines: string[]; primary?: boolean }) {
  return (
    <span className="mc-tip-wrap">
      <McButton primary={primary}>{label}</McButton>
      <McTooltip lines={lines} />
    </span>
  );
}

/** Stage that holds a preview on a faint grid. */
export function Stage({ children, className = "" }: { children: ReactNode; className?: string }) {
  return (
    <div className={cn("dot-grid relative flex flex-wrap items-center justify-center gap-4 rounded-md border border-line bg-mist/60 p-6 sm:p-8", className)}>
      {children}
    </div>
  );
}
