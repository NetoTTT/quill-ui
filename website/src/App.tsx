import { useEffect, useState } from "react";
import { useRoute } from "@/lib/router";
import { DocLayout } from "@/components/DocLayout";
import { SearchDialog } from "@/components/SearchDialog";
import { Home } from "@/pages/Home";

export default function App() {
  const route = useRoute();
  const [searchOpen, setSearchOpen] = useState(false);

  // Global shortcuts: Ctrl/Cmd+K, or "/" outside form fields.
  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      const t = e.target as HTMLElement | null;
      const typing = t && (t.tagName === "INPUT" || t.tagName === "TEXTAREA" || t.isContentEditable);
      if ((e.key.toLowerCase() === "k" && (e.metaKey || e.ctrlKey)) || (e.key === "/" && !typing)) {
        e.preventDefault();
        setSearchOpen(true);
      }
    };
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, []);

  // Search dialog closes on navigation.
  useEffect(() => setSearchOpen(false), [route.path, route.section]);

  return (
    <>
      {route.path === "" ? <Home onSearch={() => setSearchOpen(true)} /> : <DocLayout path={route.path} section={route.section} onSearch={() => setSearchOpen(true)} />}
      <SearchDialog open={searchOpen} onClose={() => setSearchOpen(false)} />
    </>
  );
}
