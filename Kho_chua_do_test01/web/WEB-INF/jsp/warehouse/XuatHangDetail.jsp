<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Chi tiết đơn xuất hàng</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <style>
            body {
                background-color: #f8f9fa;
            }
            .filter-box, .table-container {
                background-color: #fff;
                border-radius: 8px;
                padding: 20px;
                box-shadow: 0 0 5px rgba(0,0,0,0.1);
            }
            .btn-scan {
                background-color: #ff9800;
                color: #fff;
            }
            .btn-scan:hover {
                background-color: #e68900;
            }
            .alert-custom {
                border-radius: 6px;
                padding: 12px;
                margin-bottom: 15px;
            }
            .serial-chip {
                display:inline-flex;
                align-items:center;
                gap:.25rem;
                border:1px solid #dee2e6;
                border-radius:16px;
                padding:.15rem .5rem;
                margin:.15rem .15rem 0 0;
                background:#f8f9fa;
                font-size:.85rem;
            }
            .serial-chip form {
                display:inline;
                margin:0;
            }
            .serial-chip button {
                border:none;
                background:transparent;
                color:#dc3545;
                cursor:pointer;
            }
        </style>
    </head>

    <body>
        <%@ include file="../warehouse/header-warehouse.jsp" %>

        <%
            // Dữ liệu do servlet setAttribute
            Map<String,Object> movement = (Map<String,Object>) request.getAttribute("movement");
            List<Map<String,Object>> details = (List<Map<String,Object>>) request.getAttribute("details");
            List<Map<String,Object>> productOptions = (List<Map<String,Object>>) request.getAttribute("productOptions");

            Integer movementId = (movement != null) ? (Integer) movement.get("MovementID") : null;

            String status = (String) request.getAttribute("status");
            Integer productDetailId = (Integer) request.getAttribute("productDetailId");
            Integer pageNum  = (Integer) request.getAttribute("page");
            Integer pageSize = (Integer) request.getAttribute("size");
            Integer totalRows = (Integer) request.getAttribute("totalRows");
            int totalPages = (int) Math.ceil((totalRows != null ? totalRows : 0) / (double) (pageSize != null ? pageSize : 10));

            // Thông báo (PRG pattern – lấy từ session rồi xóa)
            String successMsg = (String) session.getAttribute("successMsg");
            if (successMsg != null) session.removeAttribute("successMsg");

            String errorMsg = (String) session.getAttribute("errorMsg");
            if (errorMsg != null) session.removeAttribute("errorMsg");
        %>

        <div class="container-fluid mt-4">
            <div class="row d-flex justify-content-center">

                <div class="col-md-10">
                    <div class="row g-3">

                        <!-- Bộ lọc -->
                        <div class="col-lg-3">
                            <div class="filter-box">
                                <h6 class="mb-3 fw-bold">Bộ lọc</h6>

                                <form method="get" action="XuatHangDetail">
                                    <input type="hidden" name="id" value="<%= movementId != null ? movementId : 0 %>"/>

                                    <div class="mb-3">
                                        <label class="form-label">Sản phẩm:</label>
                                        <select class="form-select" name="productDetailId">
                                            <option value="">-- Tất cả sản phẩm --</option>
                                            <%
                                                if (productOptions != null) {
                                                    for (Map<String,Object> p : productOptions) {
                                                        Integer optId = (Integer)p.get("ProductDetailID");
                                                        String txt = (String)p.get("ProductName") + " (" + (String)p.get("ProductCode") + ")";
                                            %>
                                            <option value="<%=optId%>" <%= (productDetailId != null && productDetailId.equals(optId)) ? "selected" : "" %>><%= txt %></option>
                                            <%  } } %>
                                        </select>
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label">Trạng thái:</label>
                                        <div class="ms-1">
                                            <div class="form-check">
                                                <input class="form-check-input" type="radio" name="status" value="all" <%= ("all".equalsIgnoreCase(status))?"checked":(status==null?"checked":"") %>>
                                                <label class="form-check-label">Tất cả</label>
                                            </div>
                                            <div class="form-check">
                                                <input class="form-check-input" type="radio" name="status" value="pending" <%= "pending".equalsIgnoreCase(status)?"checked":"" %>>
                                                <label class="form-check-label">Chờ xử lý</label>
                                            </div>
                                            <div class="form-check">
                                                <input class="form-check-input" type="radio" name="status" value="processing" <%= "processing".equalsIgnoreCase(status)?"checked":"" %>>
                                                <label class="form-check-label">Đang xử lý</label>
                                            </div>
                                            <div class="form-check">
                                                <input class="form-check-input" type="radio" name="status" value="completed" <%= "completed".equalsIgnoreCase(status)?"checked":"" %>>
                                                <label class="form-check-label">Hoàn thành</label>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="d-flex gap-2">
                                        <button type="submit" class="btn btn-primary flex-fill">Áp dụng lọc</button>
                                        <a class="btn btn-secondary flex-fill" href="XuatHangDetail?id=<%=movementId%>">Reset</a>
                                    </div>
                                </form>
                            </div>
                        </div>

                        <!-- Bảng chi tiết -->
                        <div class="col-lg-9">
                            <div class="table-container">

                                <% if (successMsg != null) { %>
                                <div class="alert alert-success alert-custom"><i class="fa-solid fa-circle-check me-2"></i><%= successMsg %></div>
                                    <% } %>
                                    <% if (errorMsg != null) { %>
                                <div class="alert alert-danger alert-custom"><i class="fa-solid fa-circle-exclamation me-2"></i><%= errorMsg %></div>
                                    <% } %>

                                <div class="d-flex justify-content-between align-items-center mb-3">
                                    <div>
                                        <h5 class="fw-bold mb-0">
                                            Chi tiết đơn xuất hàng #<%= movement != null ? movement.get("MovementID") : "?" %>
                                        </h5>
                                        <div class="text-muted small">
                                            Chi nhánh yêu cầu:
                                            <strong><%= movement != null ? (movement.get("FromBranchName") != null ? movement.get("FromBranchName") : "—") : "—" %></strong>
                                            &nbsp;|&nbsp; Ngày tạo:
                                            <strong><%= movement != null ? movement.get("CreatedAt") : "—" %></strong>
                                            &nbsp;|&nbsp; Người tạo:
                                            <strong><%= movement != null ? (movement.get("CreatedByName") != null ? movement.get("CreatedByName") : "—") : "—" %></strong>
                                        </div>
                                    </div>
                                    <a href="XuatHang" class="btn btn-outline-secondary">Quay lại</a>
                                </div>

                                <div class="table-responsive">
                                    <table class="table table-bordered align-middle">
                                        <thead class="table-light">
                                            <tr>
                                                <th style="width: 60px;">STT</th>
                                                <th>Tên sản phẩm</th>
                                                <th style="min-width:260px;">Serial number</th>
                                                <th style="width: 140px;">Số lượng yêu cầu</th>
                                                <th style="width: 140px;">Đã xuất</th>
                                                <th style="width: 120px;">Trạng thái</th>
                                                <th style="width: 100px;">Thao tác</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <%
                                                if (details != null && !details.isEmpty()) {
                                                    int i = 1;
                                                    for (Map<String,Object> row : details) {
                                                        int mdId = (Integer)row.get("MovementDetailID");
                                                        int qty = (Integer)row.get("Quantity");
                                                        int serialCount = ((Number)row.get("SerialCount")).intValue();
                                                        String prodName = (String)row.get("ProductName");
                                                        String code = (String)row.get("ProductCode");
                                                        String serialsStr = (String)row.get("Serials");
                                                        String itemStatus = (String)row.get("ItemStatus"); // 'Chờ xử lý'/'Đang xử lý'/'Hoàn thành'
                                            %>
                                            <tr>
                                                <td><%= i++ %></td>
                                                <td>
                                                    <strong><%= prodName %></strong><br>
                                                    <small class="text-muted">(<%= code %>)</small>
                                                </td>
                                                <td>
                                                    <%
                                                        if (serialCount == 0) {
                                                    %>
                                                    <span class="text-muted">Chưa có serial nào</span>
                                                    <%
                                                        } else {
                                                            // Hiển thị từng serial + nút gỡ
                                                            String[] sns = serialsStr.split(",\\s*");
                                                            for (String sn : sns) {
                                                    %>
                                                    <span class="serial-chip">
                                                        <span><%= sn %></span>
                                                        <form method="post" action="XuatHangDetail">
                                                            <input type="hidden" name="action" value="removeSerial">
                                                            <input type="hidden" name="id" value="<%= movementId %>">
                                                            <input type="hidden" name="movementDetailId" value="<%= mdId %>">
                                                            <input type="hidden" name="serial" value="<%= sn %>">
                                                            <button type="submit" title="Gỡ serial"><i class="fa-solid fa-xmark"></i></button>
                                                        </form>
                                                    </span>
                                                    <%
                                                            }
                                                        }
                                                    %>
                                                </td>
                                                <td><%= qty %></td>
                                                <td><%= serialCount %>/<%= qty %></td>
                                                <td>
                                                    <span class="badge <%= "Hoàn thành".equals(itemStatus) ? "bg-success" : ("Đang xử lý".equals(itemStatus) ? "bg-warning text-dark" : "bg-secondary") %>">
                                                        <%= itemStatus %>
                                                    </span>
                                                </td>
                                                <td>
                                                    <button class="btn btn-scan btn-sm"
                                                            data-bs-toggle="modal" data-bs-target="#serialModal"
                                                            data-detail-id="<%= mdId %>"
                                                            data-product="<%= prodName + " (" + code + ")" %>">
                                                        Quét
                                                    </button>
                                                </td>
                                            </tr>
                                            <%
                                                    } // end for
                                                } else {
                                            %>
                                            <tr>
                                                <td colspan="7" class="text-center text-muted py-3">
                                                    Không có sản phẩm nào trong đơn.
                                                </td>
                                            </tr>
                                            <% } %>
                                        </tbody>
                                    </table>
                                </div>

                                <div class="d-flex justify-content-between align-items-center">
                                    <p class="mb-0">
                                        Hiển thị <%= (details!=null?details.size():0) %> /
                                        Tổng <%= (totalRows!=null?totalRows:0) %> sản phẩm
                                        (Trang <%= (pageNum!=null?pageNum:1) %>/<%= Math.max(totalPages,1) %>)
                                    </p>

                                    <form method="get" action="XuatHangDetail" class="d-flex align-items-center gap-2">
                                        <input type="hidden" name="id" value="<%= movementId %>">
                                        <input type="hidden" name="status" value="<%= status != null ? status : "all" %>">
                                        <input type="hidden" name="productDetailId" value="<%= productDetailId != null ? productDetailId : "" %>">

                                        <div class="input-group" style="width: 240px;">
                                            <span class="input-group-text">Trang</span>
                                            <input class="form-control" type="number" name="page"
                                                   value="<%= pageNum!=null?pageNum:1 %>" min="1" max="<%= Math.max(totalPages,1) %>">
                                            <span class="input-group-text">| size</span>
                                            <select class="form-select" name="size" onchange="this.form.submit()">
                                                <% int sz = pageSize!=null?pageSize:10; %>
                                                <option value="10" <%= (10==sz) ? "selected":"" %>>
                                                <option value="20" <%= (20==sz) ? "selected":"" %>>
                                                <option value="50" <%= (50==sz) ? "selected":"" %>>
                                            </select>
                                        </div>
                                        <button class="btn btn-outline-primary">Đi</button>
                                    </form>
                                </div>

                            </div>
                        </div>

                    </div>
                </div>

            </div>
        </div>

        <!-- Modal nhập serial -->
        <div class="modal fade" id="serialModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog">
                <form method="post" action="XuatHangDetail" class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Nhập Serial – <span id="modalProductLabel"></span></h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <input type="hidden" name="action" value="addSerial">
                        <input type="hidden" name="id" value="<%= movementId != null ? movementId : 0 %>">
                        <input type="hidden" name="movementDetailId" id="modalMovementDetailId">

                        <label for="serialInput" class="form-label">Serial Number:</label>
                        <input type="text" class="form-control mb-3" id="serialInput" name="serial"
                               placeholder="Nhập serial hoặc quét QR..." required>
                        <div class="d-flex justify-content-center gap-2">
                            <button type="button" class="btn btn-outline-secondary" title="(demo) chưa tích hợp">
                                <i class="fa-solid fa-qrcode me-1"></i> Quét QR Code
                            </button>
                            <button type="submit" class="btn btn-success">
                                <i class="fa-solid fa-plus me-1"></i> Thêm Serial
                            </button>
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                <i class="fa-solid fa-xmark me-1"></i> Hủy
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
                                                const serialModal = document.getElementById('serialModal');
                                                serialModal.addEventListener('show.bs.modal', function (event) {
                                                    const btn = event.relatedTarget;
                                                    const detailId = btn.getAttribute('data-detail-id');
                                                    const product = btn.getAttribute('data-product');

                                                    serialModal.querySelector('#modalProductLabel').textContent = product;
                                                    serialModal.querySelector('#modalMovementDetailId').value = detailId;
                                                    serialModal.querySelector('#serialInput').value = '';
                                                    setTimeout(() => serialModal.querySelector('#serialInput').focus(), 200);
                                                });
        </script>
    </body>
</html>
