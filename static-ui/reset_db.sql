-- =====================================================
-- RESET & SEED SCRIPT cho database new_ui
-- Password cho tất cả user: secret123
-- BCrypt hash ($2a$12$) tương thích Spring Security
-- =====================================================

SET FOREIGN_KEY_CHECKS=0;

-- Xóa toàn bộ data cũ
TRUNCATE TABLE attachments;
TRUNCATE TABLE comments;
TRUNCATE TABLE bug_labels;
TRUNCATE TABLE bug_histories;
TRUNCATE TABLE bugs;
TRUNCATE TABLE workspace_users;
TRUNCATE TABLE workspaces;
TRUNCATE TABLE labels;
TRUNCATE TABLE members;
TRUNCATE TABLE activity_logs;
DELETE FROM users;
ALTER TABLE users AUTO_INCREMENT = 1;

SET FOREIGN_KEY_CHECKS=1;

-- =====================================================
-- USERS (password = "secret123", BCrypt $2a$12$)
-- Hash này được tạo bởi Spring BCryptPasswordEncoder
-- =====================================================
INSERT INTO `users` (`id`, `email`, `password`) VALUES
(1, 'manager@test.com', '$2a$12$CeSXYXvTQIc1P.IWfACuDeXNDnv/lQqr.6FRG.GCLLQR.xk61U3wi'),
(2, 'dev@test.com',     '$2a$12$CeSXYXvTQIc1P.IWfACuDeXNDnv/lQqr.6FRG.GCLLQR.xk61U3wi'),
(3, 'tester@test.com',  '$2a$12$CeSXYXvTQIc1P.IWfACuDeXNDnv/lQqr.6FRG.GCLLQR.xk61U3wi');

-- =====================================================
-- MEMBERS (thông tin profile + role)
-- =====================================================
INSERT INTO `members` (`user_id`, `full_name`, `avatar`, `phone`, `role`) VALUES
(1, 'Manager Tran', NULL, '0987654321', 'Manager'),
(2, 'Dev Nguyen',   NULL, '0912345678', 'Developer'),
(3, 'Tester Pham',  NULL, '0900000000', 'Tester');

-- =====================================================
-- WORKSPACES
-- =====================================================
INSERT INTO `workspaces` (`id`, `name`, `description`) VALUES
(1, 'Project CRM NextGen', 'Dự án trọng điểm quý 4: Quản lý khách hàng');

-- =====================================================
-- WORKSPACE USERS (roles trong workspace)
-- =====================================================
INSERT INTO `workspace_users` (`workspace_id`, `user_id`, `role`) VALUES
(1, 1, 'Manager'),
(1, 2, 'Developer'),
(1, 3, 'Tester');

-- =====================================================
-- LABELS
-- =====================================================
INSERT INTO `labels` (`id`, `name`, `color`) VALUES
(1, 'Bug',    '#ff4444'),
(2, 'UI/UX',  '#44aa44'),
(3, 'Urgent', '#aa44aa');

-- =====================================================
-- BUGS
-- =====================================================
INSERT INTO `bugs` (`id`, `workspace_id`, `title`, `description`, `priority`, `status`, `reporter_id`, `assignee_id`) VALUES
(1, 1, 'Gãy layout màn hình Login',      'Khi xem trên điện thoại, nút Đăng nhập bị méo', 'High',   'Open',        3, 2),
(2, 1, 'Không xuất được báo cáo PDF',    'Bấm xuất PDF bị màn hình trắng',                'Medium', 'In Progress', 3, 2),
(3, 1, 'Search không trả kết quả đúng',  'Tìm kiếm bug theo keyword không chính xác',     'Medium', 'Open',        1, 2),
(4, 1, 'Lỗi 500 khi tạo workspace mới', 'NullPointerException khi name trống',            'High',   'Resolved',    2, 2);

-- =====================================================
-- BUG LABELS
-- =====================================================
INSERT INTO `bug_labels` (`bug_id`, `label_id`) VALUES
(1, 1), (1, 2),
(2, 1), (2, 3),
(3, 2),
(4, 1), (4, 3);

-- =====================================================
-- BUG HISTORIES
-- =====================================================
INSERT INTO `bug_histories` (`bug_id`, `updated_by`, `old_status`, `new_status`, `change_log`) VALUES
(2, 3, 'Open',      'In Progress', 'Bắt đầu xử lý'),
(4, 2, 'Open',      'In Progress', 'Dev đang kiểm tra'),
(4, 2, 'In Progress', 'Resolved',  'Đã fix NullPointerException');

-- =====================================================
-- COMMENTS
-- =====================================================
INSERT INTO `comments` (`bug_id`, `user_id`, `text`) VALUES
(1, 1, 'Lỗi này có vẻ gấp, Dev Nguyen ưu tiên sửa nhé!'),
(1, 2, 'Vâng sếp, em xử lý ngay đây ạ.'),
(2, 3, 'Lỗi xảy ra ở tất cả trình duyệt, cần check lại API export.'),
(4, 1, 'Kiểm tra kỹ input validation trước khi merge.');

-- Verify
SELECT 'users' as tbl, COUNT(*) cnt FROM users
UNION ALL SELECT 'members', COUNT(*) FROM members
UNION ALL SELECT 'workspaces', COUNT(*) FROM workspaces
UNION ALL SELECT 'workspace_users', COUNT(*) FROM workspace_users
UNION ALL SELECT 'bugs', COUNT(*) FROM bugs
UNION ALL SELECT 'bug_histories', COUNT(*) FROM bug_histories
UNION ALL SELECT 'comments', COUNT(*) FROM comments
UNION ALL SELECT 'labels', COUNT(*) FROM labels;
