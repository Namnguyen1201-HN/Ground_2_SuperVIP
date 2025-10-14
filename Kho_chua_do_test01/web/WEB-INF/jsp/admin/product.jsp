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
                    <a href="#" class="nav-item"><span class="icon-partners"></span> Đối tác</a>
                    <a href="${pageContext.request.contextPath}/NhanVien" class="nav-item"><span class="icon-staff"></span> Nhân viên</a>
                    <a href="#" class="nav-item"><span class="icon-cashbook"></span> Sổ quỹ</a>
                    <a href="#" class="nav-item"><span class="icon-reports"></span> Báo cáo</a>
                    <a href="#" class="nav-item"><span class="icon-online"></span> Bán Online</a>
                    <a href="#" class="nav-item"><span class="icon-sales"></span> Bán hàng</a>
                </nav>
            </div>
        </header>

        <div class="main-container">
            <aside class="sidebar">
                <form id="filtersForm" action="${pageContext.request.contextPath}/product" method="get">
                    <input type="hidden" name="action" value="list" />

                    <!-- Loại hàng (placeholder UI) -->
                    <div class="filter-section">
                        <div class="filter-title" onclick="toggleFilter('product-types')">
                            Loại hàng <span class="icon-expand" id="product-types-icon"></span>
                        </div>
                        <div class="filter-content" id="product-types-content">
                            <div class="filter-item">
                                <input type="checkbox" id="normal-products" checked>
                                <label for="normal-products">Hàng hóa thường</label>
                            </div>
                            <div class="filter-item">
                                <input type="checkbox" id="serial-products">
                                <label for="serial-products">Hàng hóa - Serial/IMEI</label>
                            </div>
                            <div class="filter-item">
                                <input type="checkbox" id="services">
                                <label for="services">Dịch vụ</label>
                            </div>
                            <div class="filter-item">
                                <input type="checkbox" id="combo-products">
                                <label for="combo-products">Combo - Đóng gói</label>
                            </div>
                        </div>
                    </div>

                    <!-- Category -->


<!-- KHÔNG để hidden stock/stockThreshold ở form filter -->
<div class="filter-content" id="category-filter-content" style="display:block">
  <label><strong>Loại sản phẩm:</strong></label><br/>
  <c:forEach var="c" items="${categories}">
    <c:set var="isChecked" value="false"/>
    <c:forEach var="selId" items="${selectedCategoryIds}">
      <c:if test="${selId == c.categoryID}">
        <c:set var="isChecked" value="true"/>
      </c:if>
    </c:forEach>
    <div class="form-check">
      <input class="form-check-input" type="checkbox" name="categoryId"
             value="${c.categoryID}" id="cat-${c.categoryID}"
             <c:if test="${isChecked}">checked</c:if> />
      <label class="form-check-label" for="cat-${c.categoryID}">${c.categoryName}</label>
    </div>
  </c:forEach>
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


                    <div class="mt-2">
                        <button type="submit" class="btn btn-primary btn-sm">Lọc sản phẩm</button>
                        <button type="reset" class="btn btn-secondary btn-sm">Bỏ chọn</button>
                    </div>
                </form>
            </aside>


            <main class="content">
                <div class="page-header">
                    <h1 class="page-title">Hàng hóa</h1>
                    <div class="header-actions">
                        <div class="search-container">
                            <form action="${pageContext.request.contextPath}/product" method="get" class="search-container" style="display:flex;gap:8px;align-items:center">
                                <input type="hidden" name="action" value="list"/>
                                <input type="hidden" name="categoryId" value="${selectedCategoryId}"/>
                                <input type="hidden" name="stock" value="${param.stock}"/>
                                <input type="hidden" name="stockThreshold" value="${stockThreshold}"/>

                                <input type="text" name="keyword" class="search-input" placeholder="Theo tên hàng"
                                       value="<c:out value='${param.keyword}'/>" />
                                <button type="submit" class="btn btn-outline">Tìm</button>
                            </form>
                            <span class="search-icon icon-search"></span>
                        </div>
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
<!--                                <th>Loại</th>
                                <th>Chi nhánh</th>
                                <th>Nhà cung cấp</th>-->
                                <th>Giá vốn</th>
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
<!--                                    <td>${p.categoryId}</td>
                                    <td>${p.brandId}</td>
                                    <td>${p.supplierId}</td>-->

                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty p.costPrice}">
                                                <fmt:formatNumber value="${p.costPrice}" type="number" minFractionDigits="0"/> ₫
                                            </c:when>
                                            <c:otherwise>-</c:otherwise>
                                        </c:choose>
                                    </td>

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
                                        <!-- p.createdAt là LocalDateTime -> không dùng fmt:formatDate -->
                                        <c:out value="${p.createdAt}"/>
                                    </td>

                                    <td>
                                        <a href="${pageContext.request.contextPath}/product?action=edit&id=${p.productId}" class="btn btn-sm btn-warning">Chi tiết</a>
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
    </body>
</html>
