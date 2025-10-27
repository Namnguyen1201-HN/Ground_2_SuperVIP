<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title><c:choose><c:when test="${not empty move}">Sửa yêu cầu</c:when><c:otherwise>Tạo yêu cầu mới</c:otherwise></c:choose></title>
    <style>
        table { border-collapse: collapse; margin-top: 10px; }
        th, td { border: 1px solid #ccc; padding: 6px; }
    </style>
</head>
<body>
<h2><c:choose><c:when test="${not empty move}">✏️ Sửa yêu cầu dịch chuyển</c:when><c:otherwise>➕ Tạo yêu cầu mới</c:otherwise></c:choose></h2>

<form method="post" action="${pageContext.request.contextPath}/sa-stock">
    <input type="hidden" name="action" value="${empty move ? 'create' : 'updateHeader'}"/>
    <c:if test="${not empty move}">
        <input type="hidden" name="id" value="${move.movementId}"/>
    </c:if>

    <label>Loại yêu cầu:</label>
    <select name="movementType">
        <option value="IMPORT"
            <c:if test="${not empty move and move.movementType eq 'IMPORT'}">selected</c:if>>
            Nhập hàng
        </option>
        <option value="TRANSFER"
            <c:if test="${not empty move and move.movementType eq 'TRANSFER'}">selected</c:if>>
            Chuyển kho
        </option>
    </select><br/>

    <label>Từ NCC:</label>
    <input type="number" name="fromSupplierId" value="${not empty move ? move.fromSupplierId : ''}"/> <br/>

    <label>Từ chi nhánh:</label>
    <input type="number" name="fromBranchId" value="${not empty move ? move.fromBranchId : ''}"/> <br/>

    <label>Từ kho:</label>
    <input type="number" name="fromWarehouseId" value="${not empty move ? move.fromWarehouseId : ''}"/> <br/>

    <label>Đến chi nhánh:</label>
    <input type="number" name="toBranchId" value="${not empty move ? move.toBranchId : ''}"/> <br/>

    <label>Đến kho:</label>
    <input type="number" name="toWarehouseId" value="${not empty move ? move.toWarehouseId : ''}"/> <br/>

    <label>Ghi chú:</label><br/>
    <textarea name="note" rows="3" cols="40"><c:out value='${not empty move ? move.note : ""}'/></textarea><br/>

    <!-- Chỉ hiển thị phần chi tiết khi tạo mới -->
    <c:if test="${empty move}">
        <h4>Chi tiết sản phẩm</h4>
        <table id="detailTable">
            <thead>
            <tr><th>ProductDetailID</th><th>Số lượng</th><th></th></tr>
            </thead>
            <tbody>
            <tr>
                <td><input type="number" name="productDetailId" required></td>
                <td><input type="number" name="quantity" min="1" required></td>
                <td><button type="button" onclick="removeRow(this)">X</button></td>
            </tr>
            </tbody>
        </table>
        <button type="button" onclick="addRow()">➕ Thêm dòng</button>
    </c:if>

    <br/>
    <button type="submit">💾 Lưu</button>
    <a href="${pageContext.request.contextPath}/sa-stock?action=list">❌ Hủy</a>
</form>

<script>
function addRow() {
    const tb = document.querySelector("#detailTable tbody");
    const tr = document.createElement("tr");
    tr.innerHTML = `
        <td><input type="number" name="productDetailId" required></td>
        <td><input type="number" name="quantity" min="1" required></td>
        <td><button type="button" onclick="removeRow(this)">X</button></td>`;
    tb.appendChild(tr);
}
function removeRow(btn) {
    btn.closest('tr').remove();
}
</script>

</body>
</html>
