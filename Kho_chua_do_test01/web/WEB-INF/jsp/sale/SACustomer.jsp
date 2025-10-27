<%-- 
    Document   : SACustomer
    Created on : Oct 26, 2025, 9:39:46 PM
    Author     : Kawaii
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý Khách hàng</title>
    <style>
        body { font-family: system-ui, Arial, sans-serif; margin: 20px; }
        .toolbar { display:flex; gap:10px; align-items:center; flex-wrap: wrap; }
        .toolbar input, .toolbar select { padding:6px 8px; }
        table { border-collapse: collapse; width: 100%; margin-top: 12px; }
        th, td { border: 1px solid #ddd; padding: 8px; }
        th { background: #f6f7f9; text-align: left; }
        .actions a { margin-right: 8px; }
        .pagination { margin-top: 12px; display: flex; gap: 6px; align-items:center; }
        .pagination a, .pagination span { padding: 6px 10px; border: 1px solid #ddd; text-decoration: none; }
        .pagination .active { background: #333; color: #fff; }
        .badge { font-size: 12px; padding: 2px 6px; border-radius: 6px; background: #eef; }
        .muted { color: #666; font-size: 12px; }
    </style>
</head>
<body>
<h2>Danh sách khách hàng</h2>

<form class="toolbar" method="get" action="${pageContext.request.contextPath}/customers">
    <input type="hidden" name="action" value="list"/>

    <input type="text" name="keyword" placeholder="Tìm theo ID / Họ tên"
           value="${fn:escapeXml(keyword)}"/>

    <select name="gender">
        <option value="all"   ${gender == 'all' ? 'selected' : ''}>Giới tính (Tất cả)</option>
        <option value="male"  ${gender == 'male' ? 'selected' : ''}>Nam</option>
        <option value="female"${gender == 'female' ? 'selected' : ''}>Nữ</option>
    </select>

    <input type="number" name="branchId" placeholder="Branch ID"
           value="${branchId}"/>

    <input type="number" name="minSpent" placeholder="Tối thiểu đã chi"
           step="1000" value="${minSpent}"/>

    <input type="number" name="maxSpent" placeholder="Tối đa đã chi"s
           step="1000" value="${maxSpent}"/>


    <button type="submit">Lọc</button>
    <a href="${pageContext.request.contextPath}/customers" style="text-decoration:none;">Xoá lọc</a>

    <span style="margin-left:auto"></span>

    <a href="${pageContext.request.contextPath}/customers?action=create">+ Thêm khách hàng</a>
</form>

<p class="muted">
    Tổng: <b>${totalItems}</b> khách hàng
    <c:if test="${gender != 'all' || not empty keyword || branchId != null || minSpent != null || maxSpent != null}">
        <span class="badge">Đang áp dụng bộ lọc</span>
    </c:if>
</p>

<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>Họ tên</th>
        <th>Điện thoại</th>
        <th>Giới tính</th>
        <th>Địa chỉ</th>
        <th>Đã chi</th>
        <th>Thao tác</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="c" items="${customers}">
        <tr>
            <td>${c.customerID}</td>
            <td><a href="${pageContext.request.contextPath}/customers?action=detail&id=${c.customerID}">
                ${c.fullname}</a></td>
            <td>${c.phoneNumber}</td>
            <td><c:choose>
                    <c:when test="${c.gender}">Nam</c:when>
                    <c:otherwise>Nữ</c:otherwise>
                </c:choose>
            </td>
            <td>${c.address}</td>
            <td>
                <fmt:formatNumber value="${c.totalSpent}" type="number" groupingUsed="true"/>
            </td>
            <td class="actions">
                <a href="${pageContext.request.contextPath}/customers?action=edit&id=${c.customerID}">Sửa</a>
                <a href="${pageContext.request.contextPath}/customers?action=delete&id=${c.customerID}"
                   onclick="return confirm('Xoá khách hàng này?');">Xoá</a>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty customers}">
        <tr><td colspan="7" style="text-align:center;">Không có dữ liệu</td></tr>
    </c:if>
    </tbody>
</table>

<!-- Phân trang -->
<c:if test="${totalPages > 1}">
    <div class="pagination">
        <c:set var="ctx" value="${pageContext.request.contextPath}"/>
        <c:set var="baseQuery">
            action=list
            &keyword=${fn:escapeXml(keyword)}
            &gender=${gender}
            &branchId=${branchId}
            &minSpent=${minSpent}
            &maxSpent=${maxSpent}
            &pageSize=${pageSize}
        </c:set>

        <c:set var="prev" value="${page - 1}"/>
        <c:set var="next" value="${page + 1}"/>

        <c:if test="${page > 1}">
            <a href="${ctx}/customers?${baseQuery}&page=1">« Đầu</a>
            <a href="${ctx}/customers?${baseQuery}&page=${prev}">‹ Trước</a>
        </c:if>

        <c:forEach var="p" begin="${page-2 < 1 ? 1 : page-2}" end="${page+2 > totalPages ? totalPages : page+2}">
            <c:choose>
                <c:when test="${p == page}">
                    <span class="active">${p}</span>
                </c:when>
                <c:otherwise>
                    <a href="${ctx}/customers?${baseQuery}&page=${p}">${p}</a>
                </c:otherwise>
            </c:choose>
        </c:forEach>

        <c:if test="${page < totalPages}">
            <a href="${ctx}/customers?${baseQuery}&page=${next}">Sau ›</a>
            <a href="${ctx}/customers?${baseQuery}&page=${totalPages}">Cuối »</a>
        </c:if>
    </div>
</c:if>

</body>
</html>
