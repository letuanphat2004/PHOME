import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "./AuthContext";

export function landingPageFor(role) {
  if (role === "Admin") return "/admin";
  if (role === "Landlord") return "/landlord";
  return "/";
}

export default function RoleRoute({ roles, children }) {
  const { user, isLoading } = useAuth();
  const location = useLocation();

  if (isLoading) return <div className="page-state">Đang kiểm tra tài khoản…</div>;
  if (!user) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />;
  }
  if (roles && !roles.includes(user.role)) {
    return <Navigate to={landingPageFor(user.role)} replace />;
  }
  return children;
}
