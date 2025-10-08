<%@ page contentType="text/html;charset=UTF-8" language="java" %>

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
        color: #fff;
    }

    .gradient {
        background: linear-gradient(45deg, #007bff, #00aaff);
        border-radius: 50%;
        width: 38px;
        height: 38px;
        display: flex;
        align-items: center;
        justify-content: center;
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

        <div class="header-right">
            <div class="user-dropdown">
                <a href="#" class="user-icon gradient" id="dropdownToggle">
                    <i class="fas fa-user-circle fa-2x"></i>
                </a>
                <div class="dropdown-menu" id="dropdownMenu">
                    <a href="InformationAccount" class="dropdown-item">Thông tin chi tiết</a>
                    <a href="logout" class="dropdown-item">Đăng xuất</a>
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



