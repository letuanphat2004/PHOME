import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { ArrowRight, Home, RotateCcw, Search, ShieldCheck, Sparkles } from "lucide-react";
import { useEffect, useMemo, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { api, errorMessage } from "../api/client";
import { useAuth } from "../auth/AuthContext";
import RoomCard from "../components/RoomCard";

const filterKeys = ["address", "price", "area", "roomType"];
const emptyFilters = { address: "", price: "", area: "", roomType: "" };

export default function HomePage() {
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [searchParams, setSearchParams] = useSearchParams();
  const filters = useMemo(() => Object.fromEntries(filterKeys.map((key) => [key, searchParams.get(key) || ""])), [searchParams]);
  const [draft, setDraft] = useState(filters);
  const page = Math.max(Number(searchParams.get("page")) || 0, 0);
  const sort = searchParams.get("sort") || "newest";

  useEffect(() => setDraft(filters), [filters]);

  const rooms = useQuery({
    queryKey: ["rooms", filters, page, sort],
    queryFn: () => api.get("/rooms", { params: { ...filters, page, size: 8, sort } }).then((response) => response.data),
  });
  const favorites = useQuery({
    queryKey: ["favorites"],
    queryFn: () => api.get("/favorites").then((response) => response.data),
    enabled: user?.role === "Tenant",
  });
  const favoriteIds = new Set((favorites.data ?? []).map((room) => room.room_id));
  const toggleFavorite = useMutation({
    mutationFn: ({ roomId, favorite }) => favorite ? api.delete(`/favorites/${roomId}`) : api.post(`/favorites/${roomId}`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["favorites"] }),
  });

  const updateParams = (values) => {
    const next = new URLSearchParams(searchParams);
    Object.entries(values).forEach(([key, value]) => value ? next.set(key, value) : next.delete(key));
    setSearchParams(next);
  };
  const search = (event) => {
    event.preventDefault();
    const next = new URLSearchParams();
    filterKeys.forEach((key) => { if (draft[key]?.trim()) next.set(key, draft[key].trim()); });
    if (sort !== "newest") next.set("sort", sort);
    setSearchParams(next);
  };
  const reset = () => { setDraft(emptyFilters); setSearchParams({}); };

  return <>
    <section className="hero" id="top">
      <div className="hero-copy">
        <span className="eyebrow"><Sparkles size={15} /> Không gian dành cho bạn</span>
        <h1>Tìm một căn phòng.<br /><em>Thấy một mái nhà.</em></h1>
        <p>Khám phá những căn phòng đã được chọn lọc, phù hợp với ngân sách và nhịp sống của bạn.</p>
      </div>
      <form className="search-panel tenant-search" onSubmit={search}>
        <label className="search-wide"><span>Bạn muốn ở đâu?</span><input value={draft.address} onChange={(event) => setDraft({ ...draft, address: event.target.value })} placeholder="Quận, đường hoặc khu vực" /></label>
        <label><span>Khoảng giá</span><select value={draft.price} onChange={(event) => setDraft({ ...draft, price: event.target.value })}>
          <option value="">Tất cả</option><option value="1-2">1 – 2 triệu</option><option value="2-3">2 – 3 triệu</option><option value="3">Trên 3 triệu</option>
        </select></label>
        <label><span>Diện tích</span><select value={draft.area} onChange={(event) => setDraft({ ...draft, area: event.target.value })}>
          <option value="">Tất cả</option><option value="20">Dưới 20 m²</option><option value="20-30">20 – 30 m²</option><option value="30-40">30 – 40 m²</option><option value="40">Trên 40 m²</option>
        </select></label>
        <label><span>Loại phòng</span><select value={draft.roomType} onChange={(event) => setDraft({ ...draft, roomType: event.target.value })}>
          <option value="">Tất cả</option><option value="KHONG_CHUNG_CHU">Không chung chủ</option><option value="CHUNG_CHU">Chung chủ</option>
        </select></label>
        <button className="search-button"><Search /> Tìm phòng</button>
      </form>
    </section>

    <section className="rooms-section">
      <div className="section-heading tenant-results-heading">
        <div><span className="eyebrow">Kết quả phù hợp</span><h2>{filters.address ? `Phòng tại “${filters.address}”` : "Những căn phòng mới"}</h2></div>
        <div className="results-tools">
          <span>{rooms.data?.totalElements ?? 0} lựa chọn</span>
          <select aria-label="Sắp xếp phòng" value={sort} onChange={(event) => updateParams({ sort: event.target.value, page: "" })}>
            <option value="newest">Mới đăng</option><option value="priceAsc">Giá thấp trước</option><option value="priceDesc">Giá cao trước</option><option value="areaDesc">Diện tích lớn</option>
          </select>
          {(filterKeys.some((key) => filters[key]) || sort !== "newest") && <button className="reset-filters" onClick={reset}><RotateCcw /> Xóa bộ lọc</button>}
        </div>
      </div>
      {rooms.isLoading && <div className="state-card">Đang tìm những căn phòng phù hợp…</div>}
      {rooms.isError && <div className="state-card error-state"><p>{errorMessage(rooms.error)}</p><button className="button secondary" onClick={() => rooms.refetch()}>Thử lại</button></div>}
      {rooms.data?.content.length === 0 && <div className="empty empty-card"><Search /><h2>Chưa tìm thấy phòng phù hợp</h2><p>Hãy thử mở rộng khu vực hoặc thay đổi khoảng giá.</p><button className="button secondary" onClick={reset}>Xóa bộ lọc</button></div>}
      <div className="room-grid">{rooms.data?.content.map((room) => <RoomCard key={room.room_id} room={room} favorite={favoriteIds.has(room.room_id)} canFavorite={user?.role === "Tenant"} favoritePending={toggleFavorite.isPending} onToggleFavorite={() => toggleFavorite.mutate({ roomId: room.room_id, favorite: favoriteIds.has(room.room_id) })} />)}</div>
      {rooms.data && rooms.data.totalPages > 1 && <div className="pagination">
        <button disabled={page === 0} onClick={() => updateParams({ page: String(page - 1) })}>Trước</button>
        <span>Trang {page + 1} / {rooms.data.totalPages}</span>
        <button disabled={page + 1 >= rooms.data.totalPages} onClick={() => updateParams({ page: String(page + 1) })}>Sau</button>
      </div>}
    </section>

    <section className="steps" id="how-it-works">
      <div><span className="eyebrow">Rõ ràng và nhẹ nhàng</span><h2>Chỗ ở mới trong<br />ba bước đơn giản.</h2></div>
      <div className="step-list">
        <article><Search /><span>01</span><h3>Tìm kiếm</h3><p>Lọc theo khu vực, ngân sách và diện tích.</p></article>
        <article><Home /><span>02</span><h3>Khám phá</h3><p>Xem hình ảnh, mô tả và trao đổi với chủ nhà.</p></article>
        <article><ShieldCheck /><span>03</span><h3>Đặt lịch</h3><p>Chọn thời gian phù hợp để đến xem phòng.</p></article>
      </div>
      <a className="text-link" href="#top">Bắt đầu tìm phòng <ArrowRight /></a>
    </section>
  </>;
}
