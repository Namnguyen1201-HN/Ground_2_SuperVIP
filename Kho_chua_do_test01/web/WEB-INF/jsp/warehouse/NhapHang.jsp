<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*,java.text.*" %>

<%
    List<Map<String,Object>> list = (List<Map<String,Object>>) request.getAttribute("importOrders");
    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    DecimalFormat money = new DecimalFormat("#,##0 VNĐ");
%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Danh sách đơn nhập hàng</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body>
        <%@ include file="../warehouse/header-warehouse.jsp" %>

        <div class="container mt-4">
            <h4 class="mb-3">📦 Danh sách đơn nhập hàng</h4>

            <table class="table table-bordered align-middle">
                <thead class="table-light">
                    <tr>
                        <th>STT</th>
                        <th>Mã đơn</th>
                        <th>Nhà cung cấp</th>
                        <th>Người tạo</th>
                        <th>Ngày tạo</th>
                        <th>Tổng tiền</th>
                        <th>Trạng thái</th>
                        <th>Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        if (list == null || list.isEmpty()) {
                    %>
                    <tr><td colspan="8" class="text-center text-muted py-3">Chưa có đơn nhập hàng nào.</td></tr>
                    <%
                        } else {
                            int stt = 1;
                            for (Map<String,Object> row : list) {
                                String status = (String) row.get("Status");
                                String statusLabel = "Chờ xử lý";
                                String badgeClass = "secondary";
                                if ("processing".equalsIgnoreCase(status)) { statusLabel = "Đang xử lý"; badgeClass="warning"; }
                                else if ("completed".equalsIgnoreCase(status)) { statusLabel = "Hoàn thành"; badgeClass="success"; }
                                else if ("cancelled".equalsIgnoreCase(status)) { statusLabel = "Đã hủy"; badgeClass="danger"; }
                    %>
                    <tr>
                        <td><%=stt++%></td>
                        <td><%=row.get("MovementID")%></td>
                        <td><%=row.get("SupplierName")!=null?row.get("SupplierName"):"-"%></td>
                        <td><%=row.get("CreatedByName")!=null?row.get("CreatedByName"):"-"%></td>
                        <td><%=row.get("CreatedAt")!=null?df.format(row.get("CreatedAt")):""%></td>
                        <td><%=row.get("TotalAmount")!=null?money.format(row.get("TotalAmount")):"0 VNĐ"%></td>
                        <td><span class="badge bg-<%=badgeClass%>"><%=statusLabel%></span></td>
                        <td>
                            <a href="NhapHangDetail?id=<%=row.get("MovementID")%>" class="btn btn-sm btn-outline-primary">Xem</a>
                        </td>
                    </tr>
                    <%
                            }
                        }
                    %>
                </tbody>
            </table>
        </div>

    </body>
</html>
