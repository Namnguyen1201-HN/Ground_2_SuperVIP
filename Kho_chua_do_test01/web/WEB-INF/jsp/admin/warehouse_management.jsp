<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý kho tổng</title>
        <link href="css/admin/warehouse_management.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    </head>

    <body>
        <!-- Header -->
        <div>
            <%@ include file="../includes/header.jsp" %>
        </div>

        <!-- Layout chính -->
        <div class="container-fluid mt-3">
            <div class="row">
                
                <!-- Sidebar bên trái -->
                <%@ include file="../includes/sidebar-store.jsp" %>

                <!-- Nội dung chính bên phải -->
                <div class="col-md-9">
                    <div class="warehouse-container">
                        <div class="warehouse-header">
                            <h2><i class="fa-solid fa-warehouse"></i> Quản lý kho tổng</h2>
                            <form action="WareHouseCreate" method="get">
                                <button type="submit" class="btn-primary">Tạo kho tổng mới</button>
                            </form>
                        </div>

                        <div class="warehouse-table">
                            <table>
                                <thead>
                                    <tr>
                                        <th>TÊN KHO TỔNG</th>
                                        <th>ĐỊA CHỈ</th>
                                        <th>SỐ ĐIỆN THOẠI</th>
                                        <th>TRẠNG THÁI</th>
                                        <th>THAO TÁC</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td><strong>Hà Nội</strong></td>
                                        <td>Nam Từ Liêm, Hà Nội</td>
                                        <td>09123456789</td>
                                        <td><span class="status active">HOẠT ĐỘNG</span></td>
                                        <td><button class="btn-delete"><i class="fas fa-trash"></i> Xóa</button></td>
                                    </tr>
                                    <tr>
                                        <td><strong>Hồ Chí Minh</strong></td>
                                        <td>Tân Bình, Hồ Chí Minh</td>
                                        <td>01234567891</td>
                                        <td><span class="status active">HOẠT ĐỘNG</span></td>
                                        <td><button class="btn-delete"><i class="fas fa-trash"></i> Xóa</button></td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
