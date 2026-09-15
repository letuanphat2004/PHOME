import { ArrowUpRight, Heart, MapPin, Maximize2, Users } from "lucide-react";
import { Link, useLocation } from "react-router-dom";

export default function RoomCard({ room, detailEnabled = true, favorite = false, canFavorite = false, onToggleFavorite, favoritePending = false }) {
  const location = useLocation();
  const linkState = { from: `${location.pathname}${location.search}` };
  const image = (
    <>
      <img
        className="room-image"
        src={room.image || "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267d?auto=format&fit=crop&w=1000&q=80"}
        alt={room.address}
      />
      <span className="room-type">{room.roomType}</span>
    </>
  );
  return (
    <article className="room-card">
      {canFavorite && <button className={favorite ? "card-favorite active" : "card-favorite"} type="button" aria-label={favorite ? "Bỏ khỏi yêu thích" : "Thêm vào yêu thích"} title={favorite ? "Bỏ khỏi yêu thích" : "Thêm vào yêu thích"} disabled={favoritePending} onClick={onToggleFavorite}><Heart /></button>}
      {detailEnabled ? <Link className="room-image-wrap" to={`/rooms/${room.room_id}`} state={linkState}>{image}</Link> : <div className="room-image-wrap">{image}</div>}
      <div className="room-card-body">
        <div className="room-price">
          <strong>{room.price}</strong> triệu <span>/ tháng</span>
        </div>
        <p className="room-address">
          <MapPin size={16} />
          {room.address}
        </p>
        <div className="room-meta">
          <span>
            <Maximize2 />
            {room.area} m²
          </span>
          <span>
            <Users />
            Tối đa {room.capacity}
          </span>
        </div>
        {detailEnabled && <Link className="room-link" to={`/rooms/${room.room_id}`} state={linkState}>Xem căn phòng <ArrowUpRight /></Link>}
      </div>
    </article>
  );
}
