import axios from "axios";
import {getToken} from "@/auth/token";

export const apiAxios = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL ?? "",
});

apiAxios.interceptors.request.use((config) => {
    const token = getToken();
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
}).response.use(
    r => r,
    e => {
        if (e.response?.status === 401) {
            clearToken();
            window.location.href = "/login";
        }
        return Promise.reject(e);
    }
);