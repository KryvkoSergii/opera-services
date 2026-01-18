import axios, { AxiosInstance, AxiosError } from "axios";

export interface UserLoginRequest {
    email: string;
    password: string;
}

export interface TokenResponse {
    token?: string;
}

/**
 * General error shape (from ./general.yaml typically)
 * If your general.yaml differs, adjust this.
 */
export interface ErrorResponse {
    message?: string;
}

export interface AuthClientConfig {
    baseURL: string; // e.g. import.meta.env.VITE_API_BASE_URL
    axiosInstance?: AxiosInstance; // optionally reuse your shared axios
}

export class AuthClient {
    private readonly http: AxiosInstance;

    constructor(cfg: AuthClientConfig) {
        this.http =
            cfg.axiosInstance ??
            axios.create({
                baseURL: cfg.baseURL,
            });
    }

    /**
     * POST /v1/auth/login
     * operationId: loginUser
     */
    async loginUser(body: UserLoginRequest): Promise<TokenResponse> {
        try {
            const res = await this.http.post<TokenResponse>("/v1/auth/login", body, {
                headers: { "Content-Type": "application/json" },
            });
            return res.data;
        } catch (e) {
            throw toApiError(e);
        }
    }
}

/** ===== Error mapping ===== */

function toApiError(e: unknown): Error {
    if (!axios.isAxiosError(e)) return e instanceof Error ? e : new Error("Unknown error");

    const axErr = e as AxiosError<ErrorResponse>;
    const status = axErr.response?.status;

    // Prefer backend message if present
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

export const authClient = new AuthClient({
    baseURL: import.meta.env.VITE_API_BASE_URL ?? "",
});