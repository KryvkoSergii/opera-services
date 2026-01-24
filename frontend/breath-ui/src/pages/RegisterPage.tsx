import {useNavigate, Link as RouterLink} from "react-router-dom";
import { useEffect } from "react";
import { getToken } from "../auth/token";
import {
    Button,
    Divider,
    FormControl,
    FormControlLabel,
    FormLabel,
    Link,
    Radio,
    RadioGroup,
    Stack,
    TextField,
    Typography,
} from "@mui/material";
import {useForm} from "react-hook-form";
import {PageCard} from "../components/PageCard";
import {UserClient, type UserRegisterRequest} from "../api/user.client";

const userClient = new UserClient({baseURL: import.meta.env.VITE_API_BASE_URL});

type Gender = "male" | "female";
type RegisterForm = {
    login: string;
    password: string;
    passwordRepeat: string;
    gender?: Gender;
};


export default function RegisterPage() {
    const nav = useNavigate();

    useEffect(() => {
        if (getToken()) {
            nav("/records", { replace: true });
        }
    }, [nav]);
    const {register, handleSubmit, watch, formState: {isSubmitting, errors}} = useForm<RegisterForm>({
        defaultValues: {login: "", password: "", passwordRepeat: ""},
    });

    const pwd = watch("password");

    const onSubmit = async (data: RegisterForm) => {
        userClient.registerUser({
            email: data.login,
            password: data.password,
            gender: data.gender
        } as UserRegisterRequest);

        nav("/login");
    };

    return (
        <Stack sx={{maxWidth: 520, mx: "auto", mt: {xs: 2, sm: 5}}} spacing={2}>
            <PageCard title="Register" subtitle="Create a new account">
                <Stack spacing={2} component="form" onSubmit={handleSubmit(onSubmit)}>
                    <TextField
                        label="Login"
                        {...register("login", {required: "Login is required"})}
                        error={!!errors.login}
                        helperText={errors.login?.message}
                    />

                    <TextField
                        label="Password"
                        type="password"
                        {...register("password", {
                            required: "Password is required",
                            minLength: {value: 6, message: "Min 6 chars"}
                        })}
                        error={!!errors.password}
                        helperText={errors.password?.message}
                    />

                    <TextField
                        label="Repeat password"
                        type="password"
                        {...register("passwordRepeat", {
                            required: "Please repeat password",
                            validate: (v) => v === pwd || "Passwords do not match",
                        })}
                        error={!!errors.passwordRepeat}
                        helperText={errors.passwordRepeat?.message}
                    />

                    <FormControl>
                        <FormLabel>Gender (optional)</FormLabel>
                        <RadioGroup row>
                            <FormControlLabel value="MALE" control={<Radio/>} label="Male" {...register("gender")} />
                            <FormControlLabel value="FEMALE" control={<Radio/>}
                                              label="Female" {...register("gender")} />
                        </RadioGroup>
                        <Typography variant="caption" color="text.secondary">
                            This field is optional.
                        </Typography>
                    </FormControl>

                    <Button type="submit" variant="contained" disabled={isSubmitting}>
                        Create account
                    </Button>

                    <Divider/>

                    <Stack direction="row" spacing={1} justifyContent="center">
                        <Link component={RouterLink} to="/login">
                            Back to login
                        </Link>
                    </Stack>
                </Stack>
            </PageCard>
        </Stack>
    );
}