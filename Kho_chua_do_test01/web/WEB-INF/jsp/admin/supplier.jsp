<%-- 
    Document   : supplier
    Created on : Sep 24, 2025, 10:40:08 AM
    Author     : Kawaii
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý khách hàng - SWP391</title>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="css/admin/Supplier.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">

        <style>

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
                <a href="#" class="nav-item active">
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


</head>
<body>


</div>
</header>

<!-- Main Container -->
<div class="main-container">
    <!-- Sidebar -->
    <aside class="sidebar">


        <!-- Quick Stats -->
        <div class="quick-stats">
            <div class="stat-card">
                <div class="stat-number" id="totalCustomers">5</div>
                <div class="stat-label">Tổng Đối Tác</div>
            </div>
            <div class="stat-card">
                <div class="stat-number" id="vipCustomers">2</div>
                <div class="stat-label">VIP</div>
            </div>
        </div>

        <!-- Filters -->
        <div class="filter-section">
            <div class="filter-title">
                <i class="fas fa-users"></i>
                Nhóm Đối Tác
            </div>
            <div class="form-group">
                <select class="form-control" id="groupFilter">
                    <option value="">Tất cả các nhóm</option>
                    <option value="VIP">VIP</option>
                    <option value="Thường">Thường</option>
                    <option value="Mới">Mới</option>
                    <option value="Khách lẻ">Khách lẻ</option>
                </select>
            </div>
        </div>

        <div class="filter-section">
            <div class="filter-title">
                <i class="fas fa-chart-line"></i>
                Trạng thái
            </div>
            <div class="form-group">
                <select class="form-control" id="statusFilter">
                    <option value="">Tất cả trạng thái</option>
                    <option value="vip">VIP</option>
                    <option value="active">Hoạt động</option>
                    <option value="inactive">Ngừng hoạt động</option>
                    <option value="blocked">Bị khóa</option>
                </select>
            </div>
        </div>

        <div class="filter-section">
            <div class="filter-title">
                <i class="fas fa-map-marker-alt"></i>
                Thành phố
            </div>
            <div class="form-group">
                <select class="form-control" id="cityFilter">
                    <option value="">Tất cả thành phố</option>
                    <option value="Hà Nội">Hà Nội</option>
                    <option value="TP.HCM">TP.HCM</option>
                    <option value="Đà Nẵng">Đà Nẵng</option>
                    <option value="Cần Thơ">Cần Thơ</option>
                    <option value="Hải Phòng">Hải Phòng</option>
                </select>
            </div>
        </div>



        <div class="filter-section">
            <div class="filter-title">
                <i class="fas fa-user-plus"></i>
                Người tạo
            </div>
            <div class="form-group">
                <select class="form-control" id="creatorFilter">
                    <option value="">Chọn người tạo</option>
                    <option value="Admin">Admin</option>
                    <option value="Manager">Manager</option>
                    <option value="Staff">Staff</option>
                    <option value="System">System</option>
                </select>
            </div>
        </div>
    </aside>

    <!-- Main Content -->
    <!-- Main Content -->
    <main class="main-content">

        <div class="page-header">
            <h1>Đối Tác</h1>
            <div class="header-actions">

                <form action="so-customer" method="get" class="search-container">
                    <i class="fas fa-search"></i>
                    <input type="text" name="keyword" placeholder="Theo mã, tên khách hàng" class="search-input"
                           value="${param.keyword != null ? param.keyword : ''}" />
                    <button type="submit" style="border: none; background: none;">
                        <i class="fas fa-chevron-down"></i>
                    </button>
                </form>

            </div>
        </div>
        <!-- Products Table -->
        <div class="table-container">
            <table class="products-table table table-bordered">
                <thead class="table-light">
                    <tr>
                        <th>Mã Khách Hàng</th>
                        <th>Tên Khách hàng</th>
                        <th>Số Điện Thoại</th>
                        <!--                                <th>Gmail</th>
                        -->                                <th>Địa Chỉ</th><!--
                                                        <th>Giới Tính</th>-->
                        <th>Tổng tiền đã chi</th>
                        <!--                                <th>Ngày tạo thông tin</th>-->
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="s" items="${suppliers}">
                        <tr>
                            <td>${s.supplierId}</td>
                            <td>${s.supplierName}</td>
                            <td>${s.phone}</td>
                            <td>${s.address}</td>
                            <td>${s.createdAt}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <!-- Pagination -->
        <div class="pagination-container mt-3 d-flex justify-content-between align-items-center">
            <div class="pagination-info">
                Hiển thị ${startSupplier} - ${endSupplier} / Tổng số ${totalSuppliers} Đối tác
            </div>
            <div class="pagination">
                <a href="Supplier?page=1" class="page-btn ${currentPage == 1 ? 'disabled' : ''}">
                    <i class="fas fa-angle-double-left"></i>
                </a>
                <a href="Supplier?page=${currentPage - 1}" class="page-btn ${currentPage == 1 ? 'disabled' : ''}">
                    <i class="fas fa-angle-left"></i>
                </a>
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <a href="Supplier?page=${i}" class="page-btn ${i == currentPage ? 'active' : ''}">${i}</a>
                </c:forEach>
                <a href="Supplier?page=${currentPage + 1}" class="page-btn ${currentPage == totalPages ? 'disabled' : ''}">
                    <i class="fas fa-angle-right"></i>
                </a>
                <a href="Supplier?page=${totalPages}" class="page-btn ${currentPage == totalPages ? 'disabled' : ''}">
                    <i class="fas fa-angle-double-right"></i>
                </a>
            </div>
        </div>
    </main>





    <script>

        function toggleSelectAll() {
            const selectAll = document.getElementById('selectAll');
            const checkboxes = document.querySelectorAll('.customer-checkbox');

            checkboxes.forEach(checkbox => {
                checkbox.checked = selectAll.checked;
                toggleCustomerSelection(checkbox.value, false);
            });

            updateSelectedCount();
        }

        function toggleCustomerSelection(customerId, updateSelectAll = true) {
            const index = selectedCustomers.indexOf(customerId);

            if (index > -1) {
                selectedCustomers.splice(index, 1);
            } else {
                selectedCustomers.push(customerId);
            }

            if (updateSelectAll) {
                const checkboxes = document.querySelectorAll('.customer-checkbox');
                const selectAll = document.getElementById('selectAll');
                selectAll.checked = checkboxes.length > 0 &&
                        Array.from(checkboxes).every(cb => cb.checked);
            }

            updateSelectedCount();
        }


        function toggleDropdown(dropdownId) {
            const dropdown = document.getElementById(dropdownId);
            const isOpen = dropdown.classList.contains('show');

            // Close all dropdowns
            document.querySelectorAll('.dropdown-menu').forEach(menu => {
                menu.classList.remove('show');
            });

            // Toggle current dropdown
            if (!isOpen) {
                dropdown.classList.add('show');
            }
        }


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
</body>
</html>
