<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">  

<div class="col-md-3">
    <div class="sidebar p-3 bg-white shadow-sm rounded">
        <h6 class="text-secondary mb-3">Gian hàng</h6>
        <a href="InformationAccount" class="sidebar-link <%= request.getRequestURI().contains("InformationAccount") ? "active" : "" %>">
            <i class="bi bi-info-circle me-2"></i> Thông tin gian hàng
        </a>
        <a href="ChangePassWord" class="sidebar-link <%= request.getRequestURI().contains("ChangePassword") ? "active" : "" %>">
            <i class="bi bi-lock me-2"></i> Đổi mật khẩu
        </a>
        <a href="#" class="sidebar-link">
            <i class="bi bi-key me-2"></i> Quản lý chi nhánh
        </a>
        <a href="#" class="sidebar-link">
            <i class="bi bi-house-door me-2"></i> Quản lý kho tổng
        </a>
        <a href="#" class="sidebar-link">
            <i class="bi bi-box-seam me-2"></i> Gói dịch vụ
        </a>
        <a href="#" class="sidebar-link">
            <i class="bi bi-clock-history me-2"></i> Lịch sử mua hàng
        </a>
    </div>
</div>

