import axios from "axios";
import type {AxiosInstance, AxiosError} from "axios";

export interface UserLoginRequest {
    email: string;
    password: string;
}

export interface TokenResponse {
    token?: string;
}

export interface ErrorResponse {
    message?: string;
}

export interface AuthClientConfig {
    baseURL: string;
    axiosInstance?: AxiosInstance;
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

    async loginUser(body: UserLoginRequest): Promise<TokenResponse> {
        try {
            const res = await this.http.post<TokenResponse>("/v1/auth/login", body, {
                headers: {"Content-Type": "application/json"},
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

export const authClient = new AuthClient({
    baseURL: import.meta.env.VITE_API_BASE_URL ?? "",
});