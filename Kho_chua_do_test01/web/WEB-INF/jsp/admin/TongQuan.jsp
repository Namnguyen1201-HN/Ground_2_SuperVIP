<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.Log" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>WMS - Tổng quan</title>
        <link href="css/admin/TongQuan.css" rel="stylesheet" type="text/css"/>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    </head>
    <body>
        <!-- Header -->
        <%@ include file="../admin/header_admin.jsp" %>

        <!-- Main Content -->
        <main class="main-content">
            <div class="content-left">
                <!-- Stats Section -->
                <section class="stats-section">
                    <h2 class="stats-title">KẾT QUẢ BÁN HÀNG HÔM NAY</h2>
                    <div class="stats-grid">
                        <div class="stat-card">
                            <div class="stat-icon revenue">💰</div>
                            <div class="stat-content">
                                <h3>4,886,000</h3>
                                <p>1 Hóa đơn - Doanh thu</p>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon orders">📋</div>
                            <div class="stat-content">
                                <h3>0</h3>
                                <p>0 phiếu - Trả hàng</p>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon growth">📈</div>
                            <div class="stat-content">
                                <h3>250.00%</h3>
                                <p>So với hôm qua</p>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon comparison">📊</div>
                            <div class="stat-content">
                                <h3>133.33%</h3>
                                <p>So với cùng kỳ tháng trước</p>
                            </div>
                        </div>
                    </div>
                </section>

                <!-- Chart Section -->
                <section class="chart-section">
                    <div class="chart-header">
                        <h2 class="chart-title">DOANH THU THUẦN THÁNG NÀY ℹ️</h2>
                        <div style="display: flex; gap: 1rem; align-items: center;">
                            <div class="chart-tabs">
                                <button class="chart-tab active">Theo ngày</button>
                                <button class="chart-tab">Theo giờ</button>
                                <button class="chart-tab">Theo thứ</button>
                            </div>
                            <select class="chart-dropdown">
                                <option>Tháng này</option>
                                <option>Tháng trước</option>
                                <option>3 tháng gần đây</option>
                            </select>
                        </div>
                    </div>
                    <div class="chart-container">
                        <div style="text-align: center;">
                            <div style="font-size: 3rem; margin-bottom: 1rem;">📊</div>
                            <p>Không có dữ liệu</p>
                        </div>
                    </div>
                </section>

                <!-- Top Products Section -->
                <section class="products-section">
                    <div class="products-header">
                        <h2 class="products-title">TOP 10 HÀNG HÓA BÁN CHẠY THÁNG NÀY</h2>
                        <div style="display: flex; gap: 1rem;">
                            <select class="chart-dropdown">
                                <option>THEO DOANH THU THUẦN</option>
                                <option>THEO SỐ LƯỢNG</option>
                            </select>
                            <select class="chart-dropdown">
                                <option>Tháng này</option>
                                <option>Tháng trước</option>
                            </select>
                        </div>
                    </div>
                    <div class="chart-container">
                        <div style="text-align: center;">
                            <div style="font-size: 3rem; margin-bottom: 1rem;">📦</div>
                            <p>Chưa có dữ liệu sản phẩm</p>
                        </div>
                    </div>
                </section>
            </div>

            <!-- Right Sidebar -->
            <aside class="sidebar">

                <!-- Notifications -->
                <div class="sidebar-card notifications-section">
                    <h3>THÔNG BÁO</h3>

                    <%
                        List<Model.Announcement> anns = (List<Model.Announcement>) request.getAttribute("announcements");
                        if (anns != null && !anns.isEmpty()) {
                            for (Model.Announcement a : anns) {
                    %>
                    <div class="notification-item">
                        <div class="notification-icon info">🔔</div>
                        <div class="notification-content">
                            <p>
                                <strong><%= a.getTitle() %></strong>  
                                <br/>
                                <%= a.getDescription() %>
                            </p>
                            <div class="time">
                                Bởi <%= a.getFromUserName() != null ? a.getFromUserName() : "Hệ thống" %> |
                                <%= new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(a.getCreatedAt()) %>
                            </div>
                        </div>
                    </div>
                    <%
                            }
                        } else {
                    %>
                    <p>Không có thông báo nào.</p>
                    <%
                        }
                    %>
                </div>

                <!-- CÁC HOẠT ĐỘNG GẦN ĐÂY -->
                <div class="activities" style="padding: 16px; background: #ffffff; border-radius: 8px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);">
                    <div style="display: flex; align-items: center; margin-bottom: 16px; border-bottom: 1px solid #e0e0e0; padding-bottom: 8px;">
                        <i style="color: #2196f3; font-size: 18px; margin-right: 8px;"></i>
                        <h4 style="margin: 0; color: #1a1a1a; font-size: 18px; font-weight: 600;">CÁC HOẠT ĐỘNG GẦN ĐÂY</h4>
                    </div>

                    <%
                        List<Model.AnnouncementDTO> activityLogs = (List<Model.AnnouncementDTO>) request.getAttribute("activityLogs");

                        if (activityLogs != null && !activityLogs.isEmpty()) {
                            for (Model.AnnouncementDTO log : activityLogs) {

                                String bgColor;
                                String iconClass;

                                if ("Đơn hàng".equals(log.getCategory())) {
                                    bgColor = "#e3f2fd";
                                    iconClass = "fa-shopping-cart";
                                } else if ("Nhập hàng".equals(log.getCategory()) || "Xuất kho".equals(log.getCategory())) {
                                    bgColor = "#ede7f6";
                                    iconClass = "fa-dolly";
                                } else {
                                    bgColor = "#fff3e0";
                                    iconClass = "fa-dollar-sign";
                                }
                    %>

                    <div class="activity-card" style="display: flex; background: #f9f9f9; border-radius: 10px; box-shadow: 0 2px 6px rgba(0,0,0,0.05); padding: 16px; margin-bottom: 12px; gap: 12px;">
                        <div style="flex-shrink: 0; width: 40px; height: 40px; border-radius: 50%; display: flex; align-items: center; justify-content: center; background: <%= bgColor %>;">
                            <i class="fas <%= iconClass %>" style="color: #333;"></i>
                        </div>

                        <div style="flex: 1; font-size: 13px;">
                            <% if ("Đơn hàng".equals(log.getCategory())) { %>
                            <div style="font-weight: 600; margin-bottom: 4px;">Đơn hàng mới</div>
                            <div>Mã: <%= log.getRawDescription() %></div>
                            <div>Người tạo: <%= log.getSenderName() %></div>
                            <div>Chi nhánh: <%= log.getLocationName() %></div>
                            <div>Tổng tiền: <%= log.getDescription() %></div>

                            <% } else if ("Nhập hàng".equals(log.getCategory()) || "Xuất kho".equals(log.getCategory())) { %>
                            <div style="font-weight: 600; margin-bottom: 4px;"><%= log.getCategory() %></div>
                            <div>Người gửi: <%= log.getSenderName() %></div>
                            <div>Gửi từ: <%= log.getFromLocation() %></div>
                            <div>Đến: <%= log.getToLocation() %></div>
                            <div>Ghi chú: <%= log.getRawDescription() %></div>

                            <% } else { %>
                            <div style="font-weight: 600; margin-bottom: 4px;"><%= log.getStatus() %></div>
                            <div>Danh mục: <%= log.getCategory() %></div>
                            <div>Số tiền: <%= log.getDescription() %></div>
                            <div>Mô tả: <%= log.getRawDescription() %></div>
                            <div>Chi nhánh: <%= log.getLocationName() %></div>
                            <div>Người ghi: <%= log.getSenderName() %></div>
                            <% } %>

                            <div style="margin-top: 6px; font-size: 12px; color: #888;">
                                <i class="fas fa-clock" style="margin-right: 4px;"></i>
                                <%= new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(log.getCreatedAt()) %>
                            </div>
                        </div>
                    </div>

                    <% 
                            } // end for
                        } else { 
                    %>
                    <div style="text-align: center; padding: 32px 0; color: #9e9e9e;">
                        <i class="fas fa-history" style="font-size: 32px; margin-bottom: 12px; opacity: 0.5;"></i>
                        <p style="margin: 0; font-size: 14px;">Chưa có hoạt động nào</p>
                    </div>
                    <% } %>
                </div>
                
            </aside>
        </main>

        <script>
            // Chart tabs functionality
            document.querySelectorAll('.chart-tab').forEach(tab => {
                tab.addEventListener('click', function () {
                    document.querySelectorAll('.chart-tab').forEach(t => t.classList.remove('active'));
                    this.classList.add('active');
                });
            });

            // Simulate real-time updates
            function updateStats() {
                // This would typically fetch data from your backend
                console.log('Updating dashboard stats...');
            }

            // Update stats every 30 seconds
            setInterval(updateStats, 30000);

            // Add click handlers for navigation
            document.querySelectorAll('.nav-item').forEach(item => {
                item.addEventListener('click', function (e) {
                    document.querySelectorAll('.nav-item').forEach(nav => nav.classList.remove('active'));
                    this.classList.add('active');
                });
            });


        </script>
    </body>
</html>
