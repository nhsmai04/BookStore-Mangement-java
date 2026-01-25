# 📚 Library Management System

Library Management System là hệ thống backend quản lý thư viện được xây dựng bằng **Spring Boot**, hỗ trợ quản lý sách, người dùng, mượn/trả sách, tìm kiếm, thống kê và gửi email thông báo quá hạn.

---

## 🎯 Mục tiêu dự án

- Số hóa quy trình quản lý thư viện
- Giảm thao tác thủ công trong việc mượn/trả sách
- Hỗ trợ quản lý nhiều bản sao của cùng một đầu sách
- Cung cấp API cho các hệ thống frontend hoặc ứng dụng khác

---

## 🏗️ Kiến trúc hệ thống

- Mô hình: **MVC Architecture**
- Kiểu ứng dụng: **RESTful API**
- Backend only (không bao gồm frontend)

---

## ⚙️ Công nghệ sử dụng

- **Java 17**
- **Spring Boot**
- **Spring Data JPA**
- **Spring Security**
- **MySQL**
- **Maven**
- **Apache POI** (Import Excel)
- **iText / OpenPDF** (Xuất PDF)
- **Java Mail Sender** (Gửi email)

---

## 🗂️ Chức năng chính

### 1. Quản lý sách
- Thêm / sửa / xóa sách
- Quản lý tác giả, nhà xuất bản, thể loại
- Upload ảnh bìa sách
- Import danh sách sách từ file Excel

### 2. Quản lý Book Version
- Một sách có nhiều bản sao (BookVersion)
- Theo dõi trạng thái:
  - `AVAILABLE`
  - `BORROWED`
  - `LOST`
- Tự động cập nhật trạng thái khi mượn/trả

### 3. Quản lý người dùng
- Đăng ký / đăng nhập
- Phân quyền:
  - `ADMIN`
  - `USER`
- Bảo mật API bằng Spring Security

### 4. Mượn & trả sách
- Mượn sách online thông qua API
- Mượn sách bằng phiếu mượn (PDF)
- Theo dõi Borrow Request & Borrow Request Item

### 5. Tìm kiếm & lọc
- Tìm theo:
  - Tên sách
  - Tác giả
  - Nhà xuất bản
- Phân trang kết quả

### 6. Thông báo quá hạn
- Tự động kiểm tra sách quá hạn
- Gửi email nhắc nhở người dùng

### 7. Thống kê & báo cáo
- Số lượt mượn theo thời gian
- Sách được mượn nhiều nhất
- Xuất báo cáo dạng PDF

---

## 🗄️ Thiết kế cơ sở dữ liệu (Entities)

- Book
- Author
- Publisher
- BookVersion
- User
- BorrowRequest
- BorrowRequestItem

---

## 🔧 Cài đặt & chạy project

### 1. Clone repository

```bash
git clone https://github.com/your-username/library-management-system.git
cd library-management-system
```

### 2. Cấu hình database
Tạo database MySQL:
```bash
CREATE DATABASE library_management;
``````
Cập nhật file application.properties:
```bash
spring.datasource.url=jdbc:mysql://localhost:3306/library_management
spring.datasource.username=root
spring.datasource.password=your_password
``````
JPA config:
```bash
spring.jpa.hibernate.ddl-auto=updatespring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.thymeleaf.prefix=classpath:/templates/spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.htmlspring.thymeleaf.suffix=.html
```
Web config:
```bash
spring.mvc.hiddenmethod.filter.enabled=true
spring.flyway.baseline-on-migrate=true
```
Mail config
---bash
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=yourmail@gmail.com
spring.mail.password=uvvd sfhv miem grsf
spring.mail.protocol=smtp
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```
