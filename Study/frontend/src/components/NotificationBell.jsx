import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Bell, CheckCheck } from "lucide-react";
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { api } from "../api/client";

export default function NotificationBell({ user }) {
  const [open, setOpen] = useState(false);
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const count = useQuery({
    queryKey: ["notification-count"],
    queryFn: () => api.get("/notifications/unread-count").then((response) => response.data.count),
    enabled: Boolean(user),
    refetchInterval: 30000,
  });
  const recent = useQuery({
    queryKey: ["notifications", "recent"],
    queryFn: () => api.get("/notifications", { params: { page: 0, size: 5 } }).then((response) => response.data),
    enabled: Boolean(user) && open,
  });
  const markRead = useMutation({
    mutationFn: (id) => api.patch(`/notifications/${id}/read`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["notification-count"] });
      queryClient.invalidateQueries({ queryKey: ["notifications"] });
    },
  });
  const openNotification = async (notification) => {
    if (!notification.read) await markRead.mutateAsync(notification.id);
    setOpen(false);
    navigate(notification.link || "/notifications");
  };

  return <div className="notification-bell">
    <button className="notification-trigger" onClick={(event) => { event.stopPropagation(); setOpen((value) => !value); }} aria-label="Thông báo" aria-expanded={open}>
      <Bell />{count.data > 0 && <span>{count.data > 99 ? "99+" : count.data}</span>}
    </button>
    {open && <div className="notification-popover">
      <div className="notification-popover-head"><strong>Thông báo</strong><Link to="/notifications" onClick={() => setOpen(false)}>Xem tất cả</Link></div>
      {recent.isLoading ? <p className="notification-empty">Đang tải…</p> : (recent.data?.content?.length ?? 0) === 0 ?
        <p className="notification-empty"><CheckCheck /> Bạn đã xem hết thông báo.</p> :
        <div className="notification-preview-list">{recent.data.content.map((notification) =>
          <button key={notification.id} className={notification.read ? "" : "unread"} onClick={() => openNotification(notification)}>
            <strong>{notification.title}</strong><span>{notification.message}</span><small>{formatTime(notification.createdAt)}</small>
          </button>)}</div>}
    </div>}
  </div>;
}

export function formatTime(value) {
  if (!value) return "";
  return new Intl.DateTimeFormat("vi-VN", { dateStyle: "short", timeStyle: "short" }).format(new Date(value));
}
