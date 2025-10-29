package Controller.Admin;

import DAL.BranchDAO;
import DAL.UserDAO;
import DAL.WarehouseDAO;
import Model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet(name = "InformationAccountController", urlPatterns = {"/InformationAccount"})
public class InformationAccountController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User loggedUser = (User) session.getAttribute("currentUser");

        if (loggedUser != null) {
            // 🔄 Cập nhật lại user đầy đủ từ DB
            loggedUser = new UserDAO().getUserFullById(loggedUser.getUserId());
            session.setAttribute("currentUser", loggedUser);
        }

        int userId = (loggedUser != null) ? loggedUser.getUserId() : 1;

        UserDAO dao = new UserDAO();
        BranchDAO branchDAO = new BranchDAO();
        WarehouseDAO warehouseDAO = new WarehouseDAO();

        // ✅ Lấy user đầy đủ thông tin
        User user = dao.getUserFullById(userId);

        int branchCount = branchDAO.getAllBranches().size();
        int warehouseCount = warehouseDAO.getAllWarehouses().size();

        request.setAttribute("user", user);
        request.setAttribute("branchCount", branchCount);
        request.setAttribute("warehouseCount", warehouseCount);
        request.getRequestDispatcher("/WEB-INF/jsp/admin/information_account.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        UserDAO userDAO = new UserDAO();
        BranchDAO branchDAO = new BranchDAO();
        WarehouseDAO warehouseDAO = new WarehouseDAO();

        int userId = Integer.parseInt(request.getParameter("userId"));
        User user = userDAO.getUserFullById(userId);
        if (user == null) {
            request.setAttribute("error", "Người dùng không tồn tại!");
            doGet(request, response);
            return;
        }

        // --- Lấy dữ liệu form ---
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String identificationId = request.getParameter("identificationId");
        String taxNumber = request.getParameter("taxNumber");
        String webUrl = request.getParameter("webUrl");
        String dobStr = request.getParameter("dob");
        String gender = request.getParameter("gender");

        // --- Validate cơ bản ---
        if (fullName == null || fullName.trim().isEmpty()) {
            request.setAttribute("error", "Tên không được để trống!");
            request.setAttribute("user", user);
            doGet(request, response);
            return;
        }
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Email không được để trống!");
            doGet(request, response);
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            request.setAttribute("error", "Định dạng email không hợp lệ!");
            doGet(request, response);
            return;
        }
        if (phone == null || !phone.matches("^\\d{9,11}$")) {
            request.setAttribute("error", "Số điện thoại phải gồm 9–11 chữ số!");
            doGet(request, response);
            return;
        }

        // --- Kiểm tra trùng lặp ---
        if (userDAO.isEmailExists(email) && !email.equalsIgnoreCase(user.getEmail())) {
            request.setAttribute("error", "Email này đã được sử dụng bởi tài khoản khác!");
            doGet(request, response);
            return;
        }
        if (userDAO.isPhoneExists(phone) && !phone.equals(user.getPhone())) {
            request.setAttribute("error", "Số điện thoại này đã được sử dụng bởi tài khoản khác!");
            doGet(request, response);
            return;
        }
        if (identificationId != null && !identificationId.isEmpty()
                && userDAO.isIdentificationIdExists(identificationId)
                && !identificationId.equals(user.getIdentificationId())) {
            request.setAttribute("error", "CMND/CCCD đã tồn tại trong hệ thống!");
            doGet(request, response);
            return;
        }

        // --- Gán lại dữ liệu cho user ---
        user.setFullName(fullName.trim());
        user.setEmail(email.trim());
        user.setPhone(phone.trim());
        user.setAddress(address);
        user.setIdentificationId(identificationId);
        user.setTaxNumber(taxNumber);
        user.setWebUrl(webUrl);
        user.setIsActive(Integer.parseInt(request.getParameter("isActive")));

        if (gender != null && !gender.isEmpty()) {
            user.setGender(gender.equals("true"));
        }

        if (dobStr != null && !dobStr.isEmpty()) {
            try {
                Date dob = new SimpleDateFormat("yyyy-MM-dd").parse(dobStr);
                user.setDob(dob);
            } catch (Exception e) {
                request.setAttribute("error", "Định dạng ngày sinh không hợp lệ!");
                doGet(request, response);
                return;
            }
        }

        // --- Cập nhật ---
        boolean updated = userDAO.updateUser(user);
        if (updated) {
            User refreshed = userDAO.getUserFullById(userId);
            request.getSession().setAttribute("currentUser", refreshed);
            request.setAttribute("success", "✅ Cập nhật thông tin thành công!");
        } else {
            request.setAttribute("error", "❌ Cập nhật thất bại. Vui lòng thử lại sau!");
        }

        // --- Trả lại dữ liệu ---
        int branchCount = branchDAO.getAllBranches().size();
        int warehouseCount = warehouseDAO.getAllWarehouses().size();
        request.setAttribute("user", user);
        request.setAttribute("branchCount", branchCount);
        request.setAttribute("warehouseCount", warehouseCount);
        request.getRequestDispatcher("/WEB-INF/jsp/admin/information_account.jsp").forward(request, response);
    }

}
