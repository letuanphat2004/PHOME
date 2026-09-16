import { useQuery } from "@tanstack/react-query";
import { Building2, CalendarCheck2, CalendarClock, CheckCircle2, Clock3, RefreshCw } from "lucide-react";
import { Link, Navigate } from "react-router-dom";
import { api, errorMessage } from "../api/client";
import { useAuth } from "../auth/AuthContext";

export default function LandlordDashboardPage() {
  const { user, isLoading: authLoading } = useAuth();
  const dashboard = useQuery({
    queryKey: ["landlord-dashboard"],
    queryFn: () => api.get("/landlord/dashboard").then((response) => response.data),
    enabled: user?.role === "Landlord",
  });

  if (authLoading) return <div className="page-state">Đang tải tài khoản…</div>;
  if (user?.role !== "Landlord") return <Navigate to="/account" replace />;

  return <div className="dashboard-page landlord-overview">
    <div className="dashboard-title row">
      <div>
        <span className="eyebrow">Trung tâm chủ nhà</span>
        <h1>Chào {user.username}</h1>
        <p>Theo dõi phòng đăng và xử lý các yêu cầu xem phòng từ một nơi.</p>
      </div>
      <Link className="button" to="/my-rooms">Quản lý phòng</Link>
    </div>
    {dashboard.isLoading ? <div className="state-card">Đang tổng hợp dữ liệu…</div> : dashboard.isError ?
      <div className="state-card error-state"><p>{errorMessage(dashboard.error)}</p><button className="button secondary" onClick={() => dashboard.refetch()}><RefreshCw /> Thử lại</button></div> : <>
        <section className="overview-grid" aria-label="Tổng quan hoạt động">
          <Stat icon={<Building2 />} value={dashboard.data.approvedRooms} label="Phòng đang hiển thị" />
          <Stat icon={<Clock3 />} value={dashboard.data.pendingRooms} label="Phòng chờ duyệt" />
          <Stat icon={<CalendarClock />} value={dashboard.data.pendingAppointments} label="Yêu cầu cần xử lý" urgent={dashboard.data.pendingAppointments > 0} />
          <Stat icon={<CalendarCheck2 />} value={dashboard.data.upcomingAppointments} label="Lịch sắp diễn ra" />
        </section>
        <section className="landlord-next-steps">
          <article>
            <CalendarClock />
            <div><h2>Yêu cầu xem phòng</h2><p>Bạn có <strong>{dashboard.data.pendingAppointments}</strong> yêu cầu đang chờ. Xác nhận sớm để người thuê chủ động lịch trình.</p></div>
            <Link className="button" to="/appointments">Xử lý yêu cầu</Link>
          </article>
          <article>
            <CheckCircle2 />
            <div><h2>Tình trạng lịch hẹn</h2><p>{dashboard.data.approvedAppointments} lịch đã xác nhận · {dashboard.data.rejectedAppointments} yêu cầu đã từ chối.</p></div>
            <Link className="button secondary" to="/appointments">Xem lịch hẹn</Link>
          </article>
        </section>
      </>}
  </div>;
}

function Stat({ icon, value, label, urgent = false }) {
  return <article className={urgent ? "overview-stat urgent" : "overview-stat"}>
    <span>{icon}</span><div><strong>{value}</strong><small>{label}</small></div>
  </article>;
}
