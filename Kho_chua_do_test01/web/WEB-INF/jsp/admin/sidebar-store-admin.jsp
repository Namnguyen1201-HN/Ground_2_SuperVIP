<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">  

<style>

    /* Sidebar Styling */
    .sidebar {
        width: 300px;
    }

    .sidebar-link {
        display: flex;
        align-items: center;
        color: #374151;
        text-decoration: none;
        padding: 8px 12px;
        border-radius: 6px;
        margin-bottom: 4px;
        transition: all 0.2s;
    }

    .sidebar-link:hover {
        background-color: #eff6ff;
        color: #2563eb;
    }

    .sidebar-link.active {
        background-color: #dbeafe;
        color: #2563eb;
        font-weight: 600;
    }

    /* Card & Form */
    .card {
        border-radius: 10px;
        border: none;
    }

    .form-control {
        border-radius: 8px;
    }

    .alert {
        border-radius: 8px;
    }

</style>


<%
  // Khởi tạo 1 lần cho mỗi request để tránh trùng biến khi include nhiều lần
  if (request.getAttribute("_sb_inited") == null) {
      Model.User sb_user = (Model.User) session.getAttribute("currentUser");
      Integer sb_roleId = (sb_user != null) ? sb_user.getRoleId() : null;

      String sb_ctx = request.getContextPath();
      String sb_uri = request.getRequestURI();

      boolean sb_isAdmin = (sb_roleId != null && sb_roleId == 0);
      boolean sb_isBM    = (sb_roleId != null && sb_roleId == 1);

      String sb_infoUrl = sb_isBM ? (sb_ctx + "/InformationAccountBM") : (sb_ctx + "/InformationAccount");

      boolean sb_activeInfo   = sb_uri != null && (sb_isBM ? sb_uri.contains("InformationAccountBM") : sb_uri.contains("InformationAccount"));
      boolean sb_activeChange = sb_uri != null && sb_uri.contains("ChangePassWord");
      boolean sb_activeBranch = sb_uri != null && sb_uri.contains("BranchManagement");
      boolean sb_activeWH     = sb_uri != null && sb_uri.contains("WareHouseManagement");

      request.setAttribute("_sb_inited", Boolean.TRUE);
      request.setAttribute("sb_ctx", sb_ctx);
      request.setAttribute("sb_infoUrl", sb_infoUrl);
      request.setAttribute("sb_activeInfo", sb_activeInfo);
      request.setAttribute("sb_activeChange", sb_activeChange);
      request.setAttribute("sb_activeBranch", sb_activeBranch);
      request.setAttribute("sb_activeWH", sb_activeWH);
      request.setAttribute("sb_isAdmin", sb_isAdmin);
  }

  String sb_ctx        = (String) request.getAttribute("sb_ctx");
  String sb_infoUrl    = (String) request.getAttribute("sb_infoUrl");
  boolean sb_activeInfo   = Boolean.TRUE.equals(request.getAttribute("sb_activeInfo"));
  boolean sb_activeChange = Boolean.TRUE.equals(request.getAttribute("sb_activeChange"));
  boolean sb_activeBranch = Boolean.TRUE.equals(request.getAttribute("sb_activeBranch"));
  boolean sb_activeWH     = Boolean.TRUE.equals(request.getAttribute("sb_activeWH"));
  boolean sb_isAdmin      = Boolean.TRUE.equals(request.getAttribute("sb_isAdmin"));
%>

<div class="col-md-3" style="margin-top: 60px;">
    <div class="sidebar p-3 bg-white shadow-sm rounded">
        <h6 class="text-secondary mb-3">Gian hàng</h6>

        <!-- Thông tin gian hàng (Admin -> /InformationAccount, BM -> /InformationAccountBM) -->
        <a href="<%= sb_infoUrl %>" class="sidebar-link <%= sb_activeInfo ? "active" : "" %>">
            <i class="bi bi-info-circle me-2"></i> Thông tin gian hàng
        </a>

        <!-- Đổi mật khẩu (dùng chung) -->
        <a href="<%= sb_ctx %>/ChangePassWord" class="sidebar-link <%= sb_activeChange ? "active" : "" %>">
            <i class="bi bi-lock me-2"></i> Đổi mật khẩu
        </a>

        <% if (sb_isAdmin) { %>
        <!-- Chỉ Admin mới thấy -->
        <a href="<%= sb_ctx %>/BranchManagement" class="sidebar-link <%= sb_activeBranch ? "active" : "" %>">
            <i class="bi bi-key me-2"></i> Quản lý chi nhánh
        </a>
        <a href="<%= sb_ctx %>/WareHouseManagement" class="sidebar-link <%= sb_activeWH ? "active" : "" %>">
            <i class="bi bi-house-door me-2"></i> Quản lý kho tổng
        </a>
        <% } %>
    </div>
</div>

