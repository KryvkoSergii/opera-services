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
    Tooltip
} from "@mui/material";
import InboxIcon from "@mui/icons-material/Inbox";
import {PageCard} from "../components/PageCard";
import {useTranslation} from "react-i18next";
import {useNavigate} from "react-router-dom";
import {AnalysisClient, type ProbabilityStatus} from "../api/core.client";
import {useEffect, useState} from "react";

const analysisClient = new AnalysisClient({baseURL: import.meta.env.VITE_API_BASE_URL});

type SourceType = "MICROPHONE" | "STETHOSCOPE";
type Severity = "Normal" | "Warning" | "Critical";
type Diagnose = {
    severity: Severity;
    disease: string;
    advise: string;
}

function createDiagnose(
    severity: Severity,
    disease: string,
    advise: string
): Diagnose {
    return {severity, disease, advise};
}

type Row = {
    sentAt: string;
    sourceType?: SourceType;
    status: "NEW" | "UPLOADING" | "UPLOADED" | "PROCESSING" | "DONE" | "FAILED";
    diagnosis: Diagnose[];
    recommendation: string;
};

function diagDotBg(d: Severity) {
    if (d === "Critical") return "error.main";
    if (d === "Warning") return "warning.main";
    return "success.main";
}

function toSeverity(status: ProbabilityStatus): Severity {
    if (status === "high") return "Critical";
    if (status === "moderate") return "Warning";
    return "Normal";
}

function getLocalizedAdvice(status: Severity) {
    if (status === "Critical") return "history.high_risk"
    if (status === "Normal") return "history.low_risk"
    return "history.medium_risk"
}

export default function HistoryPage() {
    const {t} = useTranslation();
    const nav = useNavigate();
    const [rows, setRows] = useState<Row[]>([]);

    useEffect(() => {
        let cancelled = false;

        (async () => {
            try {
                const res = await analysisClient.listAnalysisRequests({page: 1, perPage: 10});

                const items: Row[] = res.items.map((item) => ({
                    sentAt: new Date(item.requested).toLocaleString(undefined, {
                        year: "numeric",
                        month: "2-digit",
                        day: "2-digit",
                        hour: "2-digit",
                        minute: "2-digit",
                        second: "2-digit",
                    }),
                    sourceType: item.type.toUpperCase() as SourceType,
                    status: item.status as Row["status"],
                    diagnosis: item.details.map((det) => createDiagnose(
                        toSeverity(det.status),
                        det.disease ?? "Unknown",
                        det.details
                    )),
                    recommendation: item.recommendation || "No recommendation",
                }));

                if (!cancelled) setRows(items);
            } catch (e) {
                console.error(e);
            }
        })();

        return () => {
            cancelled = true;
        };
    }, []);

    if (rows.length === 0) {
        return (
            <PageCard title={t("history.title")} subtitle={t("history.subtitle")}>
                <Paper variant="outlined" sx={{p: {xs: 2.5, sm: 3}}}>
                    <Stack spacing={1.5} alignItems="center" textAlign="center">
                        <InboxIcon fontSize="large"/>
                        <Typography variant="h6" sx={{fontWeight: 800}}>
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
                            <TableCell sx={{fontWeight: 800}}>{t("history.sentAt")}</TableCell>
                            <TableCell sx={{fontWeight: 800}}>{t("history.source")}</TableCell>
                            <TableCell sx={{fontWeight: 800}}>{t("history.status")}</TableCell>
                            <TableCell sx={{fontWeight: 800}}>{t("history.diagnosis")}</TableCell>
                            {/*<TableCell sx={{fontWeight: 800}}>{t("history.recommendation")}</TableCell>*/}
                        </TableRow>
                    </TableHead>

                    <TableBody>
                        {rows.map((r, idx) => (
                            <TableRow key={idx} hover>
                                <TableCell>{r.sentAt}</TableCell>
                                <TableCell>{r.sourceType.toLowerCase()}</TableCell>
                                <TableCell>
                                    <Chip
                                        size="small"
                                        label={r.status}
                                        color={
                                            r.status === "DONE" ? "success" : r.status === "FAILED" ? "error" : "warning"
                                        }
                                    />
                                </TableCell>

                                <TableCell>
                                    {r.diagnosis.map(diagnose => (
                                        <Tooltip
                                            title={diagnose.advise}
                                            arrow
                                        >
                                            <Stack direction="row" spacing={1} alignItems="center">
                                                <Typography variant="body2" sx={{fontWeight: 700}}>
                                                    {diagnose.disease}
                                                </Typography>
                                                <Box
                                                    sx={{
                                                        width: 10,
                                                        height: 10,
                                                        borderRadius: "50%",
                                                        bgcolor: diagDotBg(diagnose.severity),
                                                    }}
                                                />
                                                <Typography variant="body2" sx={{fontWeight: 700}}>
                                                    {t(getLocalizedAdvice(diagnose.severity))}
                                                </Typography>
                                            </Stack>
                                        </Tooltip>
                                    ))}
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </PageCard>
    );
}
