import {Chip} from "@mui/material";

const STATUS_COLOR: Record<JobStatus, "success" | "error" | "warning" | "info" | "default"> = {
    DONE: "success",
    FAILED: "error",
    TIMEOUT: "error",
    PROCESSING: "warning",
    PARTIAL_DONE: "warning",
    UPLOADING: "info",
    NEW: "default",
};

export function StatusChip({ status }: { status: JobStatus }) {
    const color = STATUS_COLOR[status];

    return (
        <Chip
            size="small"
            label={status}
            color={color}
            variant={color === "default" ? "outlined" : "filled"}
        />
    );
}
