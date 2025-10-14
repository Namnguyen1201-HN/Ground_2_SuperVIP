<%-- 
    Document   : NhapHang
    Created on : Oct 10, 2025, 9:20:54 PM
    Author     : TieuPham
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Danh sách đơn nhập hàng</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
        <link href="css/warehouse/NhapHang.css" rel="stylesheet" type="text/css"/>
        
    </head>
    <body>
        <%@ include file="../warehouse/header-warehouse.jsp" %>

        <div class="page-container">
            <!-- Bộ lọc -->
            <div class="filter-box">
                <h6>Bộ lọc</h6>

                <div class="filter-group">
                    <label for="fromDate">Từ ngày:</label>
                    <input type="date" id="fromDate" name="fromDate">
                </div>

                <div class="filter-group">
                    <label for="toDate">Đến ngày:</label>
                    <input type="date" id="toDate" name="toDate">
                </div>

                <div class="filter-group">
                    <label>Trạng thái:</label>
                    <div class="radio-group">
                        <div><input type="radio" name="status" checked> Tất cả</div>
                        <div><input type="radio" name="status"> Hoàn thành</div>
                        <div><input type="radio" name="status"> Chờ xử lý</div>
                        <div><input type="radio" name="status"> Đang xử lý</div>
                        <div><input type="radio" name="status"> Đã hủy</div>
                    </div>
                </div>

                <button class="btn-apply">🔍 Áp dụng lọc</button>
                <button class="btn-reset">↩️ Reset</button>
            </div>

            <!-- Danh sách đơn nhập hàng -->
            <div class="table-container">
                <div>
                    <h5>Danh sách đơn nhập hàng</h5>
                    <table class="table table-bordered align-middle">
                        <thead>
                            <tr>
                                <th>STT</th>
                                <th>Mã đơn nhập</th>
                                <th>Nhà cung cấp</th>
                                <th>Trạng thái</th>
                                <th>Ngày tạo</th>
                                <th>Người tạo</th>
                                <th>Tổng tiền</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td colspan="8" class="text-center text-muted py-3">
                                    Hiển thị 0 - 0 trong tổng số 0 đơn nhập hàng (Trang 1 / 1)
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>

                <!-- Footer căn giữa -->
                <div class="table-footer">
                    Hiển thị:
                    <select>
                        <option>10</option>
                        <option>20</option>
                        <option>50</option>
                    </select>
                    bản ghi/trang
                </div>
            </div>
        </div>
    </body>
</html>
