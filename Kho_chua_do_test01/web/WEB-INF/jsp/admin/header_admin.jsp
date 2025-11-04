<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!-- Ensure fonts and icons are loaded -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />
<link rel="preconnect" href="https://fonts.googleapis.com" />
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet" />

<style>
    * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
    }

    body {
        font-family: 'Inter', 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    }

    /* Header Navigation */
    .header {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        z-index: 1000;
    }

    .header-main {
        padding: 0.75rem 2rem;
        display: flex;
        align-items: center;
        gap: 2rem;
        max-width: 1920px;
        margin: 0 auto;
    }

    .logo {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        font-size: 1.5rem;
        font-weight: 700;
        text-decoration: none;
        color: white;
        transition: transform 0.3s ease;
    }

    .logo:hover {
        transform: scale(1.05);
    }

    .logo-icon {
        background: linear-gradient(135deg, #10b981 0%, #059669 100%);
        padding: 0.5rem;
        border-radius: 12px;
        width: 48px;
        height: 48px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 1.75rem;
        box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
    }

    .nav-menu {
        display: flex;
        gap: 0.5rem;
        flex: 1;
        align-items: center;
    }

    .nav-item {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.75rem 1.25rem;
        color: rgba(255,255,255,0.95);
        text-decoration: none;
        border-radius: 8px;
        transition: all 0.3s ease;
        font-weight: 500;
        font-size: 14px;
        position: relative;
        cursor: pointer;
    }

    .nav-item:hover {
        background: rgba(255,255,255,0.15);
        color: white;
        transform: translateY(-2px);
    }

    .nav-item.active {
        background: rgba(255,255,255,0.2);
        color: white;
    }

    /* Dropdown Navigation */
    .nav-dropdown {
        position: relative;
        display: inline-block;
    }

    .dropdown-toggle {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.75rem 1.25rem;
        color: rgba(255,255,255,0.95);
        text-decoration: none;
        border-radius: 8px;
        transition: all 0.3s ease;
        font-weight: 500;
        font-size: 14px;
        cursor: pointer;
        background: transparent;
        border: none;
    }

    .dropdown-toggle:hover {
        background: rgba(255,255,255,0.15);
        color: white;
        transform: translateY(-2px);
    }

    .nav-dropdown.active .dropdown-toggle,
    .nav-dropdown.show .dropdown-toggle {
        background: rgba(255,255,255,0.2);
        color: white;
    }

    .nav-dropdown .dropdown-menu {
        position: absolute !important;
        top: 100% !important;
        left: 0 !important;
        background: white !important;
        border-radius: 10px !important;
        box-shadow: 0 10px 30px rgba(0,0,0,0.15) !important;
        min-width: 220px !important;
        margin-top: 10px !important;
        display: none !important;
        z-index: 99999 !important;
        overflow: hidden !important;
        visibility: hidden !important;
        opacity: 0 !important;
        transition: opacity 0.3s ease, visibility 0.3s ease !important;
    }

    /* Show dropdown when active */
    .nav-dropdown.show .dropdown-menu,
    .nav-dropdown.active .dropdown-menu {
        display: block !important;
        visibility: visible !important;
        opacity: 1 !important;
    }

    @keyframes fadeIn {
        from {
            opacity: 0;
            transform: translateY(-10px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }

    .dropdown-item {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        padding: 12px 18px;
        color: #374151;
        text-decoration: none;
        font-size: 14px;
        font-weight: 500;
        transition: all 0.2s ease;
        border-left: 3px solid transparent;
    }

    .dropdown-item:hover {
        background: linear-gradient(90deg, #f3f4f6 0%, #e5e7eb 100%);
        color: #667eea;
        border-left-color: #667eea;
        padding-left: 22px;
    }

    .dropdown-item.active {
        background: linear-gradient(90deg, #ede9fe 0%, #ddd6fe 100%);
        color: #667eea;
        border-left-color: #667eea;
        font-weight: 600;
    }

    .dropdown-item i {
        width: 20px;
        text-align: center;
    }

    /* User Dropdown */
    .header-right {
        display: flex;
        align-items: center;
        gap: 1rem;
    }

    .user-dropdown {
        position: relative;
    }

    .user-icon {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.5rem 1rem;
        background: rgba(255,255,255,0.1);
        border-radius: 50px;
        cursor: pointer;
        transition: all 0.3s ease;
        text-decoration: none;
        color: white;
    }

    .user-icon:hover {
        background: rgba(255,255,255,0.2);
        transform: translateY(-2px);
    }

    .user-avatar {
        width: 36px;
        height: 36px;
        border-radius: 50%;
        background: linear-gradient(135deg, #10b981 0%, #059669 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: 700;
        font-size: 14px;
        box-shadow: 0 2px 8px rgba(16, 185, 129, 0.3);
    }

    .user-info {
        display: flex;
        flex-direction: column;
        align-items: flex-start;
    }

    .user-name {
        font-weight: 600;
        font-size: 13px;
        line-height: 1.2;
    }

    .user-role {
        font-size: 11px;
        opacity: 0.8;
    }

    .user-dropdown .dropdown-menu {
        position: absolute !important;
        right: 0 !important;
        left: auto !important;
        top: 100% !important;
        min-width: 200px !important;
        background: white !important;
        border-radius: 10px !important;
        box-shadow: 0 10px 30px rgba(0,0,0,0.15) !important;
        margin-top: 10px !important;
        display: none !important;
        z-index: 99999 !important;
        visibility: hidden !important;
        opacity: 0 !important;
        transition: opacity 0.3s ease, visibility 0.3s ease !important;
    }

    /* Show user dropdown when active */
    .user-dropdown.show .dropdown-menu,
    .user-dropdown.active .dropdown-menu {
        display: block !important;
        visibility: visible !important;
        opacity: 1 !important;
    }

    .dropdown-divider {
        height: 1px;
        background: #e5e7eb;
        margin: 6px 0;
    }

    .dropdown-item.logout {
        color: #ef4444;
    }

    .dropdown-item.logout:hover {
        background: #fee2e2;
        border-left-color: #ef4444;
    }

    /* Main content padding to account for fixed header */
    .main-content {
        padding-top: 80px;
    }

    /* Responsive */
    @media (max-width: 1200px) {
        .user-info {
            display: none;
        }
    }

    @media (max-width: 768px) {
        .header-main {
            padding: 0.5rem 1rem;
            gap: 1rem;
        }

        .nav-menu {
            gap: 0.25rem;
        }

        .nav-item,
        .dropdown-toggle {
            padding: 0.5rem 0.75rem;
            font-size: 13px;
        }

        .logo {
            font-size: 1.25rem;
        }

        .logo-icon {
            width: 40px;
            height: 40px;
        }
    }
</style>

<header class="header">
    <div class="header-main">
        <a href="${pageContext.request.contextPath}/TongQuan" class="logo">
            <div class="logo-icon">
                🏢
            </div>
            <span>VM</span>
        </a>

        <nav class="nav-menu">
            <a href="${pageContext.request.contextPath}/TongQuan" 
               class="nav-item ${fn:contains(pageContext.request.requestURI, 'TongQuan') ? 'active' : ''}">
                <i class="fas fa-chart-line"></i>
                Tổng quan
            </a>

            <a href="${pageContext.request.contextPath}/product" 
               class="nav-item ${fn:contains(pageContext.request.requestURI, 'product') ? 'active' : ''}">
                <i class="fas fa-box"></i>
                Hàng hóa
            </a>

            <!-- Giao dịch Dropdown with Role-based Items -->
            <div class="nav-dropdown ${fn:contains(pageContext.request.requestURI, 'Orders') 
                                       or fn:contains(pageContext.request.requestURI, 'import-request') 
                                       or fn:contains(pageContext.request.requestURI, 'stock-movements') 
                                       ? 'active' : ''}">
                <a href="javascript:void(0);" class="dropdown-toggle" >
                    <i class="fas fa-exchange-alt"></i>
                    Giao dịch
                    <i class="fas fa-chevron-down" style="font-size: 12px; margin-left: 4px;"></i>
                </a>
                <div class="dropdown-menu">
                    <a href="${pageContext.request.contextPath}/Orders" 
                       class="dropdown-item ${fn:contains(pageContext.request.requestURI, 'Orders') ? 'active' : ''}">
                        <i class="fas fa-shopping-cart"></i>
                        Đơn hàng
                    </a>

                    <c:choose>
                        <c:when test="${sessionScope.currentUser.roleId == 0}">
                            <!-- Admin: Import from Supplier -->
                            <a href="${pageContext.request.contextPath}/import-request" 
                               class="dropdown-item ${fn:contains(pageContext.request.requestURI, 'import-request') ? 'active' : ''}">
                                <i class="fas fa-download"></i>
                                Nhập từ NCC
                            </a>
                            <a href="${pageContext.request.contextPath}/stock-movements" 
                               class="dropdown-item ${fn:contains(pageContext.request.requestURI, 'stock-movements') ? 'active' : ''}">
                                <i class="fas fa-list-alt"></i>
                                Danh sách nhập
                            </a>
                        </c:when>
                        <c:when test="${sessionScope.currentUser.roleId == 1}">
                            <!-- Branch Manager: Export from Warehouse -->
                            <a href="${pageContext.request.contextPath}/import-request" 
                               class="dropdown-item ${fn:contains(pageContext.request.requestURI, 'import-request') ? 'active' : ''}">
                                <i class="fas fa-upload"></i>
                                Xuất từ kho
                            </a>
                            <a href="${pageContext.request.contextPath}/stock-movements" 
                               class="dropdown-item ${fn:contains(pageContext.request.requestURI, 'stock-movements') ? 'active' : ''}">
                                <i class="fas fa-list-alt"></i>
                                Danh sách xuất
                            </a>
                        </c:when>
                    </c:choose>
                </div>
            </div>

            <!-- Đối tác Dropdown -->
            <div class="nav-dropdown ${fn:contains(pageContext.request.requestURI, 'Supplier') 
                                       or fn:contains(pageContext.request.requestURI, 'Customer') 
                                       ? 'active' : ''}">
                <a href="javascript:void(0);" class="dropdown-toggle" >
                    <i class="fas fa-handshake"></i>
                    Đối tác
                    <i class="fas fa-chevron-down" style="font-size: 12px; margin-left: 4px;"></i>
                </a>
                <div class="dropdown-menu">
                    <a href="${pageContext.request.contextPath}/Customer" 
                       class="dropdown-item ${fn:contains(pageContext.request.requestURI, 'Customer') ? 'active' : ''}">
                        <i class="fas fa-user-friends"></i>
                        Khách hàng
                    </a>
                    <a href="${pageContext.request.contextPath}/Supplier" 
                       class="dropdown-item ${fn:contains(pageContext.request.requestURI, 'Supplier') ? 'active' : ''}">
                        <i class="fas fa-truck"></i>
                        Nhà cung cấp
                    </a>
                </div>
            </div>

            <a href="${pageContext.request.contextPath}/NhanVien" 
               class="nav-item ${fn:contains(pageContext.request.requestURI, 'NhanVien') ? 'active' : ''}">
                <i class="fas fa-users"></i>
                Nhân viên
            </a>

            <a href="${pageContext.request.contextPath}/Promotions" 
               class="nav-item ${fn:contains(pageContext.request.requestURI, 'Promotions') ? 'active' : ''}">
                <i class="fas fa-tags"></i>
                Khuyến mãi
            </a>

            <a href="#" class="nav-item">
                <i class="fas fa-chart-bar"></i>
                Báo cáo
            </a>
        </nav>

        <div class="header-right">
            <div class="user-dropdown">
                <a href="javascript:void(0);" class="user-icon" id="userDropdownToggle" >
                    <div class="user-avatar">
                        ${sessionScope.currentUser.fullName.substring(0,1).toUpperCase()}
                    </div>
                    <div class="user-info">
                        <span class="user-name">${sessionScope.currentUser.fullName}</span>
                        <span class="user-role">
                            <c:choose>
                                <c:when test="${sessionScope.currentUser.roleId == 0}">Admin</c:when>
                                <c:when test="${sessionScope.currentUser.roleId == 1}">Quản lý CN</c:when>
                                <c:otherwise>Nhân viên</c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                    <i class="fas fa-chevron-down" style="font-size: 12px;"></i>
                </a>
                <div class="dropdown-menu" id="userDropdownMenu">
                    <%
  Model.User cu = (Model.User) session.getAttribute("currentUser");
  Integer roleId = (cu != null) ? cu.getRoleId() : null;
  String ctx = pageContext.getRequest().getServletContext().getContextPath();
                    %>
                    <% if (roleId != null && roleId == 0) { %>
                    <a href="<%= ctx %>/InformationAccount" class="dropdown-item">
                        <i class="fas fa-user-circle"></i> Thông tin tài khoản
                    </a>
                    <% } else if (roleId != null && roleId == 1) { %>
                    <a href="<%= ctx %>/InformationAccountBM" class="dropdown-item">
                        <i class="fas fa-user-circle"></i> Thông tin tài khoản
                    </a>
                    <% } else { %>
                    <a href="<%= ctx %>/InformationAccount" class="dropdown-item">
                        <i class="fas fa-user-circle"></i> Thông tin tài khoản
                    </a>
                    <% } %>
                    <div class="dropdown-divider"></div>
                    <a href="Logout" class="dropdown-item logout">
                        <i class="fas fa-sign-out-alt"></i>
                        Đăng xuất
                    </a>
                </div>
            </div>
        </div>
    </div>
</header>

<script>
    (function () {
        'use strict';

        console.log('Initializing dropdowns...');

        // Wait for DOM
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', init);
        } else {
            init();
        }

        function init() {
            var navDropdowns = document.querySelectorAll('.nav-dropdown');
            var userDropdown = document.querySelector('.user-dropdown');
            var userToggle = document.getElementById('userDropdownToggle');

            console.log('Found', navDropdowns.length, 'nav dropdowns');
            console.log('Found user dropdown:', !!userDropdown);

            // Setup nav dropdowns
            navDropdowns.forEach(function (dropdown, index) {
                var toggle = dropdown.querySelector('.dropdown-toggle');
                if (toggle) {
                    console.log('Setting up nav dropdown', index);

                    // Prevent both mousedown and click
                    toggle.addEventListener('click', function (e) {
                        e.preventDefault();
                        e.stopPropagation();

                        console.log('Nav dropdown', index, 'clicked');

                        var isOpen = dropdown.classList.contains('show');
                        console.log('Is currently open:', isOpen);

                        // Close all dropdowns first
                        closeAllDropdowns();

                        // Open this one if it was closed
                        if (!isOpen) {
                            dropdown.classList.add('show');
                            console.log('Opened nav dropdown', index);
                            console.log('Dropdown classes:', dropdown.className);
                            console.log('Dropdown menu display:', window.getComputedStyle(dropdown.querySelector('.dropdown-menu')).display);
                        } else {
                            console.log('Was open, now closed');
                        }

                        return false;
                    });

                    // Also prevent mousedown from causing double trigger
                    toggle.addEventListener('mousedown', function (e) {
                        e.preventDefault();
                    });
                }
            });

            // Setup user dropdown
            if (userToggle && userDropdown) {
                console.log('Setting up user dropdown');

                userToggle.addEventListener('click', function (e) {
                    e.preventDefault();
                    e.stopPropagation();

                    console.log('User dropdown clicked');

                    var isOpen = userDropdown.classList.contains('show');
                    console.log('User dropdown is currently open:', isOpen);

                    // Close all dropdowns
                    closeAllDropdowns();

                    // Open if it was closed
                    if (!isOpen) {
                        userDropdown.classList.add('show');
                        console.log('Opened user dropdown');
                    }

                    return false;
                });

                // Prevent mousedown from causing issues
                userToggle.addEventListener('mousedown', function (e) {
                    e.preventDefault();
                });
            }

            // Close all dropdowns
            function closeAllDropdowns() {
                navDropdowns.forEach(function (d) {
                    d.classList.remove('show');
                });
                if (userDropdown) {
                    userDropdown.classList.remove('show');
                }
            }

            // Close on outside click
            document.addEventListener('click', function (e) {
                var clickedInsideDropdown = false;

                // Check if clicked inside any dropdown
                navDropdowns.forEach(function (dropdown) {
                    if (dropdown.contains(e.target)) {
                        clickedInsideDropdown = true;
                    }
                });

                if (userDropdown && userDropdown.contains(e.target)) {
                    clickedInsideDropdown = true;
                }

                // Close if clicked outside
                if (!clickedInsideDropdown) {
                    console.log('Clicked outside, closing all');
                    closeAllDropdowns();
                }
            });

            // Prevent dropdown menu from closing when clicking inside (but allow links to work)
            var allMenus = document.querySelectorAll('.dropdown-menu');
            allMenus.forEach(function (menu) {
                menu.addEventListener('click', function (e) {
                    // Only prevent if clicking on menu container, not links
                    if (e.target.tagName !== 'A' && !e.target.closest('a')) {
                        e.stopPropagation();
                        console.log('Clicked inside menu container');
                    }
                });
            });

            console.log('Dropdown initialization complete');
        }
    })();
</script>
