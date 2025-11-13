<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.User" %>
<%@ page import="Model.Branch" %>
<%@ page import="Model.Role" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý nhân viên</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link href="css/admin/staff_management.css" rel="stylesheet">
</head>
<body class="bg-light">
    <!-- Header -->
    <%@ include file="../admin/header_admin.jsp" %>

    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar Filters -->
            <aside class="col-md-3 sidebar-filters">
                <div class="filter-card">
                    <h6 class="filter-title">
                        <i class="fas fa-filter me-2"></i>Trạng thái nhân viên
                    </h6>
                    <form action="StaffManagement" method="get" id="filterForm">
                        <!-- Preserve search keyword when filtering -->
                        <input type="hidden" name="search" value="<%= request.getAttribute("searchKeyword") != null && !request.getAttribute("searchKeyword").toString().isEmpty() ? request.getAttribute("searchKeyword") : "" %>">
                        
                        <!-- Status Radio Buttons -->
                        <div class="status-radios">
                            <label class="radio-option">
                                <input type="radio" name="status" value="all" 
                                       <%= "all".equals(request.getAttribute("selectedStatus")) ? "checked" : "" %>
                                       onchange="submitFilter()">
                                <span class="radio-custom"></span>
                                <span class="radio-label">Tất cả</span>
                            </label>
                            <label class="radio-option">
                                <input type="radio" name="status" value="active"
                                       <%= "active".equals(request.getAttribute("selectedStatus")) ? "checked" : "" %>
                                       onchange="submitFilter()">
                                <span class="radio-custom"></span>
                                <span class="radio-label">Đang làm việc</span>
                            </label>
                            <label class="radio-option">
                                <input type="radio" name="status" value="inactive"
                                       <%= "inactive".equals(request.getAttribute("selectedStatus")) ? "checked" : "" %>
                                       onchange="submitFilter()">
                                <span class="radio-custom"></span>
                                <span class="radio-label">Nghỉ việc</span>
                            </label>
                        </div>

                        <!-- Action Buttons -->
                        <div class="filter-actions">
                            <button type="button" class="btn-clear-filter" onclick="clearFilters()">
                                <i class="fas fa-trash-alt me-2"></i>Xóa bộ lọc
                            </button>
                            <button type="submit" class="btn-apply-filter">
                                <i class="fas fa-check me-2"></i>Áp dụng lọc
                            </button>
                        </div>
                    </form>
                </div>
            </aside>

            <!-- Main Content -->
            <main class="col-md-9 main-content-area">
                <!-- Page Header -->
                <div class="page-header">
                    <h1 class="page-title">
                        <i class="fas fa-users me-2"></i>Nhân viên
                    </h1>
                    <!-- Search Bar -->
                    <form action="StaffManagement" method="get" class="search-form">
                        <div class="search-input-wrapper">
                            <i class="fas fa-search search-icon"></i>
                            <input type="text" name="search" 
                                   class="search-input" 
                                   placeholder="Theo mã, tên nhân viên"
                                   value="<%= request.getAttribute("searchKeyword") != null ? request.getAttribute("searchKeyword") : "" %>"
                                   maxlength="100"
                                   pattern="[A-Za-z0-9\s\u00C0-\u1EF9]*"
                                   title="Chỉ được nhập chữ, số và khoảng trắng">
                            <!-- Preserve filter values when searching -->
                            <input type="hidden" name="status" value="<%= request.getAttribute("selectedStatus") != null ? request.getAttribute("selectedStatus") : "all" %>">
                        </div>
                    </form>
                </div>

                <!-- Staff Table -->
                <div class="table-container">
                    <table class="staff-table">
                        <thead>
                            <tr>
                                <th class="checkbox-column">
                                    <input type="checkbox" id="selectAll" onchange="toggleSelectAll(this)">
                                </th>
                                <th>Mã nhân viên</th>
                                <th>Tên nhân viên</th>
                                <th>Chức danh</th>
                                <th>Chi nhánh/Kho</th>
                                <th>Số điện thoại</th>
                                <th>Trạng thái</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                List<User> users = (List<User>) request.getAttribute("users");
                                Integer totalUsers = (Integer) request.getAttribute("totalUsers");
                                
                                if (users != null && !users.isEmpty()) {
                                    for (User u : users) {
                                        String statusText = "";
                                        String statusClass = "";
                                        
                                        if (u.getIsActive() == 1) {
                                            statusText = "Đang làm việc";
                                            statusClass = "status-active";
                                        } else if (u.getIsActive() == 0) {
                                            statusText = "Nghỉ việc";
                                            statusClass = "status-inactive";
                                        } else if (u.getIsActive() == 2) {
                                            statusText = "Chờ phê duyệt";
                                            statusClass = "status-pending";
                                        }
                                        
                                        String locationDisplay = "—";
                                        String locationIcon = "";
                                        if (u.getBranchName() != null && !u.getBranchName().trim().isEmpty()) {
                                            locationDisplay = u.getBranchName();
                                            locationIcon = "<i class='fas fa-building me-1'></i>";
                                        } else if (u.getWarehouseName() != null && !u.getWarehouseName().trim().isEmpty()) {
                                            locationDisplay = u.getWarehouseName();
                                            locationIcon = "<i class='fas fa-warehouse me-1'></i>";
                                        }
                            %>
                            <tr>
                                <td class="checkbox-column">
                                    <input type="checkbox" class="row-checkbox" name="selectedUsers" value="<%= u.getUserId() %>">
                                </td>
                                <td><%= u.getUserId() %></td>
                                <td class="name-cell"><%= u.getFullName() != null ? u.getFullName() : "—" %></td>
                                <td><%= u.getRoleName() != null ? u.getRoleName() : "—" %></td>
                                <td><%= locationIcon %><%= locationDisplay %></td>
                                <td><%= u.getPhone() != null ? u.getPhone() : "—" %></td>
                                <td>
                                    <span class="status-badge <%= statusClass %>"><%= statusText %></span>
                                </td>
                            </tr>
                            <%
                                    }
                                } else {
                            %>
                            <tr>
                                <td colspan="7" class="empty-message">
                                    <i class="fas fa-inbox me-2"></i>
                                    Hiện chưa có nhân viên nào hoặc kết quả tìm kiếm không tồn tại.
                                </td>
                            </tr>
                            <%
                                }
                            %>
                        </tbody>
                    </table>
                </div>

                <!-- Pagination -->
                <div class="pagination-wrapper">
                    <div class="pagination-info">
                        <%
                            Integer displayFrom = (Integer) request.getAttribute("displayFrom");
                            Integer displayTo = (Integer) request.getAttribute("displayTo");
                            Integer totalUsersCount = (Integer) request.getAttribute("totalUsers");
                            if (displayFrom != null && displayTo != null && totalUsersCount != null) {
                        %>
                        Hiển thị <%= displayFrom %> - <%= displayTo %> / Tổng số <%= totalUsersCount %> nhân viên
                        <%
                            }
                        %>
                    </div>
                    <div class="pagination-controls">
                        <%
                            Integer currentPage = (Integer) request.getAttribute("currentPage");
                            Integer totalPages = (Integer) request.getAttribute("totalPages");
                            String selectedStatus = (String) request.getAttribute("selectedStatus");
                            String searchKeyword = (String) request.getAttribute("searchKeyword");
                            
                            if (currentPage != null && totalPages != null && totalPages > 1) {
                                // Build URL with filters
                                String baseUrl = "StaffManagement?";
                                boolean hasParams = false;
                                
                                if (selectedStatus != null && !selectedStatus.equals("all") && !selectedStatus.isEmpty()) {
                                    baseUrl += "status=" + selectedStatus;
                                    hasParams = true;
                                }
                                if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                                    if (hasParams) baseUrl += "&";
                                    baseUrl += "search=" + java.net.URLEncoder.encode(searchKeyword.trim(), "UTF-8");
                                    hasParams = true;
                                }
                                
                                if (hasParams) {
                                    baseUrl += "&";
                                }
                                
                                // First page
                                if (currentPage > 1) {
                        %>
                        <a href="<%= baseUrl %>page=1" class="pagination-btn" title="Trang đầu">
                            <i class="fas fa-angle-double-left"></i>
                        </a>
                        <a href="<%= baseUrl %>page=<%= currentPage - 1 %>" class="pagination-btn" title="Trang trước">
                            <i class="fas fa-angle-left"></i>
                        </a>
                        <%
                                } else {
                        %>
                        <span class="pagination-btn disabled">
                            <i class="fas fa-angle-double-left"></i>
                        </span>
                        <span class="pagination-btn disabled">
                            <i class="fas fa-angle-left"></i>
                        </span>
                        <%
                                }
                                
                                // Page numbers
                                int startPage = Math.max(1, currentPage - 2);
                                int endPage = Math.min(totalPages, currentPage + 2);
                                
                                if (startPage > 1) {
                        %>
                        <a href="<%= baseUrl %>page=1" class="pagination-btn">1</a>
                        <%
                                    if (startPage > 2) {
                        %>
                        <span class="pagination-ellipsis">...</span>
                        <%
                                    }
                                }
                                
                                for (int i = startPage; i <= endPage; i++) {
                                    if (i == currentPage) {
                        %>
                        <span class="pagination-btn current"><%= i %></span>
                        <%
                                    } else {
                        %>
                        <a href="<%= baseUrl %>page=<%= i %>" class="pagination-btn"><%= i %></a>
                        <%
                                    }
                                }
                                
                                if (endPage < totalPages) {
                                    if (endPage < totalPages - 1) {
                        %>
                        <span class="pagination-ellipsis">...</span>
                        <%
                                    }
                        %>
                        <a href="<%= baseUrl %>page=<%= totalPages %>" class="pagination-btn"><%= totalPages %></a>
                        <%
                                }
                                
                                // Next page
                                if (currentPage < totalPages) {
                        %>
                        <a href="<%= baseUrl %>page=<%= currentPage + 1 %>" class="pagination-btn" title="Trang sau">
                            <i class="fas fa-angle-right"></i>
                        </a>
                        <a href="<%= baseUrl %>page=<%= totalPages %>" class="pagination-btn" title="Trang cuối">
                            <i class="fas fa-angle-double-right"></i>
                        </a>
                        <%
                                } else {
                        %>
                        <span class="pagination-btn disabled">
                            <i class="fas fa-angle-right"></i>
                        </span>
                        <span class="pagination-btn disabled">
                            <i class="fas fa-angle-double-right"></i>
                        </span>
                        <%
                                }
                            }
                        %>
                    </div>
                </div>
            </main>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Submit filter form
        function submitFilter() {
            document.getElementById('filterForm').submit();
        }

        // Clear all filters
        function clearFilters() {
            window.location.href = 'StaffManagement';
        }

        // Toggle select all checkboxes
        function toggleSelectAll(checkbox) {
            const checkboxes = document.querySelectorAll('.row-checkbox');
            checkboxes.forEach(cb => {
                cb.checked = checkbox.checked;
            });
        }

        // Update select all checkbox when individual checkboxes change
        document.addEventListener('DOMContentLoaded', function() {
            const checkboxes = document.querySelectorAll('.row-checkbox');
            const selectAll = document.getElementById('selectAll');
            
            checkboxes.forEach(cb => {
                cb.addEventListener('change', function() {
                    const allChecked = Array.from(checkboxes).every(c => c.checked);
                    const someChecked = Array.from(checkboxes).some(c => c.checked);
                    selectAll.checked = allChecked;
                    selectAll.indeterminate = someChecked && !allChecked;
                });
            });
        });

        // Search form validation
        document.querySelector('.search-form')?.addEventListener('submit', function(e) {
            const searchInput = this.querySelector('input[name="search"]');
            if (searchInput && searchInput.value.trim().length > 100) {
                e.preventDefault();
                alert('Từ khóa tìm kiếm không được vượt quá 100 ký tự!');
                return false;
            }
        });

        // Real-time search with debounce
        let searchTimeout;
        document.querySelector('.search-input')?.addEventListener('input', function(e) {
            clearTimeout(searchTimeout);
            const searchValue = this.value.trim();
            
            // Only auto-submit if search is empty or valid
            if (searchValue.length === 0 || searchValue.length <= 100) {
                searchTimeout = setTimeout(() => {
                    // Auto-submit after 500ms of no typing
                    if (searchValue.length === 0 || searchValue.match(/^[A-Za-z0-9\s\u00C0-\u1EF9]*$/)) {
                        this.form.submit();
                    }
                }, 500);
            }
        });
    </script>
</body>
</html>

