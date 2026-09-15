import { KeyRound } from "lucide-react";
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { api, errorMessage } from "../api/client";

export default function ForgotPasswordPage() {
  const navigate = useNavigate();
  const [step, setStep] = useState(1);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const [email, setEmail] = useState("");
  const [form, setForm] = useState({ username: "", otp: "", newPassword: "" });
  const requestCode = async (e) => {
    e.preventDefault();
    setBusy(true);
    setError("");
    try {
      const { data } = await api.post("/auth/password/request", {
        username: form.username,
      });
      setEmail(data.email);
      setStep(2);
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setBusy(false);
    }
  };
  const reset = async (e) => {
    e.preventDefault();
    setBusy(true);
    setError("");
    try {
      await api.post("/auth/password/reset", form);
      navigate("/login");
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setBusy(false);
    }
  };
  return (
    <div className="auth-page">
      <section className="auth-intro">
        <span className="eyebrow">Khôi phục tài khoản</span>
        <h1>
          Lấy lại quyền truy cập <em>một cách an toàn.</em>
        </h1>
        <p>
          Mã xác nhận chỉ có hiệu lực 10 phút và được kiểm tra hoàn toàn tại máy
          chủ.
        </p>
      </section>
      <section className="auth-card">
        <div className="auth-icon">
          <KeyRound />
        </div>
        <h2>Đặt lại mật khẩu</h2>
        {step === 1 ? (
          <form onSubmit={requestCode}>
            <label>
              Tên đăng nhập
              <input
                value={form.username}
                onChange={(e) => setForm({ ...form, username: e.target.value })}
                autoFocus
                required
              />
            </label>
            {error && <p className="form-error">{error}</p>}
            <button className="button" disabled={busy}>
              {busy ? "Đang gửi…" : "Gửi mã xác nhận"}
            </button>
          </form>
        ) : (
          <form onSubmit={reset}>
            <p>
              Mã đã được gửi đến <strong>{email}</strong>.
            </p>
            <label>
              Mã xác nhận
              <input
                value={form.otp}
                onChange={(e) => setForm({ ...form, otp: e.target.value })}
                inputMode="numeric"
                pattern="[0-9]{6}"
                maxLength="6"
                required
              />
            </label>
            <label>
              Mật khẩu mới
              <input
                type="password"
                value={form.newPassword}
                onChange={(e) =>
                  setForm({ ...form, newPassword: e.target.value })
                }
                minLength="8"
                required
              />
              <small>Tối thiểu 8 ký tự, gồm chữ và số.</small>
            </label>
            {error && <p className="form-error">{error}</p>}
            <button className="button" disabled={busy}>
              {busy ? "Đang cập nhật…" : "Cập nhật mật khẩu"}
            </button>
          </form>
        )}
        <p className="auth-switch">
          <Link to="/login">Quay lại đăng nhập</Link>
        </p>
      </section>
    </div>
  );
}
