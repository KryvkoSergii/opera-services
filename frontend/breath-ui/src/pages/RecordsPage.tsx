import {useEffect, useMemo, useRef, useState} from "react";
import {
    Alert,
    Box,
    Button,
    Divider,
    FormControl,
    InputLabel,
    LinearProgress,
    MenuItem,
    Select,
    Stack,
    Typography,
} from "@mui/material";
import MicIcon from "@mui/icons-material/Mic";
import StopIcon from "@mui/icons-material/Stop";
import UploadFileIcon from "@mui/icons-material/UploadFile";
import SendIcon from "@mui/icons-material/Send";
import {PageCard} from "../components/PageCard";
import {StatusChip, type JobStatus} from "../components/StatusChip";
import {useTranslation} from "react-i18next";
import {AnalysisClient, type StatusEvent, type RequestStatus} from "../api/core.client";

type SourceType = "MICROPHONE" | "STETHOSCOPE";

const analysisClient = new AnalysisClient({baseURL: import.meta.env.VITE_API_BASE_URL});

function useRequestSse(requestId?: string) {
    const [event, setEvent] = useState<StatusEvent | null>(null);
    const [connected, setConnected] = useState(false);
    const esRef = useRef<EventSource | null>(null);

    useEffect(() => {
        esRef.current?.close();
        esRef.current = null;
        setConnected(false);
        setEvent(null);

        if (!requestId) return;

        const url = `${import.meta.env.VITE_API_BASE_URL}/v1/analyses/${encodeURIComponent(requestId)}/events`;
        const es = new EventSource(url);
        esRef.current = es;

        es.onopen = () => setConnected(true);
        es.onerror = () => setConnected(false);

        es.addEventListener("status", (e: MessageEvent) => {
            try {
                setEvent(JSON.parse(e.data) as StatusEvent);
            } catch (err) {
                console.error("Bad SSE payload", err);
            }
        });

        return () => {
            es.close();
            esRef.current = null;
        };
    }, [requestId]);

    const close = () => {
        esRef.current?.close();
        esRef.current = null;
        setConnected(false);
    };

    return { event, connected, close };
}

export default function RecordsPage() {
    const {t} = useTranslation();
    const [source, setSource] = useState<SourceType>("MICROPHONE");

    const [isRecording, setIsRecording] = useState(false);
    const mediaRecorderRef = useRef<MediaRecorder | null>(null);
    const chunksRef = useRef<BlobPart[]>([]);
    const [recordedBlob, setRecordedBlob] = useState<Blob | null>(null);

    const [pickedFile, setPickedFile] = useState<File | null>(null);

    const [status, setStatus] = useState<JobStatus>("NEW");
    const [statusText, setStatusText] = useState<string>("");

    const selectedBlobInfo = useMemo(() => {
        if (pickedFile) return `File: ${pickedFile.name} (${Math.round(pickedFile.size / 1024)} KB)`;
        if (recordedBlob) return `Recorded audio (${Math.round(recordedBlob.size / 1024)} KB)`;
        return t("records.noAudio");
    }, [pickedFile, recordedBlob]);

    const [activeRequestId, setActiveRequestId] = useState<string | undefined>(undefined);
    const { event, connected, close } = useRequestSse(activeRequestId);

    useEffect(() => {
        return () => {
            try {
                mediaRecorderRef.current?.stop();
            } catch {
            }
        };
    }, []);

    useEffect(() => {
        if (!event) return;

        setStatus(event.status);
        setStatusText(t(`records.${event.status.toLowerCase()}`));

        if (event.status === "DONE" || event.status === "FAILED") {
            close();
        }
    }, [event, close]);

    useEffect(() => {
        if (!activeRequestId) return;

        const timer = setTimeout(() => {
            setStatus("TIMEOUT" as RequestStatus);
            setStatusText(t("records.timeout"));
            close();
            setActiveRequestId(undefined);
        }, 5 * 60 * 1000);

        return () => clearTimeout(timer);
    }, [activeRequestId, close, t]);

    const startRecording = async () => {
        setPickedFile(null);
        setRecordedBlob(null);

        const stream = await navigator.mediaDevices.getUserMedia({audio: true});
        const recorder = new MediaRecorder(stream);
        mediaRecorderRef.current = recorder;
        chunksRef.current = [];

        recorder.ondataavailable = (e) => {
            if (e.data && e.data.size > 0) chunksRef.current.push(e.data);
        };

        recorder.onstop = () => {
            stream.getTracks().forEach((t) => t.stop());
            const blob = new Blob(chunksRef.current, {type: recorder.mimeType || "audio/webm"});
            setRecordedBlob(blob);
            setIsRecording(false);
        };

        recorder.start();
        setIsRecording(true);
    };

    const stopRecording = () => {
        try {
            mediaRecorderRef.current?.stop();
        } catch {
            setIsRecording(false);
        }
    };

    const onPickFile = (file: File | null) => {
        setRecordedBlob(null);
        setPickedFile(file);
    };

    const send = async () => {
            const hasAudio = !!pickedFile || !!recordedBlob;
            if (!hasAudio) {
                setStatus("FAILED");
                setStatusText(t("records.needAudio"));
                return;
            }

            setStatus("UPLOADING");
            setStatusText(t("records.uploading"));

            let response = await analysisClient.createAnalysisRequest({
                sourceType: source,
                audioFile: pickedFile ?? new File([recordedBlob!], "record.webm", {type: recordedBlob!.type})
            });

            setStatus(response.status);
            setStatusText(t(`records.${response.status.toLowerCase()}`));

            close();

            setActiveRequestId(response.requestId);
        };

    return (
        <PageCard
            title={t("records.title")}
            subtitle={t("records.subtitle")}
        >
            <Stack spacing={2.5}>
                <Alert severity="info">
                    {t("records.tip")}
                </Alert>

                <Alert severity="warning">
                    {t("records.usageWarning")}
                </Alert>

                <Stack direction={{xs: "column", sm: "row"}} spacing={1.5} alignItems={{sm: "center"}}>
                    <FormControl fullWidth>
                        <InputLabel id="source-label">{t("records.source")}</InputLabel>
                        <Select
                            labelId="source-label"
                            label={t("records.source")}
                            value={source}
                            onChange={(e) => setSource(e.target.value as SourceType)}
                        >
                            <MenuItem value="MICROPHONE">{t("records.sourceMic")}</MenuItem>
                            <MenuItem value="STETHOSCOPE">{t("records.sourceStetho")}</MenuItem>
                        </Select>
                    </FormControl>

                    <Stack direction="row" spacing={1} sx={{width: {xs: "100%", sm: "auto"}}}>
                        {!isRecording ? (
                            <Button
                                fullWidth
                                variant="contained"
                                startIcon={<MicIcon/>}
                                onClick={startRecording}
                                disabled={source !== "MICROPHONE"}
                            >
                                {t("records.record")}
                            </Button>
                        ) : (
                            <Button
                                fullWidth
                                variant="contained"
                                startIcon={<StopIcon/>}
                                onClick={stopRecording}
                                disabled={source !== "MICROPHONE"}
                            >
                                {t("records.stop")}
                            </Button>
                        )}
                    </Stack>
                </Stack>

                <Divider/>

                <Stack spacing={1}>
                    <Typography variant="subtitle1" sx={{fontWeight: 800}}>
                        {t("records.uploadTitle")}
                    </Typography>

                    <Button
                        component="label"
                        variant="outlined"
                        startIcon={<UploadFileIcon/>}
                    >
                        {t("records.chooseFile")}
                        <input
                            hidden
                            type="file"
                            accept="audio/*"
                            onChange={(e) => onPickFile(e.target.files?.[0] ?? null)}
                        />
                    </Button>

                    <Typography variant="body2" color="text.secondary">
                        {selectedBlobInfo}
                    </Typography>
                </Stack>

                <Divider/>

                <Stack direction={{xs: "column", sm: "row"}} spacing={1.5} alignItems={{sm: "center"}}>
                    <Button
                        variant="contained"
                        startIcon={<SendIcon/>}
                        onClick={send}
                        disabled={status === "UPLOADING" || status === "PROCESSING"}
                    >
                        {t("records.send")}
                    </Button>

                    <Stack direction="row" spacing={1} alignItems="center">
                        <StatusChip status={status}/>
                        <Typography variant="body2" color="text.secondary">
                            {statusText}
                        </Typography>
                    </Stack>

                    <Box sx={{flex: 1}}/>

                    {(status === "UPLOADING" || status === "PROCESSING") && (
                        <Box sx={{width: {xs: "100%", sm: 220}}}>
                            <LinearProgress/>
                        </Box>
                    )}
                </Stack>
            </Stack>
        </PageCard>
    );
}