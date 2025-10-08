<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Đổi mật khẩu</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">       
        <link href="css/admin/change_password.css" rel="stylesheet">
    </head>
    <body class="bg-light">
        <%@ include file="../includes/header.jsp" %>

        <div class="container-fluid py-4">
            <div class="row">
                <!-- Sidebar -->
                <%@ include file="../includes/sidebar-store.jsp" %>

                <!-- Main Content -->
                <div class="col-md-9">
                    <div class="card shadow-sm">
                        <div class="card-body">
                            <h5 class="mb-4">
                                <i class="bi bi-lock-fill text-primary me-2"></i> Đổi mật khẩu
                            </h5>

                            <div class="alert alert-primary d-flex align-items-start" role="alert">
                                <i class="bi bi-shield-check-fill me-2 fs-4"></i>
                                <div>
                                    <strong>Bảo mật tài khoản</strong><br>
                                    Để đảm bảo an toàn cho tài khoản, vui lòng chọn mật khẩu mạnh và không chia sẻ với bất kỳ ai. 
                                    Mật khẩu mới phải khác với mật khẩu hiện tại.
                                </div>
                            </div>

                            <form>
                                <div class="mb-3">
                                    <label class="form-label">Mật khẩu hiện tại <span class="text-danger">*</span></label>
                                    <input type="password" class="form-control" placeholder="Nhập mật khẩu hiện tại">
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Mật khẩu mới <span class="text-danger">*</span></label>
                                    <input type="password" class="form-control" placeholder="Nhập mật khẩu mới">
                                </div>
                                <div class="mb-4">
                                    <label class="form-label">Xác nhận mật khẩu mới <span class="text-danger">*</span></label>
                                    <input type="password" class="form-control" placeholder="Nhập lại mật khẩu mới">
                                </div>
                                <div class="text-end">
                                    <button type="button" class="btn btn-secondary me-2">
                                        <i class="bi bi-arrow-counterclockwise"></i> Hủy bỏ
                                    </button>
                                    <button type="submit" class="btn btn-primary">
                                        <i class="bi bi-key-fill"></i> Đổi mật khẩu
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </body>
</html>