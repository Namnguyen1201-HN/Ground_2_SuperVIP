package Controller.Admin;

import DAL.CustomerDAO;
import DAL.BranchDAO;
import Model.Customer;
import Model.Branch;
import Model.User;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "CustomerController", urlPatterns = {"/Customer"})
public class CustomerController extends HttpServlet {
    private CustomerDAO customerDAO;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        customerDAO = new CustomerDAO();
        BranchDAO branchDAO = new BranchDAO();

        // --- Lấy user đăng nhập ---
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("currentUser");

        // --- Lấy các param lọc/tìm kiếm ---
        String keyword = request.getParameter("keyword");
        String gender = request.getParameter("gender");
        String branchParam = request.getParameter("branchId");
        String minSpentParam = request.getParameter("minSpent");
        String maxSpentParam = request.getParameter("maxSpent");
        String action = request.getParameter("action");

        Integer branchId = null;
        Double minSpent = null, maxSpent = null;

        try {
            if (branchParam != null && !branchParam.isEmpty()) {
                branchId = Integer.parseInt(branchParam);
            }
            if (minSpentParam != null && !minSpentParam.isEmpty()) {
                minSpent = Double.parseDouble(minSpentParam);
            }
            if (maxSpentParam != null && !maxSpentParam.isEmpty()) {
                maxSpent = Double.parseDouble(maxSpentParam);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Nếu người dùng bấm “Xóa bộ lọc” => reset về null
        if ("clear".equalsIgnoreCase(action)) {
            keyword = null;
            gender = "all";
            branchId = 0;
            minSpent = null;
            maxSpent = null;
        }

        // --- Phân trang ---
        int pageSize = 10;
        int page;
        try {
            page = Integer.parseInt(request.getParameter("page"));
            if (page < 1) page = 1;
        } catch (Exception e) {
            page = 1;
        }

        // --- Lấy dữ liệu filter ---
        List<Customer> customers = customerDAO.searchAndFilterCustomers(keyword, gender, branchId, minSpent, maxSpent);

        // --- Tính phân trang ---
        int totalCustomers = customers.size();
        int totalPages = (int) Math.ceil((double) totalCustomers / pageSize);
        if (totalPages == 0) totalPages = 1;
        if (page > totalPages) page = totalPages;

        int fromIndex = Math.min((page - 1) * pageSize, totalCustomers);
        int toIndex = Math.min(page * pageSize, totalCustomers);
        List<Customer> pagedCustomers = customers.subList(fromIndex, toIndex);

        int startIndex = totalCustomers == 0 ? 0 : fromIndex + 1;
        int endIndex = toIndex;

        // --- Gửi dữ liệu sang JSP ---
        request.setAttribute("customers", pagedCustomers);
        request.setAttribute("branches", branchDAO.getAllBranches());
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalCustomers", totalCustomers);
        request.setAttribute("startIndex", startIndex);
        request.setAttribute("endIndex", endIndex);

        // Giữ lại giá trị lọc để hiển thị lại
        request.setAttribute("keyword", keyword);
        request.setAttribute("gender", gender);
        request.setAttribute("branchId", branchId);
        request.setAttribute("minSpent", minSpent);
        request.setAttribute("maxSpent", maxSpent);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/admin/customer.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
