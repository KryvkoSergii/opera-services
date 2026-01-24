import { createBrowserRouter } from "react-router-dom";
import { RequireAuth } from "../auth/RequireAuth";
import { AppShell } from "../components/AppShell";
import { PublicShell } from "../components/PublicShell";

import LoginPage from "../pages/LoginPage";
import RegisterPage from "../pages/RegisterPage";
import RecordsPage from "../pages/RecordsPage";
import HistoryPage from "../pages/HistoryPage";

export const router = createBrowserRouter([
    {
        element: <PublicShell />,
        children: [
            { path: "/login", element: <LoginPage /> },
            { path: "/register", element: <RegisterPage /> },
        ],
    },
    {
        element: (
            <RequireAuth>
                <AppShell />
            </RequireAuth>
        ),
        children: [
            { path: "/", element: <RecordsPage /> },
            { path: "/records", element: <RecordsPage /> },
            { path: "/history", element: <HistoryPage /> },
        ],
    },
]);
