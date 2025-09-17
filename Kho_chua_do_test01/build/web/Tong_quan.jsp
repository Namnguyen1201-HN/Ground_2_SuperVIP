<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TSMS - Tổng quan</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background-color: #f5f5f5;
            color: #333;
        }

        /* Header Navigation */
        .header {
            background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
            color: white;
            padding: 0;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }

        .header-top {
            background: #f8f9fa;
            color: #6b7280;
            padding: 0.5rem 2rem;
            font-size: 0.875rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .header-top-left {
            display: flex;
            gap: 1rem;
            align-items: center;
        }

        .header-top-right {
            display: flex;
            gap: 1rem;
            align-items: center;
        }

        .header-main {
            padding: 1rem 2rem;
            display: flex;
            align-items: center;
            gap: 2rem;
        }

        .logo {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            font-size: 1.5rem;
            font-weight: bold;
        }

        .logo-icon {
            background: #14b8a6;
            padding: 0.5rem;
            border-radius: 0.5rem;
            width: 40px;
            height: 40px;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .nav-menu {
            display: flex;
            gap: 0;
            flex: 1;
        }

        .nav-item {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            padding: 0.75rem 1.5rem;
            color: rgba(255,255,255,0.9);
            text-decoration: none;
            border-radius: 0.5rem;
            transition: all 0.2s;
            font-weight: 500;
        }

        .nav-item:hover, .nav-item.active {
            background: rgba(255,255,255,0.1);
            color: white;
        }

        .nav-item.active {
            background: rgba(255,255,255,0.2);
        }

        /* Main Content */
        .main-content {
            padding: 2rem;
            max-width: 1400px;
            margin: 0 auto;
            display: grid;
            grid-template-columns: 1fr 300px;
            gap: 2rem;
        }

        .content-left {
            display: flex;
            flex-direction: column;
            gap: 2rem;
        }

        /* Stats Cards */
        .stats-section {
            background: white;
            border-radius: 1rem;
            padding: 1.5rem;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }

        .stats-title {
            font-size: 1.125rem;
            font-weight: 600;
            margin-bottom: 1.5rem;
            color: #1f2937;
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 1.5rem;
        }

        .stat-card {
            display: flex;
            align-items: center;
            gap: 1rem;
            padding: 1rem;
            border-radius: 0.75rem;
            border: 1px solid #e5e7eb;
            transition: all 0.2s;
        }

        .stat-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }

        .stat-icon {
            width: 48px;
            height: 48px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.5rem;
        }

        .stat-icon.revenue { background: #dbeafe; color: #2563eb; }
        .stat-icon.orders { background: #fed7aa; color: #ea580c; }
        .stat-icon.growth { background: #dcfce7; color: #16a34a; }
        .stat-icon.comparison { background: #dcfce7; color: #16a34a; }

        .stat-content h3 {
            font-size: 1.5rem;
            font-weight: bold;
            color: #1f2937;
            margin-bottom: 0.25rem;
        }

        .stat-content p {
            color: #6b7280;
            font-size: 0.875rem;
        }

        /* Chart Section */
        .chart-section {
            background: white;
            border-radius: 1rem;
            padding: 1.5rem;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }

        .chart-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1.5rem;
        }

        .chart-title {
            font-size: 1.125rem;
            font-weight: 600;
            color: #1f2937;
        }

        .chart-tabs {
            display: flex;
            gap: 0.5rem;
        }

        .chart-tab {
            padding: 0.5rem 1rem;
            border: 1px solid #e5e7eb;
            border-radius: 0.5rem;
            background: white;
            color: #6b7280;
            cursor: pointer;
            transition: all 0.2s;
            font-size: 0.875rem;
        }

        .chart-tab.active, .chart-tab:hover {
            background: #2563eb;
            color: white;
            border-color: #2563eb;
        }

        .chart-dropdown {
            padding: 0.5rem 1rem;
            border: 1px solid #e5e7eb;
            border-radius: 0.5rem;
            background: white;
            color: #374151;
            cursor: pointer;
        }

        .chart-container {
            height: 300px;
            display: flex;
            align-items: center;
            justify-content: center;
            background: #f9fafb;
            border-radius: 0.5rem;
            color: #9ca3af;
        }

        /* Top Products Section */
        .products-section {
            background: white;
            border-radius: 1rem;
            padding: 1.5rem;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }

        .products-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1.5rem;
        }

        .products-title {
            font-size: 1.125rem;
            font-weight: 600;
            color: #1f2937;
        }

        /* Right Sidebar */
        .sidebar {
            display: flex;
            flex-direction: column;
            gap: 1.5rem;
        }

        .sidebar-card {
            background: white;
            border-radius: 1rem;
            padding: 1.5rem;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }

        .promo-card {
            background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
            color: white;
            position: relative;
            overflow: hidden;
        }

        .promo-content h3 {
            font-size: 1.125rem;
            font-weight: 600;
            margin-bottom: 0.5rem;
        }

        .promo-content p {
            font-size: 0.875rem;
            opacity: 0.9;
            margin-bottom: 1rem;
        }

        .promo-button {
            background: rgba(255,255,255,0.2);
            color: white;
            padding: 0.5rem 1rem;
            border: none;
            border-radius: 0.5rem;
            cursor: pointer;
            font-weight: 500;
            transition: all 0.2s;
        }

        .promo-button:hover {
            background: rgba(255,255,255,0.3);
        }

        .qr-section {
            text-align: center;
        }

        .qr-code {
            width: 120px;
            height: 120px;
            background: #f3f4f6;
            border-radius: 0.5rem;
            margin: 1rem auto;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 3rem;
        }

        .notifications-section h3,
        .activities-section h3 {
            font-size: 1rem;
            font-weight: 600;
            color: #1f2937;
            margin-bottom: 1rem;
        }

        .notification-item,
        .activity-item {
            display: flex;
            align-items: flex-start;
            gap: 0.75rem;
            padding: 0.75rem 0;
            border-bottom: 1px solid #f3f4f6;
        }

        .notification-item:last-child,
        .activity-item:last-child {
            border-bottom: none;
        }

        .notification-icon,
        .activity-icon {
            width: 32px;
            height: 32px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 0.875rem;
            flex-shrink: 0;
        }

        .notification-icon.warning { background: #fef3c7; color: #d97706; }
        .activity-icon.info { background: #dbeafe; color: #2563eb; }
        .activity-icon.success { background: #dcfce7; color: #16a34a; }
        .activity-icon.error { background: #fee2e2; color: #dc2626; }

        .notification-content,
        .activity-content {
            flex: 1;
        }

        .notification-content p,
        .activity-content p {
            font-size: 0.875rem;
            color: #374151;
            line-height: 1.4;
        }

        .notification-content .time,
        .activity-content .time {
            font-size: 0.75rem;
            color: #9ca3af;
            margin-top: 0.25rem;
        }

        /* Icons using CSS */
        .icon-overview::before { content: "📊"; }
        .icon-products::before { content: "📦"; }
        .icon-transactions::before { content: "💳"; }
        .icon-partners::before { content: "🤝"; }
        .icon-staff::before { content: "👥"; }
        .icon-cashbook::before { content: "💰"; }
        .icon-reports::before { content: "📈"; }
        .icon-online::before { content: "🌐"; }
        .icon-sales::before { content: "🛒"; }
        .icon-building::before { content: "🏢"; }

        /* Responsive Design */
        @media (max-width: 1200px) {
            .main-content {
                grid-template-columns: 1fr;
                padding: 1rem;
            }
            
            .sidebar {
                grid-row: 1;
            }
        }

        @media (max-width: 768px) {
            .header-main {
                padding: 1rem;
                flex-direction: column;
                gap: 1rem;
            }
            
            .nav-menu {
                flex-wrap: wrap;
                justify-content: center;
            }
            
            .stats-grid {
                grid-template-columns: 1fr;
            }
            
            .chart-header {
                flex-direction: column;
                gap: 1rem;
                align-items: flex-start;
            }
        }
    </style>
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
                <span>TSMS</span>
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
                <div class="notification-item">
                    <div class="notification-icon warning">⚠️</div>
                    <div class="notification-content">
                        <p>Có <strong>1 hoạt động đăng nhập khác thường</strong> cần kiểm tra.</p>
                        <div class="time">2 phút trước</div>
                    </div>
                </div>
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
            tab.addEventListener('click', function() {
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
            item.addEventListener('click', function(e) {
                e.preventDefault();
                document.querySelectorAll('.nav-item').forEach(nav => nav.classList.remove('active'));
                this.classList.add('active');
            });
        });
    </script>
</body>
</html>
