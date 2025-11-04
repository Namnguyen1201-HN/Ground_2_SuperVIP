<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <title><c:out value="${product != null ? 'Sửa sản phẩm' : 'Thêm sản phẩm'}"/></title>

    <!-- CSS tự chứa (đẹp, hiện đại) -->
    <style>
        :root{
          --bg-page:#f4f7fb;
          --bg-surface:#fff;
          --text:#1e293b;
          --text-sub:#475569;
          --primary:#2563eb;
          --primary-600:#1d4ed8;
          --ok:#22c55e; --ok-600:#16a34a;
          --border:#d0d7de;
          --radius:16px; --radius-sm:10px;
          --shadow-1:0 6px 18px rgba(0,0,0,.08);
          --focus: rgba(37,99,235,.15);
        }
        *{box-sizing:border-box}
        body{
          margin:0; padding:60px 20px; background:linear-gradient(135deg,#e8f1ff 0%,#f9fbff 100%);
          font-family:'Segoe UI', Roboto, system-ui, -apple-system, Arial, sans-serif; color:var(--text);
        }
        .wrap{max-width:720px;margin:0 auto}
        h2{text-align:center;margin:0 0 22px;font-size:26px;font-weight:700;color:#0f172a}

        .card{
          background:var(--bg-surface); border-radius:var(--radius);
          box-shadow:var(--shadow-1); padding:34px 38px;
        }
        .grid{display:grid; grid-template-columns:1fr 1fr; gap:16px}
        .grid-1{grid-template-columns:1fr}
        @media (max-width:760px){ .grid{grid-template-columns:1fr} }

        label{
          display:block; font-weight:600; color:var(--text-sub); margin-bottom:8px; font-size:14px;
        }
        .control{
          width:100%; padding:12px 14px; border:1px solid var(--border);
          border-radius:var(--radius-sm); font-size:15px; background:#fff;
          transition:border-color .2s, box-shadow .2s, background .2s;
        }
        .control:focus{outline:none; border-color:var(--primary); box-shadow:0 0 0 4px var(--focus)}

        /* Select xịn mũi tên tùy chỉnh */
        .pretty-select{
          appearance:none; -webkit-appearance:none; -moz-appearance:none;
          background:#fff url("data:image/svg+xml;utf8,<svg fill='gray' height='18' viewBox='0 0 24 24' width='18' xmlns='http://www.w3.org/2000/svg'><path d='M7 10l5 5 5-5z'/></svg>")
                     no-repeat right 12px center;
          background-size:16px;
          cursor:pointer;
        }
        .pretty-select:hover{border-color:#9fb0c2}
        .pretty-select:focus{background-color:#fff}

        .row{margin-bottom:16px}
        .row-inline{display:flex; align-items:center; gap:10px}

        .hint{font-size:12px; color:#64748b; margin-top:4px}

        .actions{display:flex; gap:12px; margin-top:20px}
        .btn{
          flex:1; padding:12px 16px; border:none; border-radius:12px;
          font-size:15px; font-weight:600; cursor:pointer; transition:all .25s;
          display:flex; align-items:center; justify-content:center; gap:8px;
        }
        .btn-primary{background:linear-gradient(135deg,#4a90e2,#2563eb); color:#fff}
        .btn-primary:hover{transform:translateY(-1px); box-shadow:0 6px 14px rgba(37,99,235,.25)}
        .btn-muted{background:#eef2f7; color:#0f172a}
        .btn-muted:hover{background:#e4e9f0}

        .checkbox{
          display:flex; align-items:center; gap:8px; padding:10px 12px;
          border:1px solid var(--border); border-radius:12px;
        }
        .badge{
          display:inline-flex; align-items:center; gap:6px;
          background:#eff6ff; color:#1d4ed8; border:1px solid #cfe0ff;
          font-size:12px; padding:4px 10px; border-radius:999px;
        }
        .readonly{
          background:#f8fafc; color:#475569;
        }
        .sep{height:1px; background:#eef2f7; margin:12px 0 6px}
    </style>
</head>
<body>
<div class="wrap">
    <h2><c:out value="${product != null ? 'Sửa sản phẩm' : 'Thêm sản phẩm'}"/></h2>

    <form class="card" action="${pageContext.request.contextPath}/product" method="post">
        <!-- hành động -->
        <input type="hidden" name="action" value="${product != null ? 'update' : 'insert'}"/>
        <c:if test="${product != null}">
            <input type="hidden" name="id" value="${product.productId}"/>
        </c:if>

        <!-- Tên sản phẩm -->
        <div class="row">
            <label for="name">Tên sản phẩm</label>
            <input id="name" class="control" type="text" name="name"
                   value="<c:out value='${product != null ? product.productName : ""}'/>" required />
        </div>

        <!-- Brand / Category -->
        <div class="grid">
            <div class="row">
                <label for="brandName">Thương hiệu</label>
                <select id="brandName" name="brandName" class="control pretty-select" required>
                    <option value="">-- Chọn thương hiệu --</option>
                    <c:forEach var="b" items="${brands}">
                        <option value="${b.brandName}"
                            <c:if test="${product != null && product.brandName == b.brandName}">selected</c:if>>
                            ${b.brandName}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="row">
                <label for="categoryName">Danh mục</label>
                <select id="categoryName" name="categoryName" class="control pretty-select" required>
                    <option value="">-- Chọn danh mục --</option>
                    <c:forEach var="cItem" items="${categories}">
                        <option value="${cItem.categoryName}"
                            <c:if test="${product != null && product.categoryName == cItem.categoryName}">selected</c:if>>
                            ${cItem.categoryName}
                        </option>
                    </c:forEach>
                </select>
            </div>
        </div>

        <!-- Supplier -->
        <div class="row">
            <label for="supplierName">Nhà cung cấp</label>
            <select id="supplierName" name="supplierName" class="control pretty-select" required>
                <option value="">-- Chọn NCC --</option>
                <c:forEach var="s" items="${suppliers}">
                    <option value="${s.supplierName}"
                        <c:if test="${product != null && product.supplierName == s.supplierName}">selected</c:if>>
                        ${s.supplierName}
                    </option>
                </c:forEach>
            </select>
        </div>

        <!-- (Chỉ hiện khi sửa) Tồn kho + Chi nhánh -->
        <c:if test="${product != null}">
            <div class="grid">
                <div class="row">
                    <label for="quantity">Số lượng (tồn mới)</label>
                    <input id="quantity" class="control" type="number" name="quantity" min="0"
                           value="<c:out value='${product.totalQty}'/>" placeholder="VD: 100" />
                    <div class="hint">Cập nhật tồn cho chi nhánh được chọn bên cạnh.</div>
                </div>
                <div class="row">
                    <label for="branchId">Chi nhánh</label>
                    <select id="branchId" name="branchId" class="control pretty-select">
                        <c:forEach var="br" items="${branches}">
                            <option value="${br.branchId}">${br.branchName}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>
        </c:if>

        <div class="sep"></div>

        <!-- Giá vốn / Giá bán -->
        <div class="grid">
            <div class="row">
                <label for="costPrice">Giá vốn (₫)</label>
                <input id="costPrice" class="control" type="number" step="0.01" name="costPrice"
                       value="<c:out value='${product != null ? product.costPrice : ""}'/>" required />
            </div>
            <div class="row">
                <label for="retailPrice">Giá bán lẻ (₫)</label>
                <input id="retailPrice" class="control" type="number" step="0.01" name="retailPrice"
                       value="<c:out value='${product != null ? product.retailPrice : ""}'/>" required />
            </div>
        </div>

        <!-- Ảnh / VAT -->
        <div class="grid">
            <div class="row">
                <label for="imageUrl">Ảnh (URL)</label>
                <input id="imageUrl" class="control" name="imageUrl"
                       value="<c:out value='${product != null ? product.imageUrl : ""}'/>"
                       placeholder="https://..." />
            </div>
            <div class="row">
                <label for="vat">VAT (%)</label>
                <input id="vat" class="control" type="number" step="0.01" name="vat"
                       value="<c:out value='${product != null ? product.vat : ""}'/>"
                       placeholder="VD: 10" />
            </div>
        </div>

        <!-- Ngày tạo (readonly khi sửa) -->
        <c:if test="${product != null && product.createdAt != null}">
            <div class="row">
                <label for="createdAt">Ngày tạo</label>
                <input id="createdAt" class="control readonly" type="text"
                       value="<c:out value='${product.createdAt}'/>" disabled />
            </div>
        </c:if>

        <!-- Trạng thái -->
        <div class="row">
            <label class="checkbox">
                <input type="checkbox" name="isActive"
                       <c:if test="${product == null || product.isActive}">checked</c:if> />
                <span>Đang hoạt động</span>
                <span class="badge">Trạng thái</span>
            </label>
        </div>

        <!-- Actions -->
        <div class="actions">
            <button type="submit" class="btn btn-primary">💾 Lưu</button>
            <a class="btn btn-muted" href="${pageContext.request.contextPath}/product?action=list">❌ Hủy</a>
        </div>
    </form>
</div>
</body>
</html>
