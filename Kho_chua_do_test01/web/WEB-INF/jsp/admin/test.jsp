<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="css/admin/test.css" rel="stylesheet" type="text/css"/>
    <title>Chi Tiết Sản Phẩm</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <div class="container">
        <div class="product-detail-card">
            <h1 class="page-title">Thông Tin Chi Tiết Sản Phẩm</h1>
            
            <div class="product-content">
                <!-- Product Image Section -->
                <div class="product-image-section">
                    <img src="${product.imageURL != null ? product.imageURL : 'https://via.placeholder.com/400x400?text=No+Image'}" 
                         alt="${product.productName}" 
                         class="product-image">
                </div>

                <!-- Product Information Section -->
                <div class="product-info-section">
                    <div class="info-group">
                        <label class="info-label">Mã Sản Phẩm:</label>
                        <div class="info-value">${product.productID}</div>
                    </div>

                    <div class="info-group">
                        <label class="info-label">Tên Sản Phẩm:</label>
                        <div class="info-value product-name">${product.productName}</div>
                    </div>

                    <div class="info-row">
                        <div class="info-group">
                            <label class="info-label">Mã Thương Hiệu:</label>
                            <div class="info-value">${product.brandID}</div>
                        </div>

                        <div class="info-group">
                            <label class="info-label">Mã Danh Mục:</label>
                            <div class="info-value">${product.categoryID}</div>
                        </div>
                    </div>

                    <div class="info-group">
                        <label class="info-label">Mã Nhà Cung Cấp:</label>
                        <div class="info-value">${product.supplierID}</div>
                    </div>

                    <div class="info-row">
                        <div class="info-group">
                            <label class="info-label">Giá Vốn:</label>
                            <div class="info-value price">
                                <fmt:formatNumber value="${product.costPrice}" type="currency" currencySymbol="₫"/>
                            </div>
                        </div>

                        <div class="info-group">
                            <label class="info-label">Giá Bán Lẻ:</label>
                            <div class="info-value price retail-price">
                                <fmt:formatNumber value="${product.retailPrice}" type="currency" currencySymbol="₫"/>
                            </div>
                        </div>
                    </div>

                    <div class="info-row">
                        <div class="info-group">
                            <label class="info-label">VAT (%):</label>
                            <div class="info-value">${product.vat}%</div>
                        </div>

                        <div class="info-group">
                            <label class="info-label">Số Lượng:</label>
                            <div class="info-value quantity">
                                <span class="quantity-badge ${product.quantity > 0 ? 'in-stock' : 'out-of-stock'}">
                                    ${product.quantity} ${product.quantity > 0 ? 'Còn hàng' : 'Hết hàng'}
                                </span>
                            </div>
                        </div>
                    </div>

                    <div class="action-buttons">
                        <button class="btn btn-primary" onclick="editProduct()">Chỉnh Sửa</button>
                        <button class="btn btn-secondary" onclick="goBack()">Quay Lại</button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
        function editProduct() {
            window.location.href = 'edit-product.jsp?id=${product.productID}';
        }

        function goBack() {
            window.history.back();
        }
    </script>
</body>
</html>
