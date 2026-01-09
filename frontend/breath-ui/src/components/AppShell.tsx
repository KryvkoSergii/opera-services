import { Outlet, useLocation, useNavigate } from "react-router-dom";
import {
    AppBar,
    Box,
    Button,
    Container,
    Toolbar,
    Typography,
    Stack,
    Chip,
    IconButton,
    ToggleButton,
    ToggleButtonGroup,
    Tooltip,
} from "@mui/material";
import MonitorHeartIcon from "@mui/icons-material/MonitorHeart";
import DarkModeIcon from "@mui/icons-material/DarkMode";
import LightModeIcon from "@mui/icons-material/LightMode";
import { CookieNotice } from "./CookieNotice";
import { useColorMode } from "../app/colorMode";
import { useTranslation } from "react-i18next";

export function AppShell() {
    const nav = useNavigate();
    const loc = useLocation();
    const isActive = (p: string) => loc.pathname === p;

    const { mode, toggle } = useColorMode();
    const { t, i18n } = useTranslation();

    const lang = i18n.language === "en" ? "en" : "uk";

    return (
        <Box
            sx={{
                minHeight: "100vh",
                background:
                    "radial-gradient(1200px 600px at 10% 10%, rgba(0,0,0,0.06), transparent 60%)," +
                    "radial-gradient(900px 500px at 90% 0%, rgba(0,0,0,0.05), transparent 55%)",
                py: { xs: 2, sm: 3 },
            }}
        >
            <Container maxWidth="md">
                <AppBar position="static" elevation={0} color="transparent">
                    <Toolbar sx={{ px: 0, gap: 2, flexWrap: "wrap" }}>
                        <Stack direction="row" alignItems="center" spacing={1} sx={{ flex: 1, minWidth: 220 }}>
                            <MonitorHeartIcon />
                            <Typography variant="h6">{t("app.title")}</Typography>
                            <Chip size="small" label={t("app.beta")} />
                        </Stack>

                        <Stack direction="row" spacing={1} alignItems="center">
                            <ToggleButtonGroup
                                size="small"
                                exclusive
                                value={lang}
                                onChange={(_, v) => v && i18n.changeLanguage(v)}
                            >
                                <ToggleButton value="uk">UK</ToggleButton>
                                <ToggleButton value="en">EN</ToggleButton>
                            </ToggleButtonGroup>

                            <Tooltip title={mode === "dark" ? "Light" : "Dark"}>
                                <IconButton onClick={toggle}>
                                    {mode === "dark" ? <LightModeIcon /> : <DarkModeIcon />}
                                </IconButton>
                            </Tooltip>
                        </Stack>

                        <Stack direction="row" spacing={1} sx={{ width: { xs: "100%", sm: "auto" } }}>
                            <Button
                                fullWidth
                                variant={isActive("/records") ? "contained" : "outlined"}
                                onClick={() => nav("/records")}
                            >
                                {t("nav.records")}
                            </Button>
                            <Button
                                fullWidth
                                variant={isActive("/history") ? "contained" : "outlined"}
                                onClick={() => nav("/history")}
                            >
                                {t("nav.history")}
                            </Button>
                            <Button fullWidth color="inherit" onClick={() => nav("/login")}>
                                {t("nav.logout")}
                            </Button>
                        </Stack>
                    </Toolbar>
                </AppBar>

                <CookieNotice />

                <Box sx={{ mt: 2.5 }}>
                    <Outlet />
                </Box>
            </Container>
        </Box>
    );
}
