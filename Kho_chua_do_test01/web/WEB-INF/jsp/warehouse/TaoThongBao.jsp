<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.Announcement" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Tạo thông báo</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <link href="css/warehouse/TaoThongBao.css" rel="stylesheet" type="text/css"/>

    </head>
    <body>
        <%@ include file="../warehouse/header-warehouse.jsp" %>

        <div class="page-container">
            <!-- Cột trái -->
            <div class="left-column">

                <!-- Soạn thông báo -->
                <div class="section-box">
                    <h5><i class="fa-solid fa-pen-to-square"></i> Soạn thông báo</h5>

                    <form action="ThongBao" method="post">
                        <label for="title" class="form-label">Tiêu đề:</label>
                        <input type="text" class="form-control" id="title" name="title" placeholder="Nhập tiêu đề thông báo..." required>

                        <label for="content" class="form-label">Nội dung:</label>
                        <textarea class="form-control" id="content" name="content" rows="4" placeholder="Nhập nội dung thông báo..." required></textarea>

                        <button type="submit" class="btn btn-send mt-2">
                            <i class="fa-solid fa-paper-plane"></i> Gửi thông báo
                        </button>
                    </form>
                </div>

                <!-- Thông báo đã gửi -->
                <div class="section-box">
                    <h5><i class="fa-solid fa-paper-plane"></i> Thông báo đã gửi</h5>

                    <%
                        List<Announcement> sentList = (List<Announcement>) request.getAttribute("sentList");
                        if (sentList != null && !sentList.isEmpty()) {
                            for (Announcement a : sentList) {
                    %>
                    <div class="sent-message">
                        <strong>Tôi</strong><br>
                        <b><%= a.getTitle() %></b><br>
                        <%= a.getDescription() %><br>
                        <small><%= new java.text.SimpleDateFormat("HH:mm dd/MM/yyyy").format(a.getCreatedAt()) %></small>
                    </div>
                    <% }} else { %>
                    <p class="text-muted">Chưa có thông báo nào đã gửi.</p>
                    <% } %>
                </div>
            </div>

            <!-- Cột phải -->
            <div class="right-column">
                <!-- Thông báo nhận được -->
                <div class="section-box text-center">
                    <h5><i class="fa-solid fa-inbox"></i> Thông báo nhận được</h5>

                    <%
                        List<Announcement> receivedList = (List<Announcement>) request.getAttribute("receivedList");
                        if (receivedList != null && !receivedList.isEmpty()) {
                            for (Announcement a : receivedList) {
                    %>
                    <div class="received-message text-start">
                        <strong><%= a.getFromUserName() %></strong><br>
                        <b><%= a.getTitle() %></b><br>
                        <%= a.getDescription() %><br>
                        <small><%= new java.text.SimpleDateFormat("HH:mm dd/MM/yyyy").format(a.getCreatedAt()) %></small>
                    </div>
                    <% }} else { %>
                    <div class="empty-box">
                        <i class="fa-regular fa-inbox"></i>
                        <p>Không có thông báo nào.</p>
                    </div>
                    <% } %>
                </div>
            </div>
        </div>
    </body>
</html>
