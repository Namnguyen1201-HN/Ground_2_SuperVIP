<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Khuyến Mãi - VIP Store</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f5f5f5;
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            margin: 0;
            padding: 0;
        }
        
        .main-container {
            padding-top: 85px;
            min-height: 100vh;
        }
        
        .sidebar {
            background: white;
            border-radius: 8px;
            padding: 20px;
            height: fit-content;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
            position: sticky;
            top: 100px;
        }
        
        .sidebar h6 {
            font-weight: 600;
            color: #374151;
            margin-bottom: 16px;
            font-size: 14px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        
        .filter-group {
            margin-bottom: 24px;
        }
        
        .filter-option {
            margin-bottom: 8px;
            display: flex;
            align-items: center;
        }
        
        .filter-option input[type="radio"] {
            margin-right: 8px;
            accent-color: #3b82f6;
        }
        
        .filter-option label {
            font-size: 14px;
            color: #6b7280;
            cursor: pointer;
            font-weight: 400;
        }
        
        .filter-option input[type="radio"]:checked + label {
            color: #3b82f6;
            font-weight: 500;
        }
        
        .content-area {
            background: white;
            border-radius: 8px;
            padding: 24px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
        }
        
        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 24px;
        }
        
        .page-title {
            font-size: 24px;
            font-weight: 600;
            color: #111827;
            margin: 0;
        }
        
        .search-area {
            margin-bottom: 24px;
        }
        
        .search-input {
            width: 100%;
            max-width: 400px;
            padding: 8px 12px 8px 40px;
            border: 1px solid #d1d5db;
            border-radius: 6px;
            font-size: 14px;
            background-image: url('data:image/svg+xml;charset=UTF-8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="%236b7280"><path fill-rule="evenodd" d="M8 4a4 4 0 100 8 4 4 0 000-8zM2 8a6 6 0 1110.89 3.476l4.817 4.817a1 1 0 01-1.414 1.414l-4.816-4.816A6 6 0 012 8z" clip-rule="evenodd" /></svg>');
            background-repeat: no-repeat;
            background-position: 12px center;
            background-size: 16px;
        }
        
        .search-input:focus {
            outline: none;
            border-color: #3b82f6;
            box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
        }
        
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 16px;
            margin-bottom: 24px;
        }
        
        .stat-card {
            border-radius: 8px;
            padding: 20px;
            text-align: center;
            position: relative;
            color: white;
        }
        
        .stat-card.blue {
            background: linear-gradient(135deg, #3b82f6, #1d4ed8);
        }
        
        .stat-card.green {
            background: linear-gradient(135deg, #10b981, #047857);
        }
        
        .stat-card.yellow {
            background: linear-gradient(135deg, #f59e0b, #d97706);
        }
        
        .stat-card.red {
            background: linear-gradient(135deg, #ef4444, #dc2626);
        }
        
        .stat-icon {
            width: 40px;
            height: 40px;
            border-radius: 8px;
            background: rgba(255,255,255,0.2);
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 8px;
            font-size: 18px;
        }
        
        .stat-number {
            font-size: 32px;
            font-weight: 700;
            line-height: 1;
            margin-bottom: 4px;
        }
        
        .stat-label {
            font-size: 14px;
            opacity: 0.9;
        }
        
        .promotion-table {
            background: white;
            border-radius: 12px;
            overflow: hidden;
            border: 1px solid #e5e7eb;
            box-shadow: 0 4px 16px rgba(0,0,0,0.08);
        }

        .table-header {
            background: linear-gradient(135deg, #1e293b, #334155);
            color: white;
            padding: 20px 24px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-bottom: 1px solid #e2e8f0;
        }

        .table-title {
            font-size: 1.1rem;
            font-weight: 600;
            color: white;
            margin: 0;
            display: flex;
            align-items: center;
        }

        .btn-add {
            background: #3b82f6;
            color: white;
            border: none;
            padding: 8px 16px;
            border-radius: 8px;
            text-decoration: none;
            font-size: 0.875rem;
            font-weight: 500;
            transition: all 0.3s ease;
            display: inline-flex;
            align-items: center;
            gap: 6px;
        }

        .btn-add:hover {
            background: #2563eb;
            color: white;
            text-decoration: none;
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
        }
        
        .table {
            margin: 0;
            width: 100%;
            border-collapse: separate;
            border-spacing: 0;
        }
        
        .table th {
            background: #f8fafc;
            border-bottom: 2px solid #e2e8f0;
            font-weight: 600;
            color: #475569;
            font-size: 0.75rem;
            text-transform: uppercase;
            letter-spacing: 0.05em;
            padding: 16px 20px;
            position: sticky;
            top: 0;
            z-index: 10;
        }
        
        .table td {
            padding: 18px 20px;
            border-bottom: 1px solid #f1f5f9;
            vertical-align: middle;
            font-size: 0.875rem;
        }
        
        .table tbody tr:hover {
            background-color: #f8fafc;
        }
        
        .promotion-name {
            font-weight: 600;
            color: #111827;
            font-size: 14px;
        }
        
        .promotion-id {
            color: #6b7280;
            font-size: 12px;
        }
        
        .discount-badge {
            background: #dbeafe;
            color: #1e40af;
            padding: 4px 8px;
            border-radius: 4px;
            font-weight: 600;
            font-size: 12px;
        }
        
        .status-badge {
            display: inline-flex;
            align-items: center;
            padding: 6px 14px;
            border-radius: 24px;
            font-size: 0.75rem;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.05em;
            box-shadow: 0 2px 4px rgba(0,0,0,0.08);
        }
        
        .status-active {
            background: linear-gradient(135deg, #d1fae5, #a7f3d0);
            color: #065f46;
            border: 1px solid #10b981;
        }
        
        .status-expired {
            background: linear-gradient(135deg, #fee2e2, #fecaca);
            color: #991b1b;
            border: 1px solid #ef4444;
        }
        
        .status-scheduled {
            background: linear-gradient(135deg, #fef3c7, #fed7aa);
            color: #92400e;
            border: 1px solid #f59e0b;
        }
        
        .action-btn {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            width: 36px;
            height: 36px;
            border-radius: 8px;
            font-size: 0.875rem;
            margin-right: 6px;
            border: none;
            cursor: pointer;
            transition: all 0.3s ease;
            text-decoration: none;
        }
        
        .btn-view {
            background: linear-gradient(135deg, #dbeafe, #bfdbfe);
            color: #1e40af;
            border: 1px solid #3b82f6;
        }
        
        .btn-edit {
            background: linear-gradient(135deg, #fed7aa, #fbbf24);
            color: #92400e;
            border: 1px solid #f59e0b;
        }
        
        .btn-delete {
            background: linear-gradient(135deg, #fecaca, #f87171);
            color: #dc2626;
            border: 1px solid #ef4444;
        }
        
        .action-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        }
        
        .btn-view:hover { box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3); }
        .btn-edit:hover { box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3); }
        .btn-delete:hover { box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3); }
        
        .create-btn {
            background: #3b82f6;
            border: none;
            padding: 8px 16px;
            border-radius: 6px;
            color: white;
            font-weight: 500;
            font-size: 14px;
            transition: all 0.2s;
            display: flex;
            align-items: center;
            gap: 8px;
        }
        
        .create-btn:hover {
            background: #2563eb;
            transform: translateY(-1px);
            box-shadow: 0 4px 8px rgba(59, 130, 246, 0.3);
        }
        
        .branch-count, .product-count {
            font-size: 12px;
            color: #6b7280;
            display: flex;
            align-items: center;
            gap: 4px;
            margin-bottom: 2px;
        }
        
        .time-range {
            font-size: 13px;
            color: #374151;
        }
        
        .time-range .text-muted {
            color: #9ca3af;
        }
        
        .checkbox-col {
            width: 40px;
        }
        
        .id-col {
            width: 80px;
        }
        
        .actions-col {
            width: 120px;
        }

        /* Filter Actions */
        .filter-actions {
            margin-top: 20px;
            display: flex;
            gap: 10px;
            flex-direction: column;
        }

        .btn-filter, .btn-reset {
            padding: 10px 16px;
            border: none;
            border-radius: 8px;
            font-size: 0.875rem;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            justify-content: center;
            text-decoration: none;
        }

        .btn-filter {
            background: linear-gradient(135deg, #3b82f6, #2563eb);
            color: white;
        }

        .btn-filter:hover {
            background: linear-gradient(135deg, #2563eb, #1d4ed8);
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
        }

        .btn-reset {
            background: linear-gradient(135deg, #6b7280, #4b5563);
            color: white;
        }

        .btn-reset:hover {
            background: linear-gradient(135deg, #4b5563, #374151);
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(107, 114, 128, 0.4);
        }

        /* Responsive Design */
        @media (max-width: 992px) {
            .main-layout {
                flex-direction: column;
            }
            
            .sidebar {
                width: 100%;
                margin-bottom: 20px;
            }
            
            .main-content {
                margin-left: 0;
            }
            
            .stats-grid {
                grid-template-columns: repeat(2, 1fr);
            }
        }
        
        @media (max-width: 576px) {
            .stats-grid {
                grid-template-columns: 1fr;
            }
            
            .table-header {
                flex-direction: column;
                gap: 16px;
                text-align: center;
            }
            
            .action-btn {
                width: 32px;
                height: 32px;
                margin-right: 4px;
            }
        }
    </style>
</head>
<body style="background-color: #f8f9fa;">
    <!-- Include Header -->
    <jsp:include page="header_admin.jsp"/>

    <div class="main-container">
        <div class="container-fluid px-4">
            <div class="row">
                <!-- Sidebar Filters -->
                <div class="col-md-3">
                    <div class="sidebar">
                        <h6>Trạng thái</h6>
                        <div class="filter-group">
                            <div class="filter-option">
                                <input type="radio" id="all_status" name="status_filter" value="" ${empty param.status ? 'checked' : ''}>
                                <label for="all_status">Tất cả</label>
                            </div>
                            <div class="filter-option">
                                <input type="radio" id="active_status" name="status_filter" value="active" ${param.status == 'active' ? 'checked' : ''}>
                                <label for="active_status">Đang hoạt động</label>
                            </div>
                            <div class="filter-option">
                                <input type="radio" id="scheduled_status" name="status_filter" value="scheduled" ${param.status == 'scheduled' ? 'checked' : ''}>
                                <label for="scheduled_status">Đã lên lịch</label>
                            </div>
                            <div class="filter-option">
                                <input type="radio" id="expired_status" name="status_filter" value="expired" ${param.status == 'expired' ? 'checked' : ''}>
                                <label for="expired_status">Đã hết hạn</label>
                            </div>
                        </div>

                        <h6>Mức giảm giá</h6>
                        <div class="filter-group">
                            <div class="filter-option">
                                <input type="radio" id="all_discount" name="discount_filter" value="" ${empty param.discount ? 'checked' : ''}>
                                <label for="all_discount">Tất cả</label>
                            </div>
                            <div class="filter-option">
                                <input type="radio" id="low_discount" name="discount_filter" value="low" ${param.discount == 'low' ? 'checked' : ''}>
                                <label for="low_discount">Dưới 15%</label>
                            </div>
                            <div class="filter-option">
                                <input type="radio" id="medium_discount" name="discount_filter" value="medium" ${param.discount == 'medium' ? 'checked' : ''}>
                                <label for="medium_discount">15% - 25%</label>
                            </div>
                            <div class="filter-option">
                                <input type="radio" id="high_discount" name="discount_filter" value="high" ${param.discount == 'high' ? 'checked' : ''}>
                                <label for="high_discount">Trên 25%</label>
                            </div>
                        </div>
                        
                        <!-- Filter Actions -->
<!--                        <div class="filter-actions">
                            <button type="button" class="btn-filter" onclick="applyFilters()">
                                <i class="fas fa-search me-1"></i>
                                Áp dụng
                            </button>
                            <button type="button" class="btn-reset" onclick="resetFilters()">
                                <i class="fas fa-undo me-1"></i>
                                Đặt lại
                            </button>
                             Debug button 
                            <button type="button" onclick="testFormSubmit()" style="background: red; color: white; padding: 5px; margin-top: 10px;">
                                Test Submit
                            </button>
                        </div>-->
                    </div>
                </div>

                <!-- Main Content -->
                <div class="col-md-9">
                    <div class="content-area">
                        <!-- Page Header -->
                        <div class="page-header">
                            <h1 class="page-title">Khuyến mãi</h1>
                            <a href="Promotions?action=create" class="btn create-btn">
                                <i class="fas fa-plus me-2"></i>Thêm mới
                            </a>
                        </div>

                        <!-- Search Area -->
                        <div class="search-area">
                            <form method="get" action="Promotions" id="searchForm">
                                <input type="hidden" name="status" id="statusFilter" value="${param.status}">
                                <input type="hidden" name="discount" id="discountFilter" value="${param.discount}">
                                <input type="text" class="search-input" name="search" 
                                       placeholder="Theo tên khuyến mãi" value="${param.search}">
                            </form>
                        </div>

                        <!-- Alert Messages -->
                        <c:if test="${param.msg == 'created'}">
                            <div class="alert alert-success alert-dismissible fade show" role="alert">
                                <i class="fas fa-check-circle me-2"></i>
                                Tạo khuyến mãi thành công!
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>
                        <c:if test="${param.msg == 'updated'}">
                            <div class="alert alert-success alert-dismissible fade show" role="alert">
                                <i class="fas fa-check-circle me-2"></i>
                                Cập nhật khuyến mãi thành công!
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>
                        <c:if test="${param.msg == 'deleted'}">
                            <div class="alert alert-success alert-dismissible fade show" role="alert">
                                <i class="fas fa-check-circle me-2"></i>
                                Xóa khuyến mãi thành công!
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>
                        <c:if test="${not empty param.error}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                <i class="fas fa-exclamation-circle me-2"></i>
                                <c:choose>
                                    <c:when test="${param.error == 'access_denied'}">Bạn không có quyền thực hiện thao tác này!</c:when>
                                    <c:when test="${param.error == 'invalid_name'}">Tên khuyến mãi không hợp lệ!</c:when>
                                    <c:when test="${param.error == 'invalid_discount'}">Phần trăm giảm giá phải từ 1% đến 100%!</c:when>
                                    <c:when test="${param.error == 'invalid_date_range'}">Ngày bắt đầu phải trước ngày kết thúc!</c:when>
                                    <c:when test="${param.error == 'create_failed'}">Không thể tạo khuyến mãi!</c:when>
                                    <c:when test="${param.error == 'update_failed'}">Không thể cập nhật khuyến mãi!</c:when>
                                    <c:when test="${param.error == 'delete_failed'}">Không thể xóa khuyến mãi!</c:when>
                                    <c:otherwise>Có lỗi xảy ra!</c:otherwise>
                                </c:choose>
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>

                        <!-- Statistics Cards -->
                        <div class="stats-grid">
                            <div class="stat-card blue">
                                <div class="stat-icon">
                                    <i class="fas fa-percentage"></i>
                                </div>
                                <div class="stat-number">${totalPromotions}</div>
                                <div class="stat-label">Tổng khuyến mãi</div>
                            </div>
                            <div class="stat-card green">
                                <div class="stat-icon">
                                    <i class="fas fa-play-circle"></i>
                                </div>
                                <div class="stat-number">${activePromotions}</div>
                                <div class="stat-label">Đang hoạt động</div>
                            </div>
                            <div class="stat-card yellow">
                                <div class="stat-icon">
                                    <i class="fas fa-clock"></i>
                                </div>
                                <div class="stat-number">${scheduledPromotions}</div>
                                <div class="stat-label">Đã lên lịch</div>
                            </div>
                            <div class="stat-card red">
                                <div class="stat-icon">
                                    <i class="fas fa-times-circle"></i>
                                </div>
                                <div class="stat-number">${expiredPromotions}</div>
                                <div class="stat-label">Đã hết hạn</div>
                            </div>
                        </div>

                        <!-- Promotions Table -->
                        <div class="promotion-table">
                            <!-- Table Header -->
                            <div class="table-header">
                                <h5 class="table-title">
                                    <i class="fas fa-list me-2"></i>Danh sách khuyến mãi
                                </h5>
                                <c:if test="${sessionScope.currentUser.roleId == 0}">
                                    <a href="Promotions?action=create" class="btn-add">
                                        <i class="fas fa-plus"></i>
                                        Thêm khuyến mãi
                                    </a>
                                </c:if>
                            </div>
                            <div class="table-responsive">
                                <table class="table align-middle mb-0">
                                    <thead>
                                        <tr>
                                            <th><input type="checkbox" id="selectAll"></th>
                                            <th>Mã KM</th>
                                            <th>Tên khuyến mãi</th>
                                            <th>Giảm giá</th>
                                            <th>Thời gian</th>
                                            <th>Phạm vi</th>
                                            <th>Trạng thái</th>
                                            <th>Thao tác</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:choose>
                                            <c:when test="${empty promotions}">
                                                <tr>
                                                    <td colspan="8" class="text-center py-5">
                                                        <div class="text-muted">
                                                            <i class="fas fa-inbox fa-3x mb-3"></i>
                                                            <p>Chưa có khuyến mãi nào</p>
                                                            <a href="Promotions?action=create" class="btn create-btn">
                                                                Tạo khuyến mãi đầu tiên
                                                            </a>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:when>
                                            <c:otherwise>
                                                <c:forEach var="promotion" items="${promotions}">
                                                    <tr>
                                                        <td><input type="checkbox" name="selectedPromotions" value="${promotion.promotionId}"></td>
                                                        <td>
                                                            <span class="promotion-id">${promotion.promotionId}</span>
                                                        </td>
                                                        <td>
                                                            <div class="promotion-name">${promotion.promoName}</div>
                                                        </td>
                                                        <td>
                                                            <span class="discount-badge">${promotion.discountPercent}%</span>
                                                        </td>
                                                        <td>
                                                            <div style="font-size: 13px;">
                                                                <fmt:formatDate value="${promotion.startDate}" pattern="dd/MM/yyyy"/>
                                                                <span class="text-muted">đến</span><br>
                                                                <fmt:formatDate value="${promotion.endDate}" pattern="dd/MM/yyyy"/>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div class="branch-count">
                                                                <i class="fas fa-map-marker-alt"></i> 
                                                                <c:choose>
                                                                    <c:when test="${empty promotion.branchCount || promotion.branchCount == 0}">
                                                                        Tất cả chi nhánh
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        ${promotion.branchCount} chi nhánh
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </div>
                                                            <div class="product-count">
                                                                <i class="fas fa-box"></i> 
                                                                <c:choose>
                                                                    <c:when test="${empty promotion.productCount || promotion.productCount == 0}">
                                                                        Tất cả sản phẩm
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        ${promotion.productCount} sản phẩm
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <jsp:useBean id="currentDate" class="java.util.Date"/>
                                                            <c:choose>
                                                                <c:when test="${currentDate.before(promotion.startDate)}">
                                                                    <span class="status-badge status-scheduled">Đã lên lịch</span>
                                                                </c:when>
                                                                <c:when test="${currentDate.after(promotion.endDate)}">
                                                                    <span class="status-badge status-expired">Đã hết hạn</span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="status-badge status-active">Đang hoạt động</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td>
                                                            <button class="action-btn btn-view" 
                                                                    onclick="viewPromotion('${promotion.promotionId}')"
                                                                    title="Xem chi tiết">
                                                                <i class="fas fa-info-circle"></i>
                                                            </button>
                                                            <c:if test="${sessionScope.currentUser.roleId == 0}">
                                                                <button class="action-btn btn-edit" 
                                                                        onclick="editPromotion('${promotion.promotionId}')"
                                                                        title="Chỉnh sửa">
                                                                    <i class="fas fa-edit"></i>
                                                                </button>
                                                                <button class="action-btn btn-delete" 
                                                                        onclick="deletePromotion('${promotion.promotionId}')"
                                                                        title="Xóa">
                                                                    <i class="fas fa-trash"></i>
                                                                </button>
                                                            </c:if>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </c:otherwise>
                                        </c:choose>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div class="modal fade" id="deleteModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Xác Nhận Xóa</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <p>Bạn có chắc chắn muốn xóa khuyến mãi này?</p>
                    <p class="text-danger fw-medium">Thao tác này không thể hoàn tác!</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <form id="deleteForm" method="post" style="display: inline;">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" id="deletePromotionId" name="promotionId" value="">
                        <button type="submit" class="btn btn-danger">Xóa</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        $(document).ready(function() {
            // Filter functionality - auto submit on change
            $('input[name="status_filter"], input[name="discount_filter"]').change(function() {
                console.log('Radio button changed:', $(this).attr('name'), $(this).val());
                updateFilters();
            });

            // Manual click handlers for radio buttons
            $('input[name="status_filter"]').click(function() {
                console.log('Status filter clicked:', $(this).val());
            });
            
            $('input[name="discount_filter"]').click(function() {
                console.log('Discount filter clicked:', $(this).val());
            });

            // Select all functionality
            $('#selectAll').change(function() {
                $('input[name="selectedPromotions"]').prop('checked', this.checked);
            });

            // Auto-hide alerts after 5 seconds
            setTimeout(function() {
                $('.alert').fadeOut();
            }, 5000);
            
            // Search input handler
            $('input[name="search"]').on('keypress', function(e) {
                if (e.which == 13) { // Enter key
                    console.log('Search submitted:', $(this).val());
                    $('#searchForm').submit();
                }
            });
            
            // Debug: Log when page loads
            console.log('Promotions page loaded');
            console.log('Form found:', $('#searchForm').length);
            console.log('Status filters found:', $('input[name="status_filter"]').length);
            console.log('Discount filters found:', $('input[name="discount_filter"]').length);
        });

        function updateFilters() {
            const status = $('input[name="status_filter"]:checked').val() || '';
            const discount = $('input[name="discount_filter"]:checked').val() || '';
            
            console.log('Update filters - Status:', status, 'Discount:', discount);
            
            $('#statusFilter').val(status);
            $('#discountFilter').val(discount);
            $('#searchForm').submit();
        }

        function applyFilters() {
            const status = $('input[name="status_filter"]:checked').val() || '';
            const discount = $('input[name="discount_filter"]:checked').val() || '';
            
            console.log('Apply filters - Status:', status, 'Discount:', discount);
            
            $('#statusFilter').val(status);
            $('#discountFilter').val(discount);
            $('#searchForm').submit();
        }

        function resetFilters() {
            // Reset all radio buttons
            $('input[name="status_filter"][value=""]').prop('checked', true);
            $('input[name="discount_filter"][value=""]').prop('checked', true);
            
            // Reset hidden fields
            $('#statusFilter').val('');
            $('#discountFilter').val('');
            
            // Reset search input
            $('input[name="search"]').val('');
            
            // Submit form
            $('#searchForm').submit();
        }

        function viewPromotion(id) {
            // Implement view promotion details
            window.location.href = 'Promotions?action=view&id=' + id;
        }

        function editPromotion(id) {
            // Redirect to edit page
            window.location.href = 'Promotions?action=edit&id=' + id;
        }

        function deletePromotion(id) {
            document.getElementById('deletePromotionId').value = id;
            new bootstrap.Modal(document.getElementById('deleteModal')).show();
        }
        
        function testFormSubmit() {
            console.log('Manual test form submit');
            console.log('Status input value:', $('input[name="status_filter"]:checked').val());
            console.log('Discount input value:', $('input[name="discount_filter"]:checked').val());
            console.log('Search input value:', $('input[name="search"]').val());
            
            // Manually set values and submit
            $('#statusFilter').val('active');
            $('#discountFilter').val('');
            $('#searchForm').submit();
        }
    </script>
</body>
</html>