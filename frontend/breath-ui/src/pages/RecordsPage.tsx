import { useEffect, useMemo, useRef, useState } from "react";
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
import { PageCard } from "../components/PageCard";
import { JobStatus, StatusChip } from "../components/StatusChip";

type SourceType = "MICROPHONE" | "STETHOSCOPE";

export function RecordsPage() {
    const [source, setSource] = useState<SourceType>("MICROPHONE");

    // recording state
    const [isRecording, setIsRecording] = useState(false);
    const mediaRecorderRef = useRef<MediaRecorder | null>(null);
    const chunksRef = useRef<BlobPart[]>([]);
    const [recordedBlob, setRecordedBlob] = useState<Blob | null>(null);

    // file upload state
    const [pickedFile, setPickedFile] = useState<File | null>(null);

    // job status
    const [status, setStatus] = useState<JobStatus>("NEW");
    const [statusText, setStatusText] = useState<string>("");

    const selectedBlobInfo = useMemo(() => {
        if (pickedFile) return `File: ${pickedFile.name} (${Math.round(pickedFile.size / 1024)} KB)`;
        if (recordedBlob) return `Recorded audio (${Math.round(recordedBlob.size / 1024)} KB)`;
        return "No audio selected yet.";
    }, [pickedFile, recordedBlob]);

    useEffect(() => {
        return () => {
            // stop recorder on unmount
            try {
                mediaRecorderRef.current?.stop();
            } catch {}
        };
    }, []);

    const startRecording = async () => {
        setPickedFile(null);
        setRecordedBlob(null);

        const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
        const recorder = new MediaRecorder(stream);
        mediaRecorderRef.current = recorder;
        chunksRef.current = [];

        recorder.ondataavailable = (e) => {
            if (e.data && e.data.size > 0) chunksRef.current.push(e.data);
        };

        recorder.onstop = () => {
            // stop tracks
            stream.getTracks().forEach((t) => t.stop());
            const blob = new Blob(chunksRef.current, { type: recorder.mimeType || "audio/webm" });
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
            setStatusText("Please record audio or select a file before sending.");
            return;
        }

        setStatus("UPLOADING");
        setStatusText("Uploading...");

        // TODO: build multipart and call backend
        // const form = new FormData();
        // form.append("source", source);
        // form.append("file", pickedFile ?? new File([recordedBlob!], "record.webm", { type: recordedBlob!.type }));

        await new Promise((r) => setTimeout(r, 800));

        setStatus("PROCESSING");
        setStatusText("Processing...");

        // TODO: poll/SSE status updates
        await new Promise((r) => setTimeout(r, 1200));

        setStatus("DONE");
        setStatusText("Completed.");
    };

    return (
        <PageCard
            title="Records"
            subtitle="Record from microphone or upload a file, then send for analysis"
        >
            <Stack spacing={2.5}>
                <Alert severity="info">
                    Tip: On mobile, grant microphone permission when prompted.
                </Alert>

                <Stack direction={{ xs: "column", sm: "row" }} spacing={1.5} alignItems={{ sm: "center" }}>
                    <FormControl fullWidth>
                        <InputLabel id="source-label">Source</InputLabel>
                        <Select
                            labelId="source-label"
                            label="Source"
                            value={source}
                            onChange={(e) => setSource(e.target.value as SourceType)}
                        >
                            <MenuItem value="MICROPHONE">Microphone</MenuItem>
                            <MenuItem value="STETHOSCOPE">Stethoscope</MenuItem>
                        </Select>
                    </FormControl>

                    <Stack direction="row" spacing={1} sx={{ width: { xs: "100%", sm: "auto" } }}>
                        {!isRecording ? (
                            <Button
                                fullWidth
                                variant="contained"
                                startIcon={<MicIcon />}
                                onClick={startRecording}
                            >
                                Записати
                            </Button>
                        ) : (
                            <Button
                                fullWidth
                                variant="contained"
                                startIcon={<StopIcon />}
                                onClick={stopRecording}
                            >
                                Stop
                            </Button>
                        )}
                    </Stack>
                </Stack>

                <Divider />

                <Stack spacing={1}>
                    <Typography variant="subtitle1" sx={{ fontWeight: 800 }}>
                        Upload file
                    </Typography>

                    <Button
                        component="label"
                        variant="outlined"
                        startIcon={<UploadFileIcon />}
                    >
                        Choose file
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

                <Divider />

                <Stack direction={{ xs: "column", sm: "row" }} spacing={1.5} alignItems={{ sm: "center" }}>
                    <Button
                        variant="contained"
                        startIcon={<SendIcon />}
                        onClick={send}
                        disabled={status === "UPLOADING" || status === "PROCESSING"}
                    >
                        Відправити
                    </Button>

                    <Stack direction="row" spacing={1} alignItems="center">
                        <StatusChip status={status} />
                        <Typography variant="body2" color="text.secondary">
                            {statusText}
                        </Typography>
                    </Stack>

                    <Box sx={{ flex: 1 }} />

                    {(status === "UPLOADING" || status === "PROCESSING") && (
                        <Box sx={{ width: { xs: "100%", sm: 220 } }}>
                            <LinearProgress />
                        </Box>
                    )}
                </Stack>
            </Stack>
        </PageCard>
    );
}