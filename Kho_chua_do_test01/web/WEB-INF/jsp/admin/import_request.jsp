<%--
    Document   : import_request_redesigned
    Created on : October 21, 2025, 10:28:00 PM
    Author     : Gemini
    Description: Modern 2-column layout for creating import requests.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TSMS - Tạo phiếu nhập kho</title>
    <%-- Link to the new redesigned CSS file --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/import-request.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/includes/header.css"/>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        /* Ensures content is not hidden behind a potentially fixed header */
        main {
            padding-top: 70px; /* Value from --header-height to push content below a fixed header */
        }
    </style>
</head>
<body>

    <header class="header">
        <div class="header-container">
            <div class="logo">
                <a href="${pageContext.request.contextPath}/DashBoard" class="logo">
                    <div class="logo-icon">T</div>
                    <span class="logo-text">TSMS</span>
                </a>
            </div>
            <nav class="main-nav">
                <a href="${pageContext.request.contextPath}/DashBoard" class="nav-item">
                    <i class="fas fa-chart-line"></i> Tổng quan
                </a>
                <a href="${pageContext.request.contextPath}/product" class="nav-item">
                    <i class="fas fa-box"></i> Hàng hóa
                </a>
                
                <c:choose>
                    <c:when test="${sessionScope.currentUser.roleId == 0}">
                        <%-- Admin: Nhập hàng từ nhà cung cấp vào kho --%>
                        <a href="${pageContext.request.contextPath}/import-request" class="nav-item active">
                            <i class="fa-solid fa-download"></i> Nhập hàng
                        </a>
                        <%-- Admin: Xem tất cả import/export movements --%>
                        <a href="${pageContext.request.contextPath}/stock-movements" class="nav-item">
                            <i class="fas fa-exchange-alt"></i> Danh sách movements
                        </a>
                    </c:when>
                    <c:when test="${sessionScope.currentUser.roleId == 1}">
                        <%-- Branch Manager: Xuất hàng từ kho về chi nhánh --%>
                        <a href="${pageContext.request.contextPath}/import-request" class="nav-item active">
                            <i class="fa-solid fa-upload"></i> Xuất hàng
                        </a>
                        <%-- Branch Manager: Danh sách xuất hàng của chi nhánh --%>
                        <a href="${pageContext.request.contextPath}/stock-movements" class="nav-item">
                            <i class="fas fa-list"></i> Danh sách xuất hàng
                        </a>
                    </c:when>
                </c:choose>
            </nav>
            <div class="header-right">
                <div class="user-dropdown">
                    <a href="#" class="user-icon gradient" id="dropdownToggle">
                        <i class="fas fa-user-circle fa-2x"></i>
                    </a>
                    <div class="dropdown-menu" id="dropdownMenu">
                        <a href="${pageContext.request.contextPath}/sale" class="dropdown-item">Thông tin chi tiết</a>
                        <a href="${pageContext.request.contextPath}/logout" class="dropdown-item">Đăng xuất</a>
                    </div>
                </div>
            </div>
        </div>
    </header>

    <main>
        <!-- Success/Error Messages -->
        <c:if test="${not empty sessionScope.successMessage}">
            <div class="alert alert-success">
                <i class="fas fa-check-circle"></i> ${sessionScope.successMessage}
            </div>
            <c:remove var="successMessage" scope="session"/>
        </c:if>

        <c:if test="${not empty sessionScope.errorMessage}">
            <div class="alert alert-error">
                <i class="fas fa-exclamation-circle"></i> ${sessionScope.errorMessage}
            </div>
            <c:remove var="errorMessage" scope="session"/>
        </c:if>

        <div class="main-container">
            <!-- ======================================================= -->
            <!-- == LEFT PANEL: SUPPLIER SELECTION & PRODUCTS          === -->
            <!-- ======================================================= -->
            <div class="product-panel">
                <div class="panel-header">
                    <h2><i class="fas fa-warehouse"></i> Chọn nhà cung cấp & Sản phẩm</h2>
                </div>

                <!-- Supplier Selection -->
                <div class="supplier-section">
                    <form method="get" action="${pageContext.request.contextPath}/import-request" id="supplierForm">
                        <div class="form-group">
                            <label for="supplierSelect">
                                <i class="fas fa-handshake"></i> Nhà cung cấp
                            </label>
                            <select id="supplierSelect" name="supplierId" onchange="this.form.submit()"
                                    <c:if test="${not empty sessionScope.cartItems}">disabled title="Đã có sản phẩm trong giỏ hàng. Xóa giỏ hàng để chọn lại."</c:if>>
                                <option value="">-- Chọn nhà cung cấp --</option>
                                <c:forEach var="supplier" items="${listSuppliers}">
                                    <option value="${supplier.supplierId}"
                                            <c:if test="${supplier.supplierId eq sessionScope.cartSupplierId}">selected</c:if>>
                                        ${supplier.supplierName}
                                        <c:if test="${not empty supplier.phone}"> - ${supplier.phone}</c:if>
                                    </option>
                                </c:forEach>
                            </select>
                            <c:if test="${not empty sessionScope.cartItems}">
                                <small class="help-text">
                                    <i class="fas fa-info-circle"></i>
                                    Nhà cung cấp đã bị khóa. Xóa giỏ hàng để chọn nhà cung cấp khác.
                                </small>
                            </c:if>
                        </div>
                    </form>
                </div>

                <!-- Products Display -->
                <div class="product-content">
                    <c:choose>
                        <c:when test="${empty sessionScope.cartSupplierId}">
                            <div class="empty-state">
                                <i class="fas fa-handshake"></i>
                                <h3>Chưa chọn nhà cung cấp</h3>
                                <p>Vui lòng chọn nhà cung cấp từ danh sách phía trên để xem sản phẩm.</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <!-- Search Bar -->
                            <div class="search-section">
                                <form method="get" action="${pageContext.request.contextPath}/import-request" class="search-form">
                                    <input type="hidden" name="supplierId" value="${sessionScope.cartSupplierId}" />
                                    <div class="search-container">
                                        <i class="fas fa-search"></i>
                                        <input type="text" name="keyword" value="${keyword}"
                                               placeholder="Tìm sản phẩm theo tên hoặc mã..." class="search-input" />
                                    </div>
                                    <button type="submit" class="btn btn-outline">Tìm kiếm</button>
                                </form>
                            </div>

                            <!-- Product Grid -->
                            <c:choose>
                                <c:when test="${empty listProductDetails}">
                                    <div class="empty-state">
                                        <i class="fas fa-box-open"></i>
                                        <h3>Không tìm thấy sản phẩm</h3>
                                        <p>Nhà cung cấp này chưa có sản phẩm nào hoặc không có sản phẩm khớp với từ khóa của bạn.</p>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="product-grid">
                                        <c:forEach var="product" items="${listProductDetails}">
                                            <div class="product-card">
                                                <div class="product-image">
                                                    <c:choose>
                                                        <c:when test="${not empty product.imageURL}">
                                                            <img src="${product.imageURL}" alt="${product.productNameUnsigned}" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="no-image"><i class="fas fa-image"></i></div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                                <div class="product-info">
                                                    <h4 class="product-name" title="${product.productNameUnsigned}">${product.productNameUnsigned}</h4>
                                                    <p class="product-code">
                                                        <i class="fas fa-barcode"></i> ${product.productCode}
                                                    </p>
                                                    <p class="product-price">
                                                        <fmt:formatNumber value="${product.costPrice}" type="number" groupingUsed="true"/> ₫
                                                    </p>
                                                </div>
                                                <form method="post" action="${pageContext.request.contextPath}/import-request" class="add-form">
                                                    <input type="hidden" name="action" value="add" />
                                                    <input type="hidden" name="productDetailID" value="${product.productDetailID}" />
                                                    <input type="hidden" name="supplierId" value="${sessionScope.cartSupplierId}" />
                                                    <div class="quantity-section">
                                                        <input type="number" name="quantity" value="1" min="1" required class="quantity-input" />
                                                        <button type="submit" class="btn btn-primary btn-add">
                                                            <i class="fas fa-plus"></i> Thêm
                                                        </button>
                                                    </div>
                                                </form>
                                            </div>
                                        </c:forEach>
                                    </div>

                                    <!-- Pagination -->
                                    <c:if test="${totalPages > 1}">
                                        <div class="pagination">
                                            <c:if test="${currentPage > 1}">
                                                <a href="${pageContext.request.contextPath}/import-request?supplierId=${sessionScope.cartSupplierId}&keyword=${keyword}&page=1" class="btn btn-page"><i class="fas fa-angle-double-left"></i></a>
                                                <a href="${pageContext.request.contextPath}/import-request?supplierId=${sessionScope.cartSupplierId}&keyword=${keyword}&page=${currentPage - 1}" class="btn btn-page"><i class="fas fa-angle-left"></i></a>
                                            </c:if>

                                            <c:forEach var="i" begin="${currentPage - 2 > 0 ? currentPage - 2 : 1}"
                                                       end="${currentPage + 2 < totalPages ? currentPage + 2 : totalPages}">
                                                <a href="${pageContext.request.contextPath}/import-request?supplierId=${sessionScope.cartSupplierId}&keyword=${keyword}&page=${i}" class="btn btn-page ${i == currentPage ? 'active' : ''}">${i}</a>
                                            </c:forEach>

                                            <c:if test="${currentPage < totalPages}">
                                                <a href="${pageContext.request.contextPath}/import-request?supplierId=${sessionScope.cartSupplierId}&keyword=${keyword}&page=${currentPage + 1}" class="btn btn-page"><i class="fas fa-angle-right"></i></a>
                                                <a href="${pageContext.request.contextPath}/import-request?supplierId=${sessionScope.cartSupplierId}&keyword=${keyword}&page=${totalPages}" class="btn btn-page"><i class="fas fa-angle-double-right"></i></a>
                                            </c:if>
                                        </div>
                                    </c:if>
                                </c:otherwise>
                            </c:choose>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- ======================================================= -->
            <!-- == RIGHT PANEL: IMPORT REQUEST CART                   === -->
            <!-- ======================================================= -->
            <div class="invoice-panel">
                 <div class="panel-header">
                    <h2><i class="fas fa-file-invoice"></i> Phiếu nhập hàng</h2>
                    <c:if test="${not empty sessionScope.cartItems}">
                        <button class="btn-clear" onclick="resetCart()" title="Xóa tất cả sản phẩm">
                            <i class="fas fa-trash-alt"></i> Xóa tất cả
                        </button>
                    </c:if>
                </div>

                <div class="cart-content">
                    <c:choose>
                        <c:when test="${empty sessionScope.cartItems}">
                            <div class="empty-cart">
                                <i class="fas fa-shopping-cart"></i>
                                <h3>Giỏ hàng trống</h3>
                                <p>Thêm sản phẩm từ danh sách bên trái để tạo phiếu nhập.</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="cart-items">
                                <c:set var="totalAmount" value="0" />
                                <c:set var="totalQuantity" value="0" />
                                <c:forEach var="item" items="${sessionScope.cartItems}">
                                    <c:set var="subtotal" value="${item.quantity * item.costPrice}" />
                                    <div class="cart-item">
                                        <div class="item-info">
                                            <p class="item-name">${item.productNameUnsigned}</p>
                                            <p class="item-code">${item.productCode}</p>
                                            <p class="item-price">Đơn giá: <fmt:formatNumber value="${item.costPrice}" type="number" groupingUsed="true"/> ₫</p>
                                        </div>
                                        <div class="item-controls">
                                            <form method="post" action="${pageContext.request.contextPath}/import-request" class="qty-form">
                                                <input type="hidden" name="action" value="updateQuantity" />
                                                <input type="hidden" name="productDetailID" value="${item.productDetailID}" />
                                                <input type="hidden" name="supplierId" value="${sessionScope.cartSupplierId}" />
                                                <input type="number" name="quantity" value="${item.quantity}"
                                                       min="1" onchange="this.form.submit()" class="quantity-input" title="Cập nhật số lượng" />
                                            </form>
                                            <form method="post" action="${pageContext.request.contextPath}/import-request">
                                                <input type="hidden" name="action" value="remove" />
                                                <input type="hidden" name="productDetailID" value="${item.productDetailID}" />
                                                <input type="hidden" name="supplierId" value="${sessionScope.cartSupplierId}" />
                                                <button type="submit" class="btn-remove" title="Xóa sản phẩm">
                                                    <i class="fas fa-times"></i>
                                                </button>
                                            </form>
                                        </div>
                                        <div class="item-total">
                                            Thành tiền: <span><fmt:formatNumber value="${subtotal}" type="number" groupingUsed="true"/> ₫</span>
                                        </div>
                                    </div>
                                    <c:set var="totalAmount" value="${totalAmount + subtotal}" />
                                    <c:set var="totalQuantity" value="${totalQuantity + item.quantity}" />
                                </c:forEach>
                            </div>
                            
                            <div class="invoice-footer">
                                <div class="cart-summary">
                                    <div class="summary-row">
                                        <span><i class="fas fa-boxes"></i> Tổng số lượng</span>
                                        <span class="value">${totalQuantity}</span>
                                    </div>
                                    <div class="summary-row total">
                                        <span><i class="fas fa-calculator"></i> Tổng cộng</span>
                                        <span class="value"><fmt:formatNumber value="${totalAmount}" type="number" groupingUsed="true"/> ₫</span>
                                    </div>
                                </div>

                                <div class="submit-section">
                                    <form method="post" action="${pageContext.request.contextPath}/import-request">
                                        <input type="hidden" name="action" value="submitRequest" />
                                        <div class="form-group">
                                            <label for="toWarehouseID">
                                                <i class="fas fa-warehouse"></i> Kho nhập hàng <span class="required">*</span>
                                            </label>
                                            <select name="toWarehouseID" id="toWarehouseID" class="form-control" required>
                                                <option value="">-- Chọn kho đích --</option>
                                                <c:forEach var="warehouse" items="${listWarehouse}">
                                                    <option value="${warehouse.warehouseId}">${warehouse.warehouseName}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                        <div class="form-group">
                                            <label for="overallNote">
                                                <i class="fas fa-sticky-note"></i> Ghi chú
                                            </label>
                                            <textarea name="overallNote" id="overallNote" rows="3" class="form-control"
                                                      placeholder="Nhập ghi chú cho phiếu nhập hàng (không bắt buộc)..."></textarea>
                                        </div>
                                        <button type="submit" class="btn btn-primary submit-btn">
                                            <i class="fas fa-paper-plane"></i> Tạo phiếu nhập hàng
                                        </button>
                                    </form>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </main>

    <!-- Hidden Reset Form -->
    <form method="post" action="${pageContext.request.contextPath}/import-request" id="resetForm" style="display:none;">
        <input type="hidden" name="action" value="reset" />
        <input type="hidden" name="supplierId" value="${sessionScope.cartSupplierId}" />
    </form>

    <script>
        // Dropdown menu for user icon
        const toggle = document.getElementById("dropdownToggle");
        const menu = document.getElementById("dropdownMenu");

        if (toggle && menu) {
            toggle.addEventListener("click", function (e) {
                e.preventDefault();
                menu.classList.toggle('show');
            });

            document.addEventListener("click", function (e) {
                if (!toggle.contains(e.target) && !menu.contains(e.target)) {
                    menu.classList.remove('show');
                }
            });
        }

        // Function to submit the hidden reset form
        function resetCart() {
            // Using a custom modal instead of confirm() would be better, but for now this works.
            if (confirm('Bạn có chắc chắn muốn xóa toàn bộ sản phẩm trong giỏ hàng không?')) {
                document.getElementById('resetForm').submit();
            }
        }
        
        // Auto-hide alert messages after 5 seconds
        setTimeout(() => {
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(alert => {
                alert.style.transition = 'opacity 0.5s ease';
                alert.style.opacity = '0';
                setTimeout(() => alert.remove(), 500);
            });
        }, 5000);
    </script>
</body>
</html>

