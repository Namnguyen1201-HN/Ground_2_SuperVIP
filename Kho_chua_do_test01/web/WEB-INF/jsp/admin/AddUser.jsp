<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.Branch" %>
<%@ page import="Model.Department" %>
<%@ page import="Model.Role" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Thêm nhân viên</title>
    <link rel="stylesheet" type="text/css" href="css/admin/AddUser.css">
    <!-- Thêm fontawesome cho icon -->
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

                    <label>Tên đăng nhập:</label>
                    <input type="text" name="username" required />

                    <label>Email:</label>
                    <input type="email" name="email" />
                </div>
            </div>

            <!-- Thông tin liên hệ -->
            <div class="section-box">
                <div class="section-header"><i class="fa fa-phone"></i> Thông tin liên hệ</div>
                <div class="section-body">
                    <label>Số điện thoại:</label>
                    <input type="text" name="phone" />

                    <label>CMND/CCCD:</label>
                    <input type="text" name="identifierCode" />
                </div>
            </div>

            <!-- Thông tin công việc -->
            <div class="section-box">
                <div class="section-header"><i class="fa fa-briefcase"></i> Thông tin công việc</div>
                <div class="section-body">
                    <label>Chi nhánh:</label>
                    <select name="branchId" required>
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

                    <label>Phòng ban:</label>
                    <select name="departmentId" required>
                        <%
                            List<Department> departments = (List<Department>) request.getAttribute("departments");
                            if (departments != null) {
                                for (Department d : departments) {
                        %>
                            <option value="<%= d.getDepartmentId() %>"><%= d.getDepartmentName() %></option>
                        <%
                                }
                            }
                        %>
                    </select>

                    <label>Chức danh (Role):</label>
                    <select name="roleId" required>
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

                    <label>Trạng thái:</label>
                    <select name="isActive">
                        <option value="1">Đang làm việc</option>
                        <option value="0">Đã nghỉ</option>
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
</body>
</html>
