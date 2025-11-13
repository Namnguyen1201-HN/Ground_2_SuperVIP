package Controller;

import DAL.BranchDAO;
import DAL.CustomerDAO;
import Model.Customer;
import Model.Branch;
import Model.User;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "SACustomerController", urlPatterns = {"/sa-customer"})
public class SACustomerController extends HttpServlet {

    private String safe(String v) {
        return (v != null && !v.trim().isEmpty()) ? v.trim() : null;
    }

    private Integer parseIntOrDefault(String s, Integer def) {
        if (s == null) return def;
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return def; }
    }

    private Double parseDoubleOrNull(String s) {
        if (s == null) return null;
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return null; }
    }

    /**
     * Thử lấy branchId từ session nếu cần (giữ cho tương thích)
     */
    private Integer getBranchIdFromSession(HttpSession session) {
        if (session == null) return null;
        Object o = session.getAttribute("branchId");
        if (o == null) o = session.getAttribute("BranchID");
        if (o == null) o = session.getAttribute("BranchId");
        if (o instanceof Integer) return (Integer) o;
        if (o instanceof String) {
            try { return Integer.parseInt(((String)o).trim()); } catch (Exception ex) { return null; }
        }
        return null;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        CustomerDAO customerDAO = new CustomerDAO();
        BranchDAO branchDAO = new BranchDAO();

        HttpSession session = request.getSession(false);
        User currentUser = null;
        if (session != null) {
            Object u = session.getAttribute("currentUser");
            if (u instanceof User) currentUser = (User) u;
        }

        // --- Lấy param từ request (hỗ trợ cả tên minSpent hoặc minGrandTotal)
        String keyword = safe(request.getParameter("keyword"));
        String gender = safe(request.getParameter("gender")); // all/male/female/null
        String branchParam = safe(request.getParameter("branchId"));

        String minSpentParam = safe(request.getParameter("minSpent"));
        String maxSpentParam = safe(request.getParameter("maxSpent"));
        // Hỗ trợ tên cũ minGrandTotal/maxGrandTotal nếu frontend dùng tên đó
        if (minSpentParam == null) minSpentParam = safe(request.getParameter("minGrandTotal"));
        if (maxSpentParam == null) maxSpentParam = safe(request.getParameter("maxGrandTotal"));

        String action = safe(request.getParameter("action"));

        Integer branchId = null;
        Double minSpent = null, maxSpent = null;

        // parse
        if (branchParam != null && !branchParam.isEmpty()) {
            try { branchId = Integer.parseInt(branchParam); } catch (Exception ex) { branchId = null; }
        }
        minSpent = parseDoubleOrNull(minSpentParam);
        maxSpent = parseDoubleOrNull(maxSpentParam);

        // Nếu action = clear: reset filters like Admin controller
        if ("clear".equalsIgnoreCase(action)) {
            keyword = null;
            gender = "all";
            branchId = 0;
            minSpent = null;
            maxSpent = null;
        }

        // --- paging ---
        int pageSize = 10;
        int page = 1;
        try {
            String p = safe(request.getParameter("page"));
            if (p != null) {
                page = Integer.parseInt(p);
                if (page < 1) page = 1;
            }
        } catch (Exception e) {
            page = 1;
        }

        // --- Fetch data via DAO ---
        List<Customer> customersAll;
        try {
            // DAO.searchAndFilterCustomers signature: (keyword, gender, branchId, minSpent, maxSpent)
            customersAll = customerDAO.searchAndFilterCustomers(keyword, gender, branchId, minSpent, maxSpent);
            if (customersAll == null) customersAll = java.util.Collections.emptyList();
        } catch (Exception ex) {
            // Log and forward error to JSP
            ex.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi truy xuất dữ liệu khách hàng. Vui lòng kiểm tra logs.");
            // Still attempt to provide branch list
            request.setAttribute("branches", branchDAO.getAllBranches());
            request.getRequestDispatcher("/WEB-INF/jsp/sale/SACustomer.jsp").forward(request, response);
            return;
        }

        // --- paging calculation (in-memory, as DAO returns grouped list) ---
        int totalCustomers = customersAll.size();
        int totalPages = (int) Math.ceil((double) totalCustomers / pageSize);
        if (totalPages == 0) totalPages = 1;
        if (page > totalPages) page = totalPages;

        int fromIndex = Math.min((page - 1) * pageSize, totalCustomers);
        int toIndex = Math.min(page * pageSize, totalCustomers);
        List<Customer> pagedCustomers = customersAll.subList(fromIndex, toIndex);

        int startIndex = totalCustomers == 0 ? 0 : fromIndex + 1;
        int endIndex = toIndex;

        // --- set attributes for JSP (matching admin controller naming) ---
        request.setAttribute("customers", pagedCustomers);
        request.setAttribute("branches", branchDAO.getAllBranches());
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalCustomers", totalCustomers);
        request.setAttribute("startIndex", startIndex);
        request.setAttribute("endIndex", endIndex);

        // giữ lại giá trị filter để hiển thị lại trên form
        request.setAttribute("keyword", keyword);
        request.setAttribute("gender", gender);
        request.setAttribute("branchId", branchId);
        request.setAttribute("minSpent", minSpent);
        request.setAttribute("maxSpent", maxSpent);

        // Forward tới JSP (đường dẫn bạn dùng trong project)
        request.getRequestDispatcher("/WEB-INF/jsp/sale/SACustomer.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // POST xử lý giống GET để submit form lọc
        doGet(req, resp);
    }
}
