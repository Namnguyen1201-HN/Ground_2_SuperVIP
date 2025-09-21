<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý đối tác </title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="css/supplier.css" rel="stylesheet" type="text/css"/>
    
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
    
    
    
    
    
</head>
<body>
    
            

        </div>
    </header>

    <!-- Main Container -->
    <div class="main-container">
        <!-- Sidebar -->
        <aside class="sidebar">
            <h2 class="sidebar-title">Khách hàng</h2>
            
            <!-- Quick Stats -->
            <div class="quick-stats">
                <div class="stat-card">
                    <div class="stat-number" id="totalCustomers">5</div>
                    <div class="stat-label">Tổng KH</div>
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
                    Nhóm khách hàng
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
                    <h1>Khách hàng</h1>
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

                        </tbody>
                    </table>
                </div>

                <!-- Pagination -->
                <div class="pagination-container mt-3 d-flex justify-content-between align-items-center">
                    <div class="pagination-info">
                        Hiển thị ${startCustomer} - ${endCustomer} / Tổng số ${totalCustomers} Khách hàng
                    </div>
                    <div class="pagination">
                        <a href="so-customer?page=1" class="page-btn ${currentPage == 1 ? 'disabled' : ''}">
                            <i class="fas fa-angle-double-left"></i>
                        </a>
                        <a href="so-customer?page=${currentPage - 1}" class="page-btn ${currentPage == 1 ? 'disabled' : ''}">
                            <i class="fas fa-angle-left"></i>
                        </a>
                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <a href="so-customer?page=${i}" class="page-btn ${i == currentPage ? 'active' : ''}">${i}</a>
                        </c:forEach>
                        <a href="so-customer?page=${currentPage + 1}" class="page-btn ${currentPage == totalPages ? 'disabled' : ''}">
                            <i class="fas fa-angle-right"></i>
                        </a>
                        <a href="so-customer?page=${totalPages}" class="page-btn ${currentPage == totalPages ? 'disabled' : ''}">
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

        function openAddCustomerModal() {
            document.getElementById('addCustomerModal').classList.add('show');
        }

        function closeModal(modalId) {
            document.getElementById(modalId).classList.remove('show');
        }

        function addCustomer() {
            const form = document.getElementById('addCustomerForm');
            const formData = new FormData(form);
            
            // Validate required fields
            const name = document.getElementById('customerName').value.trim();
            const phone = document.getElementById('customerPhone').value.trim();
            
            if (!name || !phone) {
                showNotification('error', 'Vui lòng điền đầy đủ thông tin bắt buộc');
                return;
            }

         


    </script>
</body>
</html>
