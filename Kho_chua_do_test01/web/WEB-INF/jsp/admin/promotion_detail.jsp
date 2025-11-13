<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.Promotion" %>
<%@ page import="Model.Branch" %>
<%@ page import="Model.ProductDetail" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết khuyến mãi</title>
    <link href="css/admin/promotion_management.css" rel="stylesheet">
    <link href="css/admin/promotion_detail.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>
<body>
  <!-- Header -->
            <%@ include file="../admin/header_admin.jsp" %>
    <main class="main-content">
        <div class="detail-container">
            <%
                Promotion promo = (Promotion) request.getAttribute("promotion");
                List<Branch> selectedBranches = (List<Branch>) request.getAttribute("selectedBranches");
                List<ProductDetail> selectedProducts = (List<ProductDetail>) request.getAttribute("selectedProducts");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                
                if (promo == null) {
            %>
            <div class="empty-state">
                <h3>Không tìm thấy khuyến mãi</h3>
                <p><a href="Promotion">Quay lại danh sách</a></p>
            </div>
            <%
                } else {
            %>
            <div class="detail-header">
                <div class="detail-header-left">
                    <div class="detail-header-icon">
                        <i class="fas fa-tags"></i>
                    </div>
                    <div>
                        <h2>Chi tiết khuyến mãi</h2>
                        <p style="margin: 5px 0 0 0; opacity: 0.9; font-size: 14px;">
                            Mã: #<%= promo.getPromotionId() %>
                        </p>
                    </div>
                </div>
                <div class="detail-actions">
                    <a href="Promotion?action=edit&id=<%= promo.getPromotionId() %>" class="btn-edit">
                        <i class="fas fa-edit"></i> Chỉnh sửa
                    </a>
                    <a href="Promotion" class="btn-cancel">
                        <i class="fas fa-arrow-left"></i> Quay lại
                    </a>
                </div>
            </div>

            <div class="detail-content">
                <!-- Thông tin cơ bản -->
                <div class="detail-section">
                    <h3><i class="fas fa-info-circle"></i> Thông tin cơ bản</h3>
                    <div class="info-card">
                        <div class="info-item">
                            <div class="info-item-icon">
                                <i class="fas fa-tag"></i>
                            </div>
                            <div class="info-item-content">
                                <div class="info-item-title">Tên khuyến mãi</div>
                                <div class="info-item-desc"><%= promo.getPromoName() %></div>
                            </div>
                        </div>
                        
                        <div class="info-item">
                            <div class="info-item-icon" style="background: linear-gradient(135deg, #10b981 0%, #059669 100%);">
                                <i class="fas fa-percent"></i>
                            </div>
                            <div class="info-item-content">
                                <div class="info-item-title">Tỷ lệ giảm giá</div>
                                <div class="info-item-desc">
                                    <span class="discount-badge"><%= promo.getDiscountPercent() %>%</span>
                                </div>
                            </div>
                        </div>
                        
                        <div class="info-item">
                            <div class="info-item-icon" style="background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);">
                                <i class="fas fa-calendar-alt"></i>
                            </div>
                            <div class="info-item-content">
                                <div class="info-item-title">Thời gian áp dụng</div>
                                <div class="info-item-desc">
                                    <%= dateFormat.format(promo.getStartDate()) %> - <%= dateFormat.format(promo.getEndDate()) %>
                                </div>
                            </div>
                        </div>
                        
                        <div class="info-item">
                            <div class="info-item-icon" style="background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);">
                                <i class="fas fa-info-circle"></i>
                            </div>
                            <div class="info-item-content">
                                <div class="info-item-title">Trạng thái</div>
                                <div class="info-item-desc">
                                    <%
                                        String statusClass = "";
                                        String statusText = "";
                                        if ("active".equals(promo.getStatus())) {
                                            statusClass = "status-active";
                                            statusText = "Còn " + promo.getDaysRemaining() + " ngày";
                                        } else if ("scheduled".equals(promo.getStatus())) {
                                            statusClass = "status-scheduled";
                                            statusText = "Đã lên lịch";
                                        } else if ("expired".equals(promo.getStatus())) {
                                            statusClass = "status-expired";
                                            statusText = "Đã hết hạn";
                                        }
                                    %>
                                    <span class="status <%= statusClass %> status-badge"><%= statusText %></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Chi nhánh áp dụng -->
                <div class="detail-section">
                    <h3>
                        <i class="fas fa-building"></i> 
                        Chi nhánh áp dụng 
                        <span style="font-size: 14px; color: #64748b; font-weight: 500;">
                            (<%= selectedBranches != null ? selectedBranches.size() : 0 %>)
                        </span>
                    </h3>
                    <%
                        if (selectedBranches != null && !selectedBranches.isEmpty()) {
                    %>
                    <div class="info-card">
                        <%
                            for (Branch branch : selectedBranches) {
                        %>
                        <div class="info-item">
                            <div class="info-item-icon" style="background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);">
                                <i class="fas fa-building"></i>
                            </div>
                            <div class="info-item-content">
                                <div class="info-item-title"><%= branch.getBranchName() %></div>
                                <div class="info-item-desc">
                                    <% if (branch.getAddress() != null) { %>
                                    <i class="fas fa-map-marker-alt"></i> <%= branch.getAddress() %>
                                    <% } else { %>
                                    <i class="fas fa-info-circle"></i> Không có địa chỉ
                                    <% } %>
                                </div>
                            </div>
                        </div>
                        <%
                            }
                        %>
                    </div>
                    <%
                        } else {
                    %>
                    <div class="empty-state">
                        <i class="fas fa-inbox"></i>
                        <h4>Không có chi nhánh cụ thể</h4>
                        <p>Khuyến mãi này áp dụng cho tất cả các chi nhánh</p>
                    </div>
                    <%
                        }
                    %>
                </div>

                <!-- Sản phẩm áp dụng -->
                <div class="detail-section">
                    <h3>
                        <i class="fas fa-box"></i> 
                        Sản phẩm áp dụng 
                        <span style="font-size: 14px; color: #64748b; font-weight: 500;">
                            (<%= selectedProducts != null ? selectedProducts.size() : 0 %>)
                        </span>
                    </h3>
                    <%
                        if (selectedProducts != null && !selectedProducts.isEmpty()) {
                    %>
                    <div class="info-card">
                        <%
                            for (ProductDetail product : selectedProducts) {
                        %>
                        <div class="info-item">
                            <div class="info-item-icon" style="background: linear-gradient(135deg, #ec4899 0%, #db2777 100%);">
                                <i class="fas fa-box"></i>
                            </div>
                            <div class="info-item-content">
                                <div class="info-item-title">
                                    <%= product.getProductName() != null ? product.getProductName() : "N/A" %>
                                </div>
                                <div class="info-item-desc">
                                    <% if (product.getProductCode() != null) { %>
                                    <i class="fas fa-barcode"></i> Mã: <%= product.getProductCode() %>
                                    <% } else { %>
                                    <i class="fas fa-info-circle"></i> Không có mã sản phẩm
                                    <% } %>
                                </div>
                            </div>
                        </div>
                        <%
                            }
                        %>
                    </div>
                    <%
                        } else {
                    %>
                    <div class="empty-state">
                        <i class="fas fa-inbox"></i>
                        <h4>Không có sản phẩm cụ thể</h4>
                        <p>Khuyến mãi này áp dụng cho tất cả các sản phẩm</p>
                    </div>
                    <%
                        }
                    %>
                </div>
            </div>
            <%
                }
            %>
        </div>
    </main>
</body>
</html>

