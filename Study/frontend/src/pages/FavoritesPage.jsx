import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Heart, RefreshCw } from "lucide-react";
import { Link } from "react-router-dom";
import { api, errorMessage } from "../api/client";
import RoomCard from "../components/RoomCard";

export default function FavoritesPage() {
  const queryClient = useQueryClient();
  const favorites = useQuery({ queryKey: ["favorites"], queryFn: () => api.get("/favorites").then((response) => response.data) });
  const remove = useMutation({
    mutationFn: (roomId) => api.delete(`/favorites/${roomId}`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["favorites"] }),
  });

  return <div className="dashboard-page favorites-page">
    <div className="dashboard-title"><span className="eyebrow">Bộ sưu tập của bạn</span><h1>Phòng yêu thích</h1><p>Lưu lại những lựa chọn phù hợp để dễ dàng so sánh và đặt lịch sau.</p></div>
    {favorites.isLoading ? <div className="state-card">Đang tải phòng yêu thích…</div> : favorites.isError ?
      <div className="state-card error-state"><p>{errorMessage(favorites.error)}</p><button className="button secondary" onClick={() => favorites.refetch()}><RefreshCw /> Thử lại</button></div> :
      favorites.data.length === 0 ? <div className="empty empty-card"><Heart /><h2>Chưa có phòng yêu thích</h2><p>Nhấn biểu tượng trái tim khi bạn thấy một căn phòng phù hợp.</p><Link className="button" to="/">Khám phá phòng</Link></div> :
      <div className="room-grid">{favorites.data.map((room) => <RoomCard key={room.room_id} room={room} favorite canFavorite favoritePending={remove.isPending} onToggleFavorite={() => remove.mutate(room.room_id)} />)}</div>}
  </div>;
}
