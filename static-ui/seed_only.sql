-- Seed data cho new_ui database
-- Chạy sau khi đã có bảng từ new_ui.sql

SET FOREIGN_KEY_CHECKS=0;

-- Xóa data cũ
DELETE FROM bug_labels;
DELETE FROM bug_histories;
DELETE FROM comments;
DELETE FROM attachments;
DELETE FROM bugs;
DELETE FROM workspace_users;
DELETE FROM workspaces;
DELETE FROM labels;
-- Giữ lại members (đã thêm thủ công)
-- DELETE FROM members;

-- Workspaces
INSERT INTO `workspaces` (`id`, `name`, `description`) VALUES 
(1, 'Project CRM NextGen', 'Dự án trọng điểm quý 4: Quản lý khách hàng');

-- Workspace users (roles)
INSERT INTO `workspace_users` (`workspace_id`, `user_id`, `role`) VALUES 
(1, 1, 'Manager'),
(1, 2, 'Developer'),
(1, 3, 'Tester');

-- Labels
INSERT INTO `labels` (`id`, `name`, `color`) VALUES 
(1, 'Bug', '#ff0000'),
(2, 'UI/UX', '#00ff00'),
(3, 'Urgent', '#ff00ff');

-- Bugs
INSERT INTO `bugs` (`id`, `workspace_id`, `title`, `description`, `priority`, `status`, `reporter_id`, `assignee_id`) VALUES 
(1, 1, 'Gãy layout màn hình Login', 'Khi xem trên điện thoại, nút Đăng nhập bị méo', 'High', 'Open', 3, 2),
(2, 1, 'Không xuất được báo cáo PDF', 'Bấm xuất PDF bị màn hình trắng', 'Medium', 'In Progress', 3, 2);

-- Bug labels
INSERT INTO `bug_labels` (`bug_id`, `label_id`) VALUES 
(1, 1), (1, 2), 
(2, 1), (2, 3);

-- Bug histories
INSERT INTO `bug_histories` (`bug_id`, `updated_by`, `old_status`, `new_status`, `change_log`) VALUES 
(2, 3, 'Open', 'In Progress', 'Bắt đầu xử lý');

-- Comments
INSERT INTO `comments` (`bug_id`, `user_id`, `text`) VALUES 
(1, 1, 'Lỗi này có vẻ gấp, Dev Nguyen ưu tiên sửa nhé!'),
(1, 2, 'Vâng sếp, em xử lý ngay đây ạ.');

SET FOREIGN_KEY_CHECKS=1;
