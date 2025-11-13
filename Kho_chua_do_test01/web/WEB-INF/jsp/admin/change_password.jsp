<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đổi mật khẩu</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link href="css/admin/change_password.css" rel="stylesheet">
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
                <div class="mb-4">
                    <h2 class="page-title">
                        <i class="fas fa-lock me-2"></i>Đổi mật khẩu
                    </h2>
                </div>

                <!-- Security Information Box -->
                <div class="security-info-box">
                    <div class="security-icon">
                        <i class="fas fa-shield-alt"></i>
                    </div>
                    <div class="security-content">
                        <h5 class="security-title">Bảo mật tài khoản</h5>
                        <p class="security-text">
                            Để đảm bảo an toàn cho tài khoản, vui lòng chọn mật khẩu mạnh và không chia sẻ với bất kỳ ai. 
                            Mật khẩu mới phải khác với mật khẩu hiện tại.
                        </p>
                    </div>
                </div>

                <!-- Messages -->
                <% String msg = (String) request.getAttribute("message");
                   String type = (String) request.getAttribute("msgType");
                   if (msg != null) { %>
                <div class="alert alert-<%= type != null ? type : "info" %> alert-dismissible fade show" role="alert">
                    <i class="fas fa-<%= "success".equals(type) ? "check-circle" : "danger".equals(type) ? "exclamation-circle" : "info-circle" %> me-2"></i>
                    <%= msg %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>

                <!-- Password Change Form -->
                <form action="ChangePassWord" method="post" id="passwordForm" novalidate>
                    <div class="password-card">
                        <div class="row g-3">
                            <div class="col-12">
                                <label class="form-label">
                                    <i class="fas fa-key me-2"></i>Mật khẩu hiện tại <span class="text-danger">*</span>
                                </label>
                                <div class="password-input-wrapper">
                                    <input type="password" name="currentPassword" id="currentPassword" 
                                           class="form-control" 
                                           placeholder="Nhập mật khẩu hiện tại" 
                                           required 
                                           minlength="6"
                                           autocomplete="current-password">
                                    <button type="button" class="password-toggle" onclick="togglePassword('currentPassword')">
                                        <i class="fas fa-eye" id="currentPasswordIcon"></i>
                                    </button>
                                </div>
                                <div class="invalid-feedback">Vui lòng nhập mật khẩu hiện tại.</div>
                            </div>

                            <div class="col-12">
                                <label class="form-label">
                                    <i class="fas fa-lock me-2"></i>Mật khẩu mới <span class="text-danger">*</span>
                                </label>
                                <div class="password-input-wrapper">
                                    <input type="password" name="newPassword" id="newPassword" 
                                           class="form-control" 
                                           placeholder="Nhập mật khẩu mới" 
                                           required 
                                           minlength="6"
                                           pattern=".{6,}"
                                           title="Mật khẩu phải có ít nhất 6 ký tự"
                                           autocomplete="new-password">
                                    <button type="button" class="password-toggle" onclick="togglePassword('newPassword')">
                                        <i class="fas fa-eye" id="newPasswordIcon"></i>
                                    </button>
                                </div>
                                <div class="invalid-feedback">Mật khẩu mới phải có ít nhất 6 ký tự.</div>
                                <div class="password-strength" id="passwordStrength"></div>
                            </div>

                            <div class="col-12">
                                <label class="form-label">
                                    <i class="fas fa-lock me-2"></i>Xác nhận mật khẩu mới <span class="text-danger">*</span>
                                </label>
                                <div class="password-input-wrapper">
                                    <input type="password" name="confirmPassword" id="confirmPassword" 
                                           class="form-control" 
                                           placeholder="Nhập lại mật khẩu mới" 
                                           required 
                                           minlength="6"
                                           autocomplete="new-password">
                                    <button type="button" class="password-toggle" onclick="togglePassword('confirmPassword')">
                                        <i class="fas fa-eye" id="confirmPasswordIcon"></i>
                                    </button>
                                </div>
                                <div class="invalid-feedback">Mật khẩu xác nhận không khớp.</div>
                                <div class="password-match" id="passwordMatch"></div>
                            </div>
                        </div>
                    </div>

                    <!-- Action Buttons -->
                    <div class="action-buttons">
                        <button type="reset" class="btn btn-secondary" onclick="resetForm()">
                            <i class="fas fa-undo me-2"></i>Hủy bỏ
                        </button>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-lock me-2"></i>Đổi mật khẩu
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Toggle password visibility
        function togglePassword(inputId) {
            const input = document.getElementById(inputId);
            const icon = document.getElementById(inputId + 'Icon');
            
            if (input.type === 'password') {
                input.type = 'text';
                icon.classList.remove('fa-eye');
                icon.classList.add('fa-eye-slash');
            } else {
                input.type = 'password';
                icon.classList.remove('fa-eye-slash');
                icon.classList.add('fa-eye');
            }
        }

        // Reset form
        function resetForm() {
            document.getElementById('passwordForm').reset();
            document.getElementById('passwordForm').classList.remove('was-validated');
            document.getElementById('passwordStrength').innerHTML = '';
            document.getElementById('passwordMatch').innerHTML = '';
        }

        // Form validation
        (function() {
            'use strict';
            const form = document.getElementById('passwordForm');
            const newPassword = document.getElementById('newPassword');
            const confirmPassword = document.getElementById('confirmPassword');
            const currentPassword = document.getElementById('currentPassword');

            // Real-time password strength check
            newPassword.addEventListener('input', function() {
                const password = this.value;
                const strengthDiv = document.getElementById('passwordStrength');
                
                if (password.length === 0) {
                    strengthDiv.innerHTML = '';
                    return;
                }
                
                let strength = 0;
                let feedback = [];
                
                if (password.length >= 6) strength++;
                else feedback.push('Ít nhất 6 ký tự');
                
                if (password.length >= 8) strength++;
                if (/[a-z]/.test(password)) strength++;
                if (/[A-Z]/.test(password)) strength++;
                if (/[0-9]/.test(password)) strength++;
                if (/[^a-zA-Z0-9]/.test(password)) strength++;
                
                let strengthText = '';
                let strengthClass = '';
                
                if (strength <= 2) {
                    strengthText = 'Yếu';
                    strengthClass = 'strength-weak';
                } else if (strength <= 4) {
                    strengthText = 'Trung bình';
                    strengthClass = 'strength-medium';
                } else {
                    strengthText = 'Mạnh';
                    strengthClass = 'strength-strong';
                }
                
                strengthDiv.innerHTML = '<span class="' + strengthClass + '">Độ mạnh: ' + strengthText + '</span>';
            });

            // Real-time password match check
            function checkPasswordMatch() {
                const matchDiv = document.getElementById('passwordMatch');
                const newPwd = newPassword.value;
                const confirmPwd = confirmPassword.value;
                
                if (confirmPwd.length === 0) {
                    matchDiv.innerHTML = '';
                    return;
                }
                
                if (newPwd === confirmPwd) {
                    matchDiv.innerHTML = '<span class="match-success"><i class="fas fa-check-circle me-1"></i>Mật khẩu khớp</span>';
                    confirmPassword.setCustomValidity('');
                } else {
                    matchDiv.innerHTML = '<span class="match-error"><i class="fas fa-times-circle me-1"></i>Mật khẩu không khớp</span>';
                    confirmPassword.setCustomValidity('Mật khẩu xác nhận không khớp');
                }
            }

            newPassword.addEventListener('input', checkPasswordMatch);
            confirmPassword.addEventListener('input', checkPasswordMatch);

            // Check if new password is different from current
            newPassword.addEventListener('input', function() {
                const newPwd = this.value;
                const currentPwd = currentPassword.value;
                
                if (newPwd.length > 0 && newPwd === currentPwd) {
                    this.setCustomValidity('Mật khẩu mới phải khác mật khẩu hiện tại');
                } else {
                    this.setCustomValidity('');
                }
            });

            currentPassword.addEventListener('input', function() {
                const currentPwd = this.value;
                const newPwd = newPassword.value;
                
                if (newPwd.length > 0 && newPwd === currentPwd) {
                    newPassword.setCustomValidity('Mật khẩu mới phải khác mật khẩu hiện tại');
                } else {
                    newPassword.setCustomValidity('');
                }
            });

            form.addEventListener('submit', function(event) {
                // Final validation
                const newPwd = newPassword.value;
                const currentPwd = currentPassword.value;
                
                if (newPwd === currentPwd) {
                    event.preventDefault();
                    event.stopPropagation();
                    alert('Mật khẩu mới phải khác mật khẩu hiện tại!');
                    newPassword.focus();
                    return false;
                }
                
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            }, false);
        })();
    </script>
</body>
</html>
