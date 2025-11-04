<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi Tiết Khuyến Mãi - VIP Store</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f5f5f5;
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
        }
        
        .main-container {
            padding-top: 85px;
            min-height: 100vh;
        }
        
        .detail-card {
            background: white;
            border-radius: 15px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            padding: 30px;
            margin-bottom: 20px;
        }
        
        .detail-header {
            border-bottom: 2px solid #f8f9fa;
            padding-bottom: 20px;
            margin-bottom: 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .promotion-title {
            font-size: 1.8rem;
            font-weight: 700;
            color: #2d3748;
            margin: 0;
        }
        
        .promotion-id {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 8px 16px;
            border-radius: 25px;
            font-weight: 600;
            font-size: 0.875rem;
        }
        
        .detail-section {
            margin-bottom: 25px;
        }
        
        .section-title {
            font-size: 1.1rem;
            font-weight: 600;
            color: #4a5568;
            margin-bottom: 15px;
            display: flex;
            align-items: center;
        }
        
        .section-icon {
            width: 24px;
            height: 24px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 10px;
            font-size: 0.875rem;
        }
        
        .info-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
        }
        
        .info-item {
            background: #f7fafc;
            padding: 20px;
            border-radius: 12px;
            border-left: 4px solid #3182ce;
        }
        
        .info-label {
            font-size: 0.875rem;
            color: #718096;
            font-weight: 500;
            margin-bottom: 5px;
        }
        
        .info-value {
            font-size: 1.1rem;
            color: #2d3748;
            font-weight: 600;
        }
        
        .status-badge {
            display: inline-flex;
            align-items: center;
            padding: 8px 16px;
            border-radius: 25px;
            font-size: 0.875rem;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.05em;
        }
        
        .status-active {
            background: linear-gradient(135deg, #48bb78, #38a169);
            color: white;
        }
        
        .status-expired {
            background: linear-gradient(135deg, #f56565, #e53e3e);
            color: white;
        }
        
        .status-scheduled {
            background: linear-gradient(135deg, #ed8936, #dd6b20);
            color: white;
        }
        
        .discount-highlight {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 15px 25px;
            border-radius: 15px;
            text-align: center;
            font-size: 2rem;
            font-weight: 700;
            margin: 20px 0;
        }
        
        .branch-list, .product-list {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 15px;
        }
        
        .branch-item, .product-item {
            background: white;
            border: 2px solid #e2e8f0;
            border-radius: 12px;
            padding: 15px;
            transition: all 0.3s ease;
        }
        
        .branch-item:hover, .product-item:hover {
            border-color: #3182ce;
            box-shadow: 0 4px 12px rgba(49, 130, 206, 0.15);
        }
        
        .branch-name, .product-name {
            font-weight: 600;
            color: #2d3748;
            margin-bottom: 5px;
        }
        
        .branch-address, .product-info {
            color: #718096;
            font-size: 0.875rem;
        }
        
        .action-buttons {
            display: flex;
            gap: 15px;
            justify-content: center;
            margin-top: 30px;
        }
        
        .btn-custom {
            padding: 12px 25px;
            border-radius: 10px;
            font-weight: 600;
            text-decoration: none;
            transition: all 0.3s ease;
            border: none;
        }
        
        .btn-primary-custom {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        .btn-primary-custom:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
            color: white;
        }
        
        .btn-secondary-custom {
            background: #e2e8f0;
            color: #4a5568;
        }
        
        .btn-secondary-custom:hover {
            background: #cbd5e0;
            color: #2d3748;
            text-decoration: none;
        }
        
        .btn-warning-custom {
            background: linear-gradient(135deg, #ed8936 0%, #dd6b20 100%);
            color: white;
        }
        
        .btn-warning-custom:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(237, 137, 54, 0.4);
            color: white;
        }
        
        .empty-state {
            text-align: center;
            padding: 40px;
            color: #718096;
        }
        
        .empty-state i {
            font-size: 3rem;
            margin-bottom: 15px;
            opacity: 0.5;
        }
        
        .product-list {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 20px;
            margin-top: 20px;
        }
        
        .product-item {
            background: white;
            border: 1px solid #e2e8f0;
            border-radius: 8px;
            padding: 20px;
            transition: all 0.3s ease;
        }
        
        .product-item:hover {
            border-color: #3182ce;
            box-shadow: 0 4px 12px rgba(49, 130, 206, 0.1);
            transform: translateY(-2px);
        }
        
        .product-name {
            font-weight: 600;
            font-size: 16px;
            color: #2d3748;
            margin-bottom: 15px;
            display: flex;
            align-items: center;
        }
        
        .product-name i {
            color: #ed8936;
            margin-right: 8px;
        }
        
        .product-info {
            display: flex;
            flex-direction: column;
            gap: 8px;
        }
        
        .product-info div {
            font-size: 14px;
            color: #4a5568;
        }
        
        .product-info strong {
            color: #2d3748;
            font-weight: 500;
        }
    </style>
</head>
<body>
    <!-- Include Header -->
    <jsp:include page="header_admin.jsp"/>

    <div class="main-container">
        <div class="container-fluid px-4">
            <div class="row justify-content-center">
                <div class="col-lg-10">
                    <div class="detail-card">
                        <!-- Header -->
                        <div class="detail-header">
                            <h1 class="promotion-title">${promotion.promoName}</h1>
                            <div class="promotion-id">ID: ${promotion.promotionId}</div>
                        </div>

                        <!-- Basic Information -->
                        <div class="detail-section">
                            <h3 class="section-title">
                                <span class="section-icon" style="background: #3182ce; color: white;">
                                    <i class="fas fa-info-circle"></i>
                                </span>
                                Thông tin cơ bản
                            </h3>
                            
                            <div class="info-grid">
                                <div class="info-item">
                                    <div class="info-label">Tên khuyến mãi</div>
                                    <div class="info-value">${promotion.promoName}</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Phần trăm giảm giá</div>
                                    <div class="info-value">
                                        <div class="discount-highlight">
                                            ${promotion.discountPercent}%
                                        </div>
                                    </div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Ngày bắt đầu</div>
                                    <div class="info-value">
                                        <fmt:formatDate value="${promotion.startDate}" pattern="dd/MM/yyyy HH:mm"/>
                                    </div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Ngày kết thúc</div>
                                    <div class="info-value">
                                        <fmt:formatDate value="${promotion.endDate}" pattern="dd/MM/yyyy HH:mm"/>
                                    </div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Trạng thái</div>
                                    <div class="info-value">
                                        <jsp:useBean id="currentDate" class="java.util.Date"/>
                                        <c:choose>
                                            <c:when test="${currentDate.before(promotion.startDate)}">
                                                <span class="status-badge status-scheduled">
                                                    <i class="fas fa-clock me-2"></i>Đã lên lịch
                                                </span>
                                            </c:when>
                                            <c:when test="${currentDate.after(promotion.endDate)}">
                                                <span class="status-badge status-expired">
                                                    <i class="fas fa-times-circle me-2"></i>Đã hết hạn
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-badge status-active">
                                                    <i class="fas fa-check-circle me-2"></i>Đang hoạt động
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Thống kê áp dụng</div>
                                    <div class="info-value">
                                        <div><i class="fas fa-store me-2"></i>${promotion.branchCount > 0 ? promotion.branchCount : 'Tất cả'} chi nhánh</div>
                                        <div><i class="fas fa-box me-2"></i>${promotion.productCount > 0 ? promotion.productCount : 'Tất cả'} sản phẩm</div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Branches Applied -->
                        <div class="detail-section">
                            <h3 class="section-title">
                                <span class="section-icon" style="background: #38a169; color: white;">
                                    <i class="fas fa-store"></i>
                                </span>
                                Chi nhánh áp dụng
                            </h3>
                            
                            <c:choose>
                                <c:when test="${empty promotionBranches}">
                                    <div class="empty-state">
                                        <i class="fas fa-store"></i>
                                        <h5>Áp dụng cho tất cả chi nhánh</h5>
                                        <p>Khuyến mãi này được áp dụng cho tất cả các chi nhánh trong hệ thống</p>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="branch-list">
                                        <c:forEach var="branch" items="${promotionBranches}">
                                            <div class="branch-item">
                                                <div class="branch-name">
                                                    <i class="fas fa-map-marker-alt me-2"></i>${branch.branchName}
                                                </div>
                                                <div class="branch-address">${branch.address}</div>
                                                <div class="branch-address">
                                                    <i class="fas fa-phone me-2"></i>${branch.phone}
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <!-- Products Applied -->
                        <div class="detail-section">
                            <h3 class="section-title">
                                <span class="section-icon" style="background: #ed8936; color: white;">
                                    <i class="fas fa-box"></i>
                                </span>
                                Sản phẩm áp dụng
                            </h3>
                            
                            <c:choose>
                                <c:when test="${empty promotionProducts}">
                                    <div class="empty-state">
                                        <i class="fas fa-box"></i>
                                        <h5>Áp dụng cho tất cả sản phẩm</h5>
                                        <p>Khuyến mãi này được áp dụng cho tất cả các sản phẩm trong hệ thống</p>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="product-list">
                                        <c:forEach var="product" items="${promotionProducts}">
                                            <div class="product-item">
                                                <div class="product-name">
                                                    <i class="fas fa-cube me-2"></i>${product.productName}
                                                </div>
                                                <div class="product-info">
                                                    <div><strong>Mã sản phẩm:</strong> ${product.productDetailID}</div>
                                                    <div><strong>Mã SKU:</strong> ${product.productCode}</div>
                                                    <div><strong>Mô tả:</strong> ${product.description}</div>
                                                    <div><strong>Giá bán lẻ:</strong> <fmt:formatNumber value="${product.retailPrice}" type="currency" currencySymbol="₫" currencyCode="VND" maxFractionDigits="0"/></div>
                                                    <div><strong>Giá vốn:</strong> <fmt:formatNumber value="${product.costPrice}" type="currency" currencySymbol="₫" currencyCode="VND" maxFractionDigits="0"/></div>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <!-- Action Buttons -->
                        <div class="action-buttons">
                            <a href="Promotions" class="btn btn-secondary-custom btn-custom">
                                <i class="fas fa-arrow-left me-2"></i>Quay lại danh sách
                            </a>
                            
                            <c:if test="${sessionScope.currentUser.roleId == 0}">
                                <a href="Promotions?action=edit&id=${promotion.promotionId}" class="btn btn-warning-custom btn-custom">
                                    <i class="fas fa-edit me-2"></i>Chỉnh sửa
                                </a>
                                
                                <button class="btn btn-danger btn-custom" onclick="deletePromotion('${promotion.promotionId}')">
                                    <i class="fas fa-trash me-2"></i>Xóa khuyến mãi
                                </button>
                            </c:if>
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
                    <p>Bạn có chắc chắn muốn xóa khuyến mãi "<strong>${promotion.promoName}</strong>"?</p>
                    <p class="text-danger fw-medium">Thao tác này không thể hoàn tác!</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <form action="Promotions" method="post" style="display: inline;">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="promotionId" value="${promotion.promotionId}">
                        <button type="submit" class="btn btn-danger">Xóa</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function deletePromotion(id) {
            new bootstrap.Modal(document.getElementById('deleteModal')).show();
        }
    </script>
</body>
</html>