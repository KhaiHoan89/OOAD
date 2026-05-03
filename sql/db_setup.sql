-- 1. Tạo bảng Users (Nếu chưa có)
-- 2. Tạo bảng appointments (Nếu chưa có)
-- 3. Tạo bảng appointment_participants (Để gộp nhóm)

-- Xóa dữ liệu cũ để tránh lỗi Primary Key khi chạy lại
DELETE FROM appointment_participants;
DELETE FROM appointments;
DELETE FROM Users;

-- Nạp 5 User mẫu (Identity ON/OFF tùy DB của bạn)
SET IDENTITY_INSERT Users ON;
INSERT INTO Users (user_id, user_name, email, password) VALUES
                                                            (1, N'Lý Nguyễn', 'ly@gmail.com', '123'),
                                                            (2, N'Nguyễn Khải Hoàn', 'hoan@gmail.com', '123'),
                                                            (3, N'Đoàn Tiến Đạt', 'dat@gmail.com', '123'),
                                                            (4, N'Nguyễn Thái Ngọc Thảo', 'thao@gmail.com', '123'),
                                                            (5, N'Nguyễn Thị Thuỳ Linh', 'linh@gmail.com', '123');
SET IDENTITY_INSERT Users OFF;

-- Tạo hiện trường mẫu cho Hoàn gộp nhóm
SET IDENTITY_INSERT appointments ON;
INSERT INTO appointments (app_id, title, start_time, end_time, user_id) VALUES
    (101, N'Họp nhóm đồ án', '2026-05-05 08:00:00', '2026-05-05 09:00:00', 3);
SET IDENTITY_INSERT appointments OFF;

INSERT INTO appointment_participants (app_id, user_id) VALUES (101, 3);