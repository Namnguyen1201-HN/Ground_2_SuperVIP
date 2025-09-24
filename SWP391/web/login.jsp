<%-- 
    Document   : login
    Created on : Sep 19, 2025, 10:36:07 AM
    Author     : Lenovo
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Đăng nhập tài khoản WM</title>
    <style>
        body { font-family: Arial; padding:40px; }
        input { width:250px; padding:10px; }
        button { background:#007bff; color:white; padding:10px 20px; border:none; cursor:pointer; }
    </style>
</head>
<body>
<h3>Đăng nhập tài khoản WM</h3>
<form action="login" method="post">
    <input type="text" name="username" placeholder="Địa chỉ cửa hàng"/>.wm.vn
    <br/><br/>
    <input type="password" name="password" placeholder="Mật khẩu"/>
    <br/><br/>
    <button type="submit">Vào cửa hàng</button>
</form>
<p>Bạn chưa có gian hàng trên WM? <a href="register.jsp">Dùng thử miễn phí</a></p>
<p style="color:red"><%= request.getAttribute("error") == null ? "" : request.getAttribute("error") %></p>
</body>
</html>

