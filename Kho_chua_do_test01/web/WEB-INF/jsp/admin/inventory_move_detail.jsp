<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Chi tiết yêu cầu kho</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
</head>
<body class="bg-light">
<div class="container py-3">
  <a class="btn btn-outline-secondary mb-3" href="InventoryMoves">← Danh sách</a>
  <h4 class="mb-3">Yêu cầu #${move.movementId} - ${move.movementType}</h4>
  <div class="card mb-3"><div class="card-body">
    <form class="row g-2 align-items-end" method="post" action="InventoryMoves">
      <input type="hidden" name="action" value="update" />
      <input type="hidden" name="id" value="${move.movementId}" />
      <div class="col-md-3">
        <label class="form-label">Loại</label>
        <input class="form-control" value="${move.movementType}" disabled />
      </div>
      <div class="col-md-6">
        <label class="form-label">Ghi chú</label>
        <input class="form-control" name="note" value="${move.note}" />
      </div>
      <div class="col-md-3 d-grid">
        <button class="btn btn-primary">Cập nhật</button>
      </div>
    </form>
    <div class="mt-2">Tạo lúc: ${move.createdAt}</div>
  </div></div>

  <div class="table-responsive">
    <table class="table table-hover bg-white align-middle">
      <thead class="table-light"><tr><th style="width:64px"></th><th>Sản phẩm</th><th>Số lượng</th></tr></thead>
      <tbody>
      <c:forEach var="d" items="${move.details}">
        <tr>
          <td>
            <c:set var="info" value="${pdInfos[d.productDetailId]}" />
            <img src="${empty info.imageUrl ? 'https://via.placeholder.com/56x56?text=%20' : info.imageUrl}"
                 alt="thumb" class="rounded" style="width:56px;height:56px;object-fit:cover;" />
          </td>
          <td>
            <c:set var="info" value="${pdInfos[d.productDetailId]}" />
            <c:choose>
              <c:when test="${not empty info}">${info.name} - ${info.sku} (#${d.productDetailId})</c:when>
              <c:otherwise>#${d.productDetailId}</c:otherwise>
            </c:choose>
          </td>
          <td>${d.quantity}</td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
  </div>

  <form method="post" action="InventoryMoves" class="text-end mt-3">
    <input type="hidden" name="action" value="delete" />
    <input type="hidden" name="id" value="${move.movementId}" />
    <button class="btn btn-outline-danger" onclick="return confirm('Xóa yêu cầu này?');">Xóa yêu cầu</button>
  </form>
</div>
</body>
</html>


