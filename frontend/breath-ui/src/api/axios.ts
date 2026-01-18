import axios from "axios";

export const apiAxios = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL ?? "",
});

apiAxios.interceptors.request.use((config) => {
    const token = localStorage.getItem("token");
    if (token) config.headers.Authorization = `Bearer ${token}`;

    config.headers["X-Timezone"] =
        Intl.DateTimeFormat().resolvedOptions().timeZone || "UTC";

    return config;
});