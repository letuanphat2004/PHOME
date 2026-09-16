import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Bell, CheckCheck, ChevronLeft, ChevronRight, RefreshCw } from "lucide-react";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { api, errorMessage } from "../api/client";
import { formatTime } from "../components/NotificationBell";

export default function NotificationsPage() {
  const [unreadOnly, setUnreadOnly] = useState(false);
  const [page, setPage] = useState(0);
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const notifications = useQuery({
    queryKey: ["notifications", unreadOnly, page],
    queryFn: () => api.get("/notifications", { params: { unreadOnly, page, size: 12 } }).then((response) => response.data),
  });
  const markRead = useMutation({
    mutationFn: (id) => api.patch(`/notifications/${id}/read`),
    onSuccess: refreshNotifications,
  });
  const markAll = useMutation({
    mutationFn: () => api.patch("/notifications/read-all"),
    onSuccess: refreshNotifications,
  });
  function refreshNotifications() {
    queryClient.invalidateQueries({ queryKey: ["notifications"] });
    queryClient.invalidateQueries({ queryKey: ["notification-count"] });
  }
  const openNotification = async (notification) => {
    if (!notification.read) await markRead.mutateAsync(notification.id);
    if (notification.link) navigate(notification.link);
  };

  return <div className="dashboard-page notifications-page">
    <div className="dashboard-title row"><div><span className="eyebrow">Trung tâm cập nhật</span><h1>Thông báo</h1><p>Theo dõi những thay đổi liên quan trực tiếp đến tài khoản của bạn.</p></div><button className="button secondary" onClick={() => markAll.mutate()} disabled={markAll.isPending}><CheckCheck /> Đánh dấu đã đọc</button></div>
    <div className="tabs"><button className={!unreadOnly ? "active" : ""} onClick={() => { setUnreadOnly(false); setPage(0); }}>Tất cả</button><button className={unreadOnly ? "active" : ""} onClick={() => { setUnreadOnly(true); setPage(0); }}>Chưa đọc</button></div>
    {notifications.isLoading ? <div className="state-card">Đang tải thông báo…</div> : notifications.isError ?
      <div className="state-card error-state"><p>{errorMessage(notifications.error)}</p><button className="button secondary" onClick={() => notifications.refetch()}><RefreshCw /> Thử lại</button></div> : notifications.data.content.length === 0 ?
        <div className="empty empty-card"><Bell /><h2>Chưa có thông báo</h2><p>Các cập nhật mới sẽ xuất hiện tại đây.</p></div> : <>
          <div className="notification-page-list">{notifications.data.content.map((notification) => <button key={notification.id} className={notification.read ? "" : "unread"} onClick={() => openNotification(notification)}>
            <span className="notification-dot" /><div><strong>{notification.title}</strong><p>{notification.message}</p><small>{formatTime(notification.createdAt)}</small></div>
          </button>)}</div>
          {notifications.data.totalPages > 1 && <div className="pagination"><button disabled={page === 0} onClick={() => setPage((value) => value - 1)}><ChevronLeft /> Trước</button><span>Trang {page + 1}/{notifications.data.totalPages}</span><button disabled={page + 1 >= notifications.data.totalPages} onClick={() => setPage((value) => value + 1)}>Sau <ChevronRight /></button></div>}
        </>}
  </div>;
}
