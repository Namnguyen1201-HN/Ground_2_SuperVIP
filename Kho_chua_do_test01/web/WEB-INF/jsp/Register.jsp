<%-- 
    Document   : Register
    Created on : Sep 20, 2025, 9:10:34 AM
    Author     : Lenovo
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tạo tài khoản dùng thử miễn phí - WM</title>
    <link href="css/Register.css" rel="stylesheet" type="text/css"/>
</head>
<body>
    <div class="register-container">
        <div class="register-left">
            <div class="hero-content">
                <h1>Quản lý dễ dàng<br>Bán hàng đơn giản</h1>
                <div class="support-info">
                    <span class="support-icon">📞</span>
                    <span>Hỗ trợ đăng ký 1800 6162</span>
                </div>
            </div>
        </div>
        
        <div class="register-right">
            <div class="register-header">
                <div class="close-btn">&times;</div>
            </div>
            
            <div class="register-form">
                <h2>Tạo tài khoản dùng thử miễn phí</h2>
                
                <form action="Register" method="post">
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
                        <input type="text" name="fullName" placeholder="Họ tên" required>
                    </div>
                    
                    <div class="input-group phone-group">
                        <div class="country-code">
                            <select name="countryCode">
                                <option value="+84">🇻🇳</option>
                                <option value="+1">🇺🇸</option>
                                <option value="+86">🇨🇳</option>
                            </select>
                        </div>
                        <input type="tel" name="phone" placeholder="091 234 56 78" required>
                    </div>
                    
                    <div class="input-group">
                        <select name="country" required>
                            <option value="">Quốc gia đăng ký kinh doanh</option>
                            <option value="VN" selected>Việt Nam</option>
                            <option value="US">United States</option>
                            <option value="CN">China</option>
                        </select>
                    </div>
                    
                    <div class="input-group">
                        <select name="region" required>
                            <option value="">Chọn khu vực</option>
                            <option value="hanoi">Hà Nội</option>
                            <option value="hcm">TP. Hồ Chí Minh</option>
                            <option value="danang">Đà Nẵng</option>
                            <option value="other">Khác</option>
                        </select>
                    </div>
                    
                    <div class="captcha-group">
                        <label>Mã xác thực</label>
                        <div class="captcha-container">
                            <div class="captcha-image" id="captcha-display">
                                <%
                                    // Generate random captcha
                                    int captcha = (int)(Math.random() * 9000) + 1000;
                                    session.setAttribute("captcha", String.valueOf(captcha));
                                    out.print(captcha);
                                %>
                            </div>
                            <button type="button" class="refresh-captcha">🔄</button>
                        </div>
                        <input type="text" name="captcha" placeholder="Nhập mã xác thực" required>
                    </div>
                    
                    <div class="terms-checkbox">
                        <label class="checkbox-container">
                            <input type="checkbox" name="acceptTerms" required>
                            <span class="checkmark"></span>
                            Tôi đã đọc và đồng ý <a href="#" target="_blank">Điều khoản và chính sách sử dụng</a> của WM
                        </label>
                    </div>
                    
                    <button type="submit" class="register-btn">Tiếp tục</button>
                </form>
                
                <div class="login-section">
                    <p>Đã có tài khoản? <a href="Login" class="login-link">Đăng nhập</a></p>
                </div>
            </div>
        </div>
    </div>

    <script>
        // Close button functionality
        document.querySelector('.close-btn').addEventListener('click', function() {
            window.history.back();
        });

        // Refresh captcha
        document.querySelector('.refresh-captcha').addEventListener('click', function() {
            fetch('RefreshCaptcha')
                .then(response => response.text())
                .then(captcha => {
                    document.getElementById('captcha-display').textContent = captcha;
                })
                .catch(error => {
                    const randomCode = Math.floor(Math.random() * 9000) + 1000;
                    document.getElementById('captcha-display').textContent = randomCode;
                });
        });

        // Phone number formatting
        document.querySelector('input[name="phone"]').addEventListener('input', function(e) {
            let value = e.target.value.replace(/\D/g, '');
            let formatted = value.replace(/(\d{3})(\d{3})(\d{2})(\d{2})/, '$1 $2 $3 $4');
            e.target.value = formatted;
        });

        // Form validation
        document.querySelector('form').addEventListener('submit', function(e) {
            const fullName = document.querySelector('input[name="fullName"]').value;
            const phone = document.querySelector('input[name="phone"]').value;
            const country = document.querySelector('select[name="country"]').value;
            const region = document.querySelector('select[name="region"]').value;
            const captcha = document.querySelector('input[name="captcha"]').value;
            const acceptTerms = document.querySelector('input[name="acceptTerms"]').checked;
            
            if (!fullName || !phone || !country || !region || !captcha || !acceptTerms) {
                e.preventDefault();
                alert('Vui lòng điền đầy đủ thông tin và đồng ý điều khoản!');
            }
        });
    </script>
</body>
</html>
