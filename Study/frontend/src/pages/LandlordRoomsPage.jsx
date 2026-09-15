import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { CheckCircle2, Clock3, ImagePlus, Pencil, Plus, RefreshCw, Trash2, X } from "lucide-react";
import { useState } from "react";
import { Navigate } from "react-router-dom";
import { api, errorMessage } from "../api/client";
import { useAuth } from "../auth/AuthContext";
import RoomCard from "../components/RoomCard";

const queryRooms = (approved) =>
  api.get("/landlord/rooms", { params: { approved } }).then((response) => response.data);

export default function LandlordRoomsPage() {
  const { user, isLoading: authLoading } = useAuth();
  const queryClient = useQueryClient();
  const [tab, setTab] = useState("true");
  const [editor, setEditor] = useState(null);
  const [notice, setNotice] = useState(null);
  const approvedQuery = useQuery({ queryKey: ["my-rooms", "true"], queryFn: () => queryRooms("true"), enabled: user?.role === "Landlord" });
  const pendingQuery = useQuery({ queryKey: ["my-rooms", "false"], queryFn: () => queryRooms("false"), enabled: user?.role === "Landlord" });
  const currentQuery = tab === "true" ? approvedQuery : pendingQuery;

  const loadEditor = useMutation({
    mutationFn: (id) => api.get(`/landlord/rooms/${id}`).then((response) => response.data),
    onSuccess: (data) => setEditor(data),
    onError: (error) => setNotice({ type: "error", text: errorMessage(error) }),
  });
  const remove = useMutation({
    mutationFn: (id) => api.delete(`/landlord/rooms/${id}`),
    onSuccess: () => {
      setNotice({ type: "success", text: "Đã xóa phòng và các lịch hẹn liên quan." });
      queryClient.invalidateQueries({ queryKey: ["my-rooms"] });
    },
    onError: (error) => setNotice({ type: "error", text: errorMessage(error) }),
  });

  if (authLoading) return <div className="page-state">Đang tải tài khoản…</div>;
  if (user?.role !== "Landlord") return <Navigate to="/account" replace />;

  const openNew = () => { setNotice(null); setEditor({ room: null, images: [] }); };
  const openEdit = (id) => { setNotice(null); loadEditor.mutate(id); };
  const deleteRoom = (room) => {
    if (window.confirm(`Xóa phòng tại “${room.address}”? Các lịch hẹn của phòng cũng sẽ bị xóa.`)) {
      setNotice(null);
      remove.mutate(room.room_id);
    }
  };

  return (
    <div className="dashboard-page landlord-dashboard">
      <div className="dashboard-title row">
        <div><span className="eyebrow">Trung tâm chủ nhà</span><h1>Phòng của tôi</h1><p>Đăng phòng, cập nhật thông tin và theo dõi trạng thái xét duyệt.</p></div>
        <button className="button" onClick={openNew}><Plus /> Đăng phòng mới</button>
      </div>
      <div className="landlord-stats" aria-label="Tổng quan phòng">
        <article><CheckCircle2 /><div><strong>{approvedQuery.data?.length ?? 0}</strong><span>Đang hiển thị</span></div></article>
        <article><Clock3 /><div><strong>{pendingQuery.data?.length ?? 0}</strong><span>Chờ xét duyệt</span></div></article>
      </div>
      {notice && <div className={`notice ${notice.type}`}>{notice.text}</div>}
      {loadEditor.isPending && <div className="notice">Đang tải thông tin phòng…</div>}
      <div className="tabs" role="tablist" aria-label="Trạng thái phòng">
        <button className={tab === "true" ? "active" : ""} onClick={() => setTab("true")}>Đang hiển thị</button>
        <button className={tab === "false" ? "active" : ""} onClick={() => setTab("false")}>Chờ duyệt</button>
      </div>
      {currentQuery.isLoading ? (
        <div className="state-card">Đang tải danh sách phòng…</div>
      ) : currentQuery.isError ? (
        <div className="state-card error-state"><p>{errorMessage(currentQuery.error)}</p><button className="button secondary" onClick={() => currentQuery.refetch()}><RefreshCw /> Thử lại</button></div>
      ) : currentQuery.data.length === 0 ? (
        <div className="empty empty-card">
          {tab === "true" ? <CheckCircle2 /> : <Clock3 />}
          <h2>{tab === "true" ? "Chưa có phòng đang hiển thị" : "Không có phòng chờ duyệt"}</h2>
          <p>{tab === "true" ? "Đăng phòng đầu tiên để bắt đầu tiếp cận người thuê." : "Các phòng mới hoặc vừa chỉnh sửa sẽ xuất hiện tại đây."}</p>
          {tab === "true" && <button className="button" onClick={openNew}><Plus /> Đăng phòng</button>}
        </div>
      ) : (
        <div className="room-grid management">
          {currentQuery.data.map((room) => (
            <div className="managed-room" key={room.room_id}>
              <div className="managed-status">{tab === "true" ? "Đang hiển thị" : "Chờ duyệt"}</div>
              <RoomCard room={room} detailEnabled={tab === "true"} />
              <div className="room-actions">
                <button onClick={() => openEdit(room.room_id)} disabled={loadEditor.isPending}><Pencil /> Chỉnh sửa</button>
                <button onClick={() => deleteRoom(room)} disabled={remove.isPending}><Trash2 /> Xóa</button>
              </div>
            </div>
          ))}
        </div>
      )}
      {editor && <RoomEditor initial={editor} onClose={() => setEditor(null)} onSaved={(editing) => {
        setEditor(null); setTab("false");
        setNotice({ type: "success", text: editing ? "Đã lưu thay đổi và gửi phòng xét duyệt lại." : "Đã đăng phòng và gửi xét duyệt." });
        queryClient.invalidateQueries({ queryKey: ["my-rooms"] });
      }} />}
    </div>
  );
}

function RoomEditor({ initial, onClose, onSaved }) {
  const room = initial.room;
  const editing = Boolean(room);
  const [deletedImages, setDeletedImages] = useState([]);
  const [files, setFiles] = useState([]);
  const [error, setError] = useState("");
  const save = useMutation({
    mutationFn: (form) => editing ? api.put(`/landlord/rooms/${room.room_id}`, form) : api.post("/landlord/rooms", form),
    onSuccess: () => onSaved(editing),
    onError: (requestError) => setError(errorMessage(requestError)),
  });
  const remainingImages = initial.images.filter((image) => !deletedImages.includes(image.id));
  const totalImages = remainingImages.length + files.length;
  const selectFiles = (event) => {
    const selected = Array.from(event.target.files ?? []);
    const allowedTypes = ["image/jpeg", "image/png", "image/webp", "image/gif"];
    if (selected.some((file) => !allowedTypes.includes(file.type) || file.size > 8 * 1024 * 1024)) {
      setError("Ảnh phải là JPG, PNG, WebP hoặc GIF và không vượt quá 8 MB."); event.target.value = ""; return;
    }
    if (remainingImages.length + selected.length > 8) {
      setError("Mỗi phòng được phép có tối đa 8 ảnh."); event.target.value = ""; return;
    }
    setError(""); setFiles(selected);
  };
  const submit = (event) => {
    event.preventDefault(); setError("");
    if (totalImages === 0) { setError("Phòng cần có ít nhất một hình ảnh."); return; }
    const form = new FormData(event.currentTarget);
    form.delete("images"); files.forEach((file) => form.append("images", file));
    deletedImages.forEach((id) => form.append("imageIdsDel", id));
    save.mutate(form);
  };

  return (
    <div className="modal-backdrop" role="presentation" onMouseDown={(event) => event.target === event.currentTarget && !save.isPending && onClose()}>
      <section className="modal room-editor" role="dialog" aria-modal="true" aria-labelledby="room-editor-title">
        <button className="modal-close" type="button" onClick={onClose} disabled={save.isPending} aria-label="Đóng"><X /></button>
        <span className="eyebrow">{editing ? "Cập nhật thông tin" : "Không gian mới"}</span>
        <h2 id="room-editor-title">{editing ? "Chỉnh sửa phòng" : "Đăng phòng mới"}</h2>
        {editing && <p className="editor-note">Sau khi lưu, phòng sẽ chuyển sang trạng thái chờ quản trị viên xét duyệt lại.</p>}
        <form onSubmit={submit}>
          <label>Địa chỉ<input name="address" defaultValue={room?.address} maxLength="255" placeholder="Số nhà, đường, quận/huyện, tỉnh/thành" required /></label>
          <div className="form-row">
            <label>Giá (triệu/tháng)<input name="price" type="number" step="0.1" min="1" defaultValue={room?.price} required /></label>
            <label>Diện tích (m²)<input name="area" type="number" step="0.1" min="1" defaultValue={room?.area} required /></label>
          </div>
          <div className="form-row">
            <label>Sức chứa<input name="capacity" type="number" min="1" defaultValue={room?.capacity} required /></label>
            <label>Loại phòng<select name="roomType" defaultValue={room?.roomType === "Chung chủ" ? "CHUNG_CHU" : "KHONG_CHUNG_CHU"}><option value="KHONG_CHUNG_CHU">Không chung chủ</option><option value="CHUNG_CHU">Chung chủ</option></select></label>
          </div>
          <label>Mô tả<textarea name="description" defaultValue={room?.description} maxLength="5000" placeholder="Mô tả tiện nghi, vị trí và quy định của phòng" required /></label>
          {initial.images.length > 0 && <fieldset className="image-manager">
            <legend>Ảnh hiện tại <span>Chọn ảnh muốn xóa</span></legend>
            <div className="existing-images">{initial.images.map((image) => {
              const removed = deletedImages.includes(image.id);
              return <button type="button" className={removed ? "removed" : ""} key={image.id} onClick={() => setDeletedImages((ids) => removed ? ids.filter((id) => id !== image.id) : [...ids, image.id])}><img src={image.url} alt="Ảnh phòng" /><span>{removed ? "Hoàn tác" : "Xóa ảnh"}</span></button>;
            })}</div>
          </fieldset>}
          <label className="file-picker"><ImagePlus /><span><strong>{editing ? "Thêm ảnh mới" : "Chọn ảnh phòng"}</strong><small>JPG, PNG, WebP hoặc GIF · tối đa 8 MB/ảnh</small></span><input name="images" type="file" accept="image/jpeg,image/png,image/webp,image/gif" multiple onChange={selectFiles} /></label>
          {files.length > 0 && <p className="selected-files">Đã chọn {files.length} ảnh: {files.map((file) => file.name).join(", ")}</p>}
          {error && <p className="form-error" role="alert">{error}</p>}
          <div className="modal-actions"><button className="button secondary" type="button" onClick={onClose} disabled={save.isPending}>Hủy</button><button className="button" disabled={save.isPending}>{save.isPending ? "Đang lưu…" : editing ? "Lưu và gửi duyệt" : "Đăng và gửi duyệt"}</button></div>
        </form>
      </section>
    </div>
  );
}
