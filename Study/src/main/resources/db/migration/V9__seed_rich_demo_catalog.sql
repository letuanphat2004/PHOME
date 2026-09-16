-- The three demo accounts use the shared password: Demo1234
INSERT INTO user (
    deleted, created_at, updated_at, created_by, username, password,
    fullname, tel, email, role_id, link_avatar
)
SELECT b'0', NOW(6), NOW(6), 'flyway-v9', 'landlord_demo',
       '$2a$10$Z7lmmQARu3g5.bYBaRC3WO.IccL/KsVxpXBPgq/z0tF6HtARzfn..',
       'Nguyễn Minh Anh', '0901000002', 'landlord@phome.vn', r.role_id,
       'https://images.unsplash.com/photo-1560250097-0b93528c311a?auto=format&fit=crop&w=300&q=80'
FROM role r
WHERE r.role_name = 'Landlord'
  AND NOT EXISTS (SELECT 1 FROM user WHERE username = 'landlord_demo');

INSERT INTO user (
    deleted, created_at, updated_at, created_by, username, password,
    fullname, tel, email, role_id, link_avatar
)
SELECT b'0', NOW(6), NOW(6), 'flyway-v9', 'tenant_demo',
       '$2a$10$Z7lmmQARu3g5.bYBaRC3WO.IccL/KsVxpXBPgq/z0tF6HtARzfn..',
       'Trần Thu Hà', '0901000001', 'tenant@phome.vn', r.role_id,
       'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=300&q=80'
FROM role r
WHERE r.role_name = 'Tenant'
  AND NOT EXISTS (SELECT 1 FROM user WHERE username = 'tenant_demo');

INSERT INTO user (
    deleted, created_at, updated_at, created_by, username, password,
    fullname, tel, email, role_id, link_avatar
)
SELECT b'0', NOW(6), NOW(6), 'flyway-v9', 'admin_demo',
       '$2a$10$Z7lmmQARu3g5.bYBaRC3WO.IccL/KsVxpXBPgq/z0tF6HtARzfn..',
       'Quản trị viên PHOME', '0901000000', 'admin@phome.vn', r.role_id,
       'https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?auto=format&fit=crop&w=300&q=80'
FROM role r
WHERE r.role_name = 'Admin'
  AND NOT EXISTS (SELECT 1 FROM user WHERE username = 'admin_demo');

INSERT INTO room (
    deleted, created_at, updated_at, created_by, updated_by, user_id,
    address, capacity, price, description, room_type, area,
    is_approval, moderation_note, image
)
SELECT
    b'0', DATE_SUB(NOW(6), INTERVAL seed.age_days DAY), NOW(6),
    'flyway-v9', 'flyway-v9', owner.id, seed.address, seed.capacity,
    seed.price, seed.description, seed.room_type, seed.area,
    seed.approval, seed.moderation_note, seed.image
FROM user owner
CROSS JOIN (
    SELECT 1 age_days, '12 Nguyễn Hữu Cảnh, Bình Thạnh, TP.HCM' address, 2 capacity, 4.8 price, 32 area,
           'KHONG_CHUNG_CHU' room_type, 'true' approval, NULL moderation_note,
           'Căn studio có ban công, bếp riêng, máy lạnh và cửa sổ lớn nhìn về trung tâm thành phố.' description,
           'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267d?auto=format&fit=crop&w=1200&q=85' image
    UNION ALL SELECT 2, '86 Điện Biên Phủ, Quận 1, TP.HCM', 2, 5.9, 35, 'KHONG_CHUNG_CHU', 'true', NULL,
           'Phòng mới hoàn thiện, đầy đủ giường tủ, bàn làm việc, máy giặt riêng và khóa vân tay.',
           'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=1200&q=85'
    UNION ALL SELECT 3, '25 Nguyễn Gia Trí, Bình Thạnh, TP.HCM', 3, 4.2, 30, 'CHUNG_CHU', 'true', NULL,
           'Không gian thoáng, gần Đại học HUTECH và UEF, phù hợp sinh viên hoặc người đi làm.',
           'https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?auto=format&fit=crop&w=1200&q=85'
    UNION ALL SELECT 4, '119 Hoàng Hoa Thám, Bình Thạnh, TP.HCM', 2, 3.6, 27, 'KHONG_CHUNG_CHU', 'true', NULL,
           'Hẻm xe hơi yên tĩnh, có gác rộng, khu bếp riêng và chỗ để xe miễn phí.',
           'https://images.unsplash.com/photo-1560185008-b033106af5c3?auto=format&fit=crop&w=1200&q=85'
    UNION ALL SELECT 5, '44 Phan Xích Long, Phú Nhuận, TP.HCM', 2, 5.2, 34, 'KHONG_CHUNG_CHU', 'true', NULL,
           'Studio nội thất hiện đại, thang máy, máy giặt riêng và khu vực sinh hoạt nhiều tiện ích.',
           'https://images.unsplash.com/photo-1560185127-6ed189bf02f4?auto=format&fit=crop&w=1200&q=85'
    UNION ALL SELECT 6, '210 Lê Văn Sỹ, Quận 3, TP.HCM', 2, 4.5, 29, 'CHUNG_CHU', 'true', NULL,
           'Phòng sạch sẽ trong nhà nguyên căn, giờ giấc tự do, có sân thượng và bếp dùng chung.',
           'https://images.unsplash.com/photo-1560185127-6a8c0f0c6a07?auto=format&fit=crop&w=1200&q=85'
    UNION ALL SELECT 7, '31 Trường Sa, Phú Nhuận, TP.HCM', 2, 4.9, 31, 'KHONG_CHUNG_CHU', 'true', NULL,
           'Ban công hướng kênh, nhiều ánh sáng tự nhiên, trang bị máy lạnh và tủ lạnh.',
           'https://images.unsplash.com/photo-1554995207-c18c203602cb?auto=format&fit=crop&w=1200&q=85'
    UNION ALL SELECT 8, '72 Bạch Đằng, Tân Bình, TP.HCM', 3, 3.8, 30, 'KHONG_CHUNG_CHU', 'true', NULL,
           'Gần sân bay, phòng có gác và cửa sổ, khu dân cư an ninh với camera 24 giờ.',
           'https://images.unsplash.com/photo-1493663284031-b7e3aefcae8e?auto=format&fit=crop&w=1200&q=85'
    UNION ALL SELECT 9, '15 Hồng Hà, Tân Bình, TP.HCM', 2, 5.5, 36, 'KHONG_CHUNG_CHU', 'true', NULL,
           'Căn hộ dịch vụ một phòng ngủ, có khu bếp, bàn ăn và dịch vụ vệ sinh hàng tuần.',
           'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=1200&q=85'
    UNION ALL SELECT 10, '98 Cộng Hòa, Tân Bình, TP.HCM', 4, 4.6, 38, 'CHUNG_CHU', 'true', NULL,
           'Phòng rộng phù hợp nhóm bạn, có hai cửa sổ, chỗ nấu ăn và bãi xe trong nhà.',
           'https://images.unsplash.com/photo-1524758631624-e2822e304c36?auto=format&fit=crop&w=1200&q=85'
    UNION ALL SELECT 11, '63 Nguyễn Sơn, Tân Phú, TP.HCM', 2, 3.1, 25, 'KHONG_CHUNG_CHU', 'true', NULL,
           'Phòng mới sơn, có gác, máy lạnh và nhà vệ sinh riêng, gần chợ và siêu thị.',
           'https://images.unsplash.com/photo-1540518614846-7eded433c457?auto=format&fit=crop&w=1200&q=85'
    UNION ALL SELECT 12, '140 Lũy Bán Bích, Tân Phú, TP.HCM', 3, 3.7, 32, 'CHUNG_CHU', 'true', NULL,
           'Nhà có sân để xe rộng, phòng thoáng mát, chủ nhà thân thiện và khu phố yên tĩnh.',
           'https://images.unsplash.com/photo-1501183638710-841dd1904471?auto=format&fit=crop&w=1200&q=85'
    UNION ALL SELECT 13, '28 Đường số 8, Gò Vấp, TP.HCM', 2, 3.3, 26, 'KHONG_CHUNG_CHU', 'true', NULL,
           'Phòng khép kín có gác lửng, máy lạnh, nước nóng và lối đi riêng.',
           'https://images.unsplash.com/photo-1564078516393-cf04bd966897?auto=format&fit=crop&w=1200&q=85'
    UNION ALL SELECT 14, '167 Phan Văn Trị, Gò Vấp, TP.HCM', 2, 4.0, 30, 'KHONG_CHUNG_CHU', 'true', NULL,
           'Căn studio gần Emart, nội thất cơ bản, thang máy và khu giặt phơi trên sân thượng.',
           'https://images.unsplash.com/photo-1567767292278-a4f21aa2d36e?auto=format&fit=crop&w=1200&q=85'
    UNION ALL SELECT 15, '52 Nguyễn Oanh, Gò Vấp, TP.HCM', 4, 4.4, 40, 'CHUNG_CHU', 'true', NULL,
           'Phòng lớn có hai giường, phù hợp gia đình nhỏ hoặc nhóm sinh viên, điện nước riêng.',
           'https://images.unsplash.com/photo-1560184897-ae75f418493e?auto=format&fit=crop&w=1200&q=85'
    UNION ALL SELECT 16, '91 Võ Văn Ngân, Thủ Đức, TP.HCM', 2, 3.0, 24, 'KHONG_CHUNG_CHU', 'true', NULL,
           'Cách Vincom Thủ Đức vài phút, phòng có gác, bếp và khu để xe có bảo vệ.',
           'https://images.unsplash.com/photo-1529408632839-a54952c491e5?auto=format&fit=crop&w=1200&q=85'
    UNION ALL SELECT 17, '34 Đường số 5, Linh Chiểu, Thủ Đức, TP.HCM', 2, 2.8, 23, 'CHUNG_CHU', 'true', NULL,
           'Phòng dành cho sinh viên, gần làng đại học, có bàn học và khu nấu ăn chung.',
           'https://images.unsplash.com/photo-1595526114035-0d45ed16cfbf?auto=format&fit=crop&w=1200&q=85'
    UNION ALL SELECT 18, '120 Tô Ngọc Vân, Thủ Đức, TP.HCM', 3, 3.9, 33, 'KHONG_CHUNG_CHU', 'true', NULL,
           'Phòng rộng có ban công, bếp tách biệt, khu vực không ngập và thuận tiện đi lại.',
           'https://images.unsplash.com/photo-1560185007-c5ca9d2c014d?auto=format&fit=crop&w=1200&q=85'
    UNION ALL SELECT 19, '66 Đường 17, Hiệp Bình Chánh, Thủ Đức, TP.HCM', 4, 5.0, 45, 'KHONG_CHUNG_CHU', 'true', NULL,
           'Căn hộ hai phòng ngủ nhỏ, phù hợp gia đình, có ban công và chỗ đậu xe rộng.',
           'https://images.unsplash.com/photo-1520608421741-68228b76b6df?auto=format&fit=crop&w=1200&q=85'
    UNION ALL SELECT 20, '19 Nguyễn Thị Thập, Quận 7, TP.HCM', 2, 5.8, 38, 'KHONG_CHUNG_CHU', 'true', NULL,
           'Căn hộ dịch vụ gần Crescent Mall, nội thất đầy đủ, bảo vệ và lễ tân 24 giờ.',
           'https://images.unsplash.com/photo-1560185127-6ed189bf02f4?auto=format&fit=crop&w=1200&q=85'
    UNION ALL SELECT 21, '88 Lâm Văn Bền, Quận 7, TP.HCM', 3, 4.3, 35, 'CHUNG_CHU', 'true', NULL,
           'Phòng trong nhà nguyên căn, không gian sinh hoạt chung rộng và có sân phơi riêng.',
           'https://images.unsplash.com/photo-1560448075-bb485b067938?auto=format&fit=crop&w=1200&q=85'
    UNION ALL SELECT 22, '41 Đường số 10, Quận 7, TP.HCM', 2, 4.7, 31, 'KHONG_CHUNG_CHU', 'true', NULL,
           'Studio yên tĩnh, có cửa sổ lớn, máy lạnh, tủ quần áo và bếp điện.',
           'https://images.unsplash.com/photo-1560448204-603b3fc33ddc?auto=format&fit=crop&w=1200&q=85'
    UNION ALL SELECT 23, '77 Dương Bá Trạc, Quận 8, TP.HCM', 3, 3.2, 29, 'KHONG_CHUNG_CHU', 'true', NULL,
           'Phòng có gác cao, ban công nhỏ, gần cầu Nguyễn Văn Cừ và các trường đại học.',
           'https://images.unsplash.com/photo-1556020685-ae41abfc9365?auto=format&fit=crop&w=1200&q=85'
    UNION ALL SELECT 24, '130 Cao Lỗ, Quận 8, TP.HCM', 2, 2.7, 22, 'CHUNG_CHU', 'true', NULL,
           'Phòng giá tốt, vệ sinh riêng, có khu giặt phơi và chỗ để xe trong sân.',
           'https://images.unsplash.com/photo-1536376072261-38c75010e6c9?auto=format&fit=crop&w=1200&q=85'
    UNION ALL SELECT 25, '55 Song Hành, Quận 2, TP.HCM', 2, 6.2, 40, 'KHONG_CHUNG_CHU', 'true', NULL,
           'Căn hộ dịch vụ cao cấp, nội thất gỗ, ban công rộng và khu vực làm việc riêng.',
           'https://images.unsplash.com/photo-1600210492486-724fe5c67fb0?auto=format&fit=crop&w=1200&q=85'
    UNION ALL SELECT 26, '24 Nguyễn Văn Quá, Quận 12, TP.HCM', 3, 2.9, 28, 'KHONG_CHUNG_CHU', 'false', NULL,
           'Phòng mới có gác, cửa sổ và khu bếp, đang chờ quản trị viên duyệt thông tin.',
           'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?auto=format&fit=crop&w=1200&q=85'
    UNION ALL SELECT 27, '83 Lê Đức Thọ, Gò Vấp, TP.HCM', 2, 3.5, 27, 'CHUNG_CHU', 'false', NULL,
           'Phòng trong nhà nguyên căn có nội thất cơ bản, đang chờ kiểm duyệt để đăng công khai.',
           'https://images.unsplash.com/photo-1616486338812-3dadae4b4ace?auto=format&fit=crop&w=1200&q=85'
    UNION ALL SELECT 28, '16 Đường số 4, Bình Tân, TP.HCM', 4, 3.4, 36, 'KHONG_CHUNG_CHU', 'false', NULL,
           'Phòng rộng cho gia đình nhỏ, có gác và sân để xe, đang chờ phê duyệt.',
           'https://images.unsplash.com/photo-1598928506311-c55ded91a20c?auto=format&fit=crop&w=1200&q=85'
    UNION ALL SELECT 29, '101 Quốc lộ 13, Bình Thạnh, TP.HCM', 2, 4.1, 28, 'KHONG_CHUNG_CHU', 'false', NULL,
           'Studio gần bến xe, đầy đủ tiện nghi cơ bản và đang trong quá trình kiểm duyệt.',
           'https://images.unsplash.com/photo-1600566753086-00f18fb6b3ea?auto=format&fit=crop&w=1200&q=85'
    UNION ALL SELECT 30, '39 Đường số 9, Bình Chánh, TP.HCM', 5, 2.5, 42, 'KHONG_CHUNG_CHU', 'rejected',
           'Cần bổ sung ảnh thật và địa chỉ chi tiết trước khi gửi duyệt lại.',
           'Phòng diện tích lớn nhưng thông tin tiện ích và hình ảnh hiện chưa đầy đủ.',
           'https://images.unsplash.com/photo-1600607687920-4e2a09cf159d?auto=format&fit=crop&w=1200&q=85'
    UNION ALL SELECT 31, '62 Đường số 2, Nhà Bè, TP.HCM', 3, 2.6, 30, 'CHUNG_CHU', 'rejected',
           'Mô tả giá điện nước chưa rõ ràng; vui lòng cập nhật trước khi đăng lại.',
           'Phòng thoáng trong khu dân cư, gần chợ và trạm xe buýt.',
           'https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=1200&q=85'
    UNION ALL SELECT 32, '47 Võ Chí Công, Thủ Đức, TP.HCM', 2, 5.1, 34, 'KHONG_CHUNG_CHU', 'true', NULL,
           'Studio mới gần khu công nghệ cao, có ban công, máy giặt riêng và bãi xe an toàn.',
           'https://images.unsplash.com/photo-1600566753190-17f0baa2a6c3?auto=format&fit=crop&w=1200&q=85'
) seed
WHERE owner.username = 'landlord_demo'
  AND NOT EXISTS (SELECT 1 FROM room existing WHERE existing.address = seed.address);

INSERT INTO image (room_id, url)
SELECT r.id, r.image
FROM room r
WHERE r.created_by = 'flyway-v9'
  AND NOT EXISTS (
      SELECT 1 FROM image existing
      WHERE existing.room_id = r.id AND existing.url = r.image
  );

INSERT INTO image (room_id, url)
SELECT r.id,
       CASE MOD(r.id, 4)
           WHEN 0 THEN 'https://images.unsplash.com/photo-1600566753051-f0b89df2dd90?auto=format&fit=crop&w=1200&q=85'
           WHEN 1 THEN 'https://images.unsplash.com/photo-1600210491892-03d54c0aaf87?auto=format&fit=crop&w=1200&q=85'
           WHEN 2 THEN 'https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?auto=format&fit=crop&w=1200&q=85'
           ELSE 'https://images.unsplash.com/photo-1600585154526-990dced4db0d?auto=format&fit=crop&w=1200&q=85'
       END
FROM room r
WHERE r.created_by = 'flyway-v9';

INSERT INTO favorite (user_id, room_id, created_at)
SELECT tenant.id, room.id, NOW(6)
FROM user tenant
JOIN room ON room.created_by = 'flyway-v9' AND room.is_approval = 'true'
WHERE tenant.username = 'tenant_demo'
  AND MOD(room.id, 5) = 0
  AND NOT EXISTS (
      SELECT 1 FROM favorite existing
      WHERE existing.user_id = tenant.id AND existing.room_id = room.id
  );
