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
                <div class="header-top">
                    <div class="header-top-left">
                        <span>🔙 Người hàng giá lót</span>
                        <span>💬 Chờ đề</span>
                        <span>❓ Hỗ trợ</span>
                        <span>📋 Góp ý</span>
                        <span>📧 Giao diện mới</span>
                    </div>
                    <div class="header-top-right">
                        <span>🇻🇳 Tiếng Việt</span>
                        <span>⚙️</span>
                        <span>0923391668</span>
                        <span>👤</span>
                    </div>
                </div>
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
                        <a href="#" class="nav-item">
                            <span class="icon-products"></span>
                            Hàng hóa
                        </a>
                        <a href="#" class="nav-item">
                            <span class="icon-transactions"></span>
                            Giao dịch
                        </a>
                        <a href="#" class="nav-item">
                            <span class="icon-partners"></span>
                            Đối tác
                        </a>
                        <a href="#" class="nav-item active">
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
                            <select>
                                <option>Chọn chi nhánh...</option>
                            </select>
                        </div>
                        <div class="filter-group">
                            <select>
                                <option>Phòng ban</option>
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
                            <button class="btn add">+ Nhân viên</button>
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
                                <td>Chưa có</td>   
                                <td><%= u.getDepartmentName() != null ? u.getDepartmentName() : "Chưa có" %></td>
                                <td><%= u.getRoleName() != null ? u.getRoleName() : "Chưa có" %></td>
                                <td><%= u.getPhone() != null ? u.getPhone() : "Chưa có" %></td>
                                <td><%= u.getIdentifierCode() != null ? u.getIdentifierCode() : "Chưa có" %></td>
                                <td><%= u.getEmail() != null ? u.getEmail() : "Chưa có" %></td>
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
