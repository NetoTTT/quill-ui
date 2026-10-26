# Quill UI documentation

Static documentation website for **Quill UI**, the client-side UI framework for Fabric.

Built with React, Vite and Tailwind CSS. The production build is a single self-contained
`dist/index.html`, deployed to GitHub Pages by `.github/workflows/pages.yml`.

## Run locally

```bash
npm install
npm run dev        # http://localhost:5173
```

## Build

```bash
npm run build      # typecheck + dist/index.html
npm run preview    # serve the production build locally
```

## Deploy

Pushes to `main` that touch `website/` trigger the Pages workflow. The site is published at
**https://netottt.github.io/quill-ui/**.

Repository setup (one time): **Settings → Pages → Source: GitHub Actions**.

## Editing content

Articles live in `src/docs/content/*.tsx` and are composed from helpers in
`src/components/prose.tsx` (`H2`, `H3`, `P`, `CodeBlock`, `Example`, `DocTable`, `Callout`,
`DocLink`). Register a page in the exported list at the bottom of the file (slug, title,
description, component). Navigation, search, breadcrumbs, the table of contents and previous/next
links update automatically.

Site-wide values (repository URL, version, license) live in `src/docs/site.ts`.

## Structure

```
src/
  App.tsx                 route switch + global shortcuts (Ctrl/Cmd+K, "/")
  pages/Home.tsx          homepage
  components/             layout, sidebar, TOC, search dialog, prose helpers, previews
  docs/                   site config, navigation model, article content
  lib/                    hash router, syntax highlighter, search index
```

Hash routing (`#/components/buttons`) keeps nested URLs working on GitHub Pages without server
rewrite rules, and the single-file build works under any repository sub-path.
