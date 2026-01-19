import axios from "axios";
import type { AxiosInstance, AxiosError } from "axios";
import { getToken } from "../auth/token";

/** ===== Types (based on your spec) ===== */

export type SourceType = "microphone" | "stethoscope";
export type RequestStatus = "NEW" | "UPLOADING" | "UPLOADED" | "PROCESSING" | "DONE" | "FAILED";
export type ProbabilityStatus = "high" | "moderate" | "low";

export interface AnalysisCreateResponse {
    requestId: string;
    status: RequestStatus;
    requested: string; // date-time
}

export interface ModelDetail {
    disease?: string;
    details: string;
    status: ProbabilityStatus;
    probability?: number; // 0..1
}

export interface HistoryItem {
    requestId: string;
    requested: string; // date-time
    type: SourceType;
    recommendation?: string;
    status: RequestStatus;
    details: ModelDetail[];
}

export interface PaginatedRequestHistory {
    total: number;
    page: number;
    perPage: number;
    items: HistoryItem[];
}

export interface ErrorResponse {
    message?: string;
}

/** ===== Client ===== */

export interface AnalysisClientConfig {
    baseURL: string;                // e.g. import.meta.env.VITE_API_BASE_URL
    axiosInstance?: AxiosInstance;  // optionally reuse shared axios
    getToken?: () => string | null; // default: localStorage.getItem("token")
    getTimezone?: () => string;     // default: browser timezone
}

export class AnalysisClient {
    private readonly http: AxiosInstance;
    private readonly getToken: () => string | null;
    private readonly getTimezone: () => string;

    constructor(cfg: AnalysisClientConfig) {
        this.http =
            cfg.axiosInstance ??
            axios.create({
                baseURL: cfg.baseURL,
            });

        this.getTimezone =
            cfg.getTimezone ??
            (() => Intl.DateTimeFormat().resolvedOptions().timeZone || "UTC");

        // If you pass your own axiosInstance with interceptors — you can remove this part.
        if (!cfg.axiosInstance) {
            this.http.interceptors.request.use((config) => {
                const token = getToken();
                if (token) config.headers.Authorization = `Bearer ${token}`;
                config.headers["X-Timezone"] = this.getTimezone();
                return config;
            });
        }
    }

    /**
     * POST /v1/analyses?source-type=...
     * multipart/form-data: audio-file=<File>
     */
    async createAnalysisRequest(input: {
        sourceType: SourceType;
        audioFile: File;
    }): Promise<AnalysisCreateResponse> {
        try {
            const form = new FormData();
            form.append("audio-file", input.audioFile);

            const res = await this.http.post<AnalysisCreateResponse>(
                "/v1/analyses",
                form,
                {
                    params: { "source-type": input.sourceType },
                    // axios сам поставить multipart boundary; Content-Type вручну не треба
                }
            );

            return res.data;
        } catch (e) {
            throw toApiError(e);
        }
    }

    /**
     * GET /v1/analyses?page=&perPage=
     */
    async listAnalysisRequests(query?: {
        page?: number;
        perPage?: number;
    }): Promise<PaginatedRequestHistory> {
        try {
            const res = await this.http.get<PaginatedRequestHistory>("/v1/analyses", {
                params: {
                    page: query?.page,
                    perPage: query?.perPage,
                },
            });
            return res.data;
        } catch (e) {
            throw toApiError(e);
        }
    }

    /**
     * SSE stream helper (NOT axios).
     *
     * Why not axios?
     *  - SSE is a long-lived streaming response; axios is not great here in browser.
     *  - EventSource cannot send Authorization header.
     *
     * This uses fetch streaming + Authorization header.
     *
     * onEvent receives raw "event:" name (or "message") and parsed JSON (if possible).
     * Returns an AbortController you can abort() to stop streaming.
     */
    streamRequestEvents(
        requestId: string,
        onEvent: (evt: { event: string; data: unknown; rawData: string }) => void,
        onError?: (err: unknown) => void
    ): AbortController {
        const controller = new AbortController();

        const token = this.getToken();
        const tz = this.getTimezone();

        const url = new URL(
            `/v1/analyses/${encodeURIComponent(requestId)}/events`,
            this.http.defaults.baseURL || window.location.origin
        );

        // Header X-Timezone exists in spec. With fetch we can send it.
        // If your backend also accepts timezone via header only, this is correct.

        fetch(url.toString(), {
            method: "GET",
            signal: controller.signal,
            headers: {
                ...(token ? { Authorization: `Bearer ${token}` } : {}),
                "X-Timezone": tz,
                Accept: "text/event-stream",
            },
        })
            .then(async (res) => {
                if (!res.ok) {
                    let msg = `HTTP ${res.status}`;
                    try {
                        const body = (await res.json()) as ErrorResponse;
                        msg = body?.message ?? msg;
                    } catch {}
                    throw new Error(msg);
                }

                const reader = res.body?.getReader();
                if (!reader) throw new Error("SSE: response body is empty");

                const decoder = new TextDecoder("utf-8");
                let buffer = "";

                while (true) {
                    const { value, done } = await reader.read();
                    if (done) break;

                    buffer += decoder.decode(value, { stream: true });

                    // SSE events separated by blank line
                    let idx;
                    while ((idx = buffer.indexOf("\n\n")) !== -1) {
                        const chunk = buffer.slice(0, idx);
                        buffer = buffer.slice(idx + 2);

                        const parsed = parseSseChunk(chunk);
                        if (!parsed) continue;

                        const rawData = parsed.data ?? "";
                        let data: unknown = rawData;

                        // Try JSON parse (your example is JSON)
                        if (rawData) {
                            try {
                                data = JSON.parse(rawData);
                            } catch {
                                // keep string
                            }
                        }

                        onEvent({ event: parsed.event ?? "message", data, rawData });
                    }
                }
            })
            .catch((err) => {
                if (controller.signal.aborted) return;
                onError?.(err);
            });

        return controller;
    }
}

/** ===== SSE parsing ===== */

function parseSseChunk(chunk: string): { event?: string; data?: string } | null {
    // Lines: event: name, data: ....
    // We only need event + concatenated data lines
    const lines = chunk.split("\n").map((l) => l.trimEnd());
    let event: string | undefined;
    const dataLines: string[] = [];

    for (const line of lines) {
        if (line.startsWith("event:")) {
            event = line.slice("event:".length).trim();
        } else if (line.startsWith("data:")) {
            dataLines.push(line.slice("data:".length).trim());
        }
    }

    if (!event && dataLines.length === 0) return null;
    return { event, data: dataLines.join("\n") };
}

/** ===== Error mapping ===== */

function toApiError(e: unknown): Error {
    if (!axios.isAxiosError(e)) return e instanceof Error ? e : new Error("Unknown error");

    const axErr = e as AxiosError<ErrorResponse>;
    const status = axErr.response?.status;

    const message =
        axErr.response?.data?.message ??
        axErr.message ??
        (status ? `HTTP ${status}` : "Network error");

    const err = new Error(message);
    // @ts-expect-error attach status for UI if needed
    err.status = status;
    return err;
}

/** ===== Default singleton (optional) ===== */

export const analysisClient = new AnalysisClient({
    baseURL: import.meta.env.VITE_API_BASE_URL ?? "",
});
