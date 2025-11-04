package Controller.Admin;

import DAL.AnnouncementDAO;
import DAL.OrderDAO;
import DAL.ProductDAO;
import Model.Announcement;
import Model.AnnouncementDTO;
import Model.DashboardStatsDTO;
import Model.ProductStatisticDTO;
import Model.RevenueStatisticDTO;
import Model.User;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@WebServlet(name = "TongQuanController", urlPatterns = {"/TongQuan"})
public class TongQuanController extends HttpServlet {

    // --- Constants for better readability and maintenance ---
    private static final int ROLE_BRANCH_MANAGER = 1;
    private static final int DEFAULT_ITEM_COUNT = 4;
    private static final int TOP_PRODUCT_COUNT = 10;

    // --- DAO instances ---
    private final AnnouncementDAO announcementDAO = new AnnouncementDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Get user info and determine the branch filter
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        Integer branchId = getBranchIdForUser(currentUser);

        // 2. Get parameters from request with default values
        String sortBy = getParameterOrDefault(request, "sortBy", "revenue");
        String period = getParameterOrDefault(request, "period", "this_month");
        String viewType = getParameterOrDefault(request, "viewType", "day");

        // 3. Fetch data from DAOs
        List<Announcement> announcements = announcementDAO.getLatestAnnouncements(DEFAULT_ITEM_COUNT);
        List<AnnouncementDTO> activityLogs = announcementDAO.getRecentActivities(DEFAULT_ITEM_COUNT, branchId);
        List<ProductStatisticDTO> topProducts = productDAO.getTopProducts(sortBy, period, TOP_PRODUCT_COUNT, branchId);
        DashboardStatsDTO stats = orderDAO.getDashboardStats(branchId);
        List<RevenueStatisticDTO> revenueStats = getRevenueStatistics(orderDAO, period, viewType, branchId);

        // 4. Set attributes for the JSP page
        request.setAttribute("announcements", announcements);
        request.setAttribute("activityLogs", activityLogs);
        request.setAttribute("topProducts", topProducts);
        request.setAttribute("stats", stats);
        request.setAttribute("revenueStats", revenueStats);

        // Pass parameters back to JSP to maintain state (e.g., selected filters)
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("period", period);
        request.setAttribute("viewType", viewType);

        // 5. Forward to the view
        request.getRequestDispatcher("/WEB-INF/jsp/admin/TongQuan.jsp").forward(request, response);
    }

    /**
     * Handles POST requests by delegating to the GET method.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * Extracts the branch ID based on the user's role. Returns null if the user
     * is an admin or not logged in.
     */
    private Integer getBranchIdForUser(User user) {
        if (user != null && user.getRoleId() == ROLE_BRANCH_MANAGER) {
            return user.getBranchId();
        }
        return null;
    }

    /**
     * Retrieves revenue statistics based on the selected period and view type.
     * This method encapsulates the complex conditional logic.
     */
    private List<RevenueStatisticDTO> getRevenueStatistics(OrderDAO dao, String period, String viewType, Integer branchId) {
        if ("3months".equals(period)) {
            return dao.getRevenueLast3Months(branchId);
        }

        switch (viewType) {
            case "hour":
                return dao.getRevenueByHour(period, branchId);
            case "weekday":
                return dao.getRevenueByWeekday(period, branchId);
            default: // "day" or any other value
                return dao.getRevenueByDay(period, branchId);
        }
    }

    /**
     * Gets a parameter from the request, returning a default value if it's null
     * or empty.
     */
    private String getParameterOrDefault(HttpServletRequest request, String paramName, String defaultValue) {
        String value = request.getParameter(paramName);
        return (value == null || value.trim().isEmpty()) ? defaultValue : value;
    }

    @Override
    public String getServletInfo() {
        return "Controller for the main admin dashboard.";
    }
}
