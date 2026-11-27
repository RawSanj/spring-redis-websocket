import "@astryxdesign/core/reset.css";
import "@astryxdesign/core/astryx.css";

import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import App from "./App";

const root = document.getElementById("root");

if (root) {
  createRoot(root).render(
    <StrictMode>
      <App />
    </StrictMode>,
  );
}
