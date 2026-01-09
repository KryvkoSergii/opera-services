import { PropsWithChildren } from "react";
import { Paper, Stack, Typography } from "@mui/material";

export function PageCard(props: PropsWithChildren<{ title: string; subtitle?: string }>) {
    return (
        <Paper sx={{ p: { xs: 2.5, sm: 3 } }}>
            <Stack spacing={2}>
                <Stack spacing={0.5}>
                    <Typography variant="h5">{props.title}</Typography>
                    {props.subtitle && (
                        <Typography variant="body2" color="text.secondary">
                            {props.subtitle}
                        </Typography>
                    )}
                </Stack>
                {props.children}
            </Stack>
        </Paper>
    );
}
