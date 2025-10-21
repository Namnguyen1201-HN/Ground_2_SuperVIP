<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tạo phiếu nhập kho</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/import-request.css">
        <style>
            /* Bỏ hiển thị dropdown khi có class active */
            .nav-item.dropdown.active .dropdown-menu {
                display: none;
            }

            /* Chỉ hiển thị khi hover */
            .nav-item.dropdown:hover .dropdown-menu {
                display: block !important;
            }

            /* Hoặc chỉ hiển thị khi click */
            .nav-item.dropdown.show .dropdown-menu {
                display: block;
            }
            
            /* Alert messages */
            .alert-message {
                padding: 15px 20px;
                margin: 20px auto;
                max-width: 600px;
                border-radius: 8px;
                font-weight: 500;
                text-align: center;
                animation: slideDown 0.3s ease;
            }
            
            .alert-message.success {
                background-color: #d4edda;
                color: #155724;
                border: 1px solid #c3e6cb;
            }
            
            .alert-message.error {
                background-color: #f8d7da;
                color: #721c24;
                border: 1px solid #f5c6cb;
            }
            
            @keyframes slideDown {
                from {
                    opacity: 0;
                    transform: translateY(-20px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }
        </style>
    </head>
    <body>

        <!-- Header -->
        <header class="header">
            <div class="header-container">
                <div class="logo">
                    <a href="${pageContext.request.contextPath}/DashBoard" class="logo">
                        <div class="logo-icon">S</div>
                        <span class="logo-text">SuperMarket</span>
                    </a>
                </div>
                <nav class="main-nav">
                    <a href="${pageContext.request.contextPath}/DashBoard" class="nav-item">
                        <i class="fas fa-chart-line"></i>
                        Tổng quan
                    </a>

                    <a href="${pageContext.request.contextPath}/product" class="nav-item">
                        <i class="fas fa-box"></i>
                        Hàng hóa
                    </a>

                    <div class="nav-item active dropdown">
                        <a href="#" class="dropdown-toggle">
                            <i class="fas fa-exchange-alt"></i>
                            Giao dịch
                            <i class="fas fa-caret-down"></i>
                        </a>
                        <div class="dropdown-menu">
                            <a href="${pageContext.request.contextPath}/Orders" class="dropdown-item">Đơn hàng</a>
                            <a href="${pageContext.request.contextPath}/import-request" class="dropdown-item">Tạo đơn nhập hàng</a>
                            <a href="${pageContext.request.contextPath}/stock-movements" class="dropdown-item">Danh sách movements</a>
                        </div>
                    </div>

                    <div class="nav-item dropdown">
                        <a href="#" class="dropdown-toggle">
                            <i class="fas fa-handshake"></i>
                            Đối tác
                            <i class="fas fa-caret-down"></i>
                        </a>
                        <div class="dropdown-menu">
                            <a href="${pageContext.request.contextPath}/Customer" class="dropdown-item">Khách hàng</a>
                            <a href="${pageContext.request.contextPath}/Supplier" class="dropdown-item">Nhà cung cấp</a>
                        </div>
                    </div>

                    <a href="${pageContext.request.contextPath}/AddUser" class="nav-item">
                        <i class="fas fa-users"></i>
                        Nhân viên
                    </a>

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
        <div class="main-container">
            
            <!-- LEFT PANEL: Product Selection -->
            <div class="product-panel">
                <div class="panel-header">
                    <h2>🛍️ Chọn sản phẩm từ nhà cung cấp</h2>
                </div>

                <!-- Supplier Selection Form -->
                <form method="get" action="${pageContext.request.contextPath}/import-request" class="supplier-form">
                    <div class="form-group">
                        <label for="supplierId">Chọn nhà cung cấp:</label>
                        <select name="supplierId" id="supplierId" class="form-control" 
                                onchange="this.form.submit()"
                                <c:if test="${not empty sessionScope.cartItems}">disabled</c:if>>
                            <option value="">-- Chọn nhà cung cấp --</option>
                            <c:forEach var="supplier" items="${listSuppliers}">
                                <option value="${supplier.supplierId}"
                                        <c:if test="${supplier.supplierId eq sessionScope.cartSupplierId}">selected</c:if>>
                                    ${supplier.supplierName}
                                </option>
                            </c:forEach>
                        </select>
                        <c:if test="${not empty sessionScope.cartItems}">
                            <p class="hint-text">⚠️ Đã chọn nhà cung cấp. Xóa giỏ hàng để đổi nhà cung cấp khác.</p>
                        </c:if>
                    </div>
                </form>

                <!-- Products Display -->
                <c:choose>
                    <c:when test="${empty selectedSupplierID}">
                        <div class="empty-state">
                            <p>🏭 Vui lòng chọn nhà cung cấp để xem sản phẩm</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <!-- Search Bar -->
                        <form method="get" action="${pageContext.request.contextPath}/import-request" class="search-form">
                            <input type="hidden" name="supplierId" value="${selectedSupplierID}" />
                            <input type="text" name="keyword" value="${keyword}" 
                                   placeholder="🔍 Tìm sản phẩm..." class="search-input" />
                            <button type="submit" class="btn btn-search">Tìm kiếm</button>
                        </form>

                        <!-- Product Grid -->
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
                                                        <img src="${product.imageURL}" alt="${product.productNameUnsigned}" />
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="no-image">📦</div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <div class="product-info">
                                                <h3 class="product-name">${product.productNameUnsigned}</h3>
                                                <p class="product-code">Mã: ${product.productCode}</p>
                                                <p class="product-price">
                                                    <fmt:formatNumber value="${product.costPrice}" type="currency" currencySymbol="₫"/>
                                                </p>
                                                
                                                <form method="post" action="${pageContext.request.contextPath}/import-request" class="add-form">
                                                    <input type="hidden" name="action" value="add" />
                                                    <input type="hidden" name="productDetailID" value="${product.productDetailID}" />
                                                    <input type="hidden" name="supplierId" value="${selectedSupplierID}" />
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

                                <c:if test="${totalPages > 1}">
                                    <div class="pagination">
                                        <c:if test="${currentPage > 1}">
                                            <a href="${pageContext.request.contextPath}/import-request?supplierId=${selectedSupplierID}&keyword=${keyword}&page=1" class="page-btn">
                                                ⏮️ Đầu
                                            </a>
                                            <a href="${pageContext.request.contextPath}/import-request?supplierId=${selectedSupplierID}&keyword=${keyword}&page=${currentPage - 1}" class="page-btn">
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
                                                    <a href="${pageContext.request.contextPath}/import-request?supplierId=${selectedSupplierID}&keyword=${keyword}&page=${i}" class="page-btn">${i}</a>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:forEach>

                                        <c:if test="${currentPage < totalPages}">
                                            <a href="${pageContext.request.contextPath}/import-request?supplierId=${selectedSupplierID}&keyword=${keyword}&page=${currentPage + 1}" class="page-btn">
                                                ➡️
                                            </a>
                                            <a href="${pageContext.request.contextPath}/import-request?supplierId=${selectedSupplierID}&keyword=${keyword}&page=${totalPages}" class="page-btn">
                                                ⏭️ Cuối
                                            </a>
                                        </c:if>
                                    </div>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- RIGHT PANEL: Import Invoice -->
            <div class="invoice-panel">
                <div class="panel-header">
                    <h2>📋 Phiếu nhập kho</h2>
                </div>

                <c:choose>
                    <c:when test="${empty cartItems}">
                        <div class="empty-cart">
                            <p>🛒 Giỏ hàng trống</p>
                            <p class="hint">Chọn sản phẩm từ nhà cung cấp để thêm vào phiếu nhập</p>
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
                                    <c:forEach var="item" items="${cartItems}">
                                        <c:set var="subtotal" value="${item.quantity * item.costPrice}" />
                                        <tr>
                                            <td>${item.productNameUnsigned}</td>
                                            <td>${item.productCode}</td>
                                            <td>
                                                <fmt:formatNumber value="${item.costPrice}" type="currency" currencySymbol="₫"/>
                                            </td>
                                            <td>
                                                <form method="post" action="${pageContext.request.contextPath}/import-request" class="quantity-form">
                                                    <input type="hidden" name="action" value="updateQuantity" />
                                                    <input type="hidden" name="productDetailID" value="${item.productDetailID}" />
                                                    <input type="hidden" name="supplierId" value="${selectedSupplierID}" />
                                                    <input type="number" name="quantity" value="${item.quantity}" 
                                                           min="1" onchange="this.form.submit()" />
                                                </form>
                                            </td>
                                            <td>
                                                <fmt:formatNumber value="${subtotal}" type="currency" currencySymbol="₫"/>
                                            </td>
                                            <td>
                                                <form method="post" action="${pageContext.request.contextPath}/import-request" style="display:inline;">
                                                    <input type="hidden" name="action" value="remove" />
                                                    <input type="hidden" name="productDetailID" value="${item.productDetailID}" />
                                                    <input type="hidden" name="supplierId" value="${selectedSupplierID}" />
                                                    <button type="submit" class="btn-remove" title="Xóa">🗑️</button>
                                                </form>
                                            </td>
                                        </tr>
                                        <c:set var="totalAmount" value="${totalAmount + subtotal}" />
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
                                <label for="toWarehouseID">Kho đích: <span class="required">*</span></label>
                                <select name="toWarehouseID" id="toWarehouseID" class="form-control" required>
                                    <option value="">-- Chọn kho --</option>
                                    <c:forEach var="warehouse" items="${listWarehouse}">
                                        <option value="${warehouse.warehouseId}"
                                                <c:if test="${warehouse.warehouseId eq selectedToWarehouseID}">selected</c:if>>
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
                                    ✅ Tạo phiếu nhập hàng
                                </button>
                            </div>
                        </form>

                        <!-- Hidden Reset Form -->
                        <form method="get" action="${pageContext.request.contextPath}/import-request" id="resetForm" style="display:none;">
                            <input type="hidden" name="action" value="reset" />
                            <input type="hidden" name="supplierId" value="${selectedSupplierID}" />
                        </form>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Scripts -->
        <script>
            // Dropdown trong thanh điều hướng
            document.querySelectorAll('.nav-item.dropdown > .dropdown-toggle').forEach(toggle => {
                toggle.addEventListener('click', function (e) {
                    e.preventDefault();
                    this.parentElement.classList.toggle('active');
                });
            });

            // Dropdown tài khoản người dùng
            const toggle = document.getElementById("dropdownToggle");
            const menu = document.getElementById("dropdownMenu");
            if (toggle && menu) {
                toggle.addEventListener("click", function (e) {
                    e.preventDefault();
                    menu.classList.toggle("show");
                });
                document.addEventListener("click", function (e) {
                    if (!toggle.contains(e.target) && !menu.contains(e.target)) {
                        menu.classList.remove("show");
                    }
                });
            }
        </script>

    </body>
</html>
