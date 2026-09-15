import {
  Building2,
  CalendarDays,
  ChevronRight,
  ShieldCheck,
  UserRound,
} from "lucide-react";
import { Navigate } from "react-router-dom";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api, errorMessage } from "../api/client";
import { useAuth } from "../auth/AuthContext";

export default function AccountPage() {
  const { user, isLoading } = useAuth();
  const queryClient = useQueryClient();
  const { data: profile } = useQuery({
    queryKey: ["profile"],
    queryFn: () => api.get("/profile").then((r) => r.data),
    enabled: !!user,
  });
  const update = useMutation({
    mutationFn: (body) => api.put("/profile", body),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["profile"] }),
  });
  const avatar = useMutation({
    mutationFn: (form) => api.post("/profile/avatar", form),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["profile"] }),
  });
  const password = useMutation({
    mutationFn: (body) => api.put("/profile/password", body),
  });

  if (isLoading) return <div className="page-state">Đang tải tài khoản…</div>;
  if (!user) return <Navigate to="/login" replace />;
  const items =
    user.role === "Landlord"
      ? [
          { icon: Building2, title: "Phòng của tôi", href: "/my-rooms" },
          {
            icon: CalendarDays,
            title: "Lịch hẹn xem phòng",
            href: "/appointments",
          },
        ]
      : user.role === "Tenant"
        ? [
            {
              icon: CalendarDays,
              title: "Lịch xem phòng của tôi",
              href: "/appointments",
            },
          ]
        : [];
  if (user.role === "Admin")
    items.push({ icon: ShieldCheck, title: "Trang quản trị", href: "/admin" });

  const uploadAvatar = (event) => {
    const form = new FormData();
    form.append("file", event.target.files[0]);
    avatar.mutate(form);
  };
  return (
    <div className="account-page">
      <div className="account-heading">
        <label className="avatar-upload" title="Đổi ảnh đại diện">
          {profile?.avatar ? (
            <img className="account-avatar" src={profile.avatar} alt="" />
          ) : (
            <div className="avatar-fallback">
              <UserRound />
            </div>
          )}
          <input type="file" accept="image/*" onChange={uploadAvatar} />
        </label>
        <div>
          <span className="eyebrow">Tài khoản của tôi</span>
          <h1>{profile?.fullname || user.username}</h1>
          <p>
            @{user.username} · {user.role}
          </p>
        </div>
      </div>
      {profile && (
        <form
          className="profile-form"
          onSubmit={(event) => {
            event.preventDefault();
            update.mutate(
              Object.fromEntries(new FormData(event.currentTarget)),
            );
          }}
        >
          <label>
            Họ và tên
            <input name="fullname" defaultValue={profile.fullname} required />
          </label>
          <label>
            Số điện thoại
            <input name="tel" defaultValue={profile.tel} required />
          </label>
          <label>
            Email
            <input value={profile.email} disabled readOnly />
          </label>
          <button className="button" disabled={update.isPending}>
            {update.isSuccess ? "Đã lưu" : "Lưu thông tin"}
          </button>
        </form>
      )}
      <form
        className="profile-form password-form"
        onSubmit={(event) => {
          event.preventDefault();
          password.mutate(
            Object.fromEntries(new FormData(event.currentTarget)),
          );
          event.currentTarget.reset();
        }}
      >
        <label>
          Mật khẩu hiện tại
          <input name="currentPassword" type="password" required />
        </label>
        <label>
          Mật khẩu mới
          <input name="newPassword" type="password" minLength="8" required />
        </label>
        <button className="button" disabled={password.isPending}>
          {password.isSuccess ? "Đã đổi mật khẩu" : "Đổi mật khẩu"}
        </button>
        {password.isError && (
          <p className="form-error">{errorMessage(password.error)}</p>
        )}
      </form>
      <section className="account-list">
        {items.map(({ icon: Icon, title, href }) => (
          <a href={href} key={title}>
            <Icon />
            <span>{title}</span>
            <ChevronRight />
          </a>
        ))}
      </section>
    </div>
  );
}
