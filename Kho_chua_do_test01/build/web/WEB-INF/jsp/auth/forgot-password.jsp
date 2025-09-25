<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quên mật khẩu</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css"/>
    </head>
    <body>
        <div class="auth-container">
            <div class="auth-box">
                <div class="auth-header">
                    <h1>Quên mật khẩu</h1>
                    <p>Nhập email của bạn để khôi phục mật khẩu</p>
                </div>
                
                <form action="${pageContext.request.contextPath}/forgot-password" method="POST" class="auth-form">
                    <div class="form-group">
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email" required 
                               placeholder="Nhập địa chỉ email"/>
                    </div>
                    
                    <button type="submit" class="auth-button">Gửi yêu cầu</button>
                    
                    <div class="auth-links">
                        <a href="${pageContext.request.contextPath}/login">Quay lại đăng nhập</a>
                        <span>|</span>
                        <a class="btn btn-outline" href="${pageContext.request.contextPath}/DashBoard_controller">Về trang chủ</a>
                    </div>
                </form>
            </div>
        </div>
    </body>
</html>