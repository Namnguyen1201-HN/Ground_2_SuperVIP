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
    <meta charset="UTF-8">
    <title>${product != null ? "Sửa sản phẩm" : "Thêm sản phẩm"}</title>
</head>
<body>
    <h2>${product != null ? "Sửa sản phẩm" : "Thêm sản phẩm"}</h2>
    <form action="product" method="post">
        <input type="hidden" name="action" value="${product != null ? "update" : "insert"}"/>
        <c:if test="${product != null}">
            <input type="hidden" name="id" value="${product.productId}"/>
        </c:if>

        <label>Tên sản phẩm:</label>
        <input type="text" name="name" value="${product != null ? product.productName : ''}" required/><br/>

        <label>Giá:</label>
        <input type="number" name="price" value="${product != null ? product.price : ''}" required/><br/>

        <label>Số lượng:</label>
        <input type="number" name="quantity" value="${product != null ? product.quantity : ''}" required/><br/>

        <label>Ngày hết hạn:</label>
        <input type="date" name="expiryDate" 
               value="${product != null && product.expiryDate != null ? product.expiryDate : ''}"/><br/>

        <button type="submit">Lưu</button>
        <a href="product?action=list">Hủy</a>
    </form>
</body>
</html>
