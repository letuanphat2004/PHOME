import { ArrowRight, UserPlus } from "lucide-react";
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { api, errorMessage } from "../api/client";

export default function RegisterPage() {
  const navigate = useNavigate();
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);
  const [form, setForm] = useState({
    username: "",
    password: "",
    email: "",
    fullname: "",
    tel: "",
    role: "TENANT",
  });
  const field = (key) => ({
    value: form[key],
    onChange: (e) => setForm({ ...form, [key]: e.target.value }),
  });
  const submit = async (e) => {
    e.preventDefault();
    setBusy(true);
    setError("");
    try {
      await api.post("/auth/register", form);
      navigate("/login");
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setBusy(false);
    }
  };
  return (
    <div className="auth-page register">
      <section className="auth-intro">
        <span className="eyebrow">Gia nhập PHOME</span>
        <h1>
          Một tài khoản,
          <br />
          <em>nhiều cánh cửa mới.</em>
        </h1>
        <p>
          Tìm căn phòng vừa ý hoặc giới thiệu không gian của bạn đến đúng người
          cần.
        </p>
      </section>
      <section className="auth-card">
        <div className="auth-icon">
          <UserPlus />
        </div>
        <h2>Tạo tài khoản</h2>
        <form onSubmit={submit}>
          <div className="form-row">
            <label>
              Họ và tên
              <input {...field("fullname")} required />
            </label>
            <label>
              Số điện thoại
              <input {...field("tel")} inputMode="numeric" required />
            </label>
          </div>
          <label>
            Email
            <input {...field("email")} type="email" required />
          </label>
          <label>
            Tên đăng nhập
            <input {...field("username")} required />
          </label>
          <label>
            Mật khẩu
            <input
              {...field("password")}
              type="password"
              minLength="8"
              required
            />
            <small>Tối thiểu 8 ký tự, gồm chữ và số.</small>
          </label>
          <fieldset>
            <legend>Bạn muốn</legend>
            <label className="role-option">
              <input
                type="radio"
                name="role"
                value="TENANT"
                checked={form.role === "TENANT"}
                onChange={field("role").onChange}
              />
              <span>Tìm phòng</span>
            </label>
            <label className="role-option">
              <input
                type="radio"
                name="role"
                value="LANDLORD"
                checked={form.role === "LANDLORD"}
                onChange={field("role").onChange}
              />
              <span>Đăng phòng</span>
            </label>
          </fieldset>
          {error && <p className="form-error">{error}</p>}
          <button className="button" disabled={busy}>
            {busy ? (
              "Đang tạo…"
            ) : (
              <>
                Tạo tài khoản <ArrowRight />
              </>
            )}
          </button>
        </form>
        <p className="auth-switch">
          Đã có tài khoản? <Link to="/login">Đăng nhập</Link>
        </p>
      </section>
    </div>
  );
}
