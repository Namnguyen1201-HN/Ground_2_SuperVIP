
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Thông tin cá nhân</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
         <link href="css/warehouse/Information.css" rel="stylesheet" type="text/css"/>
        
    </head>

    <body>
        <%@ include file="../warehouse/header-warehouse.jsp" %>

        <div class="container-fluid">
            <div class="row">
                <!-- Sidebar -->
                <%@ include file="../warehouse/sidebar-warehouse.jsp" %>

                <!-- Nội dung chính -->
                <div class="col-md-9">
                    <div class="card p-4">
                        <div class="card-header bg-white mb-3">
                            <i class="fa-solid fa-circle-info me-2 text-primary"></i>Thông tin cá nhân
                        </div>

                        <!-- Tên + vai trò -->
                        <div class="mb-3">
                            <h4 class="fw-bold mb-0">Nammm</h4>
                            <button class="btn btn-outline-primary btn-sm mt-1">QUẢN LÝ KHO TỔNG</button>
                        </div>

                        <!-- Thông tin cá nhân và liên hệ -->
                        <div class="row">
                            <!-- Cột trái -->
                            <div class="col-md-6">
                                <div class="info-section">
                                    <h6><i class="fa-regular fa-id-card me-2"></i>Thông tin cá nhân</h6>

                                    <div class="mb-2">
                                        <label class="label-bold">Mã người dùng:</label>
                                        <span class="ms-2 text-secondary">#6</span>
                                    </div>

                                    <div class="mb-3">
                                        <label class="label-bold">Họ và tên:</label>
                                        <input type="text" class="form-control" value="Nammm">
                                    </div>

                                    <div class="mb-3">
                                        <label class="label-bold">Giới tính:</label>
                                        <select class="form-select">
                                            <option selected>Nam</option>
                                            <option>Nữ</option>
                                        </select>
                                    </div>

                                    <div class="mb-3">
                                        <label class="label-bold">Ngày sinh:</label>
                                        <input type="date" class="form-control" value="2025-09-29">
                                    </div>

                                    <div class="mb-3">
                                        <label class="label-bold">CCCD/Hộ chiếu:</label>
                                        <input type="text" class="form-control">
                                    </div>
                                </div>
                            </div>

                            <!-- Cột phải -->
                            <div class="col-md-6">
                                <div class="info-section">
                                    <h6><i class="fa-solid fa-address-book me-2"></i>Thông tin liên hệ</h6>

                                    <div class="mb-3">
                                        <label class="label-bold">Email:</label>
                                        <input type="email" class="form-control" value="Nam3@gmail.com">
                                    </div>

                                    <div class="mb-3">
                                        <label class="label-bold">Số điện thoại:</label>
                                        <input type="text" class="form-control" value="0949614558">
                                    </div>

                                    <div class="mb-3">
                                        <label class="label-bold">Địa chỉ:</label>
                                        <input type="text" class="form-control" value="Hà Nội">
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Thông tin chuỗi cửa hàng -->
                        <div class="info-section">
                            <h6><i class="fa-solid fa-store me-2"></i>Thông tin chuỗi cửa hàng</h6>

                            <div class="mb-3">
                                <label class="label-bold">Tên cửa hàng:</label>
                                <input type="text" class="form-control" value="Namghjk123">
                            </div>

                            <div>
                                <label class="label-bold">Kho tổng đang công tác:</label>
                                <span class="badge-success ms-2">Hà Nội</span>
                            </div>
                        </div>

                        <!-- Nút thao tác -->
                        <div class="text-end mt-3">
                            <button class="btn-cancel me-2"><i class="fa-solid fa-rotate-left me-1"></i>Hủy bỏ</button>
                            <button class="btn-save"><i class="fa-solid fa-floppy-disk me-1"></i>Lưu thay đổi</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>

