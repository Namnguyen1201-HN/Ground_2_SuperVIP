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
                    <%
    Model.DashboardStatsDTO stats = (Model.DashboardStatsDTO) request.getAttribute("stats");
    if (stats == null) stats = new Model.DashboardStatsDTO();
                    %>

                    <div class="stats-grid">
                        <div class="stat-card">
                            <div class="stat-icon revenue">💰</div>
                            <div class="stat-content">
                                <h3><%= String.format("%,.0f", stats.getTodayRevenue()) %> ₫</h3>
                                <p>Doanh thu hôm nay</p>
                            </div>
                        </div>

                        <div class="stat-card">
                            <div class="stat-icon orders">📋</div>
                            <div class="stat-content">
                                <h3><%= stats.getReturnCount() %></h3>
                                <p>Phiếu trả hàng</p>
                            </div>
                        </div>

                        <%
    String colorClass1 = stats.getCompareYesterday() >= 0 ? "positive" : "negative";
    String colorClass2 = stats.getCompareLastMonth() >= 0 ? "positive" : "negative";
                        %>

                        <div class="stat-card">
                            <div class="stat-icon growth">📈</div>
                            <div class="stat-content">
                                <h3 class="<%= colorClass1 %>"><%= String.format("%.2f", stats.getCompareYesterday()) %>%</h3>
                                <p>So với hôm qua</p>
                            </div>
                        </div>

                        <div class="stat-card">
                            <div class="stat-icon comparison">📊</div>
                            <div class="stat-content">
                                <h3 class="<%= colorClass2 %>"><%= String.format("%.2f", stats.getCompareLastMonth()) %>%</h3>
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
                                <a href="TongQuan?viewType=day&period=<%= request.getAttribute("period") %>" 
                                   class="chart-tab <%= "day".equals(request.getAttribute("viewType")) ? "active" : "" %>">Theo ngày</a>
                                <a href="TongQuan?viewType=hour&period=<%= request.getAttribute("period") %>" 
                                   class="chart-tab <%= "hour".equals(request.getAttribute("viewType")) ? "active" : "" %>">Theo giờ</a>
                                <a href="TongQuan?viewType=weekday&period=<%= request.getAttribute("period") %>" 
                                   class="chart-tab <%= "weekday".equals(request.getAttribute("viewType")) ? "active" : "" %>">Theo thứ</a>
                            </div>

                            <select class="chart-dropdown" onchange="location.href = 'TongQuan?period=' + this.value + '&viewType=<%= request.getAttribute("viewType") %>'">
                                <option value="this_month" <%= "this_month".equals(request.getAttribute("period")) ? "selected" : "" %>>Tháng này</option>
                                <option value="last_month" <%= "last_month".equals(request.getAttribute("period")) ? "selected" : "" %>>Tháng trước</option>
                                <option value="3months" <%= "3months".equals(request.getAttribute("period")) ? "selected" : "" %>>3 tháng gần đây</option>
                            </select>

                        </div>
                    </div>
                    <div class="chart-container beautiful-table">
                        <%
                            List<Model.RevenueStatisticDTO> revenueStats = (List<Model.RevenueStatisticDTO>) request.getAttribute("revenueStats");
                            if (revenueStats != null && !revenueStats.isEmpty()) {
                                double total = 0;
                        %>
                        <table class="revenue-table">
                            <thead>
                                <tr>
                                    <th>⏱ Thời gian</th>
                                    <th class="text-right">💰 Doanh thu (VND)</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    for (Model.RevenueStatisticDTO r : revenueStats) {
                                        total += r.getTotalRevenue();
                                %>
                                <tr>
                                    <td><%= r.getLabel() %></td>
                                    <td class="text-right"><%= String.format("%,.0f", r.getTotalRevenue()) %></td>
                                </tr>
                                <% } %>
                            </tbody>
                            <tfoot>
                                <tr class="total-row">
                                    <td>Tổng cộng</td>
                                    <td class="text-right"><%= String.format("%,.0f", total) %></td>
                                </tr>
                            </tfoot>
                        </table>
                        <% } else { %>
                        <div class="no-data">
                            <i class="fas fa-chart-line"></i>
                            <p>Không có dữ liệu doanh thu</p>
                        </div>
                        <% } %>
                    </div>
                </section>

                <!-- Top Products Section -->

                <section class="products-section">
                    <div class="products-header">
                        <h2 class="products-title">
                            TOP 10 HÀNG HÓA BÁN CHẠY 
                            <%= "this_month".equals(request.getAttribute("period")) ? "THÁNG NÀY" : "THÁNG TRƯỚC" %>
                        </h2>

                        <form action="TongQuan" method="GET" style="display: flex; gap: 1rem;">
                            <select name="sortBy" onchange="this.form.submit()">
                                <option value="revenue" <%= "revenue".equals(request.getAttribute("sortBy")) ? "selected" : "" %>>THEO DOANH THU THUẦN</option>
                                <option value="quantity" <%= "quantity".equals(request.getAttribute("sortBy")) ? "selected" : "" %>>THEO SỐ LƯỢNG</option>
                            </select>

                            <select name="period" onchange="this.form.submit()">
                                <option value="this_month" <%= "this_month".equals(request.getAttribute("period")) ? "selected" : "" %>>Tháng này</option>
                                <option value="last_month" <%= "last_month".equals(request.getAttribute("period")) ? "selected" : "" %>>Tháng trước</option>
                            </select>
                        </form>
                    </div>

                    <div class="products-table" style="padding: 16px; background: #fff; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1);">
                        <%
                            List<Model.ProductStatisticDTO> topProducts = (List<Model.ProductStatisticDTO>) request.getAttribute("topProducts");
                            String sortBy = (String) request.getAttribute("sortBy");

                            if (topProducts != null && !topProducts.isEmpty()) {
                        %>
                        <table style="width: 100%; border-collapse: collapse;">
                            <thead style="background: #f0f0f0;">
                                <tr>
                                    <th style="padding: 8px; text-align: left;">#</th>
                                    <th style="padding: 8px; text-align: left;">Tên sản phẩm</th>
                                    <th style="padding: 8px; text-align: right;"><%= "revenue".equals(sortBy) ? "Doanh thu (VND)" : "Số lượng" %></th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    int i = 1;
                                    for (Model.ProductStatisticDTO p : topProducts) {
                                %>
                                <tr style="border-bottom: 1px solid #eee;">
                                    <td style="padding: 8px;"><%= i++ %></td>
                                    <td style="padding: 8px;"><%= p.getProductName() %></td>
                                    <td style="padding: 8px; text-align: right;">
                                        <%= "revenue".equals(sortBy) 
                                            ? String.format("%,.0f", p.getRevenue()) 
                                            : p.getTotalQuantity() %>
                                    </td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                        <%
                            } else {
                        %>
                        <div style="text-align: center; color: #9e9e9e; padding: 20px;">
                            <i class="fas fa-box" style="font-size: 32px; margin-bottom: 12px; opacity: 0.5;"></i>
                            <p>Chưa có dữ liệu sản phẩm</p>
                        </div>
                        <%
                            }
                        %>
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
