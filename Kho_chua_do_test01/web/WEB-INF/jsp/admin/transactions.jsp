<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Giao dịch</title>
        <link href="css/NhanVien.css" rel="stylesheet" type="text/css"/>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <link href="css/Transactions.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
        <div class="container-fluid">
            <%@ include file="includes/header.jsp" %>
            <div class="content">
                <main class="main-content transactions-page" style="width:100%">
                    <div class="toolbar d-flex justify-content-between align-items-center">
                        <div class="d-flex gap-2">
                            <button class="btn btn-outline-secondary" type="button" onclick="location.href='TongQuan'">← Quay lại</button>
                        </div>
                        <h2 class="m-0">Danh mục giao dịch</h2>
                        <div></div>
                    </div>
                    <div class="list-group">
                        <a class="list-group-item list-group-item-action" href="Orders">Giao dịch - Đơn hàng</a>
                        <a class="list-group-item list-group-item-action" href="CreateInbound">Giao dịch - Tạo đơn nhập kho</a>
                        <a class="list-group-item list-group-item-action" href="InventoryTracking">Giao dịch - Theo dõi nhập xuất</a>
                    </div>
                </main>
            </div>
        </div>
    </body>
</html>


