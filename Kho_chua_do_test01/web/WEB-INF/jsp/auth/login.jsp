<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Đăng nhập</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css"/>
    </head>
    <body>
        <div class="auth-container">
            <div class="auth-box">
                <div class="auth-header">
                    <h1>Đăng nhập</h1>
                    <p>Chào mừng bạn quay trở lại!</p>
                </div>
                
                <form action="${pageContext.request.contextPath}/jsp.auth/login" method="POST" class="auth-form">
                    <div class="form-group">
                        <label for="username">Tên đăng nhập</label>
                        <input type="text" id="username" name="username" required 
                               placeholder="Nhập tên đăng nhập"/>
                    </div>
                    
                    <div class="form-group">
                        <label for="password">Mật khẩu</label>
                        <input type="password" id="password" name="password" required 
                               placeholder="Nhập mật khẩu"/>
                    </div>
                    
                    <button type="submit" class="auth-button">Đăng nhập</button>
                    
                    <div class="auth-links">
                        <a href="${pageContext.request.contextPath}/jsp/auth/forgot-password.jsp">Quên mật khẩu?</a>
                        <span>|</span>
                        <a href="${pageContext.request.contextPath}/jsp/auth/register.jsp">Đăng ký tài khoản mới</a>
                    </div>
                </form>
            </div>
        </div>
    </body>
</html>