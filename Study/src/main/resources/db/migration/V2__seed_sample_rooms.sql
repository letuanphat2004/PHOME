INSERT INTO user (
    deleted, created_at, updated_at, username, password, fullname, tel, email, role_id, link_avatar
)
SELECT
    b'0', NOW(6), NOW(6), 'landlord_demo',
    '$2a$10$Z7lmmQARu3g5.bYBaRC3WO.IccL/KsVxpXBPgq/z0tF6HtARzfn..',
    'Nguyễn Minh Anh', '0901000002', 'landlord@phome.vn',
    (SELECT role_id FROM role WHERE role_name = 'Landlord' LIMIT 1),
    'https://images.unsplash.com/photo-1560250097-0b93528c311a?auto=format&fit=crop&w=300&q=80'
WHERE NOT EXISTS (
    SELECT 1 FROM user WHERE username = 'landlord_demo'
);

INSERT INTO room (
    deleted, created_at, updated_at, user_id, address, capacity, price,
    description, room_type, area, is_approval, image
)
SELECT
    b'0', NOW(6), NOW(6), u.id,
    '42 Nguyễn Thị Minh Khai, Quận 3, TP.HCM', 2, 3.2,
    'Phòng nhiều ánh sáng, có ban công riêng và đầy đủ nội thất cơ bản.',
    'KHONG_CHUNG_CHU', 28, 'true',
    'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267d?auto=format&fit=crop&w=1200&q=85'
FROM user u
WHERE u.username = 'landlord_demo'
  AND NOT EXISTS (
      SELECT 1 FROM room WHERE address = '42 Nguyễn Thị Minh Khai, Quận 3, TP.HCM'
  );

INSERT INTO room (
    deleted, created_at, updated_at, user_id, address, capacity, price,
    description, room_type, area, is_approval, image
)
SELECT
    b'0', NOW(6), NOW(6), u.id,
    '18 Phan Văn Trị, Bình Thạnh, TP.HCM', 2, 2.6,
    'Khu dân cư yên tĩnh, gần trường đại học và các tuyến xe buýt chính.',
    'CHUNG_CHU', 24, 'true',
    'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=1200&q=85'
FROM user u
WHERE u.username = 'landlord_demo'
  AND NOT EXISTS (
      SELECT 1 FROM room WHERE address = '18 Phan Văn Trị, Bình Thạnh, TP.HCM'
  );

INSERT INTO room (
    deleted, created_at, updated_at, user_id, address, capacity, price,
    description, room_type, area, is_approval, image
)
SELECT
    b'0', NOW(6), NOW(6), u.id,
    '75 Lê Văn Sỹ, Phú Nhuận, TP.HCM', 2, 4.1,
    'Studio rộng rãi với khu bếp tách biệt, cửa sổ lớn và chỗ để xe an toàn.',
    'KHONG_CHUNG_CHU', 35, 'true',
    'https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?auto=format&fit=crop&w=1200&q=85'
FROM user u
WHERE u.username = 'landlord_demo'
  AND NOT EXISTS (
      SELECT 1 FROM room WHERE address = '75 Lê Văn Sỹ, Phú Nhuận, TP.HCM'
  );

INSERT INTO image (room_id, url)
SELECT r.id, r.image
FROM room r
WHERE r.address IN (
    '42 Nguyễn Thị Minh Khai, Quận 3, TP.HCM',
    '18 Phan Văn Trị, Bình Thạnh, TP.HCM',
    '75 Lê Văn Sỹ, Phú Nhuận, TP.HCM'
)
AND NOT EXISTS (
    SELECT 1 FROM image i WHERE i.room_id = r.id AND i.url = r.image
);
