import { createElement } from "react";
import { renderToStaticMarkup } from "react-dom/server";
import { PAGES } from "@/docs";
import { slugify } from "@/components/prose";

/**
 * Client-side full-text search. The index is built from the actual rendered
 * article content (so it can never drift from the pages), once, on first use.
 * No server or external service is involved, so it works on GitHub Pages.
 */

export interface SearchEntry {
  path: string;
  pageTitle: string;
  group: string;
  heading: string | null;
  anchor: string | null;
  text: string;
}

export interface SearchHit {
  entry: SearchEntry;
  score: number;
  snippet: string;
}

let index: SearchEntry[] | null = null;

export function buildIndex(): SearchEntry[] {
  if (index) return index;
  const entries: SearchEntry[] = [];
  const parser = new DOMParser();

  for (const page of PAGES) {
    const base = { path: page.path, pageTitle: page.title, group: page.section.title };
    entries.push({ ...base, heading: null, anchor: null, text: page.description });

    const html = renderToStaticMarkup(createElement(page.component));
    const doc = parser.parseFromString(`<div>${html}</div>`, "text/html");
    doc.querySelectorAll("pre, figcaption").forEach((n) => n.remove());

    let current: SearchEntry | null = null;
    doc.body.querySelectorAll("h2, h3, p, li, td, th").forEach((el) => {
      const text = (el.textContent ?? "").replace(/\s+/g, " ").trim();
      if (!text) return;
      if (el.tagName === "H2" || el.tagName === "H3") {
        current = { ...base, heading: text, anchor: el.id || slugify(text), text: "" };
        entries.push(current);
      } else if (current) {
        current.text += (current.text ? " " : "") + text;
      } else {
        entries[entries.length - 1].text += " " + text;
      }
    });
  }
  index = entries;
  return entries;
}

function makeSnippet(text: string, terms: string[]): string {
  if (!text) return "";
  const lower = text.toLowerCase();
  let at = -1;
  for (const t of terms) {
    const i = lower.indexOf(t);
    if (i >= 0 && (at < 0 || i < at)) at = i;
  }
  const start = Math.max(0, (at < 0 ? 0 : at) - 40);
  const slice = text.slice(start, start + 140);
  return (start > 0 ? "…" : "") + slice + (start + 140 < text.length ? "…" : "");
}

export function search(query: string, limit = 8): SearchHit[] {
  const terms = query.toLowerCase().split(/\s+/).filter(Boolean);
  if (!terms.length) return [];
  const hits: SearchHit[] = [];

  for (const entry of buildIndex()) {
    const title = entry.pageTitle.toLowerCase();
    const heading = (entry.heading ?? "").toLowerCase();
    const text = entry.text.toLowerCase();
    let score = 0;
    let ok = true;
    for (const t of terms) {
      let s = 0;
      if (heading.includes(t)) s += heading.startsWith(t) ? 12 : 8;
      if (title.includes(t)) s += entry.heading ? 2 : title.startsWith(t) ? 14 : 10;
      if (text.includes(t)) s += 2;
      if (!s) {
        ok = false;
        break;
      }
      score += s;
    }
    if (!ok) continue;
    if (!entry.heading) score += 1; // slight preference for page-level hits
    hits.push({ entry, score, snippet: makeSnippet(entry.text, terms) });
  }
  return hits.sort((a, b) => b.score - a.score).slice(0, limit);
}

export function highlightParts(text: string, query: string): { text: string; hit: boolean }[] {
  const terms = query.toLowerCase().split(/\s+/).filter(Boolean);
  if (!terms.length) return [{ text, hit: false }];
  const re = new RegExp(`(${terms.map((t) => t.replace(/[.*+?^${}()|[\]\\]/g, "\\$&")).join("|")})`, "gi");
  // With one capture group, split() puts the matches at the odd indexes.
  return text
    .split(re)
    .map((part, i) => ({ text: part, hit: i % 2 === 1 }))
    .filter((p) => p.text);
}
