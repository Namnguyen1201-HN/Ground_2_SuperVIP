<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Chi tiết đơn xuất hàng</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <style>
            body {
                background-color: #f8f9fa;
            }
            .filter-box {
                background-color: #fff;
                border-radius: 8px;
                padding: 20px;
                box-shadow: 0 0 5px rgba(0,0,0,0.1);
            }
            .table-container {
                background-color: #fff;
                border-radius: 8px;
                padding: 20px;
                box-shadow: 0 0 5px rgba(0,0,0,0.1);
            }
            .btn-scan {
                background-color: #ff9800;
                color: #fff;
            }
            .btn-scan:hover {
                background-color: #e68900;
            }
            .alert-custom {
                background-color: #f8d7da;
                color: #842029;
                border: 1px solid #f5c2c7;
                border-radius: 6px;
                padding: 12px;
                margin-bottom: 15px;
            }
        </style>
    </head>

    <body>
        <%@ include file="../warehouse/header-warehouse.jsp" %>

        <div class="container-fluid mt-4">
            <div class="row d-flex justify-content-center">               

                <!-- Nội dung chính -->
                <div class="col-md-9">
                    <div class="row justify-content-center">
                        <!-- Bộ lọc -->
                        <div class="col-md-3">
                            <div class="filter-box">
                                <h6 class="mb-3 fw-bold">Bộ lọc</h6>
                                <form method="get" action="XuatHangDetail">
                                    <div class="mb-3">
                                        <label class="form-label">Sản phẩm:</label>
                                        <select class="form-select">
                                            <option value="">--Tất cả sản phẩm--</option>
                                            <option>Samsung Galaxy Buds Pro</option>
                                            <option>iPhone 15 Pro Max</option>
                                        </select>
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label">Trạng thái:</label>
                                        <div>
                                            <div class="form-check">
                                                <input class="form-check-input" type="radio" name="status" value="all" checked>
                                                <label class="form-check-label">Tất cả</label>
                                            </div>
                                            <div class="form-check">
                                                <input class="form-check-input" type="radio" name="status" value="completed">
                                                <label class="form-check-label">Hoàn thành</label>
                                            </div>
                                            <div class="form-check">
                                                <input class="form-check-input" type="radio" name="status" value="pending">
                                                <label class="form-check-label">Chờ xử lý</label>
                                            </div>
                                            <div class="form-check">
                                                <input class="form-check-input" type="radio" name="status" value="processing">
                                                <label class="form-check-label">Đang xử lý</label>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="d-flex justify-content-between">
                                        <button type="submit" class="btn btn-primary w-50 me-1">Áp dụng lọc</button>
                                        <button type="reset" class="btn btn-secondary w-50">Reset</button>
                                    </div>
                                </form>
                            </div>
                        </div>

                        <!-- Chi tiết đơn xuất hàng -->
                        <div class="col-md-9">
                            <div class="table-container">
                                <!-- Thông báo lỗi -->
                                <%
                                    String errorMsg = (String) request.getAttribute("errorMsg");
                                    if (errorMsg != null && !errorMsg.isEmpty()) {
                                %>
                                <div class="alert-custom">
                                    <i class="fa-solid fa-circle-exclamation me-2"></i>
                                    <%= errorMsg %>
                                </div>
                                <% } %>

                                <div class="d-flex justify-content-between align-items-center mb-3">
                                    <h5 class="fw-bold">Chi tiết đơn xuất hàng #5</h5>
                                    <a href="XuatHang" class="btn btn-outline-secondary">Quay lại</a>
                                </div>

                                <table class="table table-bordered align-middle">
                                    <thead class="table-light">
                                        <tr>
                                            <th>STT</th>
                                            <th>Tên sản phẩm</th>
                                            <th>Serial number</th>
                                            <th>Số lượng cần xuất</th>
                                            <th>Đã xuất</th>
                                            <th>Thao tác</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr>
                                            <td>1</td>
                                            <td>Samsung Galaxy Buds Pro<br><small>(SG8PRO)</small></td>
                                            <td>Chưa có serial nào</td>
                                            <td>1</td>
                                            <td>0/1</td>
                                            <td>
                                                <button class="btn btn-scan btn-sm" data-bs-toggle="modal" data-bs-target="#serialModal">
                                                    Quét
                                                </button>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>

                                <div class="d-flex justify-content-between">
                                    <p>Hiển thị 1 - 1 / Tổng số 1 sản phẩm (Trang 1/1)</p>
                                    <div>
                                        <label>Hiển thị:
                                            <select class="form-select d-inline-block" style="width:auto;">
                                                <option>10</option>
                                                <option>20</option>
                                                <option>50</option>
                                            </select> bản ghi/trang
                                        </label>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal nhập serial -->
        <div class="modal fade" id="serialModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Nhập Serial Sản phẩm</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <label for="serialInput" class="form-label">Serial Number:</label>
                        <input type="text" class="form-control mb-3" id="serialInput" placeholder="Nhập serial hoặc quét QR...">

                        <div class="d-flex justify-content-center gap-2">
                            <button class="btn btn-primary"><i class="fa-solid fa-qrcode me-1"></i> Quét QR Code</button>
                            <button class="btn btn-success"><i class="fa-solid fa-plus me-1"></i> Thêm Serial</button>
                            <button class="btn btn-secondary" data-bs-dismiss="modal"><i class="fa-solid fa-xmark me-1"></i> Hủy</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
