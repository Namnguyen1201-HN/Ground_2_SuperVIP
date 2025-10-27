<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TSMS - Danh sách phiếu nhập/xuất kho</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/stock-movements.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
</head>
<body>
    <!-- Include Header -->
    <%@ include file="header_admin.jsp" %>

    <div class="container">
        <div class="page-header">
            <h1>📦 Quản lý phiếu nhập/xuất kho</h1>
            <div class="header-actions">
                <c:choose>
                    <c:when test="${isBranchManager}">
                        <a href="${pageContext.request.contextPath}/import-request" class="btn btn-primary">
                            ➕ Tạo phiếu xuất
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/import-request" class="btn btn-primary">
                            ➕ Tạo phiếu nhập
                        </a>
                    </c:otherwise>
                </c:choose>
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

        <!-- Filter Section (Admin only) -->
        <c:if test="${not isBranchManager}">
            <div class="filter-section">
                <form method="get" action="${pageContext.request.contextPath}/stock-movements" class="filter-form">
                    <div class="form-group">
                        <label for="type">Loại phiếu:</label>
                        <select name="type" id="type" onchange="this.form.submit()">
                            <option value="">-- Tất cả --</option>
                            <option value="Import" ${movementType eq 'Import' ? 'selected' : ''}>📥 Nhập kho</option>
                            <option value="Export" ${movementType eq 'Export' ? 'selected' : ''}>📤 Xuất kho</option>
                        </select>
                    </div>
                </form>
            </div>
        </c:if>

        <!-- Statistics -->
        <div class="stats">
            <div class="stat-card">
                <div class="stat-value">${totalItems}</div>
                <div class="stat-label">Tổng số phiếu</div>
            </div>
        </div>

        <!-- Movements List -->
        <div class="movements-list">
            <c:choose>
                <c:when test="${empty movements}">
                    <div class="empty-state">
                        <p>📋 Chưa có phiếu nào</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <table class="movements-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Loại</th>
                                <th>Từ</th>
                                <th>Đến</th>
                                <th>Người tạo</th>
                                <th>Ngày tạo</th>
                                <th>Ghi chú</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="movement" items="${movements}">
                                <tr>
                                    <td>#${movement.movementId}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${movement.movementType eq 'Import'}">
                                                <span class="badge badge-import">📥 Nhập kho</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-export">📤 Xuất kho</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty movement.fromSupplierName}">
                                                🏭 ${movement.fromSupplierName}
                                            </c:when>
                                            <c:when test="${not empty movement.fromWarehouseName}">
                                                🏢 ${movement.fromWarehouseName}
                                            </c:when>
                                            <c:when test="${not empty movement.fromBranchName}">
                                                🏪 ${movement.fromBranchName}
                                            </c:when>
                                            <c:otherwise>
                                                --
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty movement.toWarehouseName}">
                                                🏢 ${movement.toWarehouseName}
                                            </c:when>
                                            <c:when test="${not empty movement.toBranchName}">
                                                🏪 ${movement.toBranchName}
                                            </c:when>
                                            <c:otherwise>
                                                --
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty movement.creatorName}">
                                                👤 ${movement.creatorName}
                                            </c:when>
                                            <c:otherwise>
                                                User #${movement.createdBy}
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <fmt:formatDate value="${movement.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty movement.note}">
                                                ${movement.note}
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">--</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/stock-movements?action=view-details&id=${movement.movementId}" 
                                           class="btn-view" title="Xem chi tiết">
                                            👁️ Chi tiết
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Pagination -->
        <c:if test="${totalPages > 1}">
            <div class="pagination">
                <c:if test="${currentPage > 1}">
                    <a href="${pageContext.request.contextPath}/stock-movements?page=1&type=${movementType}" class="page-btn">
                        ⏮️ Đầu
                    </a>
                    <a href="${pageContext.request.contextPath}/stock-movements?page=${currentPage - 1}&type=${movementType}" class="page-btn">
                        ⬅️ Trước
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
                            <a href="${pageContext.request.contextPath}/stock-movements?page=${i}&type=${movementType}" class="page-btn">${i}</a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>

                <c:if test="${currentPage < totalPages}">
                    <a href="${pageContext.request.contextPath}/stock-movements?page=${currentPage + 1}&type=${movementType}" class="page-btn">
                        Sau ➡️
                    </a>
                    <a href="${pageContext.request.contextPath}/stock-movements?page=${totalPages}&type=${movementType}" class="page-btn">
                        Cuối ⏭️
                    </a>
                </c:if>
            </div>
        </c:if>
    </div>
</body>
</html>
