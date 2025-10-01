<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.Log" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>TSMS - Tổng quan</title>
        <link href="css/TongQuan.css" rel="stylesheet" type="text/css"/>

    </head>
    <body>
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
                    <a href="TongQuan" class="nav-item active">
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
                    <a href="NhanVien" class="nav-item">
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

        <!-- Main Content -->
        <main class="main-content">
            <div class="content-left">
                <!-- Stats Section -->
                <section class="stats-section">
                    <h2 class="stats-title">KẾT QUẢ BÁN HÀNG HÔM NAY</h2>
                    <div class="stats-grid">
                        <div class="stat-card">
                            <div class="stat-icon revenue">💰</div>
                            <div class="stat-content">
                                <h3>4,886,000</h3>
                                <p>1 Hóa đơn - Doanh thu</p>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon orders">📋</div>
                            <div class="stat-content">
                                <h3>0</h3>
                                <p>0 phiếu - Trả hàng</p>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon growth">📈</div>
                            <div class="stat-content">
                                <h3>250.00%</h3>
                                <p>So với hôm qua</p>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon comparison">📊</div>
                            <div class="stat-content">
                                <h3>133.33%</h3>
                                <p>So với cùng kỳ tháng trước</p>
                            </div>
                        </div>
                    </div>
                </section>

                <!-- Chart Section -->
                <section class="chart-section">
                    <div class="chart-header">
                        <h2 class="chart-title">DOANH THU THUẦN THÁNG NÀY ℹ️</h2>
                        <div style="display: flex; gap: 1rem; align-items: center;">
                            <div class="chart-tabs">
                                <button class="chart-tab active">Theo ngày</button>
                                <button class="chart-tab">Theo giờ</button>
                                <button class="chart-tab">Theo thứ</button>
                            </div>
                            <select class="chart-dropdown">
                                <option>Tháng này</option>
                                <option>Tháng trước</option>
                                <option>3 tháng gần đây</option>
                            </select>
                        </div>
                    </div>
                    <div class="chart-container">
                        <div style="text-align: center;">
                            <div style="font-size: 3rem; margin-bottom: 1rem;">📊</div>
                            <p>Không có dữ liệu</p>
                        </div>
                    </div>
                </section>

                <!-- Top Products Section -->
                <section class="products-section">
                    <div class="products-header">
                        <h2 class="products-title">TOP 10 HÀNG HÓA BÁN CHẠY THÁNG NÀY</h2>
                        <div style="display: flex; gap: 1rem;">
                            <select class="chart-dropdown">
                                <option>THEO DOANH THU THUẦN</option>
                                <option>THEO SỐ LƯỢNG</option>
                            </select>
                            <select class="chart-dropdown">
                                <option>Tháng này</option>
                                <option>Tháng trước</option>
                            </select>
                        </div>
                    </div>
                    <div class="chart-container">
                        <div style="text-align: center;">
                            <div style="font-size: 3rem; margin-bottom: 1rem;">📦</div>
                            <p>Chưa có dữ liệu sản phẩm</p>
                        </div>
                    </div>
                </section>
            </div>

            <!-- Right Sidebar -->
            <aside class="sidebar">
                <!-- Promotion Card -->
                <div class="sidebar-card promo-card">
                    <div class="promo-content">
                        <h3>Nhận Hóa đơn điện tử</h3>
                        <p>8 chữ ký số - MIỄN PHÍ</p>
                        <button class="promo-button">Kích hoạt ngay</button>
                    </div>
                </div>

                <!-- QR Code Section -->
                <div class="sidebar-card qr-section">
                    <h3>TSMS ra mắt kênh CSKH</h3>
                    <p>Zalo Official Account</p>
                    <div class="qr-code">📱</div>
                    <button class="promo-button" style="background: #2563eb; color: white; border: none; padding: 0.5rem 1rem; border-radius: 0.5rem;">QUÉT TẠI ĐÂY</button>
                </div>

                <!-- Notifications -->
                <div class="sidebar-card notifications-section">
                    <h3>THÔNG BÁO</h3>

                    <%
                        List<Model.Log> logs = (List<Model.Log>) request.getAttribute("logs");
                        if (logs != null && !logs.isEmpty()) {
                            for (Model.Log log : logs) {
                    %>
                    <div class="notification-item">
                        <div class="notification-icon warning">⚠️</div>
                        <div class="notification-content">
                            <p>
                                [<%= log.getTableName() %>]
                                <strong><%= log.getAction() %></strong>
                                (Record ID: <%= log.getRecordId() %>)
                            </p>
                            <div class="time">
                                <%= log.getCreatedAt() %>
                            </div>
                        </div>
                    </div>
                    <%
                            }
                        } else {
                    %>
                    <p>Không có thông báo nào.</p>
                    <%
                        }
                    %>
                </div>

                <!-- Recent Activities -->
                <div class="sidebar-card activities-section">
                    <h3>CÁC HOẠT ĐỘNG GẦN ĐÂY</h3>
                    <div class="activity-item">
                        <div class="activity-icon error">🔴</div>
                        <div class="activity-content">
                            <p><strong>hoang minh kien</strong> vừa <strong>nhập hàng</strong> với giá trị <strong>0</strong></p>
                            <div class="time">41 phút trước</div>
                        </div>
                    </div>
                    <div class="activity-item">
                        <div class="activity-icon info">🔵</div>
                        <div class="activity-content">
                            <p><strong>hoang minh kien</strong> vừa <strong>bán đơn hàng</strong> với giá trị <strong>4,886,000</strong></p>
                            <div class="time">41 phút trước</div>
                        </div>
                    </div>
                    <div class="activity-item">
                        <div class="activity-icon info">🔵</div>
                        <div class="activity-content">
                            <p><strong>Nguyễn Lê Hùng Cường</strong> vừa <strong>bán đơn hàng</strong> với giá trị <strong>1,396,000</strong></p>
                            <div class="time">một ngày trước</div>
                        </div>
                    </div>
                    <div class="activity-item">
                        <div class="activity-icon error">🔴</div>
                        <div class="activity-content">
                            <p><strong>Nguyễn Lê Hùng Cường</strong> vừa <strong>nhập hàng</strong> với giá trị <strong>0</strong></p>
                            <div class="time">một ngày trước</div>
                        </div>
                    </div>
                </div>
            </aside>
        </main>

        <script>
            // Chart tabs functionality
            document.querySelectorAll('.chart-tab').forEach(tab => {
                tab.addEventListener('click', function () {
                    document.querySelectorAll('.chart-tab').forEach(t => t.classList.remove('active'));
                    this.classList.add('active');
                });
            });

            // Simulate real-time updates
            function updateStats() {
                // This would typically fetch data from your backend
                console.log('Updating dashboard stats...');
            }

            // Update stats every 30 seconds
            setInterval(updateStats, 30000);

            // Add click handlers for navigation
            document.querySelectorAll('.nav-item').forEach(item => {
                item.addEventListener('click', function (e) {
                    document.querySelectorAll('.nav-item').forEach(nav => nav.classList.remove('active'));
                    this.classList.add('active');
                });
            });
        </script>
    </body>
</html>
