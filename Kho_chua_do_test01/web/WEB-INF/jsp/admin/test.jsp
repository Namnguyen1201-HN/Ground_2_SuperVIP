<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Chi Tiết / Chỉnh Sửa Sản Phẩm</title>
    <style>
        :root{
            --bg: #0f172a;
            --bg-soft: #111827;
            --card: #ffffff;
            --text: #0f172a;
            --muted: #64748b;
            --primary: #6366f1;
            --primary-2: #7c3aed;
            --ring: #93c5fd;
            --border: #e5e7eb;
            --danger: #ef4444;
            --success: #10b981;
            --radius: 14px;
            --shadow: 0 18px 50px rgba(0,0,0,.15);
            --shadow-soft: 0 8px 28px rgba(0,0,0,.08);
        }
        *{box-sizing:border-box}
        html,body{height:100%}
        body{
            margin:0;
            font-family: ui-sans-serif, system-ui, -apple-system, Segoe UI, Roboto, Helvetica, Arial, "Apple Color Emoji","Segoe UI Emoji";
            color: var(--text);
            background:
                radial-gradient(1200px 600px at -10% -20%, rgba(124,58,237,.18) 0%, rgba(124,58,237,0) 60%),
                radial-gradient(1200px 600px at 110% 120%, rgba(99,102,241,.16) 0%, rgba(99,102,241,0) 60%),
                linear-gradient(135deg,#667eea 0%, #764ba2 100%);
            min-height:100vh;
            padding: clamp(16px, 3vw, 32px);
        }
        .container{
            max-width: 1100px;
            margin: 0 auto;
        }
        .card{
            background: var(--card);
            border-radius: var(--radius);
            box-shadow: var(--shadow);
            overflow: clip;
        }
        .page-header{
            background: linear-gradient(135deg, var(--primary) 0%, var(--primary-2) 100%);
            color: #fff;
            padding: clamp(18px, 3.5vw, 28px) clamp(18px, 3.5vw, 32px);
        }
        .page-title{
            margin: 0;
            font-size: clamp(20px, 3.2vw, 28px);
            line-height: 1.2;
            font-weight: 700;
            letter-spacing: .2px;
        }
        .content{
            display:grid;
            grid-template-columns: 380px 1fr;
            gap: clamp(18px, 2.8vw, 28px);
            padding: clamp(18px, 3.5vw, 32px);
        }
        @media (max-width: 920px){
            .content{ grid-template-columns: 1fr; }
        }

        /* Left: image preview */
        .media{
            position: relative;
        }
        .media .preview{
            position: relative;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: var(--shadow-soft);
            background: #f3f4f6;
        }
        .media .preview img{
            display:block;
            width:100%;
            height:auto;
            object-fit: cover;
            aspect-ratio: 1 / 1;
        }
        .hint{
            margin-top:6px;
            font-size: 12px;
            color: var(--muted);
        }

        /* Form */
        form{
            display:flex;
            flex-direction: column;
            gap: 18px;
        }
        .grid-2{
            display:grid;
            grid-template-columns: 1fr 1fr;
            gap: 16px;
        }
        @media (max-width: 640px){
            .grid-2{ grid-template-columns: 1fr; }
        }
        .field{
            display:flex;
            flex-direction: column;
            gap: 8px;
        }
        .label{
            font-size: 12px;
            font-weight: 700;
            color: var(--muted);
            letter-spacing: .4px;
            text-transform: uppercase;
        }
        .control{
            display:flex;
            align-items:center;
            position:relative;
        }
        input[type="text"],
        input[type="number"],
        input[type="url"],
        select{
            width: 100%;
            padding: 12px 12px;
            border: 1px solid var(--border);
            border-radius: 10px;
            font-size: 15px;
            transition: border-color .2s, box-shadow .2s, transform .02s;
            outline: none;
            background:#fff;
        }
        input:focus,
        select:focus{
            border-color: var(--ring);
            box-shadow: 0 0 0 4px rgba(147,197,253,.35);
        }
        input[disabled]{
            background: #f8fafc;
            color: #475569;
        }

        /* Checkbox line */
        .inline{
            display:flex;
            align-items:center;
            gap: 10px;
        }
        .inline .label{ margin:0; text-transform:none; letter-spacing: 0; font-weight:600; }

        /* Buttons */
        .actions{
            display:flex;
            gap: 10px;
            margin-top: 8px;
            flex-wrap: wrap;
        }
        .btn{
            padding: 12px 16px;
            border-radius: 10px;
            border: 1px solid transparent;
            font-weight: 700;
            cursor:pointer;
            transition: transform .06s ease, box-shadow .2s ease, background .2s ease;
        }
        .btn:active{ transform: translateY(1px); }
        .btn-primary{
            background: linear-gradient(135deg, var(--primary) 0%, var(--primary-2) 100%);
            color:#fff;
            box-shadow: 0 10px 24px rgba(99,102,241,.28);
        }
        .btn-primary:hover{ box-shadow: 0 14px 34px rgba(99,102,241,.36); }
        .btn-secondary{
            background:#fff;
            border-color: var(--border);
            color: #111827;
        }
        .btn-secondary:hover{
            background:#f8fafc;
        }

        /* Helper badges */
        .badge{
            display:inline-block;
            font-size:12px;
            padding:6px 10px;
            background:#f1f5f9;
            color:#0f172a;
            border-radius: 999px;
            border:1px solid #e2e8f0;
        }

        /* Small utilities */
        .muted{ color: var(--muted); }
        .sr-only{
            position:absolute; width:1px; height:1px; padding:0; margin:-1px; overflow:hidden; clip:rect(0,0,0,0); border:0;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="card">
        <header class="page-header">
            <h1 class="page-title">Chi Tiết / Chỉnh Sửa Sản Phẩm</h1>
        </header>

        <section class="content">
            <!-- Cột ảnh -->
            <aside class="media">
                <div class="preview" aria-live="polite">
                    <img id="previewImg"
                         src="${product.imageUrl != null ? product.imageUrl : 'https://via.placeholder.com/600x600?text=No+Image'}"
                         alt="${product.productName != null ? product.productName : 'Hình sản phẩm'}">
                </div>

                <div class="field" style="margin-top:12px">
                    <label for="imageUrlInput" class="label">Ảnh (URL)</label>
                    <div class="control">
                        <input type="url" id="imageUrlInput" form="productEditForm" name="imageUrl"
                               placeholder="https://..."
                               value="${product.imageUrl}">
                    </div>
                    <p class="hint">Dán link ảnh để xem trước ngay. Ảnh vuông sẽ hiển thị đẹp nhất.</p>
                </div>
            </aside>

            <!-- Cột form -->
            <section>
                <form id="productEditForm" action="${pageContext.request.contextPath}/product" method="post" novalidate>
                    <input type="hidden" name="action" value="update"/>
                    <input type="hidden" name="id" value="${product.productId}"/>

                    <div class="grid-2">
                        <div class="field">
                            <label class="label" for="productId">Mã Sản Phẩm</label>
                            <div class="control">
                                <input id="productId" type="text" value="${product.productId}" disabled>
                            </div>
                        </div>

                        <div class="field">
                            <label class="label" for="name">Tên Sản Phẩm</label>
                            <div class="control">
                                <input id="name" type="text" name="name" value="${product.productName}" required>
                            </div>
                        </div>
                    </div>

                    <div class="grid-2">
                        <div class="field">
                            <label class="label" for="brandName">Thương Hiệu</label>
                            <c:choose>
                                <c:when test="${not empty brands}">
                                    <div class="control">
                                        <select id="brandName" name="brandName">
                                            <option value="">-- Chọn thương hiệu --</option>
                                            <c:forEach var="b" items="${brands}">
                                                <option value="${b.name}"
                                                        <c:if test="${product.brandName == b.name}">selected</c:if>>
                                                    ${b.name}
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="control">
                                        <input id="brandName" type="text" name="brandName"
                                               value="${product.brandName}" placeholder="Nhập tên thương hiệu">
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <div class="field">
                            <label class="label" for="categoryName">Danh Mục</label>
                            <c:choose>
                                <c:when test="${not empty categories}">
                                    <div class="control">
                                        <select id="categoryName" name="categoryName">
                                            <option value="">-- Chọn danh mục --</option>
                                            <c:forEach var="c" items="${categories}">
                                                <option value="${c.categoryName}"
                                                        <c:if test="${product.categoryName == c.categoryName}">selected</c:if>>
                                                    ${c.categoryName}
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="control">
                                        <input id="categoryName" type="text" name="categoryName"
                                               value="${product.categoryName}" placeholder="Nhập tên danh mục">
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <div class="field">
                        <label class="label" for="supplierName">Nhà Cung Cấp</label>
                        <c:choose>
                            <c:when test="${not empty suppliers}">
                                <div class="control">
                                    <select id="supplierName" name="supplierName">
                                        <option value="">-- Chọn NCC --</option>
                                        <c:forEach var="s" items="${suppliers}">
                                            <option value="${s.name}"
                                                    <c:if test="${product.supplierName == s.name}">selected</c:if>>
                                                ${s.name}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="control">
                                    <input id="supplierName" type="text" name="supplierName"
                                           value="${product.supplierName}" placeholder="Nhập tên NCC">
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="grid-2">
                        <div class="field">
                            <label class="label" for="costPrice">Giá Vốn</label>
                            <div class="control">
                                <input id="costPrice" type="number" step="0.01" name="costPrice" value="${product.costPrice}">
                            </div>
                        </div>
                        <div class="field">
                            <label class="label" for="retailPrice">Giá Bán Lẻ</label>
                            <div class="control">
                                <input id="retailPrice" type="number" step="0.01" name="retailPrice" value="${product.retailPrice}">
                            </div>
                        </div>
                    </div>

                    <div class="grid-2">
                        <div class="field">
                            <label class="label" for="vat">VAT (%)</label>
                            <div class="control">
                                <input id="vat" type="number" step="0.01" name="vat" value="${product.vat}">
                            </div>
                        </div>

                        <div class="field">
                            <label class="label" for="totalQuantity">Số hàng tồn</label>
                            <div class="control">
                                <input id="totalQuantity" type="number" step="0.01" name="totalQuantity" value="${product.totalQty}">
                            </div>
                        </div>
                    </div>

                    <div class="field">
                        <div class="inline">
                            <input type="checkbox" id="isActive" name="isActive"
                                   <c:if test="${product.isActive}">checked</c:if>>
                            <label for="isActive" class="label">Đang kinh doanh</label>
                            <span class="badge muted">Trạng thái</span>
                        </div>
                    </div>

                    <div class="actions">
                        <button type="submit" class="btn btn-primary">💾 Lưu</button>
                        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/product?action=list">❌ Hủy</a>
                        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/product?action=detail&id=${product.productId}">↩️ Xem chi tiết</a>
                    </div>
                </form>
            </section>
        </section>
    </div>
</div>

<script>
    // Xem trước ảnh ngay khi đổi URL
    (function(){
        const imageUrlInput = document.getElementById('imageUrlInput');
        const previewImg = document.getElementById('previewImg');
        if(!imageUrlInput || !previewImg) return;

        const fallback = 'https://via.placeholder.com/600x600?text=No+Image';

        const update = () => {
            const v = imageUrlInput.value && imageUrlInput.value.trim() ? imageUrlInput.value.trim() : fallback;
            previewImg.src = v;
        };

        imageUrlInput.addEventListener('input', update);
        // Khi ảnh lỗi, quay về fallback
        previewImg.addEventListener('error', () => { previewImg.src = fallback; }, { once:false });
    })();
</script>
</body>
</html>
