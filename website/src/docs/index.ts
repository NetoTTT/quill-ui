import { introduction } from "./content/introduction";
import { fundamentals } from "./content/fundamentals";
import { components } from "./content/components";
import { guides } from "./content/guides";
import { reference } from "./content/reference";
import { contributing } from "./content/contributing";
import type { DocPage, DocSection } from "./types";

export const SECTIONS: DocSection[] = [
  { id: "introduction", title: "Introduction", icon: "book", pages: introduction },
  { id: "fundamentals", title: "Fundamentals", icon: "layers", pages: fundamentals },
  { id: "components", title: "Components", icon: "box", pages: components },
  { id: "guides", title: "Guides", icon: "compass", pages: guides },
  { id: "reference", title: "Reference", icon: "file", pages: reference },
  { id: "contributing", title: "Contributing", icon: "branch", pages: contributing },
];

export interface ResolvedPage extends DocPage {
  path: string;
  section: DocSection;
}

export const PAGES: ResolvedPage[] = SECTIONS.flatMap((section) =>
  section.pages.map((p) => ({ ...p, path: `${section.id}/${p.slug}`, section })),
);

export function findPage(path: string) {
  const i = PAGES.findIndex((p) => p.path === path);
  if (i < 0) return null;
  return { page: PAGES[i], prev: PAGES[i - 1] ?? null, next: PAGES[i + 1] ?? null };
}

export const FIRST_PAGE = PAGES[0];
