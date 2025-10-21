<%-- 
    Document   : SAmovelist
    Created on : Oct 19, 2025, 4:37:27 PM
    Author     : Kawaii
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sách yêu cầu dịch chuyển kho</title>
    <link href="${pageContext.request.contextPath}/css/admin/table.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<h2>📦 Danh sách yêu cầu nhập / chuyển hàng</h2>

<!-- Bộ lọc -->
<form method="get" action="sa-stock">
    <input type="hidden" name="action" value="list"/>
    Loại:
    <select name="type">
        <option value="">Tất cả</option>
        <option value="IMPORT" ${type=="IMPORT"?"selected":""}>Nhập hàng</option>
        <option value="TRANSFER" ${type=="TRANSFER"?"selected":""}>Chuyển kho</option>
    </select>
    Chi nhánh:
    <input type="number" name="branchId" value="${branchId}"/>
    Kho:
    <input type="number" name="warehouseId" value="${warehouseId}"/>
    Từ ngày:
    <input type="datetime-local" name="from" value="${from}"/>
    Đến ngày:
    <input type="datetime-local" name="to" value="${to}"/>
    <button type="submit">Lọc</button>
    <a href="sa-stock?action=add">➕ Tạo yêu cầu mới</a>
</form>

<hr/>

<!-- Danh sách -->
<table border="1" cellspacing="0" cellpadding="6">
    <thead>
    <tr>
        <th>ID</th>
        <th>Loại</th>
        <th>Từ</th>
        <th>Đến</th>
        <th>Người tạo</th>
        <th>Ngày tạo</th>
        <th>Ghi chú</th>
        <th>Hành động</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="m" items="${moves}">
        <tr>
            <td>${m.movementId}</td>
            <td>${m.movementType}</td>
            <td>
                <c:choose>
                    <c:when test="${m.fromSupplierId != null}">NCC #${m.fromSupplierId}</c:when>
                    <c:when test="${m.fromBranchId != null}">CN #${m.fromBranchId}</c:when>
                    <c:when test="${m.fromWarehouseId != null}">Kho #${m.fromWarehouseId}</c:when>
                    <c:otherwise>-</c:otherwise>
                </c:choose>
            </td>
            <td>
                <c:choose>
                    <c:when test="${m.toBranchId != null}">CN #${m.toBranchId}</c:when>
                    <c:when test="${m.toWarehouseId != null}">Kho #${m.toWarehouseId}</c:when>
                    <c:otherwise>-</c:otherwise>
                </c:choose>
            </td>
            <td>${m.createdBy}</td>
            <td>${m.createdAt}</td>
            <td>${m.note}</td>
            <td>
                <a href="sa-stock?action=view&id=${m.movementId}">👁 Xem</a>
                <a href="sa-stock?action=edit&id=${m.movementId}">✏️ Sửa</a>
                <a href="sa-stock?action=delete&id=${m.movementId}" onclick="return confirm('Xóa yêu cầu này?');">🗑 Xóa</a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<!-- Phân trang -->
<c:if test="${total > pageSize}">
    <div style="margin-top:15px;">
        <c:set var="pageCount" value="${(total / pageSize) + (total % pageSize > 0 ? 1 : 0)}"/>
        <c:forEach var="i" begin="1" end="${pageCount}">
            <a href="sa-stock?action=list&page=${i}&type=${type}&branchId=${branchId}&warehouseId=${warehouseId}"
               style="margin:0 5px; ${i==page?'font-weight:bold;':''}">
                ${i}
            </a>
        </c:forEach>
    </div>
</c:if>

</body>
</html>
