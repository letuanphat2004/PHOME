import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { CalendarClock, CalendarDays, Check, MapPin, RefreshCw, Trash2, X } from "lucide-react";
import { useState } from "react";
import { Link, Navigate } from "react-router-dom";
import { api, errorMessage } from "../api/client";
import { useAuth } from "../auth/AuthContext";

export default function AppointmentsPage() {
  const { user, isLoading: authLoading } = useAuth();
  const queryClient = useQueryClient();
  const landlord = user?.role === "Landlord";
  const [approved, setApproved] = useState("false");
  const [tenantFilter, setTenantFilter] = useState("all");
  const [editing, setEditing] = useState(null);
  const [newDate, setNewDate] = useState("");
  const [notice, setNotice] = useState(null);
  const query = useQuery({
    queryKey: ["appointments", landlord ? "received" : "mine", landlord ? approved : "all"],
    queryFn: () => api.get(landlord ? "/appointments/received" : "/appointments", landlord ? { params: { approved } } : undefined).then((response) => response.data),
    enabled: Boolean(user) && user.role !== "Admin",
  });
  const action = useMutation({
    mutationFn: ({ id, type, comeDate }) => {
      if (type === "approve") return api.patch(`/appointments/${id}/approve`);
      if (type === "update") return api.patch(`/appointments/${id}`, { comeDate });
      return api.delete(`/appointments/${id}`);
    },
    onSuccess: (_, variables) => {
      setEditing(null);
      setNotice({ type: "success", text: variables.type === "approve" ? "Đã duyệt lịch xem phòng." : variables.type === "update" ? "Đã cập nhật ngày xem phòng." : "Đã hủy lịch xem phòng." });
      queryClient.invalidateQueries({ queryKey: ["appointments"] });
    },
    onError: (error) => setNotice({ type: "error", text: errorMessage(error) }),
  });

  if (authLoading) return <div className="page-state">Đang tải tài khoản…</div>;
  if (!user) return <Navigate to="/login" replace />;
  if (user.role === "Admin") return <Navigate to="/admin" replace />;

  const approveAppointment = (item) => {
    if (window.confirm(`Xác nhận lịch xem phòng của ${item.fullname} vào ngày ${item.comeDate}?`)) {
      setNotice(null); action.mutate({ id: item.id, type: "approve" });
    }
  };
  const openDateEditor = (item) => {
    const [day, month, year] = item.comeDate.split("/");
    setNewDate(`${year}-${month}-${day}`);
    setEditing(item);
  };
  const deleteAppointment = (item) => {
    if (window.confirm(`Hủy lịch xem phòng ngày ${item.comeDate}?`)) {
      setNotice(null); action.mutate({ id: item.id, type: "delete" });
    }
  };

  return <div className="dashboard-page appointments-page">
    <div className="dashboard-title">
      <span className="eyebrow">Lịch hẹn</span>
      <h1>{landlord ? "Yêu cầu xem phòng" : "Lịch xem phòng của bạn"}</h1>
      <p>{landlord ? "Xem thông tin người thuê và xác nhận các yêu cầu xem phòng." : "Theo dõi và quản lý những buổi xem phòng đã đặt."}</p>
    </div>
    {notice && <div className={`notice ${notice.type}`}>{notice.text}</div>}
    {landlord ? <div className="tabs" role="tablist" aria-label="Trạng thái lịch hẹn">
      <button className={approved === "false" ? "active" : ""} onClick={() => { setApproved("false"); setNotice(null); }}>Chờ xác nhận</button>
      <button className={approved === "true" ? "active" : ""} onClick={() => { setApproved("true"); setNotice(null); }}>Đã xác nhận</button>
    </div> : <div className="tabs" role="tablist" aria-label="Lọc lịch đã đặt">
      <button className={tenantFilter === "all" ? "active" : ""} onClick={() => setTenantFilter("all")}>Tất cả</button>
      <button className={tenantFilter === "pending" ? "active" : ""} onClick={() => setTenantFilter("pending")}>Chờ xác nhận</button>
      <button className={tenantFilter === "approved" ? "active" : ""} onClick={() => setTenantFilter("approved")}>Đã xác nhận</button>
    </div>}
    {query.isLoading ? <div className="state-card">Đang tải lịch hẹn…</div> : query.isError ?
      <div className="state-card error-state"><p>{errorMessage(query.error)}</p><button className="button secondary" onClick={() => query.refetch()}><RefreshCw /> Thử lại</button></div> :
      <div className="data-list appointment-list">
        {query.data.filter((item) => landlord || tenantFilter === "all" || (tenantFilter === "approved" ? item.isApproval === "true" : item.isApproval !== "true")).length === 0 && <div className="empty empty-card"><CalendarDays /><h2>Chưa có lịch hẹn nào</h2><p>{landlord && approved === "false" ? "Yêu cầu mới từ người thuê sẽ xuất hiện tại đây." : "Không có lịch hẹn trong trạng thái này."}</p>{!landlord && <Link className="button" to="/">Tìm phòng</Link>}</div>}
        {query.data.filter((item) => landlord || tenantFilter === "all" || (tenantFilter === "approved" ? item.isApproval === "true" : item.isApproval !== "true")).map((item) => {
          const [day, month] = item.comeDate.split("/");
          return <article key={item.id}>
            <div className="date-tile"><strong>{day}</strong><span>THÁNG {month}</span></div>
            <div className="data-main">
              <strong>{landlord ? item.fullname : item.roomAddress || `Phòng #${item.room_id}`}</strong>
              {landlord && <Link className="appointment-room" to={`/rooms/${item.room_id}`}><MapPin /> {item.roomAddress || `Phòng #${item.room_id}`}</Link>}
              <span>{item.email} · {item.tel}</span>
              <small>{item.numPeople} người · {item.transportation}</small>
            </div>
            <span className={item.isApproval === "true" ? "status approved" : "status"}>{item.isApproval === "true" ? "Đã xác nhận" : "Chờ xác nhận"}</span>
            {landlord && item.isApproval !== "true" ?
              <button className="appointment-action approve" disabled={action.isPending} onClick={() => approveAppointment(item)}><Check /> Xác nhận</button> :
              !landlord && <div className="appointment-actions">
                {item.isApproval !== "true" && <button className="icon-action" title="Đổi ngày" disabled={action.isPending} onClick={() => openDateEditor(item)}><CalendarClock /></button>}
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
