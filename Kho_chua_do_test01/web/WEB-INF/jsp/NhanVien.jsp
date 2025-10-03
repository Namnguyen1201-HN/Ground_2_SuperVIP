<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<%@ page import="java.util.List" %>
<%@ page import="Model.User" %>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Danh sách nhân viên</title>
        <link
            href="css/NhanVien.css"
            rel="stylesheet"
            type="text/css"
            />
    </head>
    <body>
        <div class="container">

            <!-- Header -->
            <header class="header">

                <div class="header-main">
                    <div class="logo">
                        <div class="logo-icon">
                            <span class="icon-building"></span>
                        </div>
                        <span>WM</span>
                    </div>
                    <nav class="nav-menu">
                        <a href="TongQuan" class="nav-item ">
                            <span class="icon-overview"></span>
                            Tổng quan
                        </a>
                        <a href="product" class="nav-item">
                            <span class="icon-products"></span>
                            Hàng hóa
                        </a>
                        <a href="#" class="nav-item">
                            <span class="icon-transactions"></span>
                            Giao dịch
                        </a>
                        <a href="Supplier" class="nav-item">
                            <span class="icon-partners"></span>
                            Đối tác
                        </a>
                        <a href="NhanVien" class="nav-item active">
                            <span class="icon-staff"></span>
                            Nhân viên
                        </a>
                        <a href="#" class="nav-item">
                            <span class="icon-cashbook"></span>
                            Sổ quỹ
                        </a>
                        <a href="#" class="nav-item">
                            <span class="icon-reports"></span>
                            Báo cáo
                        </a>
                        <a href="#" class="nav-item">
                            <span class="icon-online"></span>
                            Bán Online
                        </a>
                        <a href="#" class="nav-item">
                            <span class="icon-sales"></span>
                            Bán hàng
                        </a>
                    </nav>
                </div>
            </header>

            <!-- Sidebar + Main -->
            <div class="content">
                <!-- Sidebar filters -->
                <aside class="sidebar">
                    <h2>Danh sách nhân viên</h2>
                    <form action="NhanVien" method="get">

                        <!-- Trạng thái -->
                        <div class="filter-group">
                            <label>Trạng thái nhân viên</label>                         
                            <div class="status-radios">
                                <label class="radio-container">
                                    <input type="radio" name="status" value="all"
                                           <%= "all".equals(request.getAttribute("selectedStatus")) ? "checked" : "" %>
                                           onchange="this.form.submit()" />
                                    <span class="custom-radio"></span>
                                    Tất cả nhân viên
                                </label>
                                <label class="radio-container">
                                    <input type="radio" name="status" value="active"
                                           <%= "active".equals(request.getAttribute("selectedStatus")) ? "checked" : "" %>
                                           onchange="this.form.submit()" />
                                    <span class="custom-radio"></span>
                                    Đang làm việc
                                </label>
                                <label class="radio-container">
                                    <input type="radio" name="status" value="inactive"
                                           <%= "inactive".equals(request.getAttribute("selectedStatus")) ? "checked" : "" %>
                                           onchange="this.form.submit()" />
                                    <span class="custom-radio"></span>
                                    Đã nghỉ
                                </label>
                            </div>

                            <!-- Chức danh -->
                        </div>
                        <div class="filter-group">
                            <select name="branch" onchange="this.form.submit()">
                                <option value="all" <%= "all".equals(request.getAttribute("selectedBranch")) ? "selected" : "" %>>Tất cả chi nhánh</option>
                                <option value="Head Office" <%= "Head Office".equals(request.getAttribute("selectedBranch")) ? "selected" : "" %>>Head Office</option>
                                <option value="Chi nhánh Sài Gòn" <%= "Chi nhánh Sài Gòn".equals(request.getAttribute("selectedBranch")) ? "selected" : "" %>>Chi nhánh Sài Gòn</option>
                                <option value="Chi nhánh Đà Nẵng" <%= "Chi nhánh Đà Nẵng".equals(request.getAttribute("selectedBranch")) ? "selected" : "" %>>Chi nhánh Đà Nẵng</option>
                                <!-- TODO: load dynamic list từ BranchDAL -->
                            </select>
                        </div>
                        <div class="filter-group">
                            <select name="department" onchange="this.form.submit()">
                                <option value="all" <%= "all".equals(request.getAttribute("selectedDepartment")) ? "selected" : "" %>>Tất cả phòng ban</option>
                                <option value="Kinh Doanh" <%= "Kinh Doanh".equals(request.getAttribute("selectedDepartment")) ? "selected" : "" %>>Phòng Kinh Doanh</option>
                                <option value="Kho Hàng" <%= "Kho Hàng".equals(request.getAttribute("selectedDepartment")) ? "selected" : "" %>>Phòng Kho Hàng</option>
                                <option value="Kế Toán" <%= "Kế Toán".equals(request.getAttribute("selectedDepartment")) ? "selected" : "" %>>Phòng Kế Toán</option>
                                <option value="Nhân Sự" <%= "Nhân Sự".equals(request.getAttribute("selectedDepartment")) ? "selected" : "" %>>Phòng Nhân Sự</option>
                                <!-- TODO: load dynamic list từ DepartmentDAL -->
                            </select>
                        </div>
                        <div class="filter-group">
                            <select name="role" onchange="this.form.submit()">
                                <option value="None" <%= "None".equals(request.getAttribute("selectedRole")) ? "selected" : "" %>>None</option>
                                <option value="Admin" <%= "Admin".equals(request.getAttribute("selectedRole")) ? "selected" : "" %>>Admin</option>
                                <option value="InventoryManager" <%= "InventoryManager".equals(request.getAttribute("selectedRole")) ? "selected" : "" %>>InventoryManager</option>
                                <option value="StoreManager" <%= "StoreManager".equals(request.getAttribute("selectedRole")) ? "selected" : "" %>>StoreManager</option>
                                <option value="Supplier" <%= "Supplier".equals(request.getAttribute("selectedRole")) ? "selected" : "" %>>Supplier</option>
                                <option value="Salesperson" <%= "Salesperson".equals(request.getAttribute("selectedRole")) ? "selected" : "" %>>Salesperson</option>
                            </select>
                        </div>
                    </form>
                </aside>

                <!-- Main content -->
                <main class="main-content">
                    <div class="toolbar">
                        <form action="NhanVien" method="get" class="search-bar">
                            <div>
                                <input type="text" name="search" placeholder="Tìm theo mã, tên nhân viên"
                                       value="<%= request.getAttribute("searchKeyword") != null ? request.getAttribute("searchKeyword") : "" %>" />

                            </div>
                        </form>
                        <div class="btn-group">
                            <a href="AddUser.jsp" class="btn add">⚙️ Thiết lập nhân viên</a>
                            <button class="btn import">Nhập file</button>
                            <button class="btn export">Xuất file</button>
                        </div>
                    </div>

                    <table class="emp-table">
                        <thead>
                            <tr>
                                <th>Mã nhân viên</th>
                                <th>Tên nhân viên</th>
                                <th>Chi nhánh làm việc</th>
                                <th>Phòng ban</th>
                                <th>Chức danh</th>
                                <th>SĐT</th>
                                <th>CMND/CCCD</th>
                                <th>Email</th>
                                <th>Hành động</th> 
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                List<Model.User> users = (List<Model.User>) request.getAttribute("users");
                                if (users != null && !users.isEmpty()) {
                                    for (Model.User u : users) {
                            %>
                            <tr>
                                <td><%= u.getUserId() %></td>
                                <td><%= u.getFullName() %></td>
                                <td><%= u.getBranchName() != null ? u.getBranchName() : "Chưa có" %></td>   
                                <td><%= u.getDepartmentName() != null ? u.getDepartmentName() : "Chưa có" %></td>
                                <td><%= u.getRoleName() != null ? u.getRoleName() : "Chưa có" %></td>
                                <td><%= u.getPhone() != null ? u.getPhone() : "Chưa có" %></td>
                                <td><%= u.getIdentifierCode() != null ? u.getIdentifierCode() : "Chưa có" %></td>
                                <td><%= u.getEmail() != null ? u.getEmail() : "Chưa có" %></td>
                                <td>
                                    <a href="EditUser?userId=<%= u.getUserId() %>" class="btn edit">Sửa</a>
                                    <a href="DeleteUser?userId=<%= u.getUserId() %>" class="btn delete" onclick="return confirm('Bạn có chắc muốn xóa nhân viên này?');">Xóa</a>
                                </td>
                            </tr>
                            <%
                                    }
                                } else {
                            %>
                            <tr>
                                <td colspan="8" class="empty">
                                    Gian hàng chưa có nhân viên hoặc tên/mã nhân viên bạn tìm kiếm không tồn tại.
                                </td>
                            </tr>
                            <%
                                }
                            %>
                        </tbody>
                    </table>
                </main>
            </div>

            <!-- Info bar -->
            <footer class="info-bar">
                <p>

                </p>
            </footer>
        </div>

    </body>
</html>
