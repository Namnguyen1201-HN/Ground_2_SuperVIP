<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.Warehouse" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Quản lý kho tổng</title>
        <link href="css/admin/warehouse_management.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    </head>

    <body>
        <%@ include file="../admin/header_admin.jsp" %>

        <div class="container-fluid mt-3">
            <div class="row">
                <%@ include file="../admin/sidebar-store-admin.jsp" %>

                <div class="col-md-9" style="margin-top: 60px;">
                    <div class="warehouse-container">
                        <%-- ALERT từ redirect --%>
                        <%
                            String success = request.getParameter("success");
                            String error = request.getParameter("error");
                        %>

                        <% if (success != null) { %>
                        <div class="alert alert-success text-center fw-bold" role="alert" style="margin-top:10px;">
                            <% if ("create".equals(success)) { %>
                            ✅ Thêm kho thành công!
                            <% } else if ("update".equals(success)) { %>
                            ✅ Cập nhật kho thành công!
                            <% } else if ("delete".equals(success)) { %>
                            ✅ Xóa kho thành công!
                            <% } else { %>
                            ✅ Thao tác thành công!
                            <% } %>
                        </div>
                        <% } else if (error != null) { %>
                        <div class="alert alert-danger text-center fw-bold" role="alert" style="margin-top:10px;">
                            <% if ("empty_fields".equals(error)) { %>
                            ⚠️ Vui lòng nhập đầy đủ thông tin!
                            <% } else if ("invalid_phone".equals(error)) { %>
                            📵 Số điện thoại không hợp lệ (bắt đầu bằng 0 và 9–11 số)!
                            <% } else if ("update_failed".equals(error)) { %>
                            ❌ Cập nhật kho thất bại!
                            <% } else if ("delete_failed".equals(error)) { %>
                            ❌ Xóa kho thất bại!
                            <% } else if ("duplicate_phone".equals(error)) { %>
                            📞 Số điện thoại đã tồn tại, vui lòng nhập số khác!
                            <% } else if ("exception".equals(error)) { %>
                            ⚠️ Đã xảy ra lỗi trong quá trình xử lý!
                            <% } else { %>
                            ❌ Thao tác thất bại!
                            <% } %>
                        </div>
                        <% } %>

                        <div class="warehouse-header">
                            <h2><i class="fa-solid fa-warehouse"></i> Quản lý kho tổng</h2>
                            <form action="WareHouseCreate" method="get">
                                <button type="submit" class="btn-primary">Tạo kho tổng mới</button>
                            </form>
                        </div>

                        <% 
                            String message = (String) request.getAttribute("message");
                            String msgType = (String) request.getAttribute("msgType");
                            if (message != null) { 
                        %>
                        <div class="alert alert-<%= msgType != null ? msgType : "info" %>"><%= message %></div>
                        <% } %>

                        <div class="warehouse-table">
                            <table>
                                <thead>
                                    <tr>
                                        <th>TÊN KHO TỔNG</th>
                                        <th>ĐỊA CHỈ</th>
                                        <th>SỐ ĐIỆN THOẠI</th>
                                        <th>TRẠNG THÁI</th>
                                        <th>THAO TÁC</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        List<Warehouse> warehouses = (List<Warehouse>) request.getAttribute("warehouses");
                                        if (warehouses != null && !warehouses.isEmpty()) {
                                            for (Warehouse w : warehouses) {
                                    %>
                                    <tr class="warehouse-row"
                                        data-id="<%= w.getWarehouseId() %>"
                                        data-name="<%= w.getWarehouseName() %>"
                                        data-address="<%= w.getAddress() %>"
                                        data-phone="<%= w.getPhone() %>"
                                        data-active="<%= w.isActive() %>">
                                        <td><strong><%= w.getWarehouseName() %></strong></td>
                                        <td><%= w.getAddress() %></td>
                                        <td><%= w.getPhone() %></td>
                                        <td>
                                            <span class="status <%= w.isActive() ? "active" : "inactive" %>">
                                                <%= w.isActive() ? "HOẠT ĐỘNG" : "TẠM NGỪNG" %>
                                            </span>
                                        </td>
                                        <td>
                                            <form action="WareHouseManagement" method="post" onsubmit="return confirm('Bạn có chắc muốn xóa kho này không?');">
                                                <input type="hidden" name="action" value="delete">
                                                <input type="hidden" name="warehouseId" value="<%= w.getWarehouseId() %>">
                                                <input type="hidden" name="page" value="<%= request.getAttribute("currentPage") != null ? request.getAttribute("currentPage") : 1 %>">
                                                <button type="submit" class="btn-delete"><i class="fas fa-trash"></i> Xóa</button>
                                            </form>
                                        </td>
                                    </tr>
                                    <% 
                                            }
                                        } else { 
                                    %>
                                    <tr><td colspan="5" style="text-align:center;">Không có kho tổng nào trong hệ thống.</td></tr>
                                    <% } %>
                                </tbody>
                            </table>
                            <div class="pagination">
                                <%
                                    Integer currentPage = (Integer) request.getAttribute("currentPage");
                                    Integer totalPages = (Integer) request.getAttribute("totalPages");
                                    if (currentPage == null) currentPage = 1;
                                    if (totalPages == null) totalPages = 1;

                                    if (totalPages > 1) {
                                %>
                                <div>
                                    <% if (currentPage > 1) { %>
                                    <a href="WareHouseManagement?page=<%= currentPage - 1 %>">&laquo; Trước</a>
                                    <% } else { %>
                                    <a class="disabled" aria-disabled="true">&laquo; Trước</a>
                                    <% } %>

                                    <% for (int i = 1; i <= totalPages; i++) { %>
                                    <% if (i == currentPage) { %>
                                    <span class="current"><%= i %></span>
                                    <% } else { %>
                                    <a href="WareHouseManagement?page=<%= i %>"><%= i %></a>
                                    <% } %>
                                    <% } %>

                                    <% if (currentPage < totalPages) { %>
                                    <a href="WareHouseManagement?page=<%= currentPage + 1 %>">Sau &raquo;</a>
                                    <% } else { %>
                                    <a class="disabled" aria-disabled="true">Sau &raquo;</a>
                                    <% } %>
                                </div>
                                <% } %>
                            </div>

                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Form chỉnh sửa kho -->
        <div id="editModal" class="edit-form">
            <form action="WareHouseManagement" method="post">
                <input type="hidden" name="action" value="update">
                <input type="hidden" id="warehouseId" name="warehouseId">
                <input type="hidden" name="page" value="<%= request.getAttribute("currentPage") != null ? request.getAttribute("currentPage") : 1 %>">

                <label>Tên kho *</label>
                <input type="text" id="warehouseName" name="warehouseName" class="form-control" required>

                <label>Địa chỉ *</label>
                <input type="text" id="address" name="address" class="form-control" required>

                <label>Số điện thoại *</label>
                <input type="text" id="phone" name="phone" class="form-control" required>

                <div style="margin-top:10px;">
                    <input type="checkbox" id="isActive" name="isActive">
                    <label for="isActive">Kho đang hoạt động</label>
                </div>

                <div class="text-end" style="margin-top:15px;">
                    <button type="button" class="btn btn-secondary" onclick="hideModal()">Hủy</button>
                    <button type="submit" class="btn btn-success">Cập nhật</button>
                </div>
            </form>
        </div>

        <div id="overlay" class="modal-overlay"></div>

        <script>
            const rows = document.querySelectorAll(".warehouse-row");
            const modal = document.getElementById("editModal");
            const overlay = document.getElementById("overlay");

            rows.forEach(row => {
                row.addEventListener("click", () => {
                    document.getElementById("warehouseId").value = row.dataset.id;
                    document.getElementById("warehouseName").value = row.dataset.name;
                    document.getElementById("address").value = row.dataset.address;
                    document.getElementById("phone").value = row.dataset.phone;
                    document.getElementById("isActive").checked = row.dataset.active === "true";
                    modal.style.display = "block";
                    overlay.style.display = "block";
                });
            });

            function hideModal() {
                modal.style.display = "none";
                overlay.style.display = "none";
            }
        </script>
    </body>
</html>
