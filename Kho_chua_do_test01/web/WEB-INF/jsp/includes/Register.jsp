<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WM - Đăng ký tài khoản cộng tác viên</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="css/includes/Register.css"/>
</head>
<body>
    <!-- Left Column - Welcome Section -->
    <div class="welcome-section slide-in-left">
        <div class="welcome-content">
            <h1 class="welcome-title">Chào mừng đến với WM!</h1>
            <p class="welcome-description">
                Đăng ký để truy cập vào hệ thống quản lý bán hàng thông minh WM.
            </p>
            <div class="features">
                <div class="feature">
                    <div class="feature-icon">
                        <i class="fas fa-chart-line"></i>
                    </div>
                    <div class="feature-content">
                        <h3>Báo cáo thời gian thực</h3>
                        <p>Theo dõi doanh thu và hiệu suất kinh doanh mỗi lúc mỗi nơi với dashboard trực quan</p>
                    </div>
                </div>
                <div class="feature">
                    <div class="feature-icon">
                        <i class="fas fa-mobile-alt"></i>
                    </div>
                    <div class="feature-content">
                        <h3>Truy cập đa nền tảng</h3>
                        <p>Sử dụng WM trên máy tính, tablet và điện thoại di động một cách liên mạch</p>
                    </div>
                </div>
                <div class="feature">
                    <div class="feature-icon">
                        <i class="fas fa-shield-alt"></i>
                    </div>
                    <div class="feature-content">
                        <h3>Bảo mật tuyệt đối</h3>
                        <p>Dữ liệu của bạn được bảo vệ bằng mã hóa cao cấp</p>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Right Column - Registration Form -->
    <div class="form-section">
        <div class="form-container fade-in">
            <!-- Header -->
            <div class="form-header">
                <div class="logo">
                    <div class="logo-icon">T</div>
                    <span class="logo-text">WM</span>
                </div>
                <h2 class="form-title">Đăng ký tài khoản cộng tác viên</h2>
                <p class="form-subtitle">Nhập thông tin để tạo tài khoản mới</p>
            </div>

            <!-- Success Message -->
            <% if (request.getAttribute("success") != null) { %>
            <div class="success-message" id="successMsg">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none" viewBox="0 0 24 24">
                    <path d="M9 16.17L4.83 12m0 0L3.41 13.41M4.83 12l5.17-5.17M9 16.17l7.07-7.07" stroke="#4CAF50" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"></path>
                </svg>
                <span><%= request.getAttribute("success") %></span>
            </div>
            <% } %>

            <!-- Error Message -->
            <% if (request.getAttribute("error") != null) { %>
            <div class="error-message" id="errorMsg">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none" viewBox="0 0 24 24">
                    <path d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 17.5228 6.47715 22 12 22Z" fill="#D00E17" stroke="#D00E17" stroke-width="1.5"></path>
                    <path d="M12 7v5M12 16h.01" stroke="#fff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"></path>
                </svg>
                <span><%= request.getAttribute("error") %></span>
            </div>
            <% } %>

            <!-- Registration Form -->
            <form class="form" action="Register" method="post" id="registerForm">
                <div class="form-group">
                    <label for="fullName" class="form-label">Họ và tên *</label>
                    <input type="text" id="fullName" name="fullName" class="form-input" 
                           placeholder="Nhập họ và tên đầy đủ" required maxlength="100">
                </div>

                <div class="form-group">
                    <label for="identificationId" class="form-label">Căn cước công dân *</label>
                    <input type="text" id="identificationId" name="identificationId" class="form-input" 
                           placeholder="Nhập căn cước công dân (9 hoặc 12 chữ số)" required maxlength="12">
                    <small style="color: #999; font-size: 0.85rem;">Ví dụ: 123456789 hoặc 123456789012</small>
                </div>

                <div class="form-group">
                    <label for="phone" class="form-label">Số điện thoại *</label>
                    <input type="tel" id="phone" name="phone" class="form-input" 
                           placeholder="Nhập số điện thoại (10-11 chữ số)" required maxlength="11">
                </div>

                <div class="form-group">
                    <label for="email" class="form-label">Email *</label>
                    <input type="email" id="email" name="email" class="form-input" 
                           placeholder="Nhập địa chỉ email" required maxlength="100">
                </div>

                <div class="form-group">
                    <label for="password" class="form-label">Mật khẩu *</label>
                    <div class="input-wrapper"> 
                        <input type="password" id="password" name="password" class="form-input" 
                               placeholder="Nhập mật khẩu (tối thiểu 6 ký tự)" required minlength="6">
                        <button type="button" class="toggle-password" onclick="togglePassword()">
                            <i class="fas fa-eye"></i>
                        </button>
                    </div>
                </div>

                <div class="form-group">
                    <label for="confirmPassword" class="form-label">Xác nhận mật khẩu *</label>
                    <div class="input-wrapper">
                        <input type="password" id="confirmPassword" name="confirmPassword" class="form-input" 
                               placeholder="Nhập lại mật khẩu" required minlength="6">
                        <button type="button" class="toggle-password" onclick="toggleConfirmPassword()">
                            <i class="fas fa-eye"></i>
                        </button>
                    </div>
                </div>

                <div class="checkbox-group">
                    <input type="checkbox" id="terms" name="terms" class="checkbox" required>
                    <label for="terms" class="checkbox-label">
                        Tôi đồng ý với 
                        <a href="#" onclick="return false;">Điều khoản dịch vụ</a> 
                        và 
                        <a href="#" onclick="return false;">Chính sách bảo mật</a> 
                        của WM
                    </label>
                </div>

                <button type="submit" class="submit-button" id="submitBtn">
                    Đăng ký
                </button>
            </form>

            <!-- Login Link -->
            <div class="login-link">
                <p>Đã có tài khoản? <a href="Login">Đăng nhập ngay</a></p>
            </div>
        </div>
    </div>

    <script>
        function togglePassword() {
            const passwordInput = document.getElementById('password');
            const toggleIcon = document.querySelector('#password + .toggle-password i');
            
            if (passwordInput.type === 'password') {
                passwordInput.type = 'text';
                toggleIcon.classList.remove('fa-eye');
                toggleIcon.classList.add('fa-eye-slash');
            } else {
                passwordInput.type = 'password';
                toggleIcon.classList.remove('fa-eye-slash');
                toggleIcon.classList.add('fa-eye');
            }
        }

        function toggleConfirmPassword() {
            const confirmPasswordInput = document.getElementById('confirmPassword');
            const toggleIcon = document.querySelector('#confirmPassword + .toggle-password i');
            
            if (confirmPasswordInput.type === 'password') {
                confirmPasswordInput.type = 'text';
                toggleIcon.classList.remove('fa-eye');
                toggleIcon.classList.add('fa-eye-slash');
            } else {
                confirmPasswordInput.type = 'password';
                toggleIcon.classList.remove('fa-eye-slash');
                toggleIcon.classList.add('fa-eye');
            }
        }

        // Check password match in real-time
        document.getElementById('confirmPassword').addEventListener('input', function () {
            const password = document.getElementById('password').value;
            if (this.value && this.value !== password) {
                this.classList.add('error');
                this.closest('.form-group').classList.add('show-error');
                this.closest('.form-group').setAttribute('data-error', 'Mật khẩu xác nhận không khớp');
            } else {
                this.classList.remove('error');
                this.closest('.form-group').classList.remove('show-error');
                this.closest('.form-group').removeAttribute('data-error');
            }
        });

        // Validate phone number
        document.getElementById('phone').addEventListener('input', function () {
            this.value = this.value.replace(/\D/g, '').slice(0, 11);
        });

        // Validate identification ID
        document.getElementById('identificationId').addEventListener('input', function () {
            this.value = this.value.replace(/\D/g, '').slice(0, 12);
        });

        // Form submit handler
        document.getElementById('registerForm').addEventListener('submit', function(e) {
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            
            if (password !== confirmPassword) {
                e.preventDefault();
                alert('Mật khẩu xác nhận không khớp!');
                return false;
            }
            
            const submitBtn = document.getElementById('submitBtn');
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<span class="loading"></span> Đang đăng ký...';
        });

        // Animation on page load
        document.addEventListener('DOMContentLoaded', function () {
            const formElements = document.querySelectorAll('.form-group, .checkbox-group, .submit-button, .login-link');
            formElements.forEach((element, index) => {
                element.style.opacity = '0';
                element.style.transform = 'translateY(20px)';
                element.style.transition = 'all 0.3s ease';
                setTimeout(() => {
                    element.style.opacity = '1';
                    element.style.transform = 'translateY(0)';
                }, 100 + index * 100);
            });

            // Auto redirect if success
            const successMsg = document.getElementById('successMsg');
            if (successMsg) {
                setTimeout(() => {
                    window.location.href = 'Login';
                }, 2000);
            }
        });
    </script>

    <style>
        .success-message {
            display: flex;
            color: #4CAF50;
            font-size: 14px;
            justify-content: center;
            gap: 8px;
            align-items: center;
            padding: 12px;
            background: #f1f8e9;
            border-radius: 8px;
            margin-bottom: 16px;
            animation: slideDown 0.3s ease;
        }

        .error-message {
            display: flex;
            color: #D00E17;
            font-size: 14px;
            justify-content: center;
            gap: 8px;
            align-items: center;
            padding: 12px;
            background: #ffebee;
            border-radius: 8px;
            margin-bottom: 16px;
            animation: slideDown 0.3s ease;
        }

        @keyframes slideDown {
            from {
                opacity: 0;
                transform: translateY(-10px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .loading {
            display: inline-block;
            width: 16px;
            height: 16px;
            border: 2px solid rgba(255, 255, 255, 0.3);
            border-radius: 50%;
            border-top-color: #fff;
            animation: spin 1s ease-in-out infinite;
            margin-right: 8px;
        }

        @keyframes spin {
            to {
                transform: rotate(360deg);
            }
        }

        .submit-button:disabled {
            opacity: 0.7;
            cursor: not-allowed;
        }

        .form-group small {
            display: block;
            margin-top: 4px;
        }

        .form-group.error .form-input {
            border-color: #D00E17;
        }

        .form-group.show-error::after {
            content: attr(data-error);
            color: #D00E17;
            font-size: 0.8rem;
            margin-top: 4px;
            display: block;
        }
    </style>
</body>
</html>
