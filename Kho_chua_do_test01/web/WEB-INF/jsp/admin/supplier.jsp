<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý Nhà Cung Cấp - SWP391</title>
        <link rel="stylesheet" href="css/admin/Supplier.css">
        <style>
            body {
                padding-top: 70px;
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
            .suppliers-table th, .suppliers-table td {
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
                padding: 0px !important
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

        </style>
    </head>
    <body>
        <%@ include file="header_admin.jsp" %>

        <div class="main-container">
            <!-- Main Content -->
            <main class="main-content">
                <div class="page-header">
                    <h1>Nhà Cung Cấp</h1>
                    <div class="header-actions">
                        <div class="search-container">
                            <form action="Supplier" method="post" id="searchForm">
                                <input type="hidden" name="action" value="search">
                                <input type="text" name="keyword" id="searchInput"
                                       value="${keyword}"
                                       placeholder="Tìm kiếm theo mã, tên"
                                       class="search-input" />
                                <button type="submit" class="search-btn" style="background:none; border:none; cursor:pointer;">
                                    <i class="fas fa-search"></i>
                                </button>
                            </form>
                        </div>
                    </div>
                </div>

                <!-- Suppliers Table -->
                <div class="table-container">
                    <table class="suppliers-table" id="suppliersTable">
                        <thead>
                            <tr>
                                <th>Mã Nhà Cung Cấp</th>
                                <th>Tên Công Ty</th>
                                <th>Người Giao Dịch</th>
                                <th>Số Điện Thoại</th>
                                <th>Gmail</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty suppliers}">
                                    <tr>
                                        <td colspan="5" style="text-align:center; color:#888;">
                                            Không tìm thấy nhà cung cấp phù hợp.
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="s" items="${suppliers}">
                                        <tr>
                                            <td>${s.supplierId}</td>
                                            <td>${s.supplierName}</td>
                                            <td>${s.contactName}</td>
                                            <td>${s.phone}</td>
                                            <td>${s.email}</td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>

                <!-- Pagination -->
                <div class="pagination-container mt-3 d-flex justify-content-between align-items-center">
                    <div class="pagination-info">
                        Hiển thị ${startSupplier} - ${endSupplier} / Tổng số ${totalSuppliers} Nhà Cung Cấp
                    </div>
                    <div class="pagination">
                        <c:choose>
                            <c:when test="${not empty keyword}">
                                <div class="pagination">
                                    <a href="Supplier?action=search&page=1&keyword=${keyword}" class="page-btn ${currentPage == 1 ? 'disabled' : ''}">
                                        <i class="fas fa-angle-double-left"></i>
                                    </a>
                                    <a href="Supplier?action=search&page=${currentPage - 1}&keyword=${keyword}" class="page-btn ${currentPage == 1 ? 'disabled' : ''}">
                                        <i class="fas fa-angle-left"></i>
                                    </a>
                                    <c:forEach begin="1" end="${totalPages}" var="i">
                                        <a href="Supplier?action=search&page=${i}&keyword=${keyword}" class="page-btn ${i == currentPage ? 'active' : ''}">${i}</a>
                                    </c:forEach>
                                    <a href="Supplier?action=search&page=${currentPage + 1}&keyword=${keyword}" class="page-btn ${currentPage == totalPages ? 'disabled' : ''}">
                                        <i class="fas fa-angle-right"></i>
                                    </a>
                                    <a href="Supplier?action=search&page=${totalPages}&keyword=${keyword}" class="page-btn ${currentPage == totalPages ? 'disabled' : ''}">
                                        <i class="fas fa-angle-double-right"></i>
                                    </a>
                                </div>
                            </c:when>

                            <c:otherwise>
                                <div class="pagination">
                                    <a href="Supplier?page=1" class="page-btn ${currentPage == 1 ? 'disabled' : ''}">
                                        <i class="fas fa-angle-double-left"></i>
                                    </a>
                                    <a href="Supplier?page=${currentPage - 1}" class="page-btn ${currentPage == 1 ? 'disabled' : ''}">
                                        <i class="fas fa-angle-left"></i>
                                    </a>
                                    <c:forEach begin="1" end="${totalPages}" var="i">
                                        <a href="Supplier?page=${i}" class="page-btn ${i == currentPage ? 'active' : ''}">${i}</a>
                                    </c:forEach>
                                    <a href="Supplier?page=${currentPage + 1}" class="page-btn ${currentPage == totalPages ? 'disabled' : ''}">
                                        <i class="fas fa-angle-right"></i>
                                    </a>
                                    <a href="Supplier?page=${totalPages}" class="page-btn ${currentPage == totalPages ? 'disabled' : ''}">
                                        <i class="fas fa-angle-double-right"></i>
                                    </a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </main>
        </div>
    </body>
</html>
