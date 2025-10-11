<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sách hàng hóa</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link href="css/warehouse/product.css" rel="stylesheet" type="text/css"/>
    
    
</head>
<body>
    <%@ include file="../warehouse/header-warehouse.jsp" %>

    <div class="page-container">
        <!-- Sidebar bộ lọc -->
        <div class="filter-sidebar">
            <div class="filter-group">
                <h6>Nhóm hàng</h6>
                <div><input type="checkbox"> Máy tính</div>
                <div><input type="checkbox"> Điện thoại</div>
                <div><input type="checkbox"> Phụ kiện</div>
                <div><input type="checkbox"> Tablet</div>
            </div>

            <div class="filter-group">
                <h6>Tồn kho</h6>
                <div><input type="radio" name="stock" checked> Tất cả</div>
                <div><input type="radio" name="stock"> Còn hàng trong kho</div>
                <div><input type="radio" name="stock"> Hết hàng trong kho</div>
            </div>

            <div class="filter-group">
                <h6>Khoảng giá</h6>
                <input type="text" placeholder="Giá từ">
                <input type="text" placeholder="Giá đến">
            </div>

            <div class="filter-group">
                <h6>Trạng thái</h6>
                <div><input type="radio" name="status" checked> Tất cả</div>
                <div><input type="radio" name="status"> Đang bán</div>
                <div><input type="radio" name="status"> Ngừng bán</div>
            </div>

            <button class="btn-apply">🔍 Áp dụng lọc</button>
            <button class="btn-reset">🗑️ Xóa bộ lọc</button>
        </div>

        <!-- Danh sách sản phẩm -->
        <div class="product-list">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <h5>Hàng hóa</h5>
                <div class="search-box">
                    <input type="text" placeholder="🔍 Theo tên hàng">
                </div>
            </div>

            <table class="table table-bordered table-striped align-middle">
                <thead>
                    <tr>
                        <th>Ảnh</th>
                        <th>Mã hàng</th>
                        <th>Tên hàng</th>
                        <th>Giá bán</th>
                        <th>Tồn kho</th>
                        <th>Trạng thái</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td><img src="https://cdn.tgdd.vn/Products/Images/42/281570/iphone-15-pro-max-blue-thumbnew-600x600.jpg" alt="iPhone 15 Pro Max"></td>
                        <td>1</td>
                        <td>iPhone 15 Pro Max 256GB, chip A17 Pro, màn hình Super Retina XDR 6.7 inch</td>
                        <td>29.990.000</td>
                        <td>10</td>
                        <td><span class="status-badge">Đang bán</span></td>
                    </tr>
                    <tr>
                        <td><img src="https://cdn.tgdd.vn/Products/Images/42/303580/samsung-galaxy-s24-ultra-grey-thumbnew-600x600.jpg" alt="Galaxy S24 Ultra"></td>
                        <td>2</td>
                        <td>Samsung Galaxy S24 Ultra 512GB, S Pen, màn hình Dynamic AMOLED 6.8 inch</td>
                        <td>26.990.000</td>
                        <td>8</td>
                        <td><span class="status-badge">Đang bán</span></td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>

