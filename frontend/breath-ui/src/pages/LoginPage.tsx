import { useNavigate, Link as RouterLink } from "react-router-dom";
import { Button, Divider, Link, Stack, TextField } from "@mui/material";
import { useForm } from "react-hook-form";
import { PageCard } from "../components/PageCard";

type LoginForm = { email: string; password: string };

export function LoginPage() {
    const nav = useNavigate();
    const { register, handleSubmit, formState: { isSubmitting } } = useForm<LoginForm>({
        defaultValues: { email: "", password: "" },
    });

    const onSubmit = async (_: LoginForm) => {
        // TODO: call auth API
        nav("/records");
    };

    return (
        <Stack sx={{ maxWidth: 520, mx: "auto", mt: { xs: 2, sm: 5 } }} spacing={2}>
            <PageCard title="Login" subtitle="Sign in to record and review analyses">
                <Stack spacing={2} component="form" onSubmit={handleSubmit(onSubmit)}>
                    <TextField
                        label="Email"
                        type="email"
                        autoComplete="email"
                        {...register("email", { required: true })}
                    />
                    <TextField
                        label="Password"
                        type="password"
                        autoComplete="current-password"
                        {...register("password", { required: true })}
                    />

                    <Button type="submit" variant="contained" disabled={isSubmitting}>
                        Sign in
                    </Button>

                    <Divider />

                    <Stack direction="row" spacing={1} justifyContent="center">
                        <Link component={RouterLink} to="/register">
                            Create account
                        </Link>
                    </Stack>
                </Stack>
            </PageCard>
        </Stack>
    );
}