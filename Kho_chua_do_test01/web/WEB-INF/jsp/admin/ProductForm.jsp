<%-- 
    Document   : ProductForm
    Created on : Sep 28, 2025, 2:16:51 AM
    Author     : Kawaii
--%>
<%-- 
    Document   : ProductForm
    Created on : Sep 28, 2025, 2:16:51 AM
    Author     : Kawaii
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <link href="css/admin/ProductForm.css" rel="stylesheet" type="text/css"/>
    <meta charset="UTF-8">
    <title>${product != null ? "Sửa sản phẩm" : "Thêm sản phẩm"}</title>
</head>
<body>
<h2>${product != null ? "Sửa sản phẩm" : "Thêm sản phẩm"}</h2>

<form action="product" method="post">
    <!-- hành động -->
    <input type="hidden" name="action" value="${product != null ? "update" : "insert"}"/>
    <c:if test="${product != null}">
        <input type="hidden" name="id" value="${product.productId}"/>
    </c:if>


    <!-- ProductName -->
<label>Tên sản phẩm:</label>
        <input type="text" name="name" value="${product != null ? product.productName : ''}" required/>

    <!-- BrandID -->
    <label>Chi nhánh:</label>
    <c:choose>
        <c:when test="${not empty brands}">
            <select name="brandId" required>
                <option value="">-- Chọn Chi nhánh --</option>
                <c:forEach var="b" items="${brands}">
                    <option value="${b.id}"
                        ${product != null && product.brandId == b.id ? "selected" : ""}>
                        ${b.name}
                    </option>
                </c:forEach>
            </select>
        </c:when>
        <c:otherwise>
            <input type="number" name="brandId"
                   value="${product != null ? product.brandId : ''}" required/>
        </c:otherwise>
    </c:choose>

    <!-- CategoryID -->
    <label>Loại sản phẩm:</label>
    <c:choose>
        <c:when test="${not empty categories}">
            <select name="categoryId" required>
                <option value="">-- Chọn loại --</option>
                <c:forEach var="categ" items="${categories}">
                    <option value="${categ.id}"
                        ${product != null && product.categoryId == categ.id ? "selected" : ""}>
                        ${categ.name}
                    </option>
                </c:forEach>
            </select>
        </c:when>
        <c:otherwise>
            <input type="number" name="categoryId"
                   value="${product != null ? product.categoryId : ''}" required/>
        </c:otherwise>
    </c:choose>

    <!-- SupplierID -->
    <label>Nhà cung cấp:</label>
    <c:choose>
        <c:when test="${not empty suppliers}">
            <select name="supplierId" required>
                <option value="">-- Chọn NCC --</option>
                <c:forEach var="s" items="${suppliers}">
                    <option value="${s.id}"
                        ${product != null && product.supplierId == s.id ? "selected" : ""}>
                        ${s.name}
                    </option>
                </c:forEach>
            </select>
        </c:when>
        <c:otherwise>
            <input type="number" name="supplierId"
                   value="${product != null ? product.supplierId : ''}" required/>
        </c:otherwise>
    </c:choose>

    <!-- CostPrice -->
    <label>Giá vốn (CostPrice):</label>
    <input type="number" step="0.01" name="costPrice"
           value="${product != null ? product.costPrice : ''}" required/>

    <!-- RetailPrice -->
    <label>Giá bán lẻ (RetailPrice):</label>
    <input type="number" step="0.01" name="retailPrice"
           value="${product != null ? product.retailPrice : ''}" required/>

    <!-- ImageURL -->
    <label>Ảnh (URL):</label>
    <input type="url" name="imageUrl"
           value="${product != null ? product.imageUrl : ''}" placeholder="https://..."/>

    <!-- VAT -->
    <label>VAT (%):</label>
    <input type="number" step="0.01" name="vat"
           value="${product != null ? product.vat : ''}" placeholder="VD: 10"/>

    <!-- CreatedAt: chỉ hiển thị khi sửa (DB tự set mặc định) -->
    <c:if test="${product != null && product.createdAt != null}">
        <label>Ngày tạo:</label>
        <input type="text" value="${product.createdAt}" disabled/>
    </c:if>

    <!-- IsActive -->
    <label style="display:flex;align-items:center;gap:8px;">
        <input type="checkbox" name="isActive"
               ${product == null || product.isActive ? "checked" : ""}/>
        Đang hoạt động
    </label>

    <button type="submit">💾 Lưu</button>
    <a href="product?action=list">❌ Hủy</a>
</form>
</body>
</html>

