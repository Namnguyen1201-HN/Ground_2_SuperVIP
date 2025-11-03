<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.CashFlowReportDTO" %>
<%@ page import="Model.RevenueReportDTO" %>
<%@ page import="Model.Branch" %>
<%@ page import="Model.User" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.math.BigDecimal" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Báo cáo</title>
    <link href="css/admin/report.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    </head>
<body>
    <!-- Main Content -->
    <main class="main-content">
        <div class="container-fluid">
            <div class="row">
                <!-- Sidebar -->
                <!-- <%@ include file="../admin/sidebar-store-admin.jsp" %> -->
                <%@ include file="../admin/header_admin.jsp" %>

                <!-- Nội dung chính -->
                <div class="col-md-9" style="margin-top: 20px;">
                    <div class="report-container">
                        <!-- Header với 2 buttons -->
                        <div class="report-header">
                            <h2><i class="fas fa-chart-line"></i> Báo cáo</h2>
                            <div class="report-tabs">
                                <a href="Report?type=revenue<%= buildFilterParams(request) %>" 
                                   class="tab-btn <%= "revenue".equals(request.getParameter("type")) || request.getParameter("type") == null ? "active" : "" %>">
                                    <i class="fas fa-money-bill-wave"></i> Báo cáo doanh thu thuần
                                </a>
                                <a href="Report?type=expense<%= buildFilterParams(request) %>" 
                                   class="tab-btn <%= "expense".equals(request.getParameter("type")) ? "active" : "" %>">
                                    <i class="fas fa-receipt"></i> Báo cáo các chi phí
                                </a>
                            </div>
                        </div>

                        <!-- Content Layout with Filters and Table -->
                        <div class="content-layout">
                            <!-- Filters Sidebar (Left) -->
                            <div class="filters-sidebar">
                                <h6><i class="fas fa-filter"></i> Bộ lọc</h6>
                                
                                <form method="get" action="Report" id="filterForm">
                                    <input type="hidden" name="type" value="<%= request.getParameter("type") != null ? request.getParameter("type") : "revenue" %>" />
                                    
                                    <div class="filter-group">
                                        <label>Từ ngày:</label>
                                        <input type="date" name="fromDate" value="<%= request.getAttribute("fromDate") != null ? request.getAttribute("fromDate") : "" %>" 
                                               class="filter-input" />
                                    </div>

                                    <div class="filter-group">
                                        <label>Đến ngày:</label>
                                        <input type="date" name="toDate" value="<%= request.getAttribute("toDate") != null ? request.getAttribute("toDate") : "" %>" 
                                               class="filter-input" />
                                    </div>

                                    <div class="filter-group">
                                        <label>Cửa hàng:</label>
                                        <select name="branchId" id="branchSelect" class="filter-select" onchange="loadEmployees(this.value)">
                                            <option value="">--Tất cả--</option>
                                            <%
                                                List<Branch> branches = (List<Branch>) request.getAttribute("branches");
                                                String selectedBranchId = (String) request.getAttribute("selectedBranchId");
                                                if (branches != null) {
                                                    for (Branch branch : branches) {
                                                        String branchIdStr = String.valueOf(branch.getBranchId());
                                                        boolean isSelected = branchIdStr.equals(selectedBranchId);
                                            %>
                                            <option value="<%= branch.getBranchId() %>" <%= isSelected ? "selected" : "" %>>
                                                <%= branch.getBranchName() %>
                                            </option>
                                            <%
                                                    }
                                                }
                                            %>
                                        </select>
                                    </div>

                                    <div class="filter-group">
                                        <label>Nhân viên:</label>
                                        <select name="employeeId" id="employeeSelect" class="filter-select">
                                            <option value="">--Chọn cửa hàng trước--</option>
                                            <%
                                                List<User> employees = (List<User>) request.getAttribute("employees");
                                                String selectedEmployeeId = (String) request.getAttribute("selectedEmployeeId");
                                                if (employees != null && !employees.isEmpty()) {
                                                    for (User employee : employees) {
                                                        String employeeIdStr = String.valueOf(employee.getUserId());
                                                        boolean isSelected = employeeIdStr.equals(selectedEmployeeId);
                                            %>
                                            <option value="<%= employee.getUserId() %>" <%= isSelected ? "selected" : "" %>>
                                                <%= employee.getFullName() %>
                                            </option>
                                            <%
                                                    }
                                                }
                                            %>
                                        </select>
                                    </div>

                                    <div class="filter-group">
                                        <label>Phương thức thanh toán:</label>
                                        <label class="radio-label">
                                            <input type="radio" name="paymentMethod" value="all" 
                                                   <%= "all".equals(request.getAttribute("selectedPaymentMethod")) || request.getAttribute("selectedPaymentMethod") == null ? "checked" : "" %> />
                                            <span>Tất cả</span>
                                        </label>
                                        <label class="radio-label">
                                            <input type="radio" name="paymentMethod" value="Chuyển khoản" 
                                                   <%= "Chuyển khoản".equals(request.getAttribute("selectedPaymentMethod")) ? "checked" : "" %> />
                                            <span>Chuyển khoản</span>
                                        </label>
                                        <label class="radio-label">
                                            <input type="radio" name="paymentMethod" value="Tiền mặt" 
                                                   <%= "Tiền mặt".equals(request.getAttribute("selectedPaymentMethod")) ? "checked" : "" %> />
                                            <span>Tiền mặt</span>
                                        </label>
                                    </div>

                                    <div class="filter-actions">
                                        <button type="submit" class="btn-apply">
                                            <i class="fas fa-check"></i> Áp dụng lọc
                                        </button>
                                        <button type="button" class="btn-reset" onclick="resetFilters()">
                                            <i class="fas fa-redo"></i> Reset
                                        </button>
                                    </div>
                                </form>
                            </div>

                            <!-- Report Table (Right) -->
                            <div class="report-table-wrapper">
                                <div class="report-header-actions">
                                    <h3 class="report-title">
                                        <%= "expense".equals(request.getParameter("type")) ? "Báo cáo chi phí" : "Báo cáo doanh thu thuần" %>
                                    </h3>
                                    <button type="button" class="btn-export">
                                        <i class="fas fa-file-excel"></i> Export Excel
                                    </button>
                                </div>

                                <div class="report-table-container">
                                    <table class="report-table">
                                        <thead>
                                            <tr>
                                                <th>STT</th>
                                                <% if ("expense".equals(request.getParameter("type"))) { %>
                                                    <th>Mã giao dịch</th>
                                                    <th>Danh mục</th>
                                                <% } else { %>
                                                    <th>Mã đơn hàng</th>
                                                    <th>Tên khách hàng</th>
                                                <% } %>
                                                <th>Phương thức thanh toán</th>
                                                <th>Ngày tạo</th>
                                                <th>Chi nhánh</th>
                                                <th>Người tạo</th>
                                                <th>Số tiền</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <%
                                                String reportType = (String) request.getAttribute("reportType");
                                                BigDecimal totalAmount = (BigDecimal) request.getAttribute("totalAmount");
                                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                                
                                                // Initialize reports variables outside if-else block
                                                List<CashFlowReportDTO> expenseReports = null;
                                                List<RevenueReportDTO> revenueReports = null;
                                                int reportCount = 0;
                                                
                                                if ("expense".equals(reportType)) {
                                                    // Display expense reports (from CashFlows)
                                                    expenseReports = (List<CashFlowReportDTO>) request.getAttribute("reports");
                                                    if (expenseReports != null && !expenseReports.isEmpty()) {
                                                        reportCount = expenseReports.size();
                                                        int stt = 1;
                                                        for (CashFlowReportDTO report : expenseReports) {
                                            %>
                                            <tr>
                                                <td><%= stt %></td>
                                                <td><%= report.getCashFlowId() %></td>
                                                <td><strong><%= report.getCategory() != null ? report.getCategory() : "N/A" %></strong></td>
                                                <td>
                                                    <span class="payment-badge payment-<%= report.getPaymentMethod() != null && report.getPaymentMethod().contains("khoản") ? "transfer" : "cash" %>">
                                                        <%= report.getPaymentMethod() != null ? report.getPaymentMethod() : "N/A" %>
                                                    </span>
                                                </td>
                                                <td><%= dateFormat.format(report.getCreatedAt()) %></td>
                                                <td><%= report.getBranchName() != null ? report.getBranchName() : "N/A" %></td>
                                                <td><%= report.getCreatedBy() != null ? report.getCreatedBy() : "N/A" %></td>
                                                <td class="amount-cell">
                                                    <strong><%= formatCurrency(report.getAmount()) %></strong>
                                                </td>
                                            </tr>
                                            <%
                                                        stt++;
                                                    }
                                                } else {
                                            %>
                                            <tr>
                                                <td colspan="8" class="no-data">Không có dữ liệu</td>
                                            </tr>
                                            <%
                                                }
                                                } else {
                                                    // Display revenue reports (from Orders)
                                                    revenueReports = (List<RevenueReportDTO>) request.getAttribute("revenueReports");
                                                    if (revenueReports != null && !revenueReports.isEmpty()) {
                                                        reportCount = revenueReports.size();
                                                        int stt = 1;
                                                        for (RevenueReportDTO report : revenueReports) {
                                            %>
                                            <tr>
                                                <td><%= stt %></td>
                                                <td><%= report.getOrderId() %></td>
                                                <td><strong><%= report.getCustomerName() != null ? report.getCustomerName() : "Khách lẻ" %></strong></td>
                                                <td>
                                                    <span class="payment-badge payment-<%= report.getPaymentMethod() != null && report.getPaymentMethod().contains("khoản") ? "transfer" : "cash" %>">
                                                        <%= report.getPaymentMethod() != null ? report.getPaymentMethod() : "N/A" %>
                                                    </span>
                                                </td>
                                                <td><%= dateFormat.format(report.getCreatedAt()) %></td>
                                                <td><%= report.getBranchName() != null ? report.getBranchName() : "N/A" %></td>
                                                <td><%= report.getCreatedByName() != null ? report.getCreatedByName() : "N/A" %></td>
                                                <td class="amount-cell">
                                                    <strong><%= formatCurrency(report.getGrandTotal()) %></strong>
                                                </td>
                                            </tr>
                                            <%
                                                        stt++;
                                                    }
                                                } else {
                                            %>
                                            <tr>
                                                <td colspan="8" class="no-data">Không có dữ liệu</td>
                                            </tr>
                                            <%
                                                }
                                                }
                                            %>
                                        </tbody>
                                    </table>
                                </div>

                                <!-- Summary -->
                                <div class="report-summary">
                                    <div class="summary-total">
                                        <strong>
                                            <%= "expense".equals(request.getParameter("type")) ? "Tổng chi phí" : "Tổng thu tất cả" %>: 
                                            <span class="total-amount"><%= totalAmount != null ? formatCurrency(totalAmount) : "0 ₫" %></span>
                                        </strong>
                                    </div>
                                    <div class="pagination-info">
                                        Hiển thị 1 - <%= reportCount %> / Tổng số <%= reportCount %> <%= "expense".equals(request.getAttribute("reportType")) ? "giao dịch" : "hóa đơn" %>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <script>
        function loadEmployees(branchId) {
            const employeeSelect = document.getElementById('employeeSelect');
            
            if (!branchId || branchId === '') {
                employeeSelect.innerHTML = '<option value="">--Chọn cửa hàng trước--</option>';
                return;
            }
            
            // Load employees via AJAX
            fetch('Report?action=loadEmployees&branchId=' + branchId)
                .then(response => response.json())
                .then(data => {
                    employeeSelect.innerHTML = '<option value="">--Tất cả--</option>';
                    data.forEach(function(emp) {
                        const option = document.createElement('option');
                        option.value = emp.id;
                        option.textContent = emp.name;
                        employeeSelect.appendChild(option);
                    });
                })
                .catch(error => {
                    console.error('Error loading employees:', error);
                    employeeSelect.innerHTML = '<option value="">--Chọn cửa hàng trước--</option>';
                });
        }
        
        function resetFilters() {
            const type = document.querySelector('input[name="type"]').value;
            window.location.href = 'Report?type=' + type;
        }
    </script>
</body>
</html>

<%!
    private String buildFilterParams(HttpServletRequest request) {
        StringBuilder params = new StringBuilder();
        
        String fromDate = (String) request.getAttribute("fromDate");
        String toDate = (String) request.getAttribute("toDate");
        String branchId = (String) request.getAttribute("selectedBranchId");
        String employeeId = (String) request.getAttribute("selectedEmployeeId");
        String paymentMethod = (String) request.getAttribute("selectedPaymentMethod");
        
        if (fromDate != null && !fromDate.isEmpty()) {
            params.append("&fromDate=").append(fromDate);
        }
        if (toDate != null && !toDate.isEmpty()) {
            params.append("&toDate=").append(toDate);
        }
        if (branchId != null && !branchId.isEmpty()) {
            params.append("&branchId=").append(branchId);
        }
        if (employeeId != null && !employeeId.isEmpty()) {
            params.append("&employeeId=").append(employeeId);
        }
        if (paymentMethod != null && !paymentMethod.isEmpty() && !"all".equals(paymentMethod)) {
            params.append("&paymentMethod=").append(paymentMethod);
        }
        
        return params.toString();
    }
    
    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "0 ₫";
        }
        java.text.NumberFormat formatter = new java.text.DecimalFormat("#,###");
        return formatter.format(amount) + " ₫";
    }
%>

