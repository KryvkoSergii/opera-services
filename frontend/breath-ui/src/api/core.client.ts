import axios from "axios";
import type {AxiosInstance, AxiosError} from "axios";
import {getToken} from "../auth/token";

export type SourceType = "microphone" | "stethoscope";
export type RequestStatus =
    "NEW"
    | "UPLOADING"
    | "UPLOADED"
    | "PROCESSING"
    | "PARTIAL_DONE"
    | "DONE"
    | "FAILED"
    | "TIMEOUT";
export type ProbabilityStatus = "high" | "moderate" | "low";

export interface AnalysisCreateResponse {
    requestId: string;
    status: RequestStatus;
    requested: string;
}

export interface ModelDetail {
    disease?: string;
    details: string;
    status: ProbabilityStatus;
    probability?: number;
}

export interface HistoryItem {
    requestId: string;
    requested: string;
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

export interface StatusEvent {
    requestId: string;
    status: RequestStatus;
    timestamp: string;
}

export interface AnalysisClientConfig {
    baseURL: string;
    axiosInstance?: AxiosInstance;
    getToken?: () => string | null;
    getTimezone?: () => string;
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

        if (!cfg.axiosInstance) {
            this.http.interceptors.request.use((config) => {
                const token = getToken();
                if (token) config.headers.Authorization = `Bearer ${token}`;
                config.headers["X-Timezone"] = this.getTimezone();
                return config;
            });
        }
    }

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
                    params: {"source-type": input.sourceType},
                }
            );

            return res.data;
        } catch (e) {
            throw toApiError(e);
        }
    }

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
}

function toApiError(e: unknown): Error {
    if (!axios.isAxiosError(e)) return e instanceof Error ? e : new Error("Unknown error");

    const axErr = e as AxiosError<ErrorResponse>;
    const status = axErr.response?.status;

    const message =
        axErr.response?.data?.message ??
        axErr.message ??
        (status ? `HTTP ${status}` : "Network error");

    const err = new Error(message);
    err.status = status;
    return err;
}

export const analysisClient = new AnalysisClient({
    baseURL: import.meta.env.VITE_API_BASE_URL ?? "",
});
