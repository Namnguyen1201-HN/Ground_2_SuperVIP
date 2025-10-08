<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <title>Chi tiết đơn nhập</title>
    <link href="css/admin/NhanVien.css" rel="stylesheet" type="text/css"/>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
  </head>
  <body>
    <div class="container-fluid">
      <%@ include file="../includes/header.jsp" %>
      <div class="content">
        <main class="main-content">
          <div class="toolbar d-flex justify-content-between align-items-center">
            <div class="d-flex gap-2">
              <button class="btn btn-outline-secondary" type="button" onclick="history.back()">← Quay lại</button>
            </div>
            <h2 class="m-0">Chi tiết đơn nhập</h2>
            <div></div>
          </div>

          <div class="card p-3 mb-3">
            <p><strong>Mã đơn:</strong> <%= ((Model.Receipt)request.getAttribute("receipt")).getReceiptId() %></p>
            <p><strong>Khách hàng:</strong> <%= ((Model.Receipt)request.getAttribute("receipt")).getCustomerName() != null ? ((Model.Receipt)request.getAttribute("receipt")).getCustomerName() : "-" %></p>
            <p><strong>Ngày tạo:</strong> <%= ((Model.Receipt)request.getAttribute("receipt")).getCreatedAt() %></p>
            <p><strong>Tổng tiền:</strong> <%= ((Model.Receipt)request.getAttribute("receipt")).getGrandTotal() %></p>
          </div>

          <table class="table table-striped align-middle">
            <thead>
              <tr>
                <th>Sản phẩm</th>
                <th>Nhà cung cấp</th>
                <th>SL</th>
                <th>Đơn giá</th>
                <th>Thành tiền</th>
              </tr>
            </thead>
            <tbody>
            <%
              java.util.List<Model.ReceiptDetail> details = (java.util.List<Model.ReceiptDetail>) request.getAttribute("details");
              DAL.ProductDAO productDAO = new DAL.ProductDAO();
              DAL.SupplierDAO supplierDAO = new DAL.SupplierDAO();
              java.util.List<Model.Product> products = productDAO.getAllProducts();
              java.util.List<Model.Supplier> suppliers = supplierDAO.getAllSuppliers();
              java.util.Map<Integer, String> productIdToName = new java.util.HashMap<>();
              java.util.Map<Integer, Integer> productIdToSupplier = new java.util.HashMap<>();
              for (Model.Product p : products) {
                  productIdToName.put(p.getProductId(), p.getProductName());
                  if (p.getSupplierId() != null) productIdToSupplier.put(p.getProductId(), p.getSupplierId());
              }
              java.util.Map<Integer, String> supplierIdToName = new java.util.HashMap<>();
              for (Model.Supplier s : suppliers) supplierIdToName.put(s.getSupplierId(), s.getSupplierName());
              if (details != null) {
                for (Model.ReceiptDetail d : details) {
                  Integer supId = productIdToSupplier.get(d.getProductId());
            %>
              <tr>
                <td><%= productIdToName.getOrDefault(d.getProductId(), String.valueOf(d.getProductId())) %></td>
                <td><%= supId != null ? supplierIdToName.getOrDefault(supId, "-") : "-" %></td>
                <td><%= d.getQuantity() %></td>
                <td><%= String.format("%.0f", d.getUnitPrice()) %></td>
                <td><%= String.format("%.0f", d.getTotal()) %></td>
              </tr>
            <%
                }
              }
            %>
            </tbody>
          </table>
        </main>
      </div>
    </div>
  </body>
  </html>


