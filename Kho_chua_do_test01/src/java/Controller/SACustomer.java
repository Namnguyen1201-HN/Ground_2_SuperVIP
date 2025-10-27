package Controller;

import DAL.CustomerDAO;
import Model.Customer;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "SACustomer", urlPatterns = {"/SACustomer"})
public class SACustomer extends HttpServlet {

    private CustomerDAO dao;

    @Override
    public void init() throws ServletException {
        dao = new CustomerDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String action = optional(req.getParameter("action"), "list");

        switch (action) {
            case "list":
                handleList(req, resp);
                break;
            case "detail":
                handleDetail(req, resp);
                break;
            case "create":
                // hiển thị form tạo
                req.getRequestDispatcher("/WEB-INF/jsp/sale/SACustomer.jsp").forward(req, resp);
                break;
            case "edit":
                handleEditForm(req, resp);
                break;
            case "delete":
                handleDelete(req, resp);
                break;
            default:
                handleList(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String action = optional(req.getParameter("action"), "");

        switch (action) {
            case "create":
                handleCreate(req, resp);
                break;
            case "update":
                handleUpdate(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/customers");
        }
    }

    /* ===================== HANDLERS ===================== */

    private void handleList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Lấy các tham số lọc & phân trang
        String keyword = trimToNull(req.getParameter("keyword"));
        String gender = optional(req.getParameter("gender"), "all"); // all | male | female
        Integer branchId = parseInteger(req.getParameter("branchId")); // null => all
        Double minSpent = parseDouble(req.getParameter("minSpent"));
        Double maxSpent = parseDouble(req.getParameter("maxSpent"));

        int page = Math.max(parseInt(req.getParameter("page"), 1), 1);
        int pageSize = Math.max(parseInt(req.getParameter("pageSize"), 10), 1);

        // Nếu bạn có lưu branch của user trong session (VD: khi đăng nhập):
        // Integer userBranchId = (Integer) req.getSession().getAttribute("branchId");
        // Nếu muốn bắt buộc theo branch của user, gán branchId = userBranchId khi null.

        boolean hasFilter =
                (keyword != null) || (branchId != null && branchId != 0)
                        || (minSpent != null) || (maxSpent != null)
                        || (gender != null && !"all".equalsIgnoreCase(gender));

        List<Customer> data;
        int totalItems;
        if (hasFilter) {
            // Tìm kiếm + lọc (DAO đã gom tổng chi tiêu)
            data = dao.searchAndFilterCustomers(keyword, gender, branchId, minSpent, maxSpent);
            // Phân trang trên memory cho nhanh gọn
            totalItems = data.size();
            int from = Math.min((page - 1) * pageSize, totalItems);
            int to = Math.min(from + pageSize, totalItems);
            data = data.subList(from, to);
        } else {
            // Danh sách tổng có phân trang DB-side
            data = dao.getAllCustomersPaged(page, pageSize);
            totalItems = dao.getTotalCustomers();
        }

        int totalPages = (int) Math.ceil(totalItems / (double) pageSize);

        req.setAttribute("customers", data);
        req.setAttribute("page", page);
        req.setAttribute("pageSize", pageSize);
        req.setAttribute("totalItems", totalItems);
        req.setAttribute("totalPages", totalPages);

        // giữ lại filter trên form
        req.setAttribute("keyword", keyword == null ? "" : keyword);
        req.setAttribute("gender", gender);
        req.setAttribute("branchId", branchId);
        req.setAttribute("minSpent", minSpent);
        req.setAttribute("maxSpent", maxSpent);

        req.getRequestDispatcher("/WEB-INF/jsp/sale/SACustomer.jsp").forward(req, resp);
    }

    private void handleDetail(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer id = parseInteger(req.getParameter("id"));
        if (id == null) {
            resp.sendRedirect(req.getContextPath() + "/customers");
            return;
        }
        Customer c = dao.getCustomerById(id);
        if (c == null) {
            resp.sendRedirect(req.getContextPath() + "/customers");
            return;
        }
        req.setAttribute("c", c);
        req.getRequestDispatcher("/WEB-INF/views/customer-detail.jsp").forward(req, resp);
    }

    private void handleEditForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer id = parseInteger(req.getParameter("id"));
        if (id == null) {
            resp.sendRedirect(req.getContextPath() + "/customers");
            return;
        }
        Customer c = dao.getCustomerById(id);
        if (c == null) {
            resp.sendRedirect(req.getContextPath() + "/customers");
            return;
        }
        req.setAttribute("c", c);
        req.getRequestDispatcher("/WEB-INF/views/customer-form.jsp").forward(req, resp);
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Integer id = parseInteger(req.getParameter("id"));
        if (id != null) {
            dao.deleteCustomer(id);
        }
        resp.sendRedirect(req.getContextPath() + "/customers");
    }

    private void handleCreate(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        Customer c = buildCustomerFromRequest(req, false);

        // Lấy branchId cho insertCustomer: ưu tiên param, fallback session
        Integer branchId = parseInteger(req.getParameter("branchId"));
        if (branchId == null) {
            Object s = req.getSession().getAttribute("branchId");
            if (s instanceof Integer) branchId = (Integer) s;
        }
        if (branchId == null) branchId = 0; // nếu để 0 thì rơi vào chi nhánh mặc định của DB

        boolean ok = dao.insertCustomer(c, branchId);
        if (ok) {
            resp.sendRedirect(req.getContextPath() + "/customers");
        } else {
            req.setAttribute("error", "Không thể tạo khách hàng. Vui lòng thử lại!");
            req.setAttribute("c", c);
            req.getRequestDispatcher("/WEB-INF/views/customer-form.jsp").forward(req, resp);
        }
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        Customer c = buildCustomerFromRequest(req, true);
        boolean ok = dao.updateCustomer(c);
        if (ok) {
            resp.sendRedirect(req.getContextPath() + "/customers");
        } else {
            req.setAttribute("error", "Không thể cập nhật khách hàng. Vui lòng thử lại!");
            req.setAttribute("c", c);
            req.getRequestDispatcher("/WEB-INF/views/customer-form.jsp").forward(req, resp);
        }
    }

    /* ===================== HELPERS ===================== */

    private Customer buildCustomerFromRequest(HttpServletRequest req, boolean includeId) {
        Customer c = new Customer();
        if (includeId) {
            c.setCustomerID(parseInt(req.getParameter("customerID"), 0));
        }
        c.setFullname(optional(req.getParameter("fullName"), ""));
        c.setPhoneNumber(optional(req.getParameter("phoneNumber"), ""));
        c.setEmail(optional(req.getParameter("email"), ""));
        c.setAddress(optional(req.getParameter("address"), ""));

        String g = req.getParameter("gender");
        if (g != null) {
            // "male" => true, "female" => false
            boolean gb = "male".equalsIgnoreCase(g) || "m".equalsIgnoreCase(g) || "nam".equalsIgnoreCase(g);
            c.setGender(gb);
        } else {
            c.setGender(null);
        }

        String dob = req.getParameter("dateOfBirth");
        if (dob != null && !dob.isBlank()) {
            try {
                Date d = new SimpleDateFormat("yyyy-MM-dd").parse(dob);
                c.setDateOfBirth(d);
            } catch (ParseException ignored) { }
        }

        // các trường audit nếu model có (không bắt buộc)
        c.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return c;
    }

    private static String optional(String s, String def) {
        return (s == null || s.isBlank()) ? def : s;
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static Integer parseInteger(String s) {
        try {
            if (s == null || s.isBlank()) return null;
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private static int parseInt(String s, int def) {
        try {
            return (s == null || s.isBlank()) ? def : Integer.parseInt(s.trim());
        } catch (Exception e) {
            return def;
        }
    }

    private static Double parseDouble(String s) {
        try {
            if (s == null || s.isBlank()) return null;
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return null;
        }
    }
}
