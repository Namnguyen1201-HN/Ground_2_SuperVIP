<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.Promotion" %>
<%@ page import="Model.Branch" %>
<%@ page import="Model.ProductDetail" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý khuyến mãi</title>
    <link href="css/admin/promotion_management.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>
<body>
    <!-- Main Content -->
    <main class="main-content">
        <div class="container-fluid">
            <div class="row">
                <!-- Sidebar -->
                <!-- Header -->
            <%@ include file="../admin/header_admin.jsp" %>

                <!-- Nội dung chính -->
                <div class="col-md-9" style="margin-top: 60px;">
                    <div class="promotion-container">
                        <!-- Hiển thị thông báo -->
                        <%
                            String success = request.getParameter("success");
                            String error = request.getParameter("error");
                        %>
                        
                        <% if (success != null) { %>
                        <div class="alert alert-success">
                            <% if ("create".equals(success)) { %>
                            ✅ Thêm khuyến mãi thành công!
                            <% } else if ("update".equals(success)) { %>
                            ✅ Cập nhật khuyến mãi thành công!
                            <% } else if ("delete".equals(success)) { %>
                            ✅ Xóa khuyến mãi thành công!
                            <% } else { %>
                            ✅ Thao tác thành công!
                            <% } %>
                        </div>
                        <% } else if (error != null) { %>
                        <div class="alert alert-error">
                            <% if ("empty_fields".equals(error) || "empty_promo_name".equals(error)) { %>
                            ⚠️ Vui lòng nhập đầy đủ thông tin!
                            <% } else if ("empty_discount".equals(error)) { %>
                            ⚠️ Vui lòng nhập tỷ lệ giảm giá!
                            <% } else if ("empty_start_date".equals(error)) { %>
                            ⚠️ Vui lòng chọn ngày bắt đầu!
                            <% } else if ("empty_end_date".equals(error)) { %>
                            ⚠️ Vui lòng chọn ngày kết thúc!
                            <% } else if ("promo_name_too_long".equals(error)) { %>
                            ⚠️ Tên khuyến mãi không được vượt quá 255 ký tự!
                            <% } else if ("invalid_discount".equals(error)) { %>
                            ⚠️ Tỷ lệ giảm giá không hợp lệ!
                            <% } else if ("invalid_discount_range".equals(error)) { %>
                            ⚠️ Tỷ lệ giảm giá phải từ 0 đến 100!
                            <% } else if ("invalid_dates".equals(error)) { %>
                            ⚠️ Ngày kết thúc phải sau ngày bắt đầu!
                            <% } else if ("invalid_date_format".equals(error)) { %>
                            ⚠️ Định dạng ngày không hợp lệ!
                            <% } else if ("invalid_id".equals(error)) { %>
                            ⚠️ Mã khuyến mãi không hợp lệ!
                            <% } else if ("not_found".equals(error)) { %>
                            ⚠️ Không tìm thấy khuyến mãi!
                            <% } else if ("create_failed".equals(error)) { %>
                            ❌ Thêm khuyến mãi thất bại!
                            <% } else if ("update_failed".equals(error)) { %>
                            ❌ Cập nhật khuyến mãi thất bại!
                            <% } else if ("delete_failed".equals(error)) { %>
                            ❌ Xóa khuyến mãi thất bại!
                            <% } else { %>
                            ❌ Thao tác thất bại!
                            <% } %>
                        </div>
                        <% } %>

                        <!-- Header và Search -->
                        <div class="promotion-header">
                            <h2><i class="fas fa-tags"></i> Khuyến mãi</h2>
                            <div class="header-actions">
                                <div class="search-box">
                                    <i class="fas fa-search"></i>
                                    <input type="text" id="searchInput" placeholder="Theo tên khuyến mãi" />
                                </div>
                                <button type="button" class="btn-add" onclick="openAddModal()">
                                    <i class="fas fa-plus"></i> Thêm mới
                                </button>
                            </div>
                        </div>

                        <!-- Summary Cards -->
                        <%
                            List<Promotion> allPromotions = (List<Promotion>) request.getAttribute("promotions");
                            if (allPromotions == null) allPromotions = new java.util.ArrayList<>();
                            
                            int totalPromotions = allPromotions.size();
                            int activeCount = 0;
                            int scheduledCount = 0;
                            int expiredCount = 0;
                            
                            for (Promotion p : allPromotions) {
                                if ("active".equals(p.getStatus())) activeCount++;
                                else if ("scheduled".equals(p.getStatus())) scheduledCount++;
                                else if ("expired".equals(p.getStatus())) expiredCount++;
                            }
                        %>
                        <div class="summary-cards">
                            <div class="summary-card">
                                <div class="card-icon">
                                    <i class="fas fa-percent"></i>
                                </div>
                                <div class="card-content">
                                    <div class="card-number"><%= totalPromotions %></div>
                                    <div class="card-label">Tổng khuyến mãi</div>
                                </div>
                            </div>
                            <div class="summary-card">
                                <div class="card-icon active">
                                    <i class="fas fa-play"></i>
                                </div>
                                <div class="card-content">
                                    <div class="card-number"><%= activeCount %></div>
                                    <div class="card-label">Đang hoạt động</div>
                                </div>
                            </div>
                            <div class="summary-card">
                                <div class="card-icon scheduled">
                                    <i class="fas fa-clock"></i>
                                </div>
                                <div class="card-content">
                                    <div class="card-number"><%= scheduledCount %></div>
                                    <div class="card-label">Đã lên lịch</div>
                                </div>
                            </div>
                            <div class="summary-card">
                                <div class="card-icon expired">
                                    <i class="fas fa-stop"></i>
                                </div>
                                <div class="card-content">
                                    <div class="card-number"><%= expiredCount %></div>
                                    <div class="card-label">Đã hết hạn</div>
                                </div>
                            </div>
                        </div>

                        <!-- Content Layout with Filters and Table -->
                        <div class="content-layout">
                            <!-- Filters Sidebar (Left) -->
                            <div class="filters-sidebar">
                                <h6>Bộ lọc</h6>
                                
                                <div class="filter-group">
                                    <label>Trạng thái:</label>
                                    <label class="radio-label">
                                        <input type="radio" name="statusFilter" value="all" checked onchange="filterTable()" />
                                        <span>Tất cả</span>
                                    </label>
                                    <label class="radio-label">
                                        <input type="radio" name="statusFilter" value="active" onchange="filterTable()" />
                                        <span>Đang hoạt động</span>
                                    </label>
                                    <label class="radio-label">
                                        <input type="radio" name="statusFilter" value="scheduled" onchange="filterTable()" />
                                        <span>Đã lên lịch</span>
                                    </label>
                                    <label class="radio-label">
                                        <input type="radio" name="statusFilter" value="expired" onchange="filterTable()" />
                                        <span>Đã hết hạn</span>
                                    </label>
                                </div>

                                <div class="filter-group">
                                    <label>Mức giảm giá:</label>
                                    <label class="radio-label">
                                        <input type="radio" name="discountFilter" value="all" checked onchange="filterTable()" />
                                        <span>Tất cả</span>
                                    </label>
                                    <label class="radio-label">
                                        <input type="radio" name="discountFilter" value="under15" onchange="filterTable()" />
                                        <span>Dưới 15%</span>
                                    </label>
                                    <label class="radio-label">
                                        <input type="radio" name="discountFilter" value="15-25" onchange="filterTable()" />
                                        <span>15% - 25%</span>
                                    </label>
                                    <label class="radio-label">
                                        <input type="radio" name="discountFilter" value="over25" onchange="filterTable()" />
                                        <span>Trên 25%</span>
                                    </label>
                                </div>
                            </div>

                            <!-- Promotion Table (Right) -->
                            <div class="promotion-table-wrapper">
                                <div class="promotion-table-container">
                                    <table class="promotion-table">
                                <thead>
                                    <tr>
                                        <th>Mã KM</th>
                                        <th>Tên khuyến mãi</th>
                                        <th>Giảm giá</th>
                                        <th>Thời gian</th>
                                        <th>Phạm vi</th>
                                        <th>Trạng thái</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                        if (allPromotions != null && !allPromotions.isEmpty()) {
                                            for (Promotion promo : allPromotions) {
                                    %>
                                    <tr data-status="<%= promo.getStatus() %>" 
                                        data-discount="<%= promo.getDiscountPercent() %>"
                                        data-name="<%= promo.getPromoName().toLowerCase() %>">
                                        <td><%= promo.getPromotionId() %></td>
                                        <td><strong><%= promo.getPromoName() %></strong></td>
                                        <td>
                                            <span class="discount-badge">
                                                <%= promo.getDiscountPercent() %>%
                                            </span>
                                        </td>
                                        <td>
                                            <%= dateFormat.format(promo.getStartDate()) %> đến 
                                            <%= dateFormat.format(promo.getEndDate()) %>
                                        </td>
                                        <td>
                                            <%= promo.getBranchCount() %> chi nhánh, 
                                            <%= promo.getProductCount() %> sản phẩm
                                        </td>
                                        <td>
                                            <%
                                                String statusClass = "";
                                                String statusText = "";
                                                if ("active".equals(promo.getStatus())) {
                                                    statusClass = "status-active";
                                                    statusText = "Còn " + promo.getDaysRemaining() + " ngày";
                                                } else if ("scheduled".equals(promo.getStatus())) {
                                                    statusClass = "status-scheduled";
                                                    statusText = "Đã lên lịch";
                                                } else if ("expired".equals(promo.getStatus())) {
                                                    statusClass = "status-expired";
                                                    statusText = "Đã hết hạn";
                                                }
                                            %>
                                            <span class="status <%= statusClass %>">
                                                <%= statusText %>
                                            </span>
                                        </td>
                                        <td class="action-buttons">
                                            <button type="button" class="btn-view" 
                                                    onclick="viewPromotion(<%= promo.getPromotionId() %>)"
                                                    title="Xem chi tiết">
                                                <i class="fas fa-eye"></i>
                                            </button>
                                            <button type="button" class="btn-edit" 
                                                    onclick="editPromotion(<%= promo.getPromotionId() %>)"
                                                    title="Sửa">
                                                <i class="fas fa-edit"></i>
                                            </button>
                                            <button type="button" class="btn-delete" 
                                                    onclick="deletePromotion(<%= promo.getPromotionId() %>, '<%= promo.getPromoName() %>')"
                                                    title="Xóa">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </td>
                                    </tr>
                                    <%
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
                            </div>
                        </div>

                        <!-- Pagination -->
                        <div class="pagination">
                            <div class="pagination-info">
                                Hiển thị 1 - <%= totalPromotions %> / Tổng số <%= totalPromotions %> khuyến mãi
                            </div>
                            <div class="pagination-controls">
                                <button class="page-btn" disabled><i class="fas fa-angle-double-left"></i></button>
                                <button class="page-btn" disabled><i class="fas fa-angle-left"></i></button>
                                <button class="page-btn active">1</button>
                                <button class="page-btn" disabled><i class="fas fa-angle-right"></i></button>
                                <button class="page-btn" disabled><i class="fas fa-angle-double-right"></i></button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <!-- Add/Edit Modal -->
    <div id="promoModal" class="modal">
        <div class="modal-content-large">
            <div class="modal-header">
                <div class="modal-header-left">
                    <div class="modal-icon">
                        <i class="fas fa-tags"></i>
                    </div>
                    <div>
                        <h3 id="modalTitleText">Thêm khuyến mãi mới</h3>
                        <p class="modal-subtitle" id="modalSubtitle">Điền thông tin để tạo khuyến mãi mới</p>
                    </div>
                </div>
                <button type="button" class="modal-close" onclick="closeModal()">
                    <i class="fas fa-times"></i>
                </button>
            </div>
            <form id="promoForm" method="post" onsubmit="return validateForm()">
                <input type="hidden" name="action" id="formAction" value="create" />
                <input type="hidden" name="promotionId" id="promotionId" />
                
                <div class="form-section">
                    <h4 class="form-section-title">
                        <i class="fas fa-info-circle"></i> Thông tin cơ bản
                    </h4>
                    
                    <div class="form-group">
                        <label>Tên khuyến mãi <span class="required">*</span></label>
                        <div class="input-wrapper">
                            <i class="fas fa-tag input-icon"></i>
                            <input type="text" name="promoName" id="promoName" maxlength="255" 
                                   placeholder="Nhập tên khuyến mãi" required />
                        </div>
                        <span class="error-message" id="promoNameError"></span>
                    </div>
                    
                    <div class="form-group">
                        <label>Tỷ lệ giảm giá (%) <span class="required">*</span></label>
                        <div class="input-wrapper">
                            <i class="fas fa-percent input-icon"></i>
                            <input type="number" name="discountPercent" id="discountPercent" 
                                   step="0.01" min="0" max="100" placeholder="0.00" required />
                        </div>
                        <span class="error-message" id="discountPercentError"></span>
                    </div>
                </div>
                
                <div class="form-section">
                    <h4 class="form-section-title">
                        <i class="fas fa-building"></i> Phạm vi áp dụng
                    </h4>
                    
                    <div class="form-group">
                        <label>Branch áp dụng khuyến mãi</label>
                        <div class="dropdown-search-container">
                            <div class="dropdown-search">
                                <i class="fas fa-search search-icon-left"></i>
                                <input type="text" id="branchSearch" class="search-input" 
                                       placeholder="Tìm kiếm chi nhánh..." 
                                       onfocus="showDropdown('branch')"
                                       oninput="filterDropdown('branch', this.value)" />
                            </div>
                            <div class="dropdown-list" id="branchDropdown" style="display: none;">
                            <%
                                List<Branch> branches = (List<Branch>) request.getAttribute("branches");
                                List<Integer> selectedBranchIds = (List<Integer>) request.getAttribute("selectedBranchIds");
                                if (branches == null) branches = new java.util.ArrayList<>();
                                if (selectedBranchIds == null) selectedBranchIds = new java.util.ArrayList<>();
                                for (Branch branch : branches) {
                                    boolean isSelected = selectedBranchIds.contains(branch.getBranchId());
                            %>
                            <label class="dropdown-item">
                                <input type="checkbox" name="branchIds" value="<%= branch.getBranchId() %>"
                                       class="branch-checkbox" 
                                       <%= isSelected ? "checked" : "" %> />
                                <span><%= branch.getBranchName() %></span>
                            </label>
                            <%
                                }
                            %>
                        </div>
                            <div class="selected-count" id="branchCount">
                                <i class="fas fa-check-circle"></i> Đã chọn: 0
                            </div>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label>Sản phẩm áp dụng khuyến mãi</label>
                        <div class="dropdown-search-container">
                            <div class="dropdown-search">
                                <i class="fas fa-search search-icon-left"></i>
                                <input type="text" id="productSearch" class="search-input" 
                                       placeholder="Tìm kiếm sản phẩm..." 
                                       onfocus="showDropdown('product')"
                                       oninput="filterDropdown('product', this.value)" />
                            </div>
                            <div class="dropdown-list" id="productDropdown" style="display: none;">
                            <%
                                List<ProductDetail> products = (List<ProductDetail>) request.getAttribute("products");
                                List<Integer> selectedProductDetailIds = (List<Integer>) request.getAttribute("selectedProductDetailIds");
                                if (products == null) products = new java.util.ArrayList<>();
                                if (selectedProductDetailIds == null) selectedProductDetailIds = new java.util.ArrayList<>();
                                for (ProductDetail product : products) {
                                    boolean isSelected = selectedProductDetailIds.contains(product.getProductDetailID());
                            %>
                            <label class="dropdown-item">
                                <input type="checkbox" name="productDetailIds" value="<%= product.getProductDetailID() %>"
                                       class="product-checkbox"
                                       <%= isSelected ? "checked" : "" %> />
                                <span><%= product.getProductName() != null ? product.getProductName() : "N/A" %> - <%= product.getProductCode() != null ? product.getProductCode() : "" %></span>
                            </label>
                            <%
                                }
                            %>
                        </div>
                            <div class="selected-count" id="productCount">
                                <i class="fas fa-check-circle"></i> Đã chọn: 0
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="form-section">
                    <h4 class="form-section-title">
                        <i class="fas fa-calendar-alt"></i> Thời gian áp dụng
                    </h4>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label>Ngày bắt đầu <span class="required">*</span></label>
                            <div class="input-wrapper">
                                <i class="fas fa-calendar input-icon"></i>
                                <input type="date" name="startDate" id="startDate" required />
                            </div>
                            <span class="error-message" id="startDateError"></span>
                        </div>
                        
                        <div class="form-group">
                            <label>Ngày kết thúc <span class="required">*</span></label>
                            <div class="input-wrapper">
                                <i class="fas fa-calendar input-icon"></i>
                                <input type="date" name="endDate" id="endDate" required />
                            </div>
                            <span class="error-message" id="endDateError"></span>
                        </div>
                    </div>
                </div>
                
                <div class="modal-footer">
                    <button type="button" class="btn-cancel" onclick="closeModal()">Hủy</button>
                    <button type="submit" class="btn-submit" id="submitBtn">Lưu</button>
                </div>
            </form>
        </div>
    </div>

    <script>
        // Search functionality
        document.getElementById('searchInput').addEventListener('input', function() {
            filterTable();
        });

        function filterTable() {
            const searchValue = document.getElementById('searchInput').value.toLowerCase();
            const statusFilter = document.querySelector('input[name="statusFilter"]:checked').value;
            const discountFilter = document.querySelector('input[name="discountFilter"]:checked').value;
            const rows = document.querySelectorAll('.promotion-table tbody tr');

            rows.forEach(row => {
                if (row.classList.contains('no-data')) return;

                const name = row.getAttribute('data-name') || '';
                const status = row.getAttribute('data-status') || '';
                const discount = parseFloat(row.getAttribute('data-discount')) || 0;

                // Search filter
                const matchesSearch = name.includes(searchValue);

                // Status filter
                let matchesStatus = true;
                if (statusFilter !== 'all') {
                    matchesStatus = status === statusFilter;
                }

                // Discount filter
                let matchesDiscount = true;
                if (discountFilter !== 'all') {
                    if (discountFilter === 'under15') {
                        matchesDiscount = discount < 15;
                    } else if (discountFilter === '15-25') {
                        matchesDiscount = discount >= 15 && discount <= 25;
                    } else if (discountFilter === 'over25') {
                        matchesDiscount = discount > 25;
                    }
                }

                // Show/hide row
                if (matchesSearch && matchesStatus && matchesDiscount) {
                    row.style.display = '';
                } else {
                    row.style.display = 'none';
                }
            });
        }

        // Modal functions
        function openAddModal() {
            const modalTitleText = document.getElementById('modalTitleText');
            const modalSubtitle = document.querySelector('.modal-subtitle');
            const submitBtn = document.getElementById('submitBtn');
            const formAction = document.getElementById('formAction');
            const promotionId = document.getElementById('promotionId');
            const promoForm = document.getElementById('promoForm');
            
            const modalSubtitleEl = document.getElementById('modalSubtitle');
            if (modalTitleText) modalTitleText.textContent = 'Thêm khuyến mãi mới';
            if (modalSubtitleEl) modalSubtitleEl.textContent = 'Điền thông tin để tạo khuyến mãi mới';
            if (submitBtn) submitBtn.textContent = 'Thêm mới';
            if (formAction) formAction.value = 'create';
            if (promotionId) promotionId.value = '';
            if (promoForm) promoForm.reset();
            
            // Reset all checkboxes
            document.querySelectorAll('.branch-checkbox, .product-checkbox').forEach(cb => {
                cb.checked = false;
            });
            
            // Reset dropdowns
            const branchSearch = document.getElementById('branchSearch');
            const productSearch = document.getElementById('productSearch');
            if (branchSearch) {
                branchSearch.value = '';
                filterDropdown('branch', '');
            }
            if (productSearch) {
                productSearch.value = '';
                filterDropdown('product', '');
            }
            
            // Hide all dropdowns
            document.querySelectorAll('.dropdown-list').forEach(dropdown => {
                dropdown.style.display = 'none';
            });
            
            updateSelectedCounts();
            document.getElementById('promoModal').style.display = 'flex';
        }

        function editPromotion(id) {
            window.location.href = 'Promotion?action=edit&id=' + id;
        }

        function viewPromotion(id) {
            window.location.href = 'Promotion?action=view&id=' + id;
        }

        function showDropdown(type) {
            document.getElementById(type + 'Dropdown').style.display = 'block';
        }

        // Close dropdown when clicking outside
        document.addEventListener('click', function(event) {
            if (!event.target.closest('.dropdown-search-container')) {
                document.querySelectorAll('.dropdown-list').forEach(dropdown => {
                    dropdown.style.display = 'none';
                });
            }
        });
        
        // Load edit form data - Wait for page to fully load
        <%
            Promotion editPromotion = (Promotion) request.getAttribute("promotion");
            if (editPromotion != null) {
        %>
        console.log('Edit mode detected for promotion ID: <%= editPromotion.getPromotionId() %>');
        (function() {
            let retryCount = 0;
            const maxRetries = 100;
            
            function loadEditForm() {
                const modalTitleText = document.getElementById('modalTitleText');
                const submitBtn = document.getElementById('submitBtn');
                const formAction = document.getElementById('formAction');
                const promotionId = document.getElementById('promotionId');
                const promoName = document.getElementById('promoName');
                const discountPercent = document.getElementById('discountPercent');
                const startDate = document.getElementById('startDate');
                const endDate = document.getElementById('endDate');
                const modal = document.getElementById('promoModal');
                
                if (!modalTitleText || !submitBtn || !formAction || !promotionId || 
                    !promoName || !discountPercent || !startDate || !endDate || !modal) {
                    // Elements not ready, retry
                    retryCount++;
                    console.log('Retry ' + retryCount + ': Waiting for elements...');
                    if (retryCount < maxRetries) {
                        setTimeout(loadEditForm, 50);
                    } else {
                        console.error('Failed to load edit form: elements not found after ' + maxRetries + ' retries');
                    }
                    return;
                }
                
                console.log('Loading edit form for promotion ID: <%= editPromotion.getPromotionId() %>');
                
                const modalSubtitle = document.getElementById('modalSubtitle');
                if (modalTitleText) modalTitleText.textContent = 'Chỉnh sửa khuyến mãi';
                if (modalSubtitle) modalSubtitle.textContent = 'Cập nhật thông tin khuyến mãi';
                if (submitBtn) submitBtn.textContent = 'Cập nhật';
                if (formAction) formAction.value = 'update';
                if (promotionId) promotionId.value = '<%= editPromotion.getPromotionId() %>';
                
                <%
                    String promoNameValue = editPromotion.getPromoName();
                    if (promoNameValue != null) {
                        // Escape for JavaScript
                        promoNameValue = promoNameValue.replace("\\", "\\\\")
                                                       .replace("'", "\\'")
                                                       .replace("\"", "\\\"")
                                                       .replace("\n", "\\n")
                                                       .replace("\r", "\\r");
                %>
                if (promoName) promoName.value = '<%= promoNameValue %>';
                <%
                    } else {
                %>
                if (promoName) promoName.value = '';
                <%
                    }
                %>
                
                if (discountPercent) discountPercent.value = '<%= editPromotion.getDiscountPercent() != null ? editPromotion.getDiscountPercent() : "" %>';
                
                <%
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    if (editPromotion.getStartDate() != null) {
                %>
                if (startDate) startDate.value = '<%= sdf.format(editPromotion.getStartDate()) %>';
                <%
                    } else {
                %>
                if (startDate) startDate.value = '';
                <%
                    }
                    if (editPromotion.getEndDate() != null) {
                %>
                if (endDate) endDate.value = '<%= sdf.format(editPromotion.getEndDate()) %>';
                <%
                    } else {
                %>
                if (endDate) endDate.value = '';
                <%
                    }
                %>
                
                // Reset search inputs
                const branchSearch = document.getElementById('branchSearch');
                const productSearch = document.getElementById('productSearch');
                if (branchSearch) {
                    branchSearch.value = '';
                }
                if (productSearch) {
                    productSearch.value = '';
                }
                
                // Hide all dropdowns initially
                document.querySelectorAll('.dropdown-list').forEach(dropdown => {
                    dropdown.style.display = 'none';
                });
                
                // Show modal
                console.log('Opening modal for edit...');
                modal.style.display = 'flex';
                
                // Wait for DOM to fully render checkboxes, then update counts
                // Checkboxes are already checked from server-side rendering
                setTimeout(function() {
                    // Verify checkboxes are checked
                    const checkedBranches = document.querySelectorAll('.branch-checkbox:checked').length;
                    const checkedProducts = document.querySelectorAll('.product-checkbox:checked').length;
                    console.log('Checked branches: ' + checkedBranches + ', Checked products: ' + checkedProducts);
                    
                    updateSelectedCounts();
                }, 400);
            }
            
            // Try to load immediately if DOM is ready
            if (document.readyState === 'loading') {
                console.log('DOM is loading, waiting for DOMContentLoaded...');
                document.addEventListener('DOMContentLoaded', loadEditForm);
            } else {
                // DOM already loaded, wait a bit for everything to render
                console.log('DOM already loaded, starting loadEditForm...');
                setTimeout(loadEditForm, 200);
            }
        })();
        <%
            }
        %>
        
        // Dropdown search functions
        function filterDropdown(type, searchValue) {
            const dropdown = document.getElementById(type + 'Dropdown');
            if (!dropdown) return;
            
            const items = dropdown.querySelectorAll('.dropdown-item');
            const search = searchValue.toLowerCase();
            
            items.forEach(item => {
                const span = item.querySelector('span');
                if (!span) return;
                const text = span.textContent.toLowerCase();
                if (text.includes(search)) {
                    item.style.display = '';
                } else {
                    item.style.display = 'none';
                }
            });
        }
        
        // Update selected counts
        function updateSelectedCounts() {
            const branchCheckboxes = document.querySelectorAll('.branch-checkbox:checked');
            const productCheckboxes = document.querySelectorAll('.product-checkbox:checked');
            
            const branchCountEl = document.getElementById('branchCount');
            const productCountEl = document.getElementById('productCount');
            
            if (branchCountEl) {
                branchCountEl.innerHTML = '<i class="fas fa-check-circle"></i> Đã chọn: ' + branchCheckboxes.length;
            }
            if (productCountEl) {
                productCountEl.innerHTML = '<i class="fas fa-check-circle"></i> Đã chọn: ' + productCheckboxes.length;
            }
        }
        
        // Add event listeners for checkboxes - use event delegation
        document.addEventListener('change', function(e) {
            if (e.target.classList.contains('branch-checkbox') || 
                e.target.classList.contains('product-checkbox')) {
                updateSelectedCounts();
            }
        });
        
        // Update counts when page loads
        document.addEventListener('DOMContentLoaded', function() {
            updateSelectedCounts();
            
            // Also update when modal opens
            const modal = document.getElementById('promoModal');
            if (modal) {
                const observer = new MutationObserver(function(mutations) {
                    if (modal.style.display === 'flex') {
                        // Delay to ensure all checkboxes are rendered
                        setTimeout(updateSelectedCounts, 150);
                    }
                });
                observer.observe(modal, { attributes: true, attributeFilter: ['style'] });
            }
        });
        
        // Form validation
        function validateForm() {
            console.log('=== validateForm called ===');
            let isValid = true;
            
            // Clear previous errors
            document.querySelectorAll('.error-message').forEach(el => el.textContent = '');
            
            // Log form data for debugging
            const formActionEl = document.getElementById('formAction');
            const promotionIdEl = document.getElementById('promotionId');
            const promoNameEl = document.getElementById('promoName');
            const discountPercentEl = document.getElementById('discountPercent');
            const startDateEl = document.getElementById('startDate');
            const endDateEl = document.getElementById('endDate');
            
            if (formActionEl && promotionIdEl) {
                console.log('Form Action:', formActionEl.value);
                console.log('Promotion ID:', promotionIdEl.value);
                console.log('Promo Name:', promoNameEl ? promoNameEl.value : 'N/A');
                console.log('Discount:', discountPercentEl ? discountPercentEl.value : 'N/A');
                console.log('Start Date:', startDateEl ? startDateEl.value : 'N/A');
                console.log('End Date:', endDateEl ? endDateEl.value : 'N/A');
            }
            
            // Log all form fields
            const form = document.getElementById('promoForm');
            if (form) {
                const formData = new FormData(form);
                console.log('All form data:');
                for (let [key, value] of formData.entries()) {
                    console.log('  ' + key + ': ' + value);
                }
            }
            
            // Validate promo name
            const promoName = document.getElementById('promoName').value.trim();
            if (!promoName) {
                document.getElementById('promoNameError').textContent = 'Vui lòng nhập tên khuyến mãi';
                isValid = false;
            } else if (promoName.length > 255) {
                document.getElementById('promoNameError').textContent = 'Tên khuyến mãi không được vượt quá 255 ký tự';
                isValid = false;
            }
            
            // Validate discount
            const discount = parseFloat(document.getElementById('discountPercent').value);
            if (isNaN(discount)) {
                document.getElementById('discountPercentError').textContent = 'Vui lòng nhập tỷ lệ giảm giá';
                isValid = false;
            } else if (discount < 0 || discount > 100) {
                document.getElementById('discountPercentError').textContent = 'Tỷ lệ giảm giá phải từ 0 đến 100';
                isValid = false;
            }
            
            // Validate dates
            const startDate = new Date(document.getElementById('startDate').value);
            const endDate = new Date(document.getElementById('endDate').value);
            
            if (!document.getElementById('startDate').value) {
                document.getElementById('startDateError').textContent = 'Vui lòng chọn ngày bắt đầu';
                isValid = false;
            } else if (!document.getElementById('endDate').value) {
                document.getElementById('endDateError').textContent = 'Vui lòng chọn ngày kết thúc';
                isValid = false;
            } else if (startDate > endDate) {
                document.getElementById('endDateError').textContent = 'Ngày kết thúc phải sau ngày bắt đầu';
                isValid = false;
            }
            
            console.log('Form validation result:', isValid);
            if (!isValid) {
                console.log('Form validation failed, preventing submit');
            } else {
                console.log('Form validation passed, submitting...');
            }
            return isValid;
        }
        
        // Add form submit event listener for debugging
        document.addEventListener('DOMContentLoaded', function() {
            const form = document.getElementById('promoForm');
            if (form) {
                form.addEventListener('submit', function(e) {
                    console.log('=== Form submit event triggered ===');
                    const formAction = document.getElementById('formAction');
                    const promotionId = document.getElementById('promotionId');
                    console.log('Action:', formAction ? formAction.value : 'N/A');
                    console.log('Promotion ID:', promotionId ? promotionId.value : 'N/A');
                });
            }
        });

        function deletePromotion(id, name) {
            if (confirm('Bạn có chắc chắn muốn xóa khuyến mãi "' + name + '"?')) {
                window.location.href = 'Promotion?action=delete&id=' + id;
            }
        }

        function closeModal() {
            document.getElementById('promoModal').style.display = 'none';
        }

        // Close modal when clicking outside
        window.onclick = function(event) {
            const modal = document.getElementById('promoModal');
            if (event.target === modal) {
                closeModal();
            }
        }
    </script>
</body>
</html>

