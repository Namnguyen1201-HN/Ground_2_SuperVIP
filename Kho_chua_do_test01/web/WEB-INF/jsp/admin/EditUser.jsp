<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, Model.Role, Model.Branch, Model.Warehouse, Model.Shift, Model.User" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Chỉnh sửa nhân viên</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/EditUser.css">
    </head>

    <body>
        <%@ include file="../admin/header_admin.jsp" %>

        <%
            User user = (User) request.getAttribute("user");
            List<Role> roles = (List<Role>) request.getAttribute("roles");
            List<Warehouse> warehouses = (List<Warehouse>) request.getAttribute("warehouses");
            List<Shift> shifts = (List<Shift>) request.getAttribute("shifts");
            List<Branch> branches = (List<Branch>) request.getAttribute("branches");
        %>

        <div class="staff-edit-container">
            <div class="staff-edit-header">
                <h2>Chỉnh sửa nhân viên</h2>
            </div>

            <!-- FORM CHỈNH SỬA NHÂN VIÊN -->
            <form action="EditUser" method="post" id="editUserForm">
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="userID" value="<%= user.getUserId() %>">

                <!-- Thông tin cá nhân -->
                <div class="section-card">
                    <div class="section-title">
                        <i class="fas fa-id-card"></i> Thông tin cá nhân
                    </div>
                    <div class="form-row">
                        <div class="form-group half">
                            <label>Mã nhân viên</label>
                            <input type="text" value="<%= user.getUserId() %>" disabled>
                        </div>
                        <div class="form-group half">
                            <label>Họ tên</label>
                            <input type="text" name="fullName" value="<%= user.getFullName() != null ? user.getFullName() : "" %>" required>
                        </div>
                        <div class="form-group half">
                            <label>Ngày sinh</label>
                            <input type="date" name="dob"
                                   value="<%= user.getDob() != null ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(user.getDob()) : "" %>">
                        </div>
                        <div class="form-group half">
                            <label>Giới tính</label>
                            <select name="gender">
                                <option value="Nam" <%= (user.getGender() != null && user.getGender()) ? "selected" : "" %>>Nam</option>
                                <option value="Nữ" <%= (user.getGender() != null && !user.getGender()) ? "selected" : "" %>>Nữ</option>
                            </select>
                        </div>
                    </div>
                </div>

                <!-- Thông tin liên hệ -->
                <div class="section-card">
                    <div class="section-title">
                        <i class="fas fa-phone"></i> Thông tin liên hệ
                    </div>
                    <div class="form-row">
                        <div class="form-group full">
                            <label>Địa chỉ</label>
                            <textarea name="address"><%= user.getAddress() != null ? user.getAddress() : "" %></textarea>
                        </div>
                        <div class="form-group half">
                            <label>Số điện thoại</label>
                            <input type="text" name="phone" value="<%= user.getPhone() != null ? user.getPhone() : "" %>">
                        </div>
                        <div class="form-group half">
                            <label>Email</label>
                            <input type="email" name="email" value="<%= user.getEmail() != null ? user.getEmail() : "" %>">
                        </div>
                    </div>
                </div>

                <!-- Thông tin công việc -->
                <div class="section-card">
                    <div class="section-title">
                        <i class="fas fa-briefcase"></i> Thông tin công việc
                    </div>
                    <div class="form-row">

                        <!-- Vai trò -->
                        <div class="form-group half">
                            <label>Chức danh</label>
                            <select name="roleID" id="roleSelect">
                                <% if (roles != null) {
                                    for (Role r : roles) { %>
                                <option value="<%= r.getRoleId() %>" <%= (user.getRoleId() == r.getRoleId()) ? "selected" : "" %>>
                                    <%= r.getRoleName() %>
                                </option>
                                <% } } %>
                            </select>
                        </div>

                        <!-- Chi nhánh làm việc -->
                        <div class="form-group half" id="branchField" style="<%= (user.getRoleId() == 1) ? "" : "display:none;" %>">
                            <label>Chi nhánh làm việc</label>
                            <select name="branchID">
                                <option value="">-- Chưa chọn chi nhánh --</option>
                                <% if (branches != null) {
            for (Branch b : branches) { %>
                                <option value="<%= b.getBranchId() %>"
                                        <%= (user.getBranchId() != null && user.getBranchId().equals(b.getBranchId())) ? "selected" : "" %>>
                                    <%= b.getBranchName() %> 
                                </option>
                                <% } } %>
                            </select>
                        </div>

                        <!-- Kho làm việc -->
                        <div class="form-group half" id="warehouseField" style="<%= (user.getRoleId() == 3) ? "" : "display:none;" %>">
                            <label>Kho làm việc</label>
                            <select name="warehouseID">
                                <option value="">-- Chưa chọn kho --</option>
                                <% if (warehouses != null) {
            for (Warehouse w : warehouses) { %>
                                <option value="<%= w.getWarehouseId() %>"
                                        <%= (user.getWarehouseId() != null && user.getWarehouseId().equals(w.getWarehouseId())) ? "selected" : "" %>>
                                    <%= w.getWarehouseName() %>
                                </option>
                                <% } } %>
                            </select>
                        </div>

                        <!-- Trạng thái -->
                        <div class="form-group half">
                            <label>Trạng thái</label>
                            <select name="isActive">
                                <option value="1" <%= (user.getIsActive() == 1) ? "selected" : "" %>>Đang làm việc</option>
                                <option value="0" <%= (user.getIsActive() == 0) ? "selected" : "" %>>Nghỉ việc</option>
                                <option value="2" <%= (user.getIsActive() == 2) ? "selected" : "" %>>Chờ phê duyệt</option>
                            </select>
                        </div>

                        <!-- Ca làm -->
                        <div class="form-group half">
                            <label>Ca làm</label>
                            <select name="shiftID">
                                <option value="">-- Chưa phân ca --</option>
                                <% if (shifts != null) {
                                    for (Shift s : shifts) { %>
                                <option value="<%= s.getShiftID() %>"
                                        <%= (user.getShiftID() != null && user.getShiftID().equals(s.getShiftID())) ? "selected" : "" %>>
                                    <%= s.getShiftName() %>
                                </option>
                                <% } } %>
                            </select>
                        </div>
                    </div>
                </div>

                <!-- Nút hành động -->
                <div class="form-actions">
                    <button type="submit" class="btn btn-save">
                        <i class="fas fa-save"></i> Lưu
                    </button>

                    <a href="NhanVien" class="btn btn-back">
                        <i class="fas fa-arrow-left"></i> Quay lại
                    </a>

                    <!-- Nút sa thải dùng form POST riêng -->
                    <a href="#" class="btn btn-delete"
                       onclick="if (confirm('Bạn có chắc muốn xóa nhân viên này?'))
                                   document.getElementById('deleteForm').submit();
                               return false;">
                        <i class="fas fa-trash"></i> Sa thải
                    </a>
                </div>
            </form>

            <!-- Form xóa nhân viên (POST riêng để tránh nested form) -->
            <form id="deleteForm" action="EditUser" method="post" style="display:none;">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="userId" value="<%= user.getUserId() %>">
            </form>
        </div>

        <!-- Script hiển thị động phần Chi nhánh / Kho -->
        <script>
            document.addEventListener("DOMContentLoaded", function () {
                const roleSelect = document.getElementById("roleSelect");
                const branchField = document.getElementById("branchField");
                const warehouseField = document.getElementById("warehouseField");

                function toggleFields() {
                    const role = parseInt(roleSelect.value);

                    // Nếu là Quản lý kho -> hiện kho
                    if (role === 3) {
                        branchField.style.display = "none";
                        warehouseField.style.display = "flex";
                    }
                    // Các vai trò khác (quản lý chi nhánh, nhân viên bán hàng, v.v...) -> hiện chi nhánh
                    else {
                        branchField.style.display = "flex";
                        warehouseField.style.display = "none";
                    }
                }

                roleSelect.addEventListener("change", toggleFields);
                toggleFields(); // Chạy khi load trang
            });
        </script>
    </body>
</html>
