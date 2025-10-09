<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý chi nhánh</title>
        <link href="css/admin/branch_management.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    </head>

    <body>
        <!-- Header -->
        <div>
            <%@ include file="../includes/header.jsp" %>
        </div>

        <!-- Layout chính: sidebar + nội dung -->
        <div class="container-fluid mt-3">
            <div class="row">
                <!-- Sidebar bên trái -->
                <%@ include file="../includes/sidebar-store.jsp" %>

                <!-- Nội dung chính bên phải -->
                <div class="col-md-9">
                    <div class="branch-container">
                        <div class="branch-header">
                            <h2><i class="fas fa-code-branch"></i> Quản lý chi nhánh</h2>
                            <button class="btn-primary">Tạo chi nhánh mới</button>
                        </div>

                        <div class="branch-table">
                            <table>
                                <thead>
                                    <tr>
                                        <th>TÊN CHI NHÁNH</th>
                                        <th>ĐỊA CHỈ</th>
                                        <th>SỐ ĐIỆN THOẠI</th>
                                        <th>TRẠNG THÁI</th>
                                        <th>THAO TÁC</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td><strong>Hà Nội</strong></td>
                                        <td>Từ Liêm Hà Nội</td>
                                        <td>12345678901</td>
                                        <td><span class="status active">HOẠT ĐỘNG</span></td>
                                        <td><button class="btn-delete"><i class="fas fa-trash"></i> Xóa</button></td>
                                    </tr>
                                    <tr>
                                        <td><strong>HCM</strong></td>
                                        <td>Tân Bình HCM</td>
                                        <td>01234567890</td>
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
