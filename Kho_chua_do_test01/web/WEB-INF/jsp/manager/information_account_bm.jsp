
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.User" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%
    // DATA từ servlet InformationAccountBMController
    User user = (User) request.getAttribute("user");
    Integer branchCount = (Integer) request.getAttribute("branchCount");
    Integer warehouseCount = (Integer) request.getAttribute("warehouseCount");
    String msg = (String) request.getAttribute("msg");

    // Phần "Thông tin cửa hàng"
    String storeName = (String) request.getAttribute("storeName");                 // Tên cửa hàng
    String currentBranchName = (String) request.getAttribute("currentBranchName"); // VD: "Hà Nội"
    Integer currentBranchId = (Integer) request.getAttribute("currentBranchId");   // nếu cần submit ẩn

    if (storeName == null) storeName = "";
    if (currentBranchName == null) currentBranchName = "—";

    String dobValue = "";
    if (user != null && user.getDob() != null) {
        dobValue = new SimpleDateFormat("yyyy-MM-dd").format(user.getDob());
    }
%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thông tin tài khoản - Quản lý chi nhánh</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body class="bg-light">
    <%-- Header & Sidebar: bạn có thể đổi include sang bản dành cho BM sau --%>
    <%@ include file="../admin/header_admin.jsp" %>

    <div class="container-fluid py-4">
        <div class="row">
            <%@ include file="../admin/sidebar-store-admin.jsp" %>

            <div class="col-md-9 col-lg-9" style="margin-top: 60px;">
                <div class="card shadow-sm p-4" style="width: 965px;">
                    <h5 class="mb-3">
                        <i class="bi bi-info-circle"></i>
                        Thông tin tài khoản (Quản lý chi nhánh)
                    </h5>

                    <%-- Alert không auto-hide --%>
                    <% if (request.getAttribute("error") != null) { %>
                        <div class="alert alert-danger text-center fw-bold">
                            <%= request.getAttribute("error") %>
                        </div>
                    <% } else if (request.getAttribute("success") != null) { %>
                        <div class="alert alert-success text-center fw-bold">
                            <%= request.getAttribute("success") %>
                        </div>
                    <% } else if (msg != null) { %>
                        <div class="alert alert-info text-center fw-bold">
                            <%= msg %>
                        </div>
                    <% } %>

                    <form action="InformationAccountBM" method="post">
                        <input type="hidden" name="userId" value="<%= user != null ? user.getUserId() : 0 %>">
                        <input type="hidden" name="branchId" value="<%= currentBranchId != null ? currentBranchId : 0 %>">

                        <div class="row g-3">
                            <%-- Thông tin cá nhân --%>
                            <div class="col-md-6">
                                <div class="card p-3 border-0 shadow-sm">
                                    <div class="section-title mb-3">Thông tin cá nhân</div>

                                    <div class="mb-3">
                                        <label class="form-label">Họ và tên:</label>
                                        <input type="text" class="form-control" name="fullName"
                                               value="<%= (user!=null && user.getFullName()!=null) ? user.getFullName() : "" %>">
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label">Giới tính:</label>
                                        <select class="form-select" name="gender">
                                            <option value="">-- Chọn --</option>
                                            <option value="true"  <%= (user!=null && user.getGender()!=null && user.getGender())  ? "selected" : "" %>>Nam</option>
                                            <option value="false" <%= (user!=null && user.getGender()!=null && !user.getGender()) ? "selected" : "" %>>Nữ</option>
                                        </select>
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label">Ngày sinh:</label>
                                        <input type="date" class="form-control" name="dob" value="<%= dobValue %>">
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label">CCCD/Hộ chiếu:</label>
                                        <input type="text" class="form-control" name="identificationId"
                                               value="<%= (user!=null && user.getIdentificationId()!=null) ? user.getIdentificationId() : "" %>">
                                    </div>
                                </div>
                            </div>

                            <%-- Thông tin liên hệ --%>
                            <div class="col-md-6">
                                <div class="card p-3 border-0 shadow-sm">
                                    <div class="section-title mb-3">Thông tin liên hệ</div>

                                    <div class="mb-3">
                                        <label class="form-label">Email:</label>
                                        <input type="email" class="form-control" name="email"
                                               value="<%= (user!=null && user.getEmail()!=null) ? user.getEmail() : "" %>">
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label">Số điện thoại:</label>
                                        <input type="text" class="form-control" name="phone"
                                               value="<%= (user!=null && user.getPhone()!=null) ? user.getPhone() : "" %>">
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label">Địa chỉ:</label>
                                        <input type="text" class="form-control" name="address"
                                               value="<%= (user!=null && user.getAddress()!=null) ? user.getAddress() : "" %>">
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label">Trạng thái tài khoản:</label>
                                        <select class="form-select" name="isActive" disabled>
                                            <option value="1" <%= (user!=null && user.getIsActive()==1) ? "selected" : "" %>>Đang hoạt động</option>
                                            <option value="0" <%= (user!=null && user.getIsActive()==0) ? "selected" : "" %>>Ngừng hoạt động</option>
                                        </select>
                                    </div>
                                </div>
                            </div>

                            <%-- Thông tin cửa hàng (thay cho Thông tin doanh nghiệp) --%>
                            <div class="col-md-6">
                                <div class="card p-3 border-0 shadow-sm">
                                    <div class="section-title mb-3">
                                        <i class="bi bi-shop me-1"></i> Thông tin cửa hàng
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label">Tên cửa hàng:</label>
                                        <input type="text" class="form-control" name="storeName" value="<%= storeName %>" disabled>
                                    </div>

                                    <div class="mb-1">
                                        <label class="form-label d-block">Chi nhánh đang công tác:</label>
                                        <span class="badge rounded-pill text-success-emphasis bg-success-subtle border border-success-subtle px-3 py-2">
                                            <i class="bi bi-geo-alt me-1"></i><%= currentBranchName %>
                                        </span>
                                    </div>
                                </div>
                            </div>

                            <%-- Thông tin chuỗi cửa hàng (giữ nguyên) --%>
                            <div class="col-md-6">
                                <div class="card p-3 border-0 shadow-sm">
                                    <div class="section-title mb-3">Thông tin chuỗi cửa hàng</div>

                                    <div class="mb-3">
                                        <label class="form-label">Số chi nhánh kho:</label>
                                        <input type="text" class="form-control" readonly
                                               value="<%= warehouseCount != null ? warehouseCount : 0 %>">
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label">Số chi nhánh cửa hàng:</label>
                                        <input type="text" class="form-control" readonly
                                               value="<%= branchCount != null ? branchCount : 0 %>">
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="mt-4 text-end">
                            <button type="reset" class="btn btn-secondary me-2">
                                <i class="bi bi-arrow-counterclockwise"></i> Hủy bỏ
                            </button>
                            <button type="submit" class="btn btn-primary">
                                <i class="bi bi-save"></i> Lưu thay đổi
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
