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
    <meta charset="UTF-8">
    <title>${product != null ? "Sửa sản phẩm" : "Thêm sản phẩm"}</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f8f9fa;
            margin: 0;
            padding: 40px;
        }
        h2 {
            text-align: center;
            color: #333;
            margin-bottom: 25px;
        }
        form {
            max-width: 500px;
            margin: auto;
            background: #fff;
            padding: 25px 30px;
            border-radius: 12px;
            box-shadow: 0px 4px 12px rgba(0,0,0,0.1);
        }
        label {
            display: block;
            font-weight: bold;
            margin-top: 15px;
            color: #555;
        }
        input[type="text"],
        input[type="number"],
        input[type="date"] {
            width: 100%;
            padding: 10px 12px;
            margin-top: 6px;
            border: 1px solid #ccc;
            border-radius: 8px;
            box-sizing: border-box;
            font-size: 14px;
        }
        input:focus {
            border-color: #4a90e2;
            outline: none;
            box-shadow: 0 0 5px rgba(74,144,226,0.4);
        }
        button {
            margin-top: 20px;
            padding: 10px 20px;
            width: 100%;
            background: #4a90e2;
            color: #fff;
            font-size: 16px;
            font-weight: bold;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            transition: 0.3s;
        }
        button:hover {
            background: #357ABD;
        }
        a {
            display: inline-block;
            margin-top: 15px;
            text-decoration: none;
            color: #4a90e2;
            font-weight: bold;
            text-align: center;
            width: 100%;
        }
        a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <h2>${product != null ? "Sửa sản phẩm" : "Thêm sản phẩm"}</h2>
    <form action="product" method="post">
        <input type="hidden" name="action" value="${product != null ? "update" : "insert"}"/>
        <c:if test="${product != null}">
            <input type="hidden" name="id" value="${product.productId}"/>
        </c:if>

        <label>Tên sản phẩm:</label>
        <input type="text" name="name" value="${product != null ? product.productName : ''}" required/>

        <label>Loại sản phẩm:</label>
        <input type="text" name="categoryId" value="${product != null ? product.categoryId : ''}" required/>

        <label>Bên cung cấp:</label>
        <input type="text" name="supplierId" value="${product != null ? product.supplierId : ''}" required/>

        <label>Giá:</label>
        <input type="number" name="price" value="${product != null ? product.price : ''}" required/>

        <label>Số lượng:</label>
        <input type="number" name="quantity" value="${product != null ? product.quantity : ''}" required/>

        <label>Ngày hết hạn:</label>
        <input type="date" name="expiryDate" 
               value="${product != null && product.expiryDate != null ? product.expiryDate : ''}"/>

        <button type="submit">💾 Lưu</button>
        <a href="product?action=list">❌ Hủy</a>
    </form>
</body>
</html>

