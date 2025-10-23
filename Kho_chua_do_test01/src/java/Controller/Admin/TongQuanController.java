package Controller.Admin;

import DAL.AnnouncementDAO;
import DAL.OrderDAO;
import DAL.ProductDAO;
import Model.Announcement;
import Model.AnnouncementDTO;
import Model.DashboardStatsDTO;
import Model.ProductStatisticDTO;
import Model.RevenueStatisticDTO;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *
 * @author TieuPham
 */
@WebServlet(name = "TongQuanController", urlPatterns = {"/TongQuan"})
public class TongQuanController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy thông tin user từ session
        Model.User currentUser = (Model.User) request.getSession().getAttribute("currentUser");
        Integer branchId = null;
        
        System.out.println("=== TongQuanController.doGet ===");
        if (currentUser != null) {
            System.out.println("User: " + currentUser.getFullName());
            System.out.println("RoleId: " + currentUser.getRoleId());
            System.out.println("BranchId: " + currentUser.getBranchId());
        } else {
            System.out.println("currentUser is NULL!");
        }
        
        // Nếu role = 1 (Branch Manager), chỉ lấy dữ liệu của chi nhánh đó
        if (currentUser != null && currentUser.getRoleId() == 1) {
            branchId = currentUser.getBranchId();
            System.out.println("Branch Manager detected - filtering by branchId: " + branchId);
        } else {
            System.out.println("Admin detected - showing all branches");
        }
        // Nếu role = 0 (Admin), branchId = null => lấy toàn bộ

        AnnouncementDAO dao = new AnnouncementDAO();
        ProductDAO productDAO = new ProductDAO();
        OrderDAO orderDAO = new OrderDAO();
        
        // lấy danh sách thông báo mới nhất
        List<Announcement> announcements = dao.getLatestAnnouncements(4);

        // Lấy danh sách hoạt động gần đây (có filter chi nhánh)
        List<AnnouncementDTO> activityLogs = dao.getRecentActivities(4, branchId);

        // --- Thêm phần top 10 sản phẩm ---
        String sortBy = request.getParameter("sortBy") == null ? "revenue" : request.getParameter("sortBy");
        String period = request.getParameter("period") == null ? "this_month" : request.getParameter("period");

        List<ProductStatisticDTO> topProducts = productDAO.getTopProducts(sortBy, period, 10, branchId);
       
        String viewType = request.getParameter("viewType") == null ? "day" : request.getParameter("viewType");

        List<RevenueStatisticDTO> revenueStats;

        if ("3months".equals(period)) {
            revenueStats = orderDAO.getRevenueLast3Months(branchId);
        } else {
            switch (viewType) {
                case "hour":
                    revenueStats = orderDAO.getRevenueByHour(period, branchId);
                    break;
                case "weekday":
                    revenueStats = orderDAO.getRevenueByWeekday(period, branchId);
                    break;
                default:
                    revenueStats = orderDAO.getRevenueByDay(period, branchId);
            }
        }
        
        DashboardStatsDTO stats = orderDAO.getDashboardStats(branchId);
        
        System.out.println("=== Stats retrieved in Controller ===");
        System.out.println("Today Orders: " + stats.getTodayOrders());
        System.out.println("Week Orders: " + stats.getWeekOrders());
        System.out.println("Month Orders: " + stats.getMonthOrders());
        System.out.println("Week Revenue: " + stats.getWeekRevenue());
        System.out.println("Month Revenue: " + stats.getMonthRevenue());
        System.out.println("====================================");

        // Gán vào request      
        request.setAttribute("announcements", announcements);
        request.setAttribute("activityLogs", activityLogs);

        request.setAttribute("topProducts", topProducts);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("period", period);

        request.setAttribute("viewType", viewType);
        request.setAttribute("revenueStats", revenueStats);
        
        request.setAttribute("stats", stats);

        request.getRequestDispatcher("/WEB-INF/jsp/admin/TongQuan.jsp")
                .forward(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
