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


<div class="col-md-3" style="margin-top: 60px;">
    <div class="sidebar p-3 bg-white shadow-sm rounded">
        <h6 class="text-secondary mb-3">Gian hàng</h6>
        <a href="InformationAccount" class="sidebar-link <%= request.getRequestURI().contains("InformationAccount") ? "active" : "" %>">
            <i class="bi bi-info-circle me-2"></i> Thông tin gian hàng
        </a>
        <a href="ChangePassWord" class="sidebar-link <%= request.getRequestURI().contains("ChangePassword") ? "active" : "" %>">
            <i class="bi bi-lock me-2"></i> Đổi mật khẩu
        </a>
        <a href="BranchManagement" class="sidebar-link <%= request.getRequestURI().contains("BranchManagement") ? "active" : "" %> ">
            <i class="bi bi-key me-2"></i> Quản lý chi nhánh
        </a>
        <a href="WareHouseManagement" class="sidebar-link <%= request.getRequestURI().contains("WareHouseManagement") ? "active" : "" %> ">
            <i class="bi bi-house-door me-2"></i> Quản lý kho tổng
        </a>

    </div>
</div>

