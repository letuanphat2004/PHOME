import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Ban, Building2, Check, Clock3, RefreshCw, Search, ShieldCheck, UserCheck, UserRoundX, Users, X } from "lucide-react";
import { useMemo, useState } from "react";
import { Navigate } from "react-router-dom";
import { api, errorMessage } from "../api/client";
import { useAuth } from "../auth/AuthContext";

const roomStatusOptions = [
  ["pending", "Chờ duyệt"],
  ["approved", "Đã duyệt"],
  ["rejected", "Đã từ chối"],
];
const roleNames = { 1: "Người thuê", 2: "Chủ nhà", 3: "Quản trị viên" };

export default function AdminPage() {
  const { user, isLoading: authLoading } = useAuth();
  const queryClient = useQueryClient();
  const [tab, setTab] = useState("rooms");
  const [roomStatus, setRoomStatus] = useState("pending");
  const [keyword, setKeyword] = useState("");
  const [rejectingRoom, setRejectingRoom] = useState(null);
  const [reason, setReason] = useState("");
  const [notice, setNotice] = useState(null);

  const dashboard = useQuery({
    queryKey: ["admin-dashboard"],
    queryFn: () => api.get("/admin/dashboard").then((response) => response.data),
    enabled: user?.role === "Admin",
  });
  const records = useQuery({
    queryKey: ["admin", tab, tab === "rooms" ? roomStatus : "all"],
    queryFn: () => api.get(`/admin/${tab}`, tab === "rooms" ? { params: { status: roomStatus } } : undefined).then((response) => response.data),
    enabled: user?.role === "Admin",
  });

  const roomAction = useMutation({
    mutationFn: ({ id, type, rejectionReason }) => type === "approve"
      ? api.patch(`/admin/rooms/${id}/approve`)
      : api.patch(`/admin/rooms/${id}/reject`, { reason: rejectionReason }),
    onSuccess: (_, variables) => {
      setRejectingRoom(null); setReason("");
      setNotice({ type: "success", text: variables.type === "approve" ? "Đã duyệt và công khai phòng." : "Đã gửi phản hồi từ chối cho chủ nhà." });
      queryClient.invalidateQueries({ queryKey: ["admin"] });
      queryClient.invalidateQueries({ queryKey: ["admin-dashboard"] });
      queryClient.invalidateQueries({ queryKey: ["landlord-dashboard"] });
      queryClient.invalidateQueries({ queryKey: ["my-rooms"] });
    },
    onError: (error) => setNotice({ type: "error", text: errorMessage(error) }),
  });
  const userAction = useMutation({
    mutationFn: ({ id, enabled }) => api.patch(`/admin/users/${id}/enabled`, { enabled }),
    onSuccess: (_, variables) => {
      setNotice({ type: "success", text: variables.enabled ? "Đã mở lại tài khoản." : "Đã vô hiệu hóa tài khoản." });
      queryClient.invalidateQueries({ queryKey: ["admin", "users"] });
      queryClient.invalidateQueries({ queryKey: ["admin-dashboard"] });
    },
    onError: (error) => setNotice({ type: "error", text: errorMessage(error) }),
  });

  const visibleRecords = useMemo(() => {
    const normalized = keyword.trim().toLocaleLowerCase("vi");
    if (!normalized) return records.data ?? [];
    return (records.data ?? []).filter((item) => tab === "rooms"
      ? item.address?.toLocaleLowerCase("vi").includes(normalized)
      : [item.fullname, item.username, item.email, item.tel].some((value) => value?.toLocaleLowerCase("vi").includes(normalized)));
  }, [records.data, keyword, tab]);

  if (authLoading) return <div className="page-state">Đang tải tài khoản…</div>;
  if (user?.role !== "Admin") return <Navigate to="/account" replace />;

  return <div className="dashboard-page admin-dashboard">
    <div className="dashboard-title">
      <span className="eyebrow"><ShieldCheck /> Quản trị PHOME</span>
      <h1>Trung tâm kiểm soát</h1>
      <p>Duyệt nội dung phòng và quản lý quyền truy cập của người dùng.</p>
    </div>
    {dashboard.isLoading ? <div className="state-card">Đang tổng hợp dữ liệu…</div> : dashboard.isError ?
      <div className="state-card error-state"><p>{errorMessage(dashboard.error)}</p><button className="button secondary" onClick={() => dashboard.refetch()}><RefreshCw /> Thử lại</button></div> :
      <section className="admin-overview" aria-label="Tổng quan quản trị">
        <AdminStat icon={<Clock3 />} value={dashboard.data.pendingRooms} label="Phòng chờ duyệt" urgent={dashboard.data.pendingRooms > 0} />
        <AdminStat icon={<Building2 />} value={dashboard.data.approvedRooms} label="Phòng đang hiển thị" />
        <AdminStat icon={<Ban />} value={dashboard.data.rejectedRooms} label="Phòng đã từ chối" />
        <AdminStat icon={<UserCheck />} value={dashboard.data.activeUsers} label="Tài khoản hoạt động" />
        <AdminStat icon={<UserRoundX />} value={dashboard.data.disabledUsers} label="Tài khoản vô hiệu hóa" />
      </section>}
    {notice && <div className={`notice ${notice.type}`}>{notice.text}</div>}
    <div className="admin-toolbar">
      <div className="tabs" role="tablist">
        <button className={tab === "rooms" ? "active" : ""} onClick={() => { setTab("rooms"); setKeyword(""); }}>Kiểm duyệt phòng</button>
        <button className={tab === "users" ? "active" : ""} onClick={() => { setTab("users"); setKeyword(""); }}>Người dùng</button>
      </div>
      <label className="admin-search"><Search /><input value={keyword} onChange={(event) => setKeyword(event.target.value)} placeholder={tab === "rooms" ? "Tìm theo địa chỉ" : "Tìm tên, tài khoản, email"} /></label>
    </div>
    {tab === "rooms" && <div className="tabs compact" role="tablist" aria-label="Trạng thái phòng">
      {roomStatusOptions.map(([value, label]) => <button key={value} className={roomStatus === value ? "active" : ""} onClick={() => { setRoomStatus(value); setNotice(null); }}>{label}</button>)}
    </div>}
    {records.isLoading ? <div className="state-card">Đang tải dữ liệu…</div> : records.isError ?
      <div className="state-card error-state"><p>{errorMessage(records.error)}</p><button className="button secondary" onClick={() => records.refetch()}><RefreshCw /> Thử lại</button></div> : visibleRecords.length === 0 ?
        <div className="empty empty-card"><Search /><h2>Không tìm thấy dữ liệu</h2><p>Thử trạng thái hoặc từ khóa khác.</p></div> : tab === "rooms" ?
          <div className="admin-grid moderation-list">{visibleRecords.map((room) => <article key={room.room_id}>
            <img src={room.image} alt={`Phòng tại ${room.address}`} />
            <div><strong>{room.address}</strong><span>{room.price} triệu · {room.area} m² · {room.capacity} người</span><small>{room.roomType}</small>{room.moderationNote && <p className="moderation-note">“{room.moderationNote}”</p>}</div>
            {roomStatus === "pending" && <div className="moderation-actions"><button className="appointment-action approve" disabled={roomAction.isPending} onClick={() => roomAction.mutate({ id: room.room_id, type: "approve" })}><Check /> Duyệt</button><button className="appointment-action reject" disabled={roomAction.isPending} onClick={() => { setRejectingRoom(room); setReason(""); setNotice(null); }}><Ban /> Từ chối</button></div>}
          </article>)}</div> :
          <div className="data-list admin-users">{visibleRecords.map((person) => <article key={person.id}>
            <div className="avatar-fallback small"><Users /></div>
            <div className="data-main"><strong>{person.fullname}</strong><span>@{person.username} · {person.email}</span><small>{roleNames[person.role_id] ?? `Vai trò #${person.role_id}`} · {person.tel}</small></div>
            <span className={person.disabled ? "status rejected" : "status approved"}>{person.disabled ? "Đã vô hiệu hóa" : "Đang hoạt động"}</span>
            {person.role_id !== 3 && <button className={person.disabled ? "appointment-action approve" : "appointment-action reject"} disabled={userAction.isPending} onClick={() => { if (window.confirm(`${person.disabled ? "Mở lại" : "Vô hiệu hóa"} tài khoản @${person.username}?`)) userAction.mutate({ id: person.id, enabled: person.disabled }); }}>{person.disabled ? <UserCheck /> : <UserRoundX />}{person.disabled ? "Mở lại" : "Vô hiệu hóa"}</button>}
          </article>)}</div>}
    {rejectingRoom && <div className="modal-backdrop" onMouseDown={(event) => event.target === event.currentTarget && !roomAction.isPending && setRejectingRoom(null)}>
      <section className="modal rejection-modal" role="dialog" aria-modal="true" aria-labelledby="reject-room-title">
        <button className="modal-close" onClick={() => setRejectingRoom(null)} disabled={roomAction.isPending} aria-label="Đóng"><X /></button>
        <span className="eyebrow">Phản hồi kiểm duyệt</span><h2 id="reject-room-title">Từ chối phòng</h2>
        <p className="editor-note">Nêu rõ nội dung cần chỉnh sửa cho phòng tại {rejectingRoom.address}.</p>
        <form onSubmit={(event) => { event.preventDefault(); roomAction.mutate({ id: rejectingRoom.room_id, type: "reject", rejectionReason: reason }); }}>
          <label>Lý do từ chối<textarea value={reason} onChange={(event) => setReason(event.target.value)} minLength="1" maxLength="500" placeholder="Ví dụ: Hình ảnh chưa thể hiện rõ không gian và mô tả còn thiếu thông tin tiện ích." required /></label>
          <small className="character-count">{reason.length}/500 ký tự</small>
          <div className="modal-actions"><button type="button" className="button secondary" onClick={() => setRejectingRoom(null)} disabled={roomAction.isPending}>Hủy</button><button className="button danger-button" disabled={roomAction.isPending}>{roomAction.isPending ? "Đang gửi…" : "Gửi phản hồi"}</button></div>
        </form>
      </section>
    </div>}
  </div>;
}

function AdminStat({ icon, value, label, urgent = false }) {
  return <article className={urgent ? "admin-stat urgent" : "admin-stat"}><span>{icon}</span><div><strong>{value}</strong><small>{label}</small></div></article>;
}
