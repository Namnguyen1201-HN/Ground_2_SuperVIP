<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.OrderDetail" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Chi tiết đơn hàng</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
        <style>
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background-color: #f8f9fa;
                line-height: 1.6;
            }


            /* Main Content Styles */
            .main-content {
                max-width: 1200px;
                margin: 30px auto;
                padding: 0 20px;
            }

            .page-header {
                background: white;
                padding: 30px;
                border-radius: 12px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                margin-bottom: 30px;
                border-left: 5px solid #2196F3;
            }

            .page-title {
                color: #333;
                font-size: 28px;
                font-weight: 600;
                margin-bottom: 10px;
                display: flex;
                align-items: center;
                gap: 15px;
            }

            .page-subtitle {
                color: #666;
                font-size: 16px;
                display: flex;
                align-items: center;
                gap: 10px;
            }

            .back-btn {
                background: linear-gradient(135deg, #2196F3 0%, #1976D2 100%);
                color: white;
                text-decoration: none;
                padding: 12px 24px;
                border-radius: 8px;
                display: inline-flex;
                align-items: center;
                gap: 8px;
                font-weight: 500;
                transition: all 0.3s ease;
                border: none;
                cursor: pointer;
            }

            .back-btn:hover {
                transform: translateY(-2px);
                box-shadow: 0 4px 15px rgba(33, 150, 243, 0.4);
                color: white;
                text-decoration: none;
            }

            .info-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
                gap: 30px;
                margin-bottom: 30px;
            }

            .info-card {
                background: white;
                border-radius: 12px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                overflow: hidden;
                transition: transform 0.3s ease;
            }

            .info-card:hover {
                transform: translateY(-5px);
            }

            .card-header {
                background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
                padding: 20px;
                border-bottom: 2px solid #dee2e6;
                font-weight: 600;
                font-size: 18px;
                color: #333;
                display: flex;
                align-items: center;
                gap: 10px;
            }

            .card-body {
                padding: 25px;
                border-bottom: 1px solid #f8f9fa;
            }

            .info-row {
                display: flex;
                padding: 12px 0;
                border-bottom: 1px solid #f8f9fa;
                align-items: flex-start;
            }

            .info-row:last-child {
                border-bottom: none;
            }

            .info-label {
                font-weight: 600;
                color: #495057;
                min-width: 140px;
                flex-shrink: 0;
            }

            .info-value {
                color: #212529;
                flex: 1;
                word-break: break-word;
            }

            .status-badge {
                padding: 8px 16px;
                border-radius: 20px;
                font-weight: 500;
                font-size: 14px;
                display: inline-block;
            }

            .status-pending {
                background-color: #fff3cd;
                color: #856404;
            }
            .status-processing {
                background-color: #cce5ff;
                color: #004085;
            }
            .status-completed {
                background-color: #d4edda;
                color: #155724;
            }
            .status-cancelled {
                background-color: #f8d7da;
                color: #721c24;
            }

            .avatar-img, .product-img {
                border-radius: 8px;
                object-fit: cover;
                border: 2px solid #dee2e6;
            }

            .avatar-img {
                width: 60px;
                height: 60px;
                border-radius: 50%;
            }

            .product-img {
                width: 80px;
                height: 80px;
            }

            .image-placeholder {
                background-color: #f8f9fa;
                display: flex;
                align-items: center;
                justify-content: center;
                color: #6c757d;
                font-size: 24px;
            }

            .edit-section {
                background: white;
                border-radius: 12px;
                padding: 30px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                border-left: 5px solid #2196F3;
            }

            .edit-title {
                color: #333;
                font-size: 24px;
                font-weight: 600;
                margin-bottom: 25px;
                display: flex;
                align-items: center;
                gap: 10px;
            }

            .form-group {
                margin-bottom: 20px;
            }

            .form-label {
                display: block;
                margin-bottom: 8px;
                font-weight: 600;
                color: #333;
                display: flex;
                align-items: center;
                gap: 8px;
            }

            .form-control {
                width: 100%;
                padding: 12px 16px;
                border: 2px solid #e9ecef;
                border-radius: 8px;
                font-size: 16px;
                transition: border-color 0.3s ease;
            }

            .form-control:focus {
                outline: none;
                border-color: #2196F3;
                box-shadow: 0 0 0 3px rgba(33, 150, 243, 0.1);
            }

            .form-row {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 20px;
            }

            .btn-group {
                display: flex;
                gap: 15px;
                justify-content: center;
                margin-top: 30px;
            }

            .btn {
                padding: 12px 30px;
                border-radius: 8px;
                font-weight: 500;
                text-decoration: none;
                display: inline-flex;
                align-items: center;
                gap: 8px;
                transition: all 0.3s ease;
                border: none;
                cursor: pointer;
                font-size: 16px;
            }

            .btn-primary {
                background: linear-gradient(135deg, #2196F3 0%, #1976D2 100%);
                color: white;
            }

            .btn-primary:hover {
                transform: translateY(-2px);
                box-shadow: 0 4px 15px rgba(33, 150, 243, 0.4);
            }

            .btn-secondary {
                background-color: #6c757d;
                color: white;
            }

            .btn-secondary:hover {
                background-color: #5a6268;
                transform: translateY(-2px);
            }

            .badge {
                padding: 6px 12px;
                border-radius: 12px;
                font-size: 12px;
                font-weight: 500;
            }

            .badge-success {
                background-color: #d4edda;
                color: #155724;
            }
            .badge-danger {
                background-color: #f8d7da;
                color: #721c24;
            }
            .badge-info {
                background-color: #cce5ff;
                color: #004085;
            }

            .price-highlight {
                font-weight: 700;
                color: #2196F3;
                font-size: 18px;
            }

            /* Styles cho phần sản phẩm */
            .products-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
                gap: 20px;
            }

            .product-card {
                border: 2px solid #e9ecef;
                border-radius: 12px;
                padding: 20px;
                background: #f8f9fa;
                transition: all 0.3s ease;
            }

            .product-card:hover {
                border-color: #2196F3;
                box-shadow: 0 4px 15px rgba(33, 150, 243, 0.1);
            }

            .product-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 15px;
                padding-bottom: 10px;
                border-bottom: 2px solid #dee2e6;
            }

            .product-header h4 {
                color: #2196F3;
                font-weight: 600;
                margin: 0;
            }

            .product-id {
                background: #2196F3;
                color: white;
                padding: 4px 8px;
                border-radius: 6px;
                font-size: 12px;
                font-weight: 600;
            }

            .product-content {
                display: flex;
                gap: 15px;
            }

            .product-image {
                flex-shrink: 0;
            }

            .product-details {
                flex: 1;
            }

            .quantity-badge {
                background: #28a745;
                color: white;
                padding: 4px 8px;
                border-radius: 6px;
                font-weight: 600;
                font-size: 12px;
            }

            .serial-numbers {
                display: flex;
                flex-wrap: wrap;
                gap: 6px;
            }

            .serial-badge {
                background: #6c757d;
                color: white;
                padding: 2px 6px;
                border-radius: 4px;
                font-size: 11px;
                font-weight: 500;
            }

            .no-products {
                text-align: center;
                padding: 40px;
                color: #6c757d;
            }

            .no-products i {
                font-size: 48px;
                margin-bottom: 16px;
                color: #ffc107;
            }

            .alert {
                padding: 12px;
                margin-bottom: 16px;
                border-radius: 4px;
            }

            .alert-success {
                background: #d4edda;
                color: #155724;
                border: 1px solid #c3e6cb;
            }

            .alert-danger {
                background: #f8d7da;
                color: #721c24;
                border: 1px solid #f5c6cb;
            }

            /* Responsive */
            @media (max-width: 1024px) {
                .main-nav {
                    gap: 0;
                }

                .nav-item {
                    padding: 18px 15px;
                    font-size: 13px;
                }
            }

            @media (max-width: 768px) {
                .header {
                    height: auto;
                    padding: 10px 15px;
                }

                .header-container {
                    flex-direction: column;
                    gap: 15px;
                }

                .main-nav {
                    flex-wrap: wrap;
                    justify-content: center;
                    width: 100%;
                }

                .nav-item {
                    padding: 12px 15px;
                    font-size: 12px;
                }

                .logo {
                    margin-right: 0;
                }

                .info-grid {
                    grid-template-columns: 1fr;
                }

                .form-row {
                    grid-template-columns: 1fr;
                }

                .btn-group {
                    flex-direction: column;
                    align-items: center;
                }

                .page-title {
                    font-size: 24px;
                }

                .main-content {
                    padding: 0 15px;
                }

                .products-grid {
                    grid-template-columns: 1fr;
                }

                .product-content {
                    flex-direction: column;
                }

                .product-header {
                    flex-direction: column;
                    gap: 10px;
                    align-items: flex-start;
                }
            }
        </style>
    </head>
    <body class="bg-light">
        <div class="container py-3">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <a class="btn btn-outline-secondary" href="Orders">← Quay lại danh sách</a>
                <h4 class="m-0">Chi tiết đơn hàng</h4>
                <div></div>
            </div>

            <!-- Main Content -->
            <div class="main-content">
                <!-- Success/Error Messages -->
                <c:if test="${param.success == 'update'}">
                    <div class="alert alert-success">
                        <i class="fas fa-check-circle"></i>
                        Cập nhật đơn hàng thành công!
                    </div>
                </c:if>
                <c:if test="${param.error == 'update'}">
                    <div class="alert alert-danger">
                        <i class="fas fa-exclamation-triangle"></i>
                        Có lỗi xảy ra khi cập nhật đơn hàng!
                    </div>
                </c:if>
                <c:if test="${param.error == 'view'}">
                    <div class="alert alert-danger">
                        <i class="fas fa-exclamation-triangle"></i>
                        Không thể tải thông tin đơn hàng!
                    </div>
                </c:if>

                <!-- Page Header -->
                <div class="page-header">
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <div>
                            <h1 class="page-title">
                                <i class="fas fa-receipt"></i>
                                Chi Tiết Đơn Hàng #${order.orderId}
                            </h1>
                            <p class="page-subtitle">
                                <i class="fas fa-calendar-alt"></i>
                                Ngày tạo: ${order.createdAt}
                            </p>
                        </div>
                        <a href="bm-orders" class="back-btn">
                            <i class="fas fa-arrow-left"></i>Quay lại danh sách
                        </a>
                    </div>
                </div>

                <!-- Info Grid -->
                <div class="info-grid">
                    <!-- Phần 1: Danh sách sản phẩm trong đơn hàng -->
                    <div class="info-card" style="grid-column: 1 / -1;">
                        <div class="card-header">
                            <i class="fas fa-box"></i>
                            Danh Sách Sản Phẩm 
                            <c:if test="${not empty pdInfos}">
                                (${pdInfos.size()} sản phẩm)
                            </c:if>
                        </div>
                        <div class="card-body">
                            <c:choose>
                                <c:when test="${not empty pdInfos}">
                                    <div class="products-grid">
                                        <c:forEach var="product" items="${pdInfos}" varStatus="status">
                                            <div class="product-card">
                                                <div class="product-header">
                                                    <h4>Sản phẩm ${status.index + 1}</h4>
                                                    <span class="product-id">#${product.key}</span>
                                                </div>

                                                <div class="product-content">
                                                    <div class="product-details">
                                                        <div class="info-row">
                                                            <span class="info-label">Tên sản phẩm:</span>
                                                            <span class="info-value">${product.value.name}</span>
                                                        </div>
                                                        <div class="info-row">
                                                            <span class="info-label">Mã sản phẩm:</span>
                                                            <span class="info-value">
                                                                <span class="quantity-badge">${product.value.sku}</span>
                                                            </span>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="no-products">
                                        <i class="fas fa-exclamation-triangle"></i>
                                        <p>Không có sản phẩm nào trong đơn hàng này</p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <!-- Phần 2: Thông tin đơn hàng -->
                    <div class="info-card">
                        <div class="card-header">
                            <i class="fas fa-shopping-cart"></i>
                            Thông Tin Đơn Hàng
                        </div>
                        <div class="card-body">
                            <div class="info-row">
                                <span class="info-label">Mã đơn hàng:</span>
                                <span class="info-value">#${order.orderId}</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Chi nhánh:</span>
                                <span class="info-value">${branchName}</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Trạng thái:</span>
                                <span class="info-value">
                                    <c:choose>
                                        <c:when test="${order.orderStatus == 'Pending'}">
                                            <span class="status-badge status-pending">Chờ xử lý</span>
                                        </c:when>
                                        <c:when test="${order.orderStatus == 'Processing'}">
                                            <span class="status-badge status-processing">Đang xử lý</span>
                                        </c:when>
                                        <c:when test="${order.orderStatus == 'Completed'}">
                                            <span class="status-badge status-completed">Hoàn thành</span>
                                        </c:when>
                                        <c:when test="${order.orderStatus == 'Cancelled'}">
                                            <span class="status-badge status-cancelled">Đã hủy</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="status-badge status-pending">${order.orderStatus}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Ngày tạo:</span>
                                <span class="info-value">
                                    ${order.createdAt}
                                </span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Phương thức TT:</span>
                                <span class="info-value">${order.paymentMethod}</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Tổng sản phẩm:</span>
                                <span class="info-value">
                                    <span class="quantity-badge">${pdInfos.size()}</span> sản phẩm
                                </span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Ghi chú:</span>
                                <span class="info-value">
                                    <c:choose>
                                        <c:when test="${not empty order.notes}">
                                            ${order.notes}
                                        </c:when>
                                        <c:otherwise>
                                            <span style="color: #6c757d; font-style: italic;">Không có ghi chú</span>
                                        </c:otherwise>
                                    </c:choose>
                                </span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Tổng tiền:</span>
                                <span class="info-value price-highlight">
                                    ${order.grandTotal} đ
                                </span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Khách đưa:</span>
                                <span class="info-value">
                                    ${order.customerPay} đ
                                </span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Tiền thừa:</span>
                                <span class="info-value">
                                    ${order.changeAmount} đ
                                </span>
                            </div>
                        </div>
                    </div>

                    <!-- Phần 4: Thông tin khách hàng -->
                    <div class="info-card">
                        <div class="card-header">
                            <i class="fas fa-user"></i>
                            Thông Tin Khách Hàng
                        </div>
                        <div class="card-body">
                            <div class="info-row">
                                <span class="info-label">Mã KH:</span>
                                <span class="info-value">${order.customerId}</span>
                            </div>
                            <c:if test="${empty customer}">
                                ${customer} ha
                            </c:if>
                            <div class="info-row">
                                <span class="info-label">Họ tên:</span>
                                <span class="info-value">${customer.fullname}</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Điện thoại:</span>
                                <span class="info-value">${customer.phoneNumber}</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Email:</span>
                                <span class="info-value">${customer.email}</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Địa chỉ:</span>
                                <span class="info-value">${customer.address}</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Giới tính:</span>
                                <span class="info-value">
                                    <c:choose>
                                        <c:when test="${customer.gender}">Nam</c:when>
                                        <c:otherwise>Nữ</c:otherwise>
                                    </c:choose>
                                </span>
                            </div>
                        </div>
                    </div>

                    <!-- Phần 3: Thông tin nhân viên -->
                    <div class="info-card">
                        <div class="card-header">
                            <i class="fas fa-user-tie"></i>
                            Thông Tin Nhân Viên Tạo
                        </div>
                        <div class="card-body">
                            <div style="display: flex; gap: 20px; margin-bottom: 20px;">
                                <div style="flex-shrink: 0;">
                                    <c:choose>
                                        <c:when test="${not empty user_create.avaUrl}">
                                            <img src="${user_create.avaUrl}" alt="Avatar" class="avatar-img">
                                        </c:when>
                                        <c:otherwise>
                                            <div class="avatar-img image-placeholder">
                                                <i class="fas fa-user"></i>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div style="flex: 1;">
                                    <div class="info-row">
                                        <span class="info-label">Mã NV:</span>
                                        <span class="info-value">${order.createdBy}</span>
                                    </div>
                                    <div class="info-row">
                                        <span class="info-label">Họ tên:</span>
                                        <span class="info-value">${user_create.fullName}</span>
                                    </div>
                                    <div class="info-row">
                                        <span class="info-label">Chức vụ:</span>
                                        <span class="info-value">
                                            <span class="badge badge-info">${role}</span>
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>

                <script>
                    // Dropdown functionality for user menu
                    document.addEventListener('DOMContentLoaded', function () {
                        const dropdownToggle = document.getElementById('dropdownToggle');
                        const dropdownMenu = document.getElementById('dropdownMenu');

                        if (dropdownToggle && dropdownMenu) {
                            dropdownToggle.addEventListener('click', function (e) {
                                e.preventDefault();
                                const isVisible = dropdownMenu.style.opacity === '1';
                                dropdownMenu.style.opacity = isVisible ? '0' : '1';
                                dropdownMenu.style.visibility = isVisible ? 'hidden' : 'visible';
                                dropdownMenu.style.transform = isVisible ? 'translateY(-10px)' : 'translateY(0)';
                            });

                            document.addEventListener('click', function (e) {
                                if (!dropdownToggle.contains(e.target) && !dropdownMenu.contains(e.target)) {
                                    dropdownMenu.style.opacity = '0';
                                    dropdownMenu.style.visibility = 'hidden';
                                    dropdownMenu.style.transform = 'translateY(-10px)';
                                }
                            });
                        }

                        // Auto-hide alerts after 5 seconds
                        const alerts = document.querySelectorAll('.alert');
                        alerts.forEach(function (alert) {
                            setTimeout(function () {
                                alert.style.opacity = '0';
                                alert.style.transform = 'translateY(-20px)';
                                setTimeout(function () {
                                    alert.remove();
                                }, 300);
                            }, 5000);
                        });
                    });

                    // Form submission confirmation
                    document.getElementById('updateOrderForm').addEventListener('submit', function (e) {
                        const currentStatus = '${order.orderStatus}';
                        const newStatus = document.getElementById('orderStatus').value;

                        let confirmMessage = 'Bạn có chắc chắn muốn cập nhật đơn hàng này?';

                        if (currentStatus !== newStatus) {
                            if (newStatus === 'Cancelled') {
                                confirmMessage = 'Bạn có chắc chắn muốn HỦY đơn hàng này? Hành động này không thể hoàn tác!';
                            } else if (newStatus === 'Completed') {
                                confirmMessage = 'Bạn có chắc chắn đơn hàng này đã HOÀN THÀNH?';
                            }
                        }

                        if (!confirm(confirmMessage)) {
                            e.preventDefault();
                        }
                    });

                    // Smooth scroll to edit section when there's an error
                    <c:if test="${param.error == 'update'}">
                    document.querySelector('.edit-section').scrollIntoView({
                        behavior: 'smooth',
                        block: 'center'
                    });
                    </c:if>

                    // Add loading state to form submission
                    document.getElementById('updateOrderForm').addEventListener('submit', function () {
                        const submitBtn = this.querySelector('button[type="submit"]');
                        const originalText = submitBtn.innerHTML;
                        submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i>Đang cập nhật...';
                        submitBtn.disabled = true;

                        // Re-enable after 10 seconds in case of network issues
                        setTimeout(function () {
                            submitBtn.innerHTML = originalText;
                            submitBtn.disabled = false;
                        }, 10000);
                    });

                    // Add tooltips for status badges
                    document.querySelectorAll('.status-badge').forEach(function (badge) {
                        const status = badge.textContent.trim();
                        let tooltip = '';

                        switch (status) {
                            case 'Chờ xử lý':
                                tooltip = 'Đơn hàng đang chờ được xử lý';
                                break;
                            case 'Đang xử lý':
                                tooltip = 'Đơn hàng đang được xử lý';
                                break;
                            case 'Hoàn thành':
                                tooltip = 'Đơn hàng đã hoàn thành';
                                break;
                            case 'Đã hủy':
                                tooltip = 'Đơn hàng đã bị hủy';
                                break;
                        }

                        if (tooltip) {
                            badge.title = tooltip;
                        }
                    });

                    // Add click to copy functionality for order ID and serial numbers
                    document.querySelectorAll('.info-value').forEach(function (element) {
                        if (element.textContent.includes('#') || element.querySelector('.serial-badge')) {
                            element.style.cursor = 'pointer';
                            element.title = 'Click để sao chép';

                            element.addEventListener('click', function () {
                                const text = this.textContent.trim();
                                navigator.clipboard.writeText(text).then(function () {
                                    // Show temporary success message
                                    const originalTitle = element.title;
                                    element.title = 'Đã sao chép!';
                                    setTimeout(function () {
                                        element.title = originalTitle;
                                    }, 2000);
                                });
                            });
                        }
                    });
                </script>
                </body>
                </html>
