import { createTheme } from "@mui/material/styles";

export function buildTheme(mode: "light" | "dark") {
    return createTheme({
        palette: { mode },
        shape: { borderRadius: 14 },
        typography: {
            fontFamily: ["Inter", "system-ui", "Segoe UI", "Roboto", "Arial"].join(","),
            h5: { fontWeight: 800 },
            h6: { fontWeight: 800 },
        },
        components: {
            MuiButton: { styleOverrides: { root: { textTransform: "none", fontWeight: 700 } } },
            MuiPaper: { styleOverrides: { root: { borderRadius: 16 } } },
            MuiTextField: { defaultProps: { fullWidth: true } },
        },
    });
}
