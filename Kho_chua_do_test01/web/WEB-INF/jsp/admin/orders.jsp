<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.Order" %>
<%@ page import="java.util.Map" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <title>Đơn hàng</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
  </head>
  <body class="bg-light">
    <div class="container-fluid py-3">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
          <a class="btn btn-outline-secondary" href="DashBoard">← Dashboard</a>
        </div>
        <h3 class="m-0">Quản lý đơn hàng</h3>
        <div></div>
      </div>

      <form class="row g-2 justify-content-end mb-2" method="get" action="Orders">
        <div class="col-auto">
          <input type="text" name="q" value="${fKw}" placeholder="Tìm ID / phương thức" class="form-control" style="max-width:280px;" />
        </div>
        <div class="col-auto">
          <input type="text" name="customer" value="${fCustomer}" placeholder="Tên khách hàng" class="form-control" style="max-width:220px;" />
        </div>
        <div class="col-auto">
          <input type="text" name="product" value="${fProduct}" placeholder="Tên sản phẩm" class="form-control" style="max-width:220px;" />
        </div>
        <div class="col-auto">
        <select name="st" class="form-select" style="max-width:200px;">
          <option value="" ${empty fStatus ? 'selected' : ''}>Tất cả trạng thái</option>
          <option value="Pending" ${fStatus=='Pending' ? 'selected' : ''}>Pending</option>
          <option value="Completed" ${fStatus=='Completed' ? 'selected' : ''}>Completed</option>
          <option value="Cancelled" ${fStatus=='Cancelled' ? 'selected' : ''}>Cancelled</option>
        </select>
        </div>
        <div class="col-auto">
        <select name="branch" class="form-select" style="max-width:220px;">
          <option value="" ${empty fBranch ? 'selected' : ''}>Tất cả chi nhánh</option>
          <c:forEach var="b" items="${allBranches}">
            <option value="${b.branchId}" ${fBranch==b.branchId ? 'selected' : ''}>${b.branchName}</option>
          </c:forEach>
        </select>
        </div>
        <div class="col-auto">
          <input type="date" name="from" class="form-control" value="${fFrom}" />
        </div>
        <div class="col-auto">
          <input type="date" name="to" class="form-control" value="${fTo}" />
        </div>
        <div class="col-auto">
          <button class="btn btn-outline-primary">Lọc</button>
        </div>
      </form>

      <div class="table-responsive">
        <table class="table table-hover align-middle bg-white" id="ordersTable" style="box-shadow:0 4px 24px rgba(59,130,246,0.07); border-radius:1rem; overflow:hidden;">
          <thead class="table-light">
            <tr>
              <th>ID</th>
              <th>Chi nhánh</th>
              <th>Người tạo</th>
              <th>Trạng thái</th>
              <th>Ngày tạo</th>
              <th>Thanh toán</th>
              <th>Tổng</th>
              <th style="width:220px">Cập nhật trạng thái</th>
            </tr>
          </thead>
          <tbody>
          <c:forEach var="o" items="${orders}">
            <tr>
              <td>${o.orderId}</td>
              <td>
                <c:choose>
                  <c:when test="${o.branchId != null && branchNames[o.branchId] != null}">${branchNames[o.branchId]}</c:when>
                  <c:when test="${o.branchId != null}">#${o.branchId}</c:when>
                  <c:otherwise>-</c:otherwise>
                </c:choose>
              </td>
              <td>${creatorNames[o.createdBy] != null ? creatorNames[o.createdBy] : ('User #' += o.createdBy)}</td>
              <td>
                <c:choose>
                  <c:when test="${o.orderStatus eq 'Completed'}"><span class="badge bg-success">${o.orderStatus}</span></c:when>
                  <c:when test="${o.orderStatus eq 'Cancelled'}"><span class="badge bg-danger">${o.orderStatus}</span></c:when>
                  <c:otherwise><span class="badge bg-secondary">${o.orderStatus}</span></c:otherwise>
                </c:choose>
              </td>
              <td>${o.createdAt}</td>
              <td>${empty o.paymentMethod ? '-' : o.paymentMethod}</td>
              <td>${empty o.grandTotal ? 0 : o.grandTotal}</td>
              <td>
                <form class="d-flex gap-2 align-items-center" method="post" action="Orders">
                  <input type="hidden" name="action" value="updateStatus" />
                  <input type="hidden" name="orderId" value="${o.orderId}" />
                  <c:set var="locked" value="${o.orderStatus eq 'Completed' or o.orderStatus eq 'Cancelled'}" />
                  <select class="form-select form-select-sm" name="status" style="max-width:140px;" ${locked ? 'disabled' : ''}>
                    <option ${o.orderStatus eq 'Pending' ? 'selected' : ''}>Pending</option>
                    <option ${o.orderStatus eq 'Completed' ? 'selected' : ''}>Completed</option>
                    <option ${o.orderStatus eq 'Cancelled' ? 'selected' : ''}>Cancelled</option>
                  </select>
                  <button class="btn btn-sm btn-primary" ${locked ? 'disabled' : ''}>Lưu</button>
                  <a class="btn btn-sm btn-outline-secondary" href="Orders?action=detail&id=${o.orderId}">Xem</a>
                </form>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </div>
      <div class="d-flex justify-content-end align-items-center gap-2 mt-2">
        <c:set var="pages" value="${(total + pageSize - 1) / pageSize}" />
        <c:forEach begin="1" end="${pages}" var="p">
          <a class="btn btn-sm ${p==page? 'btn-primary':'btn-outline-primary'}" href="Orders?page=${p}&q=${fKw}&st=${fStatus}&branch=${fBranch}">${p}</a>
        </c:forEach>
      </div>

      <!-- Create Order Modal -->
      <div class="modal fade" id="createOrderModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
          <div class="modal-content" style="border-radius:1rem;">
            <form method="post" action="Orders">
              <input type="hidden" name="action" value="create" />
              <div class="modal-header">
                <h5 class="modal-title">Tạo đơn hàng</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
              </div>
              <div class="modal-body">
                <div class="mb-3">
                  <label class="form-label">Chi nhánh</label>
                  <select class="form-select" name="branchId">
                    <option value="">-- chọn chi nhánh --</option>
                    <%
                      java.util.List<Model.Branch> allBranches = (java.util.List<Model.Branch>) request.getAttribute("allBranches");
                      if (allBranches != null) {
                        for (Model.Branch b : allBranches) {
                    %>
                      <option value="<%= b.getBranchId() %>"><%= b.getBranchName() %></option>
                    <%
                        }
                      }
                    %>
                  </select>
                </div>
                <div class="mb-3">
                  <label class="form-label">Khách hàng ID</label>
                  <input type="number" class="form-control" name="customerId" placeholder="Tùy chọn" />
                </div>
                <div class="mb-3">
                  <label class="form-label">Trạng thái</label>
                  <select class="form-select" name="status">
                    <option>Pending</option>
                    <option>Completed</option>
                    <option>Cancelled</option>
                  </select>
                </div>
                <div class="mb-3">
                  <label class="form-label">Phương thức thanh toán</label>
                  <input class="form-control" name="paymentMethod" placeholder="Cash/Banking..." />
                </div>
                <div class="mb-3">
                  <label class="form-label">Ghi chú</label>
                  <textarea class="form-control" name="notes" rows="2"></textarea>
                </div>
                <div class="border rounded p-2">
                  <div class="small text-muted mb-2">Thêm sản phẩm (tùy chọn)</div>
                  <div id="items">
                    <div class="row g-2 align-items-end mb-2">
                      <div class="col-6 position-relative">
                        <label class="form-label">Product detail</label>
                        <input class="form-control pd-input" placeholder="Gõ tên/sku để tìm..." autocomplete="off" />
                        <input type="hidden" name="productDetailId" />
                        <div class="pd-dropdown list-group" style="position:absolute; z-index:1056; width:100%; top:100%; left:0; display:none;"></div>
                      </div>
                      <div class="col-4">
                        <label class="form-label">Số lượng</label>
                        <input type="number" class="form-control" name="quantity" />
                      </div>
                      <div class="col-2 d-grid">
                        <button type="button" class="btn btn-outline-secondary" id="addItem">+</button>
                      </div>
                    </div>
                  </div>
                  <!-- results rendered dynamically into .pd-dropdown -->
                </div>
              </div>
              <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Hủy</button>
                <button type="submit" class="btn btn-primary">Tạo</button>
              </div>
            </form>
          </div>
        </div>
      </div>

      <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
      <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
      <script>
        const kw = document.getElementById('kw');
        const status = document.getElementById('status');
        function applyFilter(){
          const k = (kw.value||'').toLowerCase().trim();
          const st = status.value;
          document.querySelectorAll('#ordersTable tbody tr').forEach(tr => {
            const id = tr.children[0].textContent.toLowerCase();
            const pay = tr.children[5].textContent.toLowerCase();
            const s = tr.children[3].innerText.trim();
            let ok = true;
            if (k) ok = id.includes(k) || pay.includes(k);
            if (ok && st && s !== st) ok = false;
            tr.style.display = ok ? '' : 'none';
          });
        }
        kw.addEventListener('input', applyFilter);
        status.addEventListener('change', applyFilter);
        const items = document.getElementById('items');
        const addBtn = document.getElementById('addItem');
        if (addBtn) {
          addBtn.addEventListener('click', () => {
            const row = document.createElement('div');
            row.className = 'row g-2 align-items-end mb-2';
            row.innerHTML = `
              <div class=\"col-6\">
                <label class=\"form-label\">Product detail</label>
                <input list=\"pdOptions\" class=\"form-control pd-input\" placeholder=\"Gõ tên/sku để tìm...\" />
                <input type=\"hidden\" name=\"productDetailId\" />
              </div>
              <div class="col-4">
                <label class="form-label">Số lượng</label>
                <input type="number" class="form-control" name="quantity" />
              </div>
              <div class="col-2 d-grid">
                <button type="button" class="btn btn-outline-danger btn-remove">-</button>
              </div>`;
            items.appendChild(row);
            row.querySelector('.btn-remove').addEventListener('click', () => row.remove());
            bindPdAutocomplete(row.querySelector('.pd-input'), row.querySelector('input[type="hidden"]'));
          });
        }

        // Autocomplete dropdown for product detail
        async function fetchPd(keyword){
          const res = await fetch('Orders?action=productDetailSearch&q=' + encodeURIComponent(keyword||''));
          if (!res.ok) return [];
          return await res.json();
        }
        function bindPdAutocomplete(visibleInput, hiddenInput){
          const dropdown = visibleInput.parentElement.querySelector('.pd-dropdown');
          let last = '';
          let timer;
          function render(items){
            dropdown.innerHTML = '';
            if (!items.length) { dropdown.style.display = 'none'; return; }
            items.forEach(it => {
              const a = document.createElement('a');
              a.href = '#';
              a.className = 'list-group-item list-group-item-action';
              a.textContent = it.label + ' #' + it.id;
              a.addEventListener('click', (e) => {
                e.preventDefault();
                visibleInput.value = it.label + ' #' + it.id;
                hiddenInput.value = it.id;
                dropdown.style.display = 'none';
              });
              dropdown.appendChild(a);
            });
            dropdown.style.display = 'block';
          }
          visibleInput.addEventListener('input', () => {
            const val = visibleInput.value.trim();
            if (val === last) return; last = val;
            clearTimeout(timer);
            timer = setTimeout(async () => {
              const list = await fetchPd(val);
              render(list);
            }, 250);
          });
          visibleInput.addEventListener('blur', () => setTimeout(() => dropdown.style.display = 'none', 150));
          visibleInput.addEventListener('focus', async () => {
            if (visibleInput.value.trim() === '') {
              const list = await fetchPd('');
              render(list);
            }
          });
        }
        document.querySelectorAll('.pd-input').forEach(inp => {
          bindPdAutocomplete(inp, inp.parentElement.querySelector('input[type="hidden"]'));
        });

        // SweetAlert2 delete confirm
        document.querySelectorAll('.btn-delete').forEach(btn => {
          btn.addEventListener('click', (e) => {
            e.preventDefault();
            const id = btn.getAttribute('data-id');
            Swal.fire({
              title: 'Xóa đơn hàng?',
              text: 'Hành động này không thể hoàn tác!',
              icon: 'warning',
              showCancelButton: true,
              confirmButtonColor: '#d33',
              cancelButtonColor: '#3085d6',
              confirmButtonText: 'Xóa',
              cancelButtonText: 'Hủy'
            }).then((result) => {
              if (result.isConfirmed) {
                window.location.href = 'Orders?action=delete&id=' + id;
              }
            });
          });
        });

        // Show notifications
        const msg = '<%= request.getAttribute("msg") != null ? request.getAttribute("msg") : "" %>';
        if (msg === 'created') {
          Swal.fire({ icon: 'success', title: 'Đã tạo đơn hàng', timer: 1500, showConfirmButton: false });
        } else if (msg === 'deleted') {
          Swal.fire({ icon: 'success', title: 'Đã xóa đơn hàng', timer: 1500, showConfirmButton: false });
        }
      </script>
    </div>
  </body>
  </html>


