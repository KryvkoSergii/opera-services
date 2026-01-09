import { Chip } from "@mui/material";

export type JobStatus = "NEW" | "UPLOADING" | "PROCESSING" | "DONE" | "FAILED";

export function StatusChip({ status }: { status: JobStatus }) {
    const color =
        status === "DONE" ? "success" :
            status === "FAILED" ? "error" :
                status === "PROCESSING" ? "warning" :
                    status === "UPLOADING" ? "info" :
                        "default";

    return (
        <Chip
            size="small"
            label={status}
            color={color as any}
            variant={color === "default" ? "outlined" : "filled"}
        />
    );
}
