<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="Model.StockMovementsRequest" %>
<%@ page import="Model.ProductDetail" %>
<!DOCTYPE html>
<%
    @SuppressWarnings("unchecked")
    List<StockMovementsRequest> exportOrders = (List<StockMovementsRequest>) request.getAttribute("exportOrders");
    @SuppressWarnings("unchecked")
    List<ProductDetail> allProducts = (List<ProductDetail>) request.getAttribute("allProducts");

    String fromDate = (String) request.getAttribute("fromDate");
    String toDate = (String) request.getAttribute("toDate");
    String productId = (String) request.getAttribute("productId");
    String status = (String) request.getAttribute("status");

    if (fromDate == null) fromDate = "";
    if (toDate == null) toDate = "";
    if (productId == null) productId = "";
    if (status == null) status = "Tất cả";

    Integer currentPage = (Integer) request.getAttribute("currentPage");
    Integer totalPages = (Integer) request.getAttribute("totalPages");
    Integer totalCount = (Integer) request.getAttribute("totalCount");
    Integer pageSize = (Integer) request.getAttribute("pageSize");
    Integer startIndex = (Integer) request.getAttribute("startIndex");
    Integer endIndex = (Integer) request.getAttribute("endIndex");

    int pageNum = (currentPage != null) ? currentPage : 1;
    int totalPagesNum = (totalPages != null) ? totalPages : 1;
    int totalCountNum = (totalCount != null) ? totalCount : 0;
    int pageSizeNum = (pageSize != null) ? pageSize : 10;
    int startIdx = (startIndex != null) ? startIndex : 1;
    int endIdx = (endIndex != null) ? endIndex : 0;

    String ctx = request.getContextPath();
    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
%>
<html>
    <head>
        <title>Xuất hàng - Quản lý kho</title>
        <link href="<%=ctx%>/css/warehouse/wh-export-orders.css" rel="stylesheet" type="text/css"/>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    </head>
    <body>
        <%@ include file="header-warehouse.jsp" %>

        <div class="main-container">
            <!-- Left Sidebar - Filters -->
            <aside class="sidebar">
                <form method="GET" action="<%=ctx%>/warehouse-export-orders" class="filter-form">
                    <fieldset>
                        <legend>Bộ lọc</legend>

                        <div class="filter-item">
                            <label for="fromDate">Từ ngày:</label>
                            <input type="date" name="fromDate" id="fromDate" value="<%= fromDate %>" class="form-input"/>
                        </div>

                        <div class="filter-item">
                            <label for="toDate">Đến ngày:</label>
                            <input type="date" name="toDate" id="toDate" value="<%= toDate %>" class="form-input"/>
                        </div>

                        <div class="filter-item">
                            <label for="productId">Sản phẩm:</label>
                            <select name="productId" id="productId" class="form-input">
                                <option value="">--Tất cả sản phẩm--</option>
                                <%
                                    if (allProducts != null) {
                                        for (ProductDetail product : allProducts) {
                                            String selected = productId != null && String.valueOf(product.getProductDetailID()).equals(productId) ? "selected" : "";
                                %>
                                <option value="<%= product.getProductDetailID() %>" <%= selected %>>
                                    <%= product.getProductName() != null ? product.getProductName() : "" %> 
                                    <%= product.getProductCode() != null ? "(" + product.getProductCode() + ")" : "" %>
                                </option>
                                <%
                                        }
                                    }
                                %>
                            </select>
                        </div>

                        <div class="filter-item">
                            <label>Trạng thái:</label>
                            <div class="radio-group">
                                <label class="radio-label">
                                    <input type="radio" name="status" value="Tất cả" <%= "Tất cả".equals(status) ? "checked" : "" %>/>
                                    Tất cả
                                </label>
                                <label class="radio-label">
                                    <input type="radio" name="status" value="Chờ xử lý" <%= "Chờ xử lý".equals(status) ? "checked" : "" %>/>
                                    Chờ xử lý
                                </label>
                                <label class="radio-label">
                                    <input type="radio" name="status" value="Đang xử lý" <%= "Đang xử lý".equals(status) ? "checked" : "" %>/>
                                    Đang xử lý
                                </label>
                                <label class="radio-label">
                                    <input type="radio" name="status" value="Đang vận chuyển" <%= "Đang vận chuyển".equals(status) ? "checked" : "" %>/>
                                    Đang vận chuyển
                                </label>
                                <label class="radio-label">
                                    <input type="radio" name="status" value="Hoàn thành" <%= "Hoàn thành".equals(status) ? "checked" : "" %>/>
                                    Hoàn thành
                                </label>
                                <label class="radio-label">
                                    <input type="radio" name="status" value="Đã hủy" <%= "Đã hủy".equals(status) ? "checked" : "" %>/>
                                    Đã hủy
                                </label>
                            </div>
                        </div>

                        <div class="filter-actions">
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-filter"></i> Áp dụng lọc
                            </button>
                            <a href="<%=ctx%>/warehouse-export-orders" class="btn btn-secondary">
                                <i class="fas fa-undo"></i> Reset
                            </a>
                        </div>
                    </fieldset>
                </form>
            </aside>

            <!-- Main Content Area -->
            <main class="main-content">
                <h1>Danh sách hàng xuất</h1>

                <%
                    String successMessage = (String) session.getAttribute("successMessage");
                    String errorMessage = (String) session.getAttribute("errorMessage");
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

                <div class="table-container">
                    <table class="data-table">
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
                                if (exportOrders != null && !exportOrders.isEmpty()) {
                                    int index = startIdx;
                                    for (StockMovementsRequest order : exportOrders) {
                                        String statusClass = "";
                                        String statusText = order.getResponseStatus() != null ? order.getResponseStatus() : "Chờ xử lý";
                                        
                                        if ("Chờ xử lý".equals(statusText)) {
                                            statusClass = "status-pending";
                                        } else if ("Đang xử lý".equals(statusText) || "Đang vận chuyển".equals(statusText)) {
                                            statusClass = "status-processing";
                                        } else if ("Hoàn thành".equals(statusText)) {
                                            statusClass = "status-completed";
                                        } else if ("Đã hủy".equals(statusText)) {
                                            statusClass = "status-cancelled";
                                        }
                            %>
                            <tr>
                                <td><%= index %></td>
                                <td><%= order.getMovementId() %></td>
                                <td><%= order.getFromBranchName() != null ? order.getFromBranchName() : (order.getFromWarehouseName() != null ? order.getFromWarehouseName() : "") %></td>
                                <td>
                                    <span class="status-badge <%= statusClass %>"><%= statusText %></span>
                                </td>
                                <td><%= order.getFormattedDate() != null ? order.getFormattedDate() : (order.getCreatedAt() != null ? df.format(order.getCreatedAt()) : "") %></td>
                                <td><%= order.getCreatedByName() != null ? order.getCreatedByName() : "" %></td>
                                <td>
                                    <a href="<%=ctx%>/serial-check?id=<%= order.getMovementId() %>&movementType=export" class="btn btn-process">
                                        Xử lý
                                    </a>
                                </td>
                            </tr>
                            <%
                                        index++;
                                    }
                                } else {
                            %>
                            <tr>
                                <td colspan="7" class="no-data">Không có dữ liệu</td>
                            </tr>
                            <%
                                }
                            %>
                        </tbody>
                    </table>
                </div>

                <!-- Pagination -->
                <div class="pagination-info">
                    <div class="pagination-left">
                        Hiển thị <%= startIdx %> - <%= endIdx %> trong tổng số <%= totalCountNum %> đơn xuất hàng 
                        (Trang <%= pageNum %> / <%= totalPagesNum %>)
                    </div>
                    <div class="pagination-right">
                        <label>Hiển thị:</label>
                        <select name="pageSize" id="pageSize" onchange="changePageSize(this.value)">
                            <option value="10" <%= pageSizeNum == 10 ? "selected" : "" %>>10</option>
                            <option value="20" <%= pageSizeNum == 20 ? "selected" : "" %>>20</option>
                            <option value="50" <%= pageSizeNum == 50 ? "selected" : "" %>>50</option>
                        </select>
                        <span>bản ghi/trang</span>
                    </div>
                </div>

                <div class="pagination">
                    <%
                        if (pageNum > 1) {
                    %>
                    <a href="<%=ctx%>/warehouse-export-orders?page=<%= pageNum - 1 %>&fromDate=<%= fromDate %>&toDate=<%= toDate %>&productId=<%= productId %>&status=<%= status %>&pageSize=<%= pageSizeNum %>" class="btn btn-pagination">
                        <i class="fas fa-chevron-left"></i> Trước
                    </a>
                    <%
                        }
                        for (int i = 1; i <= totalPagesNum; i++) {
                            if (i == pageNum) {
                    %>
                    <span class="btn btn-pagination active"><%= i %></span>
                    <%
                            } else if (i == 1 || i == totalPagesNum || (i >= pageNum - 2 && i <= pageNum + 2)) {
                    %>
                    <a href="<%=ctx%>/warehouse-export-orders?page=<%= i %>&fromDate=<%= fromDate %>&toDate=<%= toDate %>&productId=<%= productId %>&status=<%= status %>&pageSize=<%= pageSizeNum %>" class="btn btn-pagination">
                        <%= i %>
                    </a>
                    <%
                            } else if (i == pageNum - 3 || i == pageNum + 3) {
                    %>
                    <span class="pagination-ellipsis">...</span>
                    <%
                            }
                        }
                        if (pageNum < totalPagesNum) {
                    %>
                    <a href="<%=ctx%>/warehouse-export-orders?page=<%= pageNum + 1 %>&fromDate=<%= fromDate %>&toDate=<%= toDate %>&productId=<%= productId %>&status=<%= status %>&pageSize=<%= pageSizeNum %>" class="btn btn-pagination">
                        Sau <i class="fas fa-chevron-right"></i>
                    </a>
                    <%
                        }
                    %>
                </div>
            </main>
        </div>

        <script>
            function changePageSize(size) {
                const url = new URL(window.location.href);
                url.searchParams.set('pageSize', size);
                url.searchParams.set('page', '1');
                window.location.href = url.toString();
            }
        </script>
    </body>
</html>

