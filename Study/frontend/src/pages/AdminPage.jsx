import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Check, ShieldCheck, Trash2, Users } from "lucide-react";
import { useState } from "react";
import { Navigate } from "react-router-dom";
import { api } from "../api/client";
import { useAuth } from "../auth/AuthContext";

export default function AdminPage() {
  const { user, isLoading: authLoading } = useAuth();
  const [tab, setTab] = useState("rooms");
  const qc = useQueryClient();
  const { data = [], isLoading } = useQuery({
    queryKey: ["admin", tab],
    queryFn: () => api.get(`/admin/${tab}`).then((r) => r.data),
    enabled: user?.role === "Admin",
  });
  const roomAction = useMutation({
    mutationFn: ({ id, approve }) =>
      approve
        ? api.patch(`/admin/rooms/${id}/approve`)
        : api.delete(`/admin/rooms/${id}`),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["admin", "rooms"] }),
  });
  if (authLoading) return <div className="page-state">Đang tải…</div>;
  if (user?.role !== "Admin") return <Navigate to="/account" replace />;
  return (
    <div className="dashboard-page">
      <div className="dashboard-title">
        <span className="eyebrow">
          <ShieldCheck /> Quản trị PHOME
        </span>
        <h1>Kiểm soát nội dung</h1>
      </div>
      <div className="tabs">
        <button
          className={tab === "rooms" ? "active" : ""}
          onClick={() => setTab("rooms")}
        >
          Phòng trọ
        </button>
        <button
          className={tab === "users" ? "active" : ""}
          onClick={() => setTab("users")}
        >
          Người dùng
        </button>
      </div>
      {isLoading ? (
        <div className="state-card">Đang tải…</div>
      ) : tab === "rooms" ? (
        <div className="admin-grid">
          {data.map((room) => (
            <article key={room.room_id}>
              <img src={room.image} alt="" />
              <div>
                <strong>{room.address}</strong>
                <span>
                  {room.price} triệu · {room.area} m²
                </span>
                <small>
                  {room.isApproval === "true" ? "Đã duyệt" : "Đang chờ duyệt"}
                </small>
              </div>
              {room.isApproval !== "true" && (
                <button
                  className="icon-action approve"
                  onClick={() =>
                    roomAction.mutate({ id: room.room_id, approve: true })
                  }
                >
                  <Check />
                </button>
              )}
              <button
                className="icon-action danger"
                onClick={() =>
                  roomAction.mutate({ id: room.room_id, approve: false })
                }
              >
                <Trash2 />
              </button>
            </article>
          ))}
        </div>
      ) : (
        <div className="data-list">
          {data.map((person) => (
            <article key={person.id}>
              <div className="avatar-fallback small">
                <Users />
              </div>
              <div className="data-main">
                <strong>{person.fullname}</strong>
                <span>
                  @{person.username} · {person.email}
                </span>
                <small>
                  Vai trò #{person.role_id} · {person.tel}
                </small>
              </div>
            </article>
          ))}
        </div>
      )}
    </div>
  );
}
