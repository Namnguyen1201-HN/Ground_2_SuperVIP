<%-- 
    Document   : Login
    Created on : Sep 20, 2025, 9:09:52 AM
    Author     : Lenovo
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập tài khoản WM</title>
    <link href="css/Login.css" rel="stylesheet" type="text/css"/>
</head>
<body>
    <div class="login-container">
        <div class="login-header">
            <div class="close-btn">&times;</div>
        </div>
        
        <div class="login-content">
            <div class="login-form">
                <h2>Đăng nhập tài khoản WM</h2>
                
                <form action="Login" method="post">
                    <% if (request.getAttribute("errorMessage") != null) { %>
                        <div class="error-message">
                            <%= request.getAttribute("errorMessage") %>
                        </div>
                    <% } %>
                    
                    <% if (request.getAttribute("successMessage") != null) { %>
                        <div class="success-message">
                            <%= request.getAttribute("successMessage") %>
                        </div>
                    <% } %>
                    
                    <div class="input-group">
                        <input type="text" name="username" placeholder="Địa chỉ truy cập của hàng" required>
                        <span class="domain-suffix">wm.vn</span>
                    </div>
                    
                    <div class="input-group">
                        <input type="password" name="password" placeholder="Mật khẩu" required>
                    </div>
                    
                    <div class="form-options">
                        <label class="remember-checkbox">
                            <input type="checkbox" name="remember">
                            <span class="checkmark"></span>
                            Ghi nhớ đăng nhập
                        </label>
                        <a href="#" class="forgot-password">Quên mật khẩu?</a>
                    </div>
                    
                    <button type="submit" class="login-btn">Đăng nhập</button>
                </form>
                
                <div class="signup-section">
                    <p>Bạn chưa có gian hàng trên WM? <a href="Register" class="signup-link">Dùng thử miễn phí</a></p>
                    <button class="store-btn">Vào cửa hàng</button>
                </div>
            </div>
        </div>
    </div>

    <script>
        // Close button functionality
        document.querySelector('.close-btn').addEventListener('click', function() {
            window.history.back();
        });

        // Form validation
        document.querySelector('form').addEventListener('submit', function(e) {
            const username = document.querySelector('input[name="username"]').value;
            const password = document.querySelector('input[name="password"]').value;
            
            if (!username || !password) {
                e.preventDefault();
                alert('Vui lòng nhập đầy đủ thông tin!');
            }
        });
    </script>
</body>
</html>
