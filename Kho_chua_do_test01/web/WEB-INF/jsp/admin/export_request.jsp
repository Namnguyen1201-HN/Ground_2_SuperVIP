<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TSMS - Tạo phiếu xuất kho</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/export-request.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/includes/header.css"/>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        /* Ensures content is not hidden behind header */
        main {
            padding-top: 70px;
        }
    </style>
</head>
<body>

    <header class="header">
        <div class="header-container">
            <a href="${pageContext.request.contextPath}/DashBoard" class="logo">
                <div class="logo-icon">T</div>
                <span class="logo-text">TSMS</span>
            </a>
            <nav class="main-nav">
                <a href="${pageContext.request.contextPath}/DashBoard" class="nav-item">
                    <i class="fas fa-chart-line"></i> Tổng quan
                </a>
                <a href="${pageContext.request.contextPath}/product" class="nav-item">
                    <i class="fas fa-box"></i> Hàng hóa
                </a>
                <a href="${pageContext.request.contextPath}/import-request" class="nav-item active">
                    <i class="fa-solid fa-upload"></i> Xuất hàng
                </a>
                <a href="${pageContext.request.contextPath}/stock-movements" class="nav-item">
                    <i class="fas fa-list"></i> Danh sách xuất hàng
                </a>
            </nav>
            <div class="user-section">
                <div class="user-dropdown">
                    <div class="user-info" id="dropdownToggle">
                        <div class="user-avatar">
                            ${sessionScope.currentUser.fullName.substring(0,1).toUpperCase()}
                        </div>
                        <div class="user-details">
                            <span class="user-name">${sessionScope.currentUser.fullName}</span>
                            <span class="user-role">${sessionScope.currentUser.roleName}</span>
                        </div>
                        <i class="fas fa-chevron-down dropdown-icon"></i>
                    </div>
                    <div class="dropdown-menu" id="dropdownMenu">
                        <a href="${pageContext.request.contextPath}/sale" class="dropdown-item">
                            <i class="fas fa-user"></i> Thông tin tài khoản
                        </a>
                        <div class="dropdown-divider"></div>
                        <a href="${pageContext.request.contextPath}/logout" class="dropdown-item logout">
                            <i class="fas fa-sign-out-alt"></i> Đăng xuất
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </header>

    <main>
                </a>
            </div>
        </div>

        <!-- Success/Error Messages -->
        <c:if test="${not empty sessionScope.successMessage}">
            <div class="alert alert-success">
                ✅ ${sessionScope.successMessage}
            </div>
            <c:remove var="successMessage" scope="session"/>
        </c:if>

        <c:if test="${not empty sessionScope.errorMessage}">
            <div class="alert alert-error">
                ❌ ${sessionScope.errorMessage}
            </div>
            <c:remove var="errorMessage" scope="session"/>
        </c:if>

        <!-- Main Content: 2 Panels -->
        <div class="main-content">
            
            <!-- LEFT PANEL: Product Selection -->
            <div class="product-panel">
                <div class="panel-header">
                    <h2>🛍️ Chọn sản phẩm từ kho</h2>
                </div>

                <!-- Warehouse Selection (No auto-submit, just for display/selection) -->
                <div class="warehouse-section">
                    <p class="section-note">
                        <i class="fas fa-info-circle"></i>
                        Chọn kho xuất hàng khi tạo phiếu (bên phải)
                    </p>
                </div>

                <!-- Search Bar -->
                <form method="get" action="${pageContext.request.contextPath}/import-request" class="search-form">
                    <input type="text" name="keyword" value="${keyword}" 
                           placeholder="🔍 Tìm sản phẩm..." class="search-input" />
                    <button type="submit" class="btn btn-search">Tìm kiếm</button>
                </form>

                <!-- Products Display -->
                <c:choose>
                    <c:when test="${empty listProductDetails}">
                        <div class="empty-state">
                            <p>📦 Không có sản phẩm nào</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="product-grid">
                            <c:forEach var="product" items="${listProductDetails}">
                                <div class="product-card">
                                    <div class="product-image">
                                        <c:choose>
                                            <c:when test="${not empty product.imageURL}">
                                                <img src="${product.imageURL}" alt="${product.productName}" />
                                            </c:when>
                                            <c:otherwise>
                                                <div class="no-image">📦</div>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="product-info">
                                        <h3 class="product-name">${product.productName}</h3>
                                        <p class="product-code">Mã: ${product.productCode}</p>
                                        <c:if test="${not empty product.supplierName}">
                                            <p class="product-supplier">NCC: ${product.supplierName}</p>
                                        </c:if>
                                        <p class="product-price">
                                            <fmt:formatNumber value="${product.costPrice}" type="currency" currencySymbol="₫"/>
                                        </p>
                                        
                                        <form method="post" action="${pageContext.request.contextPath}/import-request" class="add-form">
                                            <input type="hidden" name="action" value="add" />
                                            <input type="hidden" name="productDetailID" value="${product.productDetailID}" />
                                            <div class="quantity-input">
                                                <input type="number" name="quantity" value="1" min="1" required />
                                                <button type="submit" class="btn btn-add">
                                                    ➕ Thêm
                                                </button>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>

                        <!-- Pagination -->
                        <c:if test="${totalPages > 1}">
                            <div class="pagination">
                                <c:if test="${currentPage > 1}">
                                    <a href="${pageContext.request.contextPath}/import-request?keyword=${keyword}&page=1" class="page-btn">
                                        ⏮️ Đầu
                                    </a>
                                    <a href="${pageContext.request.contextPath}/import-request?keyword=${keyword}&page=${currentPage - 1}" class="page-btn">
                                        ⬅️
                                    </a>
                                </c:if>

                                <c:forEach begin="${currentPage - 2 > 0 ? currentPage - 2 : 1}" 
                                           end="${currentPage + 2 < totalPages ? currentPage + 2 : totalPages}" 
                                           var="i">
                                    <c:choose>
                                        <c:when test="${i == currentPage}">
                                            <span class="page-btn active">${i}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="${pageContext.request.contextPath}/import-request?keyword=${keyword}&page=${i}" class="page-btn">${i}</a>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>

                                <c:if test="${currentPage < totalPages}">
                                    <a href="${pageContext.request.contextPath}/import-request?keyword=${keyword}&page=${currentPage + 1}" class="page-btn">
                                        ➡️
                                    </a>
                                    <a href="${pageContext.request.contextPath}/import-request?keyword=${keyword}&page=${totalPages}" class="page-btn">
                                        ⏭️ Cuối
                                    </a>
                                </c:if>
                            </div>
                        </c:if>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- RIGHT PANEL: Export Invoice -->
            <div class="invoice-panel">
                <div class="panel-header">
                    <h2>📋 Phiếu xuất kho</h2>
                </div>

                <c:choose>
                    <c:when test="${empty sessionScope.exportCart}">
                        <div class="empty-cart">
                            <p>🛒 Giỏ hàng trống</p>
                            <p class="hint">Chọn sản phẩm từ kho để thêm vào phiếu xuất</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <!-- Cart Items -->
                        <div class="cart-items">
                            <table class="cart-table">
                                <thead>
                                    <tr>
                                        <th>Sản phẩm</th>
                                        <th>Mã</th>
                                        <th>Giá</th>
                                        <th>SL</th>
                                        <th>Tổng</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:set var="totalAmount" value="0" />
                                    <c:set var="totalQuantity" value="0" />
                                    <c:forEach var="item" items="${sessionScope.exportCart}">
                                        <tr>
                                            <td>${item.productName}</td>
                                            <td>${item.productCode}</td>
                                            <td>
                                                <fmt:formatNumber value="${item.costPrice}" type="currency" currencySymbol="₫"/>
                                            </td>
                                            <td>
                                                <form method="post" action="${pageContext.request.contextPath}/import-request" class="quantity-form">
                                                    <input type="hidden" name="action" value="updateQuantity" />
                                                    <input type="hidden" name="productDetailID" value="${item.productDetailID}" />
                                                    <input type="number" name="quantity" value="${item.quantity}" 
                                                           min="1" onchange="this.form.submit()" />
                                                </form>
                                            </td>
                                            <td>
                                                <fmt:formatNumber value="${item.subtotal}" type="currency" currencySymbol="₫"/>
                                            </td>
                                            <td>
                                                <form method="post" action="${pageContext.request.contextPath}/import-request" style="display:inline;">
                                                    <input type="hidden" name="action" value="remove" />
                                                    <input type="hidden" name="productDetailID" value="${item.productDetailID}" />
                                                    <button type="submit" class="btn-remove" title="Xóa">🗑️</button>
                                                </form>
                                            </td>
                                        </tr>
                                        <c:set var="totalAmount" value="${totalAmount + item.subtotal}" />
                                        <c:set var="totalQuantity" value="${totalQuantity + item.quantity}" />
                                    </c:forEach>
                                </tbody>
                                <tfoot>
                                    <tr>
                                        <td colspan="3"><strong>Tổng cộng:</strong></td>
                                        <td><strong>${totalQuantity}</strong></td>
                                        <td><strong><fmt:formatNumber value="${totalAmount}" type="currency" currencySymbol="₫"/></strong></td>
                                        <td></td>
                                    </tr>
                                </tfoot>
                            </table>
                        </div>

                        <!-- Submit Form -->
                        <form method="post" action="${pageContext.request.contextPath}/import-request" class="submit-form">
                            <input type="hidden" name="action" value="submitRequest" />
                            
                            <div class="form-group">
                                <label for="fromWarehouseID">
                                    <i class="fas fa-warehouse"></i> Kho xuất hàng <span style="color:red;">*</span>
                                </label>
                                <select name="fromWarehouseID" id="fromWarehouseID" class="form-control" required>
                                    <option value="">-- Chọn kho xuất hàng --</option>
                                    <c:forEach var="warehouse" items="${listWarehouses}">
                                        <option value="${warehouse.warehouseId}">
                                            ${warehouse.warehouseName}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                            
                            <div class="form-group">
                                <label for="overallNote">Ghi chú:</label>
                                <textarea name="overallNote" id="overallNote" rows="3" class="form-control" 
                                          placeholder="Nhập ghi chú (không bắt buộc)..."></textarea>
                            </div>

                            <div class="form-actions">
                                <button type="button" class="btn btn-reset" 
                                        onclick="if(confirm('Xóa toàn bộ giỏ hàng?')) { document.getElementById('resetForm').submit(); }">
                                    🗑️ Xóa giỏ hàng
                                </button>
                                <button type="submit" class="btn btn-submit">
                                    ✅ Tạo phiếu xuất hàng
                                </button>
                            </div>
                        </form>

                        <!-- Hidden Reset Form -->
                        <form method="post" action="${pageContext.request.contextPath}/import-request" id="resetForm" style="display:none;">
                            <input type="hidden" name="action" value="reset" />
                        </form>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </main>

    <script>
        // Dropdown toggle for user menu
        document.addEventListener('DOMContentLoaded', function() {
            const dropdownToggle = document.getElementById('dropdownToggle');
            const dropdownMenu = document.getElementById('dropdownMenu');
            
            if(dropdownToggle && dropdownMenu) {
                dropdownToggle.addEventListener('click', function(e) {
                    e.stopPropagation();
                    dropdownMenu.classList.toggle('show');
                });
                
                // Close when clicking outside
                document.addEventListener('click', function(e) {
                    if (!dropdownToggle.contains(e.target) && !dropdownMenu.contains(e.target)) {
                        dropdownMenu.classList.remove('show');
                    }
                });
            }
        });
    </script>
</body>
</html>
