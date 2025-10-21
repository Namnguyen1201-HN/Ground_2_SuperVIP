<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.Branch" %>
<%@ page import="Model.Role" %>
<%@ page import="Model.Warehouse" %>
<html>


    <head>
        <meta charset="UTF-8">
        <title>Thêm nhân viên</title>
        <link rel="stylesheet" type="text/css" href="css/admin/AddUser.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css"/>
    </head>
    <body>
        <div class="adduser-container">
            <h2>Thêm nhân viên mới</h2>
            <form action="AddUser" method="post">


                <!-- Thông tin cá nhân -->
                <div class="section-box">
                    <div class="section-header"><i class="fa fa-id-card"></i> Thông tin cá nhân</div>
                    <div class="section-body">
                        <label>Họ và tên:</label>
                        <input type="text" name="fullName" required />


                        <label>Email:</label>
                        <input type="email" name="email" required />

                        <label>Mật khẩu:</label>
                        <input type="password" name="password" required />

                        <label>Giới tính:</label>
                        <select name="gender">
                            <option value="">Không xác định</option>
                            <option value="1">Nam</option>
                            <option value="0">Nữ</option>
                        </select>

                        <label>Ngày sinh:</label>
                        <input type="date" name="dob" />
                    </div>

                </div>

                <!-- Thông tin liên hệ -->
                <div class="section-box">
                    <div class="section-header"><i class="fa fa-phone"></i> Thông tin liên hệ</div>
                    <div class="section-body">
                        <label>Số điện thoại:</label>
                        <input type="text" name="phone" />


                        <label>CMND/CCCD:</label>
                        <input type="text" name="identificationId" />

                        <label>Địa chỉ:</label>
                        <input type="text" name="address" />

                        <label>Ảnh đại diện (URL):</label>
                        <input type="text" name="avaUrl" />
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
                            %>
                            <option value="<%= r.getRoleId() %>"><%= r.getRoleName() %></option>
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
                                %>
                                <option value="<%= b.getBranchId() %>"><%= b.getBranchName() %></option>
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
                                %>
                                <option value="<%= w.getWarehouseId() %>"><%= w.getWarehouseName() %></option>
                                <%
                                        }
                                    }
                                %>
                            </select>
                        </div>

                        <label>Trạng thái:</label>
                        <select name="isActive">
                            <option value="1">Đang làm việc</option>
                            <option value="0">Đã nghỉ</option>
                            <option value="2">Chờ phê duyệt</option>
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

// Gọi khi trang load lần đầu
            document.addEventListener("DOMContentLoaded", updateFormVisibility);
        </script>
    </body>

</html>
