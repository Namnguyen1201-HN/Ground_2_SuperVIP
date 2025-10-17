<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Hàng hóa - WR</title>
    <link rel="stylesheet" href="styles.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sale/sale.css">
     <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>
<body>
  
<style>

    /* Header Navigation */
    .header {
        background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
        color: white;
        padding: 0px;
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

    div.header-main{
        margin-bottom: 10px;
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

    /* Icons using CSS */
    .icon-overview::before {
        content: "📊";
    }
    .icon-products::before {
        content: "📦";
    }
    .icon-transactions::before {
        content: "💳";
    }
    .icon-partners::before {
        content: "🤝";
    }
    .icon-staff::before {
        content: "👥";
    }
    .icon-cashbook::before {
        content: "💰";
    }
    .icon-reports::before {
        content: "📈";
    }
    .icon-online::before {
        content: "🌐";
    }
    .icon-sales::before {
        content: "🛒";
    }
    .icon-building::before {
        content: "🏢";
    }


    /* --- Dropdown người dùng --- */
    .user-dropdown {
        position: relative;
        display: inline-block;
    }

    .user-dropdown .dropdown-menu {
        position: absolute;
        top: 40px;
        right: 0;
        background: white;
        border: 1px solid #ddd;
        border-radius: 6px;
        box-shadow: 0 2px 6px rgba(0,0,0,0.15);
        min-width: 160px;
        display: none; /* Ẩn mặc định */
        z-index: 1000;
    }

    .user-dropdown.active .dropdown-menu {
        display: block; /* Hiện khi có class active */
    }

    .dropdown-item {
        display: block;
        padding: 10px 14px;
        color: #333;
        text-decoration: none;
        font-size: 14px;
    }

    .dropdown-item:hover {
        background: #f2f5ff;
        color: #0056d6;
    }

    /* Icon người dùng */
    .user-icon {
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        text-decoration: none;
    }

    .user-icon i {
        color: #fff !important;
    }

    .gradient {
        background: linear-gradient(45deg, #007bff, #00aaff) !important ;
        border-radius: 50%;
        width: 38px;
        height: 38px;
        display: flex;
        align-items: center;
        justify-content: center;
    }

    /* Giữ nguyên màu và kiểu icon user trong header */
    .header .user-icon i {
        color: #fff !important;
    }
    .header .gradient {
        background: linear-gradient(45deg, #007bff, #00aaff) !important;
        border-radius: 50% !important;
    }


</style>

<header class="header">
    <div class="header-main">
        <div class="logo">
            <div class="logo-icon">
                <span class="icon-building"></span>
            </div>
            <span>WM</span>
        </div>
        <nav class="nav-menu">
            <a href="TongQuan" class="nav-item">
                <span class="icon-overview"></span>
                Tổng quan
            </a>
            <a href="product" class="nav-item">
                <span class="icon-products"></span>
                Hàng hóa
            </a>
            <a href="Transactions" class="nav-item">
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
                Khuyến mãi
            </a>
            <a href="#" class="nav-item">
                <span class="icon-reports"></span>
                Báo cáo
            </a>
            
        </nav>

        <div class="header-right">
            <div class="user-dropdown">
                <a href="#" class="user-icon gradient" id="dropdownToggle">
                    <i class="fas fa-user-circle fa-2x"></i>
                </a>
                <div class="dropdown-menu" id="dropdownMenu">
                    <a href="InformationAccount" class="dropdown-item">Thông tin chi tiết</a>
                    <a href="Login" class="dropdown-item">Đăng xuất</a>
                </div>
            </div>      
        </div>

    </div>
</header>

<script>
    document.addEventListener('DOMContentLoaded', function () {
        const dropdown = document.querySelector('.user-dropdown');
        const toggle = document.getElementById('dropdownToggle');
        const menu = document.getElementById('dropdownMenu');

        toggle.addEventListener('click', function (e) {
            e.preventDefault();
            dropdown.classList.toggle('active');
        });

        document.addEventListener('click', function (e) {
            if (!dropdown.contains(e.target)) {
                dropdown.classList.remove('active');
            }
        });
    }); // ✅ đóng hàm
</script>

    <div class="container">
        <!-- Sidebar -->
        <aside class="sidebar">
            <div class="sidebar-section">
                <h3 class="sidebar-title">Nhóm hàng</h3>
                <div class="sidebar-tabs">
                    <button class="tab-btn active">Tạo mới</button>
                    <button class="tab-btn">Tất cả</button>
                </div>
                <input type="text" class="sidebar-input" placeholder="Chọn nhóm hàng">
            </div>

            <div class="sidebar-section">
                <h3 class="sidebar-title">Tồn kho</h3>
                <select class="sidebar-select">
                    <option>Tất cả</option>
                    <option>Còn hàng</option>
                    <option>Hết hàng</option>
                </select>
            </div>

            <div class="sidebar-section">
                <h3 class="sidebar-title">Dự kiến hết hàng</h3>
                <div class="filter-option">
                    <input type="radio" id="all-time" name="forecast" checked>
                    <label for="all-time">Toàn thời gian</label>
                </div>
                <div class="filter-option">
                    <input type="radio" id="custom-date" name="forecast">
                    <label for="custom-date">Tùy chỉnh</label>
                </div>
            </div>

            <div class="sidebar-section">
                <h3 class="sidebar-title">Thời gian tạo</h3>
                <div class="filter-option">
                    <input type="radio" id="create-all" name="create-time" checked>
                    <label for="create-all">Toàn thời gian</label>
                </div>
                <div class="filter-option">
                    <input type="radio" id="create-custom" name="create-time">
                    <label for="create-custom">Tùy chỉnh</label>
                </div>
            </div>

            <div class="sidebar-section">
                <h3 class="sidebar-title">Nhà cung cấp</h3>
                <input type="text" class="sidebar-input" placeholder="Chọn nhà cung cấp">
            </div>

            <div class="sidebar-section">
                <h3 class="sidebar-title">Vị trí</h3>
                <input type="text" class="sidebar-input" placeholder="Chọn vị trí">
            </div>

            <div class="sidebar-section">
                <h3 class="sidebar-title">Loại hàng</h3>
                <input type="text" class="sidebar-input" placeholder="Chọn loại hàng">
            </div>
        </aside>

        <!-- Main Content -->
        <main class="main-content">
            <div class="content-header">
                <h1 class="page-title">Hàng hóa</h1>
                <div class="search-bar">
                    <input type="text" placeholder="Theo mã, tên hàng" class="search-input">
                    <button class="search-btn">🔄</button>
                </div>
                <div class="action-buttons">
                    <button class="btn btn-primary">+ Tạo mới</button>
                    <button class="btn btn-secondary">📥 Import file</button>
                    <button class="btn btn-secondary">📤 Xuất file</button>
                    <button class="btn btn-secondary">☰</button>
                    <button class="btn btn-secondary">⚙️</button>
                    <button class="btn btn-secondary">❓</button>
                </div>
            </div>

            <!-- Table -->
            <div class="table-wrapper">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th><input type="checkbox"></th>
                            <th>⭐</th>
                            <th>Mã hàng</th>
                            <th>Tên hàng</th>
                            <th>Giá bán</th>
                            <th>Giá vốn</th>
                            <th>Tồn kho</th>
                            <th>Khách đặt</th>
                            <th>Thời gian tạo</th>
                            <th>Dự kiến hết hàng</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td><input type="checkbox"></td>
                            <td>⭐</td>
                            <td><span class="product-code">TB000014</span></td>
                            <td class="product-name">Samsung Galaxy Tab S9 5G 128GB</td>
                            <td>0</td>
                            <td>0</td>
                            <td>0</td>
                            <td>0</td>
                            <td>10/10/2025 16:30</td>
                            <td>---</td>
                        </tr>
                        <tr>
                            <td><input type="checkbox"></td>
                            <td>⭐</td>
                            <td><span class="product-code">TB000013</span></td>
                            <td class="product-name">Samsung Galaxy Tab S9 5G 128GB</td>
                            <td>0</td>
                            <td>0</td>
                            <td>0</td>
                            <td>0</td>
                            <td>10/10/2025 16:30</td>
                            <td>---</td>
                        </tr>
                        <tr>
                            <td><input type="checkbox"></td>
                            <td>⭐</td>
                            <td><span class="product-code">TB000012</span></td>
                            <td class="product-name">iPad Air 6 M2 11" 5G 128GB</td>
                            <td>0</td>
                            <td>0</td>
                            <td>0</td>
                            <td>0</td>
                            <td>10/10/2025 16:30</td>
                            <td>---</td>
                        </tr>
                        <tr>
                            <td><input type="checkbox"></td>
                            <td>⭐</td>
                            <td><span class="product-code">TB000011</span></td>
                            <td class="product-name">iPad Air 6 M2 11" 5G 128GB</td>
                            <td>0</td>
                            <td>0</td>
                            <td>0</td>
                            <td>0</td>
                            <td>10/10/2025 16:30</td>
                            <td>---</td>
                        </tr>
                        <tr>
                            <td><input type="checkbox"></td>
                            <td>⭐</td>
                            <td><span class="product-code">PK000020</span></td>
                            <td class="product-name">Giá đỡ Laptop/Macbook hợp kim nhôm</td>
                            <td>0</td>
                            <td>0</td>
                            <td>0</td>
                            <td>0</td>
                            <td>10/10/2025 16:30</td>
                            <td>---</td>
                        </tr>
                        <tr>
                            <td><input type="checkbox"></td>
                            <td>⭐</td>
                            <td><span class="product-code">PK000019</span></td>
                            <td class="product-name">Miếng dán kính cường lực iPhone 15 Pro Jincase</td>
                            <td>0</td>
                            <td>0</td>
                            <td>0</td>
                            <td>0</td>
                            <td>10/10/2025 16:30</td>
                            <td>---</td>
                        </tr>
                        <tr>
                            <td><input type="checkbox"></td>
                            <td>⭐</td>
                            <td><span class="product-code">PK000018</span></td>
                            <td class="product-name">Cáp Baseus Crystal Shine Type-C to Lightning 2M</td>
                            <td>0</td>
                            <td>0</td>
                            <td>0</td>
                            <td>0</td>
                            <td>10/10/2025 16:30</td>
                            <td>---</td>
                        </tr>
                        <tr>
                            <td><input type="checkbox"></td>
                            <td>⭐</td>
                            <td><span class="product-code">PK000017</span></td>
                            <td class="product-name">Ốp lưng iPhone 15 Pro Max Nhựa dẻo TPU UNIQ HYBRID Air Fender ID</td>
                            <td>0</td>
                            <td>0</td>
                            <td>0</td>
                            <td>0</td>
                            <td>10/10/2025 16:30</td>
                            <td>---</td>
                        </tr>
                        <tr>
                            <td><input type="checkbox"></td>
                            <td>⭐</td>
                            <td><span class="product-code">PK000016</span></td>
                            <td class="product-name">Chuột không dây</td>
                            <td>349,000</td>
                            <td>0</td>
                            <td>0</td>
                            <td>0</td>
                            <td>10/10/2025 16:30</td>
                            <td>---</td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <!-- Footer Notice -->
            
        </main>
    </div>

    <!-- Chat Button -->
    <button class="chat-button">💬 1900 6522</button>
</body>
</html>