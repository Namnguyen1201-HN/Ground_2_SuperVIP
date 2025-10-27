<%-- 
    Document   : SAMoveDetail
    Created on : Oct 19, 2025, 4:38:56 PM
    Author     : Kawaii
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chi tiết yêu cầu kho #${move.movementId}</title>
</head>
<body>
<h2>📄 Chi tiết yêu cầu #${move.movementId}</h2>

<table border="1" cellpadding="6">
    <tr><th>Loại</th><td>${move.movementType}</td></tr>
    <tr><th>Từ</th>
        <td>
            <c:choose>
                <c:when test="${move.fromSupplierId != null}">Nhà cung cấp #${move.fromSupplierId}</c:when>
                <c:when test="${move.fromBranchId != null}">Chi nhánh #${move.fromBranchId}</c:when>
                <c:when test="${move.fromWarehouseId != null}">Kho #${move.fromWarehouseId}</c:when>
                <c:otherwise>-</c:otherwise>
            </c:choose>
        </td></tr>
    <tr><th>Đến</th>
        <td>
            <c:choose>
                <c:when test="${move.toBranchId != null}">Chi nhánh #${move.toBranchId}</c:when>
                <c:when test="${move.toWarehouseId != null}">Kho #${move.toWarehouseId}</c:when>
                <c:otherwise>-</c:otherwise>
            </c:choose>
        </td></tr>
    <tr><th>Người tạo</th><td>${move.createdBy}</td></tr>
    <tr><th>Ngày tạo</th><td>${move.createdAt}</td></tr>
    <tr><th>Ghi chú</th><td>${move.note}</td></tr>
</table>

<h3>Danh sách sản phẩm</h3>
<table border="1" cellpadding="6">
    <thead>
    <tr><th>#</th><th>ProductDetailID</th><th>Số lượng</th><th>Đã quét</th></tr>
    </thead>
    <tbody>
    <c:forEach var="d" items="${move.details}" varStatus="st">
        <tr>
            <td>${st.index + 1}</td>
            <td>${d.productDetailId}</td>
            <td>${d.quantity}</td>
            <td>${d.quantityScanned}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<br/>
<a href="sa-stock?action=edit&id=${move.movementId}">✏️ Sửa</a>
<a href="sa-stock?action=list">🔙 Quay lại</a>

</body>
</html>
