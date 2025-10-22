<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>${product != null ? "Sửa sản phẩm" : "Thêm sản phẩm"}</title>
    <link href="${pageContext.request.contextPath}/css/admin/ProductForm.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<h2>${product != null ? "Sửa sản phẩm" : "Thêm sản phẩm"}</h2>

<form action="${pageContext.request.contextPath}/product" method="post">
    <!-- hành động -->
    <input type="hidden" name="action" value="${product != null ? 'update' : 'insert'}"/>
    <c:if test="${product != null}">
        <input type="hidden" name="id" value="${product.productId}"/>
    </c:if>

    <!-- ProductName -->
    <label>Tên sản phẩm:</label>
    <input type="text" name="name" value="${product != null ? product.productName : ''}" required/>

<!-- Brand -->
<label>Thương hiệu:</label>
<select name="brandName" required>
  <option value="">-- Chọn thương hiệu --</option>
  <c:forEach var="b" items="${brands}">
    <option value="${b.brandName}"
      <c:if test="${product != null && product.brandName == b.brandName}">selected</c:if>>
      ${b.brandName}
    </option>
  </c:forEach>
</select>

<!-- Category -->
<label>Danh mục:</label>
<select name="categoryName" required>
  <option value="">-- Chọn danh mục --</option>
  <c:forEach var="c" items="${categories}">
    <option value="${c.categoryName}"
      <c:if test="${product != null && product.categoryName == c.categoryName}">selected</c:if>>
      ${c.categoryName}
    </option>
  </c:forEach>
</select>

<!-- Supplier -->
<label>Nhà cung cấp:</label>
<select name="supplierName" required>
  <option value="">-- Chọn NCC --</option>
  <c:forEach var="s" items="${suppliers}">
    <option value="${s.supplierName}"
      <c:if test="${product != null && product.supplierName == s.supplierName}">selected</c:if>>
      ${s.supplierName}
    </option>
  </c:forEach>
</select>

<!-- (Tùy chọn) chỉnh tồn -->
<c:if test="${product != null}">
  <label>Số lượng (tồn mới):</label>
  <input type="number" name="quantity" min="0" value="${product.totalQty}"/>

  <label>Chi nhánh:</label>
  <select name="branchId">
    <c:forEach var="br" items="${branches}">
      <option value="${br.branchID}">${br.branchName}</option>
    </c:forEach>
  </select>
</c:if>


    <!-- Giá vốn -->
    <label>Giá vốn (CostPrice):</label>
    <input type="number" step="0.01" name="costPrice" value="${product != null ? product.costPrice : ''}" required/>

    <!-- Giá bán -->
    <label>Giá bán lẻ (RetailPrice):</label>
    <input type="number" step="0.01" name="retailPrice" value="${product != null ? product.retailPrice : ''}" required/>

    <!-- Ảnh -->
    <label>Ảnh (URL):</label>
    <input type="url" name="imageUrl" value="${product != null ? product.imageUrl : ''}" placeholder="https://..."/>

    <!-- VAT -->
    <label>VAT (%):</label>
    <input type="number" step="0.01" name="vat" value="${product != null ? product.vat : ''}" placeholder="VD: 10"/>

    <!-- Ngày tạo (readonly khi sửa) -->
    <c:if test="${product != null && product.createdAt != null}">
        <label>Ngày tạo:</label>
        <input type="text" value="${product.createdAt}" disabled/>
    </c:if>

    <!-- Trạng thái -->
    <label style="display:flex;align-items:center;gap:8px;">
        <input type="checkbox" name="isActive"
               <c:if test="${product == null || product.isActive}">checked</c:if> />
        Đang hoạt động
    </label>

    <button type="submit">💾 Lưu</button>
    <a href="${pageContext.request.contextPath}/product?action=list">❌ Hủy</a>
</form>
</body>
</html>
