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
            href="css/Nhan_vien.css"
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
                        <a href="#" class="nav-item active">
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
                        <a href="#" class="nav-item">
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
                    <div class="filter-group">

                        <label>Trạng thái nhân viên</label>
                        <div class="status-radios">
                            <label class="radio-container">
                                <input type="radio" name="status" value="active" checked />
                                <span class="custom-radio"></span>
                                Đang làm việc
                            </label>
                            <label class="radio-container">
                                <input type="radio" name="status" value="inactive" />
                                <span class="custom-radio"></span>
                                Đã nghỉ
                            </label>
                        </div>

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
                        <select>
                            <option>Chức danh</option>
                        </select>
                    </div>
                </aside>

                <!-- Main content -->
                <main class="main-content">
                    <div class="toolbar">
                        <div class="search-bar">
                            <input
                                type="text"
                                placeholder="Tìm theo mã, tên nhân viên"
                                />
                        </div>
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
                                <td>Chi nhánh A</td>    
                                <td>Phòng Kinh Doanh</td>
                                <td><%= u.getRoleName() %></td>
                                <td><%= u.getPhone() %></td>
                                <td>Chưa có</td>
                                <td><%= u.getEmail() %></td>
                            </tr>
                            <%
                                    }
                                } else {
                            %>
                            <tr>
                                <td colspan="8" class="empty">
                                    Gian hàng chưa có nhân viên. Vui lòng thêm mới nhân viên.
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
                    Tài khoản dùng thử hết hạn sau 2 ngày. Để sử dụng tiếp vui lòng liên hệ
                    với.
                </p>
            </footer>
        </div>

    </body>
</html>
