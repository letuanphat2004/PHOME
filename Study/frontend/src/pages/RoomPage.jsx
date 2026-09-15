import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  ArrowLeft,
  CalendarDays,
  MapPin,
  Maximize2,
  MessageCircle,
  Phone,
  Users,
} from "lucide-react";
import { useState } from "react";
import { Link, useParams } from "react-router-dom";
import { api, errorMessage } from "../api/client";
import { useAuth } from "../auth/AuthContext";

export default function RoomPage() {
  const { id } = useParams();
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [content, setContent] = useState("");
  const [message, setMessage] = useState("");
  const [bookingOpen, setBookingOpen] = useState(false);
  const [booking, setBooking] = useState({
    fullname: "",
    email: "",
    tel: "",
    numPeople: 1,
    comeDate: "",
    transportation: "Xe máy",
  });
  const { data, isLoading, isError } = useQuery({
    queryKey: ["room", id],
    queryFn: () => api.get(`/rooms/${id}`).then((r) => r.data),
  });
  const addComment = useMutation({
    mutationFn: () => api.post(`/rooms/${id}/comments`, { content }),
    onSuccess: () => {
      setContent("");
      queryClient.invalidateQueries({ queryKey: ["room", id] });
    },
    onError: (e) => setMessage(errorMessage(e)),
  });
  const book = useMutation({
    mutationFn: () =>
      api.post("/appointments", {
        ...booking,
        roomId: Number(id),
        numPeople: Number(booking.numPeople),
      }),
    onSuccess: () => {
      setBookingOpen(false);
      setMessage("Đã gửi yêu cầu xem phòng.");
    },
    onError: (e) => setMessage(errorMessage(e)),
  });
  if (isLoading) return <div className="page-state">Đang mở căn phòng…</div>;
  if (isError)
    return (
      <div className="page-state">Không tìm thấy thông tin căn phòng.</div>
    );
  const { room, images, comments, owner } = data;
  return (
    <div className="detail-page">
      <Link className="back-link" to="/">
        <ArrowLeft /> Quay lại danh sách
      </Link>
      <div className="gallery">
        <img src={images[0] || room.image} alt={room.address} />
        {images.slice(1, 3).map((image, index) => (
          <img key={index} src={image} alt="Không gian phòng" />
        ))}
      </div>
      <div className="detail-layout">
        <section>
          <span className="eyebrow">{room.roomType}</span>
          <h1>{room.address}</h1>
          <div className="detail-meta">
            <span>
              <MapPin /> {room.address}
            </span>
            <span>
              <Maximize2 /> {room.area} m²
            </span>
            <span>
              <Users /> Tối đa {room.capacity} người
            </span>
          </div>
          <div className="description">
            <h2>Về căn phòng này</h2>
            <p>{room.description}</p>
          </div>
          <div className="comments">
            <h2>
              <MessageCircle /> Trao đổi ({comments.length})
            </h2>
            {user ? (
              <form
                onSubmit={(e) => {
                  e.preventDefault();
                  addComment.mutate();
                }}
              >
                <textarea
                  value={content}
                  onChange={(e) => setContent(e.target.value)}
                  placeholder="Bạn muốn hỏi điều gì?"
                  required
                />
                <button className="button" disabled={addComment.isPending}>
                  Gửi bình luận
                </button>
                {message && <p className="form-error">{message}</p>}
              </form>
            ) : (
              <p>
                <Link to="/login">Đăng nhập</Link> để đặt câu hỏi cho chủ nhà.
              </p>
            )}
            <div className="comment-list">
              {comments.map((comment) => (
                <article key={comment.id}>
                  <img src={comment.avatar} alt="" />
                  <div>
                    <strong>{comment.username}</strong>
                    <time>{comment.commentTime}</time>
                    <p>{comment.content}</p>
                  </div>
                </article>
              ))}
            </div>
          </div>
        </section>
        <aside className="booking-card">
          <p className="detail-price">
            <strong>{room.price}</strong> triệu <span>/ tháng</span>
          </p>
          <div className="owner">
            <img src={owner.avatar} alt="" />
            <div>
              <small>Chủ phòng</small>
              <strong>{owner.fullname}</strong>
            </div>
          </div>
          <a href={`tel:${owner.tel}`} className="button secondary">
            <Phone /> {owner.tel}
          </a>
          {user?.role === "Tenant" ? (
            <button
              onClick={() => setBookingOpen(!bookingOpen)}
              className="button"
            >
              <CalendarDays /> Đặt lịch xem phòng
            </button>
          ) : !user ? (
            <Link to="/login" className="button">
              <CalendarDays /> Đăng nhập để đặt lịch
            </Link>
          ) : null}
          {bookingOpen && (
            <form
              className="booking-form"
              onSubmit={(e) => {
                e.preventDefault();
                book.mutate();
              }}
            >
              <label>
                Họ tên
                <input
                  required
                  value={booking.fullname}
                  onChange={(e) =>
                    setBooking({ ...booking, fullname: e.target.value })
                  }
                />
              </label>
              <label>
                Email
                <input
                  type="email"
                  required
                  value={booking.email}
                  onChange={(e) =>
                    setBooking({ ...booking, email: e.target.value })
                  }
                />
              </label>
              <label>
                Số điện thoại
                <input
                  required
                  value={booking.tel}
                  onChange={(e) =>
                    setBooking({ ...booking, tel: e.target.value })
                  }
                />
              </label>
              <div className="form-row">
                <label>
                  Ngày xem
                  <input
                    type="date"
                    required
                    value={booking.comeDate}
                    onChange={(e) =>
                      setBooking({ ...booking, comeDate: e.target.value })
                    }
                  />
                </label>
                <label>
                  Số người
                  <input
                    type="number"
                    min="1"
                    required
                    value={booking.numPeople}
                    onChange={(e) =>
                      setBooking({ ...booking, numPeople: e.target.value })
                    }
                  />
                </label>
              </div>
              <label>
                Phương tiện
                <input
                  required
                  value={booking.transportation}
                  onChange={(e) =>
                    setBooking({ ...booking, transportation: e.target.value })
                  }
                />
              </label>
              <button className="button" disabled={book.isPending}>
                Gửi yêu cầu
              </button>
            </form>
          )}
          <small className="booking-note">
            Bạn chưa phải thanh toán ở bước này.
          </small>
        </aside>
      </div>
    </div>
  );
}
