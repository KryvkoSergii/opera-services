import {
    Box,
    Button,
    Chip,
    Paper,
    Stack,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Typography,
} from "@mui/material";
import InboxIcon from "@mui/icons-material/Inbox";
import { PageCard } from "../components/PageCard";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";

type Row = {
    sentAt: string;
    status: "DONE" | "PROCESSING" | "FAILED";
    diagnosis: "Normal" | "Warning" | "Critical";
    recommendation: string;
};

function diagDotBg(d: Row["diagnosis"]) {
    if (d === "Critical") return "error.main";
    if (d === "Warning") return "warning.main";
    return "success.main";
}

export function HistoryPage() {
    const { t } = useTranslation();
    const nav = useNavigate();

    // TODO: fetch from API
    const rows: Row[] = []; // <- щоб побачити empty state, залиш порожнім

    if (rows.length === 0) {
        return (
            <PageCard title={t("history.title")} subtitle={t("history.subtitle")}>
                <Paper variant="outlined" sx={{ p: { xs: 2.5, sm: 3 } }}>
                    <Stack spacing={1.5} alignItems="center" textAlign="center">
                        <InboxIcon fontSize="large" />
                        <Typography variant="h6" sx={{ fontWeight: 800 }}>
                            {t("history.emptyTitle")}
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                            {t("history.emptyText")}
                        </Typography>
                        <Button variant="contained" onClick={() => nav("/records")}>
                            {t("history.emptyCta")}
                        </Button>
                    </Stack>
                </Paper>
            </PageCard>
        );
    }

    return (
        <PageCard title={t("history.title")} subtitle={t("history.subtitle")}>
            <TableContainer component={Paper} variant="outlined">
                <Table size="small" aria-label="history table">
                    <TableHead>
                        <TableRow>
                            <TableCell sx={{ fontWeight: 800 }}>{t("history.sentAt")}</TableCell>
                            <TableCell sx={{ fontWeight: 800 }}>{t("history.status")}</TableCell>
                            <TableCell sx={{ fontWeight: 800 }}>{t("history.diagnosis")}</TableCell>
                            <TableCell sx={{ fontWeight: 800 }}>{t("history.recommendation")}</TableCell>
                        </TableRow>
                    </TableHead>

                    <TableBody>
                        {rows.map((r, idx) => (
                            <TableRow key={idx} hover>
                                <TableCell>{r.sentAt}</TableCell>

                                <TableCell>
                                    <Chip
                                        size="small"
                                        label={r.status}
                                        color={
                                            r.status === "DONE" ? "success" :
                                                r.status === "PROCESSING" ? "warning" :
                                                    "error"
                                        }
                                    />
                                </TableCell>

                                <TableCell>
                                    <Stack direction="row" spacing={1} alignItems="center">
                                        <Box
                                            sx={{
                                                width: 10,
                                                height: 10,
                                                borderRadius: "50%",
                                                bgcolor: diagDotBg(r.diagnosis),
                                            }}
                                        />
                                        <Typography variant="body2" sx={{ fontWeight: 700 }}>
                                            {r.diagnosis}
                                        </Typography>
                                    </Stack>
                                </TableCell>

                                <TableCell sx={{ maxWidth: 420 }}>
                                    <Typography variant="body2" color="text.secondary">
                                        {r.recommendation}
                                    </Typography>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </PageCard>
    );
}
