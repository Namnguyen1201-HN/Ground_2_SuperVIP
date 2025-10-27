<%-- 
    Document   : SASentNoti
    Created on : Oct 24, 2025, 9:16:20 AM
    Author     : Kawaii
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, Model.Announcement" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TSMS - Hệ thống thông báo</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sale/SASentNoti.css">
    <link rel="stylesheet" href="css/header.css"/>
   
</head>
<body>
<!-- Header -->
<header class="header">
    <div class="header-main">
        <div class="logo">
            <div class="logo-icon"><span class="icon-building"></span></div>
            <span>WM</span>
        </div>
        <nav class="nav-menu">
           
            <a href="sale" class="nav-item "><span class="icon-products"></span> Hàng hóa</a>
            <a href="SAThongBao" class="nav-item active"><span class="icon-transactions"></span> Gửi yêu cầu</a>
            <a href="Supplier" class="nav-item"><span class="icon-partners"></span> Đối tác</a>

        </nav>

        <div class="header-right">
            <div class="user-dropdown">
                <a href="#" class="user-icon gradient" id="dropdownToggle">
                    <i class="fas fa-user-circle fa-2x"></i>
                </a>
                <div class="dropdown-menu" id="dropdownMenu">
                    <a href="InformationAccount" class="dropdown-item">Thông tin chi tiết</a>
                    <a href="Login" class="dropdown-item">Đăng xuất</a>
                </div>
            </div>
        </div>
    </div>
</header>

<!-- Toast theo request attribute 'msg' do controller set -->
<c:if test="${not empty msg}">
    <div id="toastMessage">
        <i class="fas fa-check-circle"></i> ${msg}
    </div>
    <script>
        setTimeout(()=>{ const t=document.getElementById('toastMessage'); if(t) t.style.display='none'; },3000);
    </script>
</c:if>

<!-- Main Container -->
<div class="main-container">
    <!-- Left Section (2/3) -->
    <div class="left-section">
        <!-- Compose Message Box -->
        <div class="chat-box compose-box">
            <div class="chat-header">
                <i class="fas fa-edit"></i> Soạn thông báo
            </div>
            <div class="compose-content">
                <!-- SỬA: action về /SAThongBao; tham số name="title" & "content" -->
                <form method="post" action="SAThongBao">
                    <div class="input-group">
                        <label for="messageTitle">Tiêu đề:</label>
                        <input type="text" id="messageTitle" name="title" placeholder="Nhập tiêu đề thông báo..." required>
                    </div>
                    <div class="input-group">
                        <label for="messageContent">Nội dung:</label>
                        <textarea id="messageContent" class="message-textarea" name="content" placeholder="Nhập nội dung thông báo..." required></textarea>
                    </div>
                    <!-- SỬA: để button là submit, bỏ onclick JS gây chặn submit -->
                    <button type="submit" class="send-button">
                        <i class="fas fa-paper-plane"></i> Gửi thông báo
                    </button>
                </form>
            </div>
        </div>

        <!-- Sent Messages Box -->
        <div class="chat-box sent-messages">
            <div class="chat-header">
                <i class="fas fa-paper-plane"></i> Thông báo đã gửi
            </div>
            <div class="messages-content" id="sentMessages">
                <c:choose>
                    <c:when test="${empty sentList}">
                        <div class="empty-state">
                            <i class="fas fa-paper-plane"></i> Bạn chưa gửi thông báo nào.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="a" items="${sentList}">
                            <div class="message sent">
                                <div class="message-header">
                                    <span class="message-sender">Tôi</span>
                                    <span class="message-time">
                                        <fmt:formatDate value="${a.createdAt}" pattern="HH:mm dd/MM/yyyy" />
                                    </span>
                                </div>
                                <div class="message-title">${a.title}</div>
                                <div class="message-content">${a.description}</div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Right Section (1/3) -->
    <div class="right-section">
        <div class="chat-box received-messages">
            <div class="chat-header">
                <i class="fas fa-inbox"></i> Thông báo nhận được
            </div>
            <div class="messages-content" id="receivedMessages">
                <c:choose>
                    <c:when test="${empty receivedList}">
                        <div class="empty-state">
                            <i class="fas fa-inbox"></i> Không có thông báo nào.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="a" items="${receivedList}">
                            <div class="message received">
                                <div class="message-header">
                                    <!-- SỬA: thuộc tính đúng theo DAO là fromUserName -->
                                    <span class="message-sender">${a.fromUserName}</span>
                                    <span class="message-time">
                                        <fmt:formatDate value="${a.createdAt}" pattern="HH:mm dd/MM/yyyy" />
                                    </span>
                                </div>
                                <div class="message-title">${a.title}</div>
                                <div class="message-content">${a.description}</div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<script>
    // User dropdown
    const toggle = document.getElementById("dropdownToggle");
    const menu = document.getElementById("dropdownMenu");
    if (toggle) {
        toggle.addEventListener("click", function (e) {
            e.preventDefault();
            menu.style.display = menu.style.display === "block" ? "none" : "block";
        });
        document.addEventListener("click", function (e) {
            if (!toggle.contains(e.target) && !menu.contains(e.target)) {
                menu.style.display = "none";
            }
        });
    }

    // Auto-resize textarea
    const ta = document.getElementById('messageContent');
    if (ta) {
        ta.addEventListener('input', function () {
            this.style.height = 'auto';
            this.style.height = (this.scrollHeight) + 'px';
        });
    }
</script>
</body>
</html>
