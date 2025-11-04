<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chỉnh Sửa Khuyến Mãi - VIP Store</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
    <style>
        .form-card {
            background: white;
            border-radius: 15px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            padding: 30px;
        }
        .form-section {
            border-bottom: 2px solid #f8f9fa;
            padding-bottom: 20px;
            margin-bottom: 25px;
        }
        .form-section:last-child {
            border-bottom: none;
            margin-bottom: 0;
        }
        .section-title {
            color: #495057;
            font-weight: 600;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
        }
        .section-icon {
            width: 30px;
            height: 30px;
            background: linear-gradient(135deg, #007bff, #0056b3);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            margin-right: 10px;
            font-size: 14px;
        }
        .form-control, .form-select {
            border-radius: 10px;
            border: 2px solid #e9ecef;
            padding: 12px 15px;
            transition: all 0.3s ease;
        }
        .form-control:focus, .form-select:focus {
            border-color: #007bff;
            box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
        }
        .btn-custom {
            padding: 12px 30px;
            border-radius: 10px;
            font-weight: 500;
            transition: all 0.3s ease;
        }
        .product-item {
            display: flex;
            align-items: center;
            padding: 8px 12px;
            margin-bottom: 5px;
            border: 1px solid #e9ecef;
            border-radius: 8px;
            transition: all 0.2s ease;
            cursor: pointer;
        }
        .product-item:hover {
            background-color: #f8f9fa;
            border-color: #007bff;
        }
        .product-item.selected {
            background-color: #e3f2fd;
            border-color: #2196f3;
        }
        .product-checkbox {
            margin-right: 10px;
        }
        .product-info {
            flex: 1;
        }
        .product-name {
            font-weight: 500;
            color: #333;
            margin-bottom: 2px;
        }
        .product-sku {
            font-size: 0.85em;
            color: #666;
        }
        .product-selection-area {
            background: #fafafa;
            max-height: 300px;
            overflow-y: auto;
            border: 2px solid #e9ecef;
            border-radius: 10px;
            padding: 15px;
        }
    </style>
</head>
<body style="background-color: #f8f9fa;">
    <!-- Include Header -->
    <jsp:include page="header_admin.jsp"/>

    <div class="container-fluid px-4" style="padding-top: 100px;">
        <!-- Page Header -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h2 class="fw-bold text-dark mb-1">
                    <i class="fas fa-edit text-primary me-2"></i>
                    Chỉnh Sửa Khuyến Mãi
                </h2>
                <p class="text-muted mb-0">Cập nhật thông tin chương trình khuyến mãi</p>
            </div>
            <a href="Promotions" class="btn btn-outline-secondary">
                <i class="fas fa-arrow-left me-2"></i>
                Quay Lại
            </a>
        </div>

        <div class="row">
            <div class="col-12">
                <div class="form-card">
                    <form method="post" action="Promotions" id="promotionForm">
                        <input type="hidden" name="action" value="update">
                        <input type="hidden" name="promotionId" value="${promotion.promotionId}">
                        
                        <!-- Basic Information Section -->
                        <div class="form-section">
                            <h4 class="section-title">
                                <span class="section-icon">
                                    <i class="fas fa-info"></i>
                                </span>
                                Thông Tin Cơ Bản
                            </h4>
                            
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="promoName" class="form-label fw-medium">
                                            <i class="fas fa-tag text-primary me-2"></i>
                                            Tên Khuyến Mãi <span class="text-danger">*</span>
                                        </label>
                                        <input type="text" class="form-control" id="promoName" name="promoName" 
                                               value="${promotion.promoName}" required>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="discountPercent" class="form-label fw-medium">
                                            <i class="fas fa-percent text-success me-2"></i>
                                            Phần Trăm Giảm Giá <span class="text-danger">*</span>
                                        </label>
                                        <div class="input-group">
                                            <input type="number" class="form-control" id="discountPercent" name="discountPercent" 
                                                   min="1" max="100" step="0.01" value="${promotion.discountPercent}" required>
                                            <span class="input-group-text">%</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="startDate" class="form-label fw-medium">
                                            <i class="fas fa-calendar-alt text-info me-2"></i>
                                            Ngày Bắt Đầu <span class="text-danger">*</span>
                                        </label>
                                        <input type="date" class="form-control" id="startDate" name="startDate" 
                                               value="<fmt:formatDate value="${promotion.startDate}" pattern="yyyy-MM-dd"/>" required>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="endDate" class="form-label fw-medium">
                                            <i class="fas fa-calendar-check text-warning me-2"></i>
                                            Ngày Kết Thúc <span class="text-danger">*</span>
                                        </label>
                                        <input type="date" class="form-control" id="endDate" name="endDate" 
                                               value="<fmt:formatDate value="${promotion.endDate}" pattern="yyyy-MM-dd"/>" required>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Branch Selection Section -->
                        <div class="form-section">
                            <h4 class="section-title">
                                <span class="section-icon">
                                    <i class="fas fa-store"></i>
                                </span>
                                Áp Dụng Cho Chi Nhánh
                            </h4>
                            
                            <div class="mb-3">
                                <label class="form-label fw-medium">
                                    <i class="fas fa-map-marker-alt text-danger me-2"></i>
                                    Chọn Chi Nhánh <span class="text-danger">*</span>
                                </label>
                                
                                <!-- Select All Branch Option -->
                                <div class="form-check mb-3 p-3 border rounded bg-light">
                                    <input class="form-check-input" type="checkbox" id="selectAllBranches">
                                    <label class="form-check-label fw-medium" for="selectAllBranches">
                                        <i class="fas fa-check-double text-primary me-2"></i>
                                        Chọn tất cả chi nhánh
                                    </label>
                                </div>
                                
                                <!-- Branch Checkboxes -->
                                <div class="row" id="branchCheckboxes">
                                    <c:forEach var="branch" items="${branches}">
                                        <div class="col-md-6 mb-2">
                                            <div class="form-check">
                                                <input class="form-check-input branch-checkbox" type="checkbox" 
                                                       name="branchIds" value="${branch.branchId}" 
                                                       id="branch_${branch.branchId}"
                                                       <c:if test="${selectedBranchIds.contains(branch.branchId)}">checked</c:if>>
                                                <label class="form-check-label" for="branch_${branch.branchId}">
                                                    <strong>${branch.branchName}</strong><br>
                                                    <small class="text-muted">${branch.address}</small>
                                                </label>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                                <div class="form-text">Chọn một hoặc nhiều chi nhánh để áp dụng khuyến mãi (để trống = áp dụng tất cả)</div>
                            </div>
                        </div>

                        <!-- Product Selection Section -->
                        <div class="form-section">
                            <h4 class="section-title">
                                <span class="section-icon">
                                    <i class="fas fa-box"></i>
                                </span>
                                Sản Phẩm Áp Dụng
                            </h4>
                            
                            <div class="mb-3">
                                <div class="d-flex justify-content-between align-items-center mb-3">
                                    <label class="form-label fw-medium">
                                        <i class="fas fa-shopping-bag text-success me-2"></i>
                                        Chọn Sản Phẩm
                                    </label>
                                    <div>
                                        <button type="button" class="btn btn-outline-primary btn-sm" onclick="selectAllProducts()">
                                            <i class="fas fa-check-double me-1"></i>Chọn Tất Cả
                                        </button>
                                        <button type="button" class="btn btn-outline-secondary btn-sm" onclick="clearAllProducts()">
                                            <i class="fas fa-times me-1"></i>Bỏ Chọn Tất Cả
                                        </button>
                                        <button type="button" class="btn btn-outline-info btn-sm" onclick="searchProducts()">
                                            <i class="fas fa-search me-1"></i>Tìm Kiếm
                                        </button>
                                    </div>
                                </div>

                                <!-- Search input for products -->
                                <div class="mb-3">
                                    <div class="input-group">
                                        <span class="input-group-text"><i class="fas fa-search"></i></span>
                                        <input type="text" class="form-control" id="productSearch" 
                                               placeholder="Tìm kiếm sản phẩm theo tên hoặc mã...">
                                        <button class="btn btn-outline-secondary" type="button" onclick="loadProducts()">
                                            <i class="fas fa-refresh"></i>
                                        </button>
                                    </div>
                                </div>
                                
                                <!-- Product selection area -->
                                <div class="product-selection-area">
                                    <div id="productsList">
                                        <div class="text-center text-muted py-4">
                                            <i class="fas fa-spinner fa-spin fa-2x mb-2"></i>
                                            <p>Đang tải danh sách sản phẩm...</p>
                                        </div>
                                    </div>
                                </div>
                                
                                <!-- Hidden select for form submission -->
                                <select class="form-control d-none" id="productDetailIds" name="productDetailIds" multiple="multiple">
                                </select>
                                
                                <div class="form-text mt-2">
                                    <i class="fas fa-info-circle me-1"></i>
                                    <strong>Lưu ý:</strong> Để trống nếu muốn áp dụng cho tất cả sản phẩm. 
                                    Chọn sản phẩm cụ thể để áp dụng khuyến mãi có chọn lọc.
                                </div>
                            </div>
                        </div>

                        <!-- Submit Buttons -->
                        <div class="d-flex justify-content-end gap-3">
                            <a href="Promotions" class="btn btn-outline-secondary btn-custom">
                                <i class="fas fa-times me-2"></i>
                                Hủy
                            </a>
                            <button type="submit" class="btn btn-primary btn-custom">
                                <i class="fas fa-save me-2"></i>
                                Cập Nhật Khuyến Mãi
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
    
    <script>
        let allProducts = [];
        let selectedProductIds = new Set();

        // Initialize selectedProductIds from server data
        <c:forEach var="productId" items="${selectedProductIds}">
            selectedProductIds.add(${productId});
        </c:forEach>

        $(document).ready(function() {
            // Handle select all branches checkbox
            $('#selectAllBranches').change(function() {
                const isChecked = $(this).is(':checked');
                $('.branch-checkbox').prop('checked', isChecked);
            });
            
            // Handle individual branch checkboxes
            $('.branch-checkbox').change(function() {
                const totalBranches = $('.branch-checkbox').length;
                const checkedBranches = $('.branch-checkbox:checked').length;
                $('#selectAllBranches').prop('checked', totalBranches === checkedBranches);
            });

            // Check if all branches are selected initially
            const totalBranches = $('.branch-checkbox').length;
            const checkedBranches = $('.branch-checkbox:checked').length;
            $('#selectAllBranches').prop('checked', totalBranches === checkedBranches);

            // Load products on page load
            loadProducts();

            // Search products on input
            $('#productSearch').on('input', function() {
                const searchTerm = $(this).val();
                filterProducts(searchTerm);
            });

            // Set minimum date to today for end date when start date changes
            document.getElementById('startDate').addEventListener('change', function() {
                document.getElementById('endDate').min = this.value;
            });
        });

        function loadProducts() {
            $('#productsList').html(
                '<div class="text-center text-muted py-4">' +
                    '<i class="fas fa-spinner fa-spin fa-2x mb-2"></i>' +
                    '<p>Đang tải danh sách sản phẩm...</p>' +
                '</div>'
            );
            
            $.ajax({
                url: 'Promotions',
                dataType: 'json',
                data: {
                    action: 'loadProducts',
                    search: ''
                },
                success: function(data) {
                    allProducts = data.items;
                    renderProducts(allProducts);
                },
                error: function() {
                    $('#productsList').html(
                        '<div class="no-products">' +
                            '<i class="fas fa-exclamation-triangle fa-2x mb-2 text-warning"></i>' +
                            '<p>Không thể tải danh sách sản phẩm</p>' +
                            '<button class="btn btn-outline-primary" onclick="loadProducts()">Thử lại</button>' +
                        '</div>'
                    );
                }
            });
        }

        function renderProducts(products) {
            if (products.length === 0) {
                $('#productsList').html(
                    '<div class="no-products">' +
                        '<i class="fas fa-inbox fa-2x mb-2 text-muted"></i>' +
                        '<p>Không tìm thấy sản phẩm nào</p>' +
                    '</div>'
                );
                return;
            }

            let html = '';
            products.forEach(function(product) {
                const isSelected = selectedProductIds.has(product.id);
                const parts = product.text.split(' - ');
                const productName = parts[0] || product.text;
                const productSku = parts[1] || '';

                html += '<div class="product-item ' + (isSelected ? 'selected' : '') + '" onclick="toggleProduct(' + product.id + ')">' +
                    '<input type="checkbox" class="product-checkbox" ' + (isSelected ? 'checked' : '') + ' ' +
                           'onchange="toggleProduct(' + product.id + ')" onclick="event.stopPropagation()">' +
                    '<div class="product-info">' +
                        '<div class="product-name">' + productName + '</div>' +
                        (productSku ? '<div class="product-sku">Mã: ' + productSku + '</div>' : '') +
                    '</div>' +
                '</div>';
            });

            $('#productsList').html(html);
            updateSelectedCount();
        }

        function filterProducts(searchTerm) {
            if (!searchTerm) {
                renderProducts(allProducts);
                return;
            }

            const filtered = allProducts.filter(product => 
                product.text.toLowerCase().includes(searchTerm.toLowerCase())
            );
            renderProducts(filtered);
        }

        function toggleProduct(productId) {
            if (selectedProductIds.has(productId)) {
                selectedProductIds.delete(productId);
            } else {
                selectedProductIds.add(productId);
            }
            
            // Update checkbox state
            const checkbox = $('.product-item input[onchange*="' + productId + '"]');
            checkbox.prop('checked', selectedProductIds.has(productId));
            
            // Update item appearance
            const item = checkbox.closest('.product-item');
            if (selectedProductIds.has(productId)) {
                item.addClass('selected');
            } else {
                item.removeClass('selected');
            }
            
            updateHiddenSelect();
            updateSelectedCount();
        }

        function updateHiddenSelect() {
            const select = $('#productDetailIds');
            select.empty();
            
            selectedProductIds.forEach(function(productId) {
                const option = new Option('', productId, true, true);
                select.append(option);
            });
        }

        function updateSelectedCount() {
            const count = selectedProductIds.size;
            const countText = count > 0 ? ' (' + count + ' đã chọn)' : '';
            // You can update UI to show count if needed
        }

        function searchProducts() {
            const searchTerm = $('#productSearch').val();
            filterProducts(searchTerm);
        }

        function selectAllProducts() {
            // Get currently visible products (after filtering)
            const visibleProducts = $('#productsList .product-item input[type="checkbox"]');
            
            visibleProducts.each(function() {
                const onChangeAttr = $(this).attr('onchange');
                const productId = parseInt(onChangeAttr.match(/toggleProduct\((\d+)\)/)[1]);
                selectedProductIds.add(productId);
                $(this).prop('checked', true);
                $(this).closest('.product-item').addClass('selected');
            });
            
            updateHiddenSelect();
            updateSelectedCount();
        }

        function clearAllProducts() {
            selectedProductIds.clear();
            $('#productsList .product-item input[type="checkbox"]').prop('checked', false);
            $('#productsList .product-item').removeClass('selected');
            updateHiddenSelect();
            updateSelectedCount();
        }

        // Form validation
        document.getElementById('promotionForm').addEventListener('submit', function(e) {
            const startDate = new Date(document.getElementById('startDate').value);
            const endDate = new Date(document.getElementById('endDate').value);
            const discountPercent = parseFloat(document.getElementById('discountPercent').value);
            
            if (startDate >= endDate) {
                e.preventDefault();
                alert('Ngày kết thúc phải sau ngày bắt đầu!');
                return;
            }
            
            if (discountPercent <= 0 || discountPercent > 100) {
                e.preventDefault();
                alert('Phần trăm giảm giá phải từ 0.01% đến 100%!');
                return;
            }
            
            // Branch validation is optional - empty means all branches
            const selectedBranches = $('.branch-checkbox:checked');
            if (selectedBranches.length === 0) {
                const confirmAll = confirm('Không chọn chi nhánh nào có nghĩa là áp dụng cho TẤT CẢ chi nhánh. Bạn có chắc chắn?');
                if (!confirmAll) {
                    e.preventDefault();
                    return;
                }
            }
        });
    </script>
</body>
</html>