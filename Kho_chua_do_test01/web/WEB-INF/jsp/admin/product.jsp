<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WM - Quản lý Hàng hóa</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/product.css">
    <style>
        body {
            padding-top: 70px;
        }
    </style>
</head>

<body>
<%@ include file="header_admin.jsp" %>

<div class="main-container">
    <aside class="sidebar">
        <!-- FORM LỌC BÊN TRÁI -->
        <form id="filtersForm" action="${pageContext.request.contextPath}/product" method="get">
            <input type="hidden" name="action" value="list"/>

            <!-- Lọc theo danh mục: DÙNG TÊN -->
            <div class="filter-content" id="category-filter-content" ">
                <label for="categoryNameSelect"><strong>Loại sản phẩm:</strong></label><br/>

                <select id="categoryNameSelect" name="categoryName" multiple
                        style="width:100%; padding:6px; height:120px;">

                    <c:forEach var="c" items="${categories}">
                        <!-- EL/JSTL an toàn cho mọi phiên bản -->
                        <c:set var="sel" value="false"/>
                        <c:if test="${not empty selectedCategoryNames}">
                            <c:forEach var="cn" items="${selectedCategoryNames}">
                                <c:if test="${cn eq c.categoryName}">
                                    <c:set var="sel" value="true"/>
                                </c:if>
                            </c:forEach>
                        </c:if>
                        <option value="${c.categoryName}" <c:if test="${sel}">selected</c:if>>
                            ${c.categoryName}
                        </option>
                    </c:forEach>

                </select>
            </div>

            <!-- Tồn kho -->
            <div class="filter-section">
                <div class="filter-title" onclick="toggleFilter('stock-filter')">
                    Tồn kho <span class="icon-expand" id="stock-filter-icon"></span>
                </div>
                <div class="filter-content" id="stock-filter-content" style="display:block">
                    <div class="filter-item">
                        <input type="radio" id="all-stock" name="stock" value="all"
                               <c:if test="${empty stock or stock == 'all'}">checked</c:if> />
                        <label for="all-stock">Tất cả</label>
                    </div>
                    <div class="filter-item">
                        <input type="radio" id="below-min" name="stock" value="belowMin"
                               <c:if test="${stock == 'belowMin'}">checked</c:if> />
                        <label for="below-min">Dưới định mức tồn</label>
                    </div>
                    <div class="filter-item">
                        <input type="radio" id="above-max" name="stock" value="aboveMax"
                               <c:if test="${stock == 'aboveMax'}">checked</c:if> />
                        <label for="above-max">Vượt định mức tồn</label>
                    </div>
                    <div class="filter-item">
                        <input type="radio" id="in-stock" name="stock" value="in"
                               <c:if test="${stock == 'in'}">checked</c:if> />
                        <label for="in-stock">Còn hàng trong kho</label>
                    </div>
                    <div class="filter-item">
                        <input type="radio" id="out-of-stock" name="stock" value="out"
                               <c:if test="${stock == 'out'}">checked</c:if> />
                        <label for="out-of-stock">Hết hàng trong kho</label>
                    </div>
                    <div class="filter-item" style="margin-top:8px">
                        <label for="stockThreshold">Ngưỡng tồn (cho Dưới/Vượt):</label>
                        <input type="number" min="0" id="stockThreshold" name="stockThreshold"
                               value="<c:out value='${empty stockThreshold ? 30 : stockThreshold}'/>" />
                    </div>
                </div>
            </div>

            <!-- Nút submit -->
            <div class="mt-2" style="margin-top:10px;">
                <button type="submit" class="btn btn-primary btn-sm">
                    <span class="icon-filter"></span> Lọc sản phẩm
                </button>
                <button type="reset" class="btn btn-secondary btn-sm">
                    <span class="icon-refresh"></span> Bỏ chọn
                </button>
            </div>
        </form>
    </aside>

    <main class="content">
        <div class="page-header">
            <h1 class="page-title">Hàng hóa</h1>
            <div class="header-actions">
                <!-- FORM TÌM KIẾM: giữ lại stock/threshold + các categoryName đang chọn -->
                <form action="${pageContext.request.contextPath}/product" method="get" class="search-container" style="display:flex;gap:8px;align-items:center">
                    <input type="hidden" name="action" value="list"/>

                    <!-- Render lại categoryName đang chọn để không mất filter khi tìm -->
                    <c:forEach var="cn" items="${selectedCategoryNames}">
                        <input type="hidden" name="categoryName" value="${cn}"/>
                    </c:forEach>

                    <input type="hidden" name="stock" value="${stock}"/>
                    <input type="hidden" name="stockThreshold" value="${stockThreshold}"/>

                    <input type="text" name="keyword" class="search-input" placeholder="Theo tên hàng"
                           value="<c:out value='${keyword}'/>" />
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
                <div class="table-actions">
                    <select style="padding: 6px 10px; border: 1px solid #d1d5db; border-radius: 4px;">
                        <option>Hiển thị 15 sản phẩm</option>
                        <option>Hiển thị 30 sản phẩm</option>
                        <option>Hiển thị 50 sản phẩm</option>
                    </select>
                </div>
            </div>

            <table class="products-table">
                <thead>
                <tr>
                    <th><input type="checkbox" id="select-all"></th>
                    <th>ID</th>
                    <th>Tên sản phẩm</th>
<!--                    <th>Danh mục</th>
                    <th>Thương hiệu</th>
                    <th>Nhà cung cấp</th>-->
                    <th>Tồn kho</th>
<!--                    <th>Giá vốn</th>-->
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
<!--                        <td>${p.categoryName}</td>
                        <td>${p.brandName}</td>
                        <td>${p.supplierName}</td>-->
                        <td>${p.totalQty}</td>

<!--                        <td>
                            <c:choose>
                                <c:when test="${not empty p.costPrice}">
                                    <fmt:formatNumber value="${p.costPrice}" type="number" minFractionDigits="0"/> ₫
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </td>-->

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

                        <td>
                            <c:out value="${p.createdAt}"/>
                        </td>

                        <td>
                            <a href="${pageContext.request.contextPath}/product?action=detail&id=${p.productId}" class="btn btn-sm btn-info">Chi tiết</a>
                            <a href="${pageContext.request.contextPath}/product?action=delete&id=${p.productId}" class="btn btn-sm btn-danger"
                               onclick="return confirm('Bạn có chắc chắn muốn xóa sản phẩm này không?');">Xóa</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>

            <div class="pagination">
                <div class="pagination-info">Hiển thị 1 - 15 / Tổng số 30 hàng hóa</div>
                <div class="pagination-controls">
                    <button class="pagination-btn" disabled>⏮️</button>
                    <button class="pagination-btn" disabled>◀️</button>
                    <button class="pagination-btn active">1</button>
                    <button class="pagination-btn">2</button>
                    <button class="pagination-btn">▶️</button>
                    <button class="pagination-btn">⏭️</button>
                </div>
            </div>
        </div>
    </main>
</div>

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




</body>
</html>
