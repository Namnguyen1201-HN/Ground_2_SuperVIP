<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.User" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    User user = (User) request.getAttribute("user");
    String storeName = (String) request.getAttribute("storeName");
    String branchName = (String) request.getAttribute("branchName");
    if (storeName == null) storeName = "";
    if (branchName == null) branchName = "—";

    String dobValue = "";
    if (user != null && user.getDob() != null) {
        dobValue = new SimpleDateFormat("yyyy-MM-dd").format(user.getDob());
    }
    
    String roleName = "";
    if (user != null) {
        if (user.getRoleId() == 0) {
            roleName = "QUẢN LÝ CỬA HÀNG";
        } else if (user.getRoleId() == 1) {
            roleName = "QUẢN LÝ CHI NHÁNH";
        } else {
            roleName = "NHÂN VIÊN";
        }
    }
%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thông tin cá nhân</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link href="css/admin/information_account.css" rel="stylesheet">
</head>
<body class="bg-light">
    <%@ include file="../admin/header_admin.jsp" %>

    <div class="container-fluid py-4">
        <div class="row">
            <!-- Sidebar -->
            <%@ include file="../admin/sidebar-store-admin.jsp" %>

            <!-- Main Content -->
            <div class="col-md-9" style="margin-top: 60px;">
                <!-- Page Header -->
                <div class="page-header-section mb-4">
                    <div class="header-content">
                        <h2 class="page-title">
                            <i class="fas fa-user-circle me-2"></i>Thông tin cá nhân
                        </h2>
                        <% if (user != null) { %>
                        <div class="user-header">
                            <h3 class="user-name"><%= user.getFullName() != null ? user.getFullName() : "" %></h3>
                            <span class="user-role-badge"><%= roleName %></span>
                        </div>
                        <% } %>
                    </div>
                </div>

                <!-- Messages -->
                <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="fas fa-exclamation-circle me-2"></i>
                    <%= request.getAttribute("error") %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>
                <% if (request.getAttribute("success") != null) { %>
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="fas fa-check-circle me-2"></i>
                    <%= request.getAttribute("success") %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>

                <form action="InformationAccount" method="post" id="profileForm" novalidate>
                    <input type="hidden" name="userId" value="<%= user != null ? user.getUserId() : 0 %>">

                    <!-- Thông tin cá nhân -->
                    <div class="info-card">
                        <div class="card-header-custom">
                            <i class="fas fa-user me-2"></i>
                            <span>Thông tin cá nhân</span>
                        </div>
                        <div class="card-body-custom">
                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label class="form-label">Mã người dùng</label>
                                    <input type="text" class="form-control" value="#<%= user != null ? user.getUserId() : "" %>" readonly>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label">Họ và tên <span class="text-danger">*</span></label>
                                    <input type="text" name="fullName" class="form-control" 
                                           value="<%= user != null && user.getFullName() != null ? user.getFullName() : "" %>" 
                                           required maxlength="100" 
                                           pattern="^[\p{L} .'-]{2,100}$"
                                           title="Họ và tên chỉ gồm chữ và khoảng trắng, 2–100 ký tự">
                                    <div class="invalid-feedback">Vui lòng nhập họ và tên hợp lệ (2–100 ký tự).</div>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label">Giới tính <span class="text-danger">*</span></label>
                                    <select name="gender" class="form-select" required>
                                        <option value="">-- Chọn giới tính --</option>
                                        <option value="true" <%= (user != null && user.getGender() != null && user.getGender()) ? "selected" : "" %>>Nam</option>
                                        <option value="false" <%= (user != null && user.getGender() != null && !user.getGender()) ? "selected" : "" %>>Nữ</option>
                                    </select>
                                    <div class="invalid-feedback">Vui lòng chọn giới tính.</div>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label">Ngày sinh <span class="text-danger">*</span></label>
                                    <div class="input-group">
                                        <input type="date" name="dob" class="form-control" 
                                               value="<%= dobValue %>" 
                                               required
                                               id="dobInput"
                                               max="<%= new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()) %>">
                                        <span class="input-group-text"><i class="fas fa-calendar"></i></span>
                                    </div>
                                    <div class="invalid-feedback" id="dobFeedback">Vui lòng chọn ngày sinh hợp lệ (từ 18 tuổi trở lên).</div>
                                    <small class="form-text text-muted">Bạn phải từ 18 tuổi trở lên</small>
                                </div>
                                <div class="col-md-12">
                                    <label class="form-label">CCCD/Hộ chiếu <span class="text-danger">*</span></label>
                                    <input type="text" name="identificationId" class="form-control" 
                                           value="<%= user != null && user.getIdentificationId() != null ? user.getIdentificationId() : "" %>"
                                           required
                                           pattern="^\d{12}$"
                                           title="CCCD phải gồm đúng 12 chữ số"
                                           maxlength="12"
                                           placeholder="Nhập 12 chữ số">
                                    <div class="invalid-feedback">CCCD/Hộ chiếu phải gồm đúng 12 chữ số.</div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Thông tin liên hệ -->
                    <div class="info-card">
                        <div class="card-header-custom">
                            <i class="fas fa-address-book me-2"></i>
                            <span>Thông tin liên hệ</span>
                        </div>
                        <div class="card-body-custom">
                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label class="form-label">Email <span class="text-danger">*</span></label>
                                    <input type="email" name="email" class="form-control" 
                                           value="<%= user != null && user.getEmail() != null ? user.getEmail() : "" %>" 
                                           required pattern="^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
                                           title="Email không hợp lệ">
                                    <div class="invalid-feedback">Vui lòng nhập email hợp lệ.</div>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label">Số điện thoại <span class="text-danger">*</span></label>
                                    <input type="tel" name="phone" class="form-control" 
                                           value="<%= user != null && user.getPhone() != null ? user.getPhone() : "" %>" 
                                           required pattern="^0\d{9}$"
                                           title="Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0"
                                           maxlength="10">
                                    <div class="invalid-feedback">Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0.</div>
                                </div>
                                <div class="col-md-12">
                                    <label class="form-label">Địa chỉ <span class="text-danger">*</span></label>
                                    <input type="text" name="address" class="form-control" 
                                           value="<%= user != null && user.getAddress() != null ? user.getAddress() : "" %>"
                                           required
                                           maxlength="255"
                                           placeholder="Nhập địa chỉ đầy đủ">
                                    <div class="invalid-feedback">Vui lòng nhập địa chỉ.</div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Thông tin chuỗi cửa hàng -->
                    <div class="info-card">
                        <div class="card-header-custom">
                            <i class="fas fa-store me-2"></i>
                            <span>Thông tin chuỗi cửa hàng</span>
                        </div>
                        <div class="card-body-custom">
                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label class="form-label">Tên cửa hàng</label>
                                    <input type="text" class="form-control" value="<%= storeName %>" readonly>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label">Chi nhánh đang công tác</label>
                                    <input type="text" class="form-control" value="<%= branchName %>" readonly>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Action Buttons -->
                    <div class="action-buttons">
                        <button type="reset" class="btn btn-secondary">
                            <i class="fas fa-undo me-2"></i>Hủy bỏ
                        </button>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save me-2"></i>Lưu thay đổi
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Form validation
        (function() {
            'use strict';
            const form = document.getElementById('profileForm');
            const dobInput = document.getElementById('dobInput');
            const dobFeedback = document.getElementById('dobFeedback');
            
            // Validate age >= 18
            function validateAge() {
                if (!dobInput.value) {
                    return false;
                }
                
                const birthDate = new Date(dobInput.value);
                const today = new Date();
                let age = today.getFullYear() - birthDate.getFullYear();
                const monthDiff = today.getMonth() - birthDate.getMonth();
                
                if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
                    age--;
                }
                
                if (age < 18) {
                    dobInput.setCustomValidity('Bạn phải từ 18 tuổi trở lên!');
                    dobFeedback.textContent = 'Bạn phải từ 18 tuổi trở lên!';
                    return false;
                } else if (birthDate > today) {
                    dobInput.setCustomValidity('Ngày sinh không được là ngày trong tương lai!');
                    dobFeedback.textContent = 'Ngày sinh không được là ngày trong tương lai!';
                    return false;
                } else {
                    dobInput.setCustomValidity('');
                    dobFeedback.textContent = 'Vui lòng chọn ngày sinh hợp lệ (từ 18 tuổi trở lên).';
                    return true;
                }
            }
            
            // Validate gender
            const genderSelect = form.querySelector('select[name="gender"]');
            genderSelect.addEventListener('change', function() {
                if (this.value === '') {
                    this.setCustomValidity('Vui lòng chọn giới tính!');
                } else {
                    this.setCustomValidity('');
                }
            });
            
            // Validate DOB on change
            dobInput.addEventListener('change', validateAge);
            dobInput.addEventListener('input', function() {
                if (this.value) {
                    validateAge();
                }
            });
            
            // Form submit validation
            form.addEventListener('submit', function(event) {
                // Validate age before submit
                if (!validateAge()) {
                    event.preventDefault();
                    event.stopPropagation();
                    dobInput.classList.add('is-invalid');
                    form.classList.add('was-validated');
                    return false;
                }
                
                // Validate gender
                if (genderSelect.value === '') {
                    event.preventDefault();
                    event.stopPropagation();
                    genderSelect.setCustomValidity('Vui lòng chọn giới tính!');
                    genderSelect.classList.add('is-invalid');
                    form.classList.add('was-validated');
                    return false;
                }
                
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            }, false);
            
            // Real-time validation for inputs
            const inputs = form.querySelectorAll('input[required], input[pattern], select[required]');
            inputs.forEach(input => {
                input.addEventListener('input', function() {
                    if (this.type === 'date') {
                        validateAge();
                    } else {
                        if (this.validity.valid) {
                            this.classList.remove('is-invalid');
                            this.classList.add('is-valid');
                        } else {
                            this.classList.remove('is-valid');
                            this.classList.add('is-invalid');
                        }
                    }
                });
                
                input.addEventListener('change', function() {
                    if (this.type === 'date') {
                        validateAge();
                    } else if (this.tagName === 'SELECT') {
                        if (this.value === '') {
                            this.setCustomValidity('Vui lòng chọn giới tính!');
                            this.classList.add('is-invalid');
                        } else {
                            this.setCustomValidity('');
                            this.classList.remove('is-invalid');
                            this.classList.add('is-valid');
                        }
                    } else {
                        if (this.validity.valid) {
                            this.classList.remove('is-invalid');
                            this.classList.add('is-valid');
                        } else {
                            this.classList.remove('is-valid');
                            this.classList.add('is-invalid');
                        }
                    }
                });
            });
        })();
    </script>
</body>
</html>
