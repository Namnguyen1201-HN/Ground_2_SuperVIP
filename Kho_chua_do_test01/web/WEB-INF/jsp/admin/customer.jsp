<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý Khách Hàng - SWP391</title>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
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
                box-shadow: 0 4px 12px rgba(0,0,0,0.07);
                padding: 22px;
                transition: all 0.3s ease;
            }
            .filter-container:hover {
                box-shadow: 0 6px 16px rgba(0,0,0,0.1);
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
                box-shadow: 0 0 0 3px rgba(0,123,255,0.15);
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
                box-shadow: 0 4px 12px rgba(0,0,0,0.07);
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
            .suppliers-table th, .suppliers-table td {
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
                    <h2>Bộ lọc khách hàng</h2>
                </div>
                <form action="Customer" method="get">
                    <div class="filter-group">
                        <h3><i class="fas fa-dollar-sign"></i> Khoảng chi tiêu</h3>
                        <input type="number" name="minSpent" placeholder="Từ..." value="${param.minSpent}">
                        <input type="number" name="maxSpent" placeholder="Đến..." value="${param.maxSpent}">
                    </div>

                    <div class="filter-group">
                        <h3><i class="fas fa-venus-mars"></i> Giới tính</h3>
                        <div class="radio-group">
                            <label><input type="radio" name="gender" value="all" ${param.gender == 'all' || empty param.gender ? 'checked' : ''}> Tất cả</label>
                            <label><input type="radio" name="gender" value="male" ${param.gender == 'male' ? 'checked' : ''}> Nam</label>
                            <label><input type="radio" name="gender" value="female" ${param.gender == 'female' ? 'checked' : ''}> Nữ</label>
                        </div>
                    </div>

                    <div class="filter-group">
                        <h3><i class="fas fa-store"></i> Chi nhánh</h3>
                        <select name="branchId">
                            <option value="0">-- Tất cả chi nhánh --</option>
                            <c:forEach var="b" items="${branches}">
                                <option value="${b.branchId}" ${param.branchId == b.branchId ? 'selected' : ''}>${b.branchName}</option>
                            </c:forEach>
                        </select>
                    </div>

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
                    <form action="Customer" method="get">
                        <input type="text" name="keyword" class="search-input" placeholder="Theo mã, tên khách hàng..." value="${param.keyword}">
                        <button type="submit" style="background:none; border:none; cursor:pointer;">
                            <i class="fas fa-search"></i>
                        </button>
                    </form>
                </div>

                <table class="suppliers-table">
                    <thead>
                        <tr>
                            <th>Mã KH</th>
                            <th>Tên khách hàng</th>
                            <th>Số điện thoại</th>
                            <th>Địa chỉ</th>
                            <th>Tổng chi tiêu</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty customers}">
                                <tr><td colspan="5" style="text-align:center; color:#888;">Không tìm thấy khách hàng phù hợp.</td></tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="c" items="${customers}">
                                    <tr>
                                        <td>${c.customerID}</td>
                                        <td>${c.fullname}</td>
                                        <td>${c.phoneNumber}</td>
                                        <td>${c.address}</td>
                                        <td>${c.totalSpent}</td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </body>
</html>
