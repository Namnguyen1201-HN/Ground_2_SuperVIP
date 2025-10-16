package Controller;

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

        AnnouncementDAO dao = new AnnouncementDAO();
        ProductDAO productDAO = new ProductDAO();
        OrderDAO orderDAO = new OrderDAO();
        // lấy danh sách thông báo mới nhất
        List<Announcement> announcements = dao.getLatestAnnouncements(4);

        // Lấy danh sách hoạt động gần đây
        List<AnnouncementDTO> activityLogs = dao.getRecentActivities(4);

        // --- Thêm phần top 10 sản phẩm ---
        String sortBy = request.getParameter("sortBy") == null ? "revenue" : request.getParameter("sortBy");
        String period = request.getParameter("period") == null ? "this_month" : request.getParameter("period");

        List<ProductStatisticDTO> topProducts = productDAO.getTopProducts(sortBy, period, 10);
       
        String viewType = request.getParameter("viewType") == null ? "day" : request.getParameter("viewType");

        List<RevenueStatisticDTO> revenueStats;

        if ("3months".equals(period)) {
            revenueStats = orderDAO.getRevenueLast3Months();
        } else {
            switch (viewType) {
                case "hour":
                    revenueStats = orderDAO.getRevenueByHour(period);
                    break;
                case "weekday":
                    revenueStats = orderDAO.getRevenueByWeekday(period);
                    break;
                default:
                    revenueStats = orderDAO.getRevenueByDay(period);
            }
        }
        
        DashboardStatsDTO stats = orderDAO.getDashboardStats();

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
