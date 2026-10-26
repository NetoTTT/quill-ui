import { Fragment, type ReactNode } from "react";

/**
 * Tiny dependency-free syntax highlighter. It only needs to be good enough for
 * documentation snippets (Java first, plus Gradle/Groovy, shell, JSON, TOML).
 */

export type Lang = "java" | "groovy" | "kotlin" | "bash" | "json" | "toml" | "properties" | "text";

const JAVA_KEYWORDS = new Set(
  (
    "abstract assert boolean break byte case catch char class const continue default do double else enum extends final finally float for goto if implements import instanceof int interface long native new package private protected public return short static strictfp super switch synchronized this throw throws transient try var void volatile while record sealed permits yield true false null " +
    // groovy / kotlin extras
    "def fun val object in is as when it plugins dependencies repositories"
  ).split(" "),
);

type Kind = "keyword" | "type" | "string" | "number" | "comment" | "annotation" | "method" | "flag" | "punct" | null;

interface Token {
  text: string;
  kind: Kind;
}

const CODE_RE =
  /(\/\/[^\n]*|\/\*[\s\S]*?\*\/)|("""[\s\S]*?"""|"(?:\\.|[^"\\\n])*"|'(?:\\.|[^'\\\n])*')|(@[A-Za-z_][\w.]*)|(\b\d[\d_]*(?:\.\d+)?[fFdDlL]?\b)|([A-Za-z_$][\w$]*)|([{}()[\];,.<>:?=+\-*/!&|]+)|(\s+)|([\s\S])/g;

function tokenizeCode(src: string): Token[] {
  const out: Token[] = [];
  for (const m of src.matchAll(CODE_RE)) {
    const [text, comment, str, ann, num, ident, punct] = m;
    if (comment) out.push({ text, kind: "comment" });
    else if (str) out.push({ text, kind: "string" });
    else if (ann) out.push({ text, kind: "annotation" });
    else if (num) out.push({ text, kind: "number" });
    else if (ident) {
      const rest = src.slice((m.index ?? 0) + text.length);
      if (JAVA_KEYWORDS.has(text)) out.push({ text, kind: "keyword" });
      else if (/^[A-Z]/.test(text)) out.push({ text, kind: "type" });
      else if (/^\s*\(/.test(rest)) out.push({ text, kind: "method" });
      else out.push({ text, kind: null });
    } else if (punct) out.push({ text, kind: "punct" });
    else out.push({ text, kind: null });
  }
  return out;
}

const SHELL_RE = /(#[^\n]*)|("(?:\\.|[^"\\\n])*"|'[^'\n]*')|(\s--?[A-Za-z][\w-]*)|(\b\d+(?:\.\d+)*\b)|([A-Za-z_./][\w./-]*)|(\s+)|([\s\S])/g;
const SHELL_CMDS = new Set(["git", "npm", "npx", "cd", "gradle", "java", "mkdir", "cp", "mv", "rm", "cat", "echo", "export"]);

function tokenizeShell(src: string): Token[] {
  const out: Token[] = [];
  let atLineStart = true;
  for (const m of src.matchAll(SHELL_RE)) {
    const [text, comment, str, flag, num, word] = m;
    if (comment) out.push({ text, kind: "comment" });
    else if (str) out.push({ text, kind: "string" });
    else if (flag) out.push({ text, kind: "flag" });
    else if (num) out.push({ text, kind: "number" });
    else if (word) {
      const isCmd = atLineStart || SHELL_CMDS.has(word) || word.startsWith("./");
      out.push({ text, kind: isCmd ? "keyword" : null });
    } else out.push({ text, kind: null });
    atLineStart = text.includes("\n") || (atLineStart && /^\s+$/.test(text));
    if (!/^\s+$/.test(text) && !text.includes("\n")) atLineStart = false;
  }
  return out;
}

const JSON_RE = /("(?:\\.|[^"\\\n])*")(\s*:)?|(-?\b\d+(?:\.\d+)?(?:[eE][+-]?\d+)?\b)|(\btrue\b|\bfalse\b|\bnull\b)|(#[^\n]*|\/\/[^\n]*)|([{}[\],:])|([\s\S])/g;

function tokenizeJson(src: string): Token[] {
  const out: Token[] = [];
  for (const m of src.matchAll(JSON_RE)) {
    const [text, str, colon, num, kw, comment, punct] = m;
    if (str && colon) {
      out.push({ text: str, kind: "type" });
      out.push({ text: colon, kind: "punct" });
    } else if (str) out.push({ text: str, kind: "string" });
    else if (num) out.push({ text: num, kind: "number" });
    else if (kw) out.push({ text: kw, kind: "keyword" });
    else if (comment) out.push({ text: comment, kind: "comment" });
    else if (punct) out.push({ text: punct, kind: "punct" });
    else out.push({ text, kind: null });
  }
  return out;
}

const PROPS_RE = /(#[^\n]*)|(^\s*\[[^\]\n]+\])|(^[ \t]*[\w.-]+)(?=[ \t]*[=:])|("(?:\\.|[^"\\\n])*")|(\b\d+(?:\.\d+)?\b)|(\btrue\b|\bfalse\b)|([\s\S])/gm;

function tokenizeProps(src: string): Token[] {
  const out: Token[] = [];
  for (const m of src.matchAll(PROPS_RE)) {
    const [text, comment, section, key, str, num, bool] = m;
    if (comment) out.push({ text, kind: "comment" });
    else if (section) out.push({ text, kind: "keyword" });
    else if (key) out.push({ text, kind: "type" });
    else if (str) out.push({ text, kind: "string" });
    else if (num) out.push({ text, kind: "number" });
    else if (bool) out.push({ text, kind: "keyword" });
    else out.push({ text, kind: null });
  }
  return out;
}

export function tokenize(src: string, lang: Lang): Token[] {
  switch (lang) {
    case "java":
    case "groovy":
    case "kotlin":
      return tokenizeCode(src);
    case "bash":
      return tokenizeShell(src);
    case "json":
      return tokenizeJson(src);
    case "toml":
    case "properties":
      return tokenizeProps(src);
    default:
      return [{ text: src, kind: null }];
  }
}

export function Highlighted({ code, lang }: { code: string; lang: Lang }): ReactNode {
  return (
    <>
      {tokenize(code, lang).map((t, i) => (
        <Fragment key={i}>{t.kind ? <span className={`tok-${t.kind}`}>{t.text}</span> : t.text}</Fragment>
      ))}
    </>
  );
}
