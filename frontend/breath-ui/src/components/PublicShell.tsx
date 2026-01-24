import { Outlet } from "react-router-dom";
import { Box, Container } from "@mui/material";
import { CookieNotice } from "./CookieNotice";
import { AppHeader } from "./AppHeader";

export function PublicShell() {
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
                <CookieNotice />
                <Box sx={{ mt: 2.5 }}>
                    <Outlet />
                </Box>
            </Container>
        </Box>
    );
}
