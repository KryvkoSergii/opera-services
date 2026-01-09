import { createBrowserRouter, Navigate } from "react-router-dom";
import { AppShell } from "../components/AppShell";
import { LoginPage } from "../pages/LoginPage";
import { RegisterPage } from "../pages/RegisterPage";
import { RecordsPage } from "../pages/RecordsPage";
import { HistoryPage } from "../pages/HistoryPage";

export const router = createBrowserRouter([
    { path: "/login", element: <LoginPage /> },
    { path: "/register", element: <RegisterPage /> },

    {
        path: "/",
        element: <AppShell />,
        children: [
            { index: true, element: <Navigate to="/records" replace /> },
            { path: "records", element: <RecordsPage /> },
            { path: "history", element: <HistoryPage /> },
        ],
    },

    { path: "*", element: <Navigate to="/records" replace /> },
]);
