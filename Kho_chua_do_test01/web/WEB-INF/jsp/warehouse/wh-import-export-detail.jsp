<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="Model.StockMovementsRequest,Model.StockMovementDetail" %>
<!DOCTYPE html>
<%
    String ctx = request.getContextPath();

    Integer movementID = null;
    try {
        Object mid = request.getAttribute("movementID");
        if (mid == null) mid = request.getParameter("movementID");
        movementID = (mid == null) ? null : Integer.valueOf(String.valueOf(mid));
    } catch (Exception ignore) {}

    StockMovementsRequest movement = (StockMovementsRequest) request.getAttribute("movement");

    @SuppressWarnings("unchecked")
    List<StockMovementDetail> movementDetails =
        (List<StockMovementDetail>) request.getAttribute("movementDetails");

    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    String successMessage = (String) session.getAttribute("successMessage");
    String errorMessage   = (String) session.getAttribute("errorMessage");

    String movementStatus = (movement != null && movement.getResponseStatus()!=null)
                            ? movement.getResponseStatus().toLowerCase() : "";
%>
<html>
    <head>
        <title>Chi tiết nhập hàng #<%= movementID == null ? "" : movementID %> - WH</title>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/warehouse/wh-import-export-detail.css">

    </head>
    <body>

        <%@ include file="header-warehouse.jsp" %>

        <div class="main-container">
            <main class="main-content">



                <% if (successMessage != null && !successMessage.isEmpty()) { %>
                <div class="alert alert-success">
                    <i class="fas fa-check-circle"></i> <%= successMessage %>
                </div>
                <% session.removeAttribute("successMessage"); %>
                <% } %>

                <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
                <div class="alert alert-error">
                    <i class="fas fa-exclamation-circle"></i> <%= errorMessage %>
                </div>
                <% session.removeAttribute("errorMessage"); %>
                <% } %>

                <div class="page-header">
                    <div class="page-header-flex">
                        <div>
                            <h1>Chi tiết đơn nhập hàng #<%= movementID == null ? "" : movementID %></h1>
                            <p class="page-subtitle">Danh sách sản phẩm và serial trong đơn nhập này</p>
                        </div>

                        <div>
                            <a href="<%=ctx%>/wh-import" class="btn btn-secondary">
                                <i class="fas fa-arrow-left"></i> Quay lại danh sách
                            </a>
                        </div>
                    </div>

                    <div class="header-actions">


                        <% Boolean canComplete = (Boolean) request.getAttribute("canComplete"); %>

                        <% if (!"completed".equals(movementStatus)) { %>
                        <% if (Boolean.TRUE.equals(canComplete)) { %>
                        <form action="<%=ctx%>/wh-complete-request" method="post" style="display:inline;">
                            <input type="hidden" name="id" value="<%= request.getParameter("id") %>"/>
                            <button type="submit" class="btn btn-success">
                                <i class="fas fa-check-circle"></i> Hoàn tất nhập hàng
                            </button>
                        </form>
                        <% } %>
                        <% } %>
                    </div>
                </div>

                <div class="movement-info-card">
                    <div class="info-grid">
                        <div class="info-item">
                            <label>Loại hoạt động:</label>
                            <span class="movement-type-badge import">
                                <i class="fas fa-arrow-down"></i> Nhập hàng
                            </span>
                        </div>

                        <div class="info-item">
                            <label>Ngày tạo:</label>
                            <%
                                String createdAtStr = "";
                                try { if (movement != null && movement.getCreatedAt()!=null) createdAtStr = df.format(movement.getCreatedAt()); } catch(Exception ignore){}
                            %>
                            <%= createdAtStr %>
                        </div>

                        <div class="info-item">
                            <label>Người tạo:</label>
                            <span><%= movement != null && movement.getCreatedByName()!=null ? movement.getCreatedByName() : "" %></span>
                        </div>

                        <div class="info-item">
                            <label>Trạng thái:</label>
                            <%
  String statusText = "Chờ xử lý";
  String icon = "fa-hourglass-half";
  if ("completed".equalsIgnoreCase(movementStatus)) { statusText="Hoàn thành"; icon="fa-check-circle"; }
  else if ("processing".equalsIgnoreCase(movementStatus)) { statusText="Đang xử lý"; icon="fa-clock"; }
                            %>
                            <span class="status-badge <%= movementStatus %>">
                                <i class="fas <%= icon %>"></i> <%= statusText %>
                            </span>
                        </div>
                    </div>

                    <%
                        String note = (movement != null) ? movement.getNote() : null;
                        if (note != null && !note.isEmpty()) {
                    %>
                    <div class="info-item full-width">
                        <label>Ghi chú:</label>
                        <span><%= note %></span>
                    </div>
                    <% } %>
                </div>

                <div class="table-container">
                    <table class="invoices-table">
                        <thead>
                            <tr>
                                <th>STT</th>
                                <th>Tên sản phẩm</th>
                                <th>Mã sản phẩm</th>
                                <th>Số lượng</th>
                                <th>Đã xử lý</th>
                                <th>Trạng thái</th>
                                <th>Serial Numbers</th>
                            </tr>
                        </thead>

                        <tbody>
                            <%
                                if (movementDetails != null && !movementDetails.isEmpty()) {
                                    for (int i = 0; i < movementDetails.size(); i++) {
                                        StockMovementDetail d = movementDetails.get(i);
                                        int qty = d.getQuantity();
                                        int scanned = d.getScanned();

                                        boolean completed = (scanned == qty);
                                        boolean inProgress = (!completed && scanned > 0);

                                        String rowCls = completed ? "row-completed" : "";
                            %>
                            <tr class="<%= rowCls %>">
                                <td><%= i+1 %></td>
                                <td><strong><%= d.getProductName() == null ? "" : d.getProductName() %></strong></td>
                                <td><code><%= d.getProductCode() == null ? "" : d.getProductCode() %></code></td>
                                <td><%= qty %></td>
                                <td>
                                    <span class="<%= completed ? "text-success" : "text-warning" %>"><%= scanned %>/<%= qty %></span>
                                </td>
                                <td>
                                    <%
                                        if (completed) {
                                    %>
                                    <span class="badge badge-success">Hoàn thành</span>
                                    <%
                                        } else if (inProgress) {
                                    %>
                                    <span class="badge badge-warning">Đang xử lý</span>
                                    <%
                                        } else {
                                    %>
                                    <span class="badge badge-pending">Chờ xử lý</span>
                                    <%
                                        }
                                    %>
                                </td>

                                <!-- Serial numbers -->
                                <td>
                                    <%
                                        List<?> serials = d.getSerials(); // List<ProductDetailSerialNumber> hoặc tương đương
                                        boolean hasSerials = (serials != null && !serials.isEmpty());
                                    %>

                                    <% if (!hasSerials) { %>
                                    <span class="text-muted">Chưa có serial</span>
                                    <% } else { %>
                                    <div class="serials-list">
                                        <%
                                            int shown = 0;
                                            for (Object sObj : serials) {
                                                shown++;
                                                String sn = "";
                                                try { sn = (String) sObj.getClass().getMethod("getSerialNumber").invoke(sObj); } catch (Exception ignore){}
                                                if (shown <= 3) {
                                        %>
                                        <span class="serial-badge"><%= sn %></span>
                                        <%
                                                }
                                            }
                                            int remain = serials.size() - 3;
                                            if (remain > 0) {
                                        %>
                                        <span class="serial-more" onclick="showMoreSerials(this)">+<%= remain %> khác</span>
                                        <%
                                                int idx = 0;
                                                for (Object sObj : serials) {
                                                    idx++;
                                                    if (idx > 3) {
                                                        String snHidden = "";
                                                        try { snHidden = (String) sObj.getClass().getMethod("getSerialNumber").invoke(sObj); } catch (Exception ignore){}
                                        %>
                                        <span class="serial-badge hidden" style="display:none;"><%= snHidden %></span>
                                        <%
                                                    }
                                                }
                                            }
                                        %>
                                    </div>
                                    <% } %>



                                    <% boolean isCompleted = "completed".equalsIgnoreCase(movementStatus); %>
                                    <% boolean invoiceCompleted = "completed".equalsIgnoreCase(movementStatus); %>
                                    <!-- Chỉ hiển thị form khi CHƯA completed -->
                                    <% if (!isCompleted && !invoiceCompleted) { %>
                                    <!-- ====== FORM THÊM SERIAL ====== -->
                                    <form action="<%=ctx%>/wh-import-export-detail" method="post" style="display:flex; gap:6px; margin-top:6px;">
                                        <input type="hidden" name="action" value="addSerial"/>
                                        <input type="hidden" name="id" value="<%= request.getParameter("id") %>"/>
                                        <input type="hidden" name="movementDetailId" value="<%= d.getMovementDetailId() %>"/>
                                        <input type="hidden" name="productDetailId" value="<%= d.getProductDetailId() %>"/>
                                        <input type="text"   name="serialNumber" placeholder="Nhập/scan serial..." required class="form-input"/>
                                        <button type="submit" class="btn btn-sm btn-primary"><i class="fas fa-plus"></i></button>
                                    </form>

                                    <!-- ====== FORM XOÁ SERIAL ====== -->
                                    <form action="<%=ctx%>/wh-import-export-detail" method="post" style="display:flex; gap:6px; margin-top:4px;">
                                        <input type="hidden" name="action" value="delSerial"/>
                                        <input type="hidden" name="id" value="<%= request.getParameter("id") %>"/>
                                        <input type="hidden" name="movementDetailId" value="<%= d.getMovementDetailId() %>"/>
                                        <input type="hidden" name="productDetailId" value="<%= d.getProductDetailId() %>"/>
                                        <input type="text"   name="serialNumber" placeholder="Xoá serial..." required class="form-input"/>
                                        <button type="submit" class="btn btn-sm btn-danger"><i class="fas fa-trash"></i></button>
                                    </form>
                                    <% } %>
                                </td>
                            </tr>
                            <%
                                    }
                                } else {
                            %>
                            <tr>
                                <td colspan="7" class="no-data">
                                    <i class="fas fa-box-open"></i> Không có sản phẩm nào trong đơn hàng này
                                </td>
                            </tr>
                            <%
                                }
                            %>
                        </tbody>
                    </table>
                </div>

            </main>
        </div>

        <!-- JS “Xem thêm serial” -->
        <script>
            function showMoreSerials(el) {
                const container = el.closest('.serials-list');
                const hidden = container.querySelectorAll('.serial-badge.hidden');
                hidden.forEach(function (s) {
                    s.style.display = 'inline-block';
                    s.classList.remove('hidden');
                });
                el.style.display = 'none';
            }
        </script>

        <!-- CSS tinh gọn (giữ nguyên nếu bạn đã có file css) -->
        <style>
            .movement-info-card {
                background:#f8f9fa;
                border:1px solid #dee2e6;
                border-radius:8px;
                padding:15px;
                margin-bottom:20px;
            }
            .info-grid {
                display:grid;
                grid-template-columns:repeat(auto-fit,minmax(220px,1fr));
                gap:10px;
            }
            .info-item label {
                font-size:12px;
                font-weight:600;
                color:#6c757d;
                text-transform:uppercase;
            }
            .badge-success {
                background:#d4edda;
                color:#155724;
            }
            .badge-warning {
                background:#fff3cd;
                color:#856404;
            }
            .badge-pending {
                background:#f8d7da;
                color:#721c24;
            }
            .serial-badge {
                display:inline-block;
                background:#e9ecef;
                padding:3px 6px;
                border-radius:3px;
                font-size:10px;
                font-family:monospace;
                margin:1px;
            }
            .serial-more {
                cursor:pointer;
                color:#007bff;
                font-weight:500;
                font-size:11px;
            }
            .row-completed {
                background:#f8fff8;
            }
            .alert {
                padding:10px;
                margin-bottom:15px;
                border-radius:4px;
            }
            .alert-success {
                background:#d4edda;
                color:#155724;
            }
            .alert-error {
                background:#f8d7da;
                color:#721c24;
            }

            /* Vùng tô màu */
            .page-header{
                /* background...;  padding trái/phải đang quyết định nút cách mép bao nhiêu */
                padding: 12px 0;        /* cho nút chạm sát mép phải của vùng tô -> 0px bên phải */
            }

            /* Hàng tiêu đề + nút */
            .page-header-flex{
                display:flex;
                align-items:center;
                justify-content:space-between;
                width:100%;             /* QUAN TRỌNG: đẩy phần nút ra được tới mép phải */
            }

            /* Không bắt buộc nhưng giúp nút không bị xuống dòng */
            .header-actions .btn{
                white-space:nowrap;
                display:inline-flex;
                align-items:center;
                gap:8px;
            }
        </style>

    </body>
</html>
