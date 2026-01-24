import { Outlet, useLocation, useNavigate } from "react-router-dom";
import { Box, Button, Container, Stack } from "@mui/material";
import { CookieNotice } from "./CookieNotice";
import { useTranslation } from "react-i18next";
import { clearToken } from "../auth/token";
import { AppHeader } from "./AppHeader";

export function AppShell() {
    const nav = useNavigate();
    const loc = useLocation();
    const isActive = (p: string) => loc.pathname === p;

    const { t } = useTranslation();

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
                <AppHeader />

                <Stack direction="row" spacing={1} sx={{ mt: 1.5, width: { xs: "100%", sm: "auto" } }}>
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

                    <Button
                        fullWidth
                        color="inherit"
                        onClick={() => {
                            clearToken();
                            nav("/login", { replace: true });
                        }}
                    >
                        {t("nav.logout")}
                    </Button>
                </Stack>

                <CookieNotice />

                <Box sx={{ mt: 2.5 }}>
                    <Outlet />
                </Box>
            </Container>
        </Box>
    );
}
