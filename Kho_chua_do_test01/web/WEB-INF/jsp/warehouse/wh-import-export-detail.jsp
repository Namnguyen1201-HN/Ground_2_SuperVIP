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
    
    String movementType = (String) request.getAttribute("movementType");
    if (movementType == null) {
        movementType = request.getParameter("movementType");
    }
    if (movementType == null || movementType.isEmpty()) {
        movementType = "import";
    }
    
    boolean isExport = "export".equalsIgnoreCase(movementType);
    String pageTitle = isExport ? "Chi tiết đơn xuất hàng" : "Chi tiết đơn nhập hàng";
    String backUrl = isExport ? ctx + "/warehouse-export-orders" : ctx + "/wh-import";
    String actionText = isExport ? "Xuất hàng" : "Nhập hàng";
    String iconClass = isExport ? "fa-arrow-up" : "fa-arrow-down";
    String badgeClass = isExport ? "export" : "import";
%>
<html>
    <head>
        <title><%= pageTitle %> #<%= movementID == null ? "" : movementID %> - WH</title>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/warehouse/wh-import-export-detail.css">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
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
                            <h1><%= pageTitle %> #<%= movementID == null ? "" : movementID %></h1>
                            <p class="page-subtitle">Danh sách sản phẩm và serial trong đơn <%= isExport ? "xuất" : "nhập" %> này</p>
                        </div>

                        <div>
                            <a href="<%= backUrl %>" class="btn btn-secondary">
                                <i class="fas fa-arrow-left"></i> Quay lại
                            </a>
                        </div>
                    </div>

                    <div class="header-actions">
                        <% Boolean canComplete = (Boolean) request.getAttribute("canComplete"); %>
                        <% if (!"completed".equals(movementStatus) && !"Hoàn thành".equalsIgnoreCase(movement != null ? movement.getResponseStatus() : "")) { %>
                        <% if (Boolean.TRUE.equals(canComplete)) { %>
                        <form action="<%=ctx%>/wh-complete-request" method="post" style="display:inline;">
                            <input type="hidden" name="id" value="<%= request.getParameter("id") %>"/>
                            <input type="hidden" name="movementType" value="<%= movementType %>"/>
                            <button type="submit" class="btn btn-success">
                                <i class="fas fa-check-circle"></i> Hoàn tất <%= isExport ? "xuất" : "nhập" %> hàng
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
                            <span class="movement-type-badge <%= badgeClass %>">
                                <i class="fas <%= iconClass %>"></i> <%= actionText %>
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
                                <th>Serial number</th>
                                <th>Số lượng cần <%= isExport ? "xuất" : "nhập" %></th>
                                <th>Đã <%= isExport ? "xuất" : "nhập" %></th>
                                <th>Thao tác</th>
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
                                <td>
                                    <strong><%= d.getProductName() == null ? "" : d.getProductName() %></strong>
                                    <% if (d.getProductCode() != null && !d.getProductCode().isEmpty()) { %>
                                    <span style="color: #6b7280; font-size: 12px;">(<%= d.getProductCode() %>)</span>
                                    <% } %>
                                </td>
                                <td>
                                    <%
                                        List<?> serials = d.getSerials();
                                        boolean hasSerials = (serials != null && !serials.isEmpty());
                                    %>
                                    <% if (!hasSerials) { %>
                                    <span class="text-muted">Chưa có serial nào</span>
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
                                </td>
                                <td><%= qty %></td>
                                <td>
                                    <span class="<%= completed ? "text-success" : "text-warning" %>"><%= scanned %>/<%= qty %></span>
                                </td>
                                <td>
                                    <% boolean isCompleted = "completed".equalsIgnoreCase(movementStatus) || "Hoàn thành".equalsIgnoreCase(movement != null ? movement.getResponseStatus() : ""); %>
                                    <% if (!isCompleted) { %>
                                    <button type="button" class="btn btn-scan" onclick="openScanModal(<%= d.getMovementDetailId() %>, <%= d.getProductDetailId() %>)">
                                        <i class="fas fa-qrcode"></i> Quét
                                    </button>
                                    <% } else { %>
                                    <span class="badge badge-success">Hoàn thành</span>
                                    <% } %>
                                </td>
                            </tr>
                            <%
                                    }
                                } else {
                            %>
                            <tr>
                                <td colspan="6" class="no-data">
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

        <!-- Modal for Serial Scanning -->
        <div id="scanModal" class="modal" style="display: none;">
            <div class="modal-content">
                <div class="modal-header">
                    <h2>Nhập Serial Sản phẩm</h2>
                    <span class="close" onclick="closeScanModal()">&times;</span>
                </div>
                <div class="modal-body">
                    <form id="serialForm" method="post" action="<%=ctx%>/serial-check">
                        <input type="hidden" name="action" value="addSerial"/>
                        <input type="hidden" name="id" value="<%= request.getParameter("id") %>"/>
                        <input type="hidden" name="movementType" value="<%= movementType %>"/>
                        <input type="hidden" name="movementDetailId" id="modalMovementDetailId"/>
                        <input type="hidden" name="productDetailId" id="modalProductDetailId"/>
                        
                        <div class="form-group">
                            <label for="serialNumber">Serial Number:</label>
                            <input type="text" name="serialNumber" id="serialNumber" 
                                   placeholder="Nhập serial hoặc quét QR..." 
                                   class="form-input" required autofocus/>
                        </div>
                        
                        <div class="modal-actions">
                            <button type="button" class="btn btn-primary" onclick="scanQRCode()">
                                <i class="fas fa-qrcode"></i> Quét QR Code
                            </button>
                            <button type="submit" class="btn btn-success">
                                <i class="fas fa-plus"></i> + Thêm Serial
                            </button>
                            <button type="button" class="btn btn-secondary" onclick="closeScanModal()">
                                <i class="fas fa-times"></i> x Hủy
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- JS "Xem thêm serial" -->
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

            function openScanModal(movementDetailId, productDetailId) {
                document.getElementById('modalMovementDetailId').value = movementDetailId;
                document.getElementById('modalProductDetailId').value = productDetailId;
                document.getElementById('scanModal').style.display = 'block';
                document.getElementById('serialNumber').focus();
            }

            function closeScanModal() {
                document.getElementById('scanModal').style.display = 'none';
                document.getElementById('serialForm').reset();
            }

            function scanQRCode() {
                // Placeholder for QR code scanning functionality
                alert('Tính năng quét QR Code sẽ được tích hợp sau');
            }

            // Close modal when clicking outside
            window.onclick = function(event) {
                const modal = document.getElementById('scanModal');
                if (event.target == modal) {
                    closeScanModal();
                }
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
