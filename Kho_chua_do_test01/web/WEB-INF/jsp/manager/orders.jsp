<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý Khách Hàng - SWP391</title>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap"
              rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
        <link rel="stylesheet" href="css/admin/Supplier.css">
        <style>
            body {
                background-color: #f5f7fa;
                font-family: 'Inter', sans-serif;
            }

            .main-container {
                display: flex;
                gap: 25px;
                padding: 25px;
            }

            /* 🎨 FILTER SIDEBAR */
            .filter-container {
                width: 280px;
                background: #fff;
                border-radius: 16px;
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.07);
                padding: 22px;
                transition: all 0.3s ease;
            }

            .filter-container:hover {
                box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
            }

            .filter-header {
                display: flex;
                align-items: center;
                gap: 8px;
                margin-bottom: 20px;
            }

            .filter-header i {
                color: #007bff;
                font-size: 18px;
            }

            .filter-header h2 {
                font-size: 17px;
                font-weight: 600;
                margin: 0;
                color: #333;
            }

            .filter-group {
                margin-bottom: 22px;
            }

            .filter-group h3 {
                font-size: 14px;
                font-weight: 600;
                color: #555;
                margin-bottom: 10px;
                display: flex;
                align-items: center;
                gap: 6px;
            }

            .filter-group h3 i {
                color: #007bff;
            }

            .filter-container input[type="number"],
            .filter-container select {
                width: 100%;
                padding: 9px 10px;
                border-radius: 8px;
                border: 1px solid #d0d7de;
                background-color: #fafbfc;
                font-size: 14px;
                transition: 0.2s;
            }

            .filter-container input:focus,
            .filter-container select:focus {
                outline: none;
                border-color: #007bff;
                box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.15);
                background-color: #fff;
            }

            .radio-group {
                padding-left: 4px;
            }

            .radio-group label {
                display: flex;
                align-items: center;
                gap: 6px;
                margin-bottom: 6px;
                font-size: 14px;
                cursor: pointer;
            }

            .radio-group input {
                accent-color: #007bff;
            }

            .filter-container button {
                width: 100%;
                padding: 10px 0;
                border-radius: 10px;
                border: none;
                color: white;
                font-weight: 500;
                font-size: 14px;
                cursor: pointer;
                transition: background 0.3s ease;
            }

            .btn-clear {
                background-color: #adb5bd;
                margin-bottom: 10px;
            }

            .btn-clear:hover {
                background-color: #9aa1a7;
            }

            .btn-apply {
                background-color: #007bff;
            }

            .btn-apply:hover {
                background-color: #0069d9;
            }

            /* TABLE */
            .table-container {
                flex: 1;
                background: white;
                border-radius: 16px;
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.07);
                padding: 25px;
            }

            .search-container {
                display: flex;
                justify-content: flex-end;
                margin-bottom: 15px;
                padding: 0px !important;

            }

            .search-input {
                padding: 8px 10px;
                border-radius: 8px;
                border: 1px solid #ccc;
                width: 260px;
            }

            .suppliers-table {
                width: 100%;
                border-collapse: collapse;
            }

            .suppliers-table th,
            .suppliers-table td {
                padding: 12px;
                border-bottom: 1px solid #eee;
            }

            .suppliers-table th {
                background: #f1f3f5;
                color: #333;
                font-weight: 600;
            }

            .suppliers-table tr:hover {
                background-color: #f9fafb;
            }

            .table-container {
                background: white;
                border-radius: 8px;
                box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                padding: 20px;
                margin-top: 20px;
            }

            .suppliers-table {
                width: 100%;
                border-collapse: collapse;
            }

            .suppliers-table th,
            .suppliers-table td {
                padding: 12px;
                text-align: left;
                border-bottom: 1px solid #ddd;
            }

            .suppliers-table th {
                background-color: #f2f2f2;
            }

            .search-container {
                display: flex;
                align-items: center;
                margin-bottom: 20px;
            }

            .search-input {
                padding: 10px;
                border: 1px solid #ccc;
                border-radius: 4px;
                margin-right: 10px;
                flex: 1;
            }

            .search-input:focus {
                border-color: #007bff;
                outline: none;
            }

            .pagination-container {
                margin-top: 20px;
            }

            .page-btn {
                padding: 6px 10px;
                border: 1px solid #ddd;
                border-radius: 4px;
                margin: 0 3px;
                text-decoration: none;
                color: #333;
            }

            .page-btn.active {
                background-color: #007bff;
                color: white;
                border-color: #007bff;
            }

            .page-btn.disabled {
                pointer-events: none;
                opacity: 0.5;
            }

            .nav-item.dropdown {
                position: relative;
            }

            .nav-item.dropdown .dropdown-toggle {
                display: flex;
                align-items: center;
                justify-content: space-between;
                text-decoration: none;
                color: #333;
                padding: 10px 15px;
                cursor: pointer;
            }

            .nav-item.dropdown .dropdown-menu {
                display: none;
                position: absolute;
                top: 100%;
                left: 0;
                background: white;
                border: 1px solid #ddd;
                border-radius: 6px;
                box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
                min-width: 180px;
                z-index: 1000;
            }

            .nav-item.dropdown:hover .dropdown-menu {
                display: block;
            }

            .dropdown-item {
                display: block;
                padding: 10px 15px;
                color: #333;
                text-decoration: none;
                transition: background 0.2s;
            }

            .dropdown-item:hover {
                background-color: #f2f2f2;
            }

            .dropdown-item.active {
                background-color: #007bff;
                color: white;
            }
            .btn.btn-icon.btn-sm.btn-outline-primary,
            .btn.btn-icon.btn-sm.btn-outline-danger {
                background: none;
                border: none;
                padding: 6px;
                border-radius: 8px;
                display: inline-flex;
            }

            /* 👁 Nền xanh nhạt cho icon xem */
            .btn.btn-icon.btn-sm.btn-outline-primary i {
                background-color: #e7f1ff;
                color: #0d6efd;
                padding: 6px;
                border-radius: 6px;
                transition: none;
            }

            /* 🗑 Nền đỏ nhạt cho icon xóa */
            .btn.btn-icon.btn-sm.btn-outline-danger i {
                background-color: #fde8e8;
                color: #dc3545;
                padding: 6px;
                border-radius: 6px;
                transition: none;
            }

            /* 🚫 Loại bỏ hiệu ứng hover */
            .btn.btn-icon.btn-sm.btn-outline-primary:hover i,
            .btn.btn-icon.btn-sm.btn-outline-danger:hover i {
                background-color: inherit;
                color: inherit;
                box-shadow: none;
            }
        </style>
    </head>

    <body>
        <header class="header">
            <div class="header-main">
                <div class="logo">
                    <div class="logo-icon">
                        <span class="icon-building"></span>
                    </div>
                    <span>WM</span>
                </div>
                <nav class="nav-menu">
                    <a href="TongQuan" class="nav-item">Tổng quan</a>
                    <a href="product" class="nav-item">Hàng hóa</a>
                    <div class="nav-item dropdown
                         ${fn:contains(pageContext.request.requestURI, 'Transactions') 
                           or fn:contains(pageContext.request.requestURI, 'Orders') 
                           ? 'active' : ''}">
                        <a href="#" class="dropdown-toggle">
                            <span class="icon-transactions"></span>
                            Giao dịch
                            <i class="fas fa-caret-down" style="margin-left:5px;"></i>
                        </a>
                        <div class="dropdown-menu">
                            <a href="Orders" class="dropdown-item
                               ${fn:contains(pageContext.request.requestURI, 'Orders') ? 'active' : ''}">
                                Đơn hàng
                            </a>
                        </div>
                    </div>
                    <div class="nav-item dropdown
                         ${fn:contains(pageContext.request.requestURI, 'Supplier') 
                           or fn:contains(pageContext.request.requestURI, 'Customer') 
                           ? 'active' : ''}">
                        <a href="#" class="dropdown-toggle">
                            <span class="icon-partners"></span>
                            Đối tác
                            <i class="fas fa-caret-down" style="margin-left:5px;"></i>
                        </a>
                        <div class="dropdown-menu">
                            <a href="Customer" class="dropdown-item
                               ${fn:contains(pageContext.request.requestURI, 'Customer') ? 'active' : ''}">
                                Khách hàng
                            </a>
                            <a href="Supplier" class="dropdown-item
                               ${fn:contains(pageContext.request.requestURI, 'Supplier') ? 'active' : ''}">
                                Nhà cung cấp
                            </a>
                        </div>
                    </div>
                    <a href="NhanVien" class="nav-item">Nhân viên</a>
                    <a href="#" class="nav-item">Sổ quỹ</a>
                    <a href="#" class="nav-item">Báo cáo</a>
                    <a href="#" class="nav-item">Bán Online</a>
                    <a href="#" class="nav-item">Bán hàng</a>
                </nav>
            </div>
        </header>

        <div class="main-container">
            <!-- 🎯 FILTER SIDEBAR -->
            <div class="filter-container">
                <div class="filter-header">
                    <i class="fas fa-filter"></i>
                    <h2>Bộ lọc đơn hàng</h2>
                </div>
                <form action="Orders" method="get">
                    <div class="filter-group">
                        <h3><i class="fas fa-dollar-sign"></i> Khoảng giá</h3>
                        <input type="number" name="minSpent" placeholder="Từ..." value="${param.minSpent}">
                        <input type="number" name="maxSpent" placeholder="Đến..." value="${param.maxSpent}">
                    </div>
                    <div class="filter-group">
                        <h3><i class="fas fa-calendar"></i> Thời gian</h3>

                        <h3>Từ ngày</h3>
                        <input type="date" name="fromDate" class="form-control" value="${param.fromDate}">

                        <h3>Đến ngày</h3>
                        <input type="date" name="toDate" class="form-control" value="${param.toDate}">
                    </div>
                    <c:if test="${sessionScope.getCurrentUser.roleId == 0}">
                        <div class="filter-group">
                            <h3><i class="fas fa-store"></i> Chi nhánh</h3>
                            <select name="branchId">
                                <option value="0">-- Tất cả chi nhánh --</option>
                                <c:forEach var="b" items="${branches}">
                                    <option value="${b.branchId}" ${param.branchId == b.branchId ? 'selected' : ''}>
                                        ${b.branchName}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </c:if>

                    <button type="submit" class="btn-clear" name="action" value="clear">
                        <i class="fas fa-rotate-left"></i> Xóa bộ lọc
                    </button>
                    <button type="submit" class="btn-apply" name="action" value="filter">
                        <i class="fas fa-check"></i> Áp dụng lọc
                    </button>
                </form>
            </div>

            <!-- TABLE -->
            <div class="table-container">
                <div class="search-container">
                    <form action="Orders" method="get">
                        <input type="text" name="q" class="search-input" placeholder="Theo mã, ghi chú, thanh toán..."
                               value="${param.q}">
                        <button type="submit" style="background:none; border:none; cursor:pointer;">
                            <i class="fas fa-search"></i>
                        </button>
                    </form>
                </div>

                <div class="table-responsive">
                    <table class="table table-hover align-middle bg-white" id="ordersTable"
                           style="box-shadow:0 4px 24px rgba(59,130,246,0.07); border-radius:1rem; overflow:hidden;">
                        <thead class="table-light">
                            <tr>
                                <th>ID</th>
                                <th>Chi nhánh</th>
                                <th>Người tạo</th>
                                <th>Trạng thái</th>
                                <th>Ngày tạo</th>
                                <th>Thanh toán</th>
                                <th>Tổng</th>
                                    <c:if test="${sessionScope.getCurrentUser.roleId == 0}">
                                    <th style="width:220px">Cập nhật trạng thái</th>
                                    </c:if>
                                    <c:if test="${sessionScope.getCurrentUser.roleId != 0}">
                                    <th style="width:220px">Thao tác</th>
                                    </c:if>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="o" items="${orders}">
                                <tr>
                                    <td>${o.orderId}</td>
                                    <td>
                                        <c:choose>
                                            <c:when
                                                test="${o.branchId != null && branchNames[o.branchId] != null}">
                                                ${branchNames[o.branchId]}</c:when>
                                            <c:when test="${o.branchId != null}">#${o.branchId}</c:when>
                                            <c:otherwise>-</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>${creatorNames[o.createdBy] != null ? creatorNames[o.createdBy] : ('User
                                          #' += o.createdBy)}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${o.orderStatus eq 'Completed'}"><span
                                                    class="badge bg-success">${o.orderStatus}</span></c:when>
                                            <c:when test="${o.orderStatus eq 'Cancelled'}"><span
                                                    class="badge bg-danger">${o.orderStatus}</span></c:when>
                                            <c:otherwise><span
                                                    class="badge bg-secondary">${o.orderStatus}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>${o.createdAt}</td>
                                    <td>${empty o.paymentMethod ? '-' : o.paymentMethod}</td>
                                    <td>${empty o.grandTotal ? 0 : o.grandTotal}</td>
                                    <c:if test="${sessionScope.getCurrentUser.roleId == 0}">
                                        <td>
                                            <form class="d-flex gap-2 align-items-center" method="post"
                                                  action="Orders">
                                                <input type="hidden" name="action" value="updateStatus" />
                                                <input type="hidden" name="orderId" value="${o.orderId}" />
                                                <c:set var="locked"
                                                       value="${o.orderStatus eq 'Completed' or o.orderStatus eq 'Cancelled'}" />
                                                <select class="form-select form-select-sm" name="status"
                                                        style="max-width:140px;" ${locked ? 'disabled' : '' }>
                                                    <option ${o.orderStatus eq 'Pending' ? 'selected' : '' }>Pending
                                                    </option>
                                                    <option ${o.orderStatus eq 'Completed' ? 'selected' : '' }>
                                                        Completed</option>
                                                    <option ${o.orderStatus eq 'Cancelled' ? 'selected' : '' }>
                                                        Cancelled</option>
                                                </select>
                                                <button class="btn btn-sm btn-primary" ${locked ? 'disabled' : ''
                                                        }>Lưu</button>
                                                <a class="btn btn-sm btn-outline-secondary"
                                                   href="Orders?action=detail&id=${o.orderId}">Xem</a>
                                            </form>
                                        </td>
                                    </c:if>
                                    <c:if test="${sessionScope.getCurrentUser.roleId != 0}">
                                        <td class="" style="background: none; hover: none">
                                            <a href="Orders?action=detail&id=${o.orderId}" 
                                               class="btn btn-icon btn-sm btn-outline-primary" 
                                               title="Xem chi tiết">
                                                <i class="fa-solid fa-eye"></i>
                                                <form method="post" action="Orders" 
                                                      onsubmit="return confirm('Bạn có chắc muốn xóa đơn hàng này không?');"
                                                      style="display:inline;">
                                                    <input type="hidden" name="action" value="delete" />
                                                    <input type="hidden" name="orderId" value="${o.orderId}" />
                                                    <button type="submit" 
                                                            class="btn btn-icon btn-sm btn-outline-danger" 
                                                            title="Xóa đơn hàng">
                                                        <i class="fa-solid fa-trash"></i>
                                                    </button>
                                                </form>
                                        </td>
                                    </c:if>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    <c:set var="query">
                        branch=${param.branch}&st=${param.st}&q=${param.q}
                        &customer=${param.customer}&product=${param.product}
                        &fromDate=${param.fromDate}&toDate=${param.toDate}
                    </c:set>

                    <ul class="pagination">
                        <c:forEach begin="1" end="${(total / pageSize) + (total % pageSize > 0 ? 1 : 0)}"
                                   var="i">
                            <li class="page-item ${i == page ? 'active' : ''}">
                                <a class="page-link" href="Orders?page=${i}&${query}">${i}</a>
                            </li>
                        </c:forEach>
                    </ul>
                </div>
            </div>

    </body>

</html>