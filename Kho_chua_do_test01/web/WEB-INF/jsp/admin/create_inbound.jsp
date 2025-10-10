<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Tạo đơn nhập kho</title>
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
                        <h2 class="m-0">Giao dịch - Tạo đơn nhập kho</h2>
                        <div></div>
                    </div>

                    <form action="CreateInbound" method="post">
                        <div class="row g-3">
                            <div class="col-md-4">
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
                            <div class="col-md-4">
                                <label class="form-label">Người Tạo Đơn</label>
                                <input type="text" class="form-control" name="customerName" placeholder="VD: Nhân Viên Kho" />
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">Ngày nhập</label>
                                <input type="datetime-local" class="form-control" name="receiptDate" />
                            </div>
                            <div class="col-md-8">
                                <label class="form-label">Ghi chú</label>
                                <textarea class="form-control" name="note" rows="3" placeholder="Nhập ghi chú..."></textarea>
                            </div>
                        </div>

                        <hr />
                        <h5>Hàng hóa nhập</h5>
                        <table class="table table-sm align-middle" id="itemsTable">
                            <thead>
                                <tr>
                                    <th style="width: 30%">Sản phẩm</th>
                                    <th style="width: 20%">Số lượng</th>
                                    <th style="width: 20%">Đơn giá</th>
                                    <th style="width: 20%">Thành tiền</th>
                                    <th style="width: 20%"></th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td>
                                        <select name="productId" class="form-select" required>
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
                                    </td>
                                    <td><input type="number" name="quantity" class="form-control" value="1" min="1" required /></td>
                                    <td><input type="number" name="unitPrice" class="form-control" value="0" min="0" step="1" required /></td>
                                    <td><input type="number" name="total" class="form-control" value="0" step="1" readonly /></td>
                                    <td>
                                        <button type="button" class="btn btn-outline-primary btn-sm" onclick="addRow()">Thêm dòng</button>
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <div class="d-flex justify-content-end gap-2">
                            <button type="reset" class="btn btn-secondary">Làm mới</button>
                            <button type="submit" class="btn btn-primary">Lưu phiếu nhập</button>
                        </div>
                    </form>

                    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
                    <script>
                        // Build price map from server products
                        const productPrice = {
                        <% java.util.List<Model.Product> pmap = (java.util.List<Model.Product>) request.getAttribute("products");
                           if (pmap != null) {
                             for (int i = 0; i < pmap.size(); i++) {
                               Model.Product p = pmap.get(i);
                        %>
                           <%= p.getProductId() %>: <%= (int) p.getPrice() %><%= (i < pmap.size()-1) ? "," : "" %>
                        <%   }
                           } %>
                        };
                        <% Boolean ok = (Boolean) request.getAttribute("success"); if (ok != null) { %>
                        Swal.fire({
                            icon: '<%= ok ? "success" : "error" %>',
                            title: '<%= ok ? "Tạo phiếu nhập thành công" : "Tạo phiếu nhập thất bại" %>',
                            confirmButtonText: 'OK'
                        });
                        <% } %>
                        function recalc(row) {
                            const qty = parseFloat(row.querySelector('input[name="quantity"]').value || 0);
                            const price = parseFloat(row.querySelector('input[name="unitPrice"]').value || 0);
                            row.querySelector('input[name="total"]').value = (qty * price).toFixed(2);
                        }

                        function bindRowEvents(row){
                            row.querySelector('input[name="quantity"]').addEventListener('input', ()=>recalc(row));
                            row.querySelector('input[name="unitPrice"]').addEventListener('input', ()=>recalc(row));
                            const sel = row.querySelector('select[name="productId"]');
                            if (sel) {
                                sel.addEventListener('change', function(){
                                    const pid = this.value;
                                    const priceInput = row.querySelector('input[name="unitPrice"]');
                                    if (productPrice[pid] != null) {
                                        priceInput.value = productPrice[pid];
                                    }
                                    recalc(row);
                                });
                            }
                        }

                        function addRow(){
                            const tbody = document.querySelector('#itemsTable tbody');
                            const tr = document.createElement('tr');
                            tr.innerHTML = `
                                <td>
                                  <select name=\"productId\" class=\"form-select\" required>
                                    <option value=\"\" disabled selected>-- Chọn sản phẩm --</option>
                                    <%
                                        java.util.List<Model.Product> prods2 = (java.util.List<Model.Product>) request.getAttribute("products");
                                        if (prods2 != null) {
                                            for (Model.Product p : prods2) {
                                    %>
                                    <option value=\"<%= p.getProductId() %>\"><%= p.getProductName() %></option>
                                    <%
                                            }
                                        }
                                    %>
                                  </select>
                                </td>
                                <td><input type="number" name="quantity" class="form-control" value="1" min="1" required /></td>
                                <td><input type="number" name="unitPrice" class="form-control" value="0" min="0" step="1" required /></td>
                                <td><input type="number" name="total" class="form-control" value="0" step="1" readonly /></td>
                                <td>
                                    <button type="button" class="btn btn-outline-danger btn-sm" onclick="this.closest('tr').remove()">Xóa</button>
                                </td>`;
                            tbody.appendChild(tr);
                            bindRowEvents(tr);
                        }

                        // init for first row
                        bindRowEvents(document.querySelector('#itemsTable tbody tr'));
                    </script>
                </main>
            </div>
        </div>
    </body>
</html>


