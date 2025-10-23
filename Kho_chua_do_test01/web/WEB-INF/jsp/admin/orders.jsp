<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý Đơn hàng</title>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap"
              rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
        <link rel="stylesheet" href="css/admin/Supplier.css">
        <style>
            /* Đã thay thế .main-container bằng .admin-content-wrapper */
            body {
                background-color: #f5f7fa;
                font-family: 'Inter', sans-serif;
                overflow-x: hidden; /* NGĂN THANH CUỘN NGANG CỦA BODY */
                padding-top: 70px; /* Đẩy nội dung xuống để không bị che bởi fixed header */
            }

            .admin-content-wrapper { /* CLASS MỚI */
                display: flex;
                gap: 15px;
                /* Điều chỉnh padding để mở rộng sát mép */
                padding: 25px 15px 25px 25px;
            }

            /* 🎨 FILTER SIDEBAR */
            .filter-container {
                /* Tăng độ rộng sidebar */
                width: 300px;
                background: #fff;
                border-radius: 16px;
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.07);
                padding: 22px;
                transition: all 0.3s ease;
                flex-shrink: 0;
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
            .filter-container select,
            .filter-container input[type="date"] {
                width: 100%;
                padding: 9px 10px;
                border-radius: 8px;
                border: 1px solid #d0d7de;
                background-color: #fafbfc;
                font-size: 14px;
                transition: 0.2s;
                margin-bottom: 10px;
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

            /* Filter Action Buttons Container */
            .filter-actions {
                display: flex;
                gap: 10px;
                margin-top: 20px;
                width: 100%;
            }

            /* Base button styles */
            .filter-container button,
            .filter-container a.btn-clear {
                flex: 1;
                padding: 11px 16px;
                border-radius: 10px;
                border: none;
                color: white;
                font-weight: 600;
                font-size: 14px;
                cursor: pointer;
                transition: all 0.3s ease;
                display: inline-flex;
                align-items: center;
                justify-content: center;
                gap: 8px;
                text-decoration: none;
                box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
            }

            .filter-container button:active,
            .filter-container a.btn-clear:active {
                transform: translateY(1px);
                box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
            }

            /* Clear Filter Button */
            .btn-clear {
                background: linear-gradient(135deg, #6c757d 0%, #5a6268 100%);
            }

            .btn-clear:hover {
                background: linear-gradient(135deg, #5a6268 0%, #4e555b 100%);
                color: white;
                transform: translateY(-1px);
                box-shadow: 0 4px 12px rgba(108, 117, 125, 0.3);
            }

            .btn-clear i {
                font-size: 13px;
            }

            /* Apply Filter Button */
            .btn-apply {
                background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
            }

            .btn-apply:hover {
                background: linear-gradient(135deg, #0069d9 0%, #004494 100%);
                transform: translateY(-1px);
                box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.15);
            }

            .btn-apply i {
                font-size: 13px;
            }

            /* TABLE */
            .table-container {
                flex: 1;
                /* Cho phép chiếm hết không gian còn lại */
                background: white;
                border-radius: 16px;
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.07);
                padding: 25px;
                min-width: 0; /* Ngăn container bị tràn do nội dung con */
            }

            .search-container {
                display: flex;
                justify-content: flex-end;
                margin-bottom: 15px;
                /* Đảm bảo không có padding ngang dư thừa */
                padding: 0px !important;
            }

            .search-container form {
                display: flex;
                align-items: center;
                gap: 5px;
            }

            .search-input {
                padding: 8px 10px;
                border-radius: 8px;
                border: 1px solid #ccc;
                width: 260px;
                flex-grow: 1;
            }

            /* Vùng chứa bảng để tạo thanh cuộn ngang khi cần */
            .table-responsive {
                overflow-x: auto; /* ĐẢM BẢO THANH CUỘN NẰM TRONG ĐÂY */
                width: 100%;
            }

            .suppliers-table {
                min-width: max-content;
                width: 100%;
                border-collapse: collapse;
                table-layout: auto;
            }

            .suppliers-table th,
            .suppliers-table td {
                padding: 14px 12px;
                border-bottom: 1px solid #eee;
                vertical-align: middle;
                white-space: nowrap;
            }

            .suppliers-table th {
                background: #f8f9fa;
                color: #333;
                font-weight: 600;
                font-size: 13px;
                text-transform: uppercase;
                letter-spacing: 0.5px;
                border-bottom: 2px solid #dee2e6;
            }

            .suppliers-table tr:hover {
                background-color: #f8f9ff;
                transition: background-color 0.2s ease;
            }

            .table > tbody > tr > td {
                vertical-align: middle;
            }

            /* Điều chỉnh độ rộng cột tối ưu */
            .suppliers-table th:nth-child(1), /* ID */
            .suppliers-table td:nth-child(1) {
                min-width: 60px;
            }

            .suppliers-table th:nth-child(2), /* Chi nhánh */
            .suppliers-table td:nth-child(2) {
                min-width: 180px;
            }

            .suppliers-table th:nth-child(3), /* Người tạo */
            .suppliers-table td:nth-child(3) {
                min-width: 140px;
            }

            .suppliers-table th:nth-child(4), /* Trạng thái */
            .suppliers-table td:nth-child(4) {
                min-width: 120px;
            }

            .suppliers-table th:nth-child(5), /* Ngày tạo */
            .suppliers-table td:nth-child(5) {
                min-width: 140px;
            }

            .suppliers-table th:nth-child(6), /* Thanh toán */
            .suppliers-table td:nth-child(6) {
                min-width: 110px;
            }

            .suppliers-table th:nth-child(7), /* Tổng tiền */
            .suppliers-table td:nth-child(7) {
                min-width: 120px;
            }

            .suppliers-table th:last-child, /* Hành động */
            .suppliers-table td:last-child {
                min-width: 230px;
                text-align: center;
                white-space: normal;
            }

            /* Status Update Form & Select - Quan trọng cho cột Hành động */
            .status-update-form {
                display: flex;
                gap: 6px;
                align-items: center;
                justify-content: center;
                flex-wrap: wrap; /* Cho phép form wrap nếu không đủ 230px */
            }

            /* Status Badge Styles */
            .badge {
                display: inline-flex;
                align-items: center;
                justify-content: center;
                gap: 6px;
                padding: 6px 12px;
                border-radius: 6px;
                font-size: 12px;
                font-weight: 600;
                min-width: 110px;
                white-space: nowrap;
            }

            .badge i {
                font-size: 11px;
            }

            .bg-success {
                background-color: #28a745 !important;
                color: white !important;
            }

            .bg-danger {
                background-color: #dc3545 !important;
                color: white !important;
            }

            .bg-info {
                background-color: #17a2b8 !important;
                color: white !important;
            }

            .bg-warning {
                background-color: #ffc107 !important;
                color: #212529 !important;
            }

            .bg-secondary {
                background-color: #6c757d !important;
                color: white !important;
            }

            .text-dark {
                color: #212529 !important;
            }

            .status-select {
                padding: 5px 8px;
                border: 1px solid #ddd;
                border-radius: 6px;
                font-size: 11px;
                font-weight: 500;
                min-width: 100px;
                max-width: 110px;
                background: white;
                cursor: pointer;
                transition: all 0.2s ease;
            }

            .status-select:focus {
                outline: none;
                border-color: #007bff;
                box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.15);
            }

            .status-select:disabled {
                background: #e9ecef;
                cursor: not-allowed;
                opacity: 0.6;
            }

            .btn-save {
                padding: 5px 10px;
                font-size: 11px;
                border-radius: 5px;
                border: none;
                background: #28a745;
                color: white;
                cursor: pointer;
                transition: all 0.2s ease;
                white-space: nowrap;
            }

            .btn-save:hover:not(:disabled) {
                background: #218838;
                transform: translateY(-1px);
                box-shadow: 0 2px 6px rgba(40, 167, 69, 0.3);
            }

            .btn-save:disabled {
                background: #6c757d;
                cursor: not-allowed;
                opacity: 0.5;
            }

            .btn-view {
                padding: 5px 10px;
                font-size: 11px;
                border-radius: 5px;
                border: 1px solid #17a2b8;
                background: white;
                color: #17a2b8;
                cursor: pointer;
                text-decoration: none;
                transition: all 0.2s ease;
                white-space: nowrap;
            }

            .btn-view:hover {
                background: #17a2b8;
                color: white;
                transform: translateY(-1px);
                box-shadow: 0 2px 6px rgba(23, 162, 184, 0.3);
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
        </style>
    </head>

    <body>
        <!-- Include Header -->
        <%@ include file="header_admin.jsp" %>

        <div class="admin-content-wrapper"> <div class="filter-container">
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

                    <div class="filter-group">
                        <h3><i class="fas fa-store"></i> Chi nhánh</h3>
                        <c:choose>
                            <c:when test="${currentUserRole == 0}">
                                <select name="branchId">
                                    <option value="0">-- Tất cả chi nhánh --</option>
                                    <c:forEach var="b" items="${allBranches}">
                                        <option value="${b.branchId}" ${param.branchId==b.branchId ? 'selected' : '' }>
                                            ${b.branchName}</option>
                                        </c:forEach>
                                </select>
                            </c:when>
                            <c:otherwise>
                                <select name="branchId" disabled>
                                    <c:forEach var="b" items="${allBranches}">
                                        <c:if test="${b.branchId == currentUserBranch}">
                                            <option value="${b.branchId}" selected>
                                                ${b.branchName}</option>
                                            </c:if>
                                        </c:forEach>
                                </select>
                                <small class="text-muted">Bạn chỉ xem được đơn hàng của chi nhánh mình</small>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="filter-actions">
                        <a href="${pageContext.request.contextPath}/Orders" class="btn-clear">
                            <i class="fas fa-rotate-left"></i> Xóa lọc
                        </a>
                        <button type="submit" class="btn-apply" name="action" value="filter">
                            <i class="fas fa-check"></i> Áp dụng
                        </button>
                    </div>
                </form>
            </div>

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
                                <th style="min-width:60px; text-align:center;">ID</th>
                                <th style="min-width:180px;">Chi nhánh</th>
                                <th style="min-width:140px;">Người tạo</th>
                                <th style="min-width:120px; text-align:center;">Trạng thái</th>
                                <th style="min-width:140px;">Ngày tạo</th>
                                <th style="min-width:110px; text-align:center;">Thanh toán</th>
                                <th style="min-width:120px; text-align:right;">Tổng tiền</th>
                                <th style="min-width:230px; text-align:center;">Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="o" items="${orders}">
                                <tr style="vertical-align: middle;">
                                    <td style="text-align:center; font-weight:600;">
                                        <span style="color:#007bff;">#${o.orderId}</span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${o.branchId != null && branchNames[o.branchId] != null}">
                                                <div style="font-weight:500; color:#333;">
                                                    <i class="fas fa-store" style="color:#6c757d; margin-right:6px; font-size:13px;"></i>
                                                    ${branchNames[o.branchId]}
                                                </div>
                                            </c:when>
                                            <c:when test="${o.branchId != null}">
                                                <span style="color:#999;">#${o.branchId}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color:#999;">-</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div style="font-weight:500; color:#555;">
                                            <i class="fas fa-user" style="color:#6c757d; margin-right:6px; font-size:12px;"></i>
                                            ${creatorNames[o.createdBy] != null ? creatorNames[o.createdBy] : 'User #'.concat(o.createdBy)}
                                        </div>
                                    </td>
                                    <td style="text-align:center;">
                                        <c:choose>
                                            <c:when test="${o.orderStatus eq 'Completed'}">
                                                <span class="badge bg-success" style="font-size:12px; padding:6px 12px; min-width:90px;">
                                                    <i class="fas fa-check-circle"></i> Completed
                                                </span>
                                            </c:when>
                                            <c:when test="${o.orderStatus eq 'Cancelled'}">
                                                <span class="badge bg-danger" style="font-size:12px; padding:6px 12px; min-width:90px;">
                                                    <i class="fas fa-times-circle"></i> Cancelled
                                                </span>
                                            </c:when>
                                            <c:when test="${o.orderStatus eq 'Processing'}">
                                                <span class="badge bg-info text-dark" style="font-size:12px; padding:6px 12px; min-width:90px;">
                                                    <i class="fas fa-spinner"></i> Processing
                                                </span>
                                            </c:when>
                                            <c:when test="${o.orderStatus eq 'Pending'}">
                                                <span class="badge bg-warning text-dark" style="font-size:12px; padding:6px 12px; min-width:90px;">
                                                    <i class="fas fa-clock"></i> Pending
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-secondary" style="font-size:12px; padding:6px 12px; min-width:90px;">
                                                    ${o.orderStatus}
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div style="font-size:13px; color:#666;">
                                            <i class="far fa-calendar-alt" style="margin-right:6px; color:#6c757d;"></i>
                                            <fmt:formatDate value="${o.createdAt}" pattern="dd/MM/yyyy" /><br>
                                            <small style="color:#999; margin-left:20px;">
                                                <fmt:formatDate value="${o.createdAt}" pattern="HH:mm" />
                                            </small>
                                        </div>
                                    </td>
                                    <td style="text-align:center;">
                                        <c:choose>
                                            <c:when test="${o.paymentMethod eq 'Cash'}">
                                                <span class="badge bg-light text-dark" style="font-size:11px;">
                                                    <i class="fas fa-money-bill-wave"></i> Tiền mặt
                                                </span>
                                            </c:when>
                                            <c:when test="${o.paymentMethod eq 'Bank Transfer'}">
                                                <span class="badge bg-light text-dark" style="font-size:11px;">
                                                    <i class="fas fa-university"></i> Chuyển khoản
                                                </span>
                                            </c:when>
                                            <c:when test="${o.paymentMethod eq 'Credit Card'}">
                                                <span class="badge bg-light text-dark" style="font-size:11px;">
                                                    <i class="fas fa-credit-card"></i> Thẻ
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color:#999;">-</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td style="text-align:right;">
                                        <div style="font-weight:600; color:#28a745; font-size:14px;">
                                            <fmt:formatNumber value="${o.grandTotal}" type="number" groupingUsed="true" /> ₫
                                        </div>
                                    </td>
                                    <td>
                                        <form class="status-update-form" method="post" action="Orders">
                                            <input type="hidden" name="action" value="updateStatus" />
                                            <input type="hidden" name="orderId" value="${o.orderId}" />
                                            <c:set var="locked" value="${o.orderStatus eq 'Completed' or o.orderStatus eq 'Cancelled'}" />
                                            <select class="status-select" name="status" ${locked ? 'disabled' : ''}>
                                                <option value="Pending" ${o.orderStatus eq 'Pending' ? 'selected' : ''}>Pending</option>
                                                <option value="Processing" ${o.orderStatus eq 'Processing' ? 'selected' : ''}>Processing</option>
                                                <option value="Completed" ${o.orderStatus eq 'Completed' ? 'selected' : ''}>Completed</option>
                                                <option value="Cancelled" ${o.orderStatus eq 'Cancelled' ? 'selected' : ''}>Cancelled</option>
                                            </select>
                                            <button class="btn-save" type="submit" ${locked ? 'disabled' : ''}>
                                                <i class="fas fa-save"></i> Lưu
                                            </button>
                                            <a class="btn-view" href="Orders?action=detail&id=${o.orderId}">
                                                <i class="fas fa-eye"></i> Xem
                                            </a>
                                        </form>
                                    </td>
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
        </div>
    </body>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <script>
        // Handle messages
        const msg = '${msg}';
        const error = '${error}';

        if (msg === 'status-updated') {
            Swal.fire({
                icon: 'success',
                title: 'Đã cập nhật trạng thái đơn hàng',
                timer: 2000,
                showConfirmButton: false
            });
        } else if (msg === 'deleted') {
            Swal.fire({
                icon: 'success',
                title: 'Đã xóa đơn hàng',
                timer: 2000,
                showConfirmButton: false
            });
        } else if (msg === 'created') {
            Swal.fire({
                icon: 'success',
                title: 'Đã tạo đơn hàng mới',
                timer: 2000,
                showConfirmButton: false
            });
        }

        if (error === 'no_permission') {
            Swal.fire({
                icon: 'error',
                title: 'Không có quyền truy cập',
                text: 'Bạn không có quyền xem hoặc chỉnh sửa đơn hàng này',
                confirmButtonText: 'OK'
            });
        } else if (error === 'order_not_found') {
            Swal.fire({
                icon: 'error',
                title: 'Không tìm thấy đơn hàng',
                timer: 2000,
                showConfirmButton: false
            });
        }
    </script>

</html>