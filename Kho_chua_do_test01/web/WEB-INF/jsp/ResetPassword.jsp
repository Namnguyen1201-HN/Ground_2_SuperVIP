<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Đặt lại mật khẩu</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css"/>
        <style>
            .alert {
                padding: 12px 16px;
                margin-bottom: 20px;
                border-radius: 4px;
                font-size: 14px;
            }
            .alert-success {
                background-color: #d4edda;
                border: 1px solid #c3e6cb;
                color: #155724;
            }
            .alert-error {
                background-color: #f8d7da;
                border: 1px solid #f5c6cb;
                color: #dc3545;
            }
            .form-group {
                margin-bottom: 20px;
            }
            .form-group label {
                display: block;
                margin-bottom: 5px;
                font-weight: 500;
                color: #333;
            }
            .form-group input {
                width: 100%;
                padding: 12px;
                border: 1px solid #ddd;
                border-radius: 4px;
                font-size: 14px;
                transition: border-color 0.3s;
            }
            .form-group input:focus {
                outline: none;
                border-color: #007bff;
                box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
            }
            .auth-button {
                width: 100%;
                padding: 12px;
                background-color: #007bff;
                color: white;
                border: none;
                border-radius: 4px;
                font-size: 16px;
                cursor: pointer;
                transition: background-color 0.3s;
            }
            .auth-button:hover { background-color: #0056b3; }
            .auth-button:disabled { background-color: #6c757d; cursor: not-allowed; }

            .password-strength { margin-top: 5px; font-size: 12px; }
            .strength-weak { color: #dc3545; }
            .strength-medium { color: #ffc107; }
            .strength-strong { color: #28a745; }
            .password-requirements { margin-top: 10px; font-size: 12px; color: #6c757d; }
            .requirement { margin: 2px 0; }
            .requirement.met { color: #28a745; }
            .requirement.unmet { color: #dc3545; }
        </style>
    </head>
    <body>
        <div class="auth-container">
            <div class="auth-box">
                <div class="auth-header">
                    <h1>Đặt lại mật khẩu</h1>
                    <p>Nhập mật khẩu mới của bạn</p>
                </div>
                
                <% if (request.getAttribute("errorMessage") != null) { %>
                    <div class="alert alert-error">
                        <%= request.getAttribute("errorMessage") %>
                    </div>
                <% } %>
                
                <% if (request.getAttribute("successMessage") != null) { %>
                    <div class="alert alert-success">
                        <%= request.getAttribute("successMessage") %>
                    </div>
                <% } %>
                
                <form action="${pageContext.request.contextPath}/ForgotPassword" method="POST" class="auth-form" id="resetForm">
                    <input type="hidden" name="action" value="resetPassword">
                    <input type="hidden" name="token" value="${token}">
                    
                    <div class="form-group">
                        <label for="newPassword">Mật khẩu mới</label>
                        <input type="password" id="newPassword" name="newPassword" required 
                               placeholder="Nhập mật khẩu mới" minlength="6"/>
                        <div class="password-strength" id="passwordStrength"></div>
                        <div class="password-requirements" id="passwordRequirements">
                            <div class="requirement" id="req-length">Ít nhất 6 ký tự</div>
                            <div class="requirement" id="req-uppercase">Có ít nhất 1 chữ hoa</div>
                            <div class="requirement" id="req-lowercase">Có ít nhất 1 chữ thường</div>
                            <div class="requirement" id="req-number">Có ít nhất 1 số</div>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label for="confirmPassword">Xác nhận mật khẩu</label>
                        <input type="password" id="confirmPassword" name="confirmPassword" required 
                               placeholder="Nhập lại mật khẩu mới" minlength="6"/>
                        <div class="password-match" id="passwordMatch"></div>
                    </div>
                    
                    <button type="submit" class="auth-button" id="submitBtn" disabled>Đặt lại mật khẩu</button>
                    
                    <div class="auth-links">
                        <a href="${pageContext.request.contextPath}/Login">Quay lại đăng nhập</a>
                        <span>|</span>
                        <a href="${pageContext.request.contextPath}/DashBoard">Về trang chủ</a>
                    </div>
                </form>
            </div>
        </div>

        <script>
            document.addEventListener('DOMContentLoaded', function() {
                const newPassword = document.getElementById('newPassword');
                const confirmPassword = document.getElementById('confirmPassword');
                const submitBtn = document.getElementById('submitBtn');
                const passwordStrength = document.getElementById('passwordStrength');
                const passwordMatch = document.getElementById('passwordMatch');
                
                function checkPasswordStrength(password) {
                    let strength = 0;
                    let requirements = {
                        length: password.length >= 6,
                        uppercase: /[A-Z]/.test(password),
                        lowercase: /[a-z]/.test(password),
                        number: /\d/.test(password)
                    };
                    
                    document.getElementById('req-length').className = 'requirement ' + (requirements.length ? 'met' : 'unmet');
                    document.getElementById('req-uppercase').className = 'requirement ' + (requirements.uppercase ? 'met' : 'unmet');
                    document.getElementById('req-lowercase').className = 'requirement ' + (requirements.lowercase ? 'met' : 'unmet');
                    document.getElementById('req-number').className = 'requirement ' + (requirements.number ? 'met' : 'unmet');
                    
                    if (requirements.length) strength++;
                    if (requirements.uppercase) strength++;
                    if (requirements.lowercase) strength++;
                    if (requirements.number) strength++;
                    
                    if (strength < 2) {
                        passwordStrength.textContent = 'Mật khẩu yếu';
                        passwordStrength.className = 'password-strength strength-weak';
                    } else if (strength < 4) {
                        passwordStrength.textContent = 'Mật khẩu trung bình';
                        passwordStrength.className = 'password-strength strength-medium';
                    } else {
                        passwordStrength.textContent = 'Mật khẩu mạnh';
                        passwordStrength.className = 'password-strength strength-strong';
                    }
                    
                    return strength >= 2;
                }
                
                function checkPasswordMatch() {
                    if (confirmPassword.value && newPassword.value) {
                        if (newPassword.value === confirmPassword.value) {
                            passwordMatch.textContent = 'Mật khẩu khớp';
                            passwordMatch.className = 'password-match';
                            passwordMatch.style.color = '#28a745';
                            return true;
                        } else {
                            passwordMatch.textContent = 'Mật khẩu không khớp';
                            passwordMatch.className = 'password-match';
                            passwordMatch.style.color = '#dc3545';
                            return false;
                        }
                    }
                    return false;
                }
                
                function updateSubmitButton() {
                    const isPasswordValid = checkPasswordStrength(newPassword.value);
                    const isPasswordMatch = checkPasswordMatch();
                    const isFormValid = isPasswordValid && isPasswordMatch && newPassword.value.length >= 6;
                    
                    submitBtn.disabled = !isFormValid;
                }
                
                newPassword.addEventListener('input', updateSubmitButton);
                confirmPassword.addEventListener('input', updateSubmitButton);
                
                document.getElementById('resetForm').addEventListener('submit', function(e) {
                    if (!checkPasswordStrength(newPassword.value) || !checkPasswordMatch()) {
                        e.preventDefault();
                        alert('Vui lòng kiểm tra lại mật khẩu của bạn');
                    }
                });
            });
        </script>
    </body>
</html>
