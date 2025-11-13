<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>WM - Quản lý Hàng hóa</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/product.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <style>
   
        /* --- Dropdown người dùng --- */
        .user-dropdown { position: relative; display: inline-block; }
        .user-dropdown .dropdown-menu {
            position: absolute; top: 40px; right: 0; background: white; border: 1px solid #ddd;
            border-radius: 6px; box-shadow: 0 2px 6px rgba(0,0,0,0.15); min-width: 160px; display: none; z-index: 1000;
        }
        .user-dropdown.active .dropdown-menu { display: block; }
        .dropdown-item { display: block; padding: 10px 14px; color: #333; text-decoration: none; font-size: 14px; }
        .dropdown-item:hover { background: #f2f5ff; color: #0056d6; }
        .user-icon { display: flex; align-items: center; justify-content: center; color: #fff; text-decoration: none; }
        .user-icon i { color: #fff !important; }
        .gradient { background: linear-gradient(45deg, #007bff, #00aaff) !important; border-radius: 50%; width: 38px; height: 38px; display: flex; align-items: center; justify-content: center; }
        .header .user-icon i { color: #fff !important; }
        .header .gradient { background: linear-gradient(45deg, #007bff, #00aaff) !important; border-radius: 50% !important; }

    </style>
</head>

<body>

<header class="header">
    <div class="header-main">
        <div class="logo">
            <div class="logo-icon"><span class="icon-building"></span></div>
            <span>WM</span>
        </div>
        <nav class="nav-menu">
            <a href="${pageContext.request.contextPath}/TongQuan" class="nav-item "><span class="icon-overview"></span> Tổng quan</a>
            <a href="${pageContext.request.contextPath}/product" class="nav-item active"><span class="icon-products"></span> Hàng hóa</a>
            <a href="${pageContext.request.contextPath}/Transactions" class="nav-item"><span class="icon-transactions"></span> Giao dịch</a>
            <a href="Supplier" class="nav-item"><span class="icon-partners"></span> Đối tác</a>
            <a href="${pageContext.request.contextPath}/NhanVien" class="nav-item"><span class="icon-staff"></span> Nhân viên</a>
            <a href="#" class="nav-item"><span class="icon-cashbook"></span> Khuyến mãi</a>
            <a href="#" class="nav-item"><span class="icon-reports"></span> Báo cáo</a>
        </nav>

        <div class="header-right">
            <div class="user-dropdown">
                <a href="#" class="user-icon gradient" id="dropdownToggle">
                    <i class="fas fa-user-circle fa-2x"></i>
                </a>
                <div class="dropdown-menu" id="dropdownMenu">
                    <a href="InformationAccount" class="dropdown-item">Thông tin chi tiết</a>
                    <a href="Login" class="dropdown-item">Đăng xuất</a>
                </div>
            </div>
        </div>
    </div>
</header>

<div class="main-container">
    <aside class="sidebar">
        <!-- FORM LỌC BÊN TRÁI -->
        <form id="filtersForm" action="${pageContext.request.contextPath}/product" method="get">
            <input type="hidden" name="action" value="list"/>

            <!-- Loại sản phẩm -->
            <div class="filter-content" id="category-filter-content">
                <label for="categoryNameSelect"><strong>Loại sản phẩm:</strong></label><br/>
                <select id="categoryNameSelect" name="categoryName" multiple style="width:100%; padding:6px; height:120px;">
                    <c:forEach var="c" items="${categories}">
                        <c:set var="sel" value="false"/>
                        <c:if test="${not empty selectedCategoryNames}">
                            <c:forEach var="cn" items="${selectedCategoryNames}">
                                <c:if test="${cn eq c.categoryName}">
                                    <c:set var="sel" value="true"/>
                                </c:if>
                            </c:forEach>
   <!-- ========== PAGINATION ROW ========== -->
                            <tr>
                                <td colspan="${(sessionScope.roleID == 0 or sessionScope.roleID == '0') ? '6' : '5'}">
                                    <div class="pagination-wrapper" style="display:flex; justify-content:space-between; align-items:center; padding:12px 8px;">
                                        <!-- left: info -->
                                        <div class="pagination-info">
                                            <c:choose>
                                                <c:when test="${not empty totalItems}">
                                                    Tổng <strong>${totalItems}</strong> sản phẩm — Trang <strong>${currentPage}</strong> / <strong>${totalPages}</strong>
                                                </c:when>
                                                <c:otherwise>
                                                    Không có sản phẩm
                                                </c:otherwise>
                                            </c:choose>
                                        </div>

                                        <!-- right: controls -->
                                        <div class="pagination-controls" style="display:flex; align-items:center; gap:10px;">

                                            <!-- small pageSize selector (keeps filters via hidden inputs) -->
                                            <form id="pageSizeForm" method="get" action="${pageContext.request.contextPath}/product" style="display:inline-flex; align-items:center; gap:6px;">
                                                <input type="hidden" name="action" value="list" />
                                                <input type="hidden" name="page" value="1" />
                                                <c:if test="${not empty param.productName}"><input type="hidden" name="productName" value="${param.productName}" /></c:if>
                                                <c:if test="${not empty param.keyword}"><input type="hidden" name="keyword" value="${param.keyword}" /></c:if>
                                                <c:if test="${not empty param.status}"><input type="hidden" name="status" value="${param.status}" /></c:if>
                                                <c:if test="${not empty param.stock}"><input type="hidden" name="stock" value="${param.stock}" /></c:if>
                                                <c:if test="${not empty param.stockThreshold}"><input type="hidden" name="stockThreshold" value="${param.stockThreshold}" /></c:if>
                                                <c:forEach var="cat" items="${paramValues.categoryName}">
                                                    <input type="hidden" name="categoryName" value="${cat}" />
                                                </c:forEach>

                                                <label for="ps">Hiển thị</label>
                                                <select id="ps" name="pageSize" onchange="document.getElementById('pageSizeForm').submit();" style="padding:4px;">
                                                    <option value="10" ${pageSize == 10 ? 'selected' : ''}>10</option>
                                                    <option value="20" ${pageSize == 20 ? 'selected' : ''}>20</option>
                                                    <option value="50" ${pageSize == 50 ? 'selected' : ''}>50</option>
                                                    <option value="100" ${pageSize == 100 ? 'selected' : ''}>100</option>
                                                </select>
                                                <span>/ trang</span>
                                            </form>

                                            <!-- Prev -->
                                            <c:choose>
                                                <c:when test="${currentPage > 1}">
                                                    <c:url var="prevUrl" value="/product">
                                                        <c:param name="action" value="list"/>
                                                        <c:param name="page" value="${currentPage - 1}"/>
                                                        <c:param name="pageSize" value="${pageSize}"/>
                                                        <c:if test="${not empty param.productName}"><c:param name="productName" value="${param.productName}"/></c:if>
                                                        <c:if test="${not empty param.keyword}"><c:param name="keyword" value="${param.keyword}"/></c:if>
                                                        <c:if test="${not empty param.status}"><c:param name="status" value="${param.status}"/></c:if>
                                                        <c:if test="${not empty param.stock}"><c:param name="stock" value="${param.stock}"/></c:if>
                                                        <c:if test="${not empty param.stockThreshold}"><c:param name="stockThreshold" value="${param.stockThreshold}"/></c:if>
                                                        <c:forEach var="cat" items="${paramValues.categoryName}"><c:param name="categoryName" value="${cat}"/></c:forEach>
                                                    </c:url>
                                                    <a href="${prevUrl}" class="btn btn-sm" style="padding:6px 10px; text-decoration:none;">&laquo; Trước</a>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="btn btn-sm disabled" style="padding:6px 10px; opacity:0.5;">&laquo; Trước</span>
                                                </c:otherwise>
                                            </c:choose>

                                            <!-- page numbers (window around currentPage) -->
                                            <c:set var="window" value="3" />
                                            <c:set var="start" value="${currentPage - window < 1 ? 1 : currentPage - window}" />
                                            <c:set var="end" value="${currentPage + window > totalPages ? totalPages : currentPage + window}" />

                                            <c:forEach var="i" begin="${start}" end="${end}">
                                                <c:url var="pageUrl" value="/product">
                                                    <c:param name="action" value="list" />
                                                    <c:param name="page" value="${i}" />
                                                    <c:param name="pageSize" value="${pageSize}" />
                                                    <c:if test="${not empty param.productName}"><c:param name="productName" value="${param.productName}" /></c:if>
                                                    <c:if test="${not empty param.keyword}"><c:param name="keyword" value="${param.keyword}" /></c:if>
                                                    <c:if test="${not empty param.status}"><c:param name="status" value="${param.status}" /></c:if>
                                                    <c:if test="${not empty param.stock}"><c:param name="stock" value="${param.stock}" /></c:if>
                                                    <c:if test="${not empty param.stockThreshold}"><c:param name="stockThreshold" value="${param.stockThreshold}" /></c:if>
                                                    <c:forEach var="cat" items="${paramValues.categoryName}"><c:param name="categoryName" value="${cat}" /></c:forEach>
                                                </c:url>

                                                <c:choose>
                                                    <c:when test="${i == currentPage}">
                                                        <a href="${pageUrl}" class="btn btn-sm active" style="padding:6px 8px; font-weight:bold; background:#eee; text-decoration:none;">${i}</a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <a href="${pageUrl}" class="btn btn-sm" style="padding:6px 8px; text-decoration:none;">${i}</a>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:forEach>

                                            <!-- Next -->
                                            <c:choose>
                                                <c:when test="${currentPage < totalPages}">
                                                    <c:url var="nextUrl" value="/product">
                                                        <c:param name="action" value="list"/>
                                                        <c:param name="page" value="${currentPage + 1}"/>
                                                        <c:param name="pageSize" value="${pageSize}"/>
                                                        <c:if test="${not empty param.productName}"><c:param name="productName" value="${param.productName}"/></c:if>
                                                        <c:if test="${not empty param.keyword}"><c:param name="keyword" value="${param.keyword}"/></c:if>
                                                        <c:if test="${not empty param.status}"><c:param name="status" value="${param.status}"/></c:if>
                                                        <c:if test="${not empty param.stock}"><c:param name="stock" value="${param.stock}"/></c:if>
                                                        <c:if test="${not empty param.stockThreshold}"><c:param name="stockThreshold" value="${param.stockThreshold}"/></c:if>
                                                        <c:forEach var="cat" items="${paramValues.categoryName}"><c:param name="categoryName" value="${cat}"/></c:forEach>
                                                    </c:url>
                                                    <a href="${nextUrl}" class="btn btn-sm" style="padding:6px 10px; text-decoration:none;">Sau &raquo;</a>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="btn btn-sm disabled" style="padding:6px 10px; opacity:0.5;">Sau &raquo;</span>
                                                </c:otherwise>
                                            </c:choose>

                                        </div>
                                    </div>
                                </td>
                            </tr>
                            <!-- ========== END PAGINATION ========== -->
                        </tbody>
                    </table>
                </div>
                <div class="filter-content" id="stock-filter-content" style="display:block">
                    <div class="filter-item">
                        <input type="radio" id="all-stock" name="stock" value="all" <c:if test="${empty stock or stock == 'all'}">checked</c:if> />
                        <label for="all-stock">Tất cả</label>
                    </div>
                    <div class="filter-item">
                        <input type="radio" id="below-min" name="stock" value="belowMin" <c:if test="${stock == 'belowMin'}">checked</c:if> />
                        <label for="below-min">Dưới định mức tồn</label>
                    </div>
                    <div class="filter-item">
                        <input type="radio" id="above-max" name="stock" value="aboveMax" <c:if test="${stock == 'aboveMax'}">checked</c:if> />
                        <label for="above-max">Vượt định mức tồn</label>
                    </div>
                    <div class="filter-item">
                        <input type="radio" id="in-stock" name="stock" value="in" <c:if test="${stock == 'in'}">checked</c:if> />
                        <label for="in-stock">Còn hàng trong kho</label>
                    </div>
                    <div class="filter-item">
                        <input type="radio" id="out-of-stock" name="stock" value="out" <c:if test="${stock == 'out'}">checked</c:if> />
                        <label for="out-of-stock">Hết hàng trong kho</label>
                    </div>

                    <div class="filter-item" style="margin-top:8px">
                        <label for="stockThreshold">Ngưỡng tồn (cho Dưới/Vượt):</label>
                        <input type="number" min="0" id="stockThreshold" name="stockThreshold"
                               value="<c:out value='${empty stockThreshold ? 30 : stockThreshold}'/>" />
                    </div>
                </div>
            </div>

            <!-- Chọn pageSize trong form filter để người dùng có thể thay đổi -->
            <div class="filter-item" style="margin-top:10px">
                <label for="pageSizeSelect">Hiển thị mỗi trang</label>
                <select id="pageSizeSelect" name="pageSize">
                    <option value="15" <c:if test="${pageSize == 15}">selected</c:if>>10</option>
                    <option value="30" <c:if test="${pageSize == 30}">selected</c:if>>20</option>
                    <option value="50" <c:if test="${pageSize == 50}">selected</c:if>>50</option>
                </select>
            </div>

            <div class="mt-2" style="margin-top:10px;">
                <button type="submit" class="btn btn-primary btn-sm"><span class="icon-filter"></span> Lọc sản phẩm</button>
                <button type="reset" class="btn btn-secondary btn-sm"><span class="icon-refresh"></span> Bỏ chọn</button>
            </div>
        </form>
    </aside>

    <main class="content">
        <div class="page-header">
            <h1 class="page-title">Hàng hóa</h1>
            <div class="header-actions">
                <!-- FORM TÌM KIẾM: giữ lại filter hiện có -->
                <form action="${pageContext.request.contextPath}/product" method="get" class="search-container" style="display:flex;gap:8px;align-items:center">
                    <input type="hidden" name="action" value="list"/>

                    <!-- giữ categoryName -->
                    <c:forEach var="cn" items="${selectedCategoryNames}">
                        <input type="hidden" name="categoryName" value="${cn}"/>
                    </c:forEach>

                    <!-- giữ các filter khác -->
                    <input type="hidden" name="stock" value="${stock}"/>
                    <input type="hidden" name="stockThreshold" value="${stockThreshold}"/>
                    <input type="hidden" name="pageSize" value="${pageSize}"/>

                    <input type="text" name="keyword" class="search-input" placeholder="Theo tên hàng" value="<c:out value='${keyword}'/>" />
                    <button type="submit" class="btn btn-outline">Tìm</button>
                </form>

                <div class="action-buttons">
                    <a href="${pageContext.request.contextPath}/product?action=add" class="btn btn-primary"><span class="icon-plus"></span> Thêm mới</a>
                    <button class="btn btn-secondary"><span class="icon-upload"></span> Import</button>
                    <button class="btn btn-outline"><span class="icon-download"></span> Xuất file</button>
                    <button class="btn btn-outline"><span class="icon-menu"></span></button>
                </div>
            </div>
        </div>

        <div class="products-table-container">
            <div class="table-header">
                <div class="table-title">Danh sách sản phẩm</div>
                <!-- Tuỳ chọn pageSize ở tiêu đề bảng (đồng bộ với form bên trái) -->
                <div class="table-actions">
                    <form action="${pageContext.request.contextPath}/product" method="get">
                        <input type="hidden" name="action" value="list"/>
                        <!-- giữ filter -->
                        <c:forEach var="cn" items="${selectedCategoryNames}">
                            <input type="hidden" name="categoryName" value="${cn}"/>
                        </c:forEach>
                        <input type="hidden" name="stock" value="${stock}"/>
                        <input type="hidden" name="stockThreshold" value="${stockThreshold}"/>
                        <input type="hidden" name="keyword" value="${keyword}"/>

                        <select name="pageSize" onchange="this.form.submit()"
                                style="padding: 6px 10px; border: 1px solid #d1d5db; border-radius: 4px;">
                            <option value="15" <c:if test="${pageSize == 15}">selected</c:if>>Hiển thị 15 sản phẩm</option>
                            <option value="30" <c:if test="${pageSize == 30}">selected</c:if>>Hiển thị 30 sản phẩm</option>
                            <option value="50" <c:if test="${pageSize == 50}">selected</c:if>>Hiển thị 50 sản phẩm</option>
                        </select>
                    </form>
                </div>
            </div>

            <table class="products-table">
                <thead>
                <tr>
                    <th><input type="checkbox" id="select-all"></th>
                    <th>ID</th>
                    <th>Tên sản phẩm</th>
                    <th>Tồn kho</th>
                    <th>Giá bán</th>
                    <th>VAT</th>
                    <th>Trạng thái</th>
                    <th>Ngày tạo</th>
                    <th>Thao tác</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="p" items="${products}">
                    <tr>
                        <td><input type="checkbox"></td>
                        <td>${p.productId}</td>
                        <td>${p.productName}</td>
                        <td>${p.totalQty}</td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty p.retailPrice}">
                                    <fmt:formatNumber value="${p.retailPrice}" type="number" minFractionDigits="0"/> ₫
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty p.vat}">
                                    <fmt:formatNumber value="${p.vat}" type="number" minFractionDigits="0"/>%
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${p.isActive}">Đang bán</c:when>
                                <c:otherwise>Ngừng bán</c:otherwise>
                            </c:choose>
                        </td>
                        <td><c:out value="${p.createdAt}"/></td>
                        <td>
                            <a href="${pageContext.request.contextPath}/product?action=detail&id=${p.productId}" class="btn btn-sm btn-info">Chi tiết</a>
                            <a href="${pageContext.request.contextPath}/product?action=delete&id=${p.productId}" class="btn btn-sm btn-danger"
                               onclick="return confirm('Bạn có chắc chắn muốn xóa sản phẩm này không?');">Xóa</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>

            <!-- ===== Pagination ===== -->
<c:url var="baseUrl" value="/product">
    <c:param name="action" value="list"/>
    <c:if test="${not empty keyword}">
        <c:param name="keyword" value="${keyword}"/>
    </c:if>
    <c:if test="${not empty stock}">
        <c:param name="stock" value="${stock}"/>
    </c:if>
    <c:if test="${not empty stockThreshold}">
        <c:param name="stockThreshold" value="${stockThreshold}"/>
    </c:if>
    <c:forEach var="cn" items="${selectedCategoryNames}">
        <c:param name="categoryName" value="${cn}"/>
    </c:forEach>
    <c:param name="pageSize" value="${pageSize}"/>
</c:url>


            <c:set var="prevPage" value="${currentPage > 1 ? currentPage - 1 : 1}"/>
            <c:set var="nextPage" value="${currentPage < totalPages ? currentPage + 1 : totalPages}"/>

            <div class="pagination-container">
                <div class="pagination-info">
                    Hiển thị ${startProduct} - ${endProduct} / Tổng số ${totalProducts} hàng hóa
                </div>
                <div class="pagination">
                    <a href="${baseUrl}&page=1" class="page-btn ${currentPage == 1 ? 'disabled' : ''}">
                        <i class="fas fa-angle-double-left"></i>
                    </a>
                    <a href="${baseUrl}&page=${prevPage}" class="page-btn ${currentPage == 1 ? 'disabled' : ''}">
                        <i class="fas fa-angle-left"></i>
                    </a>

                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <a href="${baseUrl}&page=${i}" class="page-btn ${i == currentPage ? 'active' : ''}">${i}</a>
                    </c:forEach>

                    <a href="${baseUrl}&page=${nextPage}" class="page-btn ${currentPage == totalPages ? 'disabled' : ''}">
                        <i class="fas fa-angle-right"></i>
                    </a>
                    <a href="${baseUrl}&page=${totalPages}" class="page-btn ${currentPage == totalPages ? 'disabled' : ''}">
                        <i class="fas fa-angle-double-right"></i>
                    </a>
                </div>
            </div>
            <!-- ===== End Pagination ===== -->

        </div>
    </main>
</div>

<script>
function toggleFilter(id) {
    const content = document.getElementById(id + '-content');
    const icon = document.getElementById(id + '-icon');
    if (!content) return;
    content.style.display = (content.style.display === 'none' || content.style.display === '') ? 'block' : 'none';
    if (icon) icon.classList.toggle('expanded');
}
document.getElementById('select-all')?.addEventListener('change', function () {
    document.querySelectorAll('tbody input[type="checkbox"]').forEach(cb => cb.checked = this.checked);
});
</script>
<script>
/* Bật/tắt khối filter */
function toggleFilter(id) {
    const content = document.getElementById(id + '-content');
    const icon = document.getElementById(id + '-icon');
    if (!content) return;
    content.style.display = (content.style.display === 'none' || content.style.display === '') ? 'block' : 'none';
    if (icon) icon.classList.toggle('expanded');
}

/* Chọn tất cả */
const selectAll = document.getElementById('select-all');
if (selectAll) {
    selectAll.addEventListener('change', function () {
        document.querySelectorAll('tbody input[type="checkbox"]').forEach(cb => cb.checked = this.checked);
    });
}

/* Dropdown user */
document.addEventListener('DOMContentLoaded', function () {
    const dropdown = document.querySelector('.user-dropdown');
    const toggle = document.getElementById('dropdownToggle');
    const menu = document.getElementById('dropdownMenu');

    toggle.addEventListener('click', function (e) {
        e.preventDefault();
        dropdown.classList.toggle('active');
    });

    document.addEventListener('click', function (e) {
        if (!dropdown.contains(e.target)) {
            dropdown.classList.remove('active');
        }
    });
});
</script>
</body>
</html>