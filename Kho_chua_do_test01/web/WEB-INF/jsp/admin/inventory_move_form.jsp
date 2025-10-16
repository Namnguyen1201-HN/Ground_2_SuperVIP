<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Tạo yêu cầu Nhập/Xuất kho</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
</head>
<body class="bg-light">
<div class="container py-3">
  <a class="btn btn-outline-secondary mb-3" href="InventoryMoves">← Danh sách</a>
  <h4 class="mb-3">Tạo yêu cầu Nhập/Xuất kho</h4>
  <form method="post" action="InventoryMoves">
    <input type="hidden" name="action" value="create" />
    <div class="row g-3">
      <div class="col-md-4">
        <label class="form-label">Loại yêu cầu</label>
        <select class="form-select" name="movementType">
          <option>Import</option>
          <option>Export</option>
        </select>
      </div>
      <div class="col-md-4">
        <label class="form-label">Từ Chi nhánh</label>
        <select class="form-select" name="fromBranchId">
          <option value="">-- chọn --</option>
          <c:forEach var="b" items="${branches}"><option value="${b.branchId}">${b.branchName}</option></c:forEach>
        </select>
      </div>
      <div class="col-md-4">
        <label class="form-label">Đến Chi nhánh</label>
        <select class="form-select" name="toBranchId">
          <option value="">-- chọn --</option>
          <c:forEach var="b" items="${branches}"><option value="${b.branchId}">${b.branchName}</option></c:forEach>
        </select>
      </div>
      <div class="col-12">
        <label class="form-label">Ghi chú</label>
        <textarea class="form-control" name="note" rows="2"></textarea>
      </div>
    </div>

    <hr class="my-4"/>
    <h6>Danh sách sản phẩm</h6>
    <div id="items">
      <div class="row g-2 align-items-end mb-2">
        <div class="col-md-1">
          <img id="thumb-0" src="https://via.placeholder.com/56x56?text=%20" class="rounded" style="width:56px;height:56px;object-fit:cover;" />
        </div>
        <div class="col-md-5">
          <label class="form-label">ProductDetailID / Code</label>
          <input class="form-control pdid" name="productDetailId" placeholder="Nhập ProductDetailID hoặc mã (ví dụ: SN40)" />
          <label class="form-label mt-2">Sản phẩm</label>
          <input class="form-control pname" placeholder="Tên sản phẩm sẽ hiện ở đây" readonly />
        </div>
        <div class="col-md-4">
          <label class="form-label">Số lượng</label>
          <input class="form-control" name="quantity" />
        </div>
        <div class="col-md-2 d-grid">
          <button type="button" id="addRow" class="btn btn-outline-secondary">+</button>
        </div>
      </div>
    </div>
    <div class="text-end mt-3">
      <button class="btn btn-primary" type="submit">Tạo yêu cầu</button>
    </div>
  </form>
</div>

<script>
  document.getElementById('addRow').addEventListener('click', () => {
    const wrap = document.getElementById('items');
    const row = document.createElement('div');
    row.className = 'row g-2 align-items-end mb-2';
    row.innerHTML = `
      <div class="col-md-1"><img src="https://via.placeholder.com/56x56?text=%20" class="rounded" style="width:56px;height:56px;object-fit:cover;" /></div>
      <div class="col-md-5">
        <input class="form-control pdid" name="productDetailId" placeholder="ProductDetailID hoặc mã" />
        <input class="form-control pname mt-2" placeholder="Tên sản phẩm" readonly />
      </div>
      <div class="col-md-4">
        <input class="form-control" name="quantity" placeholder="Số lượng" />
      </div>
      <div class="col-md-2 d-grid">
        <button type="button" class="btn btn-outline-danger btn-remove">-</button>
      </div>`;
    wrap.appendChild(row);
    row.querySelector('.btn-remove').addEventListener('click', ()=> row.remove());
    bindPdid(row);
  });

  async function fetchInfo(pdid){
    const res = await fetch('InventoryMoves?action=productInfo&id=' + encodeURIComponent(pdid));
    if (!res.ok) return null;
    return await res.json();
  }

  function bindPdid(scope){
    const input = (scope || document).querySelector('.pdid');
    const nameEl = (scope || document).querySelector('.pname');
    const imgEl = (scope || document).querySelector('img');
    input.addEventListener('blur', async () => {
      const id = input.value.trim();
      if (!id) return;
      const data = await fetchInfo(id);
      if (data && (data.name || data.sku)) {
        nameEl.value = (data.name||'') + (data.sku?(' - ' + data.sku):'');
        if (data.image) imgEl.src = data.image;
      } else {
        nameEl.value = 'Không tìm thấy sản phẩm';
        imgEl.src = 'https://via.placeholder.com/56x56?text=%20';
      }
    });
  }

  // bind for initial row
  bindPdid();
</script>
</body>
</html>


