const KEY = "token";

export function getToken(): string | null {
    return localStorage.getItem(KEY);
}

export function setToken(token: string) {
    localStorage.setItem(KEY, token);
}

export function clearToken() {
    localStorage.removeItem(KEY);
}

type JwtPayload = {
    exp?: number;
};

export function isTokenValid(token: string | null): boolean {
    if (!token) return false;

    try {
        const [, payloadBase64] = token.split(".");
        if (!payloadBase64) return false;

        const payloadJson = atob(payloadBase64);
        const payload: JwtPayload = JSON.parse(payloadJson);

        if (!payload.exp) return false;

        const nowInSeconds = Math.floor(Date.now() / 1000);
        return payload.exp > nowInSeconds;
    } catch {
        return false;
    }
}