import { Navigate, Route, Routes } from "react-router-dom";
import Layout from "./components/Layout";
import HomePage from "./pages/HomePage";
import RoomPage from "./pages/RoomPage";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import AccountPage from "./pages/AccountPage";
import ForgotPasswordPage from "./pages/ForgotPasswordPage";
import AppointmentsPage from "./pages/AppointmentsPage";
import LandlordRoomsPage from "./pages/LandlordRoomsPage";
import AdminPage from "./pages/AdminPage";
import RoleRoute from "./auth/RoleRoute";

export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<HomePage />} />
        <Route path="rooms/:id" element={<RoomPage />} />
        <Route path="login" element={<LoginPage />} />
        <Route path="register" element={<RegisterPage />} />
        <Route path="forgot-password" element={<ForgotPasswordPage />} />
        <Route
          path="account"
          element={
            <RoleRoute>
              <AccountPage />
            </RoleRoute>
          }
        />
        <Route
          path="appointments"
          element={
            <RoleRoute roles={["Tenant", "Landlord"]}>
              <AppointmentsPage />
            </RoleRoute>
          }
        />
        <Route
          path="my-rooms"
          element={
            <RoleRoute roles={["Landlord"]}>
              <LandlordRoomsPage />
            </RoleRoute>
          }
        />
        <Route
          path="admin"
          element={
            <RoleRoute roles={["Admin"]}>
              <AdminPage />
            </RoleRoute>
          }
        />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Route>
    </Routes>
  );
}
