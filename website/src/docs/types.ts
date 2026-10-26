import type { ComponentType } from "react";
import type { IconName } from "@/components/Icons";

/**
 * - `draft`: page structure is in place but content still needs verification.
 * - `stub`: placeholder page; real content still to be written.
 *
 * Pages that have been checked against `src/main/java` omit `status`.
 */
export type PageStatus = "draft" | "stub";

export interface DocPage {
  slug: string;
  title: string;
  description: string;
  status?: PageStatus;
  component: ComponentType;
}

export interface DocSection {
  id: string;
  title: string;
  icon: IconName;
  pages: DocPage[];
}
