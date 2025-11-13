<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!-- DEBUG: Session info -->
<!-- Debug: roleID = ${sessionScope.roleID}, branchID = ${sessionScope.branchID}, currentUser = ${sessionScope.currentUser} -->

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý Hàng hóa - VIP Store</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <style>
            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background-color: #f8fafc;
                margin: 0;
                padding: 0;
            }

            .main-container {
                display: flex;
                min-height: 100vh;
                padding-top: 70px;
            }

            /* Sidebar Styling */
            .sidebar {
                width: 280px;
                background: white;
                border-right: 1px solid #e2e8f0;
                padding: 20px;
                box-shadow: 2px 0 10px rgba(0,0,0,0.05);
                position: sticky;
                top: 70px;
                height: calc(100vh - 70px);
                overflow-y: auto;
            }

            .sidebar h4 {
                color: #2d3748;
                font-size: 16px;
                font-weight: 600;
                margin-bottom: 20px;
                border-bottom: 2px solid #3182ce;
                padding-bottom: 10px;
            }

            .filter-section {
                margin-bottom: 25px;
            }

            .filter-section label {
                display: block;
                color: #4a5568;
                font-weight: 500;
                margin-bottom: 8px;
                font-size: 14px;
            }

            .filter-section select,
            .filter-section input[type="text"],
            .filter-section input[type="number"] {
                width: 100%;
                padding: 8px 12px;
                border: 1px solid #d1d5db;
                border-radius: 6px;
                font-size: 14px;
            }

            .filter-section input[type="radio"] {
                margin-right: 8px;
            }

            .filter-item {
                margin-bottom: 8px;
                display: flex;
                align-items: center;
            }

            .filter-item label {
                margin-bottom: 0;
                font-weight: normal;
                font-size: 14px;
            }

            .btn {
                padding: 8px 16px;
                border: none;
                border-radius: 6px;
                font-size: 14px;
                font-weight: 500;
                cursor: pointer;
                transition: all 0.2s;
                text-decoration: none;
                display: inline-flex;
                align-items: center;
                gap: 6px;
            }

            .btn-primary {
                background: linear-gradient(135deg, #3182ce 0%, #2c5aa0 100%);
                color: white;
            }

            .btn-primary:hover {
                transform: translateY(-1px);
                box-shadow: 0 4px 12px rgba(49, 130, 206, 0.4);
            }

            .btn-secondary {
                background: #e2e8f0;
                color: #4a5568;
            }

            .btn-sm {
                padding: 6px 12px;
                font-size: 12px;
            }

            /* Content Area */
            .content {
                flex: 1;
                padding: 20px 30px;
            }

            .page-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 30px;
            }

            .page-title {
                font-size: 28px;
                font-weight: 700;
                color: #2d3748;
                margin: 0;
            }

            .header-actions {
                display: flex;
                gap: 15px;
                align-items: center;
            }

            .search-container {
                position: relative;
            }

            .search-input {
                padding: 10px 40px 10px 15px;
                border: 1px solid #d1d5db;
                border-radius: 8px;
                width: 250px;
                font-size: 14px;
            }

            .search-input:focus {
                outline: none;
                border-color: #3182ce;
                box-shadow: 0 0 0 3px rgba(49, 130, 206, 0.1);
            }

            /* Products Table */
            .products-container {
                background: white;
                border-radius: 12px;
                box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
                overflow: hidden;
            }

            .products-header {
                padding: 20px 25px;
                border-bottom: 1px solid #e2e8f0;
                display: flex;
                justify-content: space-between;
                align-items: center;
            }

            .products-title {
                font-size: 18px;
                font-weight: 600;
                color: #2d3748;
            }

            .products-table {
                width: 100%;
                border-collapse: separate;
                border-spacing: 0;
                background: white;
                border-radius: 12px;
                overflow: hidden;
                box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
                border: 1px solid #e2e8f0;
            }

            .products-table thead {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            }

            .products-table th {
                padding: 18px 20px;
                text-align: left;
                font-weight: 700;
                color: white;
                font-size: 0.9rem;
                text-transform: uppercase;
                letter-spacing: 0.5px;
                border: none;
                position: relative;
            }

            .products-table th:not(:last-child)::after {
                content: '';
                position: absolute;
                right: 0;
                top: 25%;
                bottom: 25%;
                width: 1px;
                background: rgba(255,255,255,0.2);
            }

            .products-table td {
                padding: 16px 20px;
                border: none;
                border-bottom: 1px solid #f1f5f9;
                vertical-align: middle;
                transition: all 0.3s ease;
            }

            .product-row {
                cursor: pointer;
                transition: all 0.3s ease;
                position: relative;
            }

            .product-row:hover {
                background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
                transform: translateY(-2px);
                box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
            }

            .product-row:hover td {
                border-color: #cbd5e1;
            }

            .product-row:last-child td {
                border-bottom: none;
            }

            .product-image {
                width: 60px;
                height: 60px;
                border-radius: 10px;
                object-fit: cover;
                background: #f7fafc;
                border: 2px solid #e2e8f0;
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
                transition: all 0.3s ease;
            }

            .product-image-placeholder {
                width: 60px;
                height: 60px;
                border-radius: 10px;
                background: #f8fafc;
                border: 2px dashed #e2e8f0;
                display: flex;
                align-items: center;
                justify-content: center;
                color: #a0aec0;
                font-size: 12px;
            }

            .product-image-placeholder::before {
                content: '';
                width: 24px;
                height: 24px;
                background: #e2e8f0;
                border-radius: 50%;
            }

            .product-row:hover .product-image {
                transform: scale(1.05);
                border-color: #667eea;
                box-shadow: 0 6px 20px rgba(102, 126, 234, 0.2);
            }

            .product-name {
                font-size: 14px;
                font-weight: 600;
                color: #2d3748;
                line-height: 1.3;
                max-width: 200px;
                margin-bottom: 2px;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
            }

            .product-code {
                font-size: 12px;
                color: #718096;
                font-family: 'Courier New', monospace;
                background: #f7fafc;
                padding: 2px 8px;
                border-radius: 12px;
                font-weight: 500;
            }

            .product-price {
                font-size: 16px;
                font-weight: 700;
                color: #e53e3e;
                position: relative;
            }

            .product-price::before {
                content: '₫';
                font-size: 12px;
                vertical-align: top;
                margin-right: 2px;
            }

            .product-stock {
                font-size: 14px;
                font-weight: 700;
                padding: 6px 16px;
                border-radius: 25px;
                display: inline-block;
                min-width: 70px;
                text-align: center;
                text-transform: uppercase;
                letter-spacing: 0.5px;
            }

            .product-stock.in-stock {
                color: #22543d;
                background: linear-gradient(135deg, #c6f6d5, #9ae6b4);
                border: 2px solid #38a169;
                box-shadow: 0 2px 8px rgba(56, 161, 105, 0.3);
            }

            .product-stock.out-of-stock {
                color: #742a2a;
                background: linear-gradient(135deg, #fed7d7, #feb2b2);
                border: 2px solid #e53e3e;
                box-shadow: 0 2px 8px rgba(229, 62, 62, 0.3);
            }

            /* Badge styles - match image */
            .badge {
                padding: 6px 16px;
                border-radius: 20px;
                font-size: 12px;
                font-weight: 600;
                text-transform: uppercase;
                letter-spacing: 0.5px;
            }

            .badge-success {
                background: #d4edda;
                color: #155724;
                border: 1px solid #c3e6cb;
            }

            .badge-danger {
                background: #f8d7da;
                color: #721c24;
                border: 1px solid #f5c6cb;
            }

            /* Simple table styling */
            .product-name {
                font-size: 14px;
                color: #2d3748;
                line-height: 1.4;
            }

            .product-price {
                font-size: 14px;
                font-weight: 600;
                color: #e53e3e;
            }

            .stock-quantity {
                font-size: 14px;
                font-weight: 500;
                color: #2d3748;
            }

            .product-code {
                font-size: 13px;
                font-weight: 500;
                color: #4a5568;
            }

            /* Detail row styles */
            .detail-row {
                background: #f8fafc;
            }

            .detail-cell {
                padding: 0 !important;
                border: none !important;
            }

            .detail-content {
                padding: 20px;
                background: white;
                margin: 10px 20px;
                border-radius: 8px;
                box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
                border-left: 4px solid #667eea;
            }

            .detail-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 15px;
                padding-bottom: 10px;
                border-bottom: 1px solid #e2e8f0;
            }

            .detail-header h6 {
                margin: 0;
                color: #2d3748;
                font-weight: 600;
            }

            .close-btn {
                background: #e2e8f0;
                border: none;
                border-radius: 50%;
                width: 25px;
                height: 25px;
                font-size: 16px;
                color: #4a5568;
                cursor: pointer;
                display: flex;
                align-items: center;
                justify-content: center;
            }

            .close-btn:hover {
                background: #cbd5e0;
            }

            .detail-info {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 20px;
            }

            .info-left,
            .info-right {
                display: flex;
                flex-direction: column;
                gap: 8px;
            }

            .info-item {
                font-size: 14px;
                color: #2d3748;
            }

            .info-item strong {
                color: #4a5568;
                margin-right: 8px;
            }

            /* Row hover and selection */
            .product-row {
                cursor: pointer;
                transition: background-color 0.2s ease;
            }

            .product-row:hover {
                background: #f7fafc;
            }

            .product-row.selected {
                background: #e6fffa;
            }

            /* Description section */
            .detail-description {
                margin-top: 15px;
                padding-top: 15px;
                border-top: 1px solid #e2e8f0;
            }

            .description-text {
                margin: 8px 0 0 0;
                color: #2d3748;
                line-height: 1.5;
                font-size: 14px;
                background: #f8fafc;
                padding: 12px;
                border-radius: 6px;
                border-left: 3px solid #667eea;
            }

            .stock-badge {
                display: inline-block;
                padding: 4px 8px;
                border-radius: 4px;
                font-size: 12px;
                font-weight: 500;
            }

            .stock-available {
                background: #c6f6d5;
                color: #22543d;
            }

            .stock-low {
                background: #fed7d7;
                color: #742a2a;
            }

            .product-actions {
                position: absolute;
                top: 15px;
                right: 15px;
                display: none;
                gap: 8px;
            }

            .product-card:hover .product-actions {
                display: flex;
            }

            .action-btn {
                width: 32px;
                height: 32px;
                border-radius: 6px;
                border: none;
                display: flex;
                align-items: center;
                justify-content: center;
                cursor: pointer;
                transition: all 0.2s;
                font-size: 12px;
            }

            .btn-edit {
                background: #3182ce;
                color: white;
            }

            .btn-edit:hover {
                background: #2c5aa0;
            }

            .btn-delete {
                background: #e53e3e;
                color: white;
            }

            .btn-delete:hover {
                background: #c53030;
            }

            /* Additional styling for better match with mockup */
            .products-count {
                font-size: 14px;
                color: #718096;
                background: #f7fafc;
                padding: 6px 12px;
                border-radius: 6px;
            }

            .product-card .product-info span {
                display: block;
                margin-bottom: 2px;
            }

            .product-card .product-info strong {
                min-width: 80px;
                display: inline-block;
            }

            /* Status badges */
            .status-active {
                background: #c6f6d5;
                color: #22543d;
                padding: 2px 8px;
                border-radius: 4px;
                font-size: 12px;
                font-weight: 500;
            }

            .status-inactive {
                background: #fed7d7;
                color: #742a2a;
                padding: 2px 8px;
                border-radius: 4px;
                font-size: 12px;
                font-weight: 500;
            }





            /* Responsive design */
            @media (max-width: 768px) {
                .main-container {
                    flex-direction: column;
                }

                .sidebar {
                    width: 100%;
                    position: relative;
                    top: 0;
                    height: auto;
                }

                .page-header {
                    flex-direction: column;
                    gap: 15px;
                    align-items: flex-start;
                }

                .header-actions {
                    width: 100%;
                    justify-content: space-between;
                }

                .search-input {
                    width: 200px;
                }

                .products-table {
                    font-size: 12px;
                }

                .products-table th,
                .products-table td {
                    padding: 8px 6px;
                }

                .product-name {
                    font-size: 11px;
                    line-height: 1.2;
                    max-width: 120px;
                }

                .product-image {
                    width: 40px;
                    height: 40px;
                }

                .product-code {
                    font-size: 10px;
                }

                .product-stock {
                    padding: 2px 6px;
                    font-size: 10px;
                }

                .modal-dialog {
                    margin: 10px;
                    max-width: 95%;
                }

                .product-detail-grid {
                    grid-template-columns: 1fr;
                    gap: 10px;
                }

                .product-detail-item {
                    padding: 12px;
                }

                .detail-content {
                    grid-template-columns: 1fr;
                    gap: 15px;
                }

                .detail-info {
                    grid-template-columns: 1fr;
                }

                .detail-image,
                .detail-image-placeholder {
                    width: 80px;
                    height: 80px;
                }

                .detail-info {
                    grid-template-columns: 1fr;
                    gap: 10px;
                }

                .detail-content {
                    margin: 5px 10px;
                    padding: 15px;
                }

                .description-text {
                    font-size: 13px;
                    padding: 10px;
                }
            }
        </style>
    </head>

    <body>
        <%@ include file="header_admin.jsp" %>

        <div class="main-container">
            <aside class="sidebar">
                <h4><i class="fas fa-filter me-2"></i>Bộ lọc</h4>

                <!-- FORM LỌC BÊN TRÁI -->
                <form id="filtersForm" action="${pageContext.request.contextPath}/product" method="get">
                    <input type="hidden" name="action" value="list"/>

                    <!-- Nhóm hàng -->
                    <div class="filter-section">
                        <label>Nhóm hàng</label>
                        <!-- Danh sách nhóm hàng từ database -->
                        <c:forEach var="c" items="${categories}">
                            <c:set var="isSelected" value="false"/>
                            <c:if test="${not empty selectedCategoryNames}">
                                <c:forEach var="cn" items="${selectedCategoryNames}">
                                    <c:if test="${cn eq c.categoryName}">
                                        <c:set var="isSelected" value="true"/>
                                    </c:if>
                                </c:forEach>
                            </c:if>
                            <div class="filter-item">
                                <input type="checkbox" id="cat_${c.categoryID}" name="categoryName" 
                                       value="${c.categoryName}" ${isSelected ? 'checked' : ''} />
                                <label for="cat_${c.categoryID}">${c.categoryName}</label>
                            </div>
                        </c:forEach>
                        
                        <!-- Debug info -->
                        <c:if test="${empty categories}">
                            <div class="filter-item">
                                <p class="text-muted">Không có danh mục nào từ database</p>
                            </div>
                        </c:if>
                        
                        <!-- Debug: Show selected categories -->
                        <c:if test="${not empty selectedCategoryNames}">
                            <div class="filter-item" style="font-size: 11px; color: #666;">
                                <em>Đã chọn: ${selectedCategoryNames}</em>
                            </div>
                        </c:if>

                    </div>

                    <!-- Tồn kho -->
                    <div class="filter-section">
                        <label>Tồn kho</label>
                        <div class="filter-item">
                            <input type="radio" id="in-stock" name="stock" value="in"
                                   <c:if test="${empty stock or stock == 'in'}">checked</c:if> />
                                   <label for="in-stock">Còn hàng trong kho</label>
                            </div>
                            <div class="filter-item">
                                <input type="radio" id="out-of-stock" name="stock" value="out"
                                <c:if test="${stock == 'out'}">checked</c:if> />
                                <label for="out-of-stock">Hết hàng trong kho</label>
                            </div>
                        </div>

                        <!-- Trạng thái -->
                        <div class="filter-section">
                            <label>Trạng thái</label>
                            <div class="filter-item">
                                <input type="radio" id="all-status" name="status" value="all"
                                <c:if test="${empty status or status == 'all'}">checked</c:if> />
                                <label for="all-status">Tất cả</label>
                            </div>
                            <div class="filter-item">
                                <input type="radio" id="active-status" name="status" value="active"
                                <c:if test="${status == 'active'}">checked</c:if> />
                                <label for="active-status">Đang bán</label>
                            </div>
                            <div class="filter-item">
                                <input type="radio" id="inactive-status" name="status" value="inactive"
                                <c:if test="${status == 'inactive'}">checked</c:if> />
                                <label for="inactive-status">Ngừng bán</label>
                            </div>
                        </div>

                        <!-- Nút submit -->
                        <div style="margin-top: 20px;">
                            <button type="submit" class="btn btn-primary btn-sm">
                                <i class="fas fa-filter"></i> Lọc sản phẩm
                            </button>
                            <a href="product" class="btn btn-secondary btn-sm" style="margin-left: 8px;">
                                <i class="fas fa-undo"></i> Bỏ chọn
                            </a>
                        </div>
                    </form>
                </aside>

                <main class="content">
                    <div class="page-header">
                        <h1 class="page-title">Hàng hóa</h1>
                        <div class="header-actions">
                            <!-- FORM TÌM KIẾM -->
                            <form action="${pageContext.request.contextPath}/product" method="get" class="search-container">
                            <input type="hidden" name="action" value="list"/>

                            <!-- Render lại các filter đang chọn -->
                            <c:forEach var="cn" items="${selectedCategoryNames}">
                                <input type="hidden" name="categoryName" value="${cn}"/>
                            </c:forEach>
                            <input type="hidden" name="stock" value="${stock}"/>
                            <input type="hidden" name="status" value="${status}"/>

                            <input type="text" name="keyword" class="search-input" placeholder="Thêm tên hàng"
                                   value="<c:out value='${keyword}'/>" />
                            <i class="fas fa-search" style="position: absolute; right: 15px; top: 50%; transform: translateY(-50%); color: #9ca3af;"></i>
                        </form>

                        <!-- Chỉ Admin (Role 0) mới có nút thêm mới -->
                        <c:if test="${sessionScope.roleID == 0 or sessionScope.roleID == '0'}">
                            <a href="${pageContext.request.contextPath}/product?action=add" class="btn btn-primary">
                                <i class="fas fa-plus"></i> Thêm mới
                            </a>
                        </c:if>
                    </div>
                </div>

                <!-- Products Container -->
                <div class="products-container">
                    <div class="products-header">
                        <h2 class="products-title">Hàng hóa</h2>
                        <div class="products-count">
                            <c:choose>
                                <c:when test="${not empty products}">
                                    ${fn:length(products)} sản phẩm
                                </c:when>
                                <c:otherwise>
                                    3 sản phẩm
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <table class="products-table">
                        <thead>
                            <tr>
                                <th style="width: 120px;" class="text-center">MÃ HÀNG</th>
                                <th>TÊN HÀNG</th>
                                <th style="width: 150px;" class="text-end">GIÁ BÁN</th>
                                <th style="width: 100px;" class="text-center">TỒN KHO</th>
                                <th style="width: 120px;" class="text-center">TRẠNG THÁI</th>
                                <!-- Chỉ hiện cột Actions cho Admin (Role 0) -->
                                <c:if test="${sessionScope.roleID == 0 or sessionScope.roleID == '0'}">
                                    <th style="width: 120px;" class="text-center">THAO TÁC</th>
                                </c:if>
                            </tr>
                        </thead>
                        <tbody>
                            <!-- Real products from database -->
                            <c:forEach var="p" items="${products}" varStatus="status">
                                <tr class="product-row" onclick="toggleRowDetail(this, '${p.productId}')" data-product-id="${p.productId}">
                                    <td class="text-center">
                                        <span class="product-code">${p.productId}</span>
                                    </td>
                                    <td>
                                        <span class="product-name" title="${p.productName}">${p.productName}</span>
                                    </td>
                                    <td class="text-end">
                                        <span class="product-price">
                                            <c:choose>
                                                <c:when test="${not empty p.retailPrice and p.retailPrice > 0}">
                                                    <fmt:formatNumber value="${p.retailPrice}" type="number" minFractionDigits="0"/>
                                                </c:when>
                                                <c:otherwise>0</c:otherwise>
                                            </c:choose>
                                        </span>
                                    </td>
                                    <td class="text-center">
                                        <c:set var="qty" value="${p.totalQty != null ? p.totalQty : 0}" />
                                        <span class="stock-quantity">${qty}</span>
                                    </td>
                                    <td class="text-center">
                                        <c:choose>
                                            <c:when test="${p.isActive == true}">
                                                <span class="badge badge-success">Đang bán</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-danger">Ngừng bán</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <!-- Chỉ hiện Actions cho Admin (Role 0) -->
                                    <c:if test="${sessionScope.roleID == 0 or sessionScope.roleID == '0'}">
                                        <td class="text-center">
                                            <a href="product?action=edit&id=${p.productId}" class="btn btn-sm btn-primary" 
                                               style="padding: 4px 8px; font-size: 12px; text-decoration: none; 
                                                      background-color: #007bff; color: white; border-radius: 4px;"
                                               onclick="event.stopPropagation();">
                                                <i class="fas fa-edit"></i> Sửa
                                            </a>
                                        </td>
                                    </c:if>
                                </tr>
                                <!-- Detail row - initially hidden -->
                                <tr class="detail-row" id="detail-${p.productId}" style="display: none;">
                                    <td colspan="${(sessionScope.roleID == 0 or sessionScope.roleID == '0') ? '6' : '5'}" class="detail-cell">
                                        <div class="detail-content">
                                            <div class="detail-header">
                                                <h6>${p.productName}</h6>
                                                <button type="button" class="close-btn" onclick="closeDetail('${p.productId}')">×</button>
                                            </div>
                                            <div class="detail-info">
                                                <div class="info-left">
                                                    <div class="info-item">
                                                        <strong>Mã hàng:</strong> ${p.productId}
                                                    </div>
                                                    <div class="info-item">
                                                        <strong>Nhóm hàng:</strong> 
                                                        <c:choose>
                                                            <c:when test="${not empty p.categoryName}">${p.categoryName}</c:when>
                                                            <c:otherwise>Điện thoại</c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                    <div class="info-item">
                                                        <strong>Thương hiệu:</strong>
                                                        <c:choose>
                                                            <c:when test="${not empty p.brandName}">${p.brandName}</c:when>
                                                            <c:otherwise>Apple</c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                    <div class="info-item">
                                                        <strong>Thời gian tạo:</strong> 
                                                        <c:set var="productDetail" value="${productDetailMap[p.productId]}" />
                                                        <c:choose>
                                                            <c:when test="${not empty formattedDateMap[p.productId]}">
                                                                ${formattedDateMap[p.productId]}
                                                            </c:when>
                                                            <c:otherwise>Không có thông tin</c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </div>
                                                <div class="info-right">
                                                    <div class="info-item">
                                                        <strong>Giá vốn:</strong>
                                                        <c:choose>
                                                            <c:when test="${not empty p.costPrice and p.costPrice > 0}">
                                                                <fmt:formatNumber value="${p.costPrice}" type="number" minFractionDigits="0"/> đ
                                                            </c:when>
                                                            <c:otherwise>25,000,000 đ</c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                    <div class="info-item">
                                                        <strong>Giá bán thực:</strong>
                                                        <c:choose>
                                                            <c:when test="${not empty p.retailPrice and p.retailPrice > 0}">
                                                                <fmt:formatNumber value="${p.retailPrice}" type="number" minFractionDigits="0"/> đ
                                                            </c:when>
                                                            <c:otherwise>29,990,000 đ</c:otherwise>
                                                        </c:choose>
                                                    </div>

                                                    <div class="info-item">
                                                        <strong>Nhà cung cấp:</strong> 
                                                        <c:set var="productDetail" value="${productDetailMap[p.productId]}" />
                                                        <c:choose>
                                                            <c:when test="${not empty productDetail && not empty productDetail.supplierName}">
                                                                ${productDetail.supplierName}
                                                            </c:when>
                                                            <c:when test="${not empty p.supplierName}">
                                                                ${p.supplierName}
                                                            </c:when>
                                                            <c:otherwise>Chưa có thông tin</c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="detail-description">
                                                <div class="info-item">
                                                    <strong>Mô tả:</strong>
                                                    <p class="description-text">
                                                        <c:set var="productDetail" value="${productDetailMap[p.productId]}" />
                                                        <c:choose>
                                                            <c:when test="${not empty productDetail && not empty productDetail.description}">
                                                                ${productDetail.description}
                                                            </c:when>
                                                            <c:when test="${not empty p.productName}">
                                                                ${p.productName}
                                                            </c:when>
                                                            <c:otherwise>Không có mô tả</c:otherwise>
                                                        </c:choose>
                                                    </p>
                                                </div>
                                                <c:if test="${not empty productDetail}">
                                                    <div class="info-item">
                                                        <strong>Mã sản phẩm:</strong>
                                                        <span class="text-muted">
                                                            <c:choose>
                                                                <c:when test="${not empty productDetail.productCode}">
                                                                    ${productDetail.productCode}
                                                                </c:when>
                                                                <c:otherwise>Chưa có mã</c:otherwise>
                                                            </c:choose>
                                                        </span>
                                                    </div>
                                                    <div class="info-item">
                                                        <strong>Thời hạn bảo hành:</strong>
                                                        <span class="text-muted">
                                                            <c:choose>
                                                                <c:when test="${not empty productDetail.warrantyPeriod}">
                                                                    ${productDetail.warrantyPeriod}
                                                                </c:when>
                                                                <c:otherwise>Không có thông tin</c:otherwise>
                                                            </c:choose>
                                                        </span>
                                                    </div>
                                                </c:if>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
   <!-- ========== PAGINATION ROW ========== -->
                            <tr>
                                <td colspan="${(sessionScope.roleID == 0 or sessionScope.roleID == '0') ? '6' : '5'}">
                                    <div class="pagination-wrapper" style="display:flex; justify-content:space-between; align-items:center; padding:12px 8px;">
                                        <!-- left: info -->
                                        <div class="pagination-info">
                                            <c:choose>
                                                <c:when test="${not empty totalItems}">
                                                    Tổng <strong>${totalItems}</strong> sản phẩm — Trang <strong>${currentPage}</strong> / <strong>${totalPages}</strong>
                                                </c:when>
                                                <c:otherwise>
                                                    Không có sản phẩm
                                                </c:otherwise>
                                            </c:choose>
                                        </div>

                                        <!-- right: controls -->
                                        <div class="pagination-controls" style="display:flex; align-items:center; gap:10px;">

                                            <!-- small pageSize selector (keeps filters via hidden inputs) -->
                                            <form id="pageSizeForm" method="get" action="${pageContext.request.contextPath}/product" style="display:inline-flex; align-items:center; gap:6px;">
                                                <input type="hidden" name="action" value="list" />
                                                <input type="hidden" name="page" value="1" />
                                                <c:if test="${not empty param.productName}"><input type="hidden" name="productName" value="${param.productName}" /></c:if>
                                                <c:if test="${not empty param.keyword}"><input type="hidden" name="keyword" value="${param.keyword}" /></c:if>
                                                <c:if test="${not empty param.status}"><input type="hidden" name="status" value="${param.status}" /></c:if>
                                                <c:if test="${not empty param.stock}"><input type="hidden" name="stock" value="${param.stock}" /></c:if>
                                                <c:if test="${not empty param.stockThreshold}"><input type="hidden" name="stockThreshold" value="${param.stockThreshold}" /></c:if>
                                                <c:forEach var="cat" items="${paramValues.categoryName}">
                                                    <input type="hidden" name="categoryName" value="${cat}" />
                                                </c:forEach>

                                                <label for="ps">Hiển thị</label>
                                                <select id="ps" name="pageSize" onchange="document.getElementById('pageSizeForm').submit();" style="padding:4px;">
                                                    <option value="10" ${pageSize == 10 ? 'selected' : ''}>10</option>
                                                    <option value="20" ${pageSize == 20 ? 'selected' : ''}>20</option>
                                                    <option value="50" ${pageSize == 50 ? 'selected' : ''}>50</option>
                                                    <option value="100" ${pageSize == 100 ? 'selected' : ''}>100</option>
                                                </select>
                                                <span>/ trang</span>
                                            </form>

                                            <!-- Prev -->
                                            <c:choose>
                                                <c:when test="${currentPage > 1}">
                                                    <c:url var="prevUrl" value="/product">
                                                        <c:param name="action" value="list"/>
                                                        <c:param name="page" value="${currentPage - 1}"/>
                                                        <c:param name="pageSize" value="${pageSize}"/>
                                                        <c:if test="${not empty param.productName}"><c:param name="productName" value="${param.productName}"/></c:if>
                                                        <c:if test="${not empty param.keyword}"><c:param name="keyword" value="${param.keyword}"/></c:if>
                                                        <c:if test="${not empty param.status}"><c:param name="status" value="${param.status}"/></c:if>
                                                        <c:if test="${not empty param.stock}"><c:param name="stock" value="${param.stock}"/></c:if>
                                                        <c:if test="${not empty param.stockThreshold}"><c:param name="stockThreshold" value="${param.stockThreshold}"/></c:if>
                                                        <c:forEach var="cat" items="${paramValues.categoryName}"><c:param name="categoryName" value="${cat}"/></c:forEach>
                                                    </c:url>
                                                    <a href="${prevUrl}" class="btn btn-sm" style="padding:6px 10px; text-decoration:none;">&laquo; Trước</a>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="btn btn-sm disabled" style="padding:6px 10px; opacity:0.5;">&laquo; Trước</span>
                                                </c:otherwise>
                                            </c:choose>

                                            <!-- page numbers (window around currentPage) -->
                                            <c:set var="window" value="3" />
                                            <c:set var="start" value="${currentPage - window < 1 ? 1 : currentPage - window}" />
                                            <c:set var="end" value="${currentPage + window > totalPages ? totalPages : currentPage + window}" />

                                            <c:forEach var="i" begin="${start}" end="${end}">
                                                <c:url var="pageUrl" value="/product">
                                                    <c:param name="action" value="list" />
                                                    <c:param name="page" value="${i}" />
                                                    <c:param name="pageSize" value="${pageSize}" />
                                                    <c:if test="${not empty param.productName}"><c:param name="productName" value="${param.productName}" /></c:if>
                                                    <c:if test="${not empty param.keyword}"><c:param name="keyword" value="${param.keyword}" /></c:if>
                                                    <c:if test="${not empty param.status}"><c:param name="status" value="${param.status}" /></c:if>
                                                    <c:if test="${not empty param.stock}"><c:param name="stock" value="${param.stock}" /></c:if>
                                                    <c:if test="${not empty param.stockThreshold}"><c:param name="stockThreshold" value="${param.stockThreshold}" /></c:if>
                                                    <c:forEach var="cat" items="${paramValues.categoryName}"><c:param name="categoryName" value="${cat}" /></c:forEach>
                                                </c:url>

                                                <c:choose>
                                                    <c:when test="${i == currentPage}">
                                                        <a href="${pageUrl}" class="btn btn-sm active" style="padding:6px 8px; font-weight:bold; background:#eee; text-decoration:none;">${i}</a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <a href="${pageUrl}" class="btn btn-sm" style="padding:6px 8px; text-decoration:none;">${i}</a>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:forEach>

                                            <!-- Next -->
                                            <c:choose>
                                                <c:when test="${currentPage < totalPages}">
                                                    <c:url var="nextUrl" value="/product">
                                                        <c:param name="action" value="list"/>
                                                        <c:param name="page" value="${currentPage + 1}"/>
                                                        <c:param name="pageSize" value="${pageSize}"/>
                                                        <c:if test="${not empty param.productName}"><c:param name="productName" value="${param.productName}"/></c:if>
                                                        <c:if test="${not empty param.keyword}"><c:param name="keyword" value="${param.keyword}"/></c:if>
                                                        <c:if test="${not empty param.status}"><c:param name="status" value="${param.status}"/></c:if>
                                                        <c:if test="${not empty param.stock}"><c:param name="stock" value="${param.stock}"/></c:if>
                                                        <c:if test="${not empty param.stockThreshold}"><c:param name="stockThreshold" value="${param.stockThreshold}"/></c:if>
                                                        <c:forEach var="cat" items="${paramValues.categoryName}"><c:param name="categoryName" value="${cat}"/></c:forEach>
                                                    </c:url>
                                                    <a href="${nextUrl}" class="btn btn-sm" style="padding:6px 10px; text-decoration:none;">Sau &raquo;</a>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="btn btn-sm disabled" style="padding:6px 10px; opacity:0.5;">Sau &raquo;</span>
                                                </c:otherwise>
                                            </c:choose>

                                        </div>
                                    </div>
                                </td>
                            </tr>
                            <!-- ========== END PAGINATION ========== -->
                        </tbody>
                    </table>
                </div>
            </main>
        </div>



        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
        <script>
                                                    let currentProductId = null;

                                                    // Toggle product detail row
                                                    function toggleRowDetail(rowElement, productId) {
                                                        const detailRow = document.getElementById('detail-' + productId);
                                                        const allProductRows = document.querySelectorAll('.product-row');
                                                        const allDetailRows = document.querySelectorAll('.detail-row');

                                                        // Close all other detail rows and remove selection
                                                        allDetailRows.forEach(row => {
                                                            if (row.id !== 'detail-' + productId) {
                                                                row.style.display = 'none';
                                                            }
                                                        });

                                                        allProductRows.forEach(row => {
                                                            if (row !== rowElement) {
                                                                row.classList.remove('selected');
                                                            }
                                                        });

                                                        // Toggle current detail row
                                                        if (detailRow.style.display === 'none' || detailRow.style.display === '') {
                                                            detailRow.style.display = 'table-row';
                                                            rowElement.classList.add('selected');

                                                            // Smooth scroll to detail
                                                            setTimeout(() => {
                                                                detailRow.scrollIntoView({
                                                                    behavior: 'smooth',
                                                                    block: 'nearest'
                                                                });
                                                            }, 100);
                                                        } else {
                                                            detailRow.style.display = 'none';
                                                            rowElement.classList.remove('selected');
                                                        }
                                                    }

                                                    // Close detail row
                                                    function closeDetail(productId) {
                                                        const detailRow = document.getElementById('detail-' + productId);
                                                        const productRow = document.querySelector(`tr[data-product-id="${productId}"]`);

                                                        if (detailRow) {
                                                            detailRow.style.display = 'none';
                                                        }

                                                        if (productRow) {
                                                            productRow.classList.remove('selected');
                                                        }
                                                    }

                                                    // Create product detail HTML (như trong hình)
                                                    function createProductDetailHTML(productId) {
                                                        const demoProducts = {
                                                            '1': {
                                                                name: 'iPhone 15 Pro Max',
                                                                fullName: 'iPhone 15 Pro Max 256GB, chip A17 Pro, màn hình Super Retina XDR 6.7 inch',
                                                                code: 'IP15PM256',
                                                                brand: 'Apple',
                                                                category: 'Điện thoại',
                                                                supplier: 'Công ty TNHH Apple Việt Nam',
                                                                costPrice: '25.000.000 ₫',
                                                                retailPrice: '29.990.000 ₫',
                                                                discount: '0.0%',
                                                                createDate: '20/10/2025 00:00',
                                                                stock: 5
                                                            },
                                                            '2': {
                                                                name: 'Samsung Galaxy S24 Ultra',
                                                                fullName: 'Samsung Galaxy S24 Ultra 512GB, S Pen, màn hình Dynamic AMOLED 6.8 inch',
                                                                code: 'SGS24U512',
                                                                brand: 'Samsung',
                                                                category: 'Điện thoại',
                                                                supplier: 'Samsung Electronics Việt Nam',
                                                                costPrice: '23.000.000 ₫',
                                                                retailPrice: '26.990.000 ₫',
                                                                discount: '0.0%',
                                                                createDate: '20/10/2025 00:00',
                                                                stock: 4
                                                            },
                                                            '3': {
                                                                name: 'Xiaomi 14 Ultra',
                                                                fullName: 'Xiaomi 14 Ultra 512GB, camera Leica, màn hình LTPO OLED 6.73 inch',
                                                                code: 'MI14U512',
                                                                brand: 'Xiaomi',
                                                                category: 'Điện thoại',
                                                                supplier: 'Xiaomi Technology Việt Nam',
                                                                costPrice: '19.000.000 ₫',
                                                                retailPrice: '21.990.000 ₫',
                                                                discount: '0.0%',
                                                                createDate: '20/10/2025 00:00',
                                                                stock: 3
                                                            }
                                                        };

                                                        const product = demoProducts[productId] || demoProducts['1'];

                                                        return `
                <div class="product-detail-grid">
                    <div class="product-detail-item">
                        <span class="product-detail-label">Mã sản phẩm</span>
                        <span class="product-detail-value">${product.code}</span>
                    </div>
                    <div class="product-detail-item">
                        <span class="product-detail-label">Tên sản phẩm</span>
                        <span class="product-detail-value">${product.name}</span>
                    </div>
                    <div class="product-detail-item">
                        <span class="product-detail-label">Mô tả chi tiết</span>
                        <span class="product-detail-value">${product.fullName}</span>
                    </div>
                    <div class="product-detail-item">
                        <span class="product-detail-label">Nhóm hàng</span>
                        <span class="product-detail-value">${product.category}</span>
                    </div>
                    <div class="product-detail-item">
                        <span class="product-detail-label">Thương hiệu</span>
                        <span class="product-detail-value">${product.brand}</span>
                    </div>
                    <div class="product-detail-item">
                        <span class="product-detail-label">Nhà cung cấp</span>
                        <span class="product-detail-value">${product.supplier}</span>
                    </div>
                    <div class="product-detail-item">
                        <span class="product-detail-label">Thời gian tạo</span>
                        <span class="product-detail-value">${product.createDate}</span>
                    </div>
                    <div class="product-detail-item">
                        <span class="product-detail-label">Giá vốn</span>
                        <span class="product-detail-value price">${product.costPrice}</span>
                    </div>
                    <div class="product-detail-item">
                        <span class="product-detail-label">Giá bán</span>
                        <span class="product-detail-value price">${product.retailPrice}</span>
                    </div>
                    <div class="product-detail-item">
                        <span class="product-detail-label">Chiết khấu</span>
                        <span class="product-detail-value">${product.discount}</span>
                    </div>
                </div>
            `;
                                                    }

                                                    // Edit product
                                                    function editProduct(productId) {
                                                        window.location.href = '${pageContext.request.contextPath}/product?action=edit&id=' + productId;
                                                    }

                                                    // Edit product from modal
                                                    function editProductFromModal() {
                                                        if (currentProductId) {
                                                            editProduct(currentProductId);
                                                        }
                                                    }

                                                    // Delete product
                                                    function deleteProduct(productId) {
                                                        if (confirm('Bạn có chắc chắn muốn xóa sản phẩm này không?')) {
                                                            window.location.href = '${pageContext.request.contextPath}/product?action=delete&id=' + productId;
                                                        }
                                                    }

                                                    // Delete product from modal
                                                    function deleteProductFromModal() {
                                                        if (currentProductId) {
                                                            deleteProduct(currentProductId);
                                                        }
                                                    }

                                                    // Search on enter
                                                    document.addEventListener('DOMContentLoaded', function () {
                                                        const searchInput = document.querySelector('.search-input');
                                                        if (searchInput) {
                                                            searchInput.addEventListener('keypress', function (e) {
                                                                if (e.key === 'Enter') {
                                                                    this.form.submit();
                                                                }
                                                            });
                                                        }
                                                    });
        </script>

    </body>
</html>
