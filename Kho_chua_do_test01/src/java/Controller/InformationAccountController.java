package Controller;

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
        // ⚙️ Giả sử user đã đăng nhập => lấy từ session
        HttpSession session = request.getSession();
        User loggedUser = (User) session.getAttribute("user");

        int userId;
        if (loggedUser != null) {
            userId = loggedUser.getUserId();
        } else {
            // Nếu chưa đăng nhập, tạm thời test cứng id = 1
            userId = 1;
        }

        UserDAO dao = new UserDAO();
        BranchDAO branchDAO = new BranchDAO();
        WarehouseDAO warehouseDAO = new WarehouseDAO();

        User user = dao.getUserById(userId);
        // ✅ Tự động đếm số chi nhánh & số kho
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
        User user = userDAO.getUserById(userId);

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

            boolean updated = userDAO.updateUser(user);
            request.setAttribute("msg", updated ? "✅ Cập nhật thông tin thành công!" : "❌ Cập nhật thất bại!");
        }

        // ✅ Cập nhật lại đếm số chi nhánh & kho để hiển thị đúng
        int branchCount = branchDAO.getAllBranches().size();
        int warehouseCount = warehouseDAO.getAllWarehouses().size();

        request.setAttribute("user", user);
        request.setAttribute("branchCount", branchCount);
        request.setAttribute("warehouseCount", warehouseCount);

        request.getRequestDispatcher("/WEB-INF/jsp/admin/information_account.jsp").forward(request, response);
    }

}
