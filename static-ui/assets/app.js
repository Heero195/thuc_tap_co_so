/* Jira Clone Frontend integrated with Spring Boot Backend */

const API_BASE = "http://localhost:8080/api";
const STORAGE_KEYS = {
  token: "staticui.token",
  userId: "staticui.userId",
  userName: "staticui.userName",
  userEmail: "staticui.userEmail",
  userRole: "staticui.userRole",
  currentWorkspaceId: "staticui.currentWorkspaceId",
};

const BUG_STATUS = { open: "Open", inprogress: "In Progress", resolved: "Resolved", closed: "Closed" };
const BUG_PRIORITY = { low: "Low", medium: "Medium", high: "High" };
const ROLES = { manager: "manager", developer: "developer", tester: "tester" };

/**
 * Shared fetch helper with Authorization header and error handling
 */
async function apiFetch(endpoint, options = {}) {
  const token = localStorage.getItem(STORAGE_KEYS.token);
  const headers = {
    "Content-Type": "application/json",
    ...options.headers,
  };
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  try {
    const res = await fetch(`${API_BASE}${endpoint}`, { ...options, headers });
    
    if (res.status === 401) {
      localStorage.removeItem(STORAGE_KEYS.token);
      window.location.href = "sign-in.html";
      throw new Error("Phiên đăng nhập hết hạn, vui lòng đăng nhập lại.");
    }

    if (!res.ok) {
      const errorText = await res.text();
      const message = parseApiErrorBody(errorText);
      throw new Error(message || `Lỗi hệ thống (${res.status})`);
    }

    if (res.status === 204) return null;

    const contentType = res.headers.get("content-type");
    if (contentType && contentType.includes("application/json")) {
      return res.json();
    }
    return res.text();
  } catch (err) {
    if (err.message.includes("Failed to fetch")) {
      throw new Error("Không thể kết nối đến máy chủ. Vui lòng kiểm tra backend.");
    }
    throw err;
  }
}


function uid(prefix = "ws") {
  return `${prefix}_${Math.random().toString(16).slice(2)}_${Date.now().toString(16)}`;
}

function initials(name) {
  const clean = String(name || "").trim();
  if (!clean) return "?";
  const parts = clean.split(/\s+/).slice(0, 2);
  return parts.map((p) => p[0]?.toUpperCase() || "").join("");
}

function loadJson(key, fallback) {
  try {
    const raw = localStorage.getItem(key);
    return raw ? JSON.parse(raw) : fallback;
  } catch {
    return fallback;
  }
}

function saveJson(key, value) {
  localStorage.setItem(key, JSON.stringify(value));
}


async function getWorkspaces() {
  return apiFetch("/workspaces");
}

async function getBugs(workspaceId) {
  if (!workspaceId) return [];
  return apiFetch(`/workspaces/${workspaceId}/bugs`);
}

async function getBugDetails(bugId) {
  return apiFetch(`/bugs/${bugId}`);
}

async function getBugHistory(bugId) {
  return apiFetch(`/bugs/${bugId}/history`);
}

async function getBugComments(bugId) {
  return apiFetch(`/bugs/${bugId}/comments`);
}

async function getBugAttachments(bugId) {
  return apiFetch(`/bugs/${bugId}/attachments`);
}

async function addBugComment(bugId, text) {
  return apiFetch(`/bugs/${bugId}/comments`, {
    method: "POST",
    body: JSON.stringify({ text }),
  });
}

async function uploadBugAttachment(bugId, file) {
  const token = localStorage.getItem(STORAGE_KEYS.token);
  const fd = new FormData();
  fd.append("file", file);

  const res = await fetch(`${API_BASE}/bugs/${bugId}/attachments`, {
    method: "POST",
    headers: {
      "Authorization": `Bearer ${token}`
    },
    body: fd
  });

  if (!res.ok) throw new Error("Upload failed");
  return res.json();
}

async function deleteBugAttachment(attachmentId) {
  return apiFetch(`/attachments/${attachmentId}`, {
    method: "DELETE",
  });
}

async function getWorkspaceMembers(workspaceId) {
  return apiFetch(`/workspaces/${workspaceId}/members`);
}


function getCurrentWorkspaceId() {
  return localStorage.getItem(STORAGE_KEYS.currentWorkspaceId);
}

function setCurrentWorkspaceId(id) {
  if (id) {
    localStorage.setItem(STORAGE_KEYS.currentWorkspaceId, id);
  } else {
    localStorage.removeItem(STORAGE_KEYS.currentWorkspaceId);
  }
}

function getUser() {
  return {
    id: localStorage.getItem(STORAGE_KEYS.userId),
    name: localStorage.getItem(STORAGE_KEYS.userName) || "Demo User",
    email: localStorage.getItem(STORAGE_KEYS.userEmail) || "demo@local.test",
    role: localStorage.getItem(STORAGE_KEYS.userRole) || ROLES.developer,
  };
}


function showToast({ title, message, variant = "primary" }) {
  const container = document.getElementById("toastContainer");
  if (!container) return;

  const toastId = uid("toast");
  const el = document.createElement("div");
  el.className = `toast align-items-center text-bg-${variant} border-0`;
  el.setAttribute("role", "alert");
  el.setAttribute("aria-live", "assertive");
  el.setAttribute("aria-atomic", "true");
  el.id = toastId;

  el.innerHTML = `
    <div class="d-flex">
      <div class="toast-body">
        <div class="fw-semibold">${escapeHtml(title || "Thông báo")}</div>
        <div class="small opacity-75">${escapeHtml(message || "")}</div>
      </div>
      <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
    </div>
  `;

  container.appendChild(el);
  const toast = bootstrap.Toast.getOrCreateInstance(el, { delay: 2600 });
  toast.show();
  el.addEventListener("hidden.bs.toast", () => el.remove());
}

function escapeHtml(str) {
  return String(str)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

function renderUser() {
  const user = getUser();
  const nameEls = document.querySelectorAll("[data-user-name]");
  const emailEls = document.querySelectorAll("[data-user-email]");
  const avatarEls = document.querySelectorAll("[data-user-avatar]");
  const roleEls = document.querySelectorAll("[data-user-role]");

  nameEls.forEach((el) => (el.textContent = user.name));
  emailEls.forEach((el) => (el.textContent = user.email));
  avatarEls.forEach((el) => (el.textContent = initials(user.name)));
  roleEls.forEach((el) => (el.textContent = (user.role || "").charAt(0).toUpperCase() + (user.role || "").slice(1)));
}

async function renderWorkspaces() {
  const listEls = [
    document.getElementById("workspaceList"),
    document.getElementById("workspaceListDesktop"),
    document.getElementById("workspaceListMobile"),
  ].filter(Boolean);
  const menuEl = document.getElementById("workspaceMenu");
  const currentNameEl = document.getElementById("currentWorkspaceName");
  const currentAvatarEl = document.getElementById("currentWorkspaceAvatar");

  if (!listEls.length && !menuEl && !currentNameEl) return;

  try {
    const workspaces = await getWorkspaces();
    let currentId = getCurrentWorkspaceId();
    
    // Validate if currentId exists in fetched workspaces
    if (!currentId || !workspaces.some((w) => String(w.id) === String(currentId))) {
      currentId = workspaces[0]?.id || null;
      if (currentId) setCurrentWorkspaceId(currentId);
    }

    const current = workspaces.find((w) => String(w.id) === String(currentId)) || workspaces[0];
    if (currentNameEl) currentNameEl.textContent = current?.name || "Workspace";
    if (currentAvatarEl) currentAvatarEl.textContent = initials(current?.name);

    if (listEls.length) {
      const html = workspaces
        .map((w) => {
          const active = String(w.id) === String(currentId) ? "active" : "";
          return `
            <a class="nav-link d-flex align-items-center gap-2 ${active}" href="workspace.html" data-select-workspace="${w.id}">
              <span class="workspace-avatar">${escapeHtml(initials(w.name))}</span>
              <span class="flex-grow-1 text-truncate">${escapeHtml(w.name)}</span>
            </a>
          `;
        })
        .join("");
      listEls.forEach((el) => (el.innerHTML = html));
    }

    if (menuEl) {
      menuEl.innerHTML = workspaces
        .map((w) => {
          const active = String(w.id) === String(currentId) ? "active" : "";
          return `
            <li>
              <a class="dropdown-item d-flex align-items-center gap-2 ${active}" href="#" data-select-workspace="${w.id}">
                <span class="workspace-avatar">${escapeHtml(initials(w.name))}</span>
                <span class="text-truncate">${escapeHtml(w.name)}</span>
              </a>
            </li>
          `;
        })
        .join("");
    }
  } catch (err) {
    console.error("Failed to render workspaces:", err);
  }
}

function wireWorkspaceSelection() {
  document.addEventListener("click", async (e) => {
    const target = e.target?.closest?.("[data-select-workspace]");
    if (!target) return;
    const id = target.getAttribute("data-select-workspace");
    if (!id) return;
    
    setCurrentWorkspaceId(id);
    
    // Re-render everything that depends on workspace
    await renderWorkspaces();
    await renderWorkspacePageHeader();
    if (typeof renderKanbanBoard === "function") await renderKanbanBoard();
    if (typeof renderDashboardStats === "function") await renderDashboardStats();
    
    showToast({ title: "Workspace", message: "Đã chuyển workspace." });

    // close dropdown if clicked inside
    const dropdown = target.closest(".dropdown-menu");
    if (dropdown) {
      const toggle = dropdown.parentElement?.querySelector?.('[data-bs-toggle="dropdown"]');
      if (toggle) bootstrap.Dropdown.getOrCreateInstance(toggle).hide();
    }
    e.preventDefault();
  });
}


const API_AUTH_BASE = "http://localhost:8080/api/auth";

function parseApiErrorBody(text) {
  if (!text) return "";
  try {
    const j = JSON.parse(text);
    if (j && typeof j.message === "string") return j.message;
  } catch (_) {
    /* not JSON */
  }
  return text;
}

function authNetworkHint() {
  return "Bật backend Spring Boot (port 8080), bật MySQL nếu cần. Thử mở UI bằng Live Server (http://localhost:5500) thay vì file://.";
}

function wireAuthForms() {
  const signInForm = document.getElementById("signInForm");
  const signUpForm = document.getElementById("signUpForm");

  if (signInForm) {
    signInForm.addEventListener("submit", async (e) => {
      e.preventDefault();
      if (signInForm.dataset.busy === "1") return;
      const fd = new FormData(signInForm);
      const email = String(fd.get("email") || "").trim();
      const password = String(fd.get("password") || "");

      if (!email || password.length < 6) {
        showToast({ title: "Không hợp lệ", message: "Vui lòng nhập email và mật khẩu (>= 6 ký tự).", variant: "danger" });
        return;
      }

      signInForm.dataset.busy = "1";
      const submitBtn = signInForm.querySelector('[type="submit"]');
      if (submitBtn) submitBtn.disabled = true;

      try {
        const res = await fetch(`${API_AUTH_BASE}/login`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ email, password }),
        });

        if (!res.ok) {
           const errText = await res.text();
           showToast({
             title: "Lỗi đăng nhập",
             message: parseApiErrorBody(errText) || "Sai email hoặc mật khẩu.",
             variant: "danger",
           });
           return;
        }

        const data = await res.json();
        const { token, user } = data;
        
        localStorage.setItem("staticui.token", token);
        localStorage.setItem(STORAGE_KEYS.userId, user.id);
        localStorage.setItem(STORAGE_KEYS.userName, user.name);
        localStorage.setItem(STORAGE_KEYS.userEmail, user.email);
        localStorage.setItem(STORAGE_KEYS.userRole, user.role);

        showToast({ title: "Đăng nhập", message: "Thành công. Đang chuyển trang..." , variant: "success" });
        setTimeout(() => (window.location.href = "dashboard.html"), 450);
      } catch (err) {
        showToast({
          title: "Không kết nối được backend",
          message: authNetworkHint(),
          variant: "danger",
        });
      } finally {
        signInForm.dataset.busy = "0";
        if (submitBtn) submitBtn.disabled = false;
      }
    });
  }

  if (signUpForm) {
    signUpForm.addEventListener("submit", async (e) => {
      e.preventDefault();
      if (signUpForm.dataset.busy === "1") return;
      const fd = new FormData(signUpForm);
      const name = String(fd.get("name") || "").trim();
      const email = String(fd.get("email") || "").trim();
      const password = String(fd.get("password") || "");

      if (!name || !email || password.length < 6) {
        showToast({ title: "Không hợp lệ", message: "Vui lòng nhập đủ thông tin (mật khẩu >= 6).", variant: "danger" });
        return;
      }

      const role = String(fd.get("role") || "developer").trim();

      signUpForm.dataset.busy = "1";
      const submitBtn = signUpForm.querySelector('[type="submit"]');
      if (submitBtn) submitBtn.disabled = true;

      try {
         const res = await fetch(`${API_AUTH_BASE}/register`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ name, email, password, role }),
        });

        if (!res.ok) {
           const errText = await res.text();
           showToast({
             title: "Lỗi đăng ký",
             message: parseApiErrorBody(errText) || "Không thể đăng ký tài khoản.",
             variant: "danger",
           });
           return;
        }

        const data = await res.json();
        const { token, user } = data;
        
        localStorage.setItem("staticui.token", token);
        localStorage.setItem(STORAGE_KEYS.userId, user.id);
        localStorage.setItem(STORAGE_KEYS.userName, user.name);
        localStorage.setItem(STORAGE_KEYS.userEmail, user.email);
        localStorage.setItem(STORAGE_KEYS.userRole, user.role);

        showToast({ title: "Tạo tài khoản", message: "Thành công. Đang chuyển trang...", variant: "success" });
        setTimeout(() => (window.location.href = "dashboard.html"), 450);
      } catch (err) {
         showToast({
          title: "Không kết nối được backend",
          message: authNetworkHint(),
          variant: "danger",
        });
      } finally {
        signUpForm.dataset.busy = "0";
        if (submitBtn) submitBtn.disabled = false;
      }
    });
  }
}

async function renderWorkspaceTable() {
  const tbody = document.querySelector("#workspaceTableBody");
  if (!tbody) return;

  try {
    const workspaces = await getWorkspaces();
    const currentId = getCurrentWorkspaceId();
    tbody.innerHTML = workspaces
      .map((w) => {
        const active = String(w.id) === String(currentId);
        return `
          <tr>
            <td class="align-middle">
              <div class="d-flex align-items-center gap-2">
                <span class="workspace-avatar">${escapeHtml(initials(w.name))}</span>
                <div class="d-flex flex-column">
                  <span class="fw-semibold">${escapeHtml(w.name)}</span>
                  <span class="small app-muted">${active ? "Đang chọn" : " "}</span>
                </div>
              </div>
            </td>
            <td class="align-middle app-muted">${new Date(w.createdAt).toLocaleDateString()}</td>
            <td class="align-middle text-end">
              <a class="btn btn-sm btn-soft" href="workspace.html" data-select-workspace="${w.id}">Mở</a>
            </td>
          </tr>
        `;
      })
      .join("");
  } catch (err) {
    console.error("Failed to render workspace table:", err);
  }
}

async function renderWorkspacePageHeader() {
  const titleEl = document.getElementById("workspacePageTitle");
  const subtitleEl = document.getElementById("workspacePageSubtitle");
  if (!titleEl && !subtitleEl) return;

  try {
    const workspaces = await getWorkspaces();
    const currentId = getCurrentWorkspaceId();
    const current = workspaces.find((w) => String(w.id) === String(currentId)) || workspaces[0];

    if (titleEl) titleEl.textContent = current?.name || "Workspace";
    if (subtitleEl) subtitleEl.textContent = "Board Bug • " + (current?.myRole ? `Role: ${current.myRole}` : "Dự án của tôi");
  } catch (err) {
    console.error("Failed to render workspace page header:", err);
  }
}

function wireCreateWorkspaceModal() {
  const form = document.getElementById("createWorkspaceForm");
  if (!form) return;

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    const fd = new FormData(form);
    const name = String(fd.get("name") || "").trim();
    if (!name) {
      showToast({ title: "Thiếu tên", message: "Vui lòng nhập tên workspace.", variant: "danger" });
      return;
    }

    try {
      const created = await apiFetch("/workspaces", {
        method: "POST",
        body: JSON.stringify({ name }),
      });
      
      setCurrentWorkspaceId(created.id);

      await renderWorkspaces();
      await renderWorkspaceTable();
      await renderWorkspacePageHeader();

      showToast({ title: "Workspace", message: "Đã tạo workspace thành công.", variant: "success" });

      const modalEl = document.getElementById("createWorkspaceModal");
      if (modalEl) bootstrap.Modal.getOrCreateInstance(modalEl).hide();
      form.reset();
    } catch (err) {
      showToast({ title: "Lỗi", message: err.message, variant: "danger" });
    }
  });
}


async function renderKanbanBoard(filters = null) {
  const cols = ["kanbanOpen", "kanbanInProgress", "kanbanResolved", "kanbanClosed"];
  const statusLabels = { open: "Open", inprogress: "In Progress", resolved: "Resolved", closed: "Closed" };

  const wsId = getCurrentWorkspaceId();
  if (!wsId) return;

  try {
    let bugs = await getBugs(wsId);

    // Apply filters client-side
    if (filters) {
      const user = getUser();
      bugs = bugs.filter((b) => {
        // Assignee filter
        if (filters.assignee === "Me") {
          if (String(b.assigneeId) !== String(user.id)) return false;
        } else if (filters.assignee === "Team") {
          if (String(b.assigneeId) === String(user.id)) return false;
        }

        // Priority filter
        if (filters.priority && filters.priority !== "All") {
          if (b.priority !== filters.priority) return false;
        }

        // Keyword filter
        if (filters.keyword) {
          const kw = filters.keyword.toLowerCase();
          const title = (b.title || "").toLowerCase();
          const desc = (b.description || "").toLowerCase();
          if (!title.includes(kw) && !desc.includes(kw)) return false;
        }

        return true;
      });
    }

    cols.forEach((id, i) => {
      const el = document.getElementById(id);
      if (!el) return;
      const statusValue = Object.values(statusLabels)[i];
      const columnBugs = bugs.filter((b) => b.status === statusValue);
      
      el.innerHTML = columnBugs
        .map((b) => {
          const prioClass = b.priority === "High" ? "badge-priority-high" : b.priority === "Medium" ? "badge-priority-medium" : "badge-priority-low";
          return `
            <div class="kanban-card cursor-pointer" data-bug-id="${b.id}" data-bs-toggle="modal" data-bs-target="#bugDetailModal" title="Xem chi tiết">
              <div class="d-flex align-items-center justify-content-between gap-2">
                <span class="small app-muted">#${escapeHtml(b.id)}</span>
                <span class="badge ${prioClass}">${escapeHtml((b.priority || "Medium"))}</span>
              </div>
              <div class="fw-semibold mt-1">${escapeHtml(b.title)}</div>
              <div class="small app-muted mt-1 text-truncate" title="${escapeHtml(b.description || "")}">${escapeHtml((b.description || "").slice(0, 50))}${(b.description || "").length > 50 ? "..." : ""}</div>
              <div class="d-flex align-items-center justify-content-between mt-2 small app-muted">
                <span title="Reporter: ${escapeHtml(b.reporterName || "?")}">R: ${escapeHtml(initials(b.reporterName))}</span>
                <span title="Assignee: ${escapeHtml(b.assigneeName || "Chưa gán")}">A: ${initials(b.assigneeName || "—")}</span>
                <span>${new Date(b.createdAt).toLocaleDateString()}</span>
              </div>
            </div>
          `;
        })
        .join("");
    });

    document.querySelectorAll("[data-kanban-count]").forEach((el) => {
      const statusKey = el.getAttribute("data-kanban-count");
      const statusValue = statusLabels[statusKey];
      const count = bugs.filter((b) => b.status === statusValue).length;
      el.textContent = count;
    });
  } catch (err) {
    console.error("Failed to render kanban board:", err);
  }
}

function wireBoardFilters() {
  const btn = document.getElementById("btnApplyFilters");
  if (!btn) return;

  btn.addEventListener("click", () => {
    const filters = {
      assignee: document.getElementById("boardFilterAssignee")?.value,
      priority: document.getElementById("boardFilterPriority")?.value,
      keyword: document.getElementById("boardFilterKeyword")?.value?.trim(),
    };
    renderKanbanBoard(filters);
    showToast({ title: "Filters", message: "Đã áp dụng bộ lọc.", variant: "info" });
  });

  // Also support Enter key on keyword input
  document.getElementById("boardFilterKeyword")?.addEventListener("keypress", (e) => {
    if (e.key === "Enter") btn.click();
  });
}


function wireCreateBugModal() {
  const form = document.getElementById("createBugForm");
  if (!form) return;

  const assigneeSel = form.querySelector('[name="assigneeId"]');
  document.getElementById("createBugModal")?.addEventListener("show.bs.modal", async () => {
    if (!assigneeSel) return;
    const wsId = getCurrentWorkspaceId();
    if (!wsId) return;
    try {
      const members = await getWorkspaceMembers(wsId);
      assigneeSel.innerHTML = '<option value="">-- Chưa gán --</option>' + 
        members.map((m) => `<option value="${m.userId}">${escapeHtml(m.name)}</option>`).join("");
    } catch (err) {
      console.error("Failed to fetch members for bug creation:", err);
    }
  });

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    const fd = new FormData(form);
    const title = String(fd.get("title") || "").trim();
    const description = String(fd.get("description") || "").trim();
    const priority = String(fd.get("priority") || "Medium").trim();
    const status = String(fd.get("status") || "Open").trim();
    const assigneeIdStr = fd.get("assigneeId") || null;
    const assigneeId = assigneeIdStr ? parseInt(assigneeIdStr, 10) : null;

    if (!title) {
      showToast({ title: "Thiếu tiêu đề", message: "Vui lòng nhập tiêu đề bug.", variant: "danger" });
      return;
    }

    const wsId = getCurrentWorkspaceId();
    if (!wsId) return;

    try {
      const created = await apiFetch(`/workspaces/${wsId}/bugs`, {
        method: "POST",
        body: JSON.stringify({ title, description, priority, status, assigneeId }),
      });

      await renderKanbanBoard();
      await renderDashboardStats();

      showToast({ title: "Bug", message: `Đã tạo bug #${created.id} thành công.`, variant: "success" });

      const modalEl = document.getElementById("createBugModal");
      if (modalEl) bootstrap.Modal.getOrCreateInstance(modalEl).hide();
      form.reset();
    } catch (err) {
      showToast({ title: "Lỗi", message: err.message, variant: "danger" });
    }
  });
}


function wireBugDetailModal() {
  document.addEventListener("show.bs.modal", (e) => {
    if (e.target.id !== "bugDetailModal") return;
    const bugId = e.relatedTarget?.getAttribute?.("data-bug-id") || e.target.getAttribute("data-current-bug-id");
    if (bugId) openBugDetailModal(bugId);
  });
}

function canDeleteBug(bug) {
  return bug?.status === BUG_STATUS.resolved || bug?.status === BUG_STATUS.closed;
}

async function openBugDetailModal(bugId) {
  const modal = document.getElementById("bugDetailModal");
  if (!modal) return;

  try {
    // Fetch all data in parallel
    const [bug, history, comments, attachments] = await Promise.all([
      getBugDetails(bugId),
      getBugHistory(bugId),
      getBugComments(bugId),
      getBugAttachments(bugId)
    ]);

    modal.setAttribute("data-current-bug-id", bugId);
    const editBtn = modal.querySelector("[data-action='edit-bug']");
    if (editBtn) editBtn.setAttribute("data-bug-id", bugId);
    const deleteBtn = modal.querySelector("[data-action='delete-bug']");
    if (deleteBtn) {
      deleteBtn.setAttribute("data-bug-id", bugId);
      // Backend handles permission, but we can do a UI hint
      deleteBtn.disabled = false; 
    }

    modal.querySelector("#bugDetailId").textContent = `#${bug.id}`;
    modal.querySelector("#bugDetailTitle").textContent = bug.title;
    modal.querySelector("#bugDetailDescription").textContent = bug.description || "(Không có mô tả)";
    modal.querySelector("#bugDetailStatus").textContent = (bug.status || "");
    modal.querySelector("#bugDetailPriority").textContent = (bug.priority || "Medium");
    modal.querySelector("#bugDetailReporter").textContent = bug.reporterName || "?";
    modal.querySelector("#bugDetailDate").textContent = bug.createdAt ? new Date(bug.createdAt).toLocaleString() : "-";

    const assigneeSelect = modal.querySelector("#bugDetailAssigneeSelect");
    if (assigneeSelect) {
      const wsId = getCurrentWorkspaceId();
      const members = await getWorkspaceMembers(wsId);
      assigneeSelect.innerHTML = '<option value="">-- Chưa gán --</option>' + 
        members.map((m) => `<option value="${m.userId}" ${m.userId === bug.assigneeId ? "selected" : ""}>${escapeHtml(m.name)}</option>`).join("");
      
      assigneeSelect.onchange = async () => {
        const newAssigneeId = assigneeSelect.value ? parseInt(assigneeSelect.value, 10) : null;
        try {
          await apiFetch(`/bugs/${bugId}`, {
            method: "PUT",
            body: JSON.stringify({ title: bug.title, assigneeId: newAssigneeId }),
          });
          await renderKanbanBoard();
          showToast({ title: "Assignee", message: "Đã cập nhật người xử lý.", variant: "success" });
        } catch (err) {
          showToast({ title: "Lỗi", message: err.message, variant: "danger" });
        }
      };
    }

    const historyContainer = modal.querySelector("#bugDetailHistoryTimeline");
    if (historyContainer) {
      historyContainer.innerHTML = history.length === 0 
        ? '<div class="small app-muted">Không có lịch sử.</div>'
        : history.map(h => `
          <div class="position-relative mb-2 ms-2">
            <span class="position-absolute translate-middle p-1 bg-primary border border-light rounded-circle" style="left: -13px; top: 8px;"></span>
            <div class="small fw-semibold">${escapeHtml(h.changeLog || "Updated")}</div>
            <div class="small app-muted">${escapeHtml(h.updatedByName || "?")} - ${new Date(h.createdAt).toLocaleString()}</div>
          </div>
        `).join("");
    }

    const commentsContainer = modal.querySelector("#bugDetailComments");
    if (commentsContainer) {
      commentsContainer.innerHTML = comments.length === 0
        ? '<div class="small app-muted">Chưa có comment.</div>'
        : comments.map((c) => `
            <div class="d-flex gap-2 mb-2">
              <span class="workspace-avatar small">${initials(c.userName)}</span>
              <div>
                <span class="fw-semibold">${escapeHtml(c.userName || "?")}</span>
                <span class="small app-muted ms-2">${new Date(c.createdAt).toLocaleString()}</span>
                <div class="small">${escapeHtml(c.text)}</div>
              </div>
            </div>
          `).join("");
    }

    const addCommentForm = modal.querySelector("#bugDetailAddCommentForm");
    if (addCommentForm) {
      addCommentForm.onsubmit = async (ev) => {
        ev.preventDefault();
        const textArea = addCommentForm.querySelector('textarea[name="commentText"]');
        const text = textArea?.value?.trim();
        if (!text) return;
        try {
          await addBugComment(bugId, text);
          textArea.value = "";
          await openBugDetailModal(bugId);
          showToast({ title: "Comment", message: "Đã thêm comment.", variant: "success" });
        } catch (err) {
          showToast({ title: "Lỗi", message: err.message, variant: "danger" });
        }
      };
    }

    const attachmentsContainer = modal.querySelector("#bugDetailAttachments");
    if (attachmentsContainer) {
      attachmentsContainer.innerHTML = attachments.length === 0
        ? '<div class="small app-muted">Chưa có file đính kèm.</div>'
        : attachments.map(a => `
            <div class="d-flex align-items-center justify-content-between p-2 mb-1 app-card rounded-3">
              <div class="d-flex align-items-center gap-2 overflow-hidden">
                <i class="bi bi-file-earmark text-primary"></i>
                <span class="small text-truncate" title="${escapeHtml(a.name)}">${escapeHtml(a.name)}</span>
              </div>
              <div class="d-flex gap-1">
                <a href="${API_BASE}/attachments/${a.id}/download" class="btn btn-sm btn-soft" title="Tải về"><i class="bi bi-download"></i></a>
                <button class="btn btn-sm btn-soft text-danger" onclick="handleDeleteAttachment(${a.id}, ${bugId})" title="Xóa"><i class="bi bi-trash"></i></button>
              </div>
            </div>
          `).join("");
    }

    const uploadForm = modal.querySelector("#bugDetailUploadForm");
    if (uploadForm) {
      // Render existing attachments if container exists
      // For now, let's just update the upload logic
      uploadForm.onsubmit = async (ev) => {
        ev.preventDefault();
        const fileInput = uploadForm.querySelector('input[type="file"]');
        const file = fileInput?.files?.[0];
        if (!file) {
          showToast({ title: "Lỗi", message: "Chọn file cần upload.", variant: "danger" });
          return;
        }
        if (file.size > 5 * 1024 * 1024) {
          showToast({ title: "Lỗi", message: "File vượt quá giới hạn 5MB.", variant: "danger" });
          return;
        }
        try {
          showToast({ title: "Upload", message: "Đang tải file lên...", variant: "info" });
          await uploadBugAttachment(bugId, file);
          fileInput.value = "";
          await openBugDetailModal(bugId);
          showToast({ title: "Upload", message: `Đã thêm file ${escapeHtml(file.name)} thành công.`, variant: "success" });
        } catch (err) {
          showToast({ title: "Lỗi", message: "Không thể upload file.", variant: "danger" });
        }
      };
    }
  } catch (err) {
    console.error("Failed to open bug details:", err);
    showToast({ title: "Lỗi", message: "Không thể lấy thông tin bug.", variant: "danger" });
  }
}


function wireDeleteBugAction() {
  document.addEventListener("click", async (e) => {
    const btn = e.target.closest("[data-action='delete-bug']");
    if (!btn) return;

    const bugId = btn.getAttribute("data-bug-id");
    if (!bugId) return;

    const ok = window.confirm(`Xóa bug #${bugId}?`);
    if (!ok) return;

    try {
      await apiFetch(`/bugs/${bugId}`, {
        method: "DELETE",
      });
      await renderKanbanBoard();
      await renderDashboardStats();

      bootstrap.Modal.getOrCreateInstance(document.getElementById("bugDetailModal")).hide();
      showToast({ title: "Bug", message: `Đã xóa #${bugId} thành công.`, variant: "success" });
    } catch (err) {
      showToast({ title: "Lỗi", message: err.message || "Không thể xóa bug này.", variant: "danger" });
    }
  });
}


function wireEditBugModal() {
  document.addEventListener("click", async (e) => {
    const btn = e.target.closest("[data-action='edit-bug']");
    if (!btn) return;
    e.preventDefault();
    const bugId = btn.getAttribute("data-bug-id");
    if (!bugId) return;

    try {
      const bug = await getBugDetails(bugId);
      const wsId = getCurrentWorkspaceId();
      const members = await getWorkspaceMembers(wsId);

      const form = document.getElementById("editBugForm");
      if (!form) return;
      form.querySelector('[name="bugId"]').value = bugId;
      form.querySelector('[name="title"]').value = bug.title;
      form.querySelector('[name="description"]').value = bug.description || "";
      form.querySelector('[name="status"]').value = bug.status;
      form.querySelector('[name="priority"]').value = bug.priority || "medium";
      
      const assigneeSel = form.querySelector('[name="assigneeId"]');
      if (assigneeSel) {
        assigneeSel.innerHTML = '<option value="">-- Chưa gán --</option>' + 
          members.map((m) => `<option value="${m.userId}" ${m.userId === bug.assigneeId ? "selected" : ""}>${escapeHtml(m.name)}</option>`).join("");
      }

      bootstrap.Modal.getOrCreateInstance(document.getElementById("bugDetailModal")).hide();
      bootstrap.Modal.getOrCreateInstance(document.getElementById("editBugModal")).show();
    } catch (err) {
      showToast({ title: "Lỗi", message: "Không thể lấy thông tin bug để sửa.", variant: "danger" });
    }
  });
}


function wireEditBugForm() {
  const form = document.getElementById("editBugForm");
  if (!form) return;
  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    const fd = new FormData(form);
    const bugId = fd.get("bugId");
    const title = String(fd.get("title") || "").trim();
    const description = String(fd.get("description") || "").trim();
    const status = String(fd.get("status") || "Open").trim();
    const priority = String(fd.get("priority") || "Medium").trim();
    const assigneeIdStr = fd.get("assigneeId") || null;
    const assigneeId = assigneeIdStr ? parseInt(assigneeIdStr, 10) : null;

    if (!bugId || !title) return;

    try {
      await apiFetch(`/bugs/${bugId}`, {
        method: "PUT",
        body: JSON.stringify({ title, description, status, priority, assigneeId }),
      });

      await renderKanbanBoard();
      await renderDashboardStats();

      showToast({ title: "Bug", message: "Đã cập nhật bug thành công.", variant: "success" });
      bootstrap.Modal.getOrCreateInstance(document.getElementById("editBugModal")).hide();
      await openBugDetailModal(bugId);
      bootstrap.Modal.getOrCreateInstance(document.getElementById("bugDetailModal")).show();
    } catch (err) {
      showToast({ title: "Lỗi", message: err.message, variant: "danger" });
    }
  });
}


async function renderDashboardStats() {
  const wsId = getCurrentWorkspaceId();
  if (!wsId) return;

  try {
    // Fetch statistics from Backend API instead of local calculation
    const [summary, priority, developers] = await Promise.all([
      apiFetch(`/dashboard/summary?workspaceId=${wsId}`),
      apiFetch(`/dashboard/by-priority?workspaceId=${wsId}`),
      apiFetch(`/dashboard/by-developer?workspaceId=${wsId}`)
    ]);

    const statIds = { "open": "statOpen", "inprogress": "statInProgress", "resolved": "statResolved", "closed": "statClosed" };
    Object.entries(statIds).forEach(([s, id]) => {
      const el = document.getElementById(id);
      if (el) el.textContent = summary[s] || 0;
    });

    const priorityEl = document.getElementById("dashboardByPriority");
    if (priorityEl) {
      const pData = { "High": priority.high, "Medium": priority.medium, "Low": priority.low };
      priorityEl.innerHTML = Object.entries(pData)
        .map(([p, n]) => `<div class="d-flex justify-content-between small"><span>${p}</span><span class="badge text-bg-secondary">${n}</span></div>`)
        .join("");
    }

    const devEl = document.getElementById("dashboardByDeveloper");
    if (devEl) {
      devEl.innerHTML = developers
        .map((d) => `<div class="d-flex justify-content-between small mb-1"><span>${escapeHtml(d.developerName)}</span><span class="badge text-bg-secondary">${d.bugCount}</span></div>`)
        .join("");
    }
  } catch (err) {
    console.error("Failed to render dashboard stats:", err);
  }
}


async function renderMemberTable() {
  const tbody = document.getElementById("memberTableBody");
  if (!tbody) return;

  const wsId = getCurrentWorkspaceId();
  if (!wsId) return;

  try {
    const members = await getWorkspaceMembers(wsId);
    tbody.innerHTML = members.map(m => `
      <tr>
        <td>
          <div class="d-flex align-items-center gap-2">
            <span class="workspace-avatar text-bg-secondary">${escapeHtml(initials(m.name))}</span>
            <div class="d-flex flex-column">
              <span class="fw-semibold">${escapeHtml(m.name)}</span>
              <span class="small app-muted">${escapeHtml(m.email)}</span>
            </div>
          </div>
        </td>
        <td>
          <select class="form-select form-select-sm dev-role-select" data-user-id="${m.userId}">
            <option value="developer" ${m.role === 'developer' ? 'selected' : ''}>Developer</option>
            <option value="tester" ${m.role === 'tester' ? 'selected' : ''}>Tester</option>
            <option value="manager" ${m.role === 'manager' ? 'selected' : ''}>Manager</option>
          </select>
        </td>
        <td class="text-end">
          <button class="btn btn-sm btn-soft text-danger" title="Xóa khỏi nhóm" data-action="remove-member" data-user-id="${m.userId}"><i class="bi bi-person-x"></i></button>
        </td>
      </tr>
    `).join('');

    // Handle role changes
    tbody.querySelectorAll('.dev-role-select').forEach(sel => {
      sel.addEventListener('change', async (e) => {
        const uid = e.target.getAttribute('data-user-id');
        const newRole = e.target.value;
        try {
          await apiFetch(`/workspaces/${wsId}/members/${uid}/role?role=${newRole}`, { method: "PATCH" });
          showToast({ title: "Phân quyền", message: "Đã cập nhật vai trò", variant: "success" });
        } catch (err) {
          showToast({ title: "Lỗi", message: err.message, variant: "danger" });
        }
      });
    });

    // Handle member removal
    tbody.querySelectorAll('[data-action="remove-member"]').forEach(btn => {
      btn.addEventListener('click', async (e) => {
        const uid = btn.getAttribute('data-user-id');
        if (!confirm("Xóa thành viên này khỏi workspace?")) return;
        try {
          await apiFetch(`/workspaces/${wsId}/members/${uid}`, { method: "DELETE" });
          showToast({ title: "Thành viên", message: "Đã xóa thành viên khỏi nhóm.", variant: "success" });
          await renderMemberTable();
        } catch (err) {
          showToast({ title: "Lỗi", message: err.message, variant: "danger" });
        }
      });
    });

  } catch (err) {
    console.error("Failed to render member table:", err);
  }
}


function wireMemberManagementModal() {
  document.addEventListener("show.bs.modal", async (e) => {
    if (e.target.id === "memberManagementModal") {
      await renderMemberTable();
    }
  });

  const form = document.getElementById("inviteMemberForm");
  if (form) {
    form.addEventListener("submit", async (e) => {
      e.preventDefault();
      const email = form.querySelector('input[type="email"]')?.value?.trim();
      const role = form.querySelector('select')?.value;
      const wsId = getCurrentWorkspaceId();

      if (!email || !wsId) return;

      try {
        await apiFetch(`/workspaces/${wsId}/members`, {
          method: "POST",
          body: JSON.stringify({ email, role }),
        });
        showToast({ title: "Mời thành viên", message: "Đã thêm thành viên vào nhóm!", variant: "success" });
        form.reset();
        await renderMemberTable();
      } catch (err) {
        showToast({ title: "Lỗi", message: err.message, variant: "danger" });
      }
    });
  }
}

function wireLogout() {
  document.addEventListener("click", (e) => {
    const btn = e.target?.closest?.("[data-action='logout']");
    if (!btn) return;
    e.preventDefault();
    Object.values(STORAGE_KEYS).forEach(k => localStorage.removeItem(k));
    showToast({ title: "Đăng xuất", message: "Đang chuyển về trang đăng nhập...", variant: "secondary" });
    setTimeout(() => (window.location.href = "sign-in.html"), 450);
  });
}

function wireKeyboardShortcuts() {
  document.addEventListener("keydown", (e) => {
    // Ctrl/Cmd + K: focus search (if present)
    const isCmdK = (e.ctrlKey || e.metaKey) && (e.key === "k" || e.key === "K");
    if (!isCmdK) return;
    const candidates = [
      document.getElementById("globalSearch"),
      document.getElementById("globalSearchMobile"),
    ].filter(Boolean);

    const pick = candidates.find((el) => el.getClientRects().length > 0) || candidates[0];
    if (!pick) return;
    e.preventDefault();
    pick.focus();
  });
}

async function init() {
  renderUser();
  
  // Initial data load
  await renderWorkspaces();
  await renderWorkspaceTable();
  await renderWorkspacePageHeader();
  await renderKanbanBoard();
  await renderDashboardStats();

  // Wire events
  wireWorkspaceSelection();
  wireAuthForms();
  wireCreateWorkspaceModal();
  wireCreateBugModal();
  wireBugDetailModal();
  wireDeleteBugAction();
  wireEditBugModal();
  wireEditBugForm();
  wireLogout();
  wireKeyboardShortcuts();
  wireMemberManagementModal();
  wireBoardFilters();
}

document.addEventListener("DOMContentLoaded", init);


