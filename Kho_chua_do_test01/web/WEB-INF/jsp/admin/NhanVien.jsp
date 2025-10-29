<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<%@ page import="java.util.List" %>
<%@ page import="Model.User" %>
<%@ page import="Model.Branch" %>
<%@ page import="Model.Role" %>

<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Danh sách nhân viên</title>
        <link href="css/admin/NhanVien.css" rel="stylesheet" type="text/css" />
    </head>
    <body>
        <div class="container">

            <!-- Header -->
            <%@ include file="../admin/header_admin.jsp" %>

            <!-- Sidebar + Main -->
            <div class="content">
                <!-- Sidebar filters -->
                <aside class="sidebar" style="margin-top: 70px">
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
                                <label class="radio-container">
                                    <input type="radio" name="status" value="pending"
                                           <%= "pending".equals(request.getAttribute("selectedStatus")) ? "checked" : "" %>
                                           onchange="this.form.submit()" />
                                    <span class="custom-radio"></span>
                                    Chờ phê duyệt
                                </label>           
                            </div>
                        </div>

                        <!-- Chi nhánh -->
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
                                <option value="<%= b.getBranchName() %>"
                                        <%= b.getBranchName().equals(selectedBranch) ? "selected" : "" %>>
                                    <%= b.getBranchName() %>
                                </option>
                                <%
                                        }
                                    }
                                %>
                            </select>
                        </div>

                        <!-- Vai trò -->
                        <div class="filter-group">
                            <select name="role" onchange="this.form.submit()">
                                <option value="None" <%= "None".equals(request.getAttribute("selectedRole")) ? "selected" : "" %>>
                                    Tất cả vai trò
                                </option>
                                <%
                                    List<Role> roles = (List<Role>) request.getAttribute("roles");
                                    String selectedRole = (String) request.getAttribute("selectedRole");
                                    if (roles != null) {
                                        for (Role r : roles) {
                                %>
                                <option value="<%= r.getRoleName() %>"
                                        <%= r.getRoleName().equals(selectedRole) ? "selected" : "" %>>
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
                <main class="main-content" style="margin-top: 13px;">
                    <div class="toolbar">
                        <form action="NhanVien" method="get" class="search-bar">
                            <div>
                                <input type="text" name="search"
                                       placeholder="Tìm theo mã, tên nhân viên"
                                       value="<%= request.getAttribute("searchKeyword") != null ? request.getAttribute("searchKeyword") : "" %>" />
                            </div>
                        </form>
                        <div class="btn-group">
                            <a href="AddUser" class="btn add">Thêm nhân viên</a>
                            <a href="ShiftUser" class="btn add">Ca làm</a>                          
                        </div>                       

                    </div>

                    <table class="emp-table">
                        <thead>
                            <tr>
                                <th>Mã nhân viên</th>
                                <th>Tên nhân viên</th>
                                <th>Chi nhánh / Kho</th>
                                <th>Chức danh</th>
                                <th>SĐT</th>                              
                                <th>CMND/CCCD</th>
                                <th>Email</th>
                                <th style="text-align: center";>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                List<User> users = (List<User>) request.getAttribute("users");
                                if (users != null && !users.isEmpty()) {
                                    for (User u : users) {
                            %>
                            <tr>
                                <td><%= u.getUserId() %></td>
                                <td><%= u.getFullName() %></td>
                                <td>
                                    <% 
                                        String icon = "";
                                        String displayLocation = "Chưa có";
                                        if (u.getRoleName() != null) {
                                            if (u.getRoleName().toLowerCase().contains("chi nhánh") || u.getRoleName().toLowerCase().contains("bán hàng")) {
                                                icon = "<i class='fas fa-building'></i> ";
                                                displayLocation = (u.getBranchName() != null) ? u.getBranchName() : "Chưa có chi nhánh";
                                            } else if (u.getRoleName().toLowerCase().contains("kho")) {
                                                icon = "<i class='fas fa-warehouse'></i> ";
                                                displayLocation = (u.getWarehouseName() != null) ? u.getWarehouseName() : "Chưa có kho";
                                            }
                                        }
                                    %>
                                    <%= icon %><%= displayLocation %>
                                </td>
                                <td><%= u.getRoleName() != null ? u.getRoleName() : "Chưa có" %></td>
                                <td><%= u.getPhone() != null ? u.getPhone() : "Chưa có" %></td>                               
                                <td><%= u.getIdentificationId() != null ? u.getIdentificationId() : "Chưa có" %></td>
                                <td><%= u.getEmail() != null ? u.getEmail() : "Chưa có" %></td>
                                <td class="action-cell">
                                    <a href="EditUser?userId=<%= u.getUserId() %>" class="btn-detail">
                                        <i class="fas fa-eye"></i> Chi tiết
                                    </a>
                                </td>
                            </tr>
                            <%
                                    }
                                } else {
                            %>
                            <tr>
                                <td colspan="8" class="empty">
                                    Hiện chưa có nhân viên nào hoặc kết quả tìm kiếm không tồn tại.
                                </td>
                            </tr>
                            <%
                                }
                            %>
                        </tbody>
                    </table>

                    <!-- Pagination -->
                    <div class="pagination">
                        <%
                            Integer currentPage = (Integer) request.getAttribute("currentPage");
                            Integer totalPages = (Integer) request.getAttribute("totalPages");
                            if (currentPage != null && totalPages != null && totalPages > 1) {
                        %>
                        <div>
                            <% if (currentPage > 1) { %>
                            <a href="NhanVien?page=<%= currentPage - 1 %>">&laquo; Trước</a>
                            <% } %>

                            <% for (int i = 1; i <= totalPages; i++) { %>
                            <% if (i == currentPage) { %>
                            <span class="current"><%= i %></span>
                            <% } else { %>
                            <a href="NhanVien?page=<%= i %>"><%= i %></a>
                            <% } %>
                            <% } %>

                            <% if (currentPage < totalPages) { %>
                            <a href="NhanVien?page=<%= currentPage + 1 %>">Sau &raquo;</a>
                            <% } %>
                        </div>
                        <%
                            }
                        %>
                    </div>
                </main>
            </div>

            <!-- Footer -->
            <footer class="info-bar">
                <p></p>
            </footer>
        </div>
    </body>
</html>
