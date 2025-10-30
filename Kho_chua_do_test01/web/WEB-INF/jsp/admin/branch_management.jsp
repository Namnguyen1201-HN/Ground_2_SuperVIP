<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.Branch" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý chi nhánh</title>
        <link href="css/admin/branch_management.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    </head>

    <body>
        <!-- Header -->
        <div>
            <%@ include file="../admin/header_admin.jsp" %>
        </div>

        <!-- Layout chính -->
        <div class="container-fluid mt-3">
            <div class="row">
                <!-- Sidebar -->
                <%@ include file="../admin/sidebar-store-admin.jsp" %>

                <!-- Nội dung chính -->
                <div class="col-md-9" style="margin-top: 60px;">    
                    <div class="branch-container">

                        <%-- HIỂN THỊ THÔNG BÁO TỪ SERVLET --%>
                        <%
    String success = request.getParameter("success");
    String error = request.getParameter("error");
                        %>

                        <% if (success != null) { %>
                        <div class="alert alert-success text-center fw-bold" role="alert">
                            <% if ("create".equals(success)) { %>
                            ✅ Thêm chi nhánh thành công!
                            <% } else if ("update".equals(success)) { %>
                            ✅ Cập nhật chi nhánh thành công!
                            <% } else if ("delete".equals(success)) { %>
                            ✅ Xóa chi nhánh thành công!
                            <% } else { %>
                            ✅ Thao tác thành công!
                            <% } %>
                        </div>

                        <% } else if (error != null) { %>
                        <div class="alert alert-danger text-center fw-bold" role="alert">
                            <% if ("empty_fields".equals(error)) { %>
                            ⚠️ Vui lòng nhập đầy đủ thông tin!
                            <% } else if ("invalid_phone".equals(error)) { %>
                            📵 Số điện thoại không hợp lệ (bắt đầu bằng 0 và 9–11 chữ số)!
                            <% } else if ("duplicate_phone".equals(error)) { %>
                            📞 Số điện thoại đã tồn tại, vui lòng nhập số khác!
                            <% } else if ("update_failed".equals(error)) { %>
                            ❌ Cập nhật chi nhánh thất bại!
                            <% } else if ("delete_failed".equals(error)) { %>
                            ❌ Xóa chi nhánh thất bại!
                            <% } else if ("exception".equals(error)) { %>
                            ⚠️ Đã xảy ra lỗi trong quá trình xử lý!
                            <% } else { %>
                            ❌ Thao tác thất bại!
                            <% } %>
                        </div>
                        <% } %>

                        <div class="branch-header">
                            <h2><i class="fas fa-code-branch"></i> Quản lý chi nhánh</h2>
                            <form action="BranchCreate" method="get">
                                <button type="submit" class="btn-primary">Tạo chi nhánh mới</button>
                            </form>
                        </div>

                        <div class="branch-table">
                            <table>
                                <thead>
                                    <tr>
                                        <th>TÊN CHI NHÁNH</th>
                                        <th>ĐỊA CHỈ</th>
                                        <th>SỐ ĐIỆN THOẠI</th>
                                        <th>TRẠNG THÁI</th>
                                        <th>THAO TÁC</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                    List<Branch> branches = (List<Branch>) request.getAttribute("branches");
                                    if (branches != null && !branches.isEmpty()) {
                                        for (Branch b : branches) {
                                    %>
                                    <tr class="branch-row" 
                                        data-id="<%= b.getBranchId() %>" 
                                        data-name="<%= b.getBranchName() %>" 
                                        data-address="<%= b.getAddress() %>" 
                                        data-phone="<%= b.getPhone() %>" 
                                        data-active="<%= b.isActive() %>">
                                        <td><strong><%= b.getBranchName() %></strong></td>
                                        <td><%= b.getAddress() %></td>
                                        <td><%= b.getPhone() %></td>
                                        <td><span class="status <%= b.isActive() ? "active" : "inactive" %>"><%= b.isActive() ? "HOẠT ĐỘNG" : "TẠM NGỪNG" %></span></td>
                                        <td>
                                            <form action="BranchManagement" method="post" onsubmit="return confirm('Xóa chi nhánh này?');">
                                                <input type="hidden" name="action" value="delete">
                                                <input type="hidden" name="branchId" value="<%= b.getBranchId() %>">
                                                <input type="hidden" name="page" value="<%= request.getAttribute("currentPage") != null ? request.getAttribute("currentPage") : 1 %>">
                                                <button type="submit" class="btn-delete"><i class="fas fa-trash"></i> Xóa</button>
                                            </form>
                                        </td>
                                    </tr>
                                    <%
                                        }
                                    } else {
                                    %>
                                    <tr><td colspan="5" style="text-align:center;">Không có chi nhánh nào</td></tr>
                                    <% } %>
                                </tbody>

                                <div id="editModal" class="edit-form">
                                    <form action="BranchManagement" method="post">
                                        <input type="hidden" name="action" value="update">
                                        <input type="hidden" id="branchId" name="branchId">
                                        <input type="hidden" name="page" value="<%= request.getAttribute("currentPage") != null ? request.getAttribute("currentPage") : 1 %>">

                                        <h4><i class="fa fa-edit"></i> Chỉnh sửa chi nhánh</h4>

                                        <label>Tên chi nhánh *</label>
                                        <input type="text" id="branchName" name="branchName" class="form-control" required>

                                        <label>Địa chỉ *</label>
                                        <input type="text" id="address" name="address" class="form-control" required>

                                        <label>Số điện thoại *</label>
                                        <input type="text" id="phone" name="phone" class="form-control" required>

                                        <div style="margin-top:10px;">
                                            <input type="checkbox" id="isActive" name="isActive">
                                            <label for="isActive">Chi nhánh đang hoạt động</label>
                                        </div>

                                        <div class="text-end" style="margin-top:15px;">
                                            <button type="button" class="btn btn-secondary" onclick="hideModal()">Hủy</button>
                                            <button type="submit" class="btn btn-success">Cập nhật</button>
                                        </div>
                                    </form>
                                </div>
                                <div id="overlay" class="modal-overlay"></div>

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
                                    <a href="BranchManagement?page=<%= currentPage - 1 %>">&laquo; Trước</a>
                                    <% } else { %>
                                    <a class="disabled" aria-disabled="true">&laquo; Trước</a>
                                    <% } %>

                                    <% for (int i = 1; i <= totalPages; i++) { %>
                                    <% if (i == currentPage) { %>
                                    <span class="current"><%= i %></span>
                                    <% } else { %>
                                    <a href="BranchManagement?page=<%= i %>"><%= i %></a>
                                    <% } %>
                                    <% } %>

                                    <% if (currentPage < totalPages) { %>
                                    <a href="BranchManagement?page=<%= currentPage + 1 %>">Sau &raquo;</a>
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

        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

        <script>
                                                const rows = document.querySelectorAll(".branch-row");
                                                const modal = document.getElementById("editModal");
                                                const overlay = document.getElementById("overlay");

                                                rows.forEach(row => {
                                                    row.addEventListener("click", () => {
                                                        document.getElementById("branchId").value = row.dataset.id;
                                                        document.getElementById("branchName").value = row.dataset.name;
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
