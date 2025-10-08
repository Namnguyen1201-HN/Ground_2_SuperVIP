<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thông tin gian hàng</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <link href="css/store.css" rel="stylesheet">
    <link href="css/admin/information_account.css" rel="stylesheet">
</head>
<body class="bg-light">
    <%@ include file="../includes/header.jsp" %>

    <div class="container-fluid py-4">
        <div class="row">
            <!-- Sidebar -->
            <%@ include file="../includes/sidebar-store.jsp" %>

            <!-- Main content -->
            <div class="col-md-9 col-lg-9">
                <div class="card shadow-sm p-4">
                    <div class="info-section mb-3">
                        <i class="bi bi-info-circle"></i>
                        Thông tin gian hàng
                    </div>

                    <div class="store-owner mb-4">
                        <h5>Nam <span class="badge">CHỦ CHUỖI CỬA HÀNG</span></h5>
                    </div>

                    <div class="row g-3">
                        <!-- Thông tin cá nhân -->
                        <div class="col-md-6">
                            <div class="card p-3 border-0 shadow-sm">
                                <div class="section-title mb-3">Thông tin cá nhân</div>
                                <div class="mb-3">
                                    <label class="form-label">Họ và tên:</label>
                                    <input type="text" class="form-control" value="Nam">
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Giới tính:</label>
                                    <select class="form-select">
                                        <option>Nam</option>
                                        <option>Nữ</option>
                                    </select>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Ngày sinh:</label>
                                    <input type="date" class="form-control">
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">CCCD/Hộ chiếu:</label>
                                    <input type="text" class="form-control">
                                </div>
                            </div>
                        </div>

                        <!-- Thông tin liên hệ -->
                        <div class="col-md-6">
                            <div class="card p-3 border-0 shadow-sm">
                                <div class="section-title mb-3">Thông tin liên hệ</div>
                                <div class="mb-3">
                                    <label class="form-label">Email:</label>
                                    <input type="email" class="form-control" value="Namhgk123@gmail.com">
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Số điện thoại:</label>
                                    <input type="text" class="form-control" value="0949614588">
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Địa chỉ:</label>
                                    <input type="text" class="form-control">
                                </div>
                            </div>
                        </div>

                        <!-- Thông tin doanh nghiệp -->
                        <div class="col-md-6">
                            <div class="card p-3 border-0 shadow-sm">
                                <div class="section-title mb-3">Thông tin doanh nghiệp</div>
                                <div class="mb-3">
                                    <label class="form-label">Mã số thuế:</label>
                                    <input type="text" class="form-control">
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Website:</label>
                                    <input type="url" class="form-control" placeholder="https://example.com">
                                </div>
                            </div>
                        </div>

                        <!-- Thông tin chuỗi cửa hàng -->
                        <div class="col-md-6">
                            <div class="card p-3 border-0 shadow-sm">
                                <div class="section-title mb-3">Thông tin chuỗi cửa hàng</div>
                                <div class="mb-3">
                                    <label class="form-label">Tên cửa hàng:</label>
                                    <input type="text" class="form-control" value="Namhgk123">
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Số chi nhánh:</label>
                                    <input type="text" class="form-control" value="2" readonly>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="mt-4 text-end">
                        <button class="btn btn-secondary me-2">
                            <i class="bi bi-arrow-counterclockwise"></i> Hủy bỏ
                        </button>
                        <button class="btn btn-primary">
                            <i class="bi bi-save"></i> Lưu thay đổi
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
