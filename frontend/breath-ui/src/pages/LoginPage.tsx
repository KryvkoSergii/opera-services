import {useMemo, useState} from "react";
import {Alert, Button, Stack, TextField, Divider, Link} from "@mui/material";
import {useLocation, useNavigate, Link as RouterLink} from "react-router-dom";
import type {AxiosError} from "axios";
import {setToken} from "../auth/token";
import {useTranslation} from "react-i18next";
import {AuthClient, type UserLoginRequest} from "../api/auth.client";
import {PageCard} from "../components/PageCard";

type ErrorResponse = { message?: string };
const authClient = new AuthClient({baseURL: import.meta.env.VITE_API_BASE_URL});

function getErrorMessage(err: unknown, t): string {
    const e = err as AxiosError<ErrorResponse>;
    const status = e?.response?.status;

    if (status === 401 || status === 403) return t("login.error");
    return e?.response?.data?.message || e?.message || t("login.retry");
}

export default function LoginPage() {
    const {t} = useTranslation();
    const nav = useNavigate();
    const location = useLocation();

    const from = useMemo(() => {
        const st = location.state as any;
        return st?.from?.pathname ?? "/records";
    }, [location.state]);

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);

    const onSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);
        setLoading(true);

        try {
            const resp = await authClient.loginUser({email, password});

            const token = (resp as any)?.token ?? resp;
            if (!token) throw new Error("Token is missing in response");

            setToken(token);
            nav(from, {replace: true});
        } catch (err) {
            setError(getErrorMessage(err, t));
        } finally {
            setLoading(false);
        }
    };

    return (
        <Stack sx={{maxWidth: 520, mx: "auto", mt: {xs: 2, sm: 5}}} spacing={2}>
            <PageCard title={t("login.title")} subtitle={t("login.subtitle")}>
                <Stack spacing={2} component="form" onSubmit={onSubmit}>

                    {error && <Alert severity="error">{error}</Alert>}

                    <TextField
                        label={t("login.email")}
                        type="email"
                        value={email}
                        autoComplete="email"
                        onChange={(e) => setEmail(e.target.value)}
                    />
                    <TextField
                        label={t("login.password")}
                        type="password"
                        value={password}
                        autoComplete="current-password"
                        onChange={(e) => setPassword(e.target.value)}
                    />

                    <Button type="submit" variant="contained" disabled={loading}>
                        {loading ? t("login.submit") : t("login.submit")}{}
                    </Button>

                    <Divider/>

                    <Stack direction="row" spacing={1} justifyContent="center">
                        <Link component={RouterLink} to="/register">
                            {t("login.create")}
                        </Link>
                    </Stack>

                </Stack>
            </PageCard>
        </Stack>
    );
    //
    // return (
    //     <form onSubmit={onSubmit}>
    //         <Stack spacing={2} sx={{maxWidth: 420}}>
    //             {error && <Alert severity="error">{error}</Alert>}
    //
    //             <TextField
    //                 label="Email"
    //                 value={email}
    //                 onChange={(e) => setEmail(e.target.value)}
    //                 autoComplete="email"
    //             />
    //             <TextField
    //                 label="Пароль"
    //                 type="password"
    //                 value={password}
    //                 onChange={(e) => setPassword(e.target.value)}
    //                 autoComplete="current-password"
    //             />
    //
    //             <Button type="submit" variant="contained" disabled={loading}>
    //                 {loading ? "Вхід..." : "Увійти"}
    //             </Button>
    //         </Stack>
    //     </form>
    // );
}
