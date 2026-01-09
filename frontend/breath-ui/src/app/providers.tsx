import { PropsWithChildren, useMemo } from "react";
import { CssBaseline, ThemeProvider } from "@mui/material";
import { buildTheme } from "./theme";
import { ColorModeProvider, useColorMode } from "./colorMode";

function InnerProviders({ children }: PropsWithChildren) {
    const { mode } = useColorMode();
    const theme = useMemo(() => buildTheme(mode), [mode]);

    return (
        <ThemeProvider theme={theme}>
            <CssBaseline />
            {children}
        </ThemeProvider>
    );
}

export function AppProviders({ children }: PropsWithChildren) {
    return (
        <ColorModeProvider>
            <InnerProviders>{children}</InnerProviders>
        </ColorModeProvider>
    );
}
