<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý Nhà Cung Cấp - SWP391</title>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="css/admin/Supplier.css">
        <style>
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
                box-shadow: 0 2px 8px rgba(0,0,0,0.1);
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
        <!-- Header -->
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
