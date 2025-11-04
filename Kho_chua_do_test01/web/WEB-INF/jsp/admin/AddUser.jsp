<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="Model.Branch, Model.Role, Model.Warehouse, Model.User" %>
<html>

    <head>
        <meta charset="UTF-8">
        <title>Thêm nhân viên</title>
        <link rel="stylesheet" type="text/css" href="css/admin/AddUser.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css"/>
        <style>
            .error-message {
                color: red;
                font-size: 13px;
                margin-top: 4px;
            }
            .error-box {
                background-color: #ffe6e6;
                border: 1px solid #ff6666;
                padding: 10px;
                margin-bottom: 10px;
                color: #b30000;
                border-radius: 6px;
            }
        </style>
    </head>

    <body>
        <div class="adduser-container">
            <h2>Thêm nhân viên mới</h2>

            <%-- 🔹 Hiển thị lỗi tổng thể nếu có --%>
            <%
                String error = (String) request.getAttribute("error");
                if (error != null) {
            %>
            <div class="error-box"><%= error %></div>
            <%
                }
                Map<String, String> errors = (Map<String, String>) request.getAttribute("errors");
                User user = (User) request.getAttribute("user");
            %>

            <form action="AddUser" method="post">

                <!-- Thông tin cá nhân -->
                <div class="section-box">
                    <div class="section-header"><i class="fa fa-id-card"></i> Thông tin cá nhân</div>
                    <div class="section-body">
                        <label>Họ và tên:</label>
                        <input type="text" name="fullName" value="<%= (user != null && user.getFullName() != null) ? user.getFullName() : "" %>" required />
                        <div class="error-message"><%= (errors != null && errors.get("fullName") != null) ? errors.get("fullName") : "" %></div>

                        <label>Email:</label>
                        <input type="email" name="email" value="<%= (user != null && user.getEmail() != null) ? user.getEmail() : "" %>" required />
                        <div class="error-message"><%= (errors != null && errors.get("email") != null) ? errors.get("email") : "" %></div>

                        <label>Mật khẩu:</label>
                        <input type="password" name="password" value="<%= (user != null && user.getPasswordHash() != null) ? user.getPasswordHash() : "" %>" required />
                        <div class="error-message"><%= (errors != null && errors.get("password") != null) ? errors.get("password") : "" %></div>

                        <label>Giới tính:</label>
                        <select name="gender">
                            <option value="">Không xác định</option>
                            <option value="1" <%= (user != null && Boolean.TRUE.equals(user.getGender())) ? "selected" : "" %>>Nam</option>
                            <option value="0" <%= (user != null && Boolean.FALSE.equals(user.getGender())) ? "selected" : "" %>>Nữ</option>
                        </select>

                        <label>Ngày sinh:</label>
                        <input type="date" name="dob" value="<%= (user != null && user.getDob() != null) ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(user.getDob()) : "" %>" />
                    </div>
                </div>

                <!-- Thông tin liên hệ -->
                <div class="section-box">
                    <div class="section-header"><i class="fa fa-phone"></i> Thông tin liên hệ</div>
                    <div class="section-body">
                        <label>Số điện thoại:</label>
                        <input type="text" name="phone" value="<%= (user != null && user.getPhone() != null) ? user.getPhone() : "" %>" />
                        <div class="error-message"><%= (errors != null && errors.get("phone") != null) ? errors.get("phone") : "" %></div>

                        <label>CMND/CCCD:</label>
                        <input type="text" name="identificationId" value="<%= (user != null && user.getIdentificationId() != null) ? user.getIdentificationId() : "" %>" />
                        <div class="error-message"><%= (errors != null && errors.get("identificationId") != null) ? errors.get("identificationId") : "" %></div>

                        <label>Địa chỉ:</label>
                        <input type="text" name="address" value="<%= (user != null && user.getAddress() != null) ? user.getAddress() : "" %>" />

                        <label>Ảnh đại diện (URL):</label>
                        <input type="text" name="avaUrl" value="<%= (user != null && user.getAvaUrl() != null) ? user.getAvaUrl() : "" %>" />
                    </div>
                </div>

                <!-- Thông tin công việc -->
                <div class="section-box">
                    <div class="section-header"><i class="fa fa-briefcase"></i> Thông tin công việc</div>
                    <div class="section-body">
                        <label>Chức danh (Role):</label>
                        <select name="roleId" id="roleId" required onchange="updateFormVisibility()">
                            <%
                                List<Role> roles = (List<Role>) request.getAttribute("roles");
                                if (roles != null) {
                                    for (Role r : roles) {
                                        String selected = (user != null && user.getRoleId() == r.getRoleId()) ? "selected" : "";
                            %>
                            <option value="<%= r.getRoleId() %>" <%= selected %>><%= r.getRoleName() %></option>
                            <%
                                    }
                                }
                            %>
                        </select>

                        <div id="branchSection" style="display: none;">
                            <label>Chi nhánh:</label>
                            <select name="branchId" id="branchId">
                                <option value="">Không thuộc chi nhánh</option>
                                <%
                                    List<Branch> branches = (List<Branch>) request.getAttribute("branches");
                                    if (branches != null) {
                                        for (Branch b : branches) {
                                            String selected = (user != null && user.getBranchId() != null && user.getBranchId() == b.getBranchId()) ? "selected" : "";
                                %>
                                <option value="<%= b.getBranchId() %>" <%= selected %>><%= b.getBranchName() %></option>
                                <%
                                        }
                                    }
                                %>
                            </select>
                        </div>

                        <div id="warehouseSection" style="display: none;">
                            <label>Kho làm việc:</label>
                            <select name="warehouseId" id="warehouseId">
                                <option value="">Không thuộc kho</option>
                                <%
                                    List<Warehouse> warehouses = (List<Warehouse>) request.getAttribute("warehouses");
                                    if (warehouses != null) {
                                        for (Warehouse w : warehouses) {
                                            String selected = (user != null && user.getWarehouseId() != null && user.getWarehouseId() == w.getWarehouseId()) ? "selected" : "";
                                %>
                                <option value="<%= w.getWarehouseId() %>" <%= selected %>><%= w.getWarehouseName() %></option>
                                <%
                                        }
                                    }
                                %>
                            </select>
                        </div>

                        <label>Trạng thái:</label>
                        <select name="isActive">
                            <option value="1" <%= (user != null && user.getIsActive() == 1) ? "selected" : "" %>>Đang làm việc</option>
                            <option value="0" <%= (user != null && user.getIsActive() == 0) ? "selected" : "" %>>Đã nghỉ</option>
                            <option value="2" <%= (user == null || user.getIsActive() == 2) ? "selected" : "" %>>Chờ phê duyệt</option>
                        </select>
                    </div>
                </div>

                <!-- Nút hành động -->
                <div class="action-buttons">
                    <a href="NhanVien" class="btn-back"><i class="fa fa-arrow-left"></i> Quay lại</a>
                    <button type="submit" class="btn-save"><i class="fa fa-save"></i> Thêm mới</button>
                </div>
            </form>
        </div>

        <script>
            function updateFormVisibility() {
                const roleSelect = document.getElementById("roleId");
                const branchSection = document.getElementById("branchSection");
                const warehouseSection = document.getElementById("warehouseSection");

                const selectedRole = roleSelect.options[roleSelect.selectedIndex].text.toLowerCase();

                // Ẩn hết trước
                branchSection.style.display = "none";
                warehouseSection.style.display = "none";

                // Logic hiển thị dựa vào Role
                if (selectedRole.includes("quản lý chi nhánh")) {
                    branchSection.style.display = "block";
                } else if (selectedRole.includes("quản lý kho")) {
                    warehouseSection.style.display = "block";
                } else if (selectedRole.includes("nhân viên")) {
                    branchSection.style.display = "block";
                }
            }

            document.addEventListener("DOMContentLoaded", updateFormVisibility);
        </script>
    </body>
</html>
