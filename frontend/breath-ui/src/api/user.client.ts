import axios, { AxiosError, AxiosInstance } from "axios";

/** ===== Types from spec ===== */

export type Gender = "male" | "female"; // from ./general.yaml#/components/schemas/Gender (як у тебе в типах)

export interface UserRegisterRequest {
    /** Desired email for the new account */
    email: string;
    /** Desired password for the new account */
    password: string;
    gender?: Gender;
}

export interface UserRegisterResponse {
    /** Unique identifier for the user */
    userId?: string;
    /** email of the account */
    email?: string;
    gender?: Gender;
    /** Timestamp when the user was registered */
    registeredAt?: string; // date-time
}

/**
 * General error shape (from ./general.yaml)
 * Adjust if your general.yaml differs.
 */
export interface ErrorResponse {
    message?: string;
}

/** ===== Client ===== */

export interface UserClientConfig {
    baseURL: string;                 // e.g. import.meta.env.VITE_API_BASE_URL
    axiosInstance?: AxiosInstance;   // optionally reuse shared axios
    /**
     * Optional token getter.
     * If not provided, client tries localStorage.getItem("token")
     */
    getToken?: () => string | null;
}

export class UserClient {
    private readonly http: AxiosInstance;
    private readonly getToken: () => string | null;

    constructor(cfg: UserClientConfig) {
        this.http =
            cfg.axiosInstance ??
            axios.create({
                baseURL: cfg.baseURL,
            });

        this.getToken = cfg.getToken ?? (() => localStorage.getItem("token"));

        // If you pass your own axiosInstance with interceptors — you can remove this part.
        if (!cfg.axiosInstance) {
            this.http.interceptors.request.use((config) => {
                const token = this.getToken();
                if (token) config.headers.Authorization = `Bearer ${token}`;

                config.headers["X-Timezone"] =
                    Intl.DateTimeFormat().resolvedOptions().timeZone || "UTC";

                return config;
            });
        }
    }

    /**
     * POST /v1/users/register
     * operationId: registerUser
     * Note: spec has bearerAuth here too.
     */
    async registerUser(body: UserRegisterRequest): Promise<UserRegisterResponse> {
        try {
            const res = await this.http.post<UserRegisterResponse>("/v1/users/register", body, {
                headers: { "Content-Type": "application/json" },
            });
            return res.data;
        } catch (e) {
            throw toApiError(e);
        }
    }

    /**
     * GET /v1/users/me
     * operationId: getMeUserDetails
     */
    async getMeUserDetails(): Promise<UserRegisterResponse> {
        try {
            const res = await this.http.get<UserRegisterResponse>("/v1/users/me");
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

export const userClient = new UserClient({
    baseURL: import.meta.env.VITE_API_BASE_URL ?? "",
});
