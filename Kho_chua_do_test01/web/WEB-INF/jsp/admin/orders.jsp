<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.Order" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Đơn hàng</title>
        <link href="css/admin/NhanVien.css" rel="stylesheet" type="text/css"/>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    </head>
    <body>
        <div class="container-fluid">
            <%@ include file="../includes/header.jsp" %>
            <div class="content">
                <main class="main-content">
                    <div class="toolbar d-flex justify-content-between align-items-center">
                        <div class="d-flex gap-2">
                            <button class="btn btn-outline-secondary" type="button" onclick="location.href='Transactions'">
                                <i class="fa fa-arrow-left"></i> Quay lại
                            </button>
                        </div>
                        <h2 class="m-0">Giao dịch - Đơn hàng</h2>
                        <div class="d-flex gap-2">
                            <button class="btn btn-primary" type="button" data-bs-toggle="modal" data-bs-target="#createOrderModal">
                                <i class="fa fa-plus"></i> Tạo đơn
                            </button>
                        </div>
                    </div>
                    <div class="d-flex justify-content-end mb-2 gap-2">
                        <input type="text" id="orderSearch" class="form-control" placeholder="Tìm theo ID, nhà cung cấp" style="width:320px;" />
                        <select id="statusFilter" class="form-select" style="width:180px;">
                            <option value="">Tất cả trạng thái</option>
                            <option value="Pending">Pending</option>
                            <option value="Completed">Completed</option>
                            <option value="Cancelled">Cancelled</option>
                        </select>
                    </div>
                    <table class="emp-table table table-hover align-middle" id="ordersTable">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Nhà cung cấp</th>
                                <th>Người tạo đơn</th>
                                <th>Ngày</th>
                                <th>Trạng thái</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                List<Order> orders = (List<Order>) request.getAttribute("orders");
                                if (orders != null) {
                                    for (Order o : orders) {
                            %>
                            <tr>
                                <td><%= o.getOrderId() %></td>
                                <td><%= new DAL.SupplierDAO().getSupplierById(o.getSupplierId()).getSupplierName() %></td>
                                <td><%= new DAL.UserDAO().getAllUsers().stream().filter(u -> u.getUserId() == o.getOrderedBy()).findFirst().map(u -> u.getFullName()).orElse(String.valueOf(o.getOrderedBy())) %></td>
                                <td><%= o.getOrderDate() %></td>
                                <td><%= o.getStatus() %></td>
                                <td>
                                    <div class="d-flex gap-2">
                                        <a class="btn btn-outline-secondary btn-sm" href="<%= request.getContextPath() %>/Orders?action=detail&id=<%= o.getOrderId() %>">Xem chi tiết</a>
                                        <button class="btn btn-outline-primary btn-sm" type="button" data-bs-toggle="modal" data-bs-target="#editStatusModal" data-id="<%= o.getOrderId() %>">Sửa</button>
                                        <form class="d-inline" action="Orders" method="get" onsubmit="return confirm('Xóa đơn hàng này?');">
                                            <input type="hidden" name="action" value="delete" />
                                            <input type="hidden" name="id" value="<%= o.getOrderId() %>" />
                                            <button class="btn btn-danger btn-sm" type="submit">Xóa</button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                            <%
                                    }
                                }
                            %>
                        </tbody>
                    </table>

                    <!-- Create Order Modal -->
                    <div class="modal fade" id="createOrderModal" tabindex="-1" aria-hidden="true">
                      <div class="modal-dialog">
                        <div class="modal-content">
                          <div class="modal-header">
                            <h5 class="modal-title">Tạo đơn hàng</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                          </div>
                          <form action="Orders" method="post">
                            <input type="hidden" name="action" value="create" />
                            <div class="modal-body">
                              <div class="mb-3">
                                <label class="form-label">Nhà cung cấp</label>
                                <select class="form-select" name="supplierId" required>
                                  <option value="" disabled selected>-- Chọn nhà cung cấp --</option>
                                  <%
                                    java.util.List<Model.Supplier> sups = (java.util.List<Model.Supplier>) request.getAttribute("suppliers");
                                    if (sups != null) {
                                      for (Model.Supplier s : sups) {
                                  %>
                                  <option value="<%= s.getSupplierId() %>"><%= s.getSupplierName() %></option>
                                  <%
                                      }
                                    }
                                  %>
                                </select>
                              </div>
                              <div class="mb-3">
                                <label class="form-label">Sản phẩm</label>
                                <div id="orderItems">
                                  <div class="row g-2 align-items-center order-item">
                                    <div class="col-4">
                                      <select class="form-select" name="productId" required>
                                        <option value="" disabled selected>-- Chọn sản phẩm --</option>
                                        <%
                                          java.util.List<Model.Product> prods = (java.util.List<Model.Product>) request.getAttribute("products");
                                          if (prods != null) {
                                            for (Model.Product p : prods) {
                                        %>
                                        <option value="<%= p.getProductId() %>"><%= p.getProductName() %></option>
                                        <%
                                            }
                                          }
                                        %>
                                      </select>
                                    </div>
                                    <div class="col-3">
                                      <input class="form-control" type="number" name="quantity" value="1" min="1" required />
                                    </div>
                                    <div class="col-3">
                                      <input class="form-control" type="number" name="price" value="0" min="0" step="1" required />
                                    </div>
                                    <div class="col-2">
                                      <button class="btn btn-outline-danger" type="button" onclick="this.closest('.order-item').remove()">Xóa</button>
                                    </div>
                                  </div>
                                </div>
                                <button class="btn btn-outline-primary mt-2" type="button" id="addItemBtn">Thêm dòng</button>
                              </div>
                              <div class="mb-3">
                                <label class="form-label">Trạng thái</label>
                                <select class="form-select" name="status">
                                  <option value="Pending">Pending</option>
                                  <option value="Completed">Completed</option>
                                  <option value="Cancelled">Cancelled</option>
                                </select>
                              </div>
                            </div>
                            <div class="modal-footer">
                              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                              <button type="submit" class="btn btn-primary">Lưu</button>
                            </div>
                          </form>
                        </div>
                      </div>
                    </div>

                    <!-- Edit Status Modal -->
                    <div class="modal fade" id="editStatusModal" tabindex="-1" aria-hidden="true">
                      <div class="modal-dialog">
                        <div class="modal-content">
                          <div class="modal-header">
                            <h5 class="modal-title">Cập nhật trạng thái</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                          </div>
                          <form action="Orders" method="post">
                            <input type="hidden" name="action" value="updateStatus" />
                            <input type="hidden" id="edit-id" name="id" />
                            <div class="modal-body">
                              <div class="mb-3">
                                <label class="form-label">Trạng thái</label>
                                <select class="form-select" name="status">
                                  <option value="Pending">Pending</option>
                                  <option value="Completed">Completed</option>
                                  <option value="Cancelled">Cancelled</option>
                                </select>
                              </div>
                            </div>
                            <div class="modal-footer">
                              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                              <button type="submit" class="btn btn-primary">Lưu</button>
                            </div>
                          </form>
                        </div>
                      </div>
                    </div>
                    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
                    <script>
                        // price map from products
                        const productPrice = {
                        <% java.util.List<Model.Product> pmap2 = (java.util.List<Model.Product>) request.getAttribute("products");
                           if (pmap2 != null) {
                             for (int i = 0; i < pmap2.size(); i++) {
                               Model.Product p = pmap2.get(i);
                        %>
                           <%= p.getProductId() %>: <%= (int) p.getPrice() %><%= (i < pmap2.size()-1) ? "," : "" %>
                        <%   }
                           } %>
                        };
                        // client-side search/filter
                        const orderSearch = document.getElementById('orderSearch');
                        const statusFilter = document.getElementById('statusFilter');
                        function applyOrderFilter(){
                            const kw = (orderSearch.value || '').toLowerCase().trim();
                            const st = statusFilter.value;
                            document.querySelectorAll('#ordersTable tbody tr').forEach(tr => {
                                const id = tr.children[0].textContent.toLowerCase();
                                const supplier = tr.children[1].textContent.toLowerCase();
                                const status = tr.children[4].textContent.trim();
                                let ok = true;
                                if (kw) ok = id.includes(kw) || supplier.includes(kw);
                                if (ok && st && status !== st) ok = false;
                                tr.style.display = ok ? '' : 'none';
                            });
                        }
                        orderSearch.addEventListener('input', applyOrderFilter);
                        statusFilter.addEventListener('change', applyOrderFilter);
                        // Add dynamic order detail rows
                        document.getElementById('addItemBtn').addEventListener('click', function(){
                            const container = document.getElementById('orderItems');
                            const div = document.createElement('div');
                            div.className = 'row g-2 align-items-center order-item';
                            div.innerHTML = `
                                <div class="col-4">
                                  <select class="form-select" name="productId" required>
                                    <option value="" disabled selected>-- Chọn sản phẩm --</option>
                                    <% if (pmap2 != null) { for (Model.Product p : pmap2) { %>
                                      <option value="<%= p.getProductId() %>"><%= p.getProductName() %></option>
                                    <% } } %>
                                  </select>
                                </div>
                                <div class="col-3">
                                  <input class="form-control" type="number" name="quantity" value="1" min="1" required />
                                </div>
                                <div class="col-3">
                                  <input class="form-control" type="number" name="price" value="0" min="0" step="1" required />
                                </div>
                                <div class="col-2">
                                  <button class="btn btn-outline-danger" type="button" onclick="this.closest('.order-item').remove()">Xóa</button>
                                </div>`;
                            container.appendChild(div);
                            bindOrderRow(div);
                        });
                        function bindOrderRow(row){
                            const sel = row.querySelector('select[name="productId"]');
                            const qty = row.querySelector('input[name="quantity"]');
                            const price = row.querySelector('input[name="price"]');
                            if (sel){
                                sel.addEventListener('change', function(){
                                    const pid = this.value;
                                    if (productPrice[pid] != null) price.value = productPrice[pid];
                                });
                            }
                        }
                        // bind existing first row if any
                        document.querySelectorAll('#orderItems .order-item').forEach(bindOrderRow);
                        const editModal = document.getElementById('editStatusModal');
                        if (editModal) {
                          editModal.addEventListener('show.bs.modal', function (event) {
                            const button = event.relatedTarget;
                            const id = button.getAttribute('data-id');
                            document.getElementById('edit-id').value = id;
                          });
                        }
                    </script>
                </main>
            </div>
        </div>
    </body>
    </html>


