import { Alert, Button, Collapse } from "@mui/material";
import { useMemo, useState } from "react";
import { useTranslation } from "react-i18next";

const KEY = "cookie_notice_accepted_v1";

export function CookieNotice() {
    const { t } = useTranslation();

    const initiallyOpen = useMemo(() => {
        try {
            return localStorage.getItem(KEY) !== "true";
        } catch {
            return true;
        }
    }, []);

    const [open, setOpen] = useState(initiallyOpen);

    const accept = () => {
        try {
            localStorage.setItem(KEY, "true");
        } catch {}
        setOpen(false);
    };

    return (
        <Collapse in={open} sx={{ mt: 2 }}>
            <Alert
                severity="info"
                action={
                    <Button color="inherit" size="small" onClick={accept}>
                        {t("common.ok")}
                    </Button>
                }
            >
                {t("cookie.text")}
            </Alert>
        </Collapse>
    );
}
