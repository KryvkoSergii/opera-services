import React from "react";
import { Navigate, useLocation } from "react-router-dom";
import { getToken, isTokenValid, clearToken } from "./token";

type Props = {
    children: React.ReactElement;
};

/**
 * Route guard: redirects to /login when there is no token.
 * Keeps the original location in state so LoginPage can redirect back.
 */
export function RequireAuth({ children }: Props) {
    const location = useLocation();
    const token = getToken();

    if (!isTokenValid(token)) {
        clearToken();

        return (
            <Navigate
                to="/login"
                replace
                state={{ from: location }}
            />
        );
    }

    return children;
}
