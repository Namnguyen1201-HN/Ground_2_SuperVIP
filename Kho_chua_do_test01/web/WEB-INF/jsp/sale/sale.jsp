<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý Hàng hóa - Sale</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sale/sale.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <style>
        /* bạn có thể giữ lại style cũ của bạn ở đây, tớ rút gọn để tập trung vào JSTL */
        .header { background: linear-gradient(135deg,#2563eb 0%,#1d4ed8 100%); color:#fff; box-shadow: 0 2px 10px rgba(0,0,0,.1); }
        .header-main { padding: 1rem 2rem; display:flex; align-items:center; gap:2rem; }
        .logo { display:flex; align-items:center; gap:.5rem; font-size:1.5rem; font-weight:700; }
        .logo-icon { background:#14b8a6; width:40px; height:40px; border-radius:.5rem; display:flex; align-items:center; justify-content:center; }
        .nav-menu { display:flex; gap:0; flex:1; }
        .nav-item { display:flex; align-items:center; gap:.5rem; padding:.75rem 1.5rem; color:rgba(255,255,255,.9); text-decoration:none; border-radius:.5rem; transition:.2s; font-weight:500; }
        .nav-item:hover, .nav-item.active { background:rgba(255,255,255,.1); color:#fff; }
        .user-dropdown { position:relative; display:inline-block; }
        .user-dropdown .dropdown-menu { position:absolute; top:40px; right:0; background:#fff; border:1px solid #ddd; border-radius:6px; box-shadow:0 2px 6px rgba(0,0,0,.15); min-width:160px; display:none; z-index:1000; }
        .user-dropdown.active .dropdown-menu { display:block; }
        .dropdown-item { display:block; padding:10px 14px; color:#333; text-decoration:none; font-size:14px; }
        .dropdown-item:hover { background:#f2f5ff; color:#0056d6; }
        .gradient { background:linear-gradient(45deg,#007bff,#00aaff)!important; border-radius:50%; width:38px; height:38px; display:flex; align-items:center; justify-content:center; }
        .container { display:flex; gap:16px; padding:16px; }
        .sidebar { width:280px; background:#fff; border:1px solid #e5e7eb; border-radius:8px; padding:12px; height:fit-content; }
        .sidebar-section { margin-bottom:14px; }
        .sidebar-title { font-size:14px; font-weight:700; margin-bottom:10px; }
        .main-content { flex:1; }
        .content-header { display:flex; align-items:center; justify-content:space-between; margin-bottom:12px; gap:12px; }
        .search-bar { display:flex; align-items:center; gap:8px; }
        .search-input { padding:8px 10px; border:1px solid #d1d5db; border-radius:6px; min-width:260px; }
        .btn { padding:8px 10px; border-radius:6px; border:1px solid #d1d5db; background:#fff; cursor:pointer; }
        .btn-primary { background:#2563eb; color:#fff; border-color:#2563eb; }
        .table-wrapper { background:#fff; border:1px solid #e5e7eb; border-radius:8px; overflow:hidden; }
        .data-table { width:100%; border-collapse:collapse; }
        .data-table th, .data-table td { padding:10px 12px; border-bottom:1px solid #f1f5f9; text-align:left; }
        .data-table thead th { background:#f8fafc; font-size:13px; text-transform:uppercase; letter-spacing:.02em; color:#475569; }
        .qty-pill { display:inline-block; padding:2px 8px; border-radius:999px; border:1px solid #e5e7eb; font-weight:600; font-size:12px; }
        .pill-in { background:#ecfdf5; border-color:#10b981; color:#065f46; }
        .pill-out { background:#fef2f2; border-color:#ef4444; color:#991b1b; }
        .filter-item { margin-bottom:8px; }
    </style>
</head>
<body>

<header class="header">
    <div class="header-main">
        <div class="logo">
            <div class="logo-icon">🏢</div>
            <span>WM</span>
        </div>
        <nav class="nav-menu">
            <a href="${pageContext.request.contextPath}/TongQuan" class="nav-item"><span>📊</span> Tổng quan</a>
            <a href="${pageContext.request.contextPath}/sale" class="nav-item active"><span>🛒</span> Bán hàng</a>
            <a href="${pageContext.request.contextPath}/product" class="nav-item"><span>📦</span> Hàng hóa (Quản trị)</a>
            <a href="${pageContext.request.contextPath}/Transactions" class="nav-item"><span>💳</span> Giao dịch</a>
            <a href="${pageContext.request.contextPath}/Supplier" class="nav-item"><span>🤝</span> Đối tác</a>
            <a href="${pageContext.request.contextPath}/NhanVien" class="nav-item"><span>👥</span> Nhân viên</a>
        </nav>
        <div class="header-right">
            <div class="user-dropdown">
                <a href="#" class="user-icon gradient" id="dropdownToggle"><i class="fas fa-user-circle fa-2x"></i></a>
                <div class="dropdown-menu" id="dropdownMenu">
                    <a href="${pageContext.request.contextPath}/InformationAccount" class="dropdown-item">Thông tin chi tiết</a>
                    <a href="${pageContext.request.contextPath}/Login" class="dropdown-item">Đăng xuất</a>
                </div>
            </div>
        </div>
    </div>
</header>

<script>
document.addEventListener('DOMContentLoaded', function () {
    const dropdown = document.querySelector('.user-dropdown');
    const toggle = document.getElementById('dropdownToggle');
    toggle.addEventListener('click', function (e) {
        e.preventDefault(); dropdown.classList.toggle('active');
    });
    document.addEventListener('click', function (e) {
        if (!dropdown.contains(e.target)) dropdown.classList.remove('active');
    });
});
</script>

<div class="container">
    <!-- ==== SIDEBAR: BỘ LỌC ==== -->
    <aside class="sidebar">
        <!-- Form lọc (gửi GET tới /sale) -->
        <form action="${pageContext.request.contextPath}/sale" method="get">
            <input type="hidden" name="action" value="list"/>

            <!-- Nhóm hàng (Danh mục) -->
            <div class="sidebar-section">
                <h3 class="sidebar-title">Nhóm hàng</h3>
                <div style="max-height:220px; overflow:auto; border:1px solid #e5e7eb; border-radius:6px; padding:8px;">
                    <c:forEach var="c" items="${categories}">
                        <c:set var="checked" value="${selectedCategoryNames != null && selectedCategoryNames.contains(c.categoryName)}"/>
                        <div class="filter-item">
                            <label style="display:flex; gap:8px; align-items:center;">
                                <input type="checkbox" name="categoryName" value="${c.categoryName}"
                                       <c:if test="${checked}">checked</c:if> />
                                <span>${c.categoryName}</span>
                            </label>
                        </div>
                    </c:forEach>
                </div>
            </div>

            <!-- Tồn kho -->
            <div class="sidebar-section">
                <h3 class="sidebar-title">Tồn kho</h3>
                <div class="filter-item">
                    <label><input type="radio" name="stock" value="all"      <c:if test="${empty stock || stock=='all'}">checked</c:if> /> Tất cả</label>
                </div>
                <div class="filter-item">
                    <label><input type="radio" name="stock" value="in"       <c:if test="${stock=='in'}">checked</c:if> /> Còn hàng</label>
                </div>
                <div class="filter-item">
                    <label><input type="radio" name="stock" value="out"      <c:if test="${stock=='out'}">checked</c:if> /> Hết hàng</label>
                </div>
                <div class="filter-item">
                    <label><input type="radio" name="stock" value="belowMin" <c:if test="${stock=='belowMin'}">checked</c:if> /> Dưới định mức</label>
                </div>
                <div class="filter-item">
                    <label><input type="radio" name="stock" value="aboveMax" <c:if test="${stock=='aboveMax'}">checked</c:if> /> Vượt định mức</label>
                </div>

                <div class="filter-item" style="margin-top:8px;">
                    <label>Ngưỡng tồn:</label>
                    <input type="number" min="0" name="stockThreshold"
                           style="width:100%; padding:6px; border:1px solid #d1d5db; border-radius:6px;"
                           value="<c:out value='${empty stockThreshold ? 30 : stockThreshold}'/>">
                </div>
            </div>

            <div class="sidebar-section">
                <button type="submit" class="btn btn-primary" style="width:100%;">Lọc</button>
            </div>
        </form>
    </aside>

    <!-- ==== MAIN: DANH SÁCH ==== -->
    <main class="main-content">
        <div class="content-header">
            <h1 class="page-title">Hàng hóa</h1>

            <!-- Tìm kiếm giữ lại filter hiện tại -->
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
                <!-- Với SAHomePage chỉ xem, bạn có thể ẩn các nút CRUD -->
                <a class="btn" href="#" onclick="alert('Chức năng dành cho quản trị!');return false;">☰</a>
            </div>
        </div>

        <div class="table-wrapper">
            <table class="data-table">
                <thead>
                <tr>
                    <th><input type="checkbox"></th>
                    <th>ID</th>
                    <th>Tên hàng</th>
                    <th>Danh mục</th>
                    <th>Thương hiệu</th>
                    <th>Nhà cung cấp</th>
                    <th>Giá bán</th>
                    <th>Giá vốn</th>
                    <th>Tồn kho</th>
                    <th>Ngày tạo</th>
                    <th>Chi tiết</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${empty products}">
                        <tr>
                            <td colspan="11" style="text-align:center; color:#64748b; padding:18px;">
                                Không có sản phẩm nào khớp bộ lọc.
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="p" items="${products}">
                            <tr>
                                <td><input type="checkbox"></td>
                                <td>${p.productId}</td>
                                <td>${p.productName}</td>
                                <td>${p.categoryName}</td>
                                <td>${p.brandName}</td>
                                <td>${p.supplierName}</td>

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
                                        <c:when test="${not empty p.costPrice}">
                                            <fmt:formatNumber value="${p.costPrice}" type="number" minFractionDigits="0"/> ₫
                                        </c:when>
                                        <c:otherwise>-</c:otherwise>
                                    </c:choose>
                                </td>

                                <td>
                                    <c:set var="qty" value="${p.totalQty}" />
                                    <span class="qty-pill <c:out value='${qty != null && qty > 0 ? "pill-in" : "pill-out"}'/>">
                                        <c:out value='${qty == null ? 0 : qty}'/>
                                    </span>
                                </td>

                                <td><c:out value="${p.createdAt}"/></td>

                                <td>
                                    <a class="btn" href="${pageContext.request.contextPath}/sale?action=detail&id=${p.productId}">
                                        Xem
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>
    </main>
</div>

</body>
</html>
