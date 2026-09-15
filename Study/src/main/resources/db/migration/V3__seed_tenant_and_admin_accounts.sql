INSERT INTO user (
    deleted, created_at, updated_at, username, password, fullname, tel, email, role_id, link_avatar
)
SELECT
    b'0', NOW(6), NOW(6), 'tenant_demo',
    '$2a$10$Z7lmmQARu3g5.bYBaRC3WO.IccL/KsVxpXBPgq/z0tF6HtARzfn..',
    'Trần Thu Hà', '0901000001', 'tenant@phome.vn',
    (SELECT role_id FROM role WHERE role_name = 'Tenant' LIMIT 1),
    NULL
WHERE NOT EXISTS (
    SELECT 1 FROM user WHERE username = 'tenant_demo'
);

INSERT INTO user (
    deleted, created_at, updated_at, username, password, fullname, tel, email, role_id, link_avatar
)
SELECT
    b'0', NOW(6), NOW(6), 'admin_demo',
    '$2a$10$Z7lmmQARu3g5.bYBaRC3WO.IccL/KsVxpXBPgq/z0tF6HtARzfn..',
    'Quản trị viên PHOME', '0901000000', 'admin@phome.vn',
    (SELECT role_id FROM role WHERE role_name = 'Admin' LIMIT 1),
    NULL
WHERE NOT EXISTS (
    SELECT 1 FROM user WHERE username = 'admin_demo'
);
