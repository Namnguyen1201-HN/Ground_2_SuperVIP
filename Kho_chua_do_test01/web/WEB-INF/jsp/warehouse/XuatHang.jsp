<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Danh sách hàng xuất</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
        <link href="css/warehouse/XuatHang.css" rel="stylesheet" type="text/css"/>

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
                        <div><input type="radio" name="status"> Chờ xử lý</div>
                        <div><input type="radio" name="status"> Đang xử lý</div>
                        <div><input type="radio" name="status"> Đang vận chuyển</div>
                        <div><input type="radio" name="status"> Hoàn thành</div>
                        <div><input type="radio" name="status"> Đã hủy</div>
                    </div>
                </div>

                <button class="btn-apply">🔍 Áp dụng lọc</button>
                <button class="btn-reset">↩️ Reset</button>
            </div>

            <!-- Danh sách hàng xuất -->
            <div class="table-container">
                <div>
                    <h5>Danh sách hàng xuất</h5>
                    <table class="table table-bordered align-middle">
                        <thead>
                            <tr>
                                <th>STT</th>
                                <th>Mã đơn xuất</th>
                                <th>Chi nhánh gửi</th>
                                <th>Trạng thái</th>
                                <th>Ngày tạo</th>
                                <th>Người tạo</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                List<Map<String, Object>> exportOrders = (List<Map<String, Object>>) request.getAttribute("exportOrders");
                                if (exportOrders != null && !exportOrders.isEmpty()) {
                                    int index = 1;
                                    for (Map<String, Object> order : exportOrders) {
                            %>
                            <tr>
                                <td><%= index++ %></td>
                                <td>#<%= order.get("MovementID") %></td>
                                <td><%= order.get("ToBranchName") != null ? order.get("ToBranchName") : "—" %></td>
                                <td><%= order.get("Status") != null ? order.get("Status") : "pending" %></td>
                                <td><%= order.get("CreatedAt") %></td>
                                <td><%= order.get("CreatedByName") != null ? order.get("CreatedByName") : "—" %></td>
                                <td>
                                    <a href="XuatHangDetail?id=<%= order.get("MovementID") %>" class="btn btn-sm btn-primary">Chi tiết</a>
                                </td>
                            </tr>
                            <%
                                    }
                                } else {
                            %>
                            <tr>
                                <td colspan="7" class="text-center text-muted py-3">
                                    Không có đơn xuất hàng nào.
                                </td>
                            </tr>
                            <%
                                }
                            %>
                        </tbody>
                    </table>
                </div>

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
