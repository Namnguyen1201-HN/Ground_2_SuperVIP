<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<%@ page import="java.util.List" %>
<%@ page import="Model.User" %>
<%@ page import="Model.Branch" %>
<%@ page import="Model.Department" %>
<%@ page import="Model.Role" %>
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
                                <option value="all" <%= "all".equals(request.getAttribute("selectedBranch")) ? "selected" : "" %>>
                                    Tất cả chi nhánh
                                </option>
                                <%
                                    List<Branch> branches = (List<Branch>) request.getAttribute("branches");
                                    String selectedBranch = (String) request.getAttribute("selectedBranch");
                                    if (branches != null) {
                                        for (Branch b : branches) {
                                %>
                                <option value="<%= b.getBranchName() %>" <%= b.getBranchName().equals(selectedBranch) ? "selected" : "" %>>
                                    <%= b.getBranchName() %>
                                </option>
                                <%
                                        }
                                    }
                                %>
                            </select>
                        </div>
                        <div class="filter-group">
                            <select name="department" onchange="this.form.submit()">
                                <option value="all" <%= "all".equals(request.getAttribute("selectedDepartment")) ? "selected" : "" %>>
                                    Tất cả phòng ban
                                </option>
                                <%
                                    List<Department> departments = (List<Department>) request.getAttribute("departments");
                                    String selectedDept = (String) request.getAttribute("selectedDepartment");
                                    if (departments != null) {
                                        for (Department d : departments) {
                                %>
                                <option value="<%= d.getDepartmentName() %>" <%= d.getDepartmentName().equals(selectedDept) ? "selected" : "" %>>
                                    <%= d.getDepartmentName() %>
                                </option>
                                <%
                                        }
                                    }
                                %>
                            </select>
                        </div>
                        <div class="filter-group">
                            <select name="role" onchange="this.form.submit()">
                                <option value="None" <%= "None".equals(request.getAttribute("selectedRole")) ? "selected" : "" %>>
                                    None
                                </option>
                                <%
                                    List<Role> roles = (List<Role>) request.getAttribute("roles");
                                    String selectedRole = (String) request.getAttribute("selectedRole");
                                    if (roles != null) {
                                        for (Role r : roles) {
                                %>
                                <option value="<%= r.getRoleName() %>" <%= r.getRoleName().equals(selectedRole) ? "selected" : "" %>>
                                    <%= r.getRoleName() %>
                                </option>
                                <%
                                        }
                                    }
                                %>
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
                            <a href="AddUser" class="btn add">Thêm nhân viên</a>
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
                                <th>Thao tác</th> 
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
                                    <a href="EditUser?userId=<%= u.getUserId() %>" class="btn edit">Chi tiết</a>                            
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
