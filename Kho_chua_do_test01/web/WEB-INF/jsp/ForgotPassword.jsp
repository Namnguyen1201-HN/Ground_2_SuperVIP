<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quên mật khẩu</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/Auth.css"/>
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
                color: #721c24;
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
            .auth-button:hover {
                background-color: #0056b3;
            }
            .auth-button:disabled {
                background-color: #6c757d;
                cursor: not-allowed;
            }
            .auth-links {
                text-align: center;
                margin-top: 20px;
            }
            .auth-links a {
                color: #007bff;
                text-decoration: none;
                margin: 0 10px;
            }
            .auth-links a:hover {
                text-decoration: underline;
            }
            .auth-links span {
                color: #6c757d;
            }
        </style>
    </head>
    <body>
        <div class="auth-container">
            <div class="auth-box">
                <div class="auth-header">
                    <h1>Quên mật khẩu</h1>
                    <p>Nhập email của bạn để khôi phục mật khẩu</p>
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
                
                <form action="${pageContext.request.contextPath}/ForgotPassword" method="POST" class="auth-form" id="forgotForm">
                    <div class="form-group">
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email" required 
                               placeholder="Nhập địa chỉ email" value="${param.email}"/>
                    </div>
                    
                    <button type="submit" class="auth-button" id="submitBtn">Gửi yêu cầu</button>
                    
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
                const form = document.getElementById('forgotForm');
                const submitBtn = document.getElementById('submitBtn');
                const emailInput = document.getElementById('email');
                
                // Disable submit button while processing
                form.addEventListener('submit', function() {
                    submitBtn.disabled = true;
                    submitBtn.textContent = 'Đang xử lý...';
                });
                
                // Email validation
                emailInput.addEventListener('input', function() {
                    const email = this.value.trim();
                    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                    
                    if (email && !emailRegex.test(email)) {
                        this.setCustomValidity('Vui lòng nhập địa chỉ email hợp lệ');
                    } else {
                        this.setCustomValidity('');
                    }
                });
                
                // Form validation
                form.addEventListener('submit', function(e) {
                    const email = emailInput.value.trim();
                    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                    
                    if (!email || !emailRegex.test(email)) {
                        e.preventDefault();
                        alert('Vui lòng nhập địa chỉ email hợp lệ');
                        submitBtn.disabled = false;
                        submitBtn.textContent = 'Gửi yêu cầu';
                    }
                });
            });
        </script>
    </body>
</html>
