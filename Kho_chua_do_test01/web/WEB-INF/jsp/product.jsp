<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title> WM - Quản lý Hàng hóa</title>
    <link rel="stylesheet" href="css/product.css">
    
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
                        <input type="text" class="search-input" placeholder="Theo mã, tên hàng">
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
                            <th width="40">
                                <input type="checkbox" id="select-all">
                            </th>
                            <th width="40"></th>
                            <th width="60">Ảnh</th>
                            <th width="120">Mã hàng</th>
                            <th>Tên hàng</th>
                            <th width="120">Giá bán</th>
                            <th width="120">Giá vốn</th>
                            <th width="80">Tồn kho</th>
                            <th width="80">Khách đặt</th>
                            <th width="140">Thời gian tạo</th>
                            <th width="140">Dự kiến hết hàng</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td><input type="checkbox"></td>
                            <td>
                                <button class="favorite-btn">
                                    <span class="icon-star-empty"></span>
                                </button>
                            </td>
                            <td>
                                
                            </td>
                            <td class="product-code">TB000014</td>
                            <td class="product-name">Samsung Galaxy Tab S9 5G 128GB</td>
                            <td class="price">0</td>
                            <td>0</td>
                            <td class="stock low">0</td>
                            <td>0</td>
                            <td>01/06/2025 21:29</td>
                            <td>---</td>
                        </tr>
                        <tr>
                            <td><input type="checkbox"></td>
                            <td>
                                <button class="favorite-btn">
                                    <span class="icon-star-empty"></span>
                                </button>
                            </td>
                            <td>
                                
                            </td>
                            <td class="product-code">TB000013</td>
                            <td class="product-name">Samsung Galaxy Tab S9 5G 128GB</td>
                            <td class="price">0</td>
                            <td>0</td>
                            <td class="stock low">0</td>
                            <td>0</td>
                            <td>01/06/2025 21:29</td>
                            <td>---</td>
                        </tr>
                        <tr>
                            <td><input type="checkbox"></td>
                            <td>
                                <button class="favorite-btn">
                                    <span class="icon-star-empty"></span>
                                </button>
                            </td>
                            <td>
                               
                            </td>
                            <td class="product-code">TB000012</td>
                            <td class="product-name">iPad Air 6 M2 11" 5G 128GB</td>
                            <td class="price">0</td>
                            <td>0</td>
                            <td class="stock low">0</td>
                            <td>0</td>
                            <td>01/06/2025 21:29</td>
                            <td>---</td>
                        </tr>
                        <tr>
                            <td><input type="checkbox"></td>
                            <td>
                                <button class="favorite-btn">
                                    <span class="icon-star-empty"></span>
                                </button>
                            </td>
                            <td>
                               
                            </td>
                            <td class="product-code">TB000011</td>
                            <td class="product-name">iPad Air 6 M2 11" 5G 128GB</td>
                            <td class="price">0</td>
                            <td>0</td>
                            <td class="stock low">0</td>
                            <td>0</td>
                            <td>01/06/2025 21:29</td>
                            <td>---</td>
                        </tr>
                        <tr>
                            <td><input type="checkbox"></td>
                            <td>
                                <button class="favorite-btn">
                                    <span class="icon-star-empty"></span>
                                </button>
                            </td>
                            <td>
                               
                            </td>
                            <td class="product-code">PK000020</td>
                            <td class="product-name">Giá đỡ Laptop/Macbook hợp kim nhôm</td>
                            <td class="price">0</td>
                            <td>0</td>
                            <td class="stock low">0</td>
                            <td>0</td>
                            <td>01/06/2025 21:29</td>
                            <td>---</td>
                        </tr>
                        <tr>
                            <td><input type="checkbox"></td>
                            <td>
                                <button class="favorite-btn">
                                    <span class="icon-star-empty"></span>
                                </button>
                            </td>
                            <td>
                               
                            </td>
                            <td class="product-code">PK000019</td>
                            <td class="product-name">Miếng dán kính cường lực iPhone 15 Pro Jincase</td>
                            <td class="price">0</td>
                            <td>0</td>
                            <td class="stock low">0</td>
                            <td>0</td>
                            <td>01/06/2025 21:29</td>
                            <td>---</td>
                        </tr>
                        <tr>
                            <td><input type="checkbox"></td>
                            <td>
                                <button class="favorite-btn">
                                    <span class="icon-star-empty"></span>
                                </button>
                            </td>
                            <td>
                               
                            </td>
                            <td class="product-code">PK000018</td>
                            <td class="product-name">Cáp Baseus Crystal Shine Type-C to Lightning 2M</td>
                            <td class="price">0</td>
                            <td>0</td>
                            <td class="stock low">0</td>
                            <td>0</td>
                            <td>01/06/2025 21:29</td>
                            <td>---</td>
                        </tr>
                        <tr>
                            <td><input type="checkbox"></td>
                            <td>
                                <button class="favorite-btn">
                                    <span class="icon-star-empty"></span>
                                </button>
                            </td>
                            <td>
                               
                            </td>
                            <td class="product-code">PK000017</td>
                            <td class="product-name">Ốp lưng iPhone 15 Pro Max Nhua dẻo TPU UNIQ HYBRID Air Fender ID</td>
                            <td class="price">0</td>
                            <td>0</td>
                            <td class="stock medium">92</td>
                            <td>0</td>
                            <td>01/06/2025 21:29</td>
                            <td>---</td>
                        </tr>
                        <tr>
                            <td><input type="checkbox"></td>
                            <td>
                                <button class="favorite-btn">
                                    <span class="icon-star-empty"></span>
                                </button>
                            </td>
                            <td>
                               
                            </td>
                            <td class="product-code">PK000016</td>
                            <td class="product-name">Chuột không dây Logitech M331</td>
                            <td class="price">349,000</td>
                            <td>0</td>
                            <td class="stock medium">92</td>
                            <td>0</td>
                            <td>01/06/2025 21:29</td>
                            <td>---</td>
                        </tr>
                        <tr>
                            <td><input type="checkbox"></td>
                            <td>
                                <button class="favorite-btn">
                                    <span class="icon-star-empty"></span>
                                </button>
                            </td>
                            <td>
                            
                            </td>
                            <td class="product-code">PK000015</td>
                            <td class="product-name">Chuột không dây Logitech M331</td>
                            <td class="price">349,000</td>
                            <td>0</td>
                            <td class="stock low">0</td>
                            <td>0</td>
                            <td>01/06/2025 21:29</td>
                            <td>---</td>
                        </tr>
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
        // Toggle filter sections
        function toggleFilter(sectionId) {
            const content = document.getElementById(sectionId + '-content');
            const icon = document.getElementById(sectionId + '-icon');
            
            if (content.classList.contains('collapsed')) {
                content.classList.remove('collapsed');
                icon.className = 'icon-expand';
            } else {
                content.classList.add('collapsed');
                icon.className = 'icon-collapse';
            }
        }

        // Select all checkbox functionality
        document.getElementById('select-all').addEventListener('change', function() {
            const checkboxes = document.querySelectorAll('tbody input[type="checkbox"]');
            checkboxes.forEach(checkbox => {
                checkbox.checked = this.checked;
            });
        });

        // Favorite button functionality
        document.querySelectorAll('.favorite-btn').forEach(btn => {
            btn.addEventListener('click', function() {
                const icon = this.querySelector('span');
                if (this.classList.contains('active')) {
                    this.classList.remove('active');
                    icon.className = 'icon-star-empty';
                } else {
                    this.classList.add('active');
                    icon.className = 'icon-star';
                }
            });
        });

        // Search functionality
        document.querySelector('.search-input').addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase();
            const rows = document.querySelectorAll('tbody tr');
            
            rows.forEach(row => {
                const productCode = row.querySelector('.product-code').textContent.toLowerCase();
                const productName = row.querySelector('.product-name').textContent.toLowerCase();
                
                if (productCode.includes(searchTerm) || productName.includes(searchTerm)) {
                    row.style.display = '';
                } else {
                    row.style.display = 'none';
                }
            });
        });

        // Filter functionality
        document.querySelectorAll('input[type="checkbox"], input[type="radio"]').forEach(input => {
            input.addEventListener('change', function() {
                // Here you would implement the actual filtering logic
                console.log('Filter changed:', this.id, this.checked);
            });
        });

        // Pagination functionality
        document.querySelectorAll('.pagination-btn:not(:disabled)').forEach(btn => {
            btn.addEventListener('click', function() {
                if (!this.classList.contains('active')) {
                    document.querySelector('.pagination-btn.active').classList.remove('active');
                    this.classList.add('active');
                    // Here you would implement the actual pagination logic
                    console.log('Page changed to:', this.textContent);
                }
            });
        });

        // Add new product button
        document.querySelector('.btn-primary').addEventListener('click', function() {
            alert('Chức năng thêm sản phẩm mới sẽ được triển khai!');
        });

        // Import button
        document.querySelector('.btn-secondary').addEventListener('click', function() {
            alert('Chức năng import sản phẩm sẽ được triển khai!');
        });

        // Export button
        document.querySelector('.btn-outline').addEventListener('click', function() {
            alert('Chức năng xuất file sẽ được triển khai!');
        });
    </script>
</body>
</html>
