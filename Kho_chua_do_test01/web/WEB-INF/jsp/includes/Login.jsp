<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập - WM</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="css/includes/Login.css"/>
</head>
<body>
    <!-- Header -->
    <header class="header" id="header">
        <div class="header-content">
            <a href="#" class="logo">
                <div class="logo-icon">T</div>
                <span class="logo-text">WM</span>
            </a>
        </div>
    </header>

    <!-- Main Content -->
    <main class="main-content">
        <div class="login-container">
            <!-- Left Visual Section -->
            <div class="login-visual slide-in-left">
                <div class="login-visual-content">
                    <h2>Chào mừng trở lại!</h2>
                    <p>Đăng nhập để truy cập vào hệ thống quản lý bán hàng thông minh WM và tối ưu hóa kinh doanh của bạn.</p>
                    <ul class="login-features">
                        <li>
                            <div class="feature-icon">
                                <i class="fas fa-chart-line"></i>
                            </div>
                            <div>
                                <strong>Báo cáo thời gian thực</strong>
                                <p>Theo dõi doanh thu và hiệu suất kinh doanh mỗi lúc mỗi nơi</p>
                            </div>
                        </li>
                        <li>
                            <div class="feature-icon">
                                <i class="fas fa-mobile-alt"></i>
                            </div>
                            <div>
                                <strong>Truy cập đa nền tảng</strong>
                                <p>Sử dụng WM trên máy tính, tablet và điện thoại di động</p>
                            </div>
                        </li>
                        <li>
                            <div class="feature-icon">
                                <i class="fas fa-shield-alt"></i>
                            </div>
                            <div>
                                <strong>Bảo mật tuyệt đối</strong>
                                <p>Dữ liệu của bạn được bảo vệ bằng mã hóa cao</p>
                            </div>
                        </li>
                    </ul>
                </div>

                <!-- Floating Elements -->
                <div class="floating-elements">
                    <div class="floating-icon">
                        <i class="fas fa-shopping-cart"></i>
                    </div>
                    <div class="floating-icon">
                        <i class="fas fa-boxes"></i>
                    </div>
                    <div class="floating-icon">
                        <i class="fas fa-chart-bar"></i>
                    </div>
                </div>
            </div>

            <!-- Right Login Form Section -->
            <div class="login-form-container fade-in">
                <div class="login-header">
                    <h1>Đăng nhập</h1>
                    <p>Nhập thông tin đăng nhập của bạn để tiếp tục</p>
                </div>

                <% if (request.getAttribute("error") != null) { %>
                <div class="error-message">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none" viewBox="0 0 24 24">
                        <path fill-rule="evenodd" clip-rule="evenodd" d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 17.5228 6.47715 22 12 22Z" fill="#D00E17" stroke="#D00E17" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"></path>
                        <path fill-rule="evenodd" clip-rule="evenodd" d="M12 7a1 1 0 0 1 1 1v4a1 1 0 1 1-2 0V8a1 1 0 0 1 1-1z" fill="#fff"></path>
                        <path d="M13 17a1 1 0 1 1-2 0 1 1 0 0 1 2 0z" fill="#fff"></path>
                    </svg>
                    <span><%= request.getAttribute("error") %></span>
                </div>
                <% } %>

                <form class="login-form" action="Login" method="post" id="loginForm">
                    <div class="form-group">
                        <label for="username">Email hoặc Số điện thoại</label>
                        <div class="input-wrapper">
                            <i class="fas fa-user input-icon"></i>
                            <input type="text" id="username" name="username" class="form-control" 
                                   placeholder="Nhập email hoặc số điện thoại" required autofocus>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="password">Mật khẩu</label>
                        <div class="input-wrapper">
                            <i class="fas fa-lock input-icon"></i>
                            <input type="password" id="password" name="password" class="form-control" 
                                   placeholder="Nhập mật khẩu" required>
                            <button type="button" class="toggle-password" onclick="togglePassword()">
                                <i class="fas fa-eye"></i>
                            </button>
                        </div>
                    </div>

                    <div class="form-options">
                        <div class="remember-me">
                            <input type="checkbox" id="remember" name="remember">
                            <label for="remember">Nhớ mật khẩu</label>
                        </div>
                        <a href="ForgotPassword" class="forgot-password">Quên mật khẩu?</a>
                    </div>

                    <button type="submit" class="btn-login" id="loginButton">
                        <i class="fas fa-sign-in-alt"></i>
                        Đăng nhập
                    </button>

                    <div class="register-link">
                        Chưa có tài khoản? <a href="Register">Đăng ký ngay</a>
                    </div>
                </form>