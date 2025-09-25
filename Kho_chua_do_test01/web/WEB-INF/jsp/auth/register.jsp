<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Đăng ký tài khoản</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css"/>
    </head>
    <body>
        <div class="auth-container">
            <div class="auth-box">
                <div class="auth-header">
                    <h1>Đăng ký tài khoản</h1>
                    <p>Tạo tài khoản mới để bắt đầu!</p>
                </div>
                
                <form action="${pageContext.request.contextPath}/register" method="POST" class="auth-form">
                    <div class="form-group">
                        <label for="username">Tên đăng nhập</label>
                        <input type="text" id="username" name="username" required 
                               placeholder="Chọn tên đăng nhập"/>
                    </div>
                    
                    <div class="form-group">
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email" required 
                               placeholder="Nhập địa chỉ email"/>
                    </div>
                    
                    <div class="form-group">
                        <label for="password">Mật khẩu</label>
                        <input type="password" id="password" name="password" required 
                               placeholder="Tạo mật khẩu"/>
                    </div>
                    
                    <div class="form-group">
                        <label for="confirmPassword">Xác nhận mật khẩu</label>
                        <input type="password" id="confirmPassword" name="confirmPassword" required 
                               placeholder="Nhập lại mật khẩu"/>
                    </div>
                    
                    <button type="submit" class="auth-button">Đăng ký</button>
                    
                    <div class="auth-links">
                        <span>Đã có tài khoản?</span>
                        <a href="${pageContext.request.contextPath}/login">Đăng nhập</a>
                        <span>|</span>
                        <a class="btn btn-outline" href="${pageContext.request.contextPath}/DashBoard_controller">Về trang chủ</a>
                    </div>
                </form>
            </div>
        </div>
    </body>
</html>
