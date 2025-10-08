
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Thông tin gian hàng</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="css/admin/information_account.css" rel="stylesheet">
  
</head>
<body>
    <%@ include file="../includes/header.jsp" %>
  <div class="container-fluid">
    <div class="row">
      <!-- Sidebar -->
      <div class="col-md-3 col-lg-2 sidebar">
        <h5 class="mb-4">Gian hàng</h5>
        <a href="#" class="active">Thông tin gian hàng</a>
        <a href="#">Đổi mật khẩu</a>
        <a href="#">Quản lý chi nhánh</a>
        <a href="#">Quản lý kho tổng</a>
        <a href="#">Gói dịch vụ</a>
        <a href="#">Lịch sử mua hàng</a>
      </div>

      <!-- Main content -->
      <div class="col-md-9 col-lg-10 content">
        <div class="d-flex justify-content-between align-items-center mb-4">
          <div>
            <h4>Thông tin gian hàng</h4>
            <h5>Nam <span class="badge bg-light text-primary border">CHỦ CHUỖI CỬA HÀNG</span></h5>
          </div>
        </div>

        <div class="row g-3">
          <!-- Thông tin cá nhân -->
          <div class="col-md-6">
            <div class="card p-3">
              <div class="section-title">Thông tin cá nhân</div>
              <div class="mb-2 small text-muted">#1</div>
              <div class="mb-3">
                <label class="form-label">Họ và tên:</label>
                <input type="text" class="form-control" value="Nam">
              </div>
              <div class="mb-3">
                <label class="form-label">Giới tính:</label>
                <select class="form-select">
                  <option>Chọn giới tính</option>
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
            <div class="card p-3">
              <div class="section-title">Thông tin liên hệ</div>
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
            <div class="card p-3">
              <div class="section-title">Thông tin doanh nghiệp</div>
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
            <div class="card p-3">
              <div class="section-title">Thông tin chuỗi cửa hàng</div>
              <div class="mb-3">
                <label class="form-label">Tên cửa hàng:</label>
                <input type="text" class="form-control" value="Namhgk123">
              </div>
              <div class="mb-3">
                <label class="form-label">Số chi nhánh:</label>
                <input type="text" class="form-control" value="2" readonly>
              </div>
              <div class="mb-3">
                <label class="form-label">Trạng thái tài khoản:</label>
                <span class="badge bg-success">HOẠT ĐỘNG</span>
              </div>
              <div class="mb-3">
                <label class="form-label">Gói dịch vụ:</label>
                <span class="badge bg-secondary">24 THÁNG</span>
              </div>
              <div class="mb-3">
                <label class="form-label">Ngày hết hạn:</label>
                <input type="text" class="form-control" value="01/10/2027 00:00" readonly>
              </div>
            </div>
          </div>
        </div>

        <div class="mt-4 text-end">
          <button class="btn btn-secondary">Hủy bỏ</button>
          <button class="btn btn-primary">Lưu thay đổi</button>
        </div>
      </div>
    </div>
  </div>
</body>
</html>

