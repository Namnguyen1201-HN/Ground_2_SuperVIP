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

        UserDAO userDAO = new UserDAO();
        BranchDAO branchDAO = new BranchDAO();
        WarehouseDAO warehouseDAO = new WarehouseDAO();

        int userId = Integer.parseInt(request.getParameter("userId"));
        User user = userDAO.getUserFullById(userId);
        User original = userDAO.getUserFullById(userId);

        if (user != null) {
            user.setFullName(request.getParameter("fullName"));
            user.setEmail(request.getParameter("email"));
            user.setPhone(request.getParameter("phone"));
            user.setAddress(request.getParameter("address"));
            user.setIdentificationId(request.getParameter("identificationId"));
            user.setTaxNumber(request.getParameter("taxNumber"));
            user.setWebUrl(request.getParameter("webUrl"));
            user.setIsActive(Integer.parseInt(request.getParameter("isActive")));

            String gender = request.getParameter("gender");
            if (gender != null && !gender.isEmpty()) {
                user.setGender(gender.equals("true"));
            }

            String dobStr = request.getParameter("dob");
            if (dobStr != null && !dobStr.isEmpty()) {
                try {
                    Date dob = new SimpleDateFormat("yyyy-MM-dd").parse(dobStr);
                    user.setDob(dob);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            user.setRoleId(original.getRoleId());
            user.setBranchId(original.getBranchId());
            user.setWarehouseId(original.getWarehouseId());

            boolean updated = userDAO.updateUser(user);

            if (updated) {
                // ✅ Cập nhật lại session
                User refreshed = userDAO.getUserFullById(userId);
                request.getSession().setAttribute("currentUser", refreshed);
            }

            request.setAttribute("msg", updated ? "✅ Cập nhật thông tin thành công!" : "❌ Cập nhật thất bại!");
        }

        int branchCount = branchDAO.getAllBranches().size();
        int warehouseCount = warehouseDAO.getAllWarehouses().size();

        request.setAttribute("user", user);
        request.setAttribute("branchCount", branchCount);
        request.setAttribute("warehouseCount", warehouseCount);
        request.getRequestDispatcher("/WEB-INF/jsp/admin/information_account.jsp").forward(request, response);
    }
}
