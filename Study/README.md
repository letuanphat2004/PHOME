# PHOME

PHOME là ứng dụng tìm và quản lý phòng trọ với React và Spring Boot REST API.

## Kiến trúc

```text
Study/
├── frontend/                         # React, Vite, React Router, TanStack Query
├── src/main/java/com/example/Study/
│   ├── Config/                       # Security, CORS, Cloudinary
│   ├── Controller/Api/               # REST API /api/v1
│   ├── entity/                       # JPA entities
│   ├── Respository/                  # Spring Data repositories
│   └── Service/                      # Nghiệp vụ
└── src/main/resources/static/        # Kết quả npm run build, không commit
```

React là giao diện duy nhất. Spring Boot phục vụ bản React đã build tại `/` và cung cấp dữ liệu qua `/api/v1/**`. Xác thực dùng session cookie và CSRF token.

## Biến môi trường

Thiết lập các biến trong `.env.example`. Ví dụ PowerShell:

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your-password"
$env:MAIL_USERNAME = "your-account@gmail.com"
$env:MAIL_PASSWORD = "your-app-password"
$env:CLOUDINARY_CLOUD_NAME = "your-cloud-name"
$env:CLOUDINARY_API_KEY = "your-api-key"
$env:CLOUDINARY_API_SECRET = "your-api-secret"
```

Không lưu mật khẩu thật trong Git. Môi trường production bắt buộc cung cấp `DB_URL`,
`DB_USERNAME` và `DB_PASSWORD`. Khi chạy sau HTTPS, đặt `SESSION_COOKIE_SECURE=true`.

Các khóa từng được lưu trực tiếp trong mã nguồn cần được đổi tại MySQL, Gmail và Cloudinary.

## Chạy phát triển

Terminal backend:

```powershell
.\mvnw.cmd spring-boot:run
```

Terminal frontend:

```powershell
cd frontend
npm install
npm run dev
```

Mở `http://localhost:5173`. Vite chuyển tiếp `/api` tới backend tại `http://localhost:8080`.

## Chạy production trên một cổng

```powershell
cd frontend
npm run build
cd ..
.\mvnw.cmd spring-boot:run
```

Mở `http://localhost:8080`.

Health check của ứng dụng có tại `http://localhost:8080/actuator/health`.

## Chạy bằng Docker

Tạo file `.env` từ `.env.example`, đặt ít nhất `MYSQL_ROOT_PASSWORD`, sau đó chạy:

```powershell
docker compose up --build -d
docker compose ps
```

Compose khởi động MySQL 8.4, chờ database sẵn sàng rồi chạy PHOME với profile `prod`.
Dữ liệu MySQL và ảnh tải lên được lưu trong các volume `phome_mysql_data` và `phome_uploads`.
Để triển khai sau reverse proxy HTTPS,
đặt `SESSION_COOKIE_SECURE=true` và không công khai trực tiếp cổng MySQL.

Pipeline `.github/workflows/ci.yml` tự động chạy lint, build React và kiểm thử backend trên mỗi push hoặc pull request.

Nếu chưa cấu hình MySQL, có thể chạy thử với H2 tạm:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=demo"
```

Tài khoản mẫu dùng chung mật khẩu `Demo1234`: `tenant`, `landlord`, `admin`.

## Flyway

Flyway quản lý schema MySQL qua các migration trong `src/main/resources/db/migration`.
Migration `V1__initialize_schema.sql` tạo các bảng ban đầu và ba vai trò `Tenant`,
`Landlord`, `Admin`. Với database `Study` cũ, Flyway tự baseline ở phiên bản `0`
rồi áp dụng migration; Hibernate chỉ kiểm tra schema bằng `ddl-auto: validate`.

Mỗi lần thay đổi database, tạo file mới theo thứ tự, ví dụ:

```text
V2__add_room_status.sql
V3__create_payment_table.sql
```

Không sửa migration đã chạy trên database dùng chung.

## API

- `/api/v1/auth`: đăng nhập, đăng xuất, đăng ký, CSRF và khôi phục mật khẩu
- `/api/v1/profile`: hồ sơ, ảnh đại diện và đổi mật khẩu
- `/api/v1/rooms`: tìm kiếm, chi tiết phòng và bình luận
- `/api/v1/rooms/{id}/reviews`: điểm trung bình và đánh giá đã xác thực của người thuê
- `/api/v1/favorites`: lưu và quản lý phòng yêu thích của người thuê
- `/api/v1/appointments`: đặt và quản lý lịch xem phòng
- `/api/v1/notifications`: danh sách, số lượng chưa đọc và trạng thái đã đọc của thông báo
- `/api/v1/reports`: gửi báo cáo vi phạm cho tin phòng hoặc đánh giá
- `/api/v1/landlord/dashboard`: số liệu tổng quan dành cho chủ nhà
- `/api/v1/landlord/rooms`: đăng, chỉnh sửa và quản lý phòng của chủ nhà
- `/api/v1/admin`: dashboard, kiểm duyệt phòng, xử lý báo cáo và quản lý trạng thái người dùng

## Kiểm tra

```powershell
.\mvnw.cmd test
cd frontend
npm run lint
npm run build
```

Test backend dùng H2 trong bộ nhớ, không cần MySQL thật.
