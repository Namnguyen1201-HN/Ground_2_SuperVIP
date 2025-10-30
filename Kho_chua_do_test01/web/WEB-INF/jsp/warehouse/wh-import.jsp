<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="Model.StockMovementsRequest" %>
<!DOCTYPE html>
<%
    // Lấy dữ liệu từ request
    @SuppressWarnings("unchecked")
    List<StockMovementsRequest> importRequests =
        (List<StockMovementsRequest>) request.getAttribute("importRequests");

    String fromDate   = (String) request.getAttribute("fromDate");
    String toDate     = (String) request.getAttribute("toDate");
    String status     = (String) request.getAttribute("status");

    String paramStatus = request.getParameter("status");
    String paramFrom   = request.getParameter("fromDate");
    String paramTo     = request.getParameter("toDate");

    if (fromDate == null) fromDate = (paramFrom != null ? paramFrom.trim() : "");
    if (toDate   == null) toDate   = (paramTo   != null ? paramTo.trim()   : "");
    if (status   == null) status   = (paramStatus != null ? paramStatus.trim() : "");

    String ctx = request.getContextPath();
    
    // ===== Paging attributes coming from Controller =====
    Integer _pgCurrent  = (Integer) request.getAttribute("currentPage");
    Integer _pgTotal    = (Integer) request.getAttribute("totalPages");
    Integer _pgItems    = (Integer) request.getAttribute("totalItems");
    Integer _pgStart    = (Integer) request.getAttribute("startItem");
    Integer _pgEnd      = (Integer) request.getAttribute("endItem");
    String  baseQuery   = (String)  request.getAttribute("baseQuery");

    int currentPage = (_pgCurrent == null) ? 1  : _pgCurrent;
    int totalPages  = (_pgTotal   == null) ? 1  : _pgTotal;
    int totalItems  = (_pgItems   == null) ? 0  : _pgItems;
    int startItem   = (_pgStart   == null) ? 0  : _pgStart;
    int endItem     = (_pgEnd     == null) ? 0  : _pgEnd;

    if (baseQuery == null) baseQuery = "";

    // Formatter
    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    NumberFormat nf = NumberFormat.getNumberInstance(new Locale("vi","VN"));
%>
<html>
    <head>
        <title>Quản lý nhập hàng - WH</title>            
        <link href="css/warehouse/track-movements.css" rel="stylesheet" type="text/css"/>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    </head>
    <body>

        <%@ include file="header-warehouse.jsp" %>

        <div class="main-container">
            <aside class="sidebar">
                <form method="GET" action="<%=ctx%>/wh-import" class="filter-form">
                    <fieldset>
                        <legend>Bộ lọc</legend>

                        <div class="filter-item">
                            <label for="status">Trạng thái:</label>
                            <select name="status" id="status" class="form-input">
                                <option value="">Tất cả</option>
                                <option value="pending"    <%= "pending".equalsIgnoreCase(status) ? "selected" : "" %>>Chờ xử lý</option>
                                <option value="processing" <%= "processing".equalsIgnoreCase(status) ? "selected" : "" %>>Đang xử lý</option>
                                <option value="completed"  <%= "completed".equalsIgnoreCase(status) ? "selected" : "" %>>Hoàn thành</option>
                            </select>
                        </div>

                        <div class="filter-item">
                            <label>Từ ngày:</label>
                            <input type="date" name="fromDate" value="<%= fromDate == null ? "" : fromDate %>" class="form-input"/>
                        </div>

                        <div class="filter-item">
                            <label>Đến ngày:</label>
                            <input type="date" name="toDate" value="<%= toDate == null ? "" : toDate %>" class="form-input"/>
                        </div>

                        <div class="filter-actions">
                            <button type="submit" class="btn btn-primary"><i class="fas fa-filter"></i> Lọc</button>
                            <a href="<%=ctx%>/wh-import" class="btn btn-secondary"><i class="fas fa-undo"></i> Reset</a>
                        </div>
                    </fieldset>
                </form>
            </aside>

            <main class="main-content">
                <h1>Danh sách đơn nhập hàng</h1>

                <%
                    String successMessage = (String) session.getAttribute("successMessage");
                    String errorMessage   = (String) session.getAttribute("errorMessage");
                    if (successMessage != null && !successMessage.isEmpty()) {
                %>
                <div class="alert alert-success"><%= successMessage %></div>
                <%
                        session.removeAttribute("successMessage");
                    }
                    if (errorMessage != null && !errorMessage.isEmpty()) {
                %>
                <div class="alert alert-error"><%= errorMessage %></div>
                <%
                        session.removeAttribute("errorMessage");
                    }
                %>

                <div class="list-summary" style="display:flex;justify-content:space-between;align-items:center;margin:10px 0;">
                    <div class="left">
                        <small>
                            Hiển thị <strong><%= startItem %></strong>–<strong><%= endItem %></strong>
                            trên tổng <strong><%= totalItems %></strong> kết quả
                        </small>
                    </div>
                </div>

                <table class="invoices-table">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Nhà cung cấp</th>
                            <th>Ngày tạo</th>
                            <th>Người tạo</th>
                            <th>Ghi chú</th>
                            <th>Trạng thái</th>
                            <th>Tổng tiền</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            if (importRequests != null && !importRequests.isEmpty()) {
                                for (int i = 0; i < importRequests.size(); i++) {
                                    StockMovementsRequest item = importRequests.get(i);
                                    String dateStr = "";
                                    try {
                                        dateStr = (item.getCreatedAt() != null) ? df.format(item.getCreatedAt()) : "";
                                    } catch (Exception ignore) {}
                                    
                                    String resp = (item.getResponseStatus() == null) ? "" : item.getResponseStatus().toLowerCase();
                                    String statusBadge = "Chờ xử lý";
                                    String statusClass = "badge-pending";
                                    if ("completed".equals(resp)) { statusBadge = "Hoàn thành"; statusClass = "badge-success"; }
                                    else if ("processing".equals(resp)) { statusBadge = "Đang xử lý"; statusClass = "badge-warning"; }

                                    String totalStr = "";
                                    try {
                                        if (item.getTotalAmount() != null) {
                                            totalStr = nf.format(item.getTotalAmount());
                                        }
                                    } catch (Exception ignore) {}
                        %>
                        <tr>
                            <td><%= (startItem == 0 ? (i + 1) : (startItem + i)) %></td>
                            <td><%= item.getFromSupplierName() == null ? "" : item.getFromSupplierName() %></td>
                            <td><%= dateStr %></td>
                            <td><%= item.getCreatedByName() == null ? "" : item.getCreatedByName() %></td>
                            <td><%= item.getNote() == null ? "" : item.getNote() %></td>
                            <td><span class="badge <%= statusClass %>"><%= statusBadge %></span></td>
                            <td><%= totalStr %></td>
                            <%
    // Lấy ID an toàn theo getter của bạn (getMovementId hoặc getMovementID)
    String movementIdStr = "";
    try {
        Object mid = null;
        try { mid = item.getClass().getMethod("getMovementID").invoke(item); }
        catch (NoSuchMethodException e1) { mid = item.getClass().getMethod("getMovementId").invoke(item); }
        if (mid != null) movementIdStr = String.valueOf(mid);
    } catch (Exception ignore) {}
                            %>
                            <td>
                                <a href="wh-import-export-detail?id=<%= movementIdStr %>" class="btn btn-sm btn-info">
                                    <i class="fas fa-eye"></i> Chi tiết
                                </a>
                            </td>
                        </tr>
                        <%
                                }
                            } else {
                        %>
                        <tr>
                            <td colspan="8" class="no-data">
                                <i class="fas fa-box-open"></i> Không có đơn nhập nào
                            </td>
                        </tr>
                        <%
                            }
                        %>
                    </tbody>
                </table>

                <%-- Pagination --%>
                <%
                    int window = 2; // số trang hiển thị 2 bên trang hiện tại
                    int start = Math.max(1, currentPage - window);
                    int end   = Math.min(totalPages, currentPage + window);
                %>

                <nav class="pagination-bar" aria-label="Pagination" style="margin-top:12px;">
                    <ul class="pagination" style="display:flex;gap:4px;justify-content:flex-end;list-style:none;padding:0;">
                        <li class="page-item <%= (currentPage <= 1 ? "disabled" : "") %>">
                            <a class="page-link" href="<%=ctx%>/wh-import?page=<%= (currentPage - 1) %><%= baseQuery %>">Trước</a>
                        </li>

                        <% if (start > 1) { %>
                        <li class="page-item">
                            <a class="page-link" href="<%=ctx%>/wh-import?page=1<%= baseQuery %>">1</a>
                        </li>
                        <% if (start > 2) { %>
                        <li class="page-item disabled"><span class="page-link">…</span></li>
                            <% } %>
                            <% } %>

                        <% for (int p = start; p <= end; p++) { %>
                        <li class="page-item <%= (p == currentPage ? "active" : "") %>">
                            <a class="page-link" href="<%=ctx%>/wh-import?page=<%= p %><%= baseQuery %>"><%= p %></a>
                        </li>
                        <% } %>

                        <% if (end < totalPages) { %>
                        <% if (end < totalPages - 1) { %>
                        <li class="page-item disabled"><span class="page-link">…</span></li>
                            <% } %>
                        <li class="page-item">
                            <a class="page-link" href="<%=ctx%>/wh-import?page=<%= totalPages %><%= baseQuery %>"><%= totalPages %></a>
                        </li>
                        <% } %>

                        <li class="page-item <%= (currentPage >= totalPages ? "disabled" : "") %>">
                            <a class="page-link" href="<%=ctx%>/wh-import?page=<%= (currentPage + 1) %><%= baseQuery %>">Sau</a>
                        </li>
                    </ul>
                </nav>    

            </main>
        </div>

    </body>
</html>
