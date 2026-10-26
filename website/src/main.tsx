import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App";
import iconUrl from "./assets/quill-ui-icon.png";

const icon = document.createElement("link");
icon.rel = "icon";
icon.type = "image/png";
icon.href = iconUrl;
document.head.appendChild(icon);

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <App />
  </StrictMode>
);
