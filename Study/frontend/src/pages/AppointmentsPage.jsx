import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Ban, CalendarClock, CalendarDays, Check, MapPin, RefreshCw, Trash2, X } from "lucide-react";
import { useState } from "react";
import { Link, Navigate } from "react-router-dom";
import { api, errorMessage } from "../api/client";
import { useAuth } from "../auth/AuthContext";

const statusMeta = {
  false: { key: "pending", label: "Chờ xác nhận", className: "status" },
  true: { key: "approved", label: "Đã xác nhận", className: "status approved" },
  rejected: { key: "rejected", label: "Đã từ chối", className: "status rejected" },
};

export default function AppointmentsPage() {
  const { user, isLoading: authLoading } = useAuth();
  const queryClient = useQueryClient();
  const landlord = user?.role === "Landlord";
  const [landlordStatus, setLandlordStatus] = useState("pending");
  const [tenantFilter, setTenantFilter] = useState("all");
  const [editing, setEditing] = useState(null);
  const [newDate, setNewDate] = useState("");
  const [notice, setNotice] = useState(null);

  const query = useQuery({
    queryKey: ["appointments", landlord ? "received" : "mine", landlord ? landlordStatus : "all"],
    queryFn: () => api.get(
      landlord ? "/appointments/received" : "/appointments",
      landlord ? { params: { status: landlordStatus } } : undefined,
    ).then((response) => response.data),
    enabled: Boolean(user) && user.role !== "Admin",
  });

  const action = useMutation({
    mutationFn: ({ id, type, comeDate }) => {
      if (type === "approve") return api.patch(`/appointments/${id}/approve`);
      if (type === "reject") return api.patch(`/appointments/${id}/reject`);
      if (type === "update") return api.patch(`/appointments/${id}`, { comeDate });
      return api.delete(`/appointments/${id}`);
    },
    onSuccess: (_, variables) => {
      const messages = {
        approve: "Đã xác nhận lịch xem phòng.",
        reject: "Đã từ chối yêu cầu xem phòng.",
        update: "Đã cập nhật ngày xem phòng.",
        delete: "Đã hủy lịch xem phòng.",
      };
      setEditing(null);
      setNotice({ type: "success", text: messages[variables.type] });
      queryClient.invalidateQueries({ queryKey: ["appointments"] });
      queryClient.invalidateQueries({ queryKey: ["landlord-dashboard"] });
    },
    onError: (error) => setNotice({ type: "error", text: errorMessage(error) }),
  });

  if (authLoading) return <div className="page-state">Đang tải tài khoản…</div>;
  if (!user) return <Navigate to="/login" replace />;
  if (user.role === "Admin") return <Navigate to="/admin" replace />;

  const decideAppointment = (item, type) => {
    const verb = type === "approve" ? "Xác nhận" : "Từ chối";
    if (window.confirm(`${verb} lịch xem phòng của ${item.fullname} vào ngày ${item.comeDate}?`)) {
      setNotice(null);
      action.mutate({ id: item.id, type });
    }
  };
  const openDateEditor = (item) => {
    const [day, month, year] = item.comeDate.split("/");
    setNewDate(`${year}-${month}-${day}`);
    setEditing(item);
  };
  const deleteAppointment = (item) => {
    if (window.confirm(`Hủy lịch xem phòng ngày ${item.comeDate}?`)) {
      setNotice(null);
      action.mutate({ id: item.id, type: "delete" });
    }
  };
  const visibleAppointments = (query.data ?? []).filter((item) =>
    landlord || tenantFilter === "all" || statusMeta[item.isApproval]?.key === tenantFilter,
  );

  return <div className="dashboard-page appointments-page">
    <div className="dashboard-title">
      <span className="eyebrow">Lịch hẹn</span>
      <h1>{landlord ? "Yêu cầu xem phòng" : "Lịch xem phòng của bạn"}</h1>
      <p>{landlord ? "Xem thông tin người thuê, xác nhận hoặc từ chối từng yêu cầu." : "Theo dõi và quản lý những buổi xem phòng đã đặt."}</p>
    </div>
    {notice && <div className={`notice ${notice.type}`}>{notice.text}</div>}
    {landlord ? <div className="tabs" role="tablist" aria-label="Trạng thái lịch hẹn">
      {[["pending", "Chờ xác nhận"], ["approved", "Đã xác nhận"], ["rejected", "Đã từ chối"]].map(([value, label]) =>
        <button key={value} className={landlordStatus === value ? "active" : ""} onClick={() => { setLandlordStatus(value); setNotice(null); }}>{label}</button>)}
    </div> : <div className="tabs" role="tablist" aria-label="Lọc lịch đã đặt">
      {[["all", "Tất cả"], ["pending", "Chờ xác nhận"], ["approved", "Đã xác nhận"], ["rejected", "Đã từ chối"]].map(([value, label]) =>
        <button key={value} className={tenantFilter === value ? "active" : ""} onClick={() => setTenantFilter(value)}>{label}</button>)}
    </div>}
    {query.isLoading ? <div className="state-card">Đang tải lịch hẹn…</div> : query.isError ?
      <div className="state-card error-state"><p>{errorMessage(query.error)}</p><button className="button secondary" onClick={() => query.refetch()}><RefreshCw /> Thử lại</button></div> :
      <div className="data-list appointment-list">
        {visibleAppointments.length === 0 && <div className="empty empty-card"><CalendarDays /><h2>Chưa có lịch hẹn nào</h2><p>{landlord && landlordStatus === "pending" ? "Yêu cầu mới từ người thuê sẽ xuất hiện tại đây." : "Không có lịch hẹn trong trạng thái này."}</p>{!landlord && <Link className="button" to="/">Tìm phòng</Link>}</div>}
        {visibleAppointments.map((item) => {
          const [day, month] = item.comeDate.split("/");
          const meta = statusMeta[item.isApproval] ?? statusMeta.false;
          return <article key={item.id}>
            <div className="date-tile"><strong>{day}</strong><span>THÁNG {month}</span></div>
            <div className="data-main">
              <strong>{landlord ? item.fullname : item.roomAddress || `Phòng #${item.room_id}`}</strong>
              {landlord && <Link className="appointment-room" to={`/rooms/${item.room_id}`}><MapPin /> {item.roomAddress || `Phòng #${item.room_id}`}</Link>}
              <span>{item.email} · {item.tel}</span>
              <small>{item.numPeople} người · {item.transportation}</small>
            </div>
            <span className={meta.className}>{meta.label}</span>
            {landlord && item.isApproval === "false" ? <div className="landlord-decision-actions">
              <button className="appointment-action approve" disabled={action.isPending} onClick={() => decideAppointment(item, "approve")}><Check /> Xác nhận</button>
              <button className="appointment-action reject" disabled={action.isPending} onClick={() => decideAppointment(item, "reject")}><Ban /> Từ chối</button>
            </div> : !landlord && <div className="appointment-actions">
              {item.isApproval === "false" && <button className="icon-action" title="Đổi ngày" disabled={action.isPending} onClick={() => openDateEditor(item)}><CalendarClock /></button>}
              <button className="icon-action danger" title="Hủy lịch" disabled={action.isPending} onClick={() => deleteAppointment(item)}><Trash2 /></button>
            </div>}
          </article>;
        })}
      </div>}
    {editing && <div className="modal-backdrop" onMouseDown={(event) => event.target === event.currentTarget && !action.isPending && setEditing(null)}>
      <section className="modal date-editor" role="dialog" aria-modal="true" aria-labelledby="date-editor-title">
        <button className="modal-close" onClick={() => setEditing(null)} disabled={action.isPending} aria-label="Đóng"><X /></button>
        <span className="eyebrow">Điều chỉnh lịch hẹn</span><h2 id="date-editor-title">Chọn ngày xem mới</h2>
        <p className="editor-note">Phòng: {editing.roomAddress || `#${editing.room_id}`}. Sau khi đổi ngày, lịch vẫn ở trạng thái chờ chủ nhà xác nhận.</p>
        <form onSubmit={(event) => { event.preventDefault(); setNotice(null); action.mutate({ id: editing.id, type: "update", comeDate: newDate }); }}>
          <label>Ngày xem<input type="date" min={new Date().toLocaleDateString("en-CA")} value={newDate} onChange={(event) => setNewDate(event.target.value)} required /></label>
          {action.isError && <p className="form-error">{errorMessage(action.error)}</p>}
          <div className="modal-actions"><button type="button" className="button secondary" onClick={() => setEditing(null)} disabled={action.isPending}>Hủy</button><button className="button" disabled={action.isPending}>{action.isPending ? "Đang lưu…" : "Lưu ngày mới"}</button></div>
        </form>
      </section>
    </div>}
  </div>;
}
