<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Tạo kho tổng mới</title>
        <link href="css/admin/warehouse_create.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    </head>

    <body>
        <%@ include file="../admin/header_admin.jsp" %>

        <div class="container-fluid mt-3">
            <div class="row">
                <%@ include file="../admin/sidebar-store-admin.jsp" %>

                <div class="col-md-9" style="margin-top: 60px;">
                    <div class="warehouse-create-container">

                        <!-- Tiêu đề -->
                        <div class="create-header">
                            <h2><i class="fa-solid fa-warehouse"></i> Tạo kho tổng mới</h2>
                        </div>

                        <% if (request.getAttribute("message") != null) { %>
                        <div class="alert alert-<%= request.getAttribute("msgType") %> text-center fw-bold">
                            <%= request.getAttribute("message") %>
                        </div>
                        <% } %>

                        <!-- Thông tin kho tổng -->
                        <div class="card form-section">
                            <h5><i class="fa-solid fa-circle-info me-2"></i> Thông tin kho tổng</h5>

                            <form action="WareHouseCreate" method="post" class="warehouse-form">
                                <div class="form-group">
                                    <label for="warehouseName">Tên kho tổng <span class="required">*</span></label>
                                    <input type="text" id="warehouseName" name="warehouseName"
                                           value="<%= request.getParameter("warehouseName") != null ? request.getParameter("warehouseName") : "" %>"
                                           placeholder="Nhập tên kho tổng" required>
                                </div>

                                <div class="form-group">
                                    <label for="address">Địa chỉ kho tổng <span class="required">*</span></label>
                                    <input type="text" id="address" name="address"
                                           value="<%= request.getParameter("address") != null ? request.getParameter("address") : "" %>"
                                           placeholder="Nhập địa chỉ đầy đủ của kho tổng" required>
                                </div>

                                <div class="form-group">
                                    <label for="phone">Số điện thoại <span class="required">*</span></label>
                                    <input type="tel" id="phone" name="phone"
                                           value="<%= request.getParameter("phone") != null ? request.getParameter("phone") : "" %>"
                                           placeholder="Nhập số điện thoại kho tổng"
                                           required pattern="[0-9]{10,11}">
                                </div>

                                <div class="form-actions">
                                    <button type="button" class="btn-secondary" onclick="history.back()">
                                        <i class="fa-solid fa-arrow-left"></i> Quay lại
                                    </button>
                                    <button type="reset" class="btn-secondary">
                                        <i class="fa-solid fa-rotate-left"></i> Đặt lại
                                    </button>
                                    <button type="submit" class="btn-success">
                                        <i class="fa-solid fa-plus"></i> Tạo kho tổng
                                    </button>
                                </div>
                            </form>
                        </div>

                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
