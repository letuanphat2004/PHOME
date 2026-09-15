import { ArrowRight, KeyRound } from "lucide-react";
import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { errorMessage } from "../api/client";
import { useAuth } from "../auth/AuthContext";
import { landingPageFor } from "../auth/RoleRoute";

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [form, setForm] = useState({ username: "", password: "" });
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);
  const submit = async (e) => {
    e.preventDefault();
    setBusy(true);
    setError("");
    try {
      const authenticatedUser = await login(form);
      navigate(location.state?.from || landingPageFor(authenticatedUser.role), {
        replace: true,
      });
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setBusy(false);
    }
  };
  return (
    <div className="auth-page">
      <section className="auth-intro">
        <span className="eyebrow">Chào mừng trở lại</span>
        <h1>
          Tiếp tục hành trình tìm <em>một nơi thuộc về.</em>
        </h1>
        <p>
          Đăng nhập để lưu phòng yêu thích, đặt lịch xem và trò chuyện cùng chủ
          nhà.
        </p>
      </section>
      <section className="auth-card">
        <div className="auth-icon">
          <KeyRound />
        </div>
        <h2>Đăng nhập</h2>
        <p>Nhập thông tin tài khoản PHOME của bạn.</p>
        <form onSubmit={submit}>
          <label>
            Tên đăng nhập
            <input
              autoFocus
              value={form.username}
              onChange={(e) => setForm({ ...form, username: e.target.value })}
              required
            />
          </label>
          <label>
            Mật khẩu
            <input
              type="password"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
              required
            />
          </label>
          <Link className="forgot-link" to="/forgot-password">
            Quên mật khẩu?
          </Link>
          {error && <p className="form-error">{error}</p>}
          <button className="button" disabled={busy}>
            {busy ? (
              "Đang đăng nhập…"
            ) : (
              <>
                Đăng nhập <ArrowRight />
              </>
            )}
          </button>
        </form>
        <p className="auth-switch">
          Chưa có tài khoản? <Link to="/register">Đăng ký miễn phí</Link>
        </p>
      </section>
    </div>
  );
}
