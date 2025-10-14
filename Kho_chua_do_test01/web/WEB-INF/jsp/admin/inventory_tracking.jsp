<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Theo dõi nhập xuất</title>
        <link href="css/admin/NhanVien.css" rel="stylesheet" type="text/css"/>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    </head>
    <body>
        <div class="container-fluid">
            <%@ include file="../admin/header_admin.jsp" %>
            <div class="content">
                <main class="main-content">
                    <div class="toolbar d-flex justify-content-between align-items-center">
                        <div class="d-flex gap-2">
                            <button class="btn btn-outline-secondary" type="button" onclick="location.href='Transactions'">← Quay lại</button>
                        </div>
                        <h2 class="m-0">Giao dịch - Theo dõi nhập xuất</h2>
                        <div></div>
                    </div>

                    <div class="row g-3">
                        <div class="col-12">
                            <div class="d-flex justify-content-between align-items-center">
                                <h5 class="m-0">Đơn nhập (Receipts)</h5>
                                <div class="d-flex gap-2">
                                    <input type="text" id="searchKeyword" class="form-control" placeholder="Tìm theo ID, khách hàng..." style="width:260px;" />
                                    <input type="date" id="fromDate" class="form-control" />
                                    <input type="date" id="toDate" class="form-control" />
                                    <button class="btn btn-outline-secondary" id="clearFilter">Xóa lọc</button>
                                </div>
                            </div>
                    <!-- Modal chi tiết -->
                    <div class="modal fade" id="receiptDetailsModal" tabindex="-1" aria-hidden="true">
                      <div class="modal-dialog modal-lg">
                        <div class="modal-content">
                          <div class="modal-header">
                            <h5 class="modal-title">Chi tiết đơn nhập</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                          </div>
                          <div class="modal-body">
                            <table class="table table-sm">
                              <thead>
                                <tr>
                                  <th>Sản phẩm</th>
                                  <th>Nhà cung cấp</th>
                                  <th>SL</th>
                                  <th>Đơn giá</th>
                                  <th>Thành tiền</th>
                                </tr>
                              </thead>
                              <tbody id="modalBodyRows"></tbody>
                            </table>
                          </div>
                          <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                          </div>
                        </div>
                      </div>
                    </div>
                    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
                    <script>
                      const ctx = '<%= request.getContextPath() %>';
                      // View details modal
                      document.querySelectorAll('#receiptsTable .view-details').forEach(function(btn){
                        btn.addEventListener('click', function(){
                          const rid = this.getAttribute('data-id');
                          fetch(ctx + '/InventoryTracking?ajax=details&receiptId=' + rid)
                            .then(r => r.json())
                            .then(data => {
                              const body = document.getElementById('modalBodyRows');
                              body.innerHTML = '';
                              data.forEach(item => {
                                const tr = document.createElement('tr');
                                tr.innerHTML = `<td>${item.productName}</td><td>${item.supplierName}</td><td>${item.quantity}</td><td>${item.unitPrice}</td><td>${item.total}</td>`;
                                body.appendChild(tr);
                              });
                              const modal = new bootstrap.Modal(document.getElementById('receiptDetailsModal'));
                              modal.show();
                            })
                            .catch(err => {
                              const body = document.getElementById('modalBodyRows');
                              body.innerHTML = `<tr><td colspan="5">Không tải được chi tiết. Vui lòng thử lại.</td></tr>`;
                              const modal = new bootstrap.Modal(document.getElementById('receiptDetailsModal'));
                              modal.show();
                              console.error('Fetch details failed', err);
                            });
                        });
                      });

                      // Client-side search & filter
                      const keyword = document.getElementById('searchKeyword');
                      const fromDate = document.getElementById('fromDate');
                      const toDate = document.getElementById('toDate');
                      const clearBtn = document.getElementById('clearFilter');

                      function normalizeDateStr(s){
                        if(!s) return null;
                        return new Date(s);
                      }

                      function applyFilter(){
                        const kw = (keyword.value || '').toLowerCase().trim();
                        const fd = normalizeDateStr(fromDate.value);
                        const td = normalizeDateStr(toDate.value);
                        document.querySelectorAll('#receiptsTable tbody tr').forEach(tr => {
                          const id = tr.children[0].textContent.trim().toLowerCase();
                          const customer = tr.children[1].textContent.trim().toLowerCase();
                          const dateStr = tr.children[2].textContent.trim();
                          const rowDate = new Date(dateStr);
                          let ok = true;
                          if (kw) {
                            ok = id.includes(kw) || customer.includes(kw);
                          }
                          if (ok && fd && !(rowDate >= fd)) ok = false;
                          if (ok && td && !(rowDate <= td)) ok = false;
                          tr.style.display = ok ? '' : 'none';
                        });
                      }

                      [keyword, fromDate, toDate].forEach(el => el.addEventListener('input', applyFilter));
                      clearBtn.addEventListener('click', () => { keyword.value=''; fromDate.value=''; toDate.value=''; applyFilter(); });
                    </script>
                            <table class="table table-striped align-middle table-responsive" id="receiptsTable">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Khách hàng</th>
                                        <th>Ngày</th>
                                        <th>Tổng</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        java.util.List<Model.Receipt> receipts = (java.util.List<Model.Receipt>) request.getAttribute("receipts");
                                        if (receipts != null) {
                                            for (Model.Receipt r : receipts) {
                                    %>
                                    <tr data-id="<%= r.getReceiptId() %>">
                                        <td><%= r.getReceiptId() %></td>
                                        <td><%= r.getCustomerName() != null ? r.getCustomerName() : "-" %></td>
                                        <td><%= r.getCreatedAt() %></td>
                                        <td><%= r.getGrandTotal() %></td>
                                        <td>
                                            <a class="btn btn-outline-primary btn-sm" href="<%= request.getContextPath() %>/InventoryTracking?id=<%= r.getReceiptId() %>">Xem chi tiết</a>
                                        </td>
                                    </tr>
                                    <%
                                            }
                                        }
                                    %>
                                </tbody>
                            </table>
                        </div>
                        
                    </div>
                    
                </main>
            </div>
        </div>
    </body>
</html>


