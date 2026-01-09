import { createContext, PropsWithChildren, useContext, useMemo, useState } from "react";

type Mode = "light" | "dark";

const KEY = "ui_color_mode_v1";

type Ctx = { mode: Mode; toggle: () => void; setMode: (m: Mode) => void };
const ColorModeContext = createContext<Ctx | null>(null);

export function ColorModeProvider({ children }: PropsWithChildren) {
    const initial: Mode = (() => {
        try {
            const saved = localStorage.getItem(KEY);
            return saved === "dark" ? "dark" : "light";
        } catch {
            return "light";
        }
    })();

    const [mode, setModeState] = useState<Mode>(initial);

    const setMode = (m: Mode) => {
        setModeState(m);
        try {
            localStorage.setItem(KEY, m);
        } catch {}
    };

    const toggle = () => setMode(mode === "light" ? "dark" : "light");

    const value = useMemo(() => ({ mode, toggle, setMode }), [mode]);

    return <ColorModeContext.Provider value={value}>{children}</ColorModeContext.Provider>;
}

export function useColorMode() {
    const ctx = useContext(ColorModeContext);
    if (!ctx) throw new Error("useColorMode must be used within ColorModeProvider");
    return ctx;
}
