import {
    AppBar,
    Chip,
    IconButton,
    Stack,
    ToggleButton,
    ToggleButtonGroup,
    Toolbar,
    Tooltip,
    Typography,
} from "@mui/material";
import MonitorHeartIcon from "@mui/icons-material/MonitorHeart";
import DarkModeIcon from "@mui/icons-material/DarkMode";
import LightModeIcon from "@mui/icons-material/LightMode";
import { useColorMode } from "../app/colorMode";
import { useTranslation } from "react-i18next";

export function AppHeader() {
    const { mode, toggle } = useColorMode();
    const { t, i18n } = useTranslation();
    const lang = i18n.language === "en" ? "en" : "uk";

    return (
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
            </Toolbar>
        </AppBar>
    );
}
