<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.OrderDetail" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <title>Chi tiết đơn hàng #${order.orderId}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" />
    <style>
      body {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        min-height: 100vh;
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
      }
      
      .info-box {
        padding: 12px;
        border-radius: 0.5rem;
        background: #f8f9fa;
        border-left: 4px solid #007bff;
        height: 100%;
      }
      
      .info-box label {
        font-size: 11px;
        font-weight: 600;
        text-transform: uppercase;
        letter-spacing: 0.5px;
        margin-bottom: 4px;
        display: block;
      }
      
      .info-box .value {
        font-size: 15px;
        font-weight: 600;
        color: #333;
      }
      
      .table > thead > tr > th {
        font-size: 13px;
        font-weight: 600;
        text-transform: uppercase;
        letter-spacing: 0.5px;
        border-bottom: 2px solid #dee2e6;
        padding: 14px 12px;
        background: #f8f9fa;
      }
      
      .table > tbody > tr > td {
        padding: 14px 12px;
        vertical-align: middle;
      }
      
      .table > tbody > tr:hover {
        background-color: #f8f9ff;
        transition: background-color 0.2s ease;
      }
      
      .payment-summary {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        padding: 20px;
        border-radius: 1rem;
        margin-top: 20px;
      }
      
      .payment-item {
        display: flex;
        justify-content: space-between;
        padding: 10px 0;
        border-bottom: 1px solid rgba(255,255,255,0.2);
      }
      
      .payment-item:last-child {
        border-bottom: none;
        font-size: 1.3rem;
        font-weight: 700;
        padding-top: 15px;
        margin-top: 10px;
        border-top: 2px solid rgba(255,255,255,0.4);
      }
      
      .product-thumb {
        width: 60px;
        height: 60px;
        object-fit: cover;
        border-radius: 0.5rem;
        border: 2px solid #e9ecef;
      }
      
      .card {
        box-shadow: 0 4px 24px rgba(0,0,0,0.1);
      }
    </style>
  </head>
  <body>
    <div class="container py-4">
      <!-- Header -->
      <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 class="mb-2 text-white" style="font-weight: 700;">
            <i class="fas fa-receipt"></i> Chi Tiết Đơn Hàng #${order.orderId}
          </h2>
          <a class="btn btn-light" href="Orders">
            <i class="fas fa-arrow-left"></i> Quay lại danh sách
          </a>
        </div>
        <div>
          <c:choose>
            <c:when test="${order.orderStatus eq 'Completed'}">
              <span class="badge bg-success fs-4 py-2 px-4">
                <i class="fas fa-check-circle"></i> Completed
              </span>
            </c:when>
            <c:when test="${order.orderStatus eq 'Cancelled'}">
              <span class="badge bg-danger fs-4 py-2 px-4">
                <i class="fas fa-times-circle"></i> Cancelled
              </span>
            </c:when>
            <c:when test="${order.orderStatus eq 'Processing'}">
              <span class="badge bg-info text-dark fs-4 py-2 px-4">
                <i class="fas fa-spinner"></i> Processing
              </span>
            </c:when>
            <c:when test="${order.orderStatus eq 'Pending'}">
              <span class="badge bg-warning text-dark fs-4 py-2 px-4">
                <i class="fas fa-clock"></i> Pending
              </span>
            </c:when>
            <c:otherwise>
              <span class="badge bg-secondary fs-4 py-2 px-4">${order.orderStatus}</span>
            </c:otherwise>
          </c:choose>
        </div>
      </div>

      <!-- Order Information Card -->
      <div class="card mb-4" style="border-radius:1rem; border: none;">
        <div class="card-header bg-primary text-white" style="border-radius: 1rem 1rem 0 0;">
          <h5 class="mb-0"><i class="fas fa-info-circle"></i> Thông Tin Đơn Hàng</h5>
        </div>
        <div class="card-body p-4">
          <div class="row g-3">
            <div class="col-md-3">
              <div class="info-box">
                <label><i class="fas fa-hashtag"></i> Mã đơn hàng</label>
                <div class="value text-primary">#${order != null ? order.orderId : '-'}</div>
              </div>
            </div>
            <div class="col-md-3">
              <div class="info-box">
                <label><i class="fas fa-user"></i> Người tạo</label>
                <div class="value">${empty creatorName ? '-' : creatorName}</div>
              </div>
            </div>
            <div class="col-md-3">
              <div class="info-box">
                <label><i class="fas fa-store"></i> Chi nhánh</label>
                <div class="value">${empty branchName ? '-' : branchName}</div>
              </div>
            </div>
            <div class="col-md-3">
              <div class="info-box">
                <label><i class="far fa-calendar-alt"></i> Ngày tạo</label>
                <div class="value">
                  <fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy" /><br>
                  <small style="color:#999;"><fmt:formatDate value="${order.createdAt}" pattern="HH:mm" /></small>
                </div>
              </div>
            </div>
            <div class="col-md-4">
              <div class="info-box">
                <label><i class="fas fa-user-circle"></i> Khách hàng</label>
                <div class="value">${empty customerName ? 'Khách vãng lai' : customerName}</div>
              </div>
            </div>
            <div class="col-md-4">
              <div class="info-box">
                <label><i class="fas fa-credit-card"></i> Phương thức thanh toán</label>
                <div class="value">
                  <c:choose>
                    <c:when test="${order.paymentMethod eq 'Cash'}">
                      <span class="badge bg-success"><i class="fas fa-money-bill-wave"></i> Tiền mặt</span>
                    </c:when>
                    <c:when test="${order.paymentMethod eq 'Bank Transfer'}">
                      <span class="badge bg-info"><i class="fas fa-university"></i> Chuyển khoản</span>
                    </c:when>
                    <c:when test="${order.paymentMethod eq 'Credit Card'}">
                      <span class="badge bg-warning text-dark"><i class="fas fa-credit-card"></i> Thẻ</span>
                    </c:when>
                    <c:otherwise>-</c:otherwise>
                  </c:choose>
                </div>
              </div>
            </div>
            <div class="col-md-4">
              <div class="info-box">
                <label><i class="fas fa-tag"></i> Tổng tiền</label>
                <div class="value text-success" style="font-size: 18px;">
                  <fmt:formatNumber value="${order.grandTotal}" type="number" groupingUsed="true" /> ₫
                </div>
              </div>
            </div>
            <c:if test="${not empty order.notes}">
            <div class="col-md-12">
              <div class="info-box">
                <label><i class="fas fa-sticky-note"></i> Ghi chú</label>
                <div class="value">${order.notes}</div>
              </div>
            </div>
            </c:if>
          </div>
        </div>
      </div>

      <!-- Products Table -->
      <div class="card" style="border-radius:1rem; border: none;">
        <div class="card-header bg-success text-white" style="border-radius: 1rem 1rem 0 0;">
          <h5 class="mb-0"><i class="fas fa-shopping-cart"></i> Danh Sách Sản Phẩm</h5>
        </div>
        <div class="card-body p-0">
          <div class="table-responsive">
            <table class="table align-middle mb-0">
              <thead>
                <tr>
                  <th style="width:80px; text-align:center;">Ảnh</th>
                  <th style="min-width:300px;">Tên sản phẩm</th>
                  <th style="width:150px; text-align:right;">Đơn giá</th>
                  <th style="width:100px; text-align:center;">Số lượng</th>
                  <th style="width:180px; text-align:right;">Thành tiền</th>
                </tr>
              </thead>
              <tbody>
              <c:forEach var="d" items="${items}" varStatus="loop">
                <c:set var="label" value="${pdLabels[d.productDetailId]}" />
                <c:set var="info" value="${pdInfos[d.productDetailId]}" />
                <tr>
                  <td style="text-align:center;">
                    <img src="${not empty info && not empty info.imageUrl ? info.imageUrl : 'https://via.placeholder.com/60x60?text=No+Image'}" 
                         alt="thumb" class="product-thumb" />
                  </td>
                  <td>
                    <div style="font-weight:600; color:#333; font-size:14px; margin-bottom:4px;">
                      <c:choose>
                        <c:when test="${not empty label}">${label}</c:when>
                        <c:otherwise>Không tìm thấy sản phẩm (#${d.productDetailId})</c:otherwise>
                      </c:choose>
                    </div>
                    <small style="color:#999;">
                      <c:if test="${not empty info && not empty info.categoryName}">
                        <i class="fas fa-tag"></i> ${info.categoryName}
                      </c:if>
                    </small>
                  </td>
                  <td style="text-align:right; color:#555; font-weight:500;">
                    <c:choose>
                      <c:when test="${not empty info && not empty info.retailPrice}">
                        <fmt:formatNumber value="${info.retailPrice}" type="number" groupingUsed="true" /> ₫
                      </c:when>
                      <c:otherwise>
                        <span class="text-muted">N/A</span>
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td style="text-align:center;">
                    <span class="badge bg-primary" style="font-size:14px; padding:6px 14px;">
                      ${d.quantity}
                    </span>
                  </td>
                  <td style="text-align:right; font-weight:700; color:#28a745; font-size:15px;">
                    <c:choose>
                      <c:when test="${not empty info && not empty info.retailPrice}">
                        <fmt:formatNumber value="${info.retailPrice * d.quantity}" type="number" groupingUsed="true" /> ₫
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
      </div>

      <!-- Payment Summary -->
      <div class="payment-summary">
        <div class="payment-item">
          <span><i class="fas fa-receipt"></i> Tổng tiền hàng:</span>
          <span style="font-size:1.1rem; font-weight:600;">
            <fmt:formatNumber value="${order.grandTotal}" type="number" groupingUsed="true" /> ₫
          </span>
        </div>
        <div class="payment-item">
          <span><i class="fas fa-hand-holding-usd"></i> Khách đưa:</span>
          <span style="font-size:1.1rem; font-weight:600;">
            <fmt:formatNumber value="${order.customerPay}" type="number" groupingUsed="true" /> ₫
          </span>
        </div>
        <div class="payment-item">
          <span><i class="fas fa-coins"></i> Tiền thừa trả khách:</span>
          <span style="font-size:1.1rem; font-weight:600;">
            <fmt:formatNumber value="${order.change}" type="number" groupingUsed="true" /> ₫
          </span>
        </div>
        <div class="payment-item">
          <span><i class="fas fa-calculator"></i> TỔNG THANH TOÁN:</span>
          <span>
            <fmt:formatNumber value="${order.grandTotal}" type="number" groupingUsed="true" /> ₫
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
