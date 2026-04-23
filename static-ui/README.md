# Static UI – Hệ thống quản lý Bug (HTML + CSS + Bootstrap + JS)

Giao diện mô phỏng **hệ thống quản lý bug** cho nhóm phát triển phần mềm (Jira clone). Mock data + localStorage, không gọi API.

## Cấu trúc

- `sign-in.html`: Đăng nhập
- `sign-up.html`: Đăng ký (có chọn Role: Admin, Manager, Developer, Tester)
- `dashboard.html`: Dashboard + thống kê bug (theo trạng thái, developer, priority) + danh sách workspaces
- `workspace.html`: Board Kanban 4 cột (Open, In Progress, Resolved, Closed) + CRUD bug + Comment + Upload file
- `assets/styles.css`: CSS bổ sung
- `assets/app.js`: Logic mock (localStorage, bug CRUD, assignee, comment, thống kê)

## Chức năng (phù hợp đề tài)

- **Quản lý người dùng**: Đăng ký, đăng nhập, phân quyền (Admin, Manager, Developer, Tester)
- **Quản lý Bug**: ID, Title, Description, Priority, Status, Reporter, Assignee, Create date
- **Trạng thái bug** (như Jira): Open, In Progress, Resolved, Closed
- **Phân công bug**: Gán/đổi assignee
- **Comment bug**: Thảo luận trên từng bug
- **Upload file**: Đính kèm file (mock)
- **Dashboard**: Số bug theo trạng thái, theo developer, theo priority

## Cách chạy

- Mở trực tiếp file `static-ui/sign-in.html` bằng trình duyệt, hoặc
- Dùng một static server (khuyến nghị để tránh một số hạn chế khi điều hướng file):

```bash
npx serve "d:\cnpm\jira-nextjs-hono-main\jira-nextjs-hono-main\static-ui"
```

Sau đó mở trang theo URL server in ra.

