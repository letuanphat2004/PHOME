import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { ArrowLeft, CalendarDays, ChevronLeft, ChevronRight, Heart, MapPin, Maximize2, MessageCircle, Pencil, Phone, Star, Trash2, Users, X } from "lucide-react";
import { useState } from "react";
import { Link, useLocation, useParams } from "react-router-dom";
import { api, errorMessage } from "../api/client";
import { useAuth } from "../auth/AuthContext";

const emptyBooking = { fullname: "", email: "", tel: "", numPeople: 1, comeDate: "", transportation: "Xe máy" };
const today = new Date().toLocaleDateString("en-CA");

export default function RoomPage() {
  const { id } = useParams();
  const location = useLocation();
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [content, setContent] = useState("");
  const [commentError, setCommentError] = useState("");
  const [notice, setNotice] = useState(null);
  const [bookingOpen, setBookingOpen] = useState(false);
  const [booking, setBooking] = useState(emptyBooking);
  const [selectedImage, setSelectedImage] = useState("");
  const [reviewDraft, setReviewDraft] = useState({ rating: 5, content: "" });
  const [editingReview, setEditingReview] = useState(false);
  const [reviewPage, setReviewPage] = useState(0);

  const details = useQuery({ queryKey: ["room", id], queryFn: () => api.get(`/rooms/${id}`).then((response) => response.data) });
  const reviews = useQuery({ queryKey: ["room-reviews", id, reviewPage], queryFn: () => api.get(`/rooms/${id}/reviews`, { params: { page: reviewPage, size: 5 } }).then((response) => response.data) });
  const profile = useQuery({ queryKey: ["profile"], queryFn: () => api.get("/profile").then((response) => response.data), enabled: user?.role === "Tenant" });
  const addComment = useMutation({
    mutationFn: () => api.post(`/rooms/${id}/comments`, { content }),
    onSuccess: () => { setContent(""); setCommentError(""); queryClient.invalidateQueries({ queryKey: ["room", id] }); },
    onError: (error) => setCommentError(errorMessage(error)),
  });
  const book = useMutation({
    mutationFn: () => api.post("/appointments", { ...booking, roomId: Number(id), numPeople: Number(booking.numPeople) }),
    onSuccess: () => {
      setBookingOpen(false); setBooking(emptyBooking);
      setNotice({ type: "success", text: "Đã gửi yêu cầu xem phòng. Bạn có thể theo dõi trong mục Lịch đã đặt." });
      queryClient.invalidateQueries({ queryKey: ["appointments"] });
    },
    onError: (error) => setNotice({ type: "error", text: errorMessage(error) }),
  });
  const toggleFavorite = useMutation({
    mutationFn: (favorite) => favorite ? api.delete(`/favorites/${id}`) : api.post(`/favorites/${id}`),
    onSuccess: () => { queryClient.invalidateQueries({ queryKey: ["room", id] }); queryClient.invalidateQueries({ queryKey: ["favorites"] }); },
    onError: (error) => setNotice({ type: "error", text: errorMessage(error) }),
  });
  const saveReview = useMutation({
    mutationFn: () => reviews.data?.mine
      ? api.put(`/rooms/${id}/reviews/mine`, reviewDraft)
      : api.post(`/rooms/${id}/reviews`, reviewDraft),
    onSuccess: () => {
      setEditingReview(false);
      setReviewPage(0);
      setReviewDraft({ rating: 5, content: "" });
      setNotice({ type: "success", text: "Đánh giá của bạn đã được lưu." });
      queryClient.invalidateQueries({ queryKey: ["room-reviews", id] });
      queryClient.invalidateQueries({ queryKey: ["notification-count"] });
    },
    onError: (error) => setNotice({ type: "error", text: errorMessage(error) }),
  });
  const deleteReview = useMutation({
    mutationFn: () => api.delete(`/rooms/${id}/reviews/mine`),
    onSuccess: () => {
      setEditingReview(false);
      setReviewPage(0);
      setReviewDraft({ rating: 5, content: "" });
      setNotice({ type: "success", text: "Đã xóa đánh giá của bạn." });
      queryClient.invalidateQueries({ queryKey: ["room-reviews", id] });
    },
    onError: (error) => setNotice({ type: "error", text: errorMessage(error) }),
  });

  if (details.isLoading) return <div className="page-state">Đang mở căn phòng…</div>;
  if (details.isError) return <div className="page-state"><h2>Không thể mở căn phòng</h2><p>{errorMessage(details.error)}</p><Link className="button" to="/">Quay lại danh sách</Link></div>;

  const { room, images, comments, owner, favorite } = details.data;
  const galleryImages = images.length ? images : [room.image];
  const activeImage = selectedImage || galleryImages[0];
  const openBooking = () => {
    setNotice(null);
    setBooking({ ...emptyBooking, fullname: profile.data?.fullname || "", email: profile.data?.email || "", tel: profile.data?.tel || "" });
    setBookingOpen(true);
  };

  return <div className="detail-page">
    <Link className="back-link" to={location.state?.from || "/"}><ArrowLeft /> Quay lại danh sách</Link>
    {notice && <div className={`notice ${notice.type}`}>{notice.text}{notice.type === "success" && <Link to="/appointments"> Xem lịch hẹn</Link>}</div>}
    <div className="room-gallery">
      <div className="gallery-main"><img src={activeImage} alt={room.address} /><span>{galleryImages.indexOf(activeImage) + 1} / {galleryImages.length}</span></div>
      {galleryImages.length > 1 && <div className="gallery-thumbnails">{galleryImages.map((image, index) => <button className={activeImage === image ? "active" : ""} key={`${image}-${index}`} onClick={() => setSelectedImage(image)}><img src={image} alt={`Không gian phòng ${index + 1}`} /></button>)}</div>}
    </div>
    <div className="detail-layout">
      <section>
        <div className="detail-heading"><div><span className="eyebrow">{room.roomType}</span><h1>{room.address}</h1></div>
          {user?.role === "Tenant" && <button className={favorite ? "detail-favorite active" : "detail-favorite"} disabled={toggleFavorite.isPending} onClick={() => toggleFavorite.mutate(favorite)}><Heart /> {favorite ? "Đã lưu" : "Lưu phòng"}</button>}
        </div>
        <div className="detail-meta"><span><MapPin /> {room.address}</span><span><Maximize2 /> {room.area} m²</span><span><Users /> Tối đa {room.capacity} người</span></div>
        <div className="description"><h2>Về căn phòng này</h2><p>{room.description}</p></div>
        <div className="reviews-section">
          <div className="review-heading"><div><h2><Star /> Đánh giá phòng</h2><p>Trải nghiệm thực tế từ những người thuê đã được xác nhận.</p></div>{reviews.data && <div className="rating-summary"><strong>{reviews.data.averageRating.toFixed(1)}</strong><span><Stars value={Math.round(reviews.data.averageRating)} /></span><small>{reviews.data.totalElements} đánh giá</small></div>}</div>
          {reviews.isLoading ? <p className="muted">Đang tải đánh giá…</p> : reviews.isError ? <p className="form-error">{errorMessage(reviews.error)}</p> : <>
            {user?.role === "Tenant" && reviews.data.eligible && (!reviews.data.mine || editingReview) && <form className="review-form" onSubmit={(event) => { event.preventDefault(); setNotice(null); saveReview.mutate(); }}>
              <strong>{reviews.data.mine ? "Chỉnh sửa đánh giá" : "Chia sẻ trải nghiệm của bạn"}</strong>
              <div className="rating-input" aria-label={`${reviewDraft.rating} sao`}>{[1, 2, 3, 4, 5].map((value) => <button type="button" key={value} className={value <= reviewDraft.rating ? "active" : ""} onClick={() => setReviewDraft({ ...reviewDraft, rating: value })} aria-label={`${value} sao`}><Star /></button>)}</div>
              <textarea required maxLength="1000" value={reviewDraft.content} onChange={(event) => setReviewDraft({ ...reviewDraft, content: event.target.value })} placeholder="Phòng ở thực tế thế nào? Chủ nhà hỗ trợ ra sao?" />
              <div className="review-form-actions">{reviews.data.mine && <button type="button" className="button secondary" onClick={() => setEditingReview(false)}>Hủy</button>}<button className="button" disabled={saveReview.isPending}>{saveReview.isPending ? "Đang lưu…" : "Lưu đánh giá"}</button></div>
            </form>}
            {user?.role === "Tenant" && !reviews.data.eligible && !reviews.data.mine && <p className="review-eligibility">Bạn có thể đánh giá sau khi chủ nhà xác nhận lịch xem phòng.</p>}
            <div className="review-list">{reviews.data.content.length === 0 ? <p className="muted">Chưa có đánh giá nào cho căn phòng này.</p> : reviews.data.content.map((review) => <article key={review.id}>
              {review.avatar ? <img src={review.avatar} alt="" /> : <span className="comment-avatar">{review.author?.[0]?.toUpperCase()}</span>}
              <div><div className="review-author"><strong>{review.author}{review.mine && <small>Đánh giá của bạn</small>}</strong><Stars value={review.rating} /></div><time>{formatReviewDate(review.updatedAt)}</time><p>{review.content}</p>{review.mine && !editingReview && <div className="review-actions"><button onClick={() => { setReviewDraft({ rating: review.rating, content: review.content }); setEditingReview(true); }}><Pencil /> Sửa</button><button className="danger" disabled={deleteReview.isPending} onClick={() => deleteReview.mutate()}><Trash2 /> Xóa</button></div>}</div>
            </article>)}</div>
            {reviews.data.totalPages > 1 && <div className="pagination"><button disabled={reviewPage === 0} onClick={() => setReviewPage((value) => value - 1)}><ChevronLeft /> Trước</button><span>Trang {reviewPage + 1}/{reviews.data.totalPages}</span><button disabled={reviewPage + 1 >= reviews.data.totalPages} onClick={() => setReviewPage((value) => value + 1)}>Sau <ChevronRight /></button></div>}
          </>}
        </div>
        <div className="comments">
          <h2><MessageCircle /> Trao đổi ({comments.length})</h2>
          {user ? <form onSubmit={(event) => { event.preventDefault(); setCommentError(""); addComment.mutate(); }}>
            <textarea value={content} maxLength="255" onChange={(event) => setContent(event.target.value)} placeholder="Bạn muốn hỏi điều gì?" required />
            <button className="button" disabled={addComment.isPending}>{addComment.isPending ? "Đang gửi…" : "Gửi bình luận"}</button>
            {commentError && <p className="form-error">{commentError}</p>}
          </form> : <p><Link to="/login" state={{ from: location.pathname }}>Đăng nhập</Link> để đặt câu hỏi cho chủ nhà.</p>}
          <div className="comment-list">{comments.length === 0 && <p className="muted">Chưa có câu hỏi nào cho căn phòng này.</p>}{comments.map((comment) => <article key={comment.id}>
            {comment.avatar ? <img src={comment.avatar} alt="" /> : <span className="comment-avatar">{comment.username?.[0]?.toUpperCase()}</span>}
            <div><strong>{comment.username}</strong><time>{comment.commentTime}</time><p>{comment.content}</p></div>
          </article>)}</div>
        </div>
      </section>
      <aside className="booking-card">
        <p className="detail-price"><strong>{room.price}</strong> triệu <span>/ tháng</span></p>
        <div className="owner">{owner.avatar ? <img src={owner.avatar} alt="" /> : <span className="comment-avatar">{owner.fullname?.[0]}</span>}<div><small>Chủ phòng</small><strong>{owner.fullname}</strong></div></div>
        <a href={`tel:${owner.tel}`} className="button secondary"><Phone /> {owner.tel}</a>
        {user?.role === "Tenant" ? <button onClick={openBooking} className="button"><CalendarDays /> Đặt lịch xem phòng</button> : !user ? <Link to="/login" state={{ from: location.pathname }} className="button"><CalendarDays /> Đăng nhập để đặt lịch</Link> : null}
        <small className="booking-note">Bạn chưa phải thanh toán ở bước này.</small>
      </aside>
    </div>

    {bookingOpen && <div className="modal-backdrop" onMouseDown={(event) => event.target === event.currentTarget && !book.isPending && setBookingOpen(false)}>
      <section className="modal booking-modal" role="dialog" aria-modal="true" aria-labelledby="booking-title">
        <button className="modal-close" onClick={() => setBookingOpen(false)} disabled={book.isPending} aria-label="Đóng"><X /></button>
        <span className="eyebrow">Yêu cầu xem phòng</span><h2 id="booking-title">Chọn lịch phù hợp</h2><p className="editor-note">Chủ nhà sẽ xem thông tin và xác nhận yêu cầu của bạn.</p>
        <form className="booking-form" onSubmit={(event) => { event.preventDefault(); setNotice(null); book.mutate(); }}>
          <label>Họ tên<input required maxLength="100" value={booking.fullname} onChange={(event) => setBooking({ ...booking, fullname: event.target.value })} /></label>
          <div className="form-row"><label>Email<input type="email" required maxLength="50" value={booking.email} onChange={(event) => setBooking({ ...booking, email: event.target.value })} /></label><label>Số điện thoại<input required maxLength="15" pattern="[0-9+ ]{8,15}" value={booking.tel} onChange={(event) => setBooking({ ...booking, tel: event.target.value })} /></label></div>
          <div className="form-row"><label>Ngày xem<input type="date" min={today} required value={booking.comeDate} onChange={(event) => setBooking({ ...booking, comeDate: event.target.value })} /></label><label>Số người<input type="number" min="1" max={room.capacity} required value={booking.numPeople} onChange={(event) => setBooking({ ...booking, numPeople: event.target.value })} /></label></div>
          <label>Phương tiện<select required value={booking.transportation} onChange={(event) => setBooking({ ...booking, transportation: event.target.value })}><option>Xe máy</option><option>Ô tô</option><option>Xe đạp</option><option>Phương tiện công cộng</option><option>Đi bộ</option></select></label>
          {notice?.type === "error" && <p className="form-error">{notice.text}</p>}
          <div className="modal-actions"><button type="button" className="button secondary" onClick={() => setBookingOpen(false)} disabled={book.isPending}>Hủy</button><button className="button" disabled={book.isPending}>{book.isPending ? "Đang gửi…" : "Gửi yêu cầu"}</button></div>
        </form>
      </section>
    </div>}
  </div>;
}

function Stars({ value }) {
  return <span className="stars" aria-label={`${value} trên 5 sao`}>{[1, 2, 3, 4, 5].map((star) => <Star key={star} className={star <= value ? "filled" : ""} />)}</span>;
}

function formatReviewDate(value) {
  return new Intl.DateTimeFormat("vi-VN", { dateStyle: "medium" }).format(new Date(value));
}
