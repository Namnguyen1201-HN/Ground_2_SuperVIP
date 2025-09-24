<%-- 
    Document   : register
    Created on : Sep 19, 2025, 10:35:09 AM
    Author     : Lenovo
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Tạo tài khoản dùng thử miễn phí - WM</title>
    <style>
        body { font-family: Arial; display:flex; }
        .left { width:50%; background:#004080; color:white; display:flex; flex-direction:column; justify-content:center; align-items:center; }
        .right { width:50%; padding:40px; }
        input, select { width:100%; padding:10px; margin:8px 0; }
        button { background:#007bff; color:white; border:none; padding:10px 20px; cursor:pointer; }
    </style>
</head>
<body>
<div class="left">
    <h2>Quản lý dễ dàng<br/>Bán hàng đơn giản</h2>
    <p>Hỗ trợ đăng ký 1800 6162</p>
</div>
<div class="right">
    <h3>Tạo tài khoản dùng thử miễn phí</h3>
    <form action="register" method="post">
        <input type="text" name="fullname" placeholder="Họ tên" required />
        <input type="text" name="phone" placeholder="091 234 56 78" required />
        <select name="country"><option>Việt Nam</option></select>
        <select name="region"><option>Hà Nội</option><option>TP.HCM</option></select>
        <div>
            <span><b>Mã xác thực: </b>8014</span>
            <input type="hidden" name="captcha" value="8014"/>
            <input type="text" name="captchaInput" placeholder="Nhập mã xác thực" required />
        </div>
        <label><input type="checkbox" checked/> Tôi đồng ý Điều khoản sử dụng của WM</label>
        <button type="submit">Tiếp tục</button>
    </form>
    <p style="color:red"><%= request.getAttribute("error") == null ? "" : request.getAttribute("error") %></p>
</div>
</body>
</html>
