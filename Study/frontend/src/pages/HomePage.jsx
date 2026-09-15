import { useQuery } from "@tanstack/react-query";
import { ArrowRight, Home, Search, ShieldCheck, Sparkles } from "lucide-react";
import { useState } from "react";
import { api } from "../api/client";
import RoomCard from "../components/RoomCard";

const emptyFilters = { address: "", price: "", area: "", roomType: "" };

export default function HomePage() {
  const [draft, setDraft] = useState(emptyFilters);
  const [filters, setFilters] = useState(emptyFilters);
  const [page, setPage] = useState(0);
  const { data, isLoading, isError } = useQuery({
    queryKey: ["rooms", filters, page],
    queryFn: () =>
      api
        .get("/rooms", { params: { ...filters, page, size: 8 } })
        .then((r) => r.data),
  });
  const search = (event) => {
    event.preventDefault();
    setPage(0);
    setFilters(draft);
  };

  return (
    <>
      <section className="hero">
        <div className="hero-copy">
          <span className="eyebrow">
            <Sparkles size={15} /> Không gian dành cho bạn
          </span>
          <h1>
            Tìm một căn phòng.
            <br />
            <em>Thấy một mái nhà.</em>
          </h1>
          <p>
            Khám phá những căn phòng đã được chọn lọc, phù hợp với ngân sách và
            nhịp sống của bạn.
          </p>
        </div>
        <form className="search-panel" onSubmit={search}>
          <label className="search-wide">
            <span>Bạn muốn ở đâu?</span>
            <input
              value={draft.address}
              onChange={(e) => setDraft({ ...draft, address: e.target.value })}
              placeholder="Quận, đường hoặc khu vực"
            />
          </label>
          <label>
            <span>Khoảng giá</span>
            <select
              value={draft.price}
              onChange={(e) => setDraft({ ...draft, price: e.target.value })}
            >
              <option value="">Tất cả</option>
              <option value="1">Dưới 1 triệu</option>
              <option value="1-2">1 – 2 triệu</option>
              <option value="2-3">2 – 3 triệu</option>
              <option value="3">Trên 3 triệu</option>
            </select>
          </label>
          <label>
            <span>Diện tích</span>
            <select
              value={draft.area}
              onChange={(e) => setDraft({ ...draft, area: e.target.value })}
            >
              <option value="">Tất cả</option>
              <option value="20">Dưới 20 m²</option>
              <option value="20-30">20 – 30 m²</option>
              <option value="30-40">30 – 40 m²</option>
              <option value="40">Trên 40 m²</option>
            </select>
          </label>
          <button className="search-button">
            <Search /> Tìm phòng
          </button>
        </form>
      </section>

      <section className="rooms-section">
        <div className="section-heading">
          <div>
            <span className="eyebrow">Gợi ý hôm nay</span>
            <h2>Những căn phòng mới</h2>
          </div>
          <p>{data?.totalElements ?? 0} lựa chọn đang chờ bạn</p>
        </div>
        {isLoading && (
          <div className="state-card">Đang tìm những căn phòng phù hợp…</div>
        )}
        {isError && (
          <div className="state-card error">
            Chưa thể tải danh sách phòng. Hãy kiểm tra backend và thử lại.
          </div>
        )}
        <div className="room-grid">
          {data?.content.map((room) => (
            <RoomCard key={room.room_id} room={room} />
          ))}
        </div>
        {data && data.totalPages > 1 && (
          <div className="pagination">
            <button disabled={page === 0} onClick={() => setPage(page - 1)}>
              Trước
            </button>
            <span>
              {page + 1} / {data.totalPages}
            </span>
            <button
              disabled={page + 1 >= data.totalPages}
              onClick={() => setPage(page + 1)}
            >
              Sau
            </button>
          </div>
        )}
      </section>

      <section className="steps" id="how-it-works">
        <div>
          <span className="eyebrow">Rõ ràng và nhẹ nhàng</span>
          <h2>
            Chỗ ở mới trong
            <br />
            ba bước đơn giản.
          </h2>
        </div>
        <div className="step-list">
          <article>
            <Search />
            <span>01</span>
            <h3>Tìm kiếm</h3>
            <p>Lọc theo khu vực, ngân sách và diện tích.</p>
          </article>
          <article>
            <Home />
            <span>02</span>
            <h3>Khám phá</h3>
            <p>Xem hình ảnh, mô tả và trao đổi với chủ nhà.</p>
          </article>
          <article>
            <ShieldCheck />
            <span>03</span>
            <h3>Đặt lịch</h3>
            <p>Chọn thời gian phù hợp để đến xem phòng.</p>
          </article>
        </div>
        <a className="text-link" href="#top">
          Bắt đầu tìm phòng <ArrowRight />
        </a>
      </section>
    </>
  );
}
