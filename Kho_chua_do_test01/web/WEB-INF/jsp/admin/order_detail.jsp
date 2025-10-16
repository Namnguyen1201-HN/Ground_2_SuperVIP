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
  </head>
  <body class="bg-light">
    <div class="container py-3">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <a class="btn btn-outline-secondary" href="Orders">← Quay lại danh sách</a>
        <h4 class="m-0">Chi tiết đơn hàng</h4>
        <div></div>
      </div>

      <div class="card shadow-sm mb-3" style="border-radius:1rem;">
        <div class="card-body">
          <div class="row g-3">
            <div class="col-md-3">
              <div class="text-muted small">Mã đơn</div>
              <div class="fw-semibold">${order != null ? order.orderId : '-'}</div>
            </div>
            <div class="col-md-3">
              <div class="text-muted small">Người tạo</div>
              <div class="fw-semibold">${empty creatorName ? '-' : creatorName}</div>
            </div>
            <div class="col-md-3">
              <div class="text-muted small">Chi nhánh</div>
              <div class="fw-semibold">${empty branchName ? '-' : branchName}</div>
            </div>
            <div class="col-md-3">
              <div class="text-muted small">Trạng thái</div>
              <div>
                <c:choose>
                  <c:when test="${order.orderStatus eq 'Completed'}">
                    <span class="badge bg-success">${order.orderStatus}</span>
                  </c:when>
                  <c:when test="${order.orderStatus eq 'Cancelled'}">
                    <span class="badge bg-danger">${order.orderStatus}</span>
                  </c:when>
                  <c:otherwise>
                    <span class="badge bg-secondary">${order.orderStatus}</span>
                  </c:otherwise>
                </c:choose>
              </div>
            </div>
          </div>
          <!-- View-only: branch update disabled -->
        </div>
      </div>

      <div class="card shadow-sm" style="border-radius:1rem;">
        <div class="card-header bg-white">
          <strong>Danh sách sản phẩm</strong>
        </div>
        <div class="card-body">
          <div class="table-responsive">
            <table class="table align-middle">
              <thead>
                <tr>
                  <th style="width:64px"></th>
                  <th>Sản phẩm</th>
                  <th style="width:160px">Số lượng</th>
                </tr>
              </thead>
              <tbody>
              <c:forEach var="d" items="${items}">
                <c:set var="label" value="${pdLabels[d.productDetailId]}" />
                <c:set var="info" value="${pdInfos[d.productDetailId]}" />
                <tr>
                  <td>
                    <img src="${empty info.imageUrl ? 'https://via.placeholder.com/56x56?text=%20' : info.imageUrl}" alt="thumb" class="rounded" style="width:56px;height:56px;object-fit:cover;" />
                  </td>
                  <td>
                    <c:choose>
                      <c:when test="${not empty label}">${label}</c:when>
                      <c:otherwise>Không tìm thấy sản phẩm (#${d.productDetailId})</c:otherwise>
                    </c:choose>
                  </td>
                  <td>${d.quantity}</td>
                </tr>
              </c:forEach>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <script>
      const msg = '<%= request.getAttribute("msg") != null ? request.getAttribute("msg") : "" %>';
      if (msg === 'branch-updated') {
        Swal.fire({ icon: 'success', title: 'Đã cập nhật chi nhánh', timer: 1500, showConfirmButton: false });
      } else if (msg) {
        Swal.fire({ icon: 'success', title: msg, timer: 1500, showConfirmButton: false });
      }
    </script>
  </body>
</html>
