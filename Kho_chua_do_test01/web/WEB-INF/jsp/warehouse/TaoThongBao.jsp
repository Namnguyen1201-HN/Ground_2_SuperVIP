<%-- 
    Document   : TaoThongBao
    Created on : Oct 10, 2025, 9:21:10 PM
    Author     : TieuPham
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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

                <form>
                    <label for="title" class="form-label">Tiêu đề:</label>
                    <input type="text" class="form-control" id="title" placeholder="Nhập tiêu đề thông báo...">

                    <label for="content" class="form-label">Nội dung:</label>
                    <textarea class="form-control" id="content" rows="4" placeholder="Nhập nội dung thông báo..."></textarea>

                    <button type="submit" class="btn btn-send mt-2">
                        <i class="fa-solid fa-paper-plane"></i> Gửi thông báo
                    </button>
                </form>
            </div>

            <!-- Thông báo đã gửi -->
            <div class="section-box">
                <h5><i class="fa-solid fa-paper-plane"></i> Thông báo đã gửi</h5>

                <!-- Mẫu thông báo -->
                <div class="sent-message">
                    <strong>Tôi</strong>
                    <br> Bố muốn nhập hàng
                    <br> Nhập cho tao 100 cân sà càn
                    <small>23:40 29/09/2025</small>
                </div>
            </div>
        </div>

        <!-- Cột phải -->
        <div class="right-column">
            <div class="section-box text-center">
                <h5><i class="fa-solid fa-inbox"></i> Thông báo nhận được</h5>

                <div class="empty-box">
                    <i class="fa-regular fa-inbox"></i>
                    <p>Không có thông báo nào.</p>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
