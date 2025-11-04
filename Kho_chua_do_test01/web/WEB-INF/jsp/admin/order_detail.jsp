<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.OrderDetail" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <title>Chi tiết đơn hàng #${order.orderId}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" />
    <style>
      body {
        background: #f8f9fa;
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        padding-top: 70px;
        margin: 0;
        padding-left: 0;
        padding-right: 0;
      }
      
      .container-fluid {
        padding-left: 2rem;
        padding-right: 2rem;
      }
      
      .info-card {
        background: white;
        border-radius: 12px;
        padding: 24px;
        box-shadow: 0 2px 12px rgba(0,0,0,0.08);
        border: 1px solid #e9ecef;
        margin-bottom: 24px;
      }
      
      .info-card h5 {
        color: #495057;
        font-weight: 600;
        margin-bottom: 20px;
        padding-bottom: 10px;
        border-bottom: 2px solid #e9ecef;
      }
      
      .info-row {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 12px 0;
        border-bottom: 1px solid #f1f3f4;
      }
      
      .info-row:last-child {
        border-bottom: none;
      }
      
      .info-label {
        color: #6c757d;
        font-weight: 500;
        font-size: 14px;
      }
      
      .info-value {
        color: #212529;
        font-weight: 600;
        font-size: 15px;
      }
      
      .status-badge {
        padding: 8px 16px;
        border-radius: 20px;
        font-size: 14px;
        font-weight: 600;
        text-transform: uppercase;
        letter-spacing: 0.5px;
      }
      
      .status-completed {
        background: #d1edff;
        color: #0066cc;
      }
      
      .products-card {
        background: white;
        border-radius: 12px;
        overflow: hidden;
        box-shadow: 0 2px 12px rgba(0,0,0,0.08);
        border: 1px solid #e9ecef;
      }
      
      .products-header {
        background: #f8f9fa;
        padding: 20px 24px;
        border-bottom: 1px solid #e9ecef;
      }
      
      .products-title {
        color: #495057;
        font-weight: 600;
        margin: 0;
        font-size: 18px;
      }
      
      .products-table {
        width: 100%;
        margin: 0;
      }
      
      .products-table th {
        background: #f8f9fa;
        color: #6c757d;
        font-weight: 600;
        font-size: 13px;
        text-transform: uppercase;
        letter-spacing: 0.5px;
        padding: 16px 20px;
        border-bottom: 2px solid #e9ecef;
        border-top: none;
      }
      
      .products-table td {
        padding: 16px 20px;
        vertical-align: middle;
        border-bottom: 1px solid #f1f3f4;
      }
      
      .products-table tbody tr:hover {
        background: #f8f9fa;
        transition: background-color 0.2s ease;
      }
      
      .product-img {
        width: 50px;
        height: 50px;
        object-fit: cover;
        border-radius: 8px;
        border: 1px solid #e9ecef;
      }
      
      .product-name {
        font-weight: 600;
        color: #212529;
        font-size: 15px;
        margin-bottom: 4px;
      }
      
      .product-category {
        color: #6c757d;
        font-size: 13px;
      }
      
      .quantity-badge {
        background: #e3f2fd;
        color: #1976d2;
        padding: 6px 12px;
        border-radius: 16px;
        font-weight: 600;
        font-size: 13px;
      }
      
      .price-text {
        font-weight: 600;
        color: #28a745;
        font-size: 15px;
      }
      
      .total-summary {
        background: white;
        border-radius: 12px;
        padding: 24px;
        box-shadow: 0 2px 12px rgba(0,0,0,0.08);
        border: 1px solid #e9ecef;
        margin-top: 24px;
      }
      
      .total-row {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 12px 0;
        font-size: 16px;
      }
      
      .total-row.final {
        border-top: 2px solid #e9ecef;
        margin-top: 12px;
        padding-top: 16px;
        font-weight: 700;
        font-size: 18px;
        color: #28a745;
      }
    </style>
  </head>
  <body>
    <!-- Include Header -->
    <%@ include file="header_admin.jsp" %>
    
    <div class="container-fluid py-4">
      <!-- Page Header -->
      <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h3 class="mb-2" style="color: #495057; font-weight: 600;">
            <i class="fas fa-receipt me-2"></i> Chi Tiết Đơn Hàng #${order.orderId}
          </h3>
          <a class="btn btn-outline-secondary" href="Orders">
            <i class="fas fa-arrow-left me-1"></i> Quay lại danh sách
          </a>
        </div>
        <div>
          <c:choose>
            <c:when test="${order.orderStatus eq 'Completed'}">
              <span class="status-badge status-completed">
                <i class="fas fa-check-circle me-1"></i> Hoàn thành
              </span>
            </c:when>
            <c:when test="${order.orderStatus eq 'Cancelled'}">
              <span class="badge bg-danger">
                <i class="fas fa-times-circle me-1"></i> Đã hủy
              </span>
            </c:when>
            <c:when test="${order.orderStatus eq 'Processing'}">
              <span class="badge bg-info">
                <i class="fas fa-spinner me-1"></i> Đang xử lý
              </span>
            </c:when>
            <c:when test="${order.orderStatus eq 'Pending'}">
              <span class="badge bg-warning">
                <i class="fas fa-clock me-1"></i> Chờ xử lý
              </span>
            </c:when>
            <c:otherwise>
              <span class="badge bg-secondary">${order.orderStatus}</span>
            </c:otherwise>
          </c:choose>
        </div>
      </div>

      <!-- Order & Customer Information -->
      <div class="row">
        <!-- Left: Order Information -->
        <div class="col-md-6">
          <div class="info-card">
            <h5><i class="fas fa-shopping-cart me-2"></i> Thông Tin Đơn Hàng</h5>
            
            <div class="info-row">
              <span class="info-label">Mã đơn hàng:</span>
              <span class="info-value">#${order.orderId}</span>
            </div>
            
            <div class="info-row">
              <span class="info-label">Chi nhánh:</span>
              <span class="info-value">${empty branchName ? '-' : branchName}</span>
            </div>
            
            <div class="info-row">
              <span class="info-label">Trạng thái:</span>
              <span class="info-value">
                <c:choose>
                  <c:when test="${order.orderStatus eq 'Completed'}">
                    <span class="status-badge status-completed">Hoàn thành</span>
                  </c:when>
                  <c:otherwise>${order.orderStatus}</c:otherwise>
                </c:choose>
              </span>
            </div>
            
            <div class="info-row">
              <span class="info-label">Ngày tạo:</span>
              <span class="info-value">
                <fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm:ss" />
              </span>
            </div>
            
            <div class="info-row">
              <span class="info-label">Phương thức TT:</span>
              <span class="info-value">
                <c:choose>
                  <c:when test="${order.paymentMethod eq 'Cash'}">Tiền mặt</c:when>
                  <c:when test="${order.paymentMethod eq 'Bank Transfer'}">Chuyển khoản</c:when>
                  <c:when test="${order.paymentMethod eq 'Credit Card'}">Thẻ tín dụng</c:when>
                  <c:otherwise>-</c:otherwise>
                </c:choose>
              </span>
            </div>
            
            <div class="info-row">
              <span class="info-label">Tổng sản phẩm:</span>
              <span class="info-value">
                <span class="quantity-badge">${fn:length(items)} sản phẩm</span>
              </span>
            </div>
            
            <div class="info-row">
              <span class="info-label">Ghi chú:</span>
              <span class="info-value">${empty order.notes ? 'Đơn hàng bán lẻ' : order.notes}</span>
            </div>
          </div>
        </div>
        
        <!-- Right: Customer Information -->
        <div class="col-md-6">
          <div class="info-card">
            <h5><i class="fas fa-user me-2"></i> Thông Tin Khách Hàng</h5>
            
            <div class="info-row">
              <span class="info-label">Mã KH:</span>
              <span class="info-value">${empty order.customerId ? '-' : order.customerId}</span>
            </div>
            
            <div class="info-row">
              <span class="info-label">Họ tên:</span>
              <span class="info-value">${empty customerName ? 'Khách vãng lai' : customerName}</span>
            </div>
            
            <div class="info-row">
              <span class="info-label">Điện thoại:</span>
              <span class="info-value">${empty customerPhone ? '-' : customerPhone}</span>
            </div>
            
            <div class="info-row">
              <span class="info-label">Email:</span>
              <span class="info-value">${empty customerEmail ? '-' : customerEmail}</span>
            </div>
            
            <div class="info-row">
              <span class="info-label">Địa chỉ:</span>
              <span class="info-value">${empty customerAddress ? '-' : customerAddress}</span>
            </div>
            
            <div class="info-row">
              <span class="info-label">Giới tính:</span>
              <span class="info-value">
                <c:choose>
                  <c:when test="${empty customerGender}">-</c:when>
                  <c:when test="${customerGender == true}">Nam</c:when>
                  <c:otherwise>Nữ</c:otherwise>
                </c:choose>
              </span>
            </div>
            
            <div class="info-row">
              <span class="info-label">Ngày sinh:</span>
              <span class="info-value">
                <c:choose>
                  <c:when test="${empty customerBirthDate}">-</c:when>
                  <c:otherwise>
                    <fmt:formatDate value="${customerBirthDate}" pattern="dd/MM/yyyy" />
                  </c:otherwise>
                </c:choose>
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Products List -->
      <div class="products-card">
        <div class="products-header">
          <h5 class="products-title">
            <i class="fas fa-list me-2"></i> Danh Sách Sản Phẩm
          </h5>
        </div>
        
        <div class="table-responsive">
          <table class="products-table">
            <thead>
              <tr>
                <th style="width: 80px;">Ảnh</th>
                <th>Tên sản phẩm</th>
                <th style="width: 140px; text-align: right;">Đơn giá</th>
                <th style="width: 100px; text-align: center;">Số lượng</th>
                <th style="width: 140px; text-align: right;">Thành tiền</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="d" items="${items}" varStatus="loop">
                <c:set var="label" value="${pdLabels[d.productDetailId]}" />
                <c:set var="info" value="${pdInfos[d.productDetailId]}" />
                <tr>
                  <td style="text-align: center;">
                    <img src="${not empty info && not empty info.imageUrl ? info.imageUrl : 'https://via.placeholder.com/50x50?text=No+Image'}" 
                         alt="Product" class="product-img" />
                  </td>
                  <td>
                    <div class="product-name">
                      <c:choose>
                        <c:when test="${not empty label}">${label}</c:when>
                        <c:otherwise>Không tìm thấy sản phẩm (#${d.productDetailId})</c:otherwise>
                      </c:choose>
                    </div>
                    <c:if test="${not empty info && not empty info.categoryName}">
                      <div class="product-category">
                        <i class="fas fa-tag me-1"></i> ${info.categoryName}
                      </div>
                    </c:if>
                  </td>
                  <td style="text-align: right;">
                    <c:choose>
                      <c:when test="${not empty info && not empty info.retailPrice}">
                        <span class="price-text">
                          <fmt:formatNumber value="${info.retailPrice}" type="number" groupingUsed="true" /> ₫
                        </span>
                      </c:when>
                      <c:otherwise>
                        <span class="text-muted">N/A</span>
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td style="text-align: center;">
                    <span class="quantity-badge">
                      ${d.quantity}
                    </span>
                  </td>
                  <td style="text-align: right;">
                    <c:choose>
                      <c:when test="${not empty info && not empty info.retailPrice}">
                        <span class="price-text" style="font-size: 16px; font-weight: 700;">
                          <fmt:formatNumber value="${info.retailPrice * d.quantity}" type="number" groupingUsed="true" /> ₫
                        </span>
                      </c:when>
                      <c:otherwise>
                        <span class="text-muted">N/A</span>
                      </c:otherwise>
                    </c:choose>
                  </td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Payment Summary -->
      <div class="total-summary">
        <div class="total-row">
          <span><i class="fas fa-receipt me-2"></i> Tổng tiền hàng:</span>
          <span>
            <c:choose>
              <c:when test="${not empty order.grandTotal}">
                <fmt:formatNumber value="${order.grandTotal}" type="number" groupingUsed="true" /> ₫
              </c:when>
              <c:otherwise>0 ₫</c:otherwise>
            </c:choose>
          </span>
        </div>
        
        <div class="total-row">
          <span><i class="fas fa-hand-holding-usd me-2"></i> Khách đưa:</span>
          <span>
            <c:choose>
              <c:when test="${not empty order.customerPay}">
                <fmt:formatNumber value="${order.customerPay}" type="number" groupingUsed="true" /> ₫
              </c:when>
              <c:otherwise>0 ₫</c:otherwise>
            </c:choose>
          </span>
        </div>
        
        <div class="total-row">
          <span><i class="fas fa-coins me-2"></i> Tiền thừa:</span>
          <span>
            <c:choose>
              <c:when test="${not empty order.changeAmount}">
                <fmt:formatNumber value="${order.changeAmount}" type="number" groupingUsed="true" /> ₫
              </c:when>
              <c:otherwise>0 ₫</c:otherwise>
            </c:choose>
          </span>
        </div>
        
        <div class="total-row final">
          <span><i class="fas fa-calculator me-2"></i> TỔNG THANH TOÁN:</span>
          <span>
            <c:choose>
              <c:when test="${not empty order.grandTotal}">
                <fmt:formatNumber value="${order.grandTotal}" type="number" groupingUsed="true" /> ₫
              </c:when>
              <c:otherwise>0 ₫</c:otherwise>
            </c:choose>
          </span>
        </div>
      </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <script>
      const msg = '${msg}';
      const error = '${error}';
      
      if (msg === 'branch-updated') {
        Swal.fire({ icon: 'success', title: 'Đã cập nhật chi nhánh', timer: 1500, showConfirmButton: false });
      } else if (msg) {
        Swal.fire({ icon: 'success', title: msg, timer: 1500, showConfirmButton: false });
      }
      
      if (error) {
        Swal.fire({ icon: 'error', title: 'Có lỗi xảy ra', text: error, confirmButtonText: 'OK' });
      }
    </script>
  </body>
</html>
