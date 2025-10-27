<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Đổi mật khẩu</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <link href="css/warehouse/ChangePassWord.css" rel="stylesheet" type="text/css"/>

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
                            <i class="fa-solid fa-lock me-2 text-primary"></i>Đổi mật khẩu
                        </div>

                        <% String msg = (String) request.getAttribute("message");
   String type = (String) request.getAttribute("msgType");
   if (msg != null && type != null) { %>
                        <div class="alert alert-<%= type %> alert-dismissible fade show" role="alert">
                            <%= msg %>
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                        <% } %>

                        <div class="alert alert-info">
                            <i class="fa-solid fa-shield-halved me-2"></i>
                            <strong>Bảo mật tài khoản:</strong>
                            Để đảm bảo an toàn cho tài khoản, vui lòng chọn mật khẩu mạnh và không chia sẻ với bất kỳ ai.
                            Mật khẩu mới phải khác với mật khẩu hiện tại.
                        </div>

                        <form action="ChangePassWordWareHouse" method="post">
                            <div class="mb-3">
                                <label class="label-bold">Mật khẩu hiện tại <span class="required">*</span></label>
                                <input type="password" class="form-control" name="currentPassword" placeholder="Nhập mật khẩu hiện tại...">
                            </div>

                            <div class="mb-3">
                                <label class="label-bold">Mật khẩu mới <span class="required">*</span></label>
                                <input type="password" class="form-control" name="newPassword" placeholder="Nhập mật khẩu mới...">
                            </div>

                            <div class="mb-4">
                                <label class="label-bold">Xác nhận mật khẩu mới <span class="required">*</span></label>
                                <input type="password" class="form-control" name="confirmPassword" placeholder="Xác nhận mật khẩu mới...">
                            </div>

                            <div class="text-end">
                                <button type="reset" class="btn-cancel me-2">
                                    <i class="fa-solid fa-rotate-left me-1"></i> Hủy bỏ
                                </button>
                                <button type="submit" class="btn-save">
                                    <i class="fa-solid fa-key me-1"></i> Đổi mật khẩu
                                </button>
                            </div>
                        </form>

                    </div>
                </div>
            </div>
        </div>
    </body>
</html>

