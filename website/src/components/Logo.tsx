import iconUrl from "@/assets/quill-ui-icon.png";

/** Official Quill UI mark (the same icon shipped in the mod jar). */
export function LogoMark({ size = 28 }: { size?: number }) {
  return <img src={iconUrl} width={size} height={size} alt="" aria-hidden="true" className="shrink-0 rounded-[6px]" />;
}

export function Logo({ size = 28 }: { size?: number }) {
  return (
    <span className="inline-flex items-center gap-2.5">
      <LogoMark size={size} />
      <span className="text-[15px] font-semibold tracking-tight text-ink">Quill UI</span>
    </span>
  );
}
