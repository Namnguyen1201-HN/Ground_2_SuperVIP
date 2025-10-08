<%-- 
    Document   : customer_requests
    Created on : Sep 29, 2025
    Author     : admin
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Yêu cầu khách hàng - SWP391</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="css/supplier.css">
</head>
<body>
    <div class="main-container">
        <main class="main-content">
            <div class="page-header">
                <h1>Yêu cầu của khách hàng</h1>
                <div class="header-actions">
                    <form action="CustomerRequests" method="get" class="search-container" style="display:flex; gap:8px; align-items:center;">
                        <i class="fas fa-user"></i>
                        <input type="number" name="userId" placeholder="User ID" class="search-input" value="${userId}" />
                        <input type="text" name="q" placeholder="Tìm tên/điện thoại/sản phẩm" class="search-input" value="${q}" />
                        <select name="status" class="search-input">
                            <option value="all" ${status == 'all' ? 'selected' : ''}>Tất cả trạng thái</option>
                            <option value="Pending" ${status == 'Pending' ? 'selected' : ''}>Đang chờ</option>
                            <option value="Approved" ${status == 'Approved' ? 'selected' : ''}>Đã duyệt</option>
                            <option value="Rejected" ${status == 'Rejected' ? 'selected' : ''}>Từ chối</option>
                        </select>
                        <input type="date" name="from" class="search-input" value="${from}" />
                        <span>đến</span>
                        <input type="date" name="to" class="search-input" value="${to}" />
                        <button type="submit" style="border: none; background: none;">
                            <i class="fas fa-search"></i>
                        </button>
                    </form>
                </div>
            </div>

            <div class="table-container">
                <table class="products-table table table-bordered">
                    <thead class="table-light">
                        <tr>
                            <th>Mã yêu cầu</th>
                            <th>Tên khách hàng</th>
                            <th>Liên hệ</th>
                            <th>Sản phẩm</th>
                            <th>Số lượng</th>
                            <th>Ngày yêu cầu</th>
                            <th>Trạng thái</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="r" items="${requests}">
                            <tr>
                                <td>${r.requestId}</td>
                                <td>${r.customerName}</td>
                                <td>${r.customerContact}</td>
                                <td>${r.productName}</td>
                                <td>${r.quantity}</td>
                                <td>${r.requestedAt}</td>
                                <td>${r.status}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>

            <div class="pagination-container mt-3 d-flex justify-content-between align-items-center">
                <div class="pagination-info">
                    Hiển thị ${startRequest} - ${endRequest} / Tổng số ${totalRequests} yêu cầu
                </div>
                <div class="pagination">
                    <a href="CustomerRequests?userId=${userId}&q=${q}&status=${status}&from=${from}&to=${to}&page=1" class="page-btn ${currentPage == 1 ? 'disabled' : ''}">
                        <i class="fas fa-angle-double-left"></i>
                    </a>
                    <a href="CustomerRequests?userId=${userId}&q=${q}&status=${status}&from=${from}&to=${to}&page=${currentPage - 1}" class="page-btn ${currentPage == 1 ? 'disabled' : ''}">
                        <i class="fas fa-angle-left"></i>
                    </a>
                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <a href="CustomerRequests?userId=${userId}&q=${q}&status=${status}&from=${from}&to=${to}&page=${i}" class="page-btn ${i == currentPage ? 'active' : ''}">${i}</a>
                    </c:forEach>
                    <a href="CustomerRequests?userId=${userId}&q=${q}&status=${status}&from=${from}&to=${to}&page=${currentPage + 1}" class="page-btn ${currentPage == totalPages ? 'disabled' : ''}">
                        <i class="fas fa-angle-right"></i>
                    </a>
                    <a href="CustomerRequests?userId=${userId}&q=${q}&status=${status}&from=${from}&to=${to}&page=${totalPages}" class="page-btn ${currentPage == totalPages ? 'disabled' : ''}">
                        <i class="fas fa-angle-double-right"></i>
                    </a>
                </div>
            </div>
        </main>
    </div>
</body>
</html>



