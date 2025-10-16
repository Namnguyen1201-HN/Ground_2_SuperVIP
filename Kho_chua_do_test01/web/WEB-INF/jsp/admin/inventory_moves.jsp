<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Nhập/Xuất kho</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
</head>
<body class="bg-light">
<div class="container-fluid py-3">
  <div class="d-flex justify-content-between align-items-center mb-3">
    <a class="btn btn-outline-secondary" href="DashBoard">← Dashboard</a>
    <h3 class="m-0">Danh sách yêu cầu Nhập/Xuất kho</h3>
    <a class="btn btn-primary" href="InventoryMoves?action=create">Tạo yêu cầu</a>
  </div>

  <form class="row g-2 mb-3" method="get" action="InventoryMoves">
    <div class="col-auto">
      <select name="type" class="form-select">
        <option value="" ${empty type ? 'selected' : ''}>Tất cả loại</option>
        <option value="Import" ${type=='Import' ? 'selected' : ''}>Import</option>
        <option value="Export" ${type=='Export' ? 'selected' : ''}>Export</option>
      </select>
    </div>
    <div class="col-auto"><input type="date" name="from" class="form-control" value="${from}" /></div>
    <div class="col-auto"><input type="date" name="to" class="form-control" value="${to}" /></div>
    <div class="col-auto"><button class="btn btn-outline-primary">Lọc</button></div>
  </form>

  <div class="table-responsive">
    <table class="table table-hover bg-white align-middle">
      <thead class="table-light">
      <tr>
        <th>ID</th>
        <th>Loại</th>
        <th>Ghi chú</th>
        <th>Thời gian</th>
        <th></th>
      </tr>
      </thead>
      <tbody>
      <c:forEach var="m" items="${items}">
        <tr>
          <td>${m.movementId}</td>
          <td>${m.movementType}</td>
          <td>${m.note}</td>
          <td>${m.createdAt}</td>
          <td><a class="btn btn-sm btn-outline-secondary" href="InventoryMoves?action=detail&id=${m.movementId}">Xem</a></td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
  </div>

  <div class="d-flex justify-content-end align-items-center gap-2 mt-2">
    <c:set var="pages" value="${(total + pageSize - 1) / pageSize}" />
    <c:forEach begin="1" end="${pages}" var="p">
      <a class="btn btn-sm ${p==page? 'btn-primary':'btn-outline-primary'}" href="InventoryMoves?page=${p}&type=${type}&from=${from}&to=${to}">${p}</a>
    </c:forEach>
  </div>
</div>
</body>
</html>


