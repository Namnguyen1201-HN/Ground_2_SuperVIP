<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <title>Chi tiết đơn hàng</title>
    <link href="css/NhanVien.css" rel="stylesheet" type="text/css"/>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
  </head>
  <body>
    <div class="container-fluid">
      <%@ include file="includes/header.jsp" %>
      <div class="content">
        <main class="main-content">
          <div class="toolbar d-flex justify-content-between align-items-center">
            <div class="d-flex gap-2">
              <button class="btn btn-outline-secondary" type="button" onclick="history.back()">← Quay lại</button>
            </div>
            <h2 class="m-0">Chi tiết đơn hàng</h2>
            <div></div>
          </div>

          <div class="card p-3 mb-3">
            <p><strong>Mã đơn:</strong> <%= ((Model.Order)request.getAttribute("order")).getOrderId() %></p>
            <p><strong>Nhà cung cấp:</strong> <%
                java.util.List<Model.Supplier> sups = (java.util.List<Model.Supplier>) request.getAttribute("suppliers");
                Model.Order od = (Model.Order)request.getAttribute("order");
                String sname = "";
                if (sups != null) {
                    for (Model.Supplier s : sups) if (s.getSupplierId() == od.getSupplierId()) { sname = s.getSupplierName(); break; }
                }
                out.print(sname);
            %></p>
            <p><strong>Ngày tạo:</strong> <%= od.getOrderDate() %></p>
            <p><strong>Trạng thái:</strong> <%= od.getStatus() %></p>
          </div>

          <table class="table table-striped align-middle">
            <thead>
              <tr>
                <th>Sản phẩm</th>
                <th>Số lượng</th>
                <th>Đơn giá</th>
                <th>Thành tiền</th>
              </tr>
            </thead>
            <tbody>
              <%
                java.util.List<Model.OrderDetail> items = (java.util.List<Model.OrderDetail>) request.getAttribute("items");
                java.util.List<Model.Product> prods = (java.util.List<Model.Product>) request.getAttribute("products");
                if (items != null) {
                  for (Model.OrderDetail d : items) {
                    String pname = String.valueOf(d.getProductId());
                    if (prods != null) {
                      for (Model.Product p : prods) if (p.getProductId() == d.getProductId()) { pname = p.getProductName(); break; }
                    }
              %>
              <tr>
                <td><%= pname %></td>
                <td><%= d.getQuantity() %></td>
                <td><%= String.format("%.0f", d.getPrice()) %></td>
                <td><%= String.format("%.0f", d.getPrice() * d.getQuantity()) %></td>
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


