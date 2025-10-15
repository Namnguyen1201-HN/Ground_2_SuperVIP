<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="${pageContext.request.contextPath}/css/admin/test.css" rel="stylesheet" type="text/css"/>
    <title>Chi Tiết / Chỉnh Sửa Sản Phẩm</title>
    <style>
        .product-detail-card { max-width: 980px; margin: 0 auto; }
        .product-content { display: grid; grid-template-columns: 360px 1fr; gap: 24px; }
        .product-image { width: 100%; height: auto; border-radius: 8px; object-fit: cover; }
        .info-group { margin-bottom: 14px; }
        .info-label { display:block; font-weight:600; margin-bottom:6px; }
        .row-2 { display:grid; grid-template-columns: 1fr 1fr; gap: 16px; }
        .action-buttons { display:flex; gap:10px; margin-top: 18px; }
        input[type="text"], input[type="number"], input[type="url"], select {
            width: 100%; padding: 8px 10px; border: 1px solid #d1d5db; border-radius: 6px;
        }
        .btn { padding: 8px 14px; border-radius: 6px; border: 1px solid transparent; cursor: pointer; }
        .btn-primary { background: #2563eb; color: #fff; }
        .btn-secondary { background: #fff; border-color:#d1d5db; }
        .page-title { margin-bottom: 16px; }
        .preview-wrap { position: relative; }
        .hint { color:#6b7280; font-size:12px; margin-top:6px; }
    </style>
</head>
<body>
<div class="container">
    <div class="product-detail-card">
        <h1 class="page-title">Chi Tiết / Chỉnh Sửa Sản Phẩm</h1>

        <div class="product-content">
            <!-- Cột ảnh -->
            <div class="product-image-section">
                <div class="preview-wrap">
                    <img id="previewImg"
                         src="${product.imageUrl != null ? product.imageUrl : 'https://via.placeholder.com/400x400?text=No+Image'}"
                         alt="${product.productName}" class="product-image">
                </div>
                <div class="info-group">
                    <label class="info-label">Ảnh (URL)</label>
                    <input type="url" id="imageUrlInput" form="productEditForm"
                           name="imageUrl"
                           value="${product.imageUrl}" placeholder="https://..."/>
                    <div class="hint">Dán link ảnh để xem trước ngay.</div>
                </div>
            </div>

            <!-- Cột thông tin & FORM -->
            <div class="product-info-section">
                <form id="productEditForm" action="${pageContext.request.contextPath}/product" method="post">
                    <!-- Hành động -->
                    <input type="hidden" name="action" value="update"/>
                    <input type="hidden" name="id" value="${product.productId}"/>

                    <div class="info-group">
                        <label class="info-label">Mã Sản Phẩm</label>
                        <input type="text" value="${product.productId}" disabled/>
                    </div>

                    <div class="info-group">
                        <label class="info-label">Tên Sản Phẩm</label>
                        <input type="text" name="name" value="${product.productName}" required/>
                    </div>

                    <div class="row-2">
                        <div class="info-group">
                            <label class="info-label">Thương Hiệu</label>
                            <c:choose>
                                <c:when test="${not empty brands}">
                                    <select name="brandName">
                                        <option value="">-- Chọn thương hiệu --</option>
                                        <c:forEach var="b" items="${brands}">
                                            <option value="${b.name}"
                                                    ${product.brandName == b.name ? 'selected' : ''}>${b.name}</option>
                                        </c:forEach>
                                    </select>
                                </c:when>
                                <c:otherwise>
                                    <input type="text" name="brandName" value="${product.brandName}" placeholder="Nhập tên thương hiệu"/>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <div class="info-group">
                            <label class="info-label">Danh Mục</label>
                            <c:choose>
                                <c:when test="${not empty categories}">
                                    <select name="categoryName">
                                        <option value="">-- Chọn danh mục --</option>
                                        <c:forEach var="c" items="${categories}">
                                            <option value="${c.categoryName}"
                                                    ${product.categoryName == c.categoryName ? 'selected' : ''}>${c.categoryName}</option>
                                        </c:forEach>
                                    </select>
                                </c:when>
                                <c:otherwise>
                                    <input type="text" name="categoryName" value="${product.categoryName}" placeholder="Nhập tên danh mục"/>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <div class="info-group">
                        <label class="info-label">Nhà Cung Cấp</label>
                        <c:choose>
                            <c:when test="${not empty suppliers}">
                                <select name="supplierName">
                                    <option value="">-- Chọn NCC --</option>
                                    <c:forEach var="s" items="${suppliers}">
                                        <option value="${s.name}"
                                                ${product.supplierName == s.name ? 'selected' : ''}>${s.name}</option>
                                    </c:forEach>
                                </select>
                            </c:when>
                            <c:otherwise>
                                <input type="text" name="supplierName" value="${product.supplierName}" placeholder="Nhập tên NCC"/>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="row-2">
                        <div class="info-group">
                            <label class="info-label">Giá Vốn</label>
                            <input type="number" step="0.01" name="costPrice" value="${product.costPrice}" />
                        </div>

                        <div class="info-group">
                            <label class="info-label">Giá Bán Lẻ</label>
                            <input type="number" step="0.01" name="retailPrice" value="${product.retailPrice}" />
                        </div>
                    </div>

                    <div class="row-2">
                        <div class="info-group">
                            <label class="info-label">VAT (%)</label>
                            <input type="number" step="0.01" name="vat" value="${product.vat}" />
                        </div>

                        <div class="info-group" style="display:flex;align-items:center;gap:10px; margin-top:28px;">
                            <input type="checkbox" id="isActive" name="isActive"
                                   ${product.isActive ? 'checked' : ''}/>
                            <label for="isActive" class="info-label" style="margin:0;">Đang kinh doanh</label>
                        </div>
                    </div>

                    <div class="action-buttons">
                        <button type="submit" class="btn btn-primary">💾 Lưu</button>
                        <a href="${pageContext.request.contextPath}/product?action=list" class="btn btn-secondary">❌ Hủy</a>
                        <a href="${pageContext.request.contextPath}/product?action=detail&id=${product.productId}" class="btn btn-secondary">↩️ Xem lại chi tiết</a>
                    </div>
                </form>
            </div>
        </div>

    </div>
</div>

<script>
    // Xem trước ảnh ngay khi đổi URL
    const imageUrlInput = document.getElementById('imageUrlInput');
    const previewImg = document.getElementById('previewImg');
    if (imageUrlInput && previewImg) {
        imageUrlInput.addEventListener('input', function() {
            const v = this.value && this.value.trim().length ? this.value.trim() : 'https://via.placeholder.com/400x400?text=No+Image';
            previewImg.src = v;
        });
    }
</script>
</body>
</html>
