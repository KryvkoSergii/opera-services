import React from "react";
import { Navigate, useLocation } from "react-router-dom";
import { getToken, isTokenValid, clearToken } from "./token";

type Props = {
    children: React.ReactElement;
};

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
