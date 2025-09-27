<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title> WM - Quản lý Hàng hóa</title>
    <link rel="stylesheet" href="css/Product.css">
    
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
                        <a href="#" class="nav-item ">
                            <span class="icon-overview"></span>
                            Tổng quan
                        </a>
                        <a href="#" class="nav-item active">
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
    

    <div class="main-container">
        <!-- Sidebar -->
        <aside class="sidebar">
            <!-- Product Types Filter -->
            <div class="filter-section">
                <div class="filter-title" onclick="toggleFilter('product-types')">
                    Loại hàng
                    <span class="icon-expand" id="product-types-icon"></span>
                </div>
                <div class="filter-content" id="product-types-content">
                    <div class="filter-item">
                        <input type="checkbox" id="normal-products" checked>
                        <label for="normal-products">Hàng hóa thường</label>
                    </div>
                    <div class="filter-item">
                        <input type="checkbox" id="serial-products">
                        <label for="serial-products">Hàng hóa - Serial/IMEI</label>
                    </div>
                    <div class="filter-item">
                        <input type="checkbox" id="services">
                        <label for="services">Dịch vụ</label>
                    </div>
                    <div class="filter-item">
                        <input type="checkbox" id="combo-products">
                        <label for="combo-products">Combo - Đóng gói</label>
                    </div>
                </div>
            </div>

            <!-- Product Groups Filter -->
            <div class="filter-section">
                <div class="filter-title" onclick="toggleFilter('product-groups')">
                    Nhóm hàng
                    <span class="icon-expand" id="product-groups-icon"></span>
                </div>
                <div class="filter-content" id="product-groups-content">
                    <input type="text" class="search-box" placeholder="Tìm kiếm nhóm hàng">
                    <div class="filter-item">
                        <span class="icon-phone"></span>
                        <label>Điện thoại</label>
                    </div>
                    <div class="filter-item">
                        <span class="icon-watch"></span>
                        <label>Đồng hồ thông minh</label>
                    </div>
                    <div class="filter-item">
                        <span class="icon-laptop"></span>
                        <label>Laptop</label>
                    </div>
                    <div class="filter-item">
                        <span class="icon-laptop"></span>
                        <label>Máy tính bảng</label>
                    </div>
                    <div class="filter-item">
                        <span class="icon-headphone"></span>
                        <label>Phụ kiện</label>
                    </div>
                </div>
            </div>

            <!-- Stock Filter -->
            <div class="filter-section">
                <div class="filter-title" onclick="toggleFilter('stock-filter')">
                    Tồn kho
                    <span class="icon-expand" id="stock-filter-icon"></span>
                </div>
                <div class="filter-content" id="stock-filter-content">
                    <div class="filter-item">
                        <input type="radio" id="all-stock" name="stock" checked>
                        <label for="all-stock">Tất cả</label>
                    </div>
                    <div class="filter-item">
                        <input type="radio" id="below-min" name="stock">
                        <label for="below-min">Dưới định mức tồn</label>
                    </div>
                    <div class="filter-item">
                        <input type="radio" id="above-max" name="stock">
                        <label for="above-max">Vượt định mức tồn</label>
                    </div>
                    <div class="filter-item">
                        <input type="radio" id="in-stock" name="stock">
                        <label for="in-stock">Còn hàng trong kho</label>
                    </div>
                    <div class="filter-item">
                        <input type="radio" id="out-of-stock" name="stock">
                        <label for="out-of-stock">Hết hàng trong kho</label>
                    </div>
                </div>
            </div>
        </aside>

        <!-- Main Content -->
        <main class="content">
            <!-- Page Header -->
            <div class="page-header">
                <h1 class="page-title">Hàng hóa</h1>
                <div class="header-actions">
                    <div class="search-container">
                        
    


                        <form action="product" method="get" class="search-container">
                        <input type="text" name="keyword" class="search-input" placeholder="Theo mã, tên hàng"
                        value="${param.keyword != null ? param.keyword : ''}" />
                        </form>
                        <span class="search-icon icon-search"></span>
                    </div>
                    <div class="action-buttons">
                        <button class="btn btn-primary">
                            <span class="icon-plus"></span>
                            Thêm mới
                        </button>
                        <button class="btn btn-secondary">
                            <span class="icon-upload"></span>
                            Import
                        </button>
                        <button class="btn btn-outline">
                            <span class="icon-download"></span>
                            Xuất file
                        </button>
                        <button class="btn btn-outline">
                            <span class="icon-menu"></span>
                        </button>
                    </div>
                </div>
            </div>

            <!-- Products Table -->
            <div class="products-table-container">
                <div class="table-header">
                    <div class="table-title">Danh sách sản phẩm</div>
                    <div class="table-actions">
                        <select style="padding: 6px 10px; border: 1px solid #d1d5db; border-radius: 4px;">
                            <option>Hiển thị 15 sản phẩm</option>
                            <option>Hiển thị 30 sản phẩm</option>
                            <option>Hiển thị 50 sản phẩm</option>
                        </select>
                    </div>
                </div>

                <table class="products-table">
                    <thead>
                        <tr>
        <tr>
            <th><input type="checkbox" id="select-all"></th>
            <th>ID</th>
            <th>Tên sản phẩm</th>
            <th>Giá</th>
            <th>Số lượng</th>
            <th>Ngày tạo</th>
        </tr>
                    </thead>
                    <tbody>



<c:forEach var="p" items="${products}">
    <tr>
        <td><input type="checkbox"></td>
        <td>${p.productId}</td>
        <td>${p.productName}</td>
        <td>   <fmt:formatNumber value="${p.price}" type="currency" currencySymbol="" groupingUsed="true"/> ₫</td>

        <td>${p.quantity}</td>
        <td>
  <fmt:formatDate value="${p.createdAt}" pattern="yyyy-MM-dd"/>
</td>
    </tr>
</c:forEach>

                    </tbody>
              
                </table>
                <!-- Pagination -->
                <div class="pagination">
                    <div class="pagination-info">
                        Hiển thị 1 - 15 / Tổng số 30 hàng hóa
                    </div>
                    <div class="pagination-controls">
                        <button class="pagination-btn" disabled>⏮️</button>
                        <button class="pagination-btn" disabled>◀️</button>
                        <button class="pagination-btn active">1</button>
                        <button class="pagination-btn">2</button>
                        <button class="pagination-btn">▶️</button>
                        <button class="pagination-btn">⏭️</button>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <script>


        // Select all checkbox functionality
        document.getElementById('select-all').addEventListener('change', function() {
            const checkboxes = document.querySelectorAll('tbody input[type="checkbox"]');
            checkboxes.forEach(checkbox => {
                checkbox.checked = this.checked;
            });
        });

     
        });
    </script>
</body>
</html>
