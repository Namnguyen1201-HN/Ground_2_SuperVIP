<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="Model.Product" %>
<%@ page import="Model.Category" %>
<%@ page import="Model.Brand" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html lang="vi">    
    <head>
        <meta charset="UTF-8">
        <title>Danh sách hàng hóa</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
        <link href="css/warehouse/product.css" rel="stylesheet" type="text/css"/>

        <style>
            .product-detail {
                display: none;
                background: #f8f9fa;
                border: 1px solid #dee2e6;
                border-radius: 8px;
                padding: 20px;
                margin: 10px 0;
            }
            .product-detail.show {
                display: block;
            }
            .product-row {
                cursor: pointer;
            }
            .product-row:hover {
                background-color: #f8f9fa;
            }
            .status-badge {
                padding: 4px 8px;
                border-radius: 4px;
                font-size: 12px;
                font-weight: bold;
            }
            .status-badge.active {
                background-color: #d4edda;
                color: #155724;
            }
            .status-badge.inactive {
                background-color: #f8d7da;
                color: #721c24;
            }
        </style>
    </head>
    <body>
        <%@ include file="../warehouse/header-warehouse.jsp" %>

        <div class="page-container">
            <!-- Sidebar bộ lọc -->
            <div class="filter-sidebar">
                <form method="GET" action="WareHouseProduct">
                    <div class="filter-group">
                        <h6>Nhóm hàng</h6>
                        <%
                            List<Category> categories = (List<Category>) request.getAttribute("categories");
                            String[] selectedCategories = (String[]) request.getAttribute("selectedCategories");
                            if (categories != null) {
                                for (Category category : categories) {
                                    boolean isChecked = false;
                                    if (selectedCategories != null) {
                                        for (String selected : selectedCategories) {
                                            if (selected != null && selected.equals(category.getCategoryName())) {
                                                isChecked = true;
                                                break;
                                            }
                                        }
                                    }
                        %>
                        <div>
                            <input type="checkbox" name="category" value="<%= category.getCategoryName() %>" 
                                   <%= isChecked ? "checked" : "" %>>
                            <%= category.getCategoryName() %>
                        </div>
                        <%
                                }
                            }
                        %>
                    </div>

                    <div class="filter-group">
                        <h6>Tồn kho</h6>
                        <%
                            String stockFilter = (String) request.getAttribute("stockFilter");
                        %>
                        <div><input type="radio" name="stock" value="all" <%= (stockFilter == null || stockFilter.equals("all")) ? "checked" : "" %>> Tất cả</div>
                        <div><input type="radio" name="stock" value="in" <%= "in".equals(stockFilter) ? "checked" : "" %>> Còn hàng trong kho</div>
                        <div><input type="radio" name="stock" value="out" <%= "out".equals(stockFilter) ? "checked" : "" %>> Hết hàng trong kho</div>
                    </div>

                    <div class="filter-group">
                        <h6>Khoảng giá</h6>
                        <%
                            String priceFrom = (String) request.getAttribute("priceFrom");
                            String priceTo = (String) request.getAttribute("priceTo");
                        %>
                        <input type="number" name="priceFrom" placeholder="Giá từ" value="<%= priceFrom != null ? priceFrom : "" %>">
                        <input type="number" name="priceTo" placeholder="Giá đến" value="<%= priceTo != null ? priceTo : "" %>">
                    </div>

                    <div class="filter-group">
                        <h6>Trạng thái</h6>
                        <%
                            String statusFilter = (String) request.getAttribute("statusFilter");
                        %>
                        <div><input type="radio" name="status" value="all" <%= (statusFilter == null || statusFilter.equals("all")) ? "checked" : "" %>> Tất cả</div>
                        <div><input type="radio" name="status" value="active" <%= "active".equals(statusFilter) ? "checked" : "" %>> Đang bán</div>
                        <div><input type="radio" name="status" value="inactive" <%= "inactive".equals(statusFilter) ? "checked" : "" %>> Ngừng bán</div>
                    </div>

                    <button type="submit" class="btn-apply">🔍 Áp dụng lọc</button>
                    <button type="button" class="btn-reset" onclick="clearFilters()">🗑️ Xóa bộ lọc</button>
                </form>
            </div>

            <!-- Danh sách sản phẩm -->
            <div class="product-list">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <h5>Hàng hóa</h5>
                    <div class="search-box">
                        <form method="GET" action="WareHouseProduct" class="d-flex">
                            <input type="text" name="search" placeholder="🔍 Theo tên hàng" 
                                   value="<%= request.getAttribute("searchKeyword") != null ? request.getAttribute("searchKeyword") : "" %>">
                            <button type="submit" class="btn btn-primary ms-2">Tìm kiếm</button>
                        </form>
                    </div>
                </div>

                <table class="table table-bordered table-striped align-middle">
                    <thead>
                        <tr>
                            <th>Ảnh</th>
                            <th>Mã hàng</th>
                            <th>Tên hàng</th>
                            <th>Giá bán</th>
                            <th>Tồn kho</th>
                            <th>Trạng thái</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            List<Product> products = (List<Product>) request.getAttribute("products");
                            DecimalFormat df = new DecimalFormat("#,###");
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        
                            if (products != null && !products.isEmpty()) {
                                for (Product product : products) {
                                    String imageUrl = product.getImageUrl();
                                    if (imageUrl == null || imageUrl.trim().isEmpty()) {
                                        imageUrl = "https://via.placeholder.com/80x80?text=No+Image";
                                    }
                                
                                    String retailPrice = "0";
                                    if (product.getRetailPrice() != null) {
                                        retailPrice = df.format(product.getRetailPrice());
                                    }
                                
                                    String totalQty = "0";
                                    if (product.getTotalQty() != null) {
                                        totalQty = String.valueOf(product.getTotalQty());
                                    }
                                
                                    String statusClass = "inactive";
                                    String statusText = "Ngừng bán";
                                    if (product.getIsActive() != null && product.getIsActive()) {
                                        statusClass = "active";
                                        statusText = "Đang bán";
                                    }
                        %>
                        <tr class="product-row" data-product-id="<%= product.getProductId() %>">
                            <td>
                                <img src="<%= imageUrl %>" alt="<%= product.getProductName() %>" 
                                     style="width: 60px; height: 60px; object-fit: cover;">
                            </td>
                            <td><%= product.getProductId() %></td>
                            <td><%= product.getProductName() %></td>
                            <td><%= retailPrice %> ₫</td>
                            <td><%= totalQty %></td>
                            <td>
                                <span class="status-badge <%= statusClass %>"><%= statusText %></span>
                            </td>
                        </tr>

                        <!-- Chi tiết sản phẩm (ẩn ban đầu) -->
                        <tr id="detail-<%= product.getProductId() %>" class="product-detail">
                            <td colspan="6">
                                <div class="row">
                                    <div class="col-md-4">
                                        <h4><%= product.getProductName() %></h4>
                                        <img src="<%= imageUrl %>" alt="<%= product.getProductName() %>" 
                                             style="width: 200px; height: 200px; object-fit: cover;">
                                    </div>
                                    <div class="col-md-4">
                                        <h6>Thông tin cơ bản:</h6>
                                        <p><strong>Mã hàng:</strong> <%= product.getProductId() %></p>
                                        <p><strong>Nhóm hàng:</strong> <%= product.getCategoryName() != null ? product.getCategoryName() : "Chưa phân loại" %></p>
                                        <p><strong>Thương hiệu:</strong> <%= product.getBrandName() != null ? product.getBrandName() : "Chưa có thương hiệu" %></p>
                                        <p><strong>Thời gian tạo:</strong> 
                                            <%= product.getCreatedAt() != null ? sdf.format(java.sql.Timestamp.valueOf(product.getCreatedAt())) : "Chưa có" %>
                                        </p>
                                        <p><strong>Giá vốn:</strong> 
                                            <%= product.getCostPrice() != null ? df.format(product.getCostPrice()) + " ₫" : "Chưa có" %>
                                        </p>
                                        <p><strong>Giá bán thực:</strong> <%= retailPrice %> ₫</p>
                                        <p><strong>Phần trăm giảm:</strong> 0.0%</p>
                                    </div>
                                    <div class="col-md-4">
                                        <h6>Mô tả và nhà cung cấp:</h6>
                                        <p><strong>Mô tả:</strong> <%= product.getProductName() %></p>
                                        <p><strong>Nhà cung cấp:</strong> <%= product.getSupplierName() != null ? product.getSupplierName() : "Chưa có" %></p>
                                        <p><strong>VAT:</strong> <%= product.getVat() != null ? product.getVat() + "%" : "0%" %></p>
                                    </div>
                                </div>
                            </td>
                        </tr>
                        <%
                                }
                            } else {
                        %>
                        <tr>
                            <td colspan="6" class="text-center">Không có sản phẩm nào</td>
                        </tr>
                        <%
                            }
                        %>
                    </tbody>
                </table>
            </div>
        </div>

        <script>
            // Thêm event listener cho các hàng sản phẩm
            document.addEventListener('DOMContentLoaded', function () {
                var productRows = document.querySelectorAll('.product-row');
                productRows.forEach(function (row) {
                    row.addEventListener('click', function () {
                        var productId = this.getAttribute('data-product-id');
                        toggleProductDetail(productId);
                    });
                });
            });

            function toggleProductDetail(productId) {
                var detail = document.getElementById('detail-' + productId);
                if (detail.classList.contains('show')) {
                    detail.classList.remove('show');
                } else {
                    // Ẩn tất cả chi tiết khác
                    var allDetails = document.querySelectorAll('.product-detail');
                    allDetails.forEach(function (d) {
                        d.classList.remove('show');
                    });
                    // Hiển thị chi tiết được chọn
                    detail.classList.add('show');
                }
            }

            function clearFilters() {
                window.location.href = 'WareHouseProduct';
            }
        </script>
    </body>
</html>

