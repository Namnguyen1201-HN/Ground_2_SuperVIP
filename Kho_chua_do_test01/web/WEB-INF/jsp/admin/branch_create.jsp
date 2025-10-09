<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Tạo chi nhánh mới</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="css/admin/branch_create.css">
        
    </head>
    <body>
        <%@ include file="../includes/header.jsp" %>

        <div class="container-fluid mt-3">
            <div class="row">
                <%@ include file="../includes/sidebar-store.jsp" %>

                <div class="col-md-9">
                    <div class="branch-form-container">
                        <h5><i class="fas fa-code-branch"></i> Tạo chi nhánh mới</h5>

                        <div class="form-section">
                            <h6><i class="fa-solid fa-store"></i> Thông tin chi nhánh</h6>

                            <form action="BranchCreate" method="post" class="mt-3">
                                <div class="mb-3">
                                    <label class="form-label">Tên chi nhánh <span class="text-danger">*</span></label>
                                    <input type="text" name="branchName" class="form-control" placeholder="Nhập tên chi nhánh" required>
                                    <small class="text-muted">Tên chi nhánh sẽ hiển thị trên hóa đơn và các tài liệu</small>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">Địa chỉ chi nhánh <span class="text-danger">*</span></label>
                                    <input type="text" name="address" class="form-control" placeholder="Nhập địa chỉ đầy đủ của chi nhánh" required>
                                    <small class="text-muted">Địa chỉ chi tiết bao gồm số nhà, tên đường, phường/xã, quận/huyện, tỉnh/thành phố</small>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">Số điện thoại <span class="text-danger">*</span></label>
                                    <input type="text" name="phone" class="form-control" placeholder="Nhập số điện thoại" required pattern="[0-9]{10,11}">
                                    <small class="text-muted">Số điện thoại gồm 10-11 chữ số</small>
                                </div>

                                <div class="mt-4 d-flex justify-content-end gap-2">
                                    <a href="BranchManagement" class="btn btn-back"><i class="fa-solid fa-arrow-left"></i> Quay lại</a>
                                    <button type="reset" class="btn btn-reset"><i class="fa-solid fa-rotate-left"></i> Đặt lại</button>
                                    <button type="submit" class="btn btn-create"><i class="fa-solid fa-plus"></i> Tạo chi nhánh</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>

