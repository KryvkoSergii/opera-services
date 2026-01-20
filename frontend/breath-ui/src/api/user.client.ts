import axios from "axios";
import type { AxiosInstance, AxiosError, AxiosRequestConfig } from "axios";
import { getToken } from "../auth/token";

/** ===== Types ===== */

export type Gender = "male" | "female";

export interface UserRegisterRequest {
    email: string;
    password: string;
    gender?: Gender;
}

export interface UserRegisterResponse {
    userId?: string;
    email?: string;
    gender?: Gender;
    registeredAt?: string;
}

export interface ErrorResponse {
    message?: string;
}

/** ===== Client ===== */

export interface UserClientConfig {
    baseURL: string;
    axiosInstance?: AxiosInstance;
}

export class UserClient {
    private readonly http: AxiosInstance;

    constructor(cfg: UserClientConfig) {
        this.http =
            cfg.axiosInstance ??
            axios.create({
                baseURL: cfg.baseURL,
            });

        // загальні заголовки (БЕЗ Authorization)
        this.http.interceptors.request.use((config) => {
            config.headers["X-Timezone"] =
                Intl.DateTimeFormat().resolvedOptions().timeZone || "UTC";
            return config;
        });
    }

    /** helper для auth header */
    private withAuth(config: AxiosRequestConfig = {}): AxiosRequestConfig {
        const token = getToken();
        if (!token) return config;

        return {
            ...config,
            headers: {
                ...config.headers,
                Authorization: `Bearer ${token}`,
            },
        };
    }

    /**
     * POST /v1/users/register
     * ❌ БЕЗ Authorization
     */
    async registerUser(
        body: UserRegisterRequest
    ): Promise<UserRegisterResponse> {
        try {
            const res = await this.http.post<UserRegisterResponse>(
                "/v1/users/register",
                body,
                {
                    headers: { "Content-Type": "application/json" },
                }
            );
            return res.data;
        } catch (e) {
            throw toApiError(e);
        }
    }

    /**
     * GET /v1/users/me
     * ✅ З Authorization
     */
    async getMeUserDetails(): Promise<UserRegisterResponse> {
        try {
            const res = await this.http.get<UserRegisterResponse>(
                "/v1/users/me",
                this.withAuth()
            );
            return res.data;
        } catch (e) {
            throw toApiError(e);
        }
    }
}
