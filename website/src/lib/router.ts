import { useEffect, useState } from "react";

/**
 * Minimal hash router.
 *
 * Hash URLs (`#/components/buttons`) are used on purpose: GitHub Pages is a
 * static host with no rewrite rules, so a hash route survives a page refresh
 * on any nested URL and works under any repository sub-path.
 *
 * An optional `?s=<heading-id>` query selects a section to scroll to.
 */
export interface Route {
  path: string; // "" for home, otherwise "group/page"
  section: string | null;
}

export function parseHash(hash: string): Route {
  const raw = hash.replace(/^#/, "");
  if (!raw.startsWith("/")) return { path: "", section: null };
  const [p, q = ""] = raw.split("?");
  return {
    path: p.replace(/^\/+|\/+$/g, ""),
    section: new URLSearchParams(q).get("s"),
  };
}

export function href(path: string, section?: string | null) {
  return `#/${path}${section ? `?s=${encodeURIComponent(section)}` : ""}`;
}

export function navigate(path: string, section?: string | null) {
  window.location.hash = href(path, section).slice(1);
}

export function useRoute(): Route {
  const [route, setRoute] = useState<Route>(() => parseHash(window.location.hash));
  useEffect(() => {
    const onChange = () => setRoute(parseHash(window.location.hash));
    window.addEventListener("hashchange", onChange);
    return () => window.removeEventListener("hashchange", onChange);
  }, []);
  return route;
}
