import { Building2, CalendarDays, Heart, LayoutDashboard, LogOut, Menu, UserRound, X } from "lucide-react";
import { useState } from "react";
import { Link, NavLink, Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

export default function Layout() {
  const [open, setOpen] = useState(false);
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const signOut = async () => {
    await logout();
    navigate("/");
  };
  return (
    <>
      <header className="site-header">
        <Link className="brand" to="/">
          <span className="brand-mark">
            <Building2 />
          </span>
          PHOME
        </Link>
        <button
          className="menu-button"
          onClick={() => setOpen(!open)}
          aria-label="Mở menu"
        >
          {open ? <X /> : <Menu />}
        </button>
        <nav
          className={open ? "nav open" : "nav"}
          onClick={() => setOpen(false)}
        >
          <NavLink to="/">Khám phá phòng</NavLink>
          {user ? (
            <>
              {user.role === "Landlord" && <>
                <NavLink to="/my-rooms"><LayoutDashboard size={17} /> Phòng của tôi</NavLink>
                <NavLink to="/appointments"><CalendarDays size={17} /> Lịch hẹn</NavLink>
              </>}
              {user.role === "Tenant" && <>
                <NavLink to="/favorites"><Heart size={17} /> Yêu thích</NavLink>
                <NavLink to="/appointments"><CalendarDays size={17} /> Lịch đã đặt</NavLink>
              </>}
              {user.role === "Admin" && <NavLink to="/admin"><LayoutDashboard size={17} /> Quản trị</NavLink>}
              <NavLink to="/account">
                <UserRound size={17} /> {user.username}
              </NavLink>
              <button className="nav-logout" onClick={signOut}>
                <LogOut size={17} /> Đăng xuất
              </button>
            </>
          ) : (
            <>
              <NavLink to="/login">Đăng nhập</NavLink>
              <NavLink className="button button-small" to="/register">
                Đăng phòng
              </NavLink>
            </>
          )}
        </nav>
      </header>
      <main>
        <Outlet />
      </main>
      <footer>
        <div className="footer-brand">
          <Building2 /> PHOME
        </div>
        <p>Không gian phù hợp cho một khởi đầu tốt đẹp.</p>
        <span>© 2026 PHOME</span>
      </footer>
    </>
  );
}
